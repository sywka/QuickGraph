package com.shlom.solutions.quickgraph.viewmodel;

import android.databinding.Bindable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

public interface ICardPreviewEditableViewModel extends IListItemViewModel {

    void setPrimaryText(String primaryText);

    @Bindable
    long getCount();

    @Bindable
    Uri getImageUri();

    @Bindable
    Drawable getPlaceHolder();

    @Bindable
    Drawable getErrorImage();
}
