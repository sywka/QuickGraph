package com.shlom.solutions.quickgraph.model.database.dbmodel;

import com.annimon.stream.Stream;
import com.shlom.solutions.quickgraph.model.database.interfaces.DBModel;
import com.shlom.solutions.quickgraph.model.database.PrimaryKeyFactory;

import java.io.Serializable;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class ProjectModel extends RealmObject
        implements DBModel<ProjectModel>, Serializable {

    @PrimaryKey
    private long uid;

    @Required
    private Date date;
    @Required
    private String name;
    private String previewFileName;

    private GraphParamsModel params;
    private RealmList<DataSetModel> dataSets;

    public ProjectModel() {
    }

    public static ProjectModel find(Realm realm, long uid) {
        return realm.where(ProjectModel.class).equalTo("uid", uid).findFirst();
    }

    public ProjectModel copyToRealm(Realm realm) {
        return realm.copyToRealm(this);
    }

    public ProjectModel copyToRealmOrUpdate(Realm realm) {
        return realm.copyToRealmOrUpdate(this);
    }

    @Override
    public void deleteCascade() {
        deleteDependents();
        deleteFromRealm();
    }

    @Override
    public void deleteDependents() {
        if (params != null) {
            params.deleteCascade();
        }
        if (dataSets != null) {
            Stream.of(dataSets).forEach(DataSetModel::deleteDependents);
            dataSets.deleteAllFromRealm();
        }
    }

    @Override
    public ProjectModel updateUIDCascade() {
        uid = PrimaryKeyFactory.getInstance().nextKey(ProjectModel.class);
        params.updateUIDCascade();
        Stream.of(dataSets).forEach(DataSetModel::updateUIDCascade);
        return this;
    }

    @Override
    public ProjectModel initDefault() {
        name = "Project";
        date = new Date();
        dataSets = new RealmList<>();
        params = new GraphParamsModel().initDefault();
        return this;
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
