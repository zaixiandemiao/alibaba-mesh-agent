package com.alibaba.dubbo.performance.demo.agent.protocol;

import com.alibaba.dubbo.performance.demo.agent.dubbo.RpcClient;

public class ProtocolService {

    public  static final int MAX_FRAME_LENGTH = 1024 * 1024;
    public  static final int LENGTH_FIELD_LENGTH = 4;
    public  static final int LENGTH_FIELD_OFFSET = 5;
    public  static final int LENGTH_ADJUSTMENT = 0;
    public  static final int INITIAL_BYTES_TO_STRIP = 0;

    private static ProviderConnectManager provider = null;
    private static final Object lock = new Object();

    public static ProviderConnectManager getProvider() {
        if (null == provider) {
            synchronized(lock) {
                if (null == provider) {
                    provider = new ProviderConnectManager();
                    provider.bindLocal(8888);
                }
            }
        }
        return provider;
    }
}
