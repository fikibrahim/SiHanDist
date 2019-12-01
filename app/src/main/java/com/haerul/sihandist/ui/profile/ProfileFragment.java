package com.haerul.sihandist.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.haerul.sihandist.R;
import com.haerul.sihandist.base.BaseFragment;
import com.haerul.sihandist.base.BaseViewModel;
import com.haerul.sihandist.data.db.repository.MasterRepository;
import com.haerul.sihandist.data.entity.GenericReferences;
import com.haerul.sihandist.data.entity.User;
import com.haerul.sihandist.databinding.FragmentProfileBinding;
import com.haerul.sihandist.ui.log_c4a.LogC4aActivity;
import com.haerul.sihandist.ui.log_gangguan.LogGgnActivity;
import com.haerul.sihandist.ui.log_inspeksi.LogInsActivity;
import com.haerul.sihandist.ui.login.LoginActivity;
import com.haerul.sihandist.ui.setting.SettingActivity;
import com.haerul.sihandist.ui.setting.SettingViewModel;
import com.haerul.sihandist.utils.Constants;
import com.haerul.sihandist.utils.SQLiteDownloaderActivity;
import com.haerul.sihandist.utils.Util;

import javax.inject.Inject;

public class ProfileFragment extends BaseFragment<FragmentProfileBinding, BaseViewModel> {

    private RelativeLayout relativeLayout;

    @Inject
    MasterRepository repository;
    FragmentProfileBinding binding;
    
    @Override
    public int getBindingVariable() {
        return com.haerul.sihandist.BR._all;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_profile;
    }

    @Override
    public BaseViewModel getViewModel() {
        return null;
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = getViewDataBinding();
        User user = repository.getUserBySID(Util.getStringPreference(getBaseActivity(), Constants.USER_SID));
        GenericReferences reg = repository.getRefBySID(user.user_unit);
        binding.setItem(repository.getUserBySID(Util.getStringPreference(getBaseActivity(), Constants.USER_SID)));
        binding.region.setText(reg.ref_name);
        binding.logout.setOnClickListener(this::logout);

        binding.settings.setOnClickListener(v -> {
            Intent intent = new Intent( getBaseActivity(),SettingActivity.class );
            startActivity( intent );
        });


        binding.reset.setOnClickListener(v -> {
            syncServer();
        });


        if (repository.getRefBySID(Util.getStringPreference(getBaseActivity(), Constants.USER_ROLE_SID)).ref_value == Constants.USER_VENDOR_TL) {
            binding.logIns.setVisibility(View.VISIBLE);
            binding.logIns.setOnClickListener(v->startActivity(new Intent(getBaseActivity(), LogInsActivity.class)));
        }
        if (repository.getRefBySID(Util.getStringPreference(getBaseActivity(), Constants.USER_ROLE_SID)).ref_value == Constants.USER_INSPEKSI) {
            binding.logIns.setVisibility(View.VISIBLE);
            binding.logIns.setOnClickListener(v->startActivity(new Intent(getBaseActivity(), LogInsActivity.class)));
        }
        if (repository.getRefBySID(Util.getStringPreference(getBaseActivity(), Constants.USER_ROLE_SID)).ref_value == Constants.USER_C4A) {
            binding.logC4a.setVisibility(View.VISIBLE);
            binding.logC4a.setOnClickListener(v->startActivity(new Intent(getBaseActivity(), LogC4aActivity.class)));
        }
        if (repository.getRefBySID(Util.getStringPreference(getBaseActivity(), Constants.USER_ROLE_SID)).ref_value == Constants.USER_GANGGUAN) {
            binding.logGgn.setVisibility(View.VISIBLE);
            binding.logGgn.setOnClickListener(v->startActivity(new Intent(getBaseActivity(), LogGgnActivity.class)));
        }
        if (repository.getRefBySID(Util.getStringPreference(getBaseActivity(), Constants.USER_ROLE_SID)).ref_value == Constants.USER_INSPEKSI_GANGGUAN_C4A) {
            binding.logIns.setVisibility(View.VISIBLE);
            binding.logIns.setOnClickListener(v->startActivity(new Intent(getBaseActivity(), LogInsActivity.class)));
            binding.logC4a.setVisibility(View.VISIBLE);
            binding.logC4a.setOnClickListener(v->startActivity(new Intent(getBaseActivity(), LogC4aActivity.class)));
            binding.logGgn.setVisibility(View.VISIBLE);
            binding.logGgn.setOnClickListener(v->startActivity(new Intent(getBaseActivity(), LogGgnActivity.class)));
        }
    }



    private void syncServer() {
        LayoutInflater li = (LayoutInflater) getBaseActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View views = li.inflate(R.layout.dialog_confirm_sync, null);
        Button submit = views.findViewById(R.id.save);
        Button cancel = views.findViewById(R.id.close);

        BottomSheetDialog dialog = new BottomSheetDialog(getBaseActivity(), R.style.DialogStyle);

        submit.setOnClickListener((v2) -> {
            dialog.dismiss();
            Toast.makeText(getBaseActivity(), "Please wait...", Toast.LENGTH_SHORT).show();
            getBaseActivity().showProgress();
            new Handler().postDelayed(() -> {
                getBaseActivity().hideProgress();
                Intent intent = new Intent(getBaseActivity(), SQLiteDownloaderActivity.class);
                startActivity(intent);
                getBaseActivity().finish();
                System.exit(0);
            }, 300);
        });
        cancel.setOnClickListener((v2) -> dialog.dismiss());

        dialog.setContentView(views);
        dialog.show();
    }

    private void logout(View v1) {
        View view = getLayoutInflater().inflate(R.layout.dialog_confirm_logout, null);
        Button delete = view.findViewById(R.id.delete_dialog);
        Button close = view.findViewById(R.id.close_dialog);

        BottomSheetDialog dialog = new BottomSheetDialog(getBaseActivity(), R.style.DialogStyle);

        delete.setOnClickListener(v -> {
            getBaseActivity().showProgress();
            new Handler().postDelayed(() -> {
                getBaseActivity().hideProgress();
                Util.putPreference(getBaseActivity(), Constants.IS_LOGIN, false);
                LoginActivity.navigateToLogin(getBaseActivity());
                getBaseActivity().finish();
                System.exit(0);
                Snackbar.make(binding.getRoot(), "Logging out!", Snackbar.LENGTH_SHORT).show();
            }, 200);

            dialog.dismiss();
        });

        close.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.setContentView(view);
        dialog.show();
    }
}
