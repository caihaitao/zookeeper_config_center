package com.cc.architecture;

import com.cc.architecture.util.ConfigAnnotationBeanPostProcessor;
import com.cc.architecture.util.ZKUtil;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * TODO 类的描述
 *
 * @author cc
 * @createTime 2018-05-26 15:34:49
 */
public class ZkClientTest extends ArchitectureApplicationTests {

    @Value("${server.id}")
    private String serviceId;

    @Test
    public void testZkClientOpt() throws InterruptedException {
        ZkClient zkClient = new ZkClient("127.0.0.1:2181",5000);
        zkClient.setZkSerializer(new ZKUtil.MyZkSerializer());
        Assert.notNull(serviceId);
        String rootPath = "/" + serviceId;

        doSubsribe(zkClient, rootPath);

        subscribeData(zkClient, rootPath);

        /*zkClient.createPersistent(rootPath);
        zkClient.createPersistent(rootPath + "/spring.datasource.url","jdbc:mysql://localhost:3306/mytest");
        zkClient.createPersistent(rootPath + "/spring.datasource.password","123321");
        zkClient.createPersistent(rootPath + "/orderNo","201805280001");*/
        Thread.sleep(500000);
    }

    private void doSubsribe(ZkClient zkClient, String rootPath) {
        final List<String> childrens = zkClient.getChildren(rootPath);
        if(!CollectionUtils.isEmpty(childrens)) {
            for(String child : childrens) {
                final String path = rootPath + "/" +child;
                subscribeData(zkClient, path);
                doSubsribe(zkClient,path);
            }
        }
    }

    private void subscribeData(ZkClient zkClient, String rootPath) {
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

}
