package com.cc.architecture;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * TODO 类的描述
 *
 * @author 蔡海涛
 * @createTime 2018-06-04 17:27:20
 */
public class MutiThreadTest {

    class Worker implements Runnable {

        private CountDownLatch countDownLatch;

        public Worker(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            try {
                countDownLatch.countDown();
                Thread.sleep(new Random().nextInt(10));
                System.out.println(Thread.currentThread().getName()+" get instance:"+MySingleton.getInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(100);
        for(int i=0;i<100;i++) {
            new Thread(new MutiThreadTest().new Worker(countDownLatch)).start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

   static class MySingleton {


        public static MySingleton getInstance() {
            MySingleton mySingleton = MySingletonHolder.get();
            if(mySingleton == null) {
                synchronized (MySingleton.class) {
                    mySingleton = MySingletonHolder.get();
                    if(mySingleton == null) {
                        mySingleton = new MySingleton();
                        MySingletonHolder.set(mySingleton);
                    }
                }
            }

            return mySingleton;
        }

        static class MySingletonHolder {
            private static MySingleton mySingleton;

            public static MySingleton get() {
                return mySingleton;
            }

            public static void set(MySingleton mySingleton) {
                MySingletonHolder.mySingleton = mySingleton;
            }
        }
    }
}
