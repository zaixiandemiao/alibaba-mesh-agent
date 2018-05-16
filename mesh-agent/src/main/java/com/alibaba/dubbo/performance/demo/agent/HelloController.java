package com.alibaba.dubbo.performance.demo.agent;

import com.alibaba.dubbo.performance.demo.agent.dubbo.RpcClient;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.Bytes;
import com.alibaba.dubbo.performance.demo.agent.protocol.ProtocolService;
import com.alibaba.dubbo.performance.demo.agent.protocol.ProviderConnectManager;
import com.alibaba.dubbo.performance.demo.agent.protocol.model.ProtocolExecutorHelper;
import com.alibaba.dubbo.performance.demo.agent.protocol.process.ConsumerRpcClient;
import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
import com.alibaba.dubbo.performance.demo.agent.registry.EtcdRegistry;
import com.alibaba.dubbo.performance.demo.agent.registry.IRegistry;
import com.alibaba.fastjson.JSON;
import okhttp3.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

@RestController
public class HelloController {

    private Logger logger = LoggerFactory.getLogger(HelloController.class);

    private IRegistry registry = new EtcdRegistry(System.getProperty("etcd.url"));

//    private RpcClient rpcClient = new RpcClient(registry);

    private ConsumerRpcClient client = null;
    private Random random = new Random();
    private List<Endpoint> endpoints = null;
    private Object lock = new Object();
    private OkHttpClient httpClient = new OkHttpClient();

    private ExecutorService workerThreadService = null;



    @RequestMapping(value = "")
    public Object invoke(@RequestParam("interface") String interfaceName,
                         @RequestParam("method") String method,
                         @RequestParam("parameterTypesString") String parameterTypesString,
                         @RequestParam("parameter") String parameter) throws Exception {
        String type = System.getProperty("type");   // 获取type参数
        if ("consumer".equals(type)){
            return consumer(interfaceName,method,parameterTypesString,parameter);
        }
        else if ("provider".equals(type)){
            return provider(interfaceName,method,parameterTypesString,parameter);
        }else {
            return "Environment variable type is needed to set to provider or consumer.";
        }
    }

    public byte[] provider(String interfaceName,String method,String parameterTypesString,String parameter) throws Exception {

//        Object result = rpcClient.invoke(interfaceName,method,parameterTypesString,parameter);
        byte[] result = Bytes.intToByteArray(parameter.hashCode());
        Thread.sleep(50);
        return (byte[]) result;
    }

    public Integer consumer(String interfaceName,String method,String parameterTypesString,String parameter) throws Exception {

        if (null == endpoints){
            synchronized (lock){
                if (null == endpoints){
                    endpoints = registry.find("com.alibaba.dubbo.performance.demo.provider.IHelloService");
                    client = new ConsumerRpcClient(endpoints.size());
                    workerThreadService = ProtocolExecutorHelper.newBlockingExecutorsUseCallerRun(Runtime.getRuntime().availableProcessors() * 2);
                    System.out.println("first using consumer");
                }
            }
        }

        // 简单的负载均衡，随机取一个
//        Endpoint endpoint = endpoints.get(random.nextInt(endpoints.size()));
        int index = random.nextInt(endpoints.size());
        Endpoint endpoint = endpoints.get(index);
//        logger.info("provider agent ip -> " + endpoint.getHost());
//        Callable<Integer> hashCall = new Callable<Integer>() {
//            @Override
//            public Integer call() throws Exception {
//                return client.invoke(index, endpoint.getHost(), 8888,
//                        interfaceName, method, parameterTypesString, parameter);
//            }
//        };
//        FutureTask<Integer> hashTask = new FutureTask<Integer>(hashCall);
//        workerThreadService.submit(hashTask);


        Integer result = client.invoke(index, endpoint.getHost(), 8888,
                interfaceName, method, parameterTypesString, parameter);
        return result;
//        String url =  "http://" + endpoint.getHost() + ":" + endpoint.getPort();
//
//        RequestBody requestBody = new FormBody.Builder()
//                .add("interface",interfaceName)
//                .add("method",method)
//                .add("parameterTypesString",parameterTypesString)
//                .add("parameter",parameter)
//                .build();
//
//        Request request = new Request.Builder()
//                .url(url)
//                .post(requestBody)
//                .build();
//
//        try (Response response = httpClient.newCall(request).execute()) {
//            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
//            byte[] bytes = response.body().bytes();
//            return Bytes.byteArrayToInt(bytes);
////            return JSON.parseObject(bytes, Integer.class);
//        }
    }
}
