package com.cc.architecture.util;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

/**
 * TODO 类的描述
 *
 * @author 蔡海涛
 * @createTime 2018-06-04 10:13:01
 */
@Component
public class ZKUtil implements PriorityOrdered{

   // @Value("${server.zkUrl}")
    private static String zkUrl;
   // @Value("${server.zkTimeOut}")
    private static Integer zkTimeOut;

    public static String serverId;

    static {
        Properties properties = new Properties();
        InputStream inputStream = ZKUtil.class.getClassLoader().getResourceAsStream("configCenter.properties");
        try {
            properties.load(inputStream);
            zkUrl = properties.getProperty("server.zkUrl");
            zkTimeOut = Integer.valueOf(properties.getProperty("server.zkTimeOut"));
            serverId = properties.getProperty("server.id");
        } catch (IOException e) {
            throw new BeanInitializationException("找不到配置中心配置");
        }
    }

    public static ZkClient getConnection() {
        ZkClient zkClient = ZkClientHolder.get();
        if (zkClient == null) {
            zkClient = new ZkClient(zkUrl, zkTimeOut);
            zkClient.setZkSerializer(new MyZkSerializer());
            ZkClientHolder.set(zkClient);
        }

        return zkClient;
    }

    public static Object getValue(String path) {
        ZkClient zkClient = getConnection();
        return zkClient.readData(path);
    }

    public static List<String> getChildren(String path) {
        ZkClient zkClient = getConnection();
        return zkClient.getChildren(path);
    }

    public static void dataChangeListener(String path) {
        ZkClient zkClient = getConnection();
        doSubscribe(zkClient, path);

        subscribeData(zkClient, path);
    }

    private static void doSubscribe(ZkClient zkClient, String rootPath) {
        final List<String> childrens = zkClient.getChildren(rootPath);
        if(!CollectionUtils.isEmpty(childrens)) {
            for(String child : childrens) {
                final String path = rootPath + "/" +child;
                subscribeData(zkClient, path);
                doSubscribe(zkClient,path);
            }
        }
    }

    private static void subscribeData(ZkClient zkClient, String rootPath) {
        zkClient.subscribeChildChanges(rootPath, (parentPath, currentChilds) -> {
            System.out.println("路径" + parentPath +"下面的子节点变更。子节点为：" + currentChilds );
        });
        zkClient.subscribeDataChanges(rootPath, new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                ConfigAnnotationBeanPostProcessor.loadConfigs();
                ConfigAnnotationBeanPostProcessor.afterProcess();
                System.out.println("路径" + dataPath +"下面的子节点变更。子节点变更为：" + data );
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                ConfigAnnotationBeanPostProcessor.loadConfigs();
                ConfigAnnotationBeanPostProcessor.afterProcess();
                System.out.println("路径" + dataPath +"下面的子节点被删除。");
            }
        });

    }

    @Override
    public int getOrder() {
        return 0;
    }

    private static class ZkClientHolder {
        private static ZkClient zkClient;

        public static ZkClient get() {
            return zkClient;
        }

        public static void set(ZkClient zkClient) {
            ZkClientHolder.zkClient = zkClient;
        }
    }

    public static class MyZkSerializer implements ZkSerializer {
        @Override
        public Object deserialize(byte[] bytes) throws ZkMarshallingError {
            try {
                return new String(bytes, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        }

        @Override
        public byte[] serialize(Object obj) throws ZkMarshallingError {
            try {
                return String.valueOf(obj).getBytes("utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        }
    }

}
