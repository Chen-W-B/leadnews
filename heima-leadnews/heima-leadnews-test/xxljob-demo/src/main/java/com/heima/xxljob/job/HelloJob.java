package com.heima.xxljob.job;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HelloJob {

    @Value("${server.port}")
    private String port;

    @XxlJob("demoJobHandler")
    public void helloJob(){
        System.out.println("简单任务执行了。。。。"+port);
    }

    @XxlJob("shardingJobHandler")
    public void shardingJobHandler(){
        //分片的参数
        int shardIndex = XxlJobHelper.getShardIndex();//当前分片序号(从0开始)，执行器集群列表中当前执行器的序号
        int shardTotal = XxlJobHelper.getShardTotal();//总分片数，执行器集群的总机器数量

        //业务逻辑
        List<Integer> list = getList();
        for (Integer integer : list) {
            if (integer % shardTotal == shardIndex){
                //任务的序号 % 分片数 = 分片的序号
                //例如：总共10个任务（序号为0~9），2个分片（序号为0~1）
                // 第5个任务被 分片0 执行：4 % 2 = 0
                // 第6个任务被 分片1 执行：5 % 2 = 1
                System.out.println("当前第"+shardIndex+"分片执行了，任务项为："+integer);
            }
        }
    }

    public List getList(){
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            list.add(i);
        }
        return list;
    }

}
