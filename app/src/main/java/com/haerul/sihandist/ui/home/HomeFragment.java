package com.haerul.sihandist.ui.home;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.haerul.sihandist.R;
import com.haerul.sihandist.base.BaseFragment;
import com.haerul.sihandist.data.api.ConnectionServer;
import com.haerul.sihandist.data.db.repository.MasterRepository;
import com.haerul.sihandist.databinding.FragmentHomeBinding;
import com.haerul.sihandist.utils.Constants;
import com.haerul.sihandist.utils.Util;

import javax.inject.Inject;

public class HomeFragment extends BaseFragment<FragmentHomeBinding, HomeViewModel> {

    @Inject
    ConnectionServer server;
    @Inject
    MasterRepository repository;
    
    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;

    @Override
    public int getBindingVariable() {
        return com.haerul.sihandist.BR._all;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    public HomeViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = getViewDataBinding();
        viewModel = ViewModelProviders.of(this, new HomeViewModel.ModelFactory(getBaseActivity(), server, repository)).get(HomeViewModel.class);
        binding.name.setText(Util.getStringPreference(getBaseActivity(), Constants.USER_NAME));
    }
}
