package com.blackweather.android.task;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.ImageView;

import com.blackweather.android.R;
import com.blackweather.android.utilities.NetworkUtils;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class BlackTask {

    private static final int LOAD_PIC_BOUND = 10;
    private static final int LOAD_PIC_ONE_PROBABILITY = 9;
    private static final int LOAD_PIC_TWO_PROBABILITY = 10;

    private static final String LOAD_PIC_URL = "http://guolin.tech/api/bing_pic";

    /**
     * 根据不同概率加载不同的背景图片
     */
    public static void loadBgPic(final Activity activity, final ImageView imageView) {
//        SharedPreferences sharedPreferences = PreferenceManager
//                .getDefaultSharedPreferences(activity);
//        String picStr = sharedPreferences.getString(activity.getString(R.string.pic_str_key)
//                , null);
//        if (picStr != null) {
            Random random = new Random();
            int randomInteger = random.nextInt(LOAD_PIC_BOUND);
            if (randomInteger <= LOAD_PIC_ONE_PROBABILITY) {
                Glide.with(activity).load(R.drawable.ic_bg_1).into(imageView);
            } else {
                Glide.with(activity).load(R.drawable.ic_bg_2).into(imageView);
            }

//                if (randomInteger <= LOAD_PIC_TWO_PROBABILITY){
//                Glide.with(activity).load(R.drawable.ic_bg_2).into(imageView);
//            }
//            else {
//                Glide.with(activity).load(picStr).into(imageView);
//            }
//        } else {
//            loadBingPic(activity, imageView);
//        }
    }


    /**
     * 加载背景图片
     */
    private static void loadBingPic(final Activity activity, final ImageView imageView) {
        NetworkUtils.sendOkHttpRequest(LOAD_PIC_URL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String picStr = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(activity).edit();
                editor.putString(activity.getString(R.string.pic_str_key), picStr);
                editor.apply();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(activity).load(picStr).into(imageView);
                    }
                });
            }
        });
    }

}
