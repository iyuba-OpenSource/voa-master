package com.iyuba.voa.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;

import com.iyuba.voa.data.entity.VoaText;
import com.iyuba.voa.data.entity.converter.WordListConverter;

import java.util.List;


/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2020/9/29
 * 邮箱：jxfengmtx@gmail.com
 */
@Dao
public interface VoaTextDao {

    @Query("SELECT * FROM VoaText where voaId = (:voaId) and `index` = (:index) and uid = (:uid)")
    VoaText getVoaTextById(String voaId, int index, String uid);

    @Query("SELECT * FROM VoaText where voaId = (:voaId)")
    List<VoaText> getVoaTextByVoaId(String voaId);


    @Query("SELECT * FROM VoaText")
    List<VoaText> getAll();

    @Query("SELECT COUNT(*) FROM VoaText where voaId = :voaid")
    int getCountByVoaid(String voaid);

    @Query("SELECT COUNT(*) FROM VoaText where voaId = :voaid and audioPath not NULL")
    int getTestingCountByVoaid(String voaid);

    @Query("SELECT COUNT(*) FROM VoaText")
    int getCount();


    @Query("DELETE FROM VoaText")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(VoaText... dataListBeans);

    @Delete
    void delete(VoaText... dataListBean);

    @Update
    void update(VoaText... dataListBeans);

}
