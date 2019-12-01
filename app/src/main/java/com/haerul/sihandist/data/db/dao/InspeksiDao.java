package com.haerul.sihandist.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.haerul.sihandist.data.entity.Inspeksi;

import java.util.List;

@Dao
public interface InspeksiDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertInspeksi(Inspeksi inspeksi);

    @Query("select * from inspeksi order by tanggal_inspeksi desc")
    LiveData<List<Inspeksi>> getAllInspeksi();

    @Query("select * from inspeksi where is_c4a =:c4a order by tanggal_inspeksi desc")
    LiveData<List<Inspeksi>> getC4A(boolean c4a);
    
    @Query("select * from inspeksi as a " +
            "left join generic_references as b " +
            "on a.jenis_temuan_sid = b.ref_sid " +
            "where b.ref_description = :pp " +
            "and a.is_c4a = :c4a " +
            "order by a.tanggal_inspeksi desc")
    LiveData<List<Inspeksi>> getInspeksiByPP(String pp, boolean c4a);

    @Update
    void updateInspeksi(Inspeksi inspeksi);

    @Query("select * from inspeksi order by post_date desc LIMIT 1")
    Inspeksi getLastInspeksi();
}
