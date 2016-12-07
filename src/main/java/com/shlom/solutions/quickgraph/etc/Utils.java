package com.shlom.solutions.quickgraph.etc;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;

import com.shlom.solutions.quickgraph.model.database.dbmodel.CoordinateModel;

import org.mariuszgromada.math.mxparser.Expression;

import java.io.Serializable;

import io.realm.RealmList;

public abstract class Utils {

    public static int calculateProgress(float currentValue, float minValue, float maxValue, int maxProgress) {
        return (int) ((currentValue - minValue) * (float) maxProgress / (maxValue - minValue));
    }

    public static float calculateValue(int currentProgress, float minValue, float maxValue, int maxProgress) {
        return minValue + (float) currentProgress * (maxValue - minValue) / (float) maxProgress;
    }

    public static void createPreview(Bitmap bitmap, View view) {
        view.layout(0, 0, bitmap.getWidth(), bitmap.getHeight());
        view.draw(new Canvas(bitmap));
        view.requestLayout();
    }

    public static int dpToPx(Context context, float dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int px = (int) (dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        return px == 0 ? 1 : px;
    }

    public static float pxToDp(Context context, int px) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public static Intent putLong(Intent intent, long uid) {
        Bundle bundle = new Bundle();
        bundle.putLong(Config.TAG_LONG, uid);
        intent.putExtras(bundle);
        return intent;
    }

    public static Intent putSerializable(Intent intent, Serializable serializable) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Config.TAG_SERIALIZABLE, serializable);
        intent.putExtras(bundle);
        return intent;
    }

    public static long getLong(Intent intent) {
        return intent.getExtras().getLong(Config.TAG_LONG, -1);
    }

    public static <T extends FragmentActivity> long getLong(T activity) {
        return getLong(activity.getIntent());
    }

    public static <T extends FragmentActivity> Serializable getSerializable(T activity) {
        return activity.getIntent().getExtras().getSerializable(Config.TAG_SERIALIZABLE);
    }

    public static <T extends Fragment> T putLong(T fragment, long uid) {
        Bundle bundle = new Bundle();
        if (fragment.getArguments() != null) bundle = fragment.getArguments();
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

    public static RealmList<CoordinateModel> generateCoordinates(String function, Float start, Float end, Float delta) {
        if (start == null || end == null || delta == null) return new RealmList<>();

        RealmList<CoordinateModel> coordinateModels = new RealmList<>();
        Expression expression = new Expression(function);
        expression.defineArgument("x", 0);
        for (float i = start; i <= end; i += delta) {
            expression.setArgumentValue("x", i);
            coordinateModels.add(new CoordinateModel()
                    .initDefault()
                    .setX(i)
                    .setY((float) expression.calculate())
            );
        }
        return coordinateModels;
    }
}
