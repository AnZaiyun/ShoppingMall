package com.anzaiyun.shoppingmall.search.thread;

import java.util.concurrent.*;

public class CompletableFutureTest {

    public static ExecutorService executorService = new ThreadPoolExecutor(10, 20, 300, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>());

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main start ...");

//        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行结果：" + i);
//
//        }, executorService);

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

        CompletableFuture<Integer> supplyAsync = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 4;
            System.out.println("运行结果：" + i);
            return i;
        }, executorService).handle((res,exc)->{
            if(res!=null){
                return res*3;
            }

            if (exc!=null){
                return 777;
            }

            return 0;
        });

        Integer integer = supplyAsync.get();
        System.out.println(integer);

        System.out.println("main end ...");


    }
}
