package com.shlom.solutions.quickgraph;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.shlom.solutions.quickgraph.etc.Config;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder()
                .schemaVersion(Config.DATA_BASE_VERSION)
                .deleteRealmIfMigrationNeeded()
//                .migration(new DataBaseMigration())
                .build());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        MultiDex.install(this);
    }
}
