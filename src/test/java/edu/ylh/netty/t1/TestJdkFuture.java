package edu.ylh.netty.t1;


import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author 靓仔
 * @date 2023/5/17
 */
@Slf4j
public class TestJdkFuture {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        // 1. 创建线程池
        ExecutorService service =  Executors.newFixedThreadPool(2);
        // 2. 提交任务
        Future<Integer> future = service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.debug("执行计算");
                Thread.sleep(1000);
                return 50;
            }
        });

        // 3. 主线程通过 future 获取结果
        log.debug("等待结果");
        log.debug("结果是{}",future.get());
        log.debug("结束");

    }

}
