package com.shlom.solutions.quickgraph.model.asynctask;

import android.content.Context;
import android.support.annotation.ColorInt;

import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.model.database.DataBaseManager;
import com.shlom.solutions.quickgraph.model.database.RealmModelFactory;
import com.shlom.solutions.quickgraph.model.database.model.DataSetModel;
import com.shlom.solutions.quickgraph.model.database.model.FunctionRangeModel;
import com.shlom.solutions.quickgraph.model.database.model.ProjectModel;
import com.shlom.solutions.quickgraph.etc.Utils;

public class DemoGenerator extends ProgressAsyncTaskLoader<ProgressParams, Void> {

    public DemoGenerator(Context context) {
        super(context);
    }

    @Override
    protected void onReset() {
        super.onReset();

        cancelLoad();
    }

    @Override
    public Void loadInBackground() {
        DataBaseManager dataBaseManager = new DataBaseManager();
        dataBaseManager.getRealm().executeTransaction(realm -> {
            String description = getContext().getString(R.string.project_generate_demo);
            ProgressParams progressParams = new ProgressParams(0, 10, description);

            String name = getContext().getString(R.string.action_demo_project);
            ProjectModel projectModel = RealmModelFactory.newProject(realm, name);

            int a = 1;
            @ColorInt int color = -20000;
            while (progressParams.getProgress() < progressParams.getTotal()) {
                a += progressParams.getProgress();
                color -= 1000000;

                FunctionRangeModel functionRangeModel = new FunctionRangeModel()
                        .setUid(dataBaseManager.generateUID(FunctionRangeModel.class))
                        .setFrom(-10f)
                        .setTo(10f)
                        .setDelta(0.5f)
                        .copyToRealm(realm);

                String function = a + " + x^2";
                DataSetModel dataSetModel = new DataSetModel()
                        .setUid(dataBaseManager.generateUID(DataSetModel.class))
                        .setPrimary(getContext().getString(R.string.data_set))
                        .setSecondary(function)
                        .setColor(color)
                        .setType(DataSetModel.Type.FROM_FUNCTION)
                        .setFunctionRange(functionRangeModel)
                        .setCoordinates(Utils.generateCoordinates(dataBaseManager,
                                function,
                                functionRangeModel.getFrom(),
                                functionRangeModel.getTo(),
                                functionRangeModel.getDelta()))
                        .copyToRealm(realm);

                dataSetModel.setPrimary(dataSetModel.getPrimary() + " №" +
                        (projectModel.getDataSets().size() + 1));

                projectModel.addDataSet(0, dataSetModel);

                if (isLoadInBackgroundCanceled()) throw new RuntimeException("Canceled");
                progressParams.increment();
                publishProgress(progressParams);
            }
        });
        dataBaseManager.closeRealm();
        return null;
    }
}