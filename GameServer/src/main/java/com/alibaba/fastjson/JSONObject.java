package com.alibaba.fastjson;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A minimal, secure replacement for com.alibaba.fastjson.JSONObject.
 * Used to maintain source compatibility for HWID generation without the security risks of Fastjson.
 */
public class JSONObject {
    private final Map<String, Object> map = new LinkedHashMap<>();

    public void put(String key, Object value) {
        map.put(key, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(entry.getKey()).append("\":");
            appendValue(sb, entry.getValue());
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    protected static void appendValue(StringBuilder sb, Object val) {
        if (val == null) {
            sb.append("null");
        } else if (val instanceof String) {
            sb.append("\"").append(val.toString().replace("\"", "\\\"")).append("\"");
        } else if (val instanceof JSONObject || val instanceof JSONArray) {
            sb.append(val.toString());
        } else if (val instanceof Number || val instanceof Boolean) {
            sb.append(val.toString());
        } else {
            // Default to string representation for other types
            sb.append("\"").append(val.toString().replace("\"", "\\\"")).append("\"");
        }
    }
}
