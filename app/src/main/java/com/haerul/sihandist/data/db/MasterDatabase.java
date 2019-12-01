package com.haerul.sihandist.data.db;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.haerul.sihandist.data.db.dao.Base64DataDao;
import com.haerul.sihandist.data.db.dao.GangguanDao;
import com.haerul.sihandist.data.db.dao.GenericCategoryDao;
import com.haerul.sihandist.data.db.dao.GenericReferencesDao;
import com.haerul.sihandist.data.db.dao.InspeksiDao;
import com.haerul.sihandist.data.db.dao.UserDao;
import com.haerul.sihandist.data.entity.Base64Data;
import com.haerul.sihandist.data.entity.Gangguan;
import com.haerul.sihandist.data.entity.GenericCategory;
import com.haerul.sihandist.data.entity.GenericReferences;
import com.haerul.sihandist.data.entity.Inspeksi;
import com.haerul.sihandist.data.entity.User;
import com.haerul.sihandist.utils.Constants;

@Database(entities = {
        User.class, 
        Inspeksi.class,
        Gangguan.class,
        GenericCategory.class,
        GenericReferences.class,
        Base64Data.class
}, version = 1, exportSchema = false)
public abstract class MasterDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract InspeksiDao inspeksiDao(); 
    public abstract GangguanDao gangguanDao(); 
    public abstract GenericCategoryDao genericCategoryDao();
    public abstract GenericReferencesDao genericReferencesDao();
    public abstract Base64DataDao base64DataDao();
    
    private static MasterDatabase INSTANCE;
    public  static MasterDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (MasterDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context, MasterDatabase.class, Constants.MASTER_DB)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
