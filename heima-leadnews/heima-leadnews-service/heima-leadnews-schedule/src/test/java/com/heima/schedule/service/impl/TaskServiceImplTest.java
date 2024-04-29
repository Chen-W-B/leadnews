package com.heima.schedule.service.impl;

import com.heima.common.redis.CacheService;
import com.heima.model.schedule.dtos.Task;
import com.heima.schedule.ScheduleApplication;
import com.heima.schedule.service.TaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.Set;

import static org.junit.Assert.*;

@SpringBootTest(classes = ScheduleApplication.class)
@RunWith(SpringRunner.class)
public class TaskServiceImplTest {
    @Autowired
    private TaskService taskService;

    @Test //测试添加任务
    public void addTask() {
        //Task task = new Task();
        //task.setTaskType(100);
        //task.setPriority(50);
        //task.setParameters("task test".getBytes());
        //
        //task.setExecuteTime(new Date().getTime());
        ////task.setExecuteTime(new Date().getTime()+500000);
        //
        //long taskId = taskService.addTask(task);
        //System.out.println(taskId);

        for (int i = 0; i < 5; i++) {
            Task task = new Task();
            task.setTaskType(100+i);
            task.setPriority(50);
            task.setParameters("task test".getBytes());
            task.setExecuteTime(new Date().getTime()+500*i);

            long taskId = taskService.addTask(task);
        }

    }

    @Test //测试取消任务
    public void cancelTask(){
        //在taskId后加上 L 转为long类型
        taskService.cancelTask(1777138248585039873L);
    }

    @Test //测试在redis中拉取数据
    public void testPoll(){
        Task task = taskService.poll(100, 50);
        System.out.println(task);
    }


    @Autowired
    private CacheService cacheService;

    @Test //测试在redis中获取 key值
    public void testKeys(){
        //keys的模糊匹配功能很方便也很强大，但是在生产环境需要慎用！
        // 开发中使用keys的模糊匹配却发现redis的CPU使用率极高，所以公司的redis生产环境将keys命令禁用了！redis是单线程，会被堵塞
        Set<String> keys = cacheService.keys("future_*");
        System.out.println(keys);

        //SCAN 命令是一个基于游标的迭代器，SCAN命令每次被调用之后，
        // 都会向用户返回一个新的游标， 用户在下次迭代时需要使用这个新游标作为SCAN命令的游标参数， 以此来延续之前的迭代过程。
        Set<String> scan = cacheService.scan("future_*");
        System.out.println(scan);
    }

}