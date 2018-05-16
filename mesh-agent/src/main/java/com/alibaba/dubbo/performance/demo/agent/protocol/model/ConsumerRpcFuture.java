package com.alibaba.dubbo.performance.demo.agent.protocol.model;

import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcResponse;
import com.alibaba.dubbo.performance.demo.agent.protocol.ProtocolMsg;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class ConsumerRpcFuture  implements Future<Integer> {
    private CountDownLatch latch = new CountDownLatch(1);
    private Sync sync = new Sync();

    private Integer response;

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public Integer get() throws InterruptedException {
        //boolean b = latch.await(100, TimeUnit.MICROSECONDS);
//        latch.await();
        sync.acquire(-1);
        try {
            return response;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Integer get(long timeout, TimeUnit unit) throws InterruptedException {
//        boolean b = latch.await(timeout,unit);
        boolean success = sync.tryAcquireNanos(-1, unit.toNanos(timeout));
        if(success)
            return response;
        return null;
    }

    public void done(Integer response){
        this.response = response;
        sync.release(1);
//        latch.countDown();
    }

    static class Sync extends AbstractQueuedSynchronizer {

        private static final long serialVersionUID = 1L;

        //future status
        private final int done = 1;
        private final int pending = 0;

        @Override
        protected boolean tryAcquire(int arg) {
            return getState() == done;
        }

        @Override
        protected boolean tryRelease(int arg) {
            if (getState() == pending) {
                if (compareAndSetState(pending, done)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }

        public boolean isDone() {
            return getState() == done;
        }
    }

}