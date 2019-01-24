package com.blackweather.android.utilities;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

public class ToastUtils {
    private static ToastUtils instance;
    private Toast toast;
    private View view;

    private ToastUtils(Context context) {
        toast = new Toast(context);
        view = Toast.makeText(context, "", Toast.LENGTH_SHORT).getView();
        toast.setView(view);
    }

    public static ToastUtils getInstance(Context context) {
        if (instance == null) {
            instance = new ToastUtils(context);
        }
        return instance;
    }

    public void show(String msg) {
        toast.setText(msg);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }
}

