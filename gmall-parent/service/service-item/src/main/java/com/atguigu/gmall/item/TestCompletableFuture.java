package com.atguigu.gmall.item;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class TestCompletableFuture {
    public static void main(String[] args) throws Exception {
        /**
         * 测试1: runAsync(无返回值)和SupplyAsync(有返回值)的区别
         */
//        System.out.println("主线程工作" + Thread.currentThread().getName());
//        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("有返回值的开始工作-supplyAsync" + Thread.currentThread().getName());
//            // 沉睡
//            try {
//                Thread.sleep(10000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return 1;
//        });
//        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
//            System.out.println("没有返回值的开始工作-runAsync" + Thread.currentThread().getName());
//        });
//        // 调用者调用.get(), 谁是调用线程谁阻塞，get会阻塞住调用线程
//        System.out.println("主线程复活又工作, 返回值是L:" + future1.get());
//    }

        /**
         * 测试2: thenRun，thenAppept，thenApply，不加Async
         */
        // thenAppept：接收第一步操作结果充当第二步的参数，但第二步不返回结果
        CompletableFuture<Void> future1 = CompletableFuture.supplyAsync(() -> {
            // 第一步：返回结果
            System.out.println("任务一：" + Thread.currentThread().getName());
            return 1;
        }).thenAccept((abc) -> {
            // 第二步
            // 接收第一步的返回结果，但第二步不再返回结果
            System.out.println("任务二：future1使用thenAppept没有返回值，但接收supplyAsync的结果为参数：" + abc + Thread.currentThread().getName());
        });

        // thenRun：第一步和第二步没有关系，第二步没有参数也没有返回结果
        CompletableFuture<Void> future2 = CompletableFuture.supplyAsync(() -> {
            // 第一步：返回结果
            System.out.println("任务一：" + Thread.currentThread().getName());
            return 1;
        }).thenRun(() -> {
            // 第二步：执行第二步自己的操作，与第一步操作无关
            System.out.println("任务二：future2使用thenRun没有参数也没有返回值，相互独立" + Thread.currentThread().getName());
        });

        // thenApply：接收第一步的结果作为参数，第二步可选择性对参数加工，第二步可返回结果
        CompletableFuture<Integer> future3 = CompletableFuture.supplyAsync(() -> {
            // 第一步：返回结果
            System.out.println("任务一：" + Thread.currentThread().getName());
            return 1;
        }).thenApply((a) -> {
            // 第二步：可以对第一步的结果进行再加工，也可以返回自定义的值
            return a * 10;
        });
        System.out.println("任务二：future3使用thenapply有参数有返回值" + future3.get() + Thread.currentThread().getName());

        System.out.println("=========================================");

        /**
         * 测试3: thenRun，thenAppept，thenApply, 加Async
         */
        // thenAppept：接收第一步操作结果充当第二步的参数，但第二步不返回结果
        CompletableFuture<Void> future4 = CompletableFuture.supplyAsync(() -> {
            // 第一步：返回结果
            System.out.println("任务一：" + Thread.currentThread().getName());
            return 1;
        }).thenAcceptAsync((abc) -> {
            // 第二步
            // 接收第一步的返回结果，但第二步不在返回结果
            System.out.println("任务二：future4使用thenAcceptAsync没有返回值，但接收supplyAsync的结果为参数：" + abc + Thread.currentThread().getName());
        });

        // thenRun：第一步和第二步没有关系，第二步没有参数也没有返回结果
        CompletableFuture<Void> future5 = CompletableFuture.supplyAsync(() -> {
            // 第一步：返回结果
            System.out.println("任务一：" + Thread.currentThread().getName());
            return 1;
        }).thenRunAsync(() -> {
            // 第二步：执行第二步自己的操作，与第一步操作无关
            System.out.println("任务二：future5使用thenRunAsync没有参数也没有返回值，相互独立" + Thread.currentThread().getName());
        });

        // thenApply：接收第一步的结果作为参数，第二步可选择性对参数加工，第二步可返回结果
        CompletableFuture<Integer> future6 = CompletableFuture.supplyAsync(() -> {
            // 第一步：返回结果
            System.out.println("任务一：" + Thread.currentThread().getName());
            return 1;
        }).thenApplyAsync((a) -> {
            // 第二步：可以对第一步的结果进行再加工，也可以返回自定义的值
            System.out.println("任务二：future6使用thenApplyAsync有参数有返回值" + Thread.currentThread().getName());
            return a * 10;
        });
        System.out.println("主线程拿到返回值" + future6.get() + Thread.currentThread().getName());

        System.out.println("=========================");

        // whenComplete：记录下异常
        // thenApply：接收第一步的结果作为参数，第二步可选择性对参数加工，第二步可返回结果
        CompletableFuture<Integer> futureWhenComplete = CompletableFuture.supplyAsync(() -> {
            // 第一步：返回结果
            System.out.println("任务一：" + Thread.currentThread().getName());
            return 1;
        }).thenApplyAsync((a) -> {
            // 第二步：可以对第一步的结果进行再加工，也可以返回自定义的值
            System.out.println("任务二：futureWhenComplete使用thenApplyAsync有参数有返回值" + Thread.currentThread().getName());
            int i = 10 / 0;
            return a * 10;
        }).whenComplete(new BiConsumer<Integer, Throwable>() {
            @Override
            public void accept(Integer integer, Throwable throwable) {
                System.out.println("任务执行结果为：" + integer);
                System.out.println("任务执行异常为：" + throwable);
            }
        });

        System.out.println("======================");

        // exceptionally：接收异常，不接收参数，但可对返回结果自定义
        // thenApply：接收第一步的结果作为参数，第二步可选择性对参数加工，第二步可返回结果
        CompletableFuture<Integer> futureExceptionally = CompletableFuture.supplyAsync(() -> {
            // 第一步：返回结果
            System.out.println("任务一：" + Thread.currentThread().getName());
            return 1;
        }).thenApplyAsync((a) -> {
            // 第二步：可以对第一步的结果进行再加工，也可以返回自定义的值
            System.out.println("任务二：futureExceptionally使用thenApplyAsync有参数有返回值" + Thread.currentThread().getName());
            int i = 10 / 0;
            return a * 10;
        }).exceptionally((e) -> {
            // 接收异常，不接收参数，但可对返回结果自定义
            e.printStackTrace();
            return 12315;
        });

        System.out.println("====================================");

        // handle：接收异常，接收参数，可对返回结果自定义,并且三步操作使用的是同一个线程
        // thenApply：接收第一步的结果作为参数，第二步可选择性对参数加工，第二步可返回结果
        CompletableFuture<Integer> futureHandle = CompletableFuture.supplyAsync(() -> {
            // 第一步：返回结果
            System.out.println("任务一：" + Thread.currentThread().getName());
            return 1;
        }).thenApplyAsync((a) -> {
            // 第二步：可以对第一步的结果进行再加工，也可以返回自定义的值
            System.out.println("任务二：futureExceptionally使用thenApplyAsync有参数有返回值" + Thread.currentThread().getName());
            int i = 10 / 0;
            return a * 10;
        }).handle(new BiFunction<Integer, Throwable, Integer>() {
            @Override
            public Integer apply(Integer integer, Throwable throwable) {
                System.out.println("任务执行结果：" + integer + "当前使用的线程是："+ Thread.currentThread().getName());
                System.out.println("任务执行异常：" +throwable + "当前使用的线程是："+ Thread.currentThread().getName());
                return 12309;
            }
        });
        System.out.println(futureHandle.get() + Thread.currentThread().getName());
    }

}