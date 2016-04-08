package com.lom.quickgraph.etc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.lom.quickgraph.activity.BaseActivity;
import com.lom.quickgraph.model.CoordinateModel;

import org.mariuszgromada.math.mxparser.Expression;

import io.realm.RealmList;

public abstract class Utils {

    public static <T extends BaseActivity> Intent putLong(Context context, Class<T> cl, long uid) {
        Intent intent = new Intent(context, cl);
        Bundle bundle = new Bundle();
        bundle.putLong(Config.TAG_LONG, uid);
        intent.putExtras(bundle);
        return intent;
    }

    public static <T extends BaseActivity> long getLong(T activity) {
        return activity.getIntent().getExtras().getLong(Config.TAG_LONG, -1);
    }

    public static <T extends Fragment> T putLong(T fragment, long uid) {
        Bundle bundle = new Bundle();
        bundle.putLong(Config.TAG_LONG, uid);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static long getLong(Fragment fragment) {
        return fragment.getArguments().getLong(Config.TAG_LONG, -1);
    }

    public static boolean checkSyntaxExpression(String exp) {
        Expression expression = new Expression(exp);
        expression.defineArgument("x", 0);
        return expression.checkSyntax();
    }

    public static RealmList<CoordinateModel> generateCoordinates(RealmHelper realmHelper, String function, Float start, Float end, Float delta) {
        if (start == null || end == null || delta == null) return new RealmList<>();

        long uid = realmHelper.generateUID(CoordinateModel.class);

        RealmList<CoordinateModel> coordinateModels = new RealmList<>();
        Expression expression = new Expression(function);
        expression.defineArgument("x", 0);
        for (float i = start; i <= end; i += delta) {
            expression.setArgumentValue("x", i);
            coordinateModels.add(new CoordinateModel()
                    .setUid(uid)
                    .setX(i)
                    .setY((float) expression.calculate())
            );
            uid++;
        }
        return coordinateModels;
    }
}
