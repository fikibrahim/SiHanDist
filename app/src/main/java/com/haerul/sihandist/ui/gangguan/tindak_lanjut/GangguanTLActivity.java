package com.haerul.sihandist.ui.gangguan.tindak_lanjut;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.haerul.sihandist.R;
import com.haerul.sihandist.base.BaseActivity;
import com.haerul.sihandist.data.api.ConnectionServer;
import com.haerul.sihandist.data.db.repository.MasterRepository;
import com.haerul.sihandist.data.entity.Base64Data;
import com.haerul.sihandist.data.entity.Gangguan;
import com.haerul.sihandist.databinding.ActivityGangguanTlBinding;
import com.haerul.sihandist.utils.CameraXActivity;
import com.haerul.sihandist.utils.Constants;
import com.haerul.sihandist.utils.MapActivity;
import com.haerul.sihandist.utils.Util;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

public class GangguanTLActivity extends BaseActivity<ActivityGangguanTlBinding, GangguanViewModel> implements GangguanViewModel.Navigator {

    private static final int REQUEST_CODE_LOC = 1237;
    private static final int REQUEST_CAMERA1 = 1273;
    private static final int REQUEST_CAMERA2 = 1272;
    private static final int REQUEST_CAMERATL = 1271;
    public String lon = "";
    public String lat = "";
    @Inject
    MasterRepository repository;
    @Inject
    ConnectionServer server;
    
    private ActivityGangguanTlBinding binding;
    private GangguanViewModel viewModel;
    private Gangguan gangguan;
    private WebView myWebView;
    private String fileUri1;
    private String fileUri2;
    private String fileUriTL;
    private String imageBase641;
    private String imageBase642;
    private String imageBase64TL;

    @Override
    public int getBindingVariable() {
        return com.haerul.sihandist.BR._all;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_gangguan_tl;
    }

    @Override
    public GangguanViewModel getViewModel() {
        return viewModel;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewDataBinding();
        viewModel = ViewModelProviders.of(this, new GangguanViewModel.ModelFactory(this, server, repository)).get(GangguanViewModel.class);
        viewModel.setNavigator(this);
        setupToolbar();
        if (getIntent().getSerializableExtra(Constants.EXTRA_DATA) != null) {
            gangguan = (Gangguan) getIntent().getSerializableExtra(Constants.EXTRA_DATA);
            assert gangguan != null;
            binding.setItem(gangguan);
            binding.setViewModel(viewModel);
            binding.unit.setSpinnerValue(repository, Constants.ULP, gangguan.g_unit);
            binding.unit.setEnabled(false);
            binding.penyulang.setSpinnerValue(repository, Constants.PENYULANG, gangguan.g_penyulang);
            binding.penyulang.setEnabled(false);
            binding.indikasi.setSpinnerValue(repository, Constants.INDIKASI, gangguan.g_indikasi);
            binding.indikasi.setEnabled(false);
            binding.kelompok.setSpinnerValue(repository, Constants.KELOMPOK, gangguan.g_kelompok);
            
            binding.status.setSpinnerValue(repository, Constants.STATUS_GANGGUAN, repository.getRefByValue(Constants.STATUS_GANGGUAN,2).ref_sid, "is_user");
            binding.tanggalTL.setText(Util.dateFormatter(Util.getTimestampNow(), Constants.DATE_ONLY_FORMAT));
            binding.jamTL.setText(Util.dateFormatter(Util.getTimestampNow(), Constants.TIME_ONLY_FORMAT));
            
            binding.lokasiGangguan.setOnClickListener(view1 -> pickLocation());
            binding.setLokasi.setOnClickListener(view1 -> pickLocation());
            binding.photoText1.setOnClickListener(view12 -> takePhoto(REQUEST_CAMERA1));
            binding.photo1.setOnClickListener(view12 -> takePhoto(REQUEST_CAMERA1));
            binding.photoPreview1.setOnClickListener(view12 -> takePhoto(REQUEST_CAMERA1));
            binding.photoText2.setOnClickListener(view12 -> takePhoto(REQUEST_CAMERA2));
            binding.photo2.setOnClickListener(view12 -> takePhoto(REQUEST_CAMERA2));
            binding.photoPreview2.setOnClickListener(view12 -> takePhoto(REQUEST_CAMERA2));
            binding.photoTextTL.setOnClickListener(view12 -> takePhoto(REQUEST_CAMERATL));
            binding.photoTL.setOnClickListener(view12 -> takePhoto(REQUEST_CAMERATL));
            binding.photoPreviewTL.setOnClickListener(view12 -> takePhoto(REQUEST_CAMERATL));

            Util.loadMapView(binding.mapView, gangguan.g_lat, gangguan.g_lon);
            binding.mapPreviewLayout.setVisibility(View.VISIBLE);
            viewModel.lat = gangguan.g_lat;
            viewModel.lon = gangguan.g_lon;

            if (repository.getBase64Data(gangguan.g_foto_1) != null) {
                Base64Data base64data = repository.getBase64Data(gangguan.g_foto_1);
                Util.setImageBase64Preview(base64data, binding.photoPreview1);
                binding.photoText1.setText(base64data.data_path.replaceAll(Constants.PATH_IMG, ""));
            } else {
                Util.downloadImageBase64Preview(gangguan.g_foto_1, binding.photoPreview1);
            }

            if (repository.getBase64Data(gangguan.g_foto_2) != null) {
                Base64Data base64data = repository.getBase64Data(gangguan.g_foto_2);
                Util.setImageBase64Preview(base64data, binding.photoPreview2);
                binding.photoText2.setText(base64data.data_path.replaceAll(Constants.PATH_IMG, ""));
            } else {
                Util.downloadImageBase64Preview(gangguan.g_foto_2, binding.photoPreview2);
            }

            if (repository.getBase64Data(gangguan.g_foto_tl) != null) {
                Base64Data base64data = repository.getBase64Data(gangguan.g_foto_tl);
                Util.setImageBase64Preview(base64data, binding.photoPreviewTL);
                binding.photoTextTL.setText(base64data.data_path.replaceAll(Constants.PATH_IMG, ""));
            } else {
                Util.downloadImageBase64Preview(gangguan.g_foto_tl, binding.photoPreviewTL);
            }
            
            //check status 
            if (repository.getRefBySID(gangguan.g_status).ref_value == 1) {
                //update status to DIPERIKSA - BELUM SELESAI
                gangguan.g_status = repository.getRefByValue(Constants.STATUS_GANGGUAN, 2).ref_sid;
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                new Handler().postDelayed(() -> {
                    try {
                        Util.getApi().putGangguan(Util.getToken(GangguanTLActivity.this),
                                new JsonParser().parse(new Gson().toJson(gangguan)).getAsJsonObject()).execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }, 200);
            }
        }
        
        binding.cancel.setOnClickListener(v->onBackPressed());
        binding.save.setOnClickListener(v -> {
            hideKeyboard();
            saveTL();
        });
    }

    private void saveTL() {
        if (Util.isNullOrEmpty(binding.arusPashaR.getText().toString()) ||
                Util.isNullOrEmpty(binding.arusPashaS.getText().toString()) ||
                Util.isNullOrEmpty(binding.arusPashaT.getText().toString()) ||
                Util.isNullOrEmpty(binding.arusPashaN.getText().toString()) ) {
            Snackbar.make(binding.getRoot(), "Please input Phasa", Snackbar.LENGTH_LONG).show();
            Util.scrollToView(binding.arusPashaR, binding.scrollView);
            Util.setFadeBackgroundError(binding.layoutPhasa);
            return;
        } else if (Util.isNullOrEmpty(binding.jamTL.getText().toString())) {
            binding.jamTL.setError("This field can't be empty!");
            binding.jamTL.requestFocus();
            Util.scrollToView(binding.jamTL, binding.scrollView);
            return;
        } else if (Util.isNullOrEmpty(binding.tanggalTL.getText().toString())) {
            binding.tanggalTL.setError("This field can't be empty!");
            binding.tanggalTL.requestFocus();
            Util.scrollToView(binding.tanggalTL, binding.scrollView);
            return;
        } else if (Util.isNullOrEmpty(binding.tindakLanjut.getText().toString())) {
            binding.tindakLanjut.setError("This field can't be empty!");
            binding.tindakLanjut.requestFocus();
            Util.scrollToView(binding.tindakLanjut, binding.scrollView);
            return;
        } else if (Util.isNullOrEmpty(binding.status.getSpinnerValueSID(repository, Constants.STATUS_GANGGUAN, "is_user"))) {
            Util.setErrorSipnner(binding.status, "This field can't be empty!");
            Util.scrollToView(binding.status, binding.scrollView);
            return;
        } else if (Util.isNullOrEmpty(binding.kelompok.getSpinnerValueSID(repository, Constants.KELOMPOK))) {
            Util.setErrorSipnner(binding.kelompok, "This field can't be empty!");
            Util.scrollToView(binding.kelompok, binding.scrollView);
            return;
        } else if (Util.isNullOrEmpty(binding.sebab.getText().toString())) {
            binding.sebab.setError("This field can't be empty!");
            binding.sebab.requestFocus();
            Util.scrollToView(binding.sebab, binding.scrollView);
            return;
        } else if (Util.isNullOrEmpty(binding.lokasiGangguan.getText().toString())) {
            Snackbar.make(binding.getRoot(), "Please pick a Lokasi TL", Snackbar.LENGTH_LONG).show();
            Util.scrollToView(binding.lokasiGangguan, binding.scrollView);
            Util.setFadeBackgroundError(binding.layoutLokasi);
            return;
        } else if (Util.isNullOrEmpty(binding.photoText1.getText().toString())) {
            Snackbar.make(binding.getRoot(), "Please take a Photo 1", Snackbar.LENGTH_LONG).show();
            Util.scrollToView(binding.photoText1, binding.scrollView);
            Util.setFadeBackgroundError(binding.layoutFoto1);
            return;
        }
        else {
            
            if (Util.isNullOrEmpty(binding.photoTextTL.getText().toString())) {
                Snackbar.make(binding.getRoot(), "Please take a Photo TL", Snackbar.LENGTH_LONG).show();
                Util.scrollToView(binding.photoTextTL, binding.scrollView);
                Util.setFadeBackgroundError(binding.layoutFotoTL);
                return;
            } 
            
            else {
                gangguan.g_kelompok = binding.kelompok.getSpinnerValueSID(repository, Constants.KELOMPOK);
                gangguan.g_sebab = binding.sebab.getText().toString().trim().replaceAll("\n", "");
                gangguan.g_date_tl = binding.tanggalTL.getText().toString() + " " + binding.jamTL.getText().toString() + ":00";
                gangguan.g_lat = viewModel.lat;
                gangguan.g_lon = viewModel.lon;
                
                Base64Data dataBase641 = Util.insertBase64(repository, imageBase641, Constants.PATH_IMG + binding.photoText1.getText().toString(), Util.getUserSID(this));
                gangguan.g_foto_1 = dataBase641.data_sid;

                Base64Data dataBase642 = null;
                if (this.imageBase642 != null) {
                    dataBase642 = Util.insertBase64(repository, imageBase642, Constants.PATH_IMG + binding.photoText2.getText().toString(), Util.getUserSID(this));
                    gangguan.g_foto_2 = dataBase642.data_sid;
                }

                Base64Data dataBase64TL = Util.insertBase64(repository, imageBase64TL, Constants.PATH_IMG + binding.photoTextTL.getText().toString(), Util.getUserSID(this));
                gangguan.g_foto_tl= dataBase64TL.data_sid;

                gangguan.g_keterangan = binding.keterangan.getText().toString().trim().replaceAll("\n", "");
                gangguan.g_r = binding.arusPashaR.getText().toString().trim().replaceAll("\n", "");
                gangguan.g_s = binding.arusPashaS.getText().toString().trim().replaceAll("\n", "");
                gangguan.g_t = binding.arusPashaT.getText().toString().trim().replaceAll("\n", "");
                gangguan.g_n = binding.arusPashaN.getText().toString().trim().replaceAll("\n", "");
                
                gangguan.g_tl = binding.tindakLanjut.getText().toString();
                gangguan.g_status = binding.status.getSpinnerValueSID(repository, Constants.STATUS_GANGGUAN, "is_user");
                gangguan.post_by = Util.getStringPreference(this, Constants.USER_SID);
                gangguan.date_modified = Util.dateFormatter(Util.getTimestampNow() , Constants.DATE_TIME_FORMAT);
                
                viewModel.putGangguan(gangguan);
                viewModel.postBase64Data(dataBase641);
                if (imageBase642 != null) { viewModel.postBase64Data(dataBase642); }
                viewModel.postBase64Data(dataBase64TL);
            }
        }
    }

    void takePhoto(int requerstCode) {
        Intent intent = new Intent(this, CameraXActivity.class);
        startActivityForResult(intent, requerstCode);
    }

    private void pickLocation() {
        Log.d("TAG", "addLocation");
        try {
            JSONObject LonLat = new JSONObject();

            LonLat.put("LONGITUDE", Double.parseDouble(viewModel.lon.equals("") ? "0" : viewModel.lon));
            LonLat.put("LATITUDE", Double.parseDouble(viewModel.lat.equals("") ? "0" : viewModel.lat));

            Log.w("TAG", LonLat.toString());

            Intent intent = new Intent(this, MapActivity.class);
            intent.putExtra("JSON", String.valueOf(LonLat));
            Log.w("TAG pick", String.valueOf(LonLat));
            startActivityForResult(intent, REQUEST_CODE_LOC);

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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOC) {
            if (resultCode == RESULT_OK) {
                try {
                    Log.wtf("LOCATION", "EDIT");
                    JSONObject jsons = new JSONObject(data.getStringExtra("JSON"));
                    viewModel.lon = String.valueOf(jsons.getDouble("LONGITUDE"));
                    viewModel.lat = String.valueOf(jsons.getDouble("LATITUDE"));
                    binding.lokasiGangguan.setText(String.format("%s, %s", viewModel.lon, viewModel.lat));
                    Util.loadMapView(binding.mapView, viewModel.lat, viewModel.lon);
                    binding.mapPreviewLayout.setVisibility(View.VISIBLE);
                    Log.w("TAG 2", String.valueOf(jsons));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } 
        else if (requestCode == REQUEST_CAMERA1) {
            if (resultCode == RESULT_OK) {
                try {
                    Log.w("Test", data.toString());
                    File file = new File(data.getStringExtra("data"));
                    fileUri1 = Uri.fromFile(file).toString();
                    imageBase641 = viewModel.previewCapturedImage(binding.photoPreview1, fileUri1);
                    binding.photoText1.setText("" + file.getName());
                    binding.photoPreview1.setVisibility(View.VISIBLE);
                    Util.deleteFileByPath(repository.getBase64Data(gangguan.g_foto_1).data_path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else if (requestCode == REQUEST_CAMERA2) {
            if (resultCode == RESULT_OK) {
                try {
                    Log.w("Test", data.toString());
                    File file = new File(data.getStringExtra("data"));
                    fileUri2 = Uri.fromFile(file).toString();
                    imageBase642 = viewModel.previewCapturedImage(binding.photoPreview2, fileUri2);
                    binding.photoText2.setText("" + file.getName());
                    binding.photoPreview2.setVisibility(View.VISIBLE);
                    Util.deleteFileByPath(repository.getBase64Data(gangguan.g_foto_2).data_path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else if (requestCode == REQUEST_CAMERATL) {
            if (resultCode == RESULT_OK) {
                try {
                    Log.w("Test", data.toString());
                    File file = new File(data.getStringExtra("data"));
                    fileUriTL = Uri.fromFile(file).toString();
                    imageBase64TL = viewModel.previewCapturedImage(binding.photoPreviewTL, fileUriTL);
                    binding.photoTextTL.setText("" + file.getName());
                    binding.photoPreviewTL.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
    
    @Override
    public void result(boolean status, String message) {
        hideKeyboard();
        Util.confirmDialog(this, "Message", message, answer -> finish());
    }
}
