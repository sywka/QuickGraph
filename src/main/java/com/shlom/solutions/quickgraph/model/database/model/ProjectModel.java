package com.shlom.solutions.quickgraph.model.database.model;

import android.content.Context;

import com.shlom.solutions.quickgraph.model.database.ObjectWithUID;
import com.shlom.solutions.quickgraph.etc.FileCacheHelper;
import com.shlom.solutions.quickgraph.etc.LogUtil;

import java.io.Serializable;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class ProjectModel extends RealmObject implements ObjectWithUID, Serializable {

    @PrimaryKey
    private long uid;

    @Required
    private String name;
    @Required
    private Date date;

    private String previewFileName;

    private GraphParamsModel params;

    private RealmList<DataSetModel> dataSets;

    public ProjectModel() {
        name = "Project";
        date = new Date();
        dataSets = new RealmList<>();
    }

    public ProjectModel copyToRealm(Realm realm) {
        return realm.copyToRealm(this);
    }

    public ProjectModel copyToRealmOrUpdate(Realm realm) {
        return realm.copyToRealmOrUpdate(this);
    }

    public void deleteDependentsFromRealm(Context context) {
        if (params != null) {
            params.deleteDependentsFromRealm();
            params.deleteFromRealm();
        }
        if (dataSets != null) {
            for (DataSetModel dataSetModel : dataSets) {
                dataSetModel.deleteDependentsFromRealm();
            }
            dataSets.deleteAllFromRealm();
        }
        if (previewFileName != null) {
            LogUtil.d(FileCacheHelper.getImageCache(context, previewFileName).delete());
        }
    }

    public ProjectModel addDataSet(int position, DataSetModel dataSet) {
        if (dataSets == null) dataSets = new RealmList<>();
        dataSets.add(position, dataSet);
        return this;
    }

    public ProjectModel addDataSet(DataSetModel dataSet) {
        addDataSet(dataSets.size(), dataSet);
        return this;
    }

    // generated getters and setters

    @Override
    public long getUid() {
        return uid;
    }

    public ProjectModel setUid(long uid) {
        this.uid = uid;
        return this;
    }

    public String getName() {
        return name;
    }

    public ProjectModel setName(String name) {
        this.name = name;
        return this;
    }

    public Date getDate() {
        return date;
    }

    public ProjectModel setDate(Date date) {
        this.date = date;
        return this;
    }

    public String getPreviewFileName() {
        return previewFileName;
    }

    public ProjectModel setPreviewFileName(String previewFileName) {
        this.previewFileName = previewFileName;
        return this;
    }

    public RealmList<DataSetModel> getDataSets() {
        return dataSets;
    }

    public ProjectModel setDataSets(RealmList<DataSetModel> dataSets) {
        this.dataSets = dataSets;
        return this;
    }

    public GraphParamsModel getParams() {
        return params;
    }

    public ProjectModel setParams(GraphParamsModel params) {
        this.params = params;
        return this;
    }
}
