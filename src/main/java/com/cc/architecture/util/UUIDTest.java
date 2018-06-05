package com.cc.architecture.util;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * TODO 类的描述
 *
 * @author 蔡海涛
 * @createTime 2018-06-01 11:51:42
 */
public class UUIDTest {

    public static final int TOTAL_COUNT = 10000;
    static CountDownLatch countDownLatch = new CountDownLatch(TOTAL_COUNT);
    static Set<String> idSet = new HashSet<>(TOTAL_COUNT << 1);

    public static void main(String[] args) throws InterruptedException {

       for(int i = 0; i< TOTAL_COUNT; i++) {
            new Thread(new UUIDTest().new IdGenerator()).start();
        }
        countDownLatch.await();
        System.out.println("success total size:"+idSet.size());

        /*for(int i=0;i<TOTAL_COUNT;i++) {
            System.out.println("insert into ids(id) values("+(i+1) +");");
        }*/
    }



    class IdGenerator implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(Double.valueOf(Math.random() * 5).longValue());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            final String id = UUID.randomUUID().toString();
            System.out.println("insert into ids(id) values("+id +")");
            final boolean res = idSet.add(id);
            if(!res) {
                System.err.println("发现重复id："+id);
            }
            countDownLatch.countDown();
        }
    }
}
