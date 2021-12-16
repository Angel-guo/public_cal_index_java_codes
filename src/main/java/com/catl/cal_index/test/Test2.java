package com.catl.cal_index.test;

import java.util.*;
import java.util.stream.Collectors;

public class Test2 {

    public static void main(String[] args) {
        Set<List<String>> doubleSet = new HashSet<>();
        List<String> set1 = new ArrayList<>();
        List<String> set2 = new ArrayList<>();
        List<String> set3 = new ArrayList<>();
        set1.add("2021-10-11");
        set1.add("123");
        set2.add("2021-10-12");
        set2.add("abc");
        set3.add("2021-09-11");
        set3.add("123");
        doubleSet.add(set1);
        doubleSet.add(set2);
        doubleSet.add(set3);
        List<String> collect = doubleSet.stream().map(obj -> obj.get(0)).sorted().collect(Collectors.toList());
        System.out.println(doubleSet);
        System.out.println(collect);

    }


}
