package com.haerul.sihandist.ui.setting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.haerul.sihandist.R;
import com.haerul.sihandist.databinding.FragmentProfileBinding;

public class UpdateProfilActivity extends AppCompatActivity {

    FragmentProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_update_profil );


        binding.settings.setOnClickListener(v -> {
            Intent intent = new Intent( this,UpdateProfilActivity.class );
            startActivity( intent );
        });
    }

}
