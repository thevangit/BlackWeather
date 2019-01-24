package com.blackweather.android;

import android.content.Intent;
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

import com.blackweather.android.adapters.MainRecyclerAdapter;
import com.blackweather.android.gson.Weather;
import com.blackweather.android.utilities.JsonUtils;
import com.blackweather.android.utilities.NetworkUtils;
import com.blackweather.android.utilities.ToastUtils;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomeFragment extends Fragment implements Serializable,
        SharedPreferences.OnSharedPreferenceChangeListener,
        MainRecyclerAdapter.OnItemClickListener{

    private static final String TAG = HomeFragment.class.getSimpleName();
    public static final String BUNDLE_WEATHER_ID_KEY = "bundle_weather_id";

    private String mWeatherId;
    private RecyclerView mHomeRecyclerView;
    private MainRecyclerAdapter mAdapter = new
            MainRecyclerAdapter(this);
    private SwipeRefreshLayout mRefreshLayout;

    /**
     * 利用静态方法构建对象以使用Bundle（捆绑）来传递数据
     *
     * @param dataStr 字符串数据
     * @return 返回实例
     */
    public static HomeFragment newInstance(String dataStr) {
        HomeFragment blackHomeFragment = new HomeFragment();
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
        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .registerOnSharedPreferenceChangeListener(this);
        Log.d(TAG, "debug onCreate: bundlweatherid: " + args.getString(BUNDLE_WEATHER_ID_KEY));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

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

    /**
     * 根据天气id请求城市天气数据
     */
    private void requestWeather(final String weatherId) throws MalformedURLException {
        if (weatherId == null) {
            ToastUtils.getInstance(BlackApplication.getContext())
                    .show("获取天气信息失败 error:201");
//            Toast.makeText(getActivity(), "获取天气信息失败h101",
//                    Toast.LENGTH_SHORT).show();
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
                        ToastUtils.getInstance(BlackApplication.getContext())
                                .show("获取天气信息失败 error:202");
//                        Toast.makeText(BlackApplication.getContext(), "获取天气信息失败h102",
//                                Toast.LENGTH_SHORT).show();
                        mRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.body() == null) {
                    ToastUtils.getInstance(BlackApplication.getContext())
                            .show("获取天气信息失败 error:203");
//                    Toast.makeText(BlackApplication.getContext(), "获取天气信息失败h201", Toast.LENGTH_SHORT).show();
                    return;
                }
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
                            ToastUtils.getInstance(BlackApplication.getContext())
                                    .show("获取天气信息失败 error:204");
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "settings onSharedPreferenceChanged: 执行");
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 实现点击recycler view中的item时的响应
     * @param position
     */
    @Override
    public void onItemClickListener(int position) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra("weatherId_key", mWeatherId);
        intent.putExtra("day_num_key", position);
        startActivity(intent);
    }

}
