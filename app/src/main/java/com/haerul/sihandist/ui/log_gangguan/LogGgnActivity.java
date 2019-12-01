package com.haerul.sihandist.ui.log_gangguan;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.haerul.sihandist.R;
import com.haerul.sihandist.base.BaseActivity;
import com.haerul.sihandist.data.api.ConnectionServer;
import com.haerul.sihandist.data.db.repository.MasterRepository;
import com.haerul.sihandist.data.entity.Gangguan;
import com.haerul.sihandist.databinding.ActivityLogGgnBinding;
import com.haerul.sihandist.utils.Util;

import javax.inject.Inject;

public class LogGgnActivity extends BaseActivity<ActivityLogGgnBinding, LogGgnViewModel> implements LogGgnViewModel.Navigator {
    
    @Inject
    MasterRepository repository;
    @Inject
    ConnectionServer server;
    private ActivityLogGgnBinding binding;
    private LogGgnViewModel viewModel;
    
    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_log_ggn;
    }

    @Override
    public LogGgnViewModel getViewModel() {
        return viewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewDataBinding();
        viewModel = ViewModelProviders.of(this, new LogGgnViewModel.ModelFactory(this, server, repository)).get(LogGgnViewModel.class);
        viewModel.setNavigator(this);
        viewModel.getAllGangguan().observe(this, data -> {
            if (data.size() > 0) {
                binding.emptyView.setVisibility(View.GONE);
                binding.recyclerView.setAdapter(new LogGgnAdapter(data, viewModel));
            } else {
                binding.emptyView.setVisibility(View.VISIBLE);
            }
        });
        
        setupToolbar();
    }

    @Override
    public void onItemSend(Gangguan data) {
        if (!data.post_status) {
            viewModel.postGangguan(data);
            viewModel.postBase64Data(repository.getBase64Data(data.g_foto_1));
            if (data.g_foto_2 != null) { viewModel.postBase64Data(repository.getBase64Data(data.g_foto_2)); }
            viewModel.postBase64Data(repository.getBase64Data(data.g_foto_tl));
        } else {
            Toast.makeText(this, "No data updated!", Toast.LENGTH_SHORT).show();
        }
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
