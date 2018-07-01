package com.example.admin.bluetoothguard.guards;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by admin on 2018/7/2.
 */

public class MyResult {
    private Map<String, Double> doubleMap;
    private Map<String, Integer> intMap;
    private Map<String, Boolean> booleanMap;

    public MyResult(){
        doubleMap = new TreeMap<String,Double>();
        intMap = new TreeMap<String,Integer>();
        booleanMap = new TreeMap<String,Boolean>();
    }

    public Double getDouble(String s){
        return doubleMap.get(s);
    }
    public Integer getInt(String s){
        return intMap.get(s);
    }
    public Boolean getBoolean(String s){
        return booleanMap.get(s);
    }

    public void putDouble(String s, double d){
        doubleMap.put(s, d);
    }
    public void putInt(String s, int d){
        intMap.put(s, d);
    }
    public void putBoolean(String s, boolean d){
        booleanMap.put(s, d);
    }

}
