package com.heima.schedule.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.common.constants.ScheduleConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.schedule.dtos.Task;
import com.heima.model.schedule.pojos.Taskinfo;
import com.heima.model.schedule.pojos.TaskinfoLogs;
import com.heima.schedule.mapper.TaskinfoLogsMapper;
import com.heima.schedule.mapper.TaskinfoMapper;
import com.heima.schedule.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@Transactional
public class TaskServiceImpl implements TaskService {


    /**
     * 添加延迟任务
     * @param task 任务对象
     * @return 任务id
     */
    @Override
    public long addTask(Task task) {
        //1.添加任务到数据库中
        boolean success = addTaskToDb(task);

        if (success){
            //2.添加任务到redis中
            addTaskToCache(task);
        }

        return task.getTaskId();
    }

    @Autowired
    private CacheService cacheService;

    /**
     * 把任务添加到redis中
     * @param task
     * */
    private void addTaskToCache(Task task){
        String key = task.getTaskType()+"_"+task.getPriority();

        //获取5分钟之后的时间 毫秒值 --- 预设时间
        Calendar calendar = Calendar.getInstance(); //初始化为当前的日期和时间
        calendar.add(Calendar.MINUTE,5);//将Calendar对象的时间增加了5分钟
        long nextScheduleTime = calendar.getTimeInMillis();//获取了修改后的时间（即当前时间加5分钟）的毫秒值

        //2.1 如果任务的执行时间小于等于当前时间，存入list
        if (task.getExecuteTime() <= System.currentTimeMillis()){
            //ScheduleConstants.TOPIC -- 当前数据key前缀
            cacheService.lLeftPush(ScheduleConstants.TOPIC+key, JSON.toJSONString(task));
        } else if (task.getExecuteTime() <= nextScheduleTime) {
            //2.2 如果任务的执行时间大于当前时间 && 小于等于预设时间（未来5分钟） 存入zset中
            //ScheduleConstants.FUTURE -- 未来数据key前缀
            cacheService.zAdd(ScheduleConstants.FUTURE+key, JSON.toJSONString(task), task.getExecuteTime());
        }

    }


    @Autowired
    private TaskinfoMapper taskinfoMapper;
    @Autowired
    private TaskinfoLogsMapper taskinfoLogsMapper;

    /**
     * 添加任务到数据库中
     * @param task
     * @return
     * */
    private boolean addTaskToDb(Task task) {
        boolean flag = false;
        try {
            //保存任务表
            Taskinfo taskinfo = new Taskinfo();
            BeanUtils.copyProperties(task,taskinfo);
            taskinfo.setExecuteTime(new Date(task.getExecuteTime()));
            taskinfoMapper.insert(taskinfo);

            //设置taskId
            task.setTaskId(taskinfo.getTaskId());

            //保存任务日志数据
            TaskinfoLogs taskinfoLogs = new TaskinfoLogs();
            BeanUtils.copyProperties(taskinfo,taskinfoLogs);
            taskinfoLogs.setVersion(1);
            taskinfoLogs.setStatus(ScheduleConstants.SCHEDULED);
            taskinfoLogsMapper.insert(taskinfoLogs);

            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }


    /**
     * 取消任务
     * @param taskId 任务id
     * @return 取消结果
     */
    @Override
    public boolean cancelTask(long taskId) {
        boolean flag = false;
        //删除任务，更新日志
        Task task = updateDb(taskId, ScheduleConstants.CANCELLED);

        //删除redis的数据
        if (task != null){
            removeTaskFromCache(task);
            flag = true;
        }
        return flag;
    }

    /**
     * 删除redis中的任务数据
     * @param task
     */
    private void removeTaskFromCache(Task task) {
        String key = task.getTaskType()+"_"+task.getPriority();

        if (task.getExecuteTime() <= System.currentTimeMillis()){
            //任务的执行时间 小于等于 当前系统时间
            //说明该任务 已经执行完了，是存放在list中的
            cacheService.lRemove(ScheduleConstants.TOPIC+key, 0, JSON.toJSONString(task));
        }else {
            //任务的执行时间 大于 当前系统时间
            //说明该任务 还未执行，是存放在zset中的
            cacheService.zRemove(ScheduleConstants.FUTURE+key, JSON.toJSONString(task));
        }
    }

    /**
     * 删除任务，更新任务日志状态
     * @param taskId
     * @param status
     * @return
     */
    private Task updateDb(long taskId, int status) {
        Task task = null;
        try {
            //删除任务 -- taskinfo表
            taskinfoMapper.deleteById(taskId);

            TaskinfoLogs taskinfoLogs = taskinfoLogsMapper.selectById(taskId);
            taskinfoLogs.setStatus(status);
            taskinfoLogsMapper.updateById(taskinfoLogs);

            task = new Task();
            BeanUtils.copyProperties(taskinfoLogs,task);
            task.setExecuteTime(taskinfoLogs.getExecuteTime().getTime());
        }catch (Exception e){
            log.error("task cancel exception taskid={}",taskId);
        }
        return task;
    }


    /**
     * 按照类型和优先级来拉取任务
     * @param type
     * @param priority
     * @return
     */
    @Override
    public Task poll(int type, int priority) {
        Task task = null;
        try {
            String key = type + "_" + priority;
            //从redis中拉取数据 pop
            //在redis中到了任务执行时间，消费任务--即弹出并返回数据
            String task_json = cacheService.lRightPop(ScheduleConstants.TOPIC + key);
            if (StringUtils.isNotBlank(task_json)){
                task = JSON.parseObject(task_json, Task.class);
                //更新数据库信息
                updateDb(task.getTaskId(),ScheduleConstants.EXECUTED);
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("poll task exception");
        }
        return task;
    }

    /**
     * 未来数据定时刷新
     */
    @Scheduled(cron = "0 */1 * * * ?") //cron = "0 */1 * * * ?" 表示每分钟执行一次
    public void refresh(){
        //FUTURE_TASK_SYNC为锁的名字， 1000 * 30 为30秒后过期
        String token = cacheService.tryLock("FUTURE_TASK_SYNC", 1000 * 30);

        if (StringUtils.isNotBlank(token)){
            log.info("未来数据定时刷新---定时任务");

            //获取所有未来数据的集合key
            Set<String> futureKeys = cacheService.scan(ScheduleConstants.FUTURE + "*");// future_*
            for (String futureKey : futureKeys) { //futureKey的值为 future_100_50 模式
                //获取当前数据的key topic
                //futureKey.split(ScheduleConstants.FUTURE)根据 "future_" 分隔成两个元素存进数组，[0]="future_"，[1]="100_50"
                String topicKey = ScheduleConstants.TOPIC + futureKey.split(ScheduleConstants.FUTURE)[1];

                //按照key和分值查询符合条件的数据
                Set<String> tasks = cacheService.zRangeByScore(futureKey, 0, System.currentTimeMillis());

                //同步数据
                if (!tasks.isEmpty()){
                    cacheService.refreshWithPipeline(futureKey, topicKey, tasks);
                    log.info("成功的将 "+futureKey+" 刷新到了 "+topicKey);
                }
            }
        }

    }

    /**
     * 数据库任务定时同步到redis中
     */
    @PostConstruct //在依赖注入完成后执行被注解的方法，通常用于进行初始化操作 -- 在启动服务后将同步一次任务
    @Scheduled(cron = "0 */5 * * * ?") //每5分钟执行一次
    public void reloadData(){
        //清理缓存中的数据 List  zset
        clearCache();

        //查询符合条件的任务 小于未来5分钟的数据
        //获取5分钟之后的时间 毫秒值 --- 预设时间
        Calendar calendar = Calendar.getInstance(); //初始化为当前的日期和时间
        calendar.add(Calendar.MINUTE,5);//将Calendar对象的时间增加了5分钟
        List<Taskinfo> taskinfoList = taskinfoMapper.selectList(Wrappers.<Taskinfo>lambdaQuery().lt(Taskinfo::getExecuteTime, calendar.getTime()));

        //把任务添加到redis中
        if (taskinfoList != null && taskinfoList.size() > 0){
            for (Taskinfo taskinfo : taskinfoList) {
                Task task = new Task();
                BeanUtils.copyProperties(taskinfo,task);
                task.setExecuteTime(taskinfo.getExecuteTime().getTime());
                addTaskToCache(task);
            }
        }
        log.info("数据库的任务同步到了redis中");

    }

    /**
     * 清理缓存中的数据
     */
    public void clearCache(){
        Set<String> topicKeys = cacheService.scan(ScheduleConstants.TOPIC + "*"); //查询所有topic开头的key
        Set<String> futureKeys = cacheService.scan(ScheduleConstants.FUTURE + "*"); //查询所有future开头的key
        cacheService.delete(topicKeys); //删除所有topic开头的key
        cacheService.delete(futureKeys); //删除所有future开头的key
    }


}
