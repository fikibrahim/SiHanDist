package com.haerul.sihandist.di.builder;

import com.haerul.sihandist.ui.MainActivity;
import com.haerul.sihandist.ui.c4a.C4aFragment;
import com.haerul.sihandist.ui.c4a.add.AddC4AActivity;
import com.haerul.sihandist.ui.gangguan.BaseGangguanFragment;
import com.haerul.sihandist.ui.gangguan.GangguanFragment;
import com.haerul.sihandist.ui.gangguan.tindak_lanjut.GangguanTLActivity;
import com.haerul.sihandist.ui.home.HomeFragment;
import com.haerul.sihandist.ui.inspeksi.BaseInspeksiFragment;
import com.haerul.sihandist.ui.inspeksi.InspeksiFragment;
import com.haerul.sihandist.ui.inspeksi.add.AddInspeksiActivity;
import com.haerul.sihandist.ui.inspeksi.tindak_lanjut.TLActivity;
import com.haerul.sihandist.ui.inspeksi.tindak_lanjut.TLInfoFragment;
import com.haerul.sihandist.ui.inspeksi.tindak_lanjut.TLUpdateFragment;
import com.haerul.sihandist.ui.log_c4a.LogC4aActivity;
import com.haerul.sihandist.ui.log_inspeksi.LogInsActivity;
import com.haerul.sihandist.ui.login.LoginActivity;
import com.haerul.sihandist.ui.profile.ProfileFragment;
import com.haerul.sihandist.ui.log_gangguan.LogGgnActivity;
import com.haerul.sihandist.utils.CameraActivity;
import com.haerul.sihandist.utils.CameraXActivity;
import com.haerul.sihandist.utils.MapActivity;
import com.haerul.sihandist.utils.SQLiteDownloaderActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuilder {
    
    @ContributesAndroidInjector
    abstract LoginActivity loginActivity();
    
    @ContributesAndroidInjector
    abstract MainActivity mainActivity();
    
    @ContributesAndroidInjector
    abstract SQLiteDownloaderActivity sqLiteDownloaderActivity();
    
    @ContributesAndroidInjector
    abstract AddInspeksiActivity addFragment();
    
    @ContributesAndroidInjector
    abstract MapActivity mapActivity();
    
    @ContributesAndroidInjector
    abstract CameraActivity cameraActivity();
    
    @ContributesAndroidInjector
    abstract CameraXActivity cameraXActivity();
    
    @ContributesAndroidInjector
    abstract InspeksiFragment inspeksiFragment();
    
    @ContributesAndroidInjector
    abstract BaseInspeksiFragment baseInspeksiFragment();
    
    @ContributesAndroidInjector
    abstract TLActivity tlActivity();
    
    @ContributesAndroidInjector
    abstract TLInfoFragment tlInfoFragment();
    
    @ContributesAndroidInjector
    abstract HomeFragment homeFragment();
    
    @ContributesAndroidInjector
    abstract ProfileFragment profileFragment();
    
    @ContributesAndroidInjector
    abstract TLUpdateFragment tlUpdateFragment();
    
    @ContributesAndroidInjector
    abstract LogInsActivity logInsFragment();
    
    @ContributesAndroidInjector
    abstract BaseGangguanFragment baseGangguanFragment();
    
    @ContributesAndroidInjector
    abstract GangguanFragment gangguanFragment();
    
    @ContributesAndroidInjector
    abstract GangguanTLActivity gangguanTLActivityO();
    
    @ContributesAndroidInjector
    abstract C4aFragment c4aFragment();
    
    @ContributesAndroidInjector
    abstract AddC4AActivity addC4AActivity();
    
    @ContributesAndroidInjector
    abstract LogGgnActivity logGgnFragment();
    
    @ContributesAndroidInjector
    abstract LogC4aActivity logC4aFragment();
}
