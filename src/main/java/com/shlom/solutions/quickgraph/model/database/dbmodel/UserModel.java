package com.shlom.solutions.quickgraph.model.database.dbmodel;

import com.annimon.stream.Stream;
import com.shlom.solutions.quickgraph.model.database.PrimaryKeyFactory;
import com.shlom.solutions.quickgraph.model.database.interfaces.DBModel;

import java.io.Serializable;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.annotations.PrimaryKey;

public class UserModel extends RealmObject
        implements DBModel<UserModel>, Serializable {

    @PrimaryKey
    private long uid;

    private RealmList<ProjectModel> projects;

    public UserModel() {
    }

    public static UserModel createOrGetFirst(Realm realm) {
        UserModel userModel = realm.where(UserModel.class).findFirst();
        if (userModel == null) {
            boolean withTransaction = !realm.isInTransaction();
            if (withTransaction) realm.beginTransaction();
            userModel = new UserModel()
                    .initDefault()
                    .updateUIDCascade()
                    .copyToRealm(realm);
            if (withTransaction) realm.commitTransaction();
        }
        return userModel;
    }

    public UserModel copyToRealm(Realm realm) {
        return realm.copyToRealm(this);
    }

    public UserModel copyToRealmOrUpdate(Realm realm) {
        return realm.copyToRealmOrUpdate(this);
    }

    @Override
    public void deleteCascade() {
        deleteDependents();
        deleteFromRealm();
    }

    @Override
    public void deleteDependents() {
        Stream.of(projects).forEach(ProjectModel::deleteDependents);
        projects.deleteAllFromRealm();
    }

    @Override
    public UserModel updateUIDCascade() {
        uid = PrimaryKeyFactory.getInstance().nextKey(UserModel.class);
        Stream.of(projects).forEach(ProjectModel::updateUIDCascade);
        return this;
    }

    @Override
    public UserModel initDefault() {
        projects = new RealmList<>();
        return this;
    }

    public RealmResults<ProjectModel> getOrderedProjects() {
        return projects.sort("date", Sort.DESCENDING);
    }

    public UserModel addProject(int position, ProjectModel projectModel) {
        if (projects == null) projects = new RealmList<>();
        projects.add(position, projectModel);
        return this;
    }

    public UserModel addProject(ProjectModel projectModel) {
        addProject(projects.size(), projectModel);
        return this;
    }

    // generated getters and setters

    @Override
    public long getUid() {
        return uid;
    }

    public UserModel setUid(long uid) {
        this.uid = uid;
        return this;
    }

    public RealmList<ProjectModel> getProjects() {
        return projects;
    }

    public void setProjects(RealmList<ProjectModel> projects) {
        this.projects = projects;
    }
}
