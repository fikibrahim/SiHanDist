package com.haerul.sihandist.ui.gangguan;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.haerul.sihandist.base.BaseViewModel;
import com.haerul.sihandist.data.api.ConnectionServer;
import com.haerul.sihandist.data.db.repository.MasterRepository;
import com.haerul.sihandist.data.entity.Gangguan;
import com.haerul.sihandist.data.entity.GenericReferences;
import com.haerul.sihandist.utils.Constants;
import com.haerul.sihandist.utils.Util;

import java.util.List;

public class GangguanViewModel extends BaseViewModel<GangguanViewModel.Navigator> {
    
    public GangguanViewModel(Context context, ConnectionServer connectionServer, MasterRepository repository) {
        super(context, connectionServer, repository);
    }
    
    public String userRole () {
        return Util.getStringPreference(getContext(), Constants.USER_ROLE_SID);
    }

    public LiveData<List<Gangguan>> getGangguanByUnit(String unit) {
        return getRepository().getGangguanByUnit(unit);
    }
    
    public GenericReferences getRef(String sid) { 
        return getRepository().getRefBySID(sid);
    }

    public String getRefName(String sid) {
        return getRepository().getRefBySID(sid).ref_description.equals("is_user") ? getRepository().getRefBySID(sid).ref_name : getRepository().getRefBySID(sid).ref_description;
    }
    
    public void onItemClick(Gangguan data) {
        getNavigator().onItemClick(data);
    }

    public String dateTimeFormatter(String dateTime) {
        return Util.dateFormatter2(dateTime, Constants.DATE_ONLY_FORMAT + " " + Constants.TIME_ONLY_FORMAT);
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
            return (T) new GangguanViewModel(context, server, repository);
        }
    }

    public interface Navigator {
        void onItemClick(Gangguan data);
    }
}
