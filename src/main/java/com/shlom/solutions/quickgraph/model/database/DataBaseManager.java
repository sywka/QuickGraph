package com.shlom.solutions.quickgraph.model.database;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.shlom.solutions.quickgraph.etc.LogUtil;
import com.shlom.solutions.quickgraph.model.database.model.DataSetModel;
import com.shlom.solutions.quickgraph.model.database.model.ProjectModel;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class DataBaseManager {

    private final Realm realm;

    public DataBaseManager() {
        realm = Realm.getDefaultInstance();
    }

    public DataBaseManager(Realm realm) {
        this.realm = realm;
    }

    public static void execute(@NonNull Executor executor) {
        DataBaseManager dataBaseManager = new DataBaseManager();
        try {
            executor.execute(dataBaseManager);
        } catch (Exception e) {
            LogUtil.d(e);
        }
        dataBaseManager.closeRealm();
    }

    public static void executeTrans(Realm.Transaction transaction) {
        execute(realmHelper -> realmHelper.executeTransaction(transaction));
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

    public void executeTransaction(Realm.Transaction transaction) {
        realm.executeTransaction(transaction);
    }

    public void beginTransaction() {
        realm.beginTransaction();
    }

    public void commitTransaction() {
        realm.commitTransaction();
    }

    public void cancelTransaction() {
        realm.cancelTransaction();
    }

    @Nullable
    public <T extends RealmObject & ObjectWithUID> T findObject(Class<T> cl, long uid) {
        return findObject(realm.where(cl), uid);
    }

    @Nullable
    public <T extends RealmModel & ObjectWithUID> T findObject(OrderedRealmCollection<T> collection,
                                                               long uid) {
        return findObject(collection.where(), uid);
    }

    private <T extends RealmModel & ObjectWithUID> T findObject(RealmQuery<T> query, long uid) {
        return query.equalTo("uid", uid).findFirst();
    }

    public <T extends RealmObject & ObjectWithUID> T findObjectAsync(Class<T> cl, long uid) {
        return realm.where(cl).equalTo("uid", uid).findFirstAsync();
    }

    public <T extends RealmObject & ObjectWithUID> long generateUID(Class<T> cl) {
        Number max = realm.where(cl).max("uid");
        if (max == null) return 0;
        return max.longValue() + 1;
    }

    public void clear() {
        realm.deleteAll();
    }

    public RealmResults<ProjectModel> getProjects() {
        return realm.where(ProjectModel.class).findAllSorted("date", Sort.DESCENDING);
    }

    public interface Executor {
        void execute(DataBaseManager dataBaseManager);
    }
}
