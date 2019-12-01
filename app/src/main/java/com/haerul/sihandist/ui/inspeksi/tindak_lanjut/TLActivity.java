package com.haerul.sihandist.ui.inspeksi.tindak_lanjut;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.haerul.sihandist.R;
import com.haerul.sihandist.base.BaseActivity;
import com.haerul.sihandist.base.ViewPagerAdapter;
import com.haerul.sihandist.data.api.ConnectionServer;
import com.haerul.sihandist.data.db.repository.MasterRepository;
import com.haerul.sihandist.data.entity.Inspeksi;
import com.haerul.sihandist.databinding.ActivityInspeksiTlBinding;
import com.haerul.sihandist.utils.Constants;

import javax.inject.Inject;

public class TLActivity extends BaseActivity<ActivityInspeksiTlBinding, TLViewModel> {
    
    @Inject
    ConnectionServer server;
    @Inject
    MasterRepository repository;
    
    private ActivityInspeksiTlBinding binding;
    private TLViewModel viewModel;
    
    @Override
    public int getBindingVariable() {
        return com.haerul.sihandist.BR._all;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_inspeksi_tl;
    }

    @Override
    public TLViewModel getViewModel() {
        return viewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewDataBinding();
        viewModel = ViewModelProviders.of(this, new TLViewModel.ModelFactory(this, server, repository)).get(TLViewModel.class);
        setupToolbar();
        setupViewPager(binding.viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        try {
            ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
            adapter.addFragment(TLInfoFragment.newInstance((Inspeksi) getIntent().getSerializableExtra(Constants.EXTRA_DATA)), "Inspeksi");
            adapter.addFragment(TLUpdateFragment.newInstance((Inspeksi) getIntent().getSerializableExtra(Constants.EXTRA_DATA)), "Tindak Lanjut");
            viewPager.setAdapter(adapter);
            binding.tabLayout.setupWithViewPager(binding.viewPager);
            binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));
            binding.tabLayout.setTabTextColors(Color.parseColor("#77CBFF"), Color.parseColor("#ffffff"));
            binding.viewPager.setOffscreenPageLimit(2);
            binding.viewPager.setCurrentItem(0);
            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
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
