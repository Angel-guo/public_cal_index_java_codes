package com.catl.cal_index.test;//package com.catl.download.test;
//
//import com.catl.download.entity.CalIndexStatusFutures;
//import com.catl.download.entity.calIndex.CalIndexRecord;
//import org.springframework.jdbc.datasource.DataSourceUtils;
//
//import java.io.PrintStream;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.Date;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class Test {
//
//    private static volatile ConcurrentHashMap<String, CalIndexStatusFutures> map = new ConcurrentHashMap<>();
//
//    public static void main(String[] args) {
////        for (int i = 1000; i > 0; i--) {
////            map.put("key" + i, new CalIndexStatusFutures(String.valueOf(i)));
////        }
////
////        extractMap(map);
//
////        String calIndex = "A";
////        String calIndexVars = "A_X1_Y1_Z1";
////        String[] split = calIndexVars.split(calIndex + "_");
////        for (String s : split) {
////            System.out.println(s);
////        }
////
////        System.out.println(split[0]);
//
////        System.out.println(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));
////        System.out.println(new CalIndexRecord());
//
////        try {
////            String vins = "vin1,vin2,vin3";
////            String[] split = vins.split(",");
////            for (String s : split) {
////                System.out.println(s);
////                System.out.println(s.equals("vin1"));
////            }
////            System.out.println(split[10]);
////        } catch (Exception e) {
////            System.out.println("出错了，错误信息---" + e);
////        } finally {
////            System.out.println("有没有出错都执行！！！！");
////        }
////        map.forEach((key, value) -> System.out.println(key + "---------" + value));
//
//
//
//
//
//    }
//
//
//    public static void extractMap(ConcurrentHashMap<String, CalIndexStatusFutures> map) {
//        // ########################################################################
//        // ######################## map 维护策略
//        // ########################################################################
//        // 监控map中的任务数量, 运行状态： -1 初始态；0 运行成功；1 运行中；2 运行失败
//        // 如果数量达到1000之后，将非运行态的移除
//        if (map.size() == 1000) {
////            map.entrySet().removeIf(entry -> !entry.getValue().getDateTime().contains("1"));
//            // 如果上面尝试移除之后数量还是1000，那么就将时间距离最远的100个任务删除掉
//            if (map.size() == 1000) {
//                ArrayList<Map.Entry<String, CalIndexStatusFutures>> entryList = new ArrayList<>(map.entrySet());
//                // 正序排序
//                entryList.sort(new Comparator<Map.Entry<String, CalIndexStatusFutures>>() {
//                    @Override
//                    public int compare(Map.Entry<String, CalIndexStatusFutures> o1, Map.Entry<String, CalIndexStatusFutures> o2) {
//                        return (int) (Long.parseLong(o1.getValue().getDateTime()) - Long.parseLong(o2.getValue().getDateTime()));
//                    }
//                });
//                entryList.sort(Comparator.comparing(obj -> obj.getValue().getDateTime()));
//                entryList.forEach(obj -> {
//                    System.out.println(obj.getKey() + "----------" + obj.getValue());
//                });
//
//                // 将前100个距离最远的任务移除
//                for (int i = 0; i < 100; i++) {
//                    map.remove(entryList.get(i).getKey());
//                }
//            }
//        }
//
//    }
//}


