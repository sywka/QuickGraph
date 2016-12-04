package com.shlom.solutions.quickgraph.model.database;

import com.shlom.solutions.quickgraph.etc.LogUtil;

import io.realm.Realm;

public class RealmHelper {

    public static void executeTrans(Realm.Transaction transaction) {
        Realm realm = Realm.getDefaultInstance();
        try {
            realm.executeTransaction(transaction);
        } catch (Exception e) {
            LogUtil.e(e);
        }
        realm.close();
    }

    public static void execute(Executor executor) {
        Realm realm = Realm.getDefaultInstance();
        try {
            executor.execute(realm);
        } catch (Exception e) {
            LogUtil.d(e);
        }
        realm.close();
    }

    public interface Executor {
        void execute(Realm realm);
    }
}
