package com.haerul.sihandist.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "gangguan")
public class Gangguan implements Serializable {
    @PrimaryKey @NonNull
    public String g_sid;
    public String g_uid;
    public String g_date;
    public String g_unit;
    public String g_penyulang;
    public String g_indikasi;
    public String g_kelompok;
    public String g_r;
    public String g_s;
    public String g_t;
    public String g_n;
    public String g_sebab;
    public String g_lat;
    public String g_lon;
    public String g_foto_1;
    public String g_foto_2;
    public String g_tl;
    public String g_date_tl;
    public String g_status;
    public String g_foto_tl;
    public String g_keterangan;
    public String date_created;
    public String date_modified;
    public String post_date;
    public String post_by;
    public boolean post_status;
}
