package com.haerul.sihandist.ui.inspeksi.tindak_lanjut;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.haerul.sihandist.R;
import com.haerul.sihandist.base.BaseFragment;
import com.haerul.sihandist.data.api.ConnectionServer;
import com.haerul.sihandist.data.db.repository.MasterRepository;
import com.haerul.sihandist.data.entity.Base64Data;
import com.haerul.sihandist.data.entity.Inspeksi;
import com.haerul.sihandist.databinding.FragmentInspeksiTlUpdateBinding;
import com.haerul.sihandist.utils.MapActivity;
import com.haerul.sihandist.utils.CameraXActivity;
import com.haerul.sihandist.utils.Constants;
import com.haerul.sihandist.utils.Util;

import org.json.JSONObject;

import java.io.File;

import javax.inject.Inject;

import static android.app.Activity.RESULT_OK;

public class TLUpdateFragment extends BaseFragment<FragmentInspeksiTlUpdateBinding, TLViewModel> implements TLViewModel.Navigator{

    private static final int REQUEST_CODE_LOC = 0x291;
    private static final int REQUEST_CAMERA = 0x218;
    @Inject
    ConnectionServer server;
    @Inject
    MasterRepository repository;
    private FragmentInspeksiTlUpdateBinding binding;
    private TLViewModel viewModel;
    private Inspeksi inspeksi;
    private String fileUri = null;
    private String imageBase64 = null;

    public static TLUpdateFragment newInstance(Inspeksi inspeksi) {
        Bundle args = new Bundle();
        args.putSerializable(Constants.DATA, inspeksi);
        TLUpdateFragment fragment = new TLUpdateFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public int getBindingVariable() {
        return com.haerul.sihandist.BR._all;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_inspeksi_tl_update;
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
        viewModel.setNavigator(this);
        if (getArguments() != null) {
            inspeksi = (Inspeksi) getArguments().getSerializable(Constants.DATA);
            binding.setItem(inspeksi);
            binding.setViewModel(viewModel);
            binding.tanggalTL.setText(Util.dateFormatter(Util.getTimestampNow(), Constants.DATE_ONLY_FORMAT));
            binding.jamTL.setText(Util.dateFormatter(Util.getTimestampNow(), Constants.TIME_ONLY_FORMAT));
            binding.jenisWO.setSpinnerValue(repository, Constants.JENIS_WO, inspeksi.jenis_wo_sid);
            binding.jenisWO.setEnabled(false);
            binding.lokasiTL.setOnClickListener(view1 -> pickLocation());
            binding.setLokasiTL.setOnClickListener(view1 -> pickLocation());
            binding.photoText.setOnClickListener(view12 -> takePhoto());
            binding.photoTL.setOnClickListener(view12 -> takePhoto());
            binding.photoPreviewTL.setOnClickListener(view12 -> takePhoto());
            binding.statusTL.setupSpinnerGeneric(repository, Constants.STATUS_TL, "is_vendor");

            //map snapshot
            if (inspeksi.lokasi_tl_x != null) {
                Util.loadMapView(binding.mapView, inspeksi.lokasi_tl_y, inspeksi.lokasi_tl_x);
                binding.mapPreviewLayout.setVisibility(View.VISIBLE);
            } 

            if (repository.getBase64Data(inspeksi.foto_tl) != null) {
                Base64Data base64data = repository.getBase64Data(inspeksi.foto_tl);
                Util.setImageBase64Preview(base64data, binding.photoPreviewTL);
                binding.photoText.setText(base64data.data_path.replaceAll(Constants.PATH_IMG, ""));
            } else {
                Util.downloadImageBase64Preview(inspeksi.foto_tl, binding.photoPreviewTL);
            }
            
            if (repository.getRefBySID(inspeksi.status_tl_sid).ref_value == 0) {
                binding.lokasiTL.setText("");
                binding.statusTL.setSpinnerValue(repository, Constants.STATUS_TL, inspeksi.status_tl_sid, "is_vendor");
            }
            else if (repository.getRefBySID(inspeksi.status_tl_sid).ref_value > 0) {
                binding.statusTL.setSpinnerValue(repository, Constants.STATUS_TL, inspeksi.status_tl_sid);
                binding.statusTL.setEnabled(false);
                binding.jamTL.setEnabled(false);
                binding.lokasiTL.setOnClickListener(null);
                binding.lokasiTL.setEnabled(false);
                binding.setLokasiTL.setOnClickListener(null);
                binding.photoText.setOnClickListener(null);
                binding.photoText.setEnabled(false);
                binding.photoTL.setOnClickListener(null);
                binding.photoPreviewTL.setOnClickListener(null);
                binding.keterangan.setEnabled(false);
                binding.cancel.setVisibility(View.GONE);
                binding.save.setVisibility(View.GONE);
            }
            
            binding.cancel.setOnClickListener(v -> {
                getBaseActivity().finish();
            });

            binding.save.setOnClickListener(v -> {
                hideKeyboard();
                saveTL();
            });
        }
    }

    private void saveTL() {
        if (Util.isNullOrEmpty(binding.jamTL.getText().toString())) {
            binding.jamTL.setError("This field can't be empty!");
            binding.jamTL.requestFocus();
            Util.scrollToView(binding.jamTL, binding.scrollView);
            return;
        } else if (Util.isNullOrEmpty(binding.tanggalTL.getText().toString())) {
            binding.tanggalTL.setError("This field can't be empty!");
            binding.tanggalTL.requestFocus();
            Util.scrollToView(binding.tanggalTL, binding.scrollView);
            return;
        } else if (Util.isNullOrEmpty(binding.jenisWO.getSpinnerValueSID(repository, Constants.JENIS_WO))) {
            Util.setErrorSipnner(binding.jenisWO, "This field can't be empty!");
            Util.scrollToView(binding.jenisWO, binding.scrollView);
            return;
        } else if (Util.isNullOrEmpty(binding.lokasiTL.getText().toString())) {
            Snackbar.make(binding.getRoot(), "Please pick a Lokasi TL", Snackbar.LENGTH_LONG).show();
            Util.scrollToView(binding.lokasiTL, binding.scrollView);
            Util.setFadeBackgroundError(binding.layoutLokasi);
            return;
        } else if (Util.isNullOrEmpty(binding.photoText.getText().toString())) {
            Snackbar.make(binding.getRoot(), "Please take a Photo TL", Snackbar.LENGTH_LONG).show();
            Util.scrollToView(binding.photoText, binding.scrollView);
            Util.setFadeBackgroundError(binding.layoutFoto);
            return;
        } else if (Util.isNullOrEmpty(binding.statusTL.getSpinnerValueSID(repository, Constants.STATUS_TL))) {
            Util.setErrorSipnner(binding.statusTL, "This field can't be empty!");
            Util.scrollToView(binding.statusTL, binding.scrollView);
            return;
        } else {
            inspeksi.tanggal_tl = binding.tanggalTL.getText().toString() + " " + binding.jamTL.getText().toString() + ":00";
            inspeksi.lokasi_tl_x = viewModel.lon;
            inspeksi.lokasi_tl_y = viewModel.lat;
            Base64Data dataBase64 = Util.insertBase64(repository, imageBase64, Constants.PATH_IMG + binding.photoText.getText().toString(), Util.getUserSID(getBaseActivity()));
            inspeksi.foto_tl = dataBase64.data_sid;
            inspeksi.status_tl_sid = repository.getRefByValue(Constants.STATUS_TL, 1).ref_sid;
            inspeksi.keterangan = binding.keterangan.getText().toString();
            viewModel.putInspeksi(inspeksi);
            viewModel.postBase64Data(dataBase64);
        }
    }

    void takePhoto() {
        Intent intent = new Intent(getBaseActivity(), CameraXActivity.class);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void pickLocation() {
        Log.d("TAG", "addLocation");
        try {
            JSONObject LonLat = new JSONObject();

            LonLat.put("LONGITUDE", Double.parseDouble(viewModel.lon.equals("") ? "0" : viewModel.lon));
            LonLat.put("LATITUDE", Double.parseDouble(viewModel.lat.equals("") ? "0" : viewModel.lat));

            Log.w("TAG", LonLat.toString());

            Intent intent = new Intent(getBaseActivity(), MapActivity.class);
            intent.putExtra("JSON", String.valueOf(LonLat));
            Log.w("TAG pick", String.valueOf(LonLat));
            startActivityForResult(intent, REQUEST_CODE_LOC);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOC) {
            if (resultCode == RESULT_OK) {
                try {
                    Log.wtf("LOCATION", "EDIT");
                    JSONObject jsons = new JSONObject(data.getStringExtra("JSON"));
                    viewModel.lon = String.valueOf(jsons.getDouble("LONGITUDE"));
                    viewModel.lat = String.valueOf(jsons.getDouble("LATITUDE"));
                    binding.lokasiTL.setText(String.format("%s, %s", viewModel.lon, viewModel.lat));
                    Util.loadMapView(binding.mapView, viewModel.lat, viewModel.lon);
                    binding.mapPreviewLayout.setVisibility(View.VISIBLE);
                    Log.w("TAG 2", String.valueOf(jsons));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == REQUEST_CAMERA) {
            if (resultCode == RESULT_OK) {
                try {
                    Log.w("Test", data.toString());
                    File file = new File(data.getStringExtra("data"));
                    fileUri = Uri.fromFile(file).toString();
                    imageBase64 = viewModel.previewCapturedImage(binding.photoPreviewTL, fileUri);
                    binding.photoText.setText("" + file.getName());
                    binding.photoPreviewTL.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void showProgress() {
        getBaseActivity().showProgress();
    }

    @Override
    public void hideProgress() {
        getBaseActivity().hideProgress();
    }

    @Override
    public void result(boolean status, String message) {
        hideKeyboard();
        if (status) {
            getBaseActivity().finish();
            Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
        }
    }
}
