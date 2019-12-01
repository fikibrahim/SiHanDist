package com.haerul.sihandist.data.db;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.haerul.sihandist.data.db.dao.UserDao;
import com.haerul.sihandist.data.entity.User;
import com.haerul.sihandist.utils.Constants;

@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class LogDatabase extends RoomDatabase {

    abstract UserDao userDao();
    
    private static LogDatabase INSTANCE;
    public  static LogDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (LogDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context, LogDatabase.class, Constants.TEMP_DB)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
