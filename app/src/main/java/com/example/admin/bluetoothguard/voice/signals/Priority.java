package com.example.admin.bluetoothguard.voice.signals;

/**
 * Created by admin on 2018/7/4.
 */

public class Priority {
    private final static int mostImportant = 0;
    private final static int veryImportant = 0;
    private final static int important = 3;
    private final static int normal = 5;  // 大部分信号处于这个优先级
    private final static int leastImportant = 10;

    public static int getMostImportant(){
        return mostImportant;
    }
    public static int getVeryImportant(){
        return veryImportant;
    }
    public static int getImportant(){
        return important;
    }
    public static int getNormal(){
        return normal;
    }
    public static int getLeastImportant(){
        return leastImportant;
    }
}
