package com.haerul.sihandist.ui.inspeksi;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.haerul.sihandist.base.BaseViewModel;
import com.haerul.sihandist.data.api.ConnectionServer;
import com.haerul.sihandist.data.db.repository.MasterRepository;
import com.haerul.sihandist.data.entity.GenericReferences;
import com.haerul.sihandist.data.entity.Inspeksi;
import com.haerul.sihandist.utils.Constants;
import com.haerul.sihandist.utils.Util;

import java.util.List;

public class InspeksiViewModel extends BaseViewModel<InspeksiViewModel.InspeksiNavigator> {
    
    public boolean isTL;
    
    public InspeksiViewModel(Context context, ConnectionServer connectionServer, MasterRepository repository) {
        super(context, connectionServer, repository);
        isTL = repository.getRefBySID(Util.getStringPreference(context, Constants.USER_ROLE_SID)).ref_value == 7;
    }
    
    public String userRole () {
        return Util.getStringPreference(getContext(), Constants.USER_ROLE_SID);
    }

    public LiveData<List<Inspeksi>> getInspeksiByPP(String pp) {
        return getRepository().getInspeksiByPP(pp);
    }
    
    public GenericReferences getRef(String sid) { 
        return getRepository().getRefBySID(sid);
    }
    
    public void onItemClick(Inspeksi data) {
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
            return (T) new InspeksiViewModel(context, server, repository);
        }
    }

    public interface InspeksiNavigator {
        void onItemClick(Inspeksi data);
    }
}
