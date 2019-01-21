package com.blackweather.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blackweather.android.gson.Weather;
import com.blackweather.android.utilities.JsonUtils;
import com.blackweather.android.utilities.NetworkUtils;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FBlackHomeFragment extends Fragment implements Serializable {

    private static final String TAG = FBlackHomeFragment.class.getSimpleName();
    public static final String BUNDLE_WEATHER_ID_KEY = "bundle_weather_id";

    private String mWeatherId;
    private RecyclerView mHomeRecyclerView;
    private ABlackRecycleHomeAdapter mAdapter = new ABlackRecycleHomeAdapter();
    private SwipeRefreshLayout mRefreshLayout;

    /**
     * 利用静态方法构建对象以使用Bundle（捆绑）来传递数据
     *
     * @param dataStr 字符串数据
     * @return 返回实例
     */
    public static FBlackHomeFragment newInstance(String dataStr) {
        FBlackHomeFragment blackHomeFragment = new FBlackHomeFragment();
        Bundle args = new Bundle();
        args.putString(BUNDLE_WEATHER_ID_KEY, dataStr);
        blackHomeFragment.setArguments(args);

        Log.d(TAG, "debug newInstance: dataStr: " + dataStr);

        return blackHomeFragment;
    }


    /**
     * 在onCrate()中获取data而不在onCreateView()中获取，因为onCreateView不一定会被调用。
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mWeatherId = args.getString(BUNDLE_WEATHER_ID_KEY);
        }
        Log.d(TAG, "debug onCreate: bundlweatherid: " + args.getString(BUNDLE_WEATHER_ID_KEY));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle args = getArguments();
        if (args != null) {
            mWeatherId = args.getString(BUNDLE_WEATHER_ID_KEY);
        }
        Log.d(TAG, "onAttach: 执行" );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            mWeatherId = args.getString(BUNDLE_WEATHER_ID_KEY);
        }
        Log.d(TAG, "debug onCreateView: 执行");
        
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        // bind view
        mHomeRecyclerView = view.findViewById(R.id.home_recycler_view);
        mRefreshLayout = view.findViewById(R.id.swipe_refresh);
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        mHomeRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mHomeRecyclerView.setLayoutManager(layoutManager);

        // 优先从缓存中获取数据，否则请求网络
        String weatherStr = prefs.getString(mWeatherId, null);
        if (weatherStr != null) {
            Weather weather = JsonUtils.handleWeatherResponse(weatherStr);
            mAdapter.setData(weather);
        } else {
            try {
//                mWeatherId = getActivity().getIntent().getStringExtra("weather_id");
//            mWeatherLayout.setVisibility(View.INVISIBLE);
                requestWeather(mWeatherId);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    requestWeather(mWeatherId);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }
//        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                requestWeather(mWeatherId);
//            }
//        });
//        mNavButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mDrawerLayout.openDrawer(GravityCompat.START);
//            }
//        });
//        String bcPic = prefs.getString("bc_pic", null);
//        if (bcPic != null) {
//            Glide.with(this).load(bcPic).into(mBcPicImg);
//        } else {
//            loadBingPic();
//        }

    /**
     * 根据天气id请求城市天气数据
     */
    private void requestWeather(final String weatherId) throws MalformedURLException {
        if (weatherId == null) {
            Toast.makeText(getActivity(), "weatherId为空",
                    Toast.LENGTH_SHORT).show();
//            getActivity().sendWeatherId(String newWeatherId);
            return;
        }
        URL url = NetworkUtils.buildUrlWithWeatherId(weatherId);
        Log.i(TAG, "requestWeather: url = " + url);
        NetworkUtils.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "获取天气信息失败1",
                                Toast.LENGTH_SHORT).show();
                        mRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.body() == null) {
                    Toast.makeText(getActivity(), "response错误", Toast.LENGTH_SHORT).show();
                    return;
                };
                final String responseStr = response.body().string();
                final Weather weather = JsonUtils.handleWeatherResponse(responseStr);
                if (getActivity() == null) {return;}
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(getActivity())
                                    .edit();
                            editor.putString(weatherId, responseStr);
                            editor.apply();
                            mAdapter.setData(weather);
                        } else {
                            Toast.makeText(getActivity(), "获取天气信息失败3",
                                    Toast.LENGTH_SHORT).show();
                        }
                        mRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    public String getWeatherId() {
        return mWeatherId;
    }

//    /**
//     * 加载背景图片
//     */
//    private void loadBingPic() {
//        Glide.with(this).load(R.drawable.bg_1).into(mBcPicImg);
//    }
//
//    public DrawerLayout getDrawerLayout() {
//        return mDrawerLayout;
//    }
//
//    public SwipeRefreshLayout getSwipeRefresh() {
//        return mSwipeRefresh;
//    }

}
