package com.haerul.sihandist.ui.setting;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.haerul.sihandist.R;

public class SettingActivity extends AppCompatActivity {

    private SettingViewModel settingViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_setting );
    }
}

