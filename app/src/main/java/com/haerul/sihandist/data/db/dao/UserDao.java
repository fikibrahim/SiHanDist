package com.haerul.sihandist.data.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.haerul.sihandist.data.entity.User;

@Dao
public interface UserDao {
    
    @Query("select * from users where user_sid=:sid")
    User getUserBySID(String sid);
    
}
