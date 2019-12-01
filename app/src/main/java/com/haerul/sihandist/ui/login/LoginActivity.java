package com.haerul.sihandist.ui.login;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.haerul.sihandist.R;
import com.haerul.sihandist.base.BaseActivity;
import com.haerul.sihandist.data.api.ConnectionServer;
import com.haerul.sihandist.databinding.ActivityLoginBinding;
import com.haerul.sihandist.ui.MainActivity;
import com.haerul.sihandist.utils.Constants;
import com.haerul.sihandist.utils.SQLiteDownloaderActivity;
import com.haerul.sihandist.utils.Util;

import java.io.File;
import java.util.List;
import javax.inject.Inject;

public class LoginActivity extends BaseActivity<ActivityLoginBinding, LoginViewModel> implements LoginViewModel.LoginNavigator {

    @Inject
    ConnectionServer connectionServer;

    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;
    private boolean permissionNotAllowed;

    @Override
    public int getBindingVariable() {
        return com.haerul.sihandist.BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public LoginViewModel getViewModel() {
        return viewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Util.getBooleanPreference(this, Constants.IS_LOGIN)) {
            MainActivity.navigateToMain(this);
            finish();
        }
        binding = getViewDataBinding();
        viewModel = ViewModelProviders.of(this, new LoginViewModel.ModelFactory(getApplicationContext(), connectionServer)).get(LoginViewModel.class);
        binding.setViewModel(viewModel);
        viewModel.setNavigator(this);

        requestPermission();
    }

    private void requestPermission() {
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.CAMERA)
                .check();
    }

    boolean login() {
        if (binding.username.getText().toString().equals("")) {
            binding.username.setError("Username can't be empty!");
            binding.username.requestFocus();
            return false;
        }
        else if (binding.password.getText().toString().equals("")) {
            binding.password.setError("Password can't be empty!");
            binding.password.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public void onLogin() {
        
        hideKeyboard();

        if (!permissionNotAllowed) {
           
            loginProcess();
            
        } else {
            requestPermission();
        }
    }

    private void loginProcess() {
        createFile();

        if (login()) {

            if (binding.checkbox.isChecked()) {
                Util.putPreference(LoginActivity.this, "password_save", true);
                Util.putPreference(LoginActivity.this, Constants.USER_EMAIL, binding.username.getText().toString());
                Util.putPreference(LoginActivity.this, Constants.USER_PASSWORD, binding.password.getText().toString());
            }

            JsonObject object = new JsonObject();
            object.addProperty(Constants.USER_LOGIN_NAME, binding.username.getText().toString().trim());
            object.addProperty(Constants.USER_PASSWORD, binding.password.getText().toString().trim());
            viewModel.postLogin(object, binding.username.getText().toString().trim(), binding.password.getText().toString().trim());
        }
    }

    @Override
    public void loginResult(boolean isLogin, String message) {
        Log.w("TAG", message);
        hideKeyboard();
        if (isLogin) {
            viewModel.setIsLoading(false);

            Snackbar.make(binding.cardLogin, message, Snackbar.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, SQLiteDownloaderActivity.class));
            overridePendingTransition(0, 0);
            finish();
        } else {
            viewModel.setIsLoading(false);
            Snackbar.make(binding.cardLogin, message, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void createFile() {
        File file = new File(Constants.PATH_IMG);
        if (!file.exists()) {
            file.mkdir();
        }
        
        File file2 = new File(Constants.PATH_DOWNLOAD);
        if (!file2.exists()) {
            file2.mkdir();
        }
    }

    public static void navigateToLogin(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Util.getBooleanPreference(this, "password_save")) {
            binding.checkbox.setChecked(true);
            binding.username.setText(Util.getStringPreference(this, Constants.USER_LOGIN_NAME));
            binding.password.setText(Util.getStringPreference(this, Constants.USER_PASSWORD));
        }
    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            permissionNotAllowed = false;
            if (!permissionNotAllowed) {
                createFile();
            }
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            permissionNotAllowed = true;
            TedPermission.with(LoginActivity.this)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                    .setPermissions(
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CALL_PHONE,
                            Manifest.permission.CAMERA)
                    .check();
        }
    };
}
