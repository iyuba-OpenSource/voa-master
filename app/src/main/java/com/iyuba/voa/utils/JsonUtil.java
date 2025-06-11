package com.iyuba.voa.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Lxt user on 2018/7/12.
 * good good study ，day day up
 */
public class JsonUtil {

    /**
     * 禁止调用无参构造
     *
     * @throws Exception
     */
    private JsonUtil() throws Exception {
        throw new Exception("Error...");
    }

    private static final Gson GSON = new GsonBuilder()
            .setDateFormat(DateUtil.ALL)
            .create();

    private static final JsonParser JSON_PARSER = new JsonParser();

    private static final Type MAP_TYPE = new TypeToken<Map<String, ?>>() {
    }.getType();

    private static final Type JSON_OBJECT_TYPE = new TypeToken<List<JsonObject>>() {
    }.getType();

    public static Gson getInstance() {
        return GSON;
    }

    /**
     * 将对象转换成Json字符串
     *
     * @param src
     * @return
     */
    public static String obj2Json(Object src) {
        return GSON.toJson(src);
    }

    /**
     * 将Json转换成对象
     *
     * @param json
     * @param classOfT
     * @return
     */
    public static <T> T json2Entity(String json, Class<T> classOfT) {
        return GSON.fromJson(json, classOfT);
    }

    /**
     * 将Json转化成Map
     *
     * @param json
     * @return
     */
    public static Map<String, ?> json2Map(String json) {
        return GSON.fromJson(json, MAP_TYPE);
    }

    /**
     * 将Json字符串转化成List
     *
     * @param json
     * @param typeOfT
     * @return
     */
    public static <T> List<T> json2List(String json, Class<T> typeOfT) {
        if (TextUtils.isEmpty(json) || json.equals("null")) {
            return null;
        }
        List<JsonObject> jsonObjectList = GSON.fromJson(json, JSON_OBJECT_TYPE);
        List<T> list = new ArrayList<>();
        for (JsonObject jsonObject : jsonObjectList) {
            list.add(json2Entity(jsonObject.toString(), typeOfT));
        }
        return list;
    }

    /**
     * Json字符串转JsonObject
     *
     * @param json
     * @return
     */
    public static JsonObject json2JsonObject(String json) {
        return JSON_PARSER.parse(json).getAsJsonObject();
    }

    /**
     * 将JsonObject转换成Json字符串
     *
     * @param jsonObject
     * @return
     */
    public static String jsonobject2Json(JsonObject jsonObject) {
        return jsonObject.toString();
    }

    /**
     * 将Json字符串转化成List  string类型用这个
     *
     * @param json
     * @return
     */
    public static <T> List<T> jsonToList(String json) {
        Type LIST_TYPE = new TypeToken<List<T>>() {
        }.getType();
        return GSON.fromJson(json, LIST_TYPE);
    }

}