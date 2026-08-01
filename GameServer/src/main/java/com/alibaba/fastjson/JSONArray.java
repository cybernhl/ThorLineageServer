package com.alibaba.fastjson;

import java.util.ArrayList;
import java.util.List;

/**
 * A minimal, secure replacement for com.alibaba.fastjson.JSONArray.
 * Used to maintain source compatibility for HWID generation without the security risks of Fastjson.
 */
public class JSONArray {
    private final List<Object> list = new ArrayList<>();

    public void add(Object value) {
        list.add(value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            JSONObject.appendValue(sb, list.get(i));
        }
        sb.append("]");
        return sb.toString();
    }
}
