package edu.ylh.nio.componentAndFile;

import org.junit.Test;

/**
 * @author 靓仔
 * @date 2023/5/11
 */
public class Test1 {

    @Test
    public void t2(){
        // int i = 41;
        int i = 1;
        // int i = 41;
        // System.out.println(i / 10);
        System.out.println(1%10);
        System.out.println(2%10);
    }

    @Test
    public void t1(){
        // String s = "seal,check";
        // String s = "check";
        String s = "seal";
        // String s = "";
        // String[] split = s.split(",");
        // for (String s1 : split) {
        //     System.out.println(s1);
        // }
        // System.out.println(split.length);

        System.out.println(s.contains("seal"));
        System.out.println(s.contains("check"));

    }

}
