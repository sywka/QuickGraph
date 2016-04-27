package com.shlom.solutions.quickgraph.model;

import com.shlom.solutions.quickgraph.App;
import com.shlom.solutions.quickgraph.R;

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

    private byte[] preview;

    private GraphParamsModel params;

    private RealmList<DataSetModel> dataSets;

    public ProjectModel() {
        name = App.getContext().getString(R.string.action_demo_project);
        date = new Date();
    }

    public ProjectModel copyToRealm(Realm realm) {
        return realm.copyToRealm(this);
    }

    public ProjectModel copyToRealmOrUpdate(Realm realm) {
        return realm.copyToRealmOrUpdate(this);
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

    @Override
    public void deleteFromRealm() {
        if (params != null) params.deleteFromRealm();
        dataSets.deleteAllFromRealm();
        super.deleteFromRealm();
    }

    // generated getters

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

    public byte[] getPreview() {
        return preview;
    }

    public ProjectModel setPreview(byte[] preview) {
        this.preview = preview;
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
