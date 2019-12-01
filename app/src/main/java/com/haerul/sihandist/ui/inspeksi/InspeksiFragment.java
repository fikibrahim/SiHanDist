package com.haerul.sihandist.ui.inspeksi;

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
import com.haerul.sihandist.data.entity.Inspeksi;
import com.haerul.sihandist.databinding.FragmentInspeksiBinding;
import com.haerul.sihandist.ui.inspeksi.add.AddInspeksiActivity;
import com.haerul.sihandist.ui.inspeksi.tindak_lanjut.TLActivity;
import com.haerul.sihandist.utils.Constants;
import com.haerul.sihandist.utils.Util;

import javax.inject.Inject;

public class InspeksiFragment extends BaseFragment<FragmentInspeksiBinding, InspeksiViewModel> 
        implements InspeksiViewModel.InspeksiNavigator {

    @Inject
    ConnectionServer server;
    @Inject
    MasterRepository repository;

    public FragmentInspeksiBinding binding;
    public InspeksiViewModel viewModel;

    public static InspeksiFragment newInstance(String refUID, String tab, int tabPos) {  // By Jenis Temuan
        Bundle args = new Bundle();
        args.putString(Constants.JENIS_WO, refUID);
        args.putString(Constants.TAB_TITLE, tab);
        args.putInt(Constants.TAB_POSITION, tabPos);
        InspeksiFragment fragment = new InspeksiFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getBindingVariable() {
        return com.haerul.sihandist.BR._all;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_inspeksi;
    }

    @Override
    public InspeksiViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = getViewDataBinding();
        binding.add.setOnClickListener(v -> startActivity(new Intent(getBaseActivity(), AddInspeksiActivity.class)));
        if (repository.getRefBySID(Util.getStringPreference(getBaseActivity(), Constants.USER_ROLE_SID)).ref_value == Constants.USER_VENDOR_TL) {
            binding.add.setVisibility(View.INVISIBLE);
        }
        viewModel = ViewModelProviders.of(this, new InspeksiViewModel.ModelFactory(getBaseActivity(), server, repository)).get(InspeksiViewModel.class);
        viewModel.setNavigator(this);
        
        getData();
        binding.swipe.setOnRefreshListener(this::getData);
    }

    private void getData() {
        if (getArguments() != null) {
            viewModel.getInspeksiByPP(getArguments().getString(Constants.JENIS_WO)).observe(this, data -> {
                BaseInspeksiFragment.tabLayout.getTabAt(getArguments().getInt(Constants.TAB_POSITION)).setText(getArguments().getString(Constants.TAB_TITLE) + " (" + data.size() + ")");
                if (data.size() > 0) {
                    binding.emptyView.setVisibility(View.GONE);
                    binding.recyclerView.setAdapter(new InspeksiAdapter(data, viewModel));
                } else {
                    binding.emptyView.setVisibility(View.VISIBLE);
                }
                binding.swipe.setRefreshing(false);
            });
        }
    }

    @Override
    public void onItemClick(Inspeksi data) {
        if (repository.getRefBySID(Util.getStringPreference(getBaseActivity(), Constants.USER_ROLE_SID)).ref_value == Constants.USER_VENDOR_TL) {
            Intent intent = new Intent(getBaseActivity(), TLActivity.class);
            intent.putExtra(Constants.EXTRA_DATA, data);
            new StartActivity(getBaseActivity(), binding.progressBar, intent).execute();
        }
        if (repository.getRefBySID(Util.getStringPreference(getBaseActivity(), Constants.USER_ROLE_SID)).ref_value == Constants.USER_INSPEKSI) {
            Intent intent = new Intent(getBaseActivity(), AddInspeksiActivity.class);
            intent.putExtra(Constants.EXTRA_DATA, data);
            new StartActivity(getBaseActivity(), binding.progressBar, intent).execute();
        }
    }
}
