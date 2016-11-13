package com.shlom.solutions.quickgraph.database;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.shlom.solutions.quickgraph.etc.LogUtil;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;

public class RealmHelper {

    private final Realm realm;

    public RealmHelper() {
        realm = Realm.getDefaultInstance();
    }

    public RealmHelper(Realm realm) {
        this.realm = realm;
    }

    public static void execute(@NonNull Executor executor) {
        RealmHelper realmHelper = new RealmHelper();
        try {
            executor.execute(realmHelper);
        } catch (Exception e) {
            LogUtil.d(e);
        }
        realmHelper.closeRealm();
    }

    public Realm getRealm() {
        return realm;
    }

    public void closeRealm() {
        realm.close();
    }

    public boolean realmIsClosed() {
        return realm.isClosed();
    }

    @Nullable
    public <T extends RealmObject & ObjectWithUID> T findObject(Class<T> cl, long uid) {
        return realm.where(cl).equalTo("uid", uid).findFirst();
    }

    public <T extends RealmObject & ObjectWithUID> T findObjectAsync(Class<T> cl, long uid) {
        return realm.where(cl).equalTo("uid", uid).findFirstAsync();
    }

    public <T extends RealmObject> RealmResults<T> findResults(Class<T> cl, Sort sort) {
        return realm.where(cl).findAllSorted("date", sort);
    }

    public <T extends RealmObject> RealmResults<T> findResultsAsync(Class<T> cl, Sort sort) {
        return realm.where(cl).findAllSortedAsync("date", sort);
    }

    public <T extends RealmObject & ObjectWithUID> long generateUID(Class<T> cl) {
        Number max = realm.where(cl).max("uid");
        if (max == null) return 0;
        return max.longValue() + 1;
    }

    public void clear() {
        realm.deleteAll();
    }

    public interface Executor {
        void execute(RealmHelper realmHelper);
    }
}
