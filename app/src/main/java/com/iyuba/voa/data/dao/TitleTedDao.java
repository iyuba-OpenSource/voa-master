package com.iyuba.voa.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.iyuba.voa.data.entity.TitleTed;

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
public interface TitleTedDao {

    @Query("SELECT * FROM TitleTed where voaId = (:id) and uids like '%A' || :uid || 'A%' ")
    TitleTed getTitleTedById(String id, String uid);

    // ||相当于+号
    @Transaction
    @Query("SELECT * FROM TitleTed where voaId = (:id) and hotFlg like '%' || :flag || '%' and uids like '%A' || :uid || 'A%' ")
    TitleTed getTitleTedByIdFlag(String id, String flag, String uid);

    @Query("SELECT * FROM TitleTed where title in (:title)")
    TitleTed getAllByTitle(String title);

    @Query("SELECT * FROM TitleTed")
    List<TitleTed> getAll();

    // ||相当于+号
    @Transaction
    @Query("SELECT * FROM TitleTed where hotFlg like '%' || :flag || '%' and uids like '%A' || :uid || 'A%'")
    List<TitleTed> getAllByFlag(String flag, String uid);


    @Query("DELETE FROM TitleTed ")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(TitleTed... dataListBeans);

    //根据主键id删
    @Delete
    void delete(TitleTed... dataListBean);

    @Update
    void update(TitleTed... dataListBeans);

}
