package com.anzaiyun.shoppingmall.search.thread;

import java.util.concurrent.*;

public class CompletableFutureTest {

    /**
     * corePoolSize 核心线程数，线程池中该部分线程一致处于激活状态
     * maximumPoolSize 线程池允许存在的最大线程数
     * keepAliveTime 空闲线程允许存在的最大存活时间
     * TimeUnit unit 线程允许存在的最大存活时间的单位
     * BlockingQueue<Runnable> workQueue  任务队列，用于缓存当前未执行的任务
     * ThreadFactory threadFactory  线程工厂。可通过工厂为新建的线程设置更有意义的名字
     * RejectedExecutionHandler handler 拒绝策略。当线程池和任务队列均处于饱和状态时，使用拒绝策略处理新任务。默认是 AbortPolicy，即直接抛出异常
     *
     *
     * 在 Java 线程池实现中，线程池所能创建的线程数量受限于 corePoolSize 和 maximumPoolSize 两个参数值。
     * 线程的创建时机则和 corePoolSize 以及 workQueue 两个参数有关。
     * 下面列举一下线程创建的4个规则（线程池中无空闲线程），如下：
     *
     * 线程数量小于 corePoolSize，直接创建新线程处理新的任务
     * 线程数量大于等于 corePoolSize，workQueue 未满，则缓存新任务
     * 线程数量大于等于 corePoolSize，但小于 maximumPoolSize，且 workQueue 已满。则创建新线程处理新任务
     * 线程数量大于等于 maximumPoolSize，且 workQueue 已满，则使用拒绝策略处理新任务
     */
    public static ExecutorService executorService =
            new ThreadPoolExecutor(10,
                    20,
                    300,
                    TimeUnit.SECONDS,
            new LinkedBlockingQueue<>());

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main start ...");

        /**
         * runAsync方法无法接收到返回值
         */
//        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行结果：" + i);
//
//        }, executorService);

        /**
         * supplyAsync方法可以接收到返回值
         * whenComplete可以感知到异常，获取到异常信息，但是不能处理异常
         * exceptionally可以感知到异常，并对异常做处理，修改返回值
         */
//        CompletableFuture<Integer> supplyAsync = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 0;
//            System.out.println("运行结果：" + i);
//            return i;
//        }, executorService).whenComplete((res,exc)->{
//            //可以感知异常，不能处理
//            System.out.println("异步任务完成了，结果是："+res+",异常是："+exc);
//        }).exceptionally((throwable)->{
//            //可以感知异常，并对异常进行处理，修改返回值
//            return 10;
//        });

        /**
         * handle相当于whenComplete和exceptionally结合
         */
//        CompletableFuture<Integer> supplyAsync = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 4;
//            System.out.println("运行结果：" + i);
//            return i;
//        }, executorService).handle((res,exc)->{
//            if(res!=null){
//                return res*3;
//            }
//
//            if (exc!=null){
//                return 777;
//            }
//
//            return 0;
//        });

        /**
         * thenAcceptAsync获取到上一线程返回值并处理，当前线程无返回值
         */
//        CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 4;
//            System.out.println("运行结果：" + i);
//            return i;
//        }, executorService).thenAcceptAsync((result) -> {
//
//            System.out.println("任务2启动,获取到上一步返回值：" + result);
//        }, executorService);

        /**
         * thenApplyAsync获取到上一线程返回值并处理，且当前线程有返回值
         */
//        CompletableFuture<String> thenApplyAsync = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 4;
//            System.out.println("运行结果：" + i);
//            return i;
//        }, executorService).thenApplyAsync((result) -> {
//
//            System.out.println("任务2启动,获取到上一步返回值：" + result);
//            return "接收到返回值" + result;
//        }, executorService);

//        String s = thenApplyAsync.get();
//        System.out.println(s);

//        CompletableFuture<String> future01 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("线程111");
//            return "线程111";
//        },executorService);
//
//        CompletableFuture<String> future02 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("线程222");
//            return "线程222";
//        }, executorService);

        /**
         * 线程1、2同时执行完成后，执行线程3,无法获取线程1、2的返回值
         */
//        future01.runAfterBothAsync(future02,()->{
//            System.out.println("线程333");
//        },executorService);

        /**
         * 线程1、2同时执行完成后，执行线程3,可以获取线程1、2的返回值，线程3无返回值
         */
//        future01.thenAcceptBothAsync(future02,(res01,res02)->{
//            System.out.println("线程333...start...");
//
//            System.out.println("res01 is "+res01+",res02 is "+res02);
//
//            System.out.println("线程333...end...");
//
//        },executorService);

        /**
         * 线程1、2同时执行完成后，执行线程3,可以获取线程1、2的返回值，线程3可以返回值
         */
//        CompletableFuture<String> thenCombineAsync = future01.thenCombineAsync(future02, (res01, res02) -> {
//            System.out.println("线程333...start...");
//
//            System.out.println("res01 is " + res01 + ",res02 is " + res02);
//
//            System.out.println("线程333...end...");
//
//            return "线程333";
//
//        }, executorService);
//
//        System.out.println(thenCombineAsync.get());


        CompletableFuture<String> allFuture01 = CompletableFuture.supplyAsync(() -> {
            System.out.println("线程allFuture01");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int i = 10/0;
            return "线程allFuture01";
        },executorService);

        CompletableFuture<String> allFuture02 = CompletableFuture.supplyAsync(() -> {
            System.out.println("线程allFuture02");
            return "线程allFuture02";
        },executorService);

        CompletableFuture<String> allFuture03 = CompletableFuture.supplyAsync(() -> {
            System.out.println("线程allFuture03");
            return "线程allFuture03";
        },executorService);

        /**
         * 全部的线程都成功才返回执行下一个线程
         */
        CompletableFuture.allOf(allFuture01, allFuture02, allFuture03).thenAccept((result01) -> {
            System.out.println("allOf获取到的结果是"+result01);
            System.out.println("allOf全部执行完毕");
        }).exceptionally(expc->{
            System.out.println("allOf捕捉到异常");
            return null;
        });

        /**
         * 只要有一个线程都成功就返回执行下一个线程？？
         * 这里有点问题，这个判断可能是按照顺序判断的，如果第一个线程报错，及时其他的线程不报错还是不会执行下一个异常
         *
         * 查阅资料后这里的正确理解其实是
         * 只要异步线程队列有一个任务率先完成就返回，这个特性可以用来获取最快的那个线程结果。
         * 如果第一个返回的是报错，那外部线程捕获到的就是报错
         */
        CompletableFuture.anyOf(allFuture01, allFuture02, allFuture03).thenAccept((result01) -> {
            System.out.println("anyOf获取到的结果是"+result01);
            System.out.println("anyOf全部执行完毕");
        }).exceptionally(expc->{
            System.out.println("anyOf捕捉到异常");
            return null;
        });

        System.out.println("main end ...");


    }
}
