package com.heima.schedule;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@MapperScan("com.heima.schedule.mapper")
@EnableScheduling //开启调度任务
public class ScheduleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScheduleApplication.class,args);
    }

    //悲观锁 -- 每次去拿数据的时候都认为别人会修改，所以每次在拿数据的时候都会上锁
    //乐观锁 -- 每次去拿数据的时候都认为别人不会修改，所以不会上锁，
    // 但是在更新的时候会判断一下在此期间别人有没有去更新这个数据，可以使用版本号等机制
    /**
     * mybatis-plus乐观锁支持拦截器
     * @return
     */
    @Bean
    public MybatisPlusInterceptor optimisticLockerInterceptor(){
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }
}
