package com.blackweather.android;

import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.blackweather.android.utilities.SharedPreferenceUtils;
import com.rd.PageIndicatorView;

import java.util.ArrayList;
import java.util.List;

/**
 * d90:Author:theVan
 */
public class BlackHomeActivity extends AppCompatActivity implements
        ViewPager.OnPageChangeListener {

    private static final int PAGE_LIMIT = 5;

    private String mHomePageWeatherId;
    private ViewPager mViewPager;
    private PageIndicatorView mPageIndicator;
    private FloatingActionButton mPlusFabButton;
    private BlackFragPagerAdapter mPagerAdapter;
    private Button mDeleteButton;
    private DrawerLayout mDrawerLayout;
    private Button mSwitchButton;

    private FrameLayout mStartFrame;
    private FrameLayout mEndFrame;
    private List<BlackHomeFragment> mFragments = new ArrayList<>();

//    private SharedPreferences mPrefs = PreferenceManager
//            .getDefaultSharedPreferences(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 将背景图和status bar融合在一起
        transStatusBar();
        setContentView(R.layout.activity_home);
        // bind views and set the widget
        mViewPager = findViewById(R.id.blackActivity_view_pager);
        mPageIndicator = findViewById(R.id.backActivity_page_indicator);
        mPlusFabButton = findViewById(R.id.backActivity_fab_button);
        mDeleteButton = findViewById(R.id.title_delete_button);
        mDrawerLayout = findViewById(R.id.blackActivity_drawer_layout);
        mSwitchButton = findViewById(R.id.titel_switch_button);
        mStartFrame = findViewById(R.id.start_frame_layout);
        mEndFrame = findViewById(R.id.end_frame_layout);
        mPagerAdapter = new BlackFragPagerAdapter(getSupportFragmentManager(), mFragments);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);
        // 初始化主页
        initMainPage();
        if (mViewPager.getCurrentItem() == 0) {
            mDeleteButton.setVisibility(View.GONE);
        } else {
            mDeleteButton.setVisibility(View.VISIBLE);
        }
        mPlusFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                }
                mDrawerLayout.openDrawer(GravityCompat.END);
            }
        });
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delFragment();
            }
        });
        mSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
                    mDrawerLayout.closeDrawer(GravityCompat.END);
                }
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

    }

    /**
     * 融合status bar
     */
    private void transStatusBar() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 初始化home page
     */
    private void initMainPage() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.start_frame_layout, BlackChooseFragment
                        .newInstance(BlackChooseFragment.STATE_REFRESH_PAGE)).commit();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.end_frame_layout, BlackChooseFragment
                        .newInstance(BlackChooseFragment.STATE_PLUS_PAGE)).commit();
        // 首次运行，设置首页的偏好的地方
        if (getIntent().hasExtra("weather_id")) {
            mHomePageWeatherId = getIntent().getStringExtra("weather_id");
            addFragment(BlackHomeFragment.newInstance(mHomePageWeatherId));
        } else {
            initFragments();
        }
    }

    // 初始化所有的页面
    private void initFragments() {
        List<String> strings = SharedPreferenceUtils.fetchPagesWeatherId(
                this,SharedPreferenceUtils.PAGES_WEATHER_ID_KEY);
        if (strings != null && strings.size() > 0) {
            for (String str : strings) {
                addFragment(BlackHomeFragment.newInstance(str));
                mViewPager.setCurrentItem(0);
            }
        }
    }

    public void setPlusBundle(Bundle bundle) {
        addFragment((BlackHomeFragment) bundle.getSerializable("add_fragment"));
    }

    public void setSwitchBundle(Bundle bundle) {
        swapFragment((BlackHomeFragment) bundle.getSerializable("switch_fragment"));
    }

    private void addFragment(BlackHomeFragment blackHomeFragment) {
        if (mFragments.size() < PAGE_LIMIT) {
            mFragments.add(blackHomeFragment);
            mPagerAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "页面数到达上限", Toast.LENGTH_SHORT).show();
        }
        mViewPager.setCurrentItem(mFragments.size() - 1);
    }

    private void swapFragment(BlackHomeFragment blackHomeFragment) {
        int position = mViewPager.getCurrentItem();
        mFragments.set(position, blackHomeFragment);
        mPagerAdapter.replaceFragment(position, mFragments.get(position));
    }

    private void delFragment() {
        // 不删除第一个fragment即home page
        int position = mViewPager.getCurrentItem();
        if (position > 0) {
            mFragments.remove(position);
            mPagerAdapter.notifyDataSetChanged();
        }
        mViewPager.setCurrentItem(position - 1, false);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position != 0) {
            mDeleteButton.setVisibility(View.VISIBLE);
        } else {
            mDeleteButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    @Override
    protected void onPause() {
        super.onPause();
        List<String> pagesWeatherIds = new ArrayList<>();
        if (pagesWeatherIds.size() > 0) {
            pagesWeatherIds.clear();
        }
        for (BlackHomeFragment bhf : mFragments){
            pagesWeatherIds.add(bhf.getWeatherId());
        }
        SharedPreferenceUtils.savedPagesWeatherId(this,
                SharedPreferenceUtils.PAGES_WEATHER_ID_KEY, pagesWeatherIds);
    }
}

// 初始化
//        mDrawerLayout = findViewById(R.id.drawer_layout);
//        mLocationTextView = findViewById(R.id.title_location);
//        mHomeRecyclerView = findViewById(R.id.home_recycler_view);
//        mAdapter = new BlackAdapter();
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        String weatherStr = prefs.getString("weather", null);
//        mHomeRecyclerView.setAdapter(mAdapter);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        mHomeRecyclerView.setLayoutManager(layoutManager);
//        if (weatherStr != null) {
//            Weather weather = JsonUtils.handleWeatherResponse(weatherStr);
//            mAdapter.setData(weather);
//            mLocationTextView.setText(weather.basic.location);
//        }else {
//            try {
//                mWeatherId = getIntent().getStringExtra("weather_id");
////            mWeatherLayout.setVisibility(View.INVISIBLE);
//                requestWeather(mWeatherId);
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            }
//        }

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
//    }

//    /**
//     * 根据天气id请求城市天气数据
//     */
//    public void requestWeather(final String weatherId) throws MalformedURLException {
//        URL url = NetworkUtils.buildUrlWithWeatherId(weatherId);
//        NetworkUtils.sendOkHttpRequest(url, new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(BlackHomeActivity.this, "获取天气信息失败2",
//                                Toast.LENGTH_SHORT).show();
////                        mSwipeRefresh.setRefreshing(false);
//                    }
//                });
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                final String responseText = response.body().string();
//                final Weather weather = JsonUtils.handleWeatherResponse(responseText);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (weather != null && "ok".equals(weather.status)) {
//                            SharedPreferences.Editor editor = PreferenceManager
//                                    .getDefaultSharedPreferences(BlackHomeActivity.this)
//                                    .edit();
//                            editor.putString("weather", responseText);
//                            editor.apply();
//                            mAdapter.setData(weather);
//                        } else {
//                            Toast.makeText(BlackHomeActivity.this, "获取天气信息失败",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        });
//    }

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

//}
