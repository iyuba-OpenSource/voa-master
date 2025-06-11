package com.iyuba.voa.data.entity.converter;

import android.text.TextUtils;

import androidx.room.TypeConverter;

import com.iyuba.voa.data.entity.VoaScore;
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
public class WordListConverter {
    @TypeConverter
    public String objToString(List<VoaScore.Words> list) {
        return list != null ? JsonUtil.obj2Json(list) : null;
    }

    @TypeConverter
    public List<VoaScore.Words> strToObj(String json) {
        return JsonUtil.json2List(json, VoaScore.Words.class);
    }
}
