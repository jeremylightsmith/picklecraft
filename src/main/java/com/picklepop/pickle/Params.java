package com.picklepop.pickle;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class Params {
    private final Map map;

    public Params(Map map) {
        this.map = map;
    }

    public String getString(String name) {
        Object val = map.get(name);
        return val instanceof String ? (String) val : val.toString();
    }

    public int getInt(String name) {
        Object val = map.get(name);
        if (val instanceof Number) {
            return ((Number) val).intValue();
        } else {
            throw new IllegalArgumentException("Can't parse " + val.toString());
        }
    }

    public static Params parse(String string) throws ParseException {
        return new Params((JSONObject) new JSONParser().parse(string));
    }

    public static Params parse(InputStream stream) throws IOException {
        InputStreamReader isReader = new InputStreamReader(stream);
        BufferedReader reader = new BufferedReader(isReader);
        StringBuilder sb = new StringBuilder();
        String str;
        while ((str = reader.readLine()) != null) {
            sb.append(str);
        }
        try {
            return parse(sb.toString());
        } catch (ParseException e) {
            throw new IOException(e);
        }
    }
}
