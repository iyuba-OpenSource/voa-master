package com.iyuba.voa.data.entity.converter;

import androidx.room.TypeConverter;

import com.iyuba.voa.utils.JsonUtil;

import java.util.List;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/04/23
 * 邮箱：jxfengmtx@gmail.com
 */
public class StringListConverter {
    @TypeConverter
    public String objToString(List<String> list) {
        return JsonUtil.obj2Json(list);
    }

    @TypeConverter
    public List<String> strToObj(String json) {
        return JsonUtil.jsonToList(json);
    }
}
