package com.haerul.sihandist.ui.c4a;

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
import com.haerul.sihandist.databinding.FragmentC4aBinding;
import com.haerul.sihandist.ui.c4a.add.AddC4AActivity;
import com.haerul.sihandist.ui.inspeksi.add.AddInspeksiActivity;
import com.haerul.sihandist.ui.inspeksi.tindak_lanjut.TLActivity;
import com.haerul.sihandist.utils.Constants;
import com.haerul.sihandist.utils.Util;

import javax.inject.Inject;

public class C4aFragment extends BaseFragment<FragmentC4aBinding, C4aViewModel> implements C4aViewModel.InspeksiNavigator {

    @Inject
    ConnectionServer server;
    @Inject
    MasterRepository repository;

    public FragmentC4aBinding binding;
    public C4aViewModel viewModel;

    @Override
    public int getBindingVariable() {
        return com.haerul.sihandist.BR._all;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_c4a;
    }

    @Override
    public C4aViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = getViewDataBinding();
        binding.add.setOnClickListener(v -> startActivity(new Intent(getBaseActivity(), AddC4AActivity.class)));
        if (repository.getRefBySID(Util.getStringPreference(getBaseActivity(), Constants.USER_ROLE_SID)).ref_value == Constants.USER_VENDOR_TL) {
            binding.add.setVisibility(View.INVISIBLE);
        }
        viewModel = ViewModelProviders.of(this, new C4aViewModel.ModelFactory(getBaseActivity(), server, repository)).get(C4aViewModel.class);
        viewModel.setNavigator(this);
        
        getData();
        binding.swipe.setOnRefreshListener(this::getData);
    }

    private void getData() {
        viewModel.getC4A().observe(this, data -> {
            if (data.size() > 0) {
                binding.emptyView.setVisibility(View.GONE);
                binding.recyclerView.setAdapter(new C4aAdapter(data, viewModel));
            } else {
                binding.emptyView.setVisibility(View.VISIBLE);
            }
            binding.swipe.setRefreshing(false);
        });
    }

    @Override
    public void onItemClick(Inspeksi data) {
        if (repository.getRefBySID(Util.getStringPreference(getBaseActivity(), Constants.USER_ROLE_SID)).ref_value == Constants.USER_VENDOR_TL) {
            Intent intent = new Intent(getBaseActivity(), TLActivity.class);
            intent.putExtra(Constants.EXTRA_DATA, data);
            new StartActivity(getBaseActivity(), binding.progressBar, intent).execute();
        }
        else if (repository.getRefBySID(Util.getStringPreference(getBaseActivity(), Constants.USER_ROLE_SID)).ref_value == Constants.USER_INSPEKSI) {
            Intent intent = new Intent(getBaseActivity(), AddInspeksiActivity.class);
            intent.putExtra(Constants.EXTRA_DATA, data);
            new StartActivity(getBaseActivity(), binding.progressBar, intent).execute();
        }
        else if (repository.getRefBySID(Util.getStringPreference(getBaseActivity(), Constants.USER_ROLE_SID)).ref_value == Constants.USER_C4A) {
            Intent intent = new Intent(getBaseActivity(), AddC4AActivity.class);
            intent.putExtra(Constants.EXTRA_DATA, data);
            new StartActivity(getBaseActivity(), binding.progressBar, intent).execute();
        }
        else if (repository.getRefBySID(Util.getStringPreference(getBaseActivity(), Constants.USER_ROLE_SID)).ref_value == Constants.USER_INSPEKSI_GANGGUAN_C4A) {
            Intent intent = new Intent(getBaseActivity(), AddC4AActivity.class);
            intent.putExtra(Constants.EXTRA_DATA, data);
            new StartActivity(getBaseActivity(), binding.progressBar, intent).execute();
        }
    }
}
