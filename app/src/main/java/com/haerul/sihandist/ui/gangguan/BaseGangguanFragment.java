package com.haerul.sihandist.ui.gangguan;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.haerul.sihandist.R;
import com.haerul.sihandist.base.BaseFragment;
import com.haerul.sihandist.base.ViewPagerAdapter;
import com.haerul.sihandist.data.api.ConnectionServer;
import com.haerul.sihandist.data.db.repository.MasterRepository;
import com.haerul.sihandist.data.entity.GenericReferences;
import com.haerul.sihandist.databinding.FragmentBaseGgnBinding;
import com.haerul.sihandist.ui.inspeksi.InspeksiViewModel;
import com.haerul.sihandist.utils.Constants;
import com.haerul.sihandist.utils.Util;

import javax.inject.Inject;

public class BaseGangguanFragment extends BaseFragment<FragmentBaseGgnBinding, InspeksiViewModel> {
   
    @Inject
    ConnectionServer server;
    @Inject
    MasterRepository repository;

    @SuppressLint("StaticFieldLeak")
    public static TabLayout tabLayout;
    public FragmentBaseGgnBinding binding;
    public InspeksiViewModel viewModel;
    
    @Override
    public int getBindingVariable() {
        return com.haerul.sihandist.BR._all;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_base_ggn;
    }

    @Override
    public InspeksiViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = getViewDataBinding();
        viewModel = ViewModelProviders.of(this, new InspeksiViewModel.ModelFactory(getBaseActivity(), server, repository)).get(InspeksiViewModel.class);
        setupViewPager(binding.viewPager);
        binding.toolbar.setTitle("SiHanDist - Gangguan");
    }

    private void setupViewPager(ViewPager viewPager) {
        try {
            ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
            int pageSize = 0;
            for (GenericReferences ref : repository.getRefByCategory(Constants.ULP)) {
                if (repository.getRefBySID(Util.getStringPreference(getBaseActivity(), Constants.USER_UNIT)).ref_sid.equals(ref.ref_sid)) {
                    adapter.addFragment(GangguanFragment.newInstance(ref.ref_sid, ref.ref_name, pageSize), ref.ref_name);
                    pageSize++;
                } else if (repository.getRefBySID(Util.getStringPreference(getBaseActivity(), Constants.USER_ROLE_SID)).ref_value == Constants.ADMIN_UP3) {
                    adapter.addFragment(GangguanFragment.newInstance(ref.ref_sid, ref.ref_name, pageSize), ref.ref_name);
                    pageSize++;
                }
            }
            viewPager.setAdapter(adapter);
            tabLayout = binding.tabLayout;
            tabLayout.setupWithViewPager(binding.viewPager);
            binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));
            tabLayout.setTabTextColors(Color.parseColor("#77CBFF"), Color.parseColor("#ffffff"));
            binding.viewPager.setOffscreenPageLimit(pageSize);
            binding.viewPager.setCurrentItem(0);
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
