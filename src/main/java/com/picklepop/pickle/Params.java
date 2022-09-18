package com.picklepop.pickle;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.json.simple.JSONArray;
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
        return objectToInt(map.get(name));
    }

    public BlockPos getBlockPos(String name) {
        JSONArray array = (JSONArray) map.get(name);
        return new BlockPos(objectToInt(array.get(0)), objectToInt(array.get(1)), objectToInt(array.get(2)));
    }

    public Vec3 getVec3(String name) {
        JSONArray array = (JSONArray) map.get(name);
        return new Vec3(objectToInt(array.get(0)), objectToInt(array.get(1)), objectToInt(array.get(2)));
    }

    private static int objectToInt(Object val) {
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

    public String toString() {
        return this.map.toString();
    }
}
