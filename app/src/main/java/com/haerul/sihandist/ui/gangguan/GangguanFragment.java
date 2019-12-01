package com.haerul.sihandist.ui.gangguan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.haerul.sihandist.R;
import com.haerul.sihandist.base.BaseFragment;
import com.haerul.sihandist.base.StartActivity;
import com.haerul.sihandist.data.api.ConnectionServer;
import com.haerul.sihandist.data.db.repository.MasterRepository;
import com.haerul.sihandist.data.entity.Gangguan;
import com.haerul.sihandist.databinding.FragmentGangguanBinding;
import com.haerul.sihandist.ui.gangguan.tindak_lanjut.GangguanTLActivity;
import com.haerul.sihandist.utils.Constants;

import javax.inject.Inject;

public class GangguanFragment extends BaseFragment<FragmentGangguanBinding, GangguanViewModel> implements GangguanViewModel.Navigator {

    @Inject
    ConnectionServer server;
    @Inject
    MasterRepository repository;

    public FragmentGangguanBinding binding;
    public GangguanViewModel viewModel;

    public static GangguanFragment newInstance(String ref, String tab, int tabPos) { 
        Bundle args = new Bundle();
        args.putString(Constants.ULP, ref);
        args.putString(Constants.TAB_TITLE, tab);
        args.putInt(Constants.TAB_POSITION, tabPos);
        GangguanFragment fragment = new GangguanFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getBindingVariable() {
        return com.haerul.sihandist.BR._all;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_gangguan;
    }

    @Override
    public GangguanViewModel getViewModel() {
        return viewModel;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = getViewDataBinding();
        viewModel = ViewModelProviders.of(this, new GangguanViewModel.ModelFactory(getBaseActivity(), server, repository)).get(GangguanViewModel.class);
        viewModel.setNavigator(this);
        
        getData();
        binding.swipe.setOnRefreshListener(this::getData);
    }

    private void getData() {
        if (getArguments() != null) {
            viewModel.getGangguanByUnit(getArguments().getString(Constants.ULP)).observe(this, data -> {
                BaseGangguanFragment.tabLayout.getTabAt(getArguments().getInt(Constants.TAB_POSITION)).setText(getArguments().getString(Constants.TAB_TITLE) + " (" + data.size() + ")");
                if (data.size() > 0) {
                    binding.emptyView.setVisibility(View.GONE);
                    binding.recyclerView.setAdapter(new GangguanAdapter(data, viewModel));
                } else {
                    binding.emptyView.setVisibility(View.VISIBLE);
                }
                binding.swipe.setRefreshing(false);
            });
        }
    }

    @Override
    public void onItemClick(Gangguan data) {
        Intent intent = new Intent(getBaseActivity(), GangguanTLActivity.class);
        intent.putExtra(Constants.EXTRA_DATA, data);
        new StartActivity(getBaseActivity(), binding.progressBar, intent).execute();
    }
}
