package com.blackweather.android;

import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.blackweather.android.adapters.ViewPagerAdapter;
import com.blackweather.android.data.SavedWeatherIds;
import com.blackweather.android.service.AutoUpdateService;
import com.blackweather.android.task.BlackTask;
import com.blackweather.android.utilities.NetworkUtils;
import com.blackweather.android.utilities.PreferenceUtils;
import com.blackweather.android.utilities.ToastUtils;
import com.rd.PageIndicatorView;

import java.util.ArrayList;
import java.util.List;

/**
 * d90:Author:theVan
 */
public class HomeActivity extends AppCompatActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();

    private static final int PAGE_LIMIT = 5;

    // data
    private List<HomeFragment> mFragmentList = new ArrayList<>();
    private String mHomePageWeatherId;

    // widget
    private ViewPager mViewPager;
    private PageIndicatorView mPageIndicator;
    private ViewPagerAdapter mPagerAdapter;
    private DrawerLayout mDrawerLayout;
    private FloatingActionButton mPlusFabButton;
    private Button mDeleteButton;
    private Button mSwapButton;
    private ImageView mBcPicImg;
    private Button mSettingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 将背景图和status bar融合在一起
        transStatusBar();
        // 加载布局
        setContentView(R.layout.activity_home);
        // bind views
        bindView();
        // 初始化控件
        setupController();
        // 确定要显示的ui
        initUI();
        // 根据偏好设置启动后台更新服务，默认为启动
        startService();

        // 注入事件监听器
        mPlusFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDrawerLayout(GravityCompat.END);
            }
        });
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delFragment();
            }
        });
        mSwapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDrawerLayout(GravityCompat.START);
            }
        });
        mSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * bind views
     */
    private void bindView() {
        mViewPager = findViewById(R.id.home_view_pager);
        mPageIndicator = findViewById(R.id.home_page_indicator);
        mPlusFabButton = findViewById(R.id.home_fab_button);
        mDeleteButton = findViewById(R.id.title_delete_button);
        mDrawerLayout = findViewById(R.id.blackActivity_drawer_layout);
        mSwapButton = findViewById(R.id.title_swap_button);
        mBcPicImg = findViewById(R.id.home_pic_img);
    }

    /**
     * 初始化控件
     */
    private void setupController() {
        mSettingsButton = findViewById(R.id.title_settings_button);
        mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), mFragmentList);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
        });
    }

    /**
     * 将背景图和status bar融合在一起
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
     * 处理需要显示的UI
     */
    private void initUI() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.home_start_frame, ChooseFragment
                        .newInstance(ChooseFragment.STATE_SWAP_PAGE)).commit();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.home_end_frame, ChooseFragment
                        .newInstance(ChooseFragment.STATE_PLUS_PAGE)).commit();
        // 首次运行，设置首页的偏好的地方
        if (getIntent().hasExtra("weather_id")) {
            mHomePageWeatherId = getIntent().getStringExtra("weather_id");
            addFragment(HomeFragment.newInstance(mHomePageWeatherId));
            getIntent().removeExtra("weather_id");
        } else {
            initFragments();
        }
        // 加载背景图片
        BlackTask.loadBgPic(this, mBcPicImg);
        // 如果当前页面为主页，则不显示删除按钮
        setupDeleteButton();
    }

    /**
     * 根据是否为第一页来判断是否显示删除按钮
     */
    private void setupDeleteButton() {
        if (mViewPager.getCurrentItem() == 0) {
            mDeleteButton.setVisibility(View.GONE);
        } else {
            mDeleteButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 根据是否有缓存来初始化fragments
     */
    private void initFragments() {
        List<String> strings = PreferenceUtils.fetchPagesWeatherId(
                this, PreferenceUtils.PAGES_WEATHER_ID_KEY);
        if (strings != null && strings.size() > 0) {
            for (String str : strings) {
                if (strings != null) {
                    addFragment(HomeFragment.newInstance(str));
                }
            }
            mViewPager.setCurrentItem(0);
        }
    }

    /**
     * 根据偏好设置来确定是否启动后台自动更新的service
     */
    private void startService() {
        if (PreferenceUtils.isAllowedAutpUpdate(this)) {
            // 开启后台更新Service
            Intent intent = new Intent(this, AutoUpdateService.class);
            startService(intent);
        }
    }

    /**
     * 为view pager添加一个页面
     * @param newWeatherId  新页面要显示的天气信息的id
     */
    public void plusPage(String newWeatherId) {
        SavedWeatherIds savedWeatherIds = new SavedWeatherIds();
        if (savedWeatherIds != null) {
            addFragment(HomeFragment.newInstance(newWeatherId));
        }
    }

    /**
     * 切换当前view pager的页面
     * @param newWeatherId 新页面要显示的天气信息的id
     */
    public void swapCurrentPage(String newWeatherId) {
        if (newWeatherId != null) {
            // 删除本地化数据
            int curPosition = mViewPager.getCurrentItem();
            PreferenceUtils.removeKeyValuePair(this,
                    mFragmentList.get(curPosition).getWeatherId());
            // 切换城市
            swapFragment(HomeFragment.newInstance(newWeatherId));
        }
    }

    /**
     * 添加一个fragment
     */
    private void addFragment(HomeFragment blackHomeFragment) {
        if (mFragmentList.size() < PAGE_LIMIT) {
            mFragmentList.add(blackHomeFragment);
            mPagerAdapter.notifyDataSetChanged();
        } else {
            ToastUtils.getInstance(BlackApplication.getContext())
                    .show("页面数到达上限");
//            Toast.makeText(this, "页面数到达上限", Toast.LENGTH_SHORT).show();
        }
        mViewPager.setCurrentItem(mFragmentList.size() - 1);
    }

    /**
     * 切换显示当前页面的fragment
     */
    private void swapFragment(HomeFragment blackHomeFragment) {
        int position = mViewPager.getCurrentItem();
        mFragmentList.set(position, blackHomeFragment);
        mPagerAdapter.notifyDataSetChanged();
    }

    /**
     * 删除一个fragment
     */
    private void delFragment() {
        // 不删除第一个fragment即home page
        int curPosition = mViewPager.getCurrentItem();
        if (curPosition > 0) {
            // 删除本地数据
            String curWeatherId = mFragmentList.get(curPosition).getWeatherId();
            PreferenceUtils.removeKeyValuePair(this,
                    curWeatherId);
            mFragmentList.remove(curPosition);
            mPagerAdapter.notifyDataSetChanged();
        }
        mViewPager.setCurrentItem(curPosition - 1, false);
    }

    /**
     * 主要的作用是为在choose fragment中获取DrawLayout的实例
     *
     * @return DrawerLayout的实例
     */
    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    /**
     * 从指定方向打开drawer layout
     *
     * @param direction drawer打开的方向
     */
    private void showDrawerLayout(int direction) {
        if (mDrawerLayout.isDrawerOpen(direction)) {
            mDrawerLayout.closeDrawer(direction);
        }
        mDrawerLayout.openDrawer(direction);
    }

    /**
     * 当drawer layout打开时让按返回键执行关闭drawer而不是关闭activity
     */
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            mDrawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 保存当前的数据
     */
    @Override
    protected void onPause() {
        super.onPause();
        // 保存数据
        List<String> pagesWeatherIds = new ArrayList<>();
        for (HomeFragment bhf : mFragmentList) {
            String weatherId = bhf.getWeatherId();
            if (weatherId != null) {
                pagesWeatherIds.add(bhf.getWeatherId());

            }
        }
        PreferenceUtils.savedPagesWeatherId(this,
                PreferenceUtils.PAGES_WEATHER_ID_KEY, pagesWeatherIds);
    }

}

