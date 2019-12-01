package com.haerul.sihandist;

import android.app.Activity;
import android.app.Service;
import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.multidex.MultiDexApplication;

import com.facebook.stetho.Stetho;
import com.haerul.sihandist.di.component.AppComponent;
import com.haerul.sihandist.di.component.DaggerAppComponent;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasServiceInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class App extends MultiDexApplication implements HasActivityInjector, HasSupportFragmentInjector, HasServiceInjector {

    @Inject
    DispatchingAndroidInjector<Activity> activityDispatchingAndroidInjector;

    @Inject
    DispatchingAndroidInjector<Service> dispatchingAndroidInjectorService;

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;

    AppComponent appComponent;

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        createDaggerComponent().inject(this);
    }

    public void clearComponent() {
        appComponent = null;
    }

    public AppComponent createDaggerComponent() {
        if (appComponent == null)
            appComponent = DaggerAppComponent.builder()
                    .application(this)
                    .build();
        return appComponent;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return activityDispatchingAndroidInjector;
    }

    @Override
    public AndroidInjector<Service> serviceInjector() {
        return dispatchingAndroidInjectorService;
    }
}
    