package com.haerul.sihandist.ui.inspeksi.tindak_lanjut;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.haerul.sihandist.R;
import com.haerul.sihandist.base.BaseFragment;
import com.haerul.sihandist.data.api.ConnectionServer;
import com.haerul.sihandist.data.db.repository.MasterRepository;
import com.haerul.sihandist.data.entity.Base64Data;
import com.haerul.sihandist.data.entity.Inspeksi;
import com.haerul.sihandist.databinding.FragmentInspeksiTlInfoBinding;
import com.haerul.sihandist.utils.Constants;
import com.haerul.sihandist.utils.Util;

import javax.inject.Inject;

public class TLInfoFragment extends BaseFragment<FragmentInspeksiTlInfoBinding, TLViewModel> {
   
    @Inject
    ConnectionServer server;
    @Inject
    MasterRepository repository;
    private FragmentInspeksiTlInfoBinding binding;
    private TLViewModel viewModel;
    private Inspeksi inspeksi;

    public static TLInfoFragment newInstance(Inspeksi inspeksi) {
        Bundle args = new Bundle();
        args.putSerializable(Constants.DATA, inspeksi);
        TLInfoFragment fragment = new TLInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public int getBindingVariable() {
        return com.haerul.sihandist.BR._all;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_inspeksi_tl_info;
    }

    @Override
    public TLViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = getViewDataBinding();
        viewModel = ViewModelProviders.of(getBaseActivity(), new TLViewModel.ModelFactory(getBaseActivity(), server, repository)).get(TLViewModel.class);
        if (getArguments() != null) {
            inspeksi = (Inspeksi) getArguments().getSerializable(Constants.DATA);
            binding.setItem(inspeksi);
            binding.setViewModel(viewModel);
            binding.rayon.setSpinnerValue(repository, Constants.ULP, inspeksi.rayon_sid);
            binding.rayon.setEnabled(false);
            binding.penyulang.setSpinnerValue(repository, Constants.PENYULANG, inspeksi.penyulang_sid);
            binding.penyulang.setEnabled(false);
            binding.jenisTemuan.setSpinnerValue(repository, Constants.JENIS_TEMUAN, inspeksi.jenis_temuan_sid);
            binding.jenisTemuan.setEnabled(false);
            binding.tingkatEmergency.setRadioGroupValue(repository, Constants.KONDISI_TINGKAT_EMERGENCY, inspeksi.tingkat_emergency_sid, false);
            binding.pemadaman.setRadioGroupValue(repository, Constants.PEMADAMAN, inspeksi.pemadaman_sid, false);

            //map snapshot
            if (inspeksi.lokasi_inspeksi_y != null) {
                Util.loadMapView(binding.mapView, inspeksi.lokasi_inspeksi_y, inspeksi.lokasi_inspeksi_x);
                binding.mapPreviewLayout.setVisibility(View.VISIBLE);
            }

            if (repository.getBase64Data(inspeksi.foto_inspeksi) != null) {
                Log.w("TAG", "set");
                Base64Data base64data = repository.getBase64Data(inspeksi.foto_inspeksi);
                Util.setImageBase64Preview(base64data, binding.photoPreview);
                Util.setImageBase64Preview(base64data, binding.headerImage);
                binding.photoText.setText(base64data.data_path.replaceAll(Constants.PATH_IMG, ""));
            } else {
                Log.w("TAG", "download");
                Util.downloadImageBase64Preview2(inspeksi.foto_inspeksi, binding.photoPreview, binding.headerImage);
            }
        }
    }
}
