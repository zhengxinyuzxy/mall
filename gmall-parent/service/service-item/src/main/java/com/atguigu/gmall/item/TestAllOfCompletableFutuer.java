package com.atguigu.gmall.item;

import java.util.concurrent.CompletableFuture;

public class TestAllOfCompletableFutuer {
    public static void main(String[] args) {

//        // allOf的作用，join就是分别get阻塞
//        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
//            System.out.println("任务一：开始" + Thread.currentThread().getName());
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.println("任务一：结束"+ Thread.currentThread().getName());
//        });
//        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
//            System.out.println("任务二：开始" + Thread.currentThread().getName());
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.println("任务二：结束"+ Thread.currentThread().getName());
//        });
//
//        CompletableFuture<Void> future3 = CompletableFuture.runAsync(() -> {
//            System.out.println("任务三：开始" + Thread.currentThread().getName());
//            try {
//                Thread.sleep(4000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.println("任务三：结束"+ Thread.currentThread().getName());
//        });
//
//        CompletableFuture<Void> future4 = CompletableFuture.runAsync(() -> {
//            System.out.println("任务四：开始" + Thread.currentThread().getName());
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.println("任务四：结束"+ Thread.currentThread().getName());
//        });
//
//        CompletableFuture<Void> future5 = CompletableFuture.runAsync(() -> {
//            System.out.println("任务五：开始" + Thread.currentThread().getName());
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.println("任务五：结束"+ Thread.currentThread().getName());
//        });
//        CompletableFuture.allOf(future1,future2 ,future3, future4, future5).join();
//        System.out.println("任务完成");


        // anfOf的作用, join就是分别get阻塞
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            System.out.println("任务一：开始" + Thread.currentThread().getName());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("任务一：结束"+ Thread.currentThread().getName());
        });
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            System.out.println("任务二：开始" + Thread.currentThread().getName());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("任务二：结束"+ Thread.currentThread().getName());
        });

        CompletableFuture<Void> future3 = CompletableFuture.runAsync(() -> {
            System.out.println("任务三：开始" + Thread.currentThread().getName());
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("任务三：结束"+ Thread.currentThread().getName());
        });

        CompletableFuture<Void> future4 = CompletableFuture.runAsync(() -> {
            System.out.println("任务四：开始" + Thread.currentThread().getName());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("任务四：结束"+ Thread.currentThread().getName());
        });

        CompletableFuture<Void> future5 = CompletableFuture.runAsync(() -> {
            System.out.println("任务五：开始" + Thread.currentThread().getName());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("任务五：结束"+ Thread.currentThread().getName());
        });
        CompletableFuture.anyOf(future1,future2 ,future3, future4, future5).join();
        System.out.println("任务完成");

    }
}
