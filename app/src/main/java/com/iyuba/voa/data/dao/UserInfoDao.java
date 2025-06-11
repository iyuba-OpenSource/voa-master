package com.iyuba.voa.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.iyuba.voa.data.entity.UserInfo;

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
public interface UserInfoDao {

    @Query("SELECT * FROM UserInfo where uid = (:id)")
    UserInfo getUserById(String id);

    @Query("SELECT * FROM UserInfo where username = (:username)")
    UserInfo getAllByCode(String username);

    @Query("SELECT * FROM UserInfo")
    List<UserInfo> getAll();

    @Query("DELETE FROM UserInfo")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(UserInfo... dataListBeans);

    @Delete
    void delete(UserInfo... dataListBean);

    @Update
    void update(UserInfo... dataListBeans);

    @Update(entity = UserInfo.class)
    void updateVip(UserInfo... userInfo);

}
