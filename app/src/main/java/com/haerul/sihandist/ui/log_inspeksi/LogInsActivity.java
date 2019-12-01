package com.haerul.sihandist.ui.log_inspeksi;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.haerul.sihandist.R;
import com.haerul.sihandist.base.BaseActivity;
import com.haerul.sihandist.data.api.ConnectionServer;
import com.haerul.sihandist.data.db.repository.MasterRepository;
import com.haerul.sihandist.data.entity.Inspeksi;
import com.haerul.sihandist.databinding.FragmentLogInsBinding;
import com.haerul.sihandist.utils.Util;

import javax.inject.Inject;

public class LogInsActivity extends BaseActivity<FragmentLogInsBinding, LogInsViewModel> implements LogInsViewModel.Navigator {
    
    @Inject
    MasterRepository repository;
    @Inject
    ConnectionServer server;
    private FragmentLogInsBinding binding;
    private LogInsViewModel viewModel;
    
    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_log_ins;
    }

    @Override
    public LogInsViewModel getViewModel() {
        return viewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewDataBinding();
        viewModel = ViewModelProviders.of(this, new LogInsViewModel.ModelFactory(this, server, repository)).get(LogInsViewModel.class);
        viewModel.setNavigator(this);
        viewModel.getAllInspeksi().observe(this, data -> {
            if (data.size() > 0) {
                binding.emptyView.setVisibility(View.GONE);
                binding.recyclerView.setAdapter(new LogInsAdapter(data, viewModel));
            } else {
                binding.emptyView.setVisibility(View.VISIBLE);
            }
        });
        
        setupToolbar();
    }

    @Override
    public void onItemSend(Inspeksi data) {
        viewModel.postInspeksi(data);
        viewModel.postBase64Data(repository.getBase64Data(data.foto_inspeksi));
    }

    @Override
    public void result(boolean status, String message) {
        if (status) {
            Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
        } else {
            Util.showDialog(this, "Network Failure!", message);
        }
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
}
