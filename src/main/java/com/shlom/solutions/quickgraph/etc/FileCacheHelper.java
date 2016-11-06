package com.shlom.solutions.quickgraph.etc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileCacheHelper {

    public static File getCacheDir(Context context) {
        return context.getCacheDir();
    }

    public static File getImageCacheDir(Context context) {
        File imageCacheDir = new File(getCacheDir(context), "images");
        imageCacheDir.mkdir();
        return imageCacheDir;
    }

    public static File getImageCache(Context context, String name) {
        if (name == null) name = "";
        return new File(getImageCacheDir(context), name + ".png");
    }

    @Nullable
    public static Bitmap getImageFromCache(Context context, String name) {
        File cacheFile = getImageCache(context, name);
        if (cacheFile.exists()) {
            return BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
        }
        return null;
    }

    public static boolean putImageToCache(Context context, String name, @Nullable Bitmap bitmap) {
        File cacheFile = getImageCache(context, name);
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
