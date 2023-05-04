package com.junkiedan.ludumdare53;

import java.util.HashMap;
import java.util.Map;

public class TargetHouseMap {
    private static Map<Integer, int[]> map;

    public static void init() {
        map = new HashMap<>();
        map.put(1, new int[]{0, 12});
        map.put(2, new int[]{0, 13});
        map.put(3, new int[]{1, 0});
        map.put(4, new int[]{2, 2});
        map.put(5, new int[]{2, 5});
        map.put(6, new int[]{2, 8});
        map.put(7, new int[]{2, 13});
        map.put(8, new int[]{2, 18});
        map.put(9, new int[]{3, 13});
        map.put(10, new int[]{4, 0});
        map.put(11, new int[]{5, 10});
        map.put(12, new int[]{6, 18});
        map.put(13, new int[]{8, 0});
        map.put(14, new int[]{10, 2});
        map.put(15, new int[]{10, 3});
        map.put(16, new int[]{10, 4});
        map.put(17, new int[]{10, 12});
        map.put(18, new int[]{10, 13});
    }

    public static int[] get(int key) {
        return map.get(key);
    }

    public static int size() {
        return map.size();
    }
}
