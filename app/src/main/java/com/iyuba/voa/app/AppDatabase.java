package com.iyuba.voa.app;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.iyuba.voa.data.dao.TitleTedDao;
import com.iyuba.voa.data.dao.UserInfoDao;
import com.iyuba.voa.data.dao.VoaTextDao;
import com.iyuba.voa.data.dao.WordDao;
import com.iyuba.voa.data.entity.TitleTed;
import com.iyuba.voa.data.entity.UserInfo;
import com.iyuba.voa.data.entity.VoaText;
import com.iyuba.voa.data.entity.XmlWord;
import com.iyuba.voa.data.source.LocalDataSourceImpl;

import java.io.File;

import me.goldze.mvvmhabit.utils.SDCardUtils;
import me.goldze.mvvmhabit.utils.Utils;


/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2020/9/9
 * 邮箱：jxfengmtx@gmail.com
 * <p>
 * room管理类
 * <p>
 * version 3 用户表
 */
@Database(entities = {
        UserInfo.class,
        TitleTed.class,
        XmlWord.class,
        VoaText.class,
}, version = 7, exportSchema = true,
        autoMigrations = {@AutoMigration(from = 6, to = 7)}
)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase DB;  //创建单例

    public static AppDatabase getInstance() {
        if (DB == null) {
            synchronized (LocalDataSourceImpl.class) {
                if (DB == null) {
                    String path = SDCardUtils.getFileDataPath();
                    DB = Room.databaseBuilder(Utils.getContext(),
                                    AppDatabase.class, path + File.separator + "appdata.db")
                            .fallbackToDestructiveMigration()    //版本更改找不到迁移规则时，直接删除之前的数据库，重新再创建新的数据库
                            .allowMainThreadQueries()   //允许在主线程查询数据
                            .build();
                }
            }
        }
        return DB;
    }

    public abstract UserInfoDao userInfoDao();

    public abstract TitleTedDao titleTedDao();

    public abstract WordDao wordDao();

    public abstract VoaTextDao voaTextDao();

}

