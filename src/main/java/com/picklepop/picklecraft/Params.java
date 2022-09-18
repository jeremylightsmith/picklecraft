package com.picklepop.picklecraft;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.*;

public class Params {
    private final JsonObject json;

    public Params(JsonObject json) {
        this.json = json;
    }

    public String getString(String name) {
        return json.getString(name);
    }

    public int getInt(String name) {
        return json.getInt(name);
    }

    public BlockPos getBlockPos(String name) {
        JsonArray array = json.getJsonArray(name);
        return new BlockPos(array.getInt(0), array.getInt(1), array.getInt(2));
    }

    public Vec3 getVec3(String name) {
        JsonArray array = json.getJsonArray(name);
        return new Vec3(array.getInt(0), array.getInt(1), array.getInt(2));
    }

    public static Params parse(String string) {
        return new Params(Json.createReader(new StringReader(string)).readObject());
    }

    public static Params parse(InputStream stream) throws IOException {
        InputStreamReader isReader = new InputStreamReader(stream);
        BufferedReader reader = new BufferedReader(isReader);
        StringBuilder sb = new StringBuilder();
        String str;
        while ((str = reader.readLine()) != null) {
            sb.append(str);
        }
        return parse(sb.toString());
    }

    public String toString() {
        return this.json.toString();
    }
}
