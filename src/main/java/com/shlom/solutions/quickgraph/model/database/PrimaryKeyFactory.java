package com.shlom.solutions.quickgraph.model.database;

import com.shlom.solutions.quickgraph.etc.LogUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmModel;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class PrimaryKeyFactory {

    private static final String PRIMARY_KEY_FIELD = "uid";

    private final static PrimaryKeyFactory instance = new PrimaryKeyFactory();

    private Map<Class<? extends RealmModel>, AtomicLong> keys;

    private PrimaryKeyFactory() {
    }

    public static PrimaryKeyFactory getInstance() {
        return instance;
    }

    public synchronized void init(final Realm realm) {
        if (keys != null) {
            throw new IllegalStateException("already initialized");
        }
        keys = new HashMap<>();
        final RealmConfiguration configuration = realm.getConfiguration();
        final RealmSchema realmSchema = realm.getSchema();
        for (final Class<? extends RealmModel> c : configuration.getRealmObjectClasses()) {

            final RealmObjectSchema objectSchema = realmSchema.get(c.getSimpleName());
            if (objectSchema != null && objectSchema.hasPrimaryKey()) {
                long maxKey = realm.where(c).max(PRIMARY_KEY_FIELD).longValue();
                keys.put(c, new AtomicLong(maxKey));
            }
        }
    }

    public synchronized long nextKey(final Class<? extends RealmModel> clazz) {
        if (keys == null) {
            throw new IllegalStateException("not initialized yet");
        }
        if (!isValidMethodCall()) return 0;

        AtomicLong l = keys.get(clazz);
        if (l == null) {
            LogUtil.d("There was no primary keys for " + clazz.getName());
            l = new AtomicLong(0);
            keys.put(clazz, l);
        }
        return l.incrementAndGet();
    }

    private boolean isValidMethodCall() {

        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

        for (StackTraceElement stackTraceElement : stackTraceElements) {

            if (stackTraceElement.getMethodName().equals("newInstance")) {
                return false;
            }
        }
        return true;
    }
}
