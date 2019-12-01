package com.haerul.sihandist.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "inspeksi")
public class Inspeksi implements Serializable {
    @PrimaryKey
    @NonNull
    public String inspeksi_sid;
    public String inspeksi_uid;
    public String rayon_sid;
    public String penyulang_sid;
    public String jenis_temuan_sid;
    public String tingkat_emergency_sid;
    public String pemadaman_sid;
    public String jenis_wo_sid;
    public String status_tl_sid;
    public String lokasi_inspeksi_y;
    public String lokasi_inspeksi_x;
    public String lokasi_tl_y;
    public String lokasi_tl_x;
    public String tanggal_inspeksi;
    public String tanggal_tl;
    public String keterangan;
    public String foto_inspeksi;
    public String foto_tl;
    public boolean is_approve;
    public String post_by;
    public String post_date;
    public boolean post_status;
    public String wo_date;
    public boolean is_receive;
    public boolean is_c4a;
}
