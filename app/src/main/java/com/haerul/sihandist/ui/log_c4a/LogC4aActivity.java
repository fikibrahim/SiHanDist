package com.haerul.sihandist.ui.log_c4a;

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
import com.haerul.sihandist.databinding.ActivityLogC4aBinding;
import com.haerul.sihandist.utils.Util;

import javax.inject.Inject;

public class LogC4aActivity extends BaseActivity<ActivityLogC4aBinding, LogC4aViewModel> implements LogC4aViewModel.Navigator {
    
    @Inject
    MasterRepository repository;
    @Inject
    ConnectionServer server;
    private ActivityLogC4aBinding binding;
    private LogC4aViewModel viewModel;
    
    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_log_c4a;
    }

    @Override
    public LogC4aViewModel getViewModel() {
        return viewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewDataBinding();
        viewModel = ViewModelProviders.of(this, new LogC4aViewModel.ModelFactory(this, server, repository)).get(LogC4aViewModel.class);
        viewModel.setNavigator(this);
        viewModel.getAllC4a().observe(this, data -> {
            if (data.size() > 0) {
                binding.emptyView.setVisibility(View.GONE);
                binding.recyclerView.setAdapter(new LogC4aAdapter(data, viewModel));
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
