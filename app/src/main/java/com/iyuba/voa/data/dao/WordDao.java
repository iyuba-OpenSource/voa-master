package com.iyuba.voa.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.iyuba.voa.data.entity.XmlWord;

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
public interface WordDao {


    @Query("SELECT * FROM XmlWord where `key` in (:word)")
    XmlWord getAllByTitle(String word);

    @Query("SELECT * FROM XmlWord")
    List<XmlWord> getAll();

    @Query("SELECT * FROM XmlWord ORDER BY createDate DESC LIMIT (:page),(:pageNum)")  //不加lower会不工作
    List<XmlWord> getAllByDate( int page, int pageNum);


    @Query("SELECT * FROM XmlWord ORDER BY `key` LIMIT (:page),(:pageNum)")  //不加lower会不工作
    List<XmlWord> getAllByLetter(int page, int pageNum);


    @Query("DELETE FROM XmlWord")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(XmlWord... dataListBeans);

    @Delete
    void delete(XmlWord... dataListBean);

    @Update
    void update(XmlWord... dataListBeans);

}
