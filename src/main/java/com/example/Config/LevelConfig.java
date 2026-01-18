//package com.example.Config;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class LevelConfig {
//    public static final Map<String, LevelInfo>Level_MAP=new HashMap<>();
//    static {
//        Level_MAP.put("见习学徒", new LevelInfo(0, 1.0, "#A1887F"));
//        Level_MAP.put("青铜学者", new LevelInfo(100, 1.2, "#795548"));
//        Level_MAP.put("银辉行者", new LevelInfo(500, 1.5, "#9E9E9E"));
//        Level_MAP.put("鎏金匠人", new LevelInfo(2000, 2.0, "#FFD700"));
//        Level_MAP.put("翠玉守护者", new LevelInfo(5000, 3.0, "#4CAF50"));
//        Level_MAP.put("晶钻贤者", new LevelInfo(10000, 5.0, "#00BCD4"));
//    }
//
//    //根据经验值获取等级
//    public static String getLevelByExp(int exp) {
//        // 处理负数经验的情况
//        if (exp < 0) {
//            return "见习学徒"; // 或抛出异常，根据业务需求决定
//        }
//
//        // 按照经验从高到低排列，便于顺序比较
//        if (exp >= 10000) return "晶钻贤者";
//        if (exp >= 5000) return "翠玉守护者";
//        if (exp >= 2000) return "鎏金匠人";
//        if (exp >= 500) return "银辉行者";
//        if (exp >= 100) return "青铜学者";
//        return "见习学徒";
//    }
//}
