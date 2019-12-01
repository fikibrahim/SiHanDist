package com.haerul.sihandist.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.haerul.sihandist.data.entity.Gangguan;

import java.util.List;

@Dao
public interface GangguanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGangguan(Gangguan gangguan);
    
    @Query("select a.* from gangguan as a " +
            "left join generic_references as b " +
            "on a.g_unit = b.ref_sid " +
            "where b.ref_sid = :unit order by a.post_date desc")
    LiveData<List<Gangguan>> getGangguanByUnit(String unit);

    @Query("select a.* from gangguan as a order by a.g_uid desc")
    LiveData<List<Gangguan>> getGangguan();
    
    @Update
    void updateGangguan(Gangguan gangguan);

    @Query("select * from gangguan order by date_created desc LIMIT 1")
    Gangguan getLastGangguan();
}
