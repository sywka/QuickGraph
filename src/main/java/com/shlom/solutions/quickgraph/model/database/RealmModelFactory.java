package com.shlom.solutions.quickgraph.model.database;

import com.shlom.solutions.quickgraph.model.database.model.AxisParamsModel;
import com.shlom.solutions.quickgraph.model.database.model.GraphParamsModel;
import com.shlom.solutions.quickgraph.model.database.model.LineParamsModel;
import com.shlom.solutions.quickgraph.model.database.model.ProjectModel;

import io.realm.Realm;

public abstract class RealmModelFactory {

    public static ProjectModel newProject(Realm realm, String name) {
        DataBaseManager dataBaseManager = new DataBaseManager(realm);

        LineParamsModel gridXLineParamsModel = new LineParamsModel()
                .setUid(dataBaseManager.generateUID(LineParamsModel.class))
                .copyToRealm(realm);

        LineParamsModel gridYLineParamsModel = new LineParamsModel()
                .setUid(dataBaseManager.generateUID(LineParamsModel.class))
                .copyToRealm(realm);

        LineParamsModel axisXLineParamsModel = new LineParamsModel()
                .setUid(dataBaseManager.generateUID(LineParamsModel.class))
                .copyToRealm(realm);

        LineParamsModel axisYLineParamsModel = new LineParamsModel()
                .setUid(dataBaseManager.generateUID(LineParamsModel.class))
                .copyToRealm(realm);

        AxisParamsModel axisXParamsModel = new AxisParamsModel()
                .setUid(dataBaseManager.generateUID(AxisParamsModel.class))
                .setTitle("X")
                .setLineParams(axisXLineParamsModel)
                .setGridLineParams(gridXLineParamsModel)
                .copyToRealm(realm);

        AxisParamsModel axisYParamsModel = new AxisParamsModel()
                .setUid(dataBaseManager.generateUID(AxisParamsModel.class))
                .setTitle("Y")
                .setLineParams(axisYLineParamsModel)
                .setGridLineParams(gridYLineParamsModel)
                .copyToRealm(realm);

        GraphParamsModel graphParamsModel = new GraphParamsModel()
                .setUid(dataBaseManager.generateUID(GraphParamsModel.class))
                .setAxisXParams(axisXParamsModel)
                .setAxisYParams(axisYParamsModel)
                .copyToRealm(realm);

        return new ProjectModel()
                .setUid(dataBaseManager.generateUID(ProjectModel.class))
                .setName(name)
                .setParams(graphParamsModel)
                .copyToRealm(realm);
    }
}
