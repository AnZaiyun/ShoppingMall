package com.anzaiyun.shoppingmall.cart;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ListRemoveTest {

    private List<String> testList = new ArrayList<>();

    public void setTestList() {
        this.testList.add("C");
        this.testList.add("A");
        this.testList.add("B");
        this.testList.add("C");
        this.testList.add("C");
        this.testList.add("D");
        this.testList.add("C");
    }

    @Test
    public void test01() {
        setTestList();

        System.out.println("修改前：" + testList);

        /**
         * 报错java.util.ConcurrentModificationException
         * 原因分析：查看remove方法，是直接删除符合匹配条件的第一个字符，该方法没有问题
         * 实际的问题因此便是出在for循环的初始化中，查看报错信息也能得到这个结论
         * java.util.ConcurrentModificationException
         * 	at java.util.ArrayList$Itr.checkForComodification(ArrayList.java:909)
         * 	at java.util.ArrayList$Itr.next(ArrayList.java:859)
         * 查看ArrayList的Itr.next方法，其中会进行一个校验，校验方法如下
         * final void checkForComodification() {
         *   if (modCount != expectedModCount)
         *      throw new ConcurrentModificationException();
         * }
         * 这里的expectedModCount一开始会将modCount的值赋给他，modCount则是对列表的整个操作数，列表在初始化完成后，当前的操作数就是7
         * 然后在进行foreach的初始化时expectedModCount会被置为7，删除第一个数据后操作数+1，但是expectedModCount未改变，这就导致
         * 校验不通过，for循环报错
         */
        for (String s : testList) {
            if ("C".equals(s)) {
                testList.remove("C");
                System.out.println("修改后：" + testList);
            }
        }

        System.out.println("修改后：" + testList);

    }

    @Test
    public void test02() {
        setTestList();

        System.out.println("修改前：" + testList);
        /**
         * 将增强for改写普通for，执行无报错，但是数据的结果却不正确
         * 修改前：[C, A, B, C, C, D, C]
         * 修改后：[A, B, D, C]
         * 明显的可以看到最后一个C未删除成功
         * 这是因为每次删除数据后，list的size都在变化
         * 以 a，c，c 中删除c为例
         * 第1次循环i=0，a<>c,size=3,结束i=1
         * 第2次循环i=1，c==c,删除c，size=2，结束i=2
         * 此时循环条件不成立，退出循环，最后一个数据未被删除
         */
        for (int i=0; i<testList.size(); i++) {
            if ("C".equals(testList.get(i))) {
                testList.remove("C");
            }
        }
        System.out.println("修改后：" + testList);

    }

    @Test
    public void test03() {
        setTestList();

        System.out.println("修改前：" + testList);
        /**
         * public boolean remove(Object o) {
         *         if (o == null) {
         *             for (int index = 0; index < size; index++)
         *                 if (elementData[index] == null) {
         *                     fastRemove(index);
         *                     return true;
         *                 }
         *         } else {
         *             for (int index = 0; index < size; index++)
         *                 if (o.equals(elementData[index])) {
         *                     fastRemove(index);
         *                     return true;
         *                 }
         *         }
         *         return false;
         *     }
         * remove删除成功会返回true，因此可以通过该种方式删除
         */
        while (testList.remove("C"));
        System.out.println("修改后：" + testList);



    }
    @Test
    public void test04() {
        setTestList();

        testList.add(null);
        System.out.println("修改前：" + testList);
        /**
         * 这两种写法 运行移除 C 的时候都是没问题的。
         * 但是当需要删除null数据时，则需要注意
         * public boolean removeAll(Collection<?> c) {
         *         Objects.requireNonNull(c); //该方法的存在使得不能直接传入null
         *         return batchRemove(c, false);
         *     }
         *
         * 查看Arrays.asList可知入参是当list处理，而list的构造方法是不许传入null的
         * 查看Collections.singleton可知入参是当做set处理，而set的构造方法则是可以传入null的
         */
        testList.removeAll(Collections.singleton(null));
//        testList.removeAll(Arrays.asList("C"));
        System.out.println("修改后：" + testList);
    }

    @Test
    public void test05() {
        setTestList();

        testList.add(null);
        System.out.println("修改前：" + testList);
        List<String> c = testList.stream().filter(o -> !(null == o || o.equals("C") || o.equals(""))).collect(Collectors.toList());
        System.out.println("修改后：" + c);
    }
}
