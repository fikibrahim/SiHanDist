package com.haerul.sihandist.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.haerul.sihandist.R;
import com.haerul.sihandist.base.BaseActivity;
import com.haerul.sihandist.data.api.ConnectionServer;
import com.haerul.sihandist.data.db.repository.MasterRepository;
import com.haerul.sihandist.databinding.ActivityMainBaseBinding;
import com.haerul.sihandist.ui.c4a.C4aFragment;
import com.haerul.sihandist.ui.gangguan.BaseGangguanFragment;
import com.haerul.sihandist.ui.home.HomeFragment;
import com.haerul.sihandist.ui.inspeksi.BaseInspeksiFragment;
import com.haerul.sihandist.ui.login.LoginActivity;
import com.haerul.sihandist.ui.profile.ProfileFragment;
import com.haerul.sihandist.utils.AppService;
import com.haerul.sihandist.utils.Constants;
import com.haerul.sihandist.utils.Util;

import java.util.List;

import javax.inject.Inject;

public class MainActivity extends BaseActivity<ActivityMainBaseBinding, MainViewModel> 
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    public static final String STARTFOREGROUND_ACTION = "com.haerul.sihandist.start.foreground";
    public static final String STOPFOREGROUND_ACTION = "com.haerul.sihandist.stop.foreground";
    
    @Inject
    MasterRepository repository;
    @Inject
    ConnectionServer server;
    MainViewModel viewModel;

    @SuppressLint("StaticFieldLeak")
    public static ActivityMainBaseBinding binding;
    public static final int INS_ID = 0x101;
    public static final int HOME = 0x102;
    public static final int PROFILE_ID = 0x104;
    public static final int GGN_ID = 0x105;
    public static final int C4A_ID = 0x106;
    
    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main_base;
    }

    @Override
    public MainViewModel getViewModel() {
        return viewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewDataBinding();
        viewModel = ViewModelProviders.of(this, new MainViewModel.ModelFactory(this, server, repository)).get(MainViewModel.class);
        
        checkLogin();
        
        requestPermission();
        setupBottomNavMenu();
        
        binding.bnMain.setOnNavigationItemSelectedListener(this);
        loadFragment(new HomeFragment(), "HOME");
        
        startService();
    }

    private void setupBottomNavMenu() {
        Menu menu = binding.bnMain.getMenu();
        menu.add(Menu.NONE, HOME, Menu.NONE, "HOME").setIcon(R.drawable.ic_home);

        if (repository.getRefBySID(Util.getStringPreference(this, Constants.USER_ROLE_SID)).ref_value == Constants.USER_VENDOR_TL) {
            menu.add(Menu.NONE, INS_ID, Menu.NONE, "TL INSPEKSI").setIcon(R.drawable.ic_list);
        }
        if (repository.getRefBySID(Util.getStringPreference(this, Constants.USER_ROLE_SID)).ref_value == Constants.USER_INSPEKSI) {
            menu.add(Menu.NONE, INS_ID, Menu.NONE, "INSPEKSI").setIcon(R.drawable.ic_list);
        }
        if (repository.getRefBySID(Util.getStringPreference(this, Constants.USER_ROLE_SID)).ref_value == Constants.USER_C4A) {
            menu.add(Menu.NONE, C4A_ID, Menu.NONE, "C4A").setIcon(R.drawable.ic_list);
        }
        if (repository.getRefBySID(Util.getStringPreference(this, Constants.USER_ROLE_SID)).ref_value == Constants.USER_GANGGUAN) {
            menu.add(Menu.NONE, GGN_ID, Menu.NONE, "GANGGUAN").setIcon(R.drawable.ic_list);
        }

        if (repository.getRefBySID(Util.getStringPreference(this, Constants.USER_ROLE_SID)).ref_value == Constants.USER_INSPEKSI_GANGGUAN_C4A) {
            menu.add(Menu.NONE, INS_ID, Menu.NONE, "INSPEKSI").setIcon(R.drawable.ic_list);
            menu.add(Menu.NONE, C4A_ID, Menu.NONE, "C4A").setIcon(R.drawable.ic_list);
            menu.add(Menu.NONE, GGN_ID, Menu.NONE, "GANGGUAN").setIcon(R.drawable.ic_list);
        }
        
        
        menu.add(Menu.NONE, PROFILE_ID, Menu.NONE, "PROFILE").setIcon(R.drawable.ic_profile);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        switch (menuItem.getItemId()){
            case HOME:
                fragment = new HomeFragment();
                break;
            case INS_ID:
                fragment = new BaseInspeksiFragment();
                break;
            case GGN_ID:
                fragment = new BaseGangguanFragment();
                break;
            case C4A_ID:
                fragment = new C4aFragment();
                break;
            case PROFILE_ID:
                fragment = new ProfileFragment();
                break;
        }
        setTag(menuItem.getTitle().toString());
        return loadFragment(fragment, getTag());
    }
    

    private void logout() {
        View view = getLayoutInflater().inflate(R.layout.dialog_confirm_logout, null);
        Button delete = view.findViewById(R.id.delete_dialog);
        Button close = view.findViewById(R.id.close_dialog);

        BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.DialogStyle);

        delete.setOnClickListener(v -> {
            showProgress();
            new Handler().postDelayed(() -> {
                hideProgress();
                Util.putPreference(this, Constants.IS_LOGIN, false);
                LoginActivity.navigateToLogin(this);
                finish();
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

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {

        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            TedPermission.with(MainActivity.this)
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

    public static void navigateToMain(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    public boolean loadFragment(Fragment fragment, String tag) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_container, fragment, tag)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
            Log.w("TAG", tag);
            return true;
        }
        return false;
    }
    
    public static void setNavigationSelectedItemId(int id) {
        binding.bnMain.setSelectedItemId(id);
    }
    
    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    private void startService() {
        Log.w("LOG_SERVICE_BROADCAST", "onReceive");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.w("on", "foreground");
            Intent serviceIntent = new Intent(this, AppService.class);
            serviceIntent.setAction(MainActivity.STARTFOREGROUND_ACTION);
            this.startForegroundService(serviceIntent);
        } else {
            this.startService(new Intent(this, AppService.class));
            Log.w("on", "background");
        }
    }
}
