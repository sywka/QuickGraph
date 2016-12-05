package com.shlom.solutions.quickgraph.model.asynctask;

import android.content.Context;
import android.support.annotation.ColorInt;

import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.etc.Utils;
import com.shlom.solutions.quickgraph.model.database.RealmHelper;
import com.shlom.solutions.quickgraph.model.database.dbmodel.DataSetModel;
import com.shlom.solutions.quickgraph.model.database.dbmodel.FunctionRangeModel;
import com.shlom.solutions.quickgraph.model.database.dbmodel.ProjectModel;
import com.shlom.solutions.quickgraph.model.database.dbmodel.UserModel;

import io.realm.RealmList;

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
        RealmHelper.executeTransaction(realm -> {
            String description = getContext().getString(R.string.project_generate_demo);
            ProgressParams progressParams = new ProgressParams(0, 10, description);

            RealmList<DataSetModel> dataSetModels = new RealmList<>();
            int a = 1;
            @ColorInt int color = -20000;
            while (progressParams.getProgress() < progressParams.getTotal()) {
                a += progressParams.getProgress();
                color -= 1000000;

                FunctionRangeModel functionRangeModel = new FunctionRangeModel()
                        .initDefault()
                        .setFrom(-10f)
                        .setTo(10f)
                        .setDelta(0.5f);

                String function = a + " + x^2";
                DataSetModel dataSetModel = new DataSetModel()
                        .initDefault()
                        .setPrimary(getContext().getString(R.string.data_set))
                        .setSecondary(function)
                        .setColor(color)
                        .setType(DataSetModel.Type.FROM_FUNCTION)
                        .setFunctionRange(functionRangeModel)
                        .setCoordinates(Utils.generateCoordinates(
                                function,
                                functionRangeModel.getFrom(),
                                functionRangeModel.getTo(),
                                functionRangeModel.getDelta()));

                dataSetModel.setPrimary(dataSetModel.getPrimary() + " №" +
                        (dataSetModels.size() + 1));

                dataSetModels.add(0, dataSetModel);

                if (isLoadInBackgroundCanceled()) throw new RuntimeException("Canceled");
                progressParams.increment();
                publishProgress(progressParams);
            }

            String name = getContext().getString(R.string.action_demo_project);
            UserModel.createOrGetFirst(realm)
                    .addProject(new ProjectModel()
                            .initDefault()
                            .setName(name)
                            .setDataSets(dataSetModels)
                            .updateUIDCascade()
                    );
        });
        return null;
    }
}
