package com.shlom.solutions.quickgraph;

import android.app.Application;
import android.content.Context;

import com.shlom.solutions.quickgraph.etc.Config;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class App extends Application {

    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        Realm.setDefaultConfiguration(new RealmConfiguration.Builder(getApplicationContext())
                .schemaVersion(Config.DATA_BASE_VERSION)
                .deleteRealmIfMigrationNeeded()
//                .migration(new DataBaseMigration())
                .build());
    }
}
