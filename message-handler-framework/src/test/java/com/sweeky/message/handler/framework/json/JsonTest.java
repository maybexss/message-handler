package com.sweeky.message.handler.framework.json;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.Map;
import java.util.TreeMap;

public class JsonTest {
    public static void main(String[] args) {
        Map<String, Object> map = new TreeMap<>();

        map.put("one", 1);
        map.put("two", "2");
        map.put("three", null);
        map.put("four", '3');

        byte[] data = new byte[10];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) i;
        }
        map.put("five", data);

        String str = JSONObject.toJSONString(map, SerializerFeature.WRITE_MAP_NULL_FEATURES);
        String str2 = JSONObject.toJSONString(map, SerializerFeature.WriteNullStringAsEmpty);
        String str3 = JSONObject.toJSONString(map, SerializerFeature.WriteMapNullValue);

        System.out.println(str);
        System.out.println(str2);
        System.out.println(str3);
    }
}