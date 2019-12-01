package com.haerul.sihandist.data.db.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.haerul.sihandist.data.db.MasterDatabase;
import com.haerul.sihandist.data.entity.Base64Data;
import com.haerul.sihandist.data.entity.Gangguan;
import com.haerul.sihandist.data.entity.GenericReferences;
import com.haerul.sihandist.data.entity.Inspeksi;
import com.haerul.sihandist.data.entity.User;

import java.util.List;

public class MasterRepository {
    private final MasterDatabase database;

    public MasterRepository(MasterDatabase database) {
        this.database = database;
    }

    public MasterRepository(Context context) {
        this.database =  MasterDatabase.getDatabase(context);
    }

    public static MasterRepository getInstance(MasterDatabase database) {
        return new MasterRepository(database);
    }
    
    //reff generic
    public List<GenericReferences> getRefByCategory(String category) {
        return database.genericReferencesDao().getGenericRefByCategory(category);
    }
    public List<GenericReferences> getRefByCategory(String category, String where) {
        return database.genericReferencesDao().getGenericRefByCategory(category, where);
    }
    public GenericReferences getRefBySID(String sid) {
        return database.genericReferencesDao().getRefBySID(sid);
    }

    public GenericReferences getRefByValue(String cat, int val) {
        return database.genericReferencesDao().getRefByValue(cat, val);
    }
    
    public User getUserBySID(String sid) {
        return database.userDao().getUserBySID(sid);
    }
    
    
    // inspeksi
    public void insertInspeksi(Inspeksi inspeksi) {
        database.inspeksiDao().insertInspeksi(inspeksi);
    }

    public void updateInspeksi(Inspeksi inspeksi) {
        database.inspeksiDao().updateInspeksi(inspeksi);
    }
    
    public Inspeksi getLastInspeksi() {
        return database.inspeksiDao().getLastInspeksi();
    }
    
    public LiveData<List<Inspeksi>> getC4a() {
        return database.inspeksiDao().getC4A(true);
    }
    
    public LiveData<List<Inspeksi>> getInspeksi() {
        return database.inspeksiDao().getC4A(false);
    }

    public LiveData<List<Inspeksi>> getAllInspeksi() {
        return database.inspeksiDao().getAllInspeksi();
    }

    public LiveData<List<Inspeksi>> getInspeksiByPP(String pp) {
        return database.inspeksiDao().getInspeksiByPP(pp, false);
    }
    
    // base64data
    public void insertBase64Data(Base64Data data) {
        database.base64DataDao().insertBase64Data(data);
    }
    
    public void updateStatus(String sid, boolean status) {
        database.base64DataDao().updateStatus(sid, status);
    }

    public Base64Data getBase64Data(String sid) {
        return database.base64DataDao().getBase64Data(sid);
    }
    
    public void updateBase64Data(Base64Data data) {
        database.base64DataDao().updateData(data);
    }
    
    // Gangguan
    public LiveData<List<Gangguan>> getGangguanByUnit(String unit) {
        return database.gangguanDao().getGangguanByUnit(unit);
    }

    public LiveData<List<Gangguan>> getGangguan() {
        return database.gangguanDao().getGangguan();
    }

    public void insertGangguan(Gangguan ganggaun) {
        database.gangguanDao().insertGangguan(ganggaun);
    }

    public void updateGangguan(Gangguan data) {
        database.gangguanDao().updateGangguan(data);
    }

    public Gangguan getLastGangguan() {
        return database.gangguanDao().getLastGangguan();
    }
}
