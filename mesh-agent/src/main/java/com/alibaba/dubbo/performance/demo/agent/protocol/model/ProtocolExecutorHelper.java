package com.alibaba.dubbo.performance.demo.agent.protocol.model;

import java.util.concurrent.*;

public class ProtocolExecutorHelper {
    private static final int PROCESSOR_NUM = Runtime.getRuntime().availableProcessors();

    public static ExecutorService newBlockingExecutorsUseCallerRun(int size) {
        return new ThreadPoolExecutor(size, size, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        try {
                            executor.getQueue().put(r);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
    }
}
