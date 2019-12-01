package com.haerul.sihandist.ui.log_inspeksi;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.haerul.sihandist.base.BaseViewModel;
import com.haerul.sihandist.data.api.ConnectionServer;
import com.haerul.sihandist.data.db.repository.MasterRepository;
import com.haerul.sihandist.data.entity.Base64Data;
import com.haerul.sihandist.data.entity.GenericReferences;
import com.haerul.sihandist.data.entity.Inspeksi;
import com.haerul.sihandist.utils.Constants;
import com.haerul.sihandist.utils.Util;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogInsViewModel extends BaseViewModel<LogInsViewModel.Navigator> {
    
    public LogInsViewModel(Context context, ConnectionServer connectionServer, MasterRepository repository) {
        super(context, connectionServer, repository);
    }

    public LiveData<List<Inspeksi>> getAllInspeksi() {
        return getRepository().getAllInspeksi();
    }
    
    public GenericReferences getRef(String sid) { 
        return getRepository().getRefBySID(sid);
    }
    
    public void onItemSend(Inspeksi data) {
        getNavigator().onItemSend(data);
    }

    public String dateTimeFormatter(String dateTime) {
        return Util.dateFormatter2(dateTime, Constants.DATE_ONLY_FORMAT + " " + Constants.TIME_ONLY_FORMAT);
    }

    public String dateFormatter(String inputString, String inputString2) {
        if (!Util.isNullOrEmpty(inputString)) {
            return Util.dateToTimeFormat(inputString);
        } else {
            return Util.dateToTimeFormat(inputString2);
        }
    }

    public void postInspeksi(Inspeksi inspeksi) {
        getNavigator().showProgress();
        JsonObject object = new JsonParser().parse(new Gson().toJson(inspeksi)).getAsJsonObject();
        getConnectionServer().postInspeksi(Util.getToken(getContext()), object)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().get(Constants.STATUS).getAsBoolean()) {
                                inspeksi.post_status = true;
                                inspeksi.inspeksi_uid = response.body().get(Constants.DATA).getAsJsonObject().get(Constants.UID).getAsString();
                                getRepository().updateInspeksi(inspeksi);
                                getNavigator().result(true, "Successfully inserted!");
                            }
                        }
                        getNavigator().hideProgress();
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        getNavigator().hideProgress();
                        inspeksi.post_status = false;
                        inspeksi.inspeksi_uid = "INS-000000-00000";
                        getRepository().updateInspeksi(inspeksi);
                        getNavigator().result(false, t.getMessage());
                    }
                });
    }

    public void postBase64Data(Base64Data data) {
        JsonObject object = new JsonParser().parse(new Gson().toJson(data)).getAsJsonObject();
        getConnectionServer().postBase64Data(Util.getToken(getContext()), object).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().get(Constants.STATUS).getAsBoolean()) {
                        getRepository().updateStatus(data.data_sid, true);
                    }
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) { }
        });
    }
    
    public static class ModelFactory implements ViewModelProvider.Factory {
        private Context context;
        private ConnectionServer server;
        private MasterRepository repository;
        public ModelFactory(Context context, ConnectionServer server, MasterRepository repository) {
            this.context = context;
            this.server = server;
            this.repository = repository;
        }
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new LogInsViewModel(context, server, repository);
        }
    }

    public interface Navigator {
        void onItemSend(Inspeksi data);
        void showProgress();
        void hideProgress();
        void result(boolean status, String message);
    }
}
