package com.shlom.solutions.quickgraph.etc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;

import com.shlom.solutions.quickgraph.App;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileCacheHelper {

    private static final Context context = App.getContext();

    public static File getCacheDir() {
        return context.getCacheDir();
    }

    public static File getImageCacheDir() {
        File imageCacheDir = new File(getCacheDir(), "images");
        imageCacheDir.mkdir();
        return imageCacheDir;
    }

    public static File getImageCache(String name) {
        if (name == null) name = "";
        return new File(getImageCacheDir(), name + ".png");
    }

    @Nullable
    public static Bitmap getImageFromCache(String name) {
        File cacheFile = getImageCache(name);
        if (cacheFile.exists()) {
            return BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
        }
        return null;
    }

    public static boolean putImageToCache(String name, @Nullable Bitmap bitmap) {
        File cacheFile = getImageCache(name);
        try {
            if (bitmap == null) return cacheFile.delete();
            if (cacheFile.exists() && !cacheFile.delete()) return false;
            if (!cacheFile.createNewFile()) return false;

            FileOutputStream fos = new FileOutputStream(cacheFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

            fos.flush();
            fos.close();
            return true;
        } catch (IOException e) {
            LogUtil.d("Error when saving image to cache. ", e);
            return false;
        }
    }
}
