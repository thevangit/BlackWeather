package com.blackweather.android;

import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.blackweather.android.litePalDatabase.SavedWeatherIds;
import com.blackweather.android.utilities.SharedPreferenceUtils;
import com.bumptech.glide.Glide;
import com.rd.PageIndicatorView;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

/**
 * d90:Author:theVan
 */
public class BlackHomeActivity extends AppCompatActivity {

    private static final String TAG = BlackHomeActivity.class.getSimpleName();

    private static final int PAGE_LIMIT = 5;

    // data
    private List<FBlackHomeFragment> mFragments = new ArrayList<>();
    private String mHomePageWeatherId;
    // widget
    private ViewPager mViewPager;
    private PageIndicatorView mPageIndicator;
    private ABlackViewPagerAdapter mPagerAdapter;
    private DrawerLayout mDrawerLayout;
    private FloatingActionButton mPlusFabButton;
    private Button mDeleteButton;
    private Button mSwitchButton;
    private ImageView mBcPicImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transStatusBar(); // 将背景图和status bar融合在一起
        setContentView(R.layout.activity_home);

        // bind views and setup widgets
        mViewPager = findViewById(R.id.blackActivity_view_pager);
        mPageIndicator = findViewById(R.id.backActivity_page_indicator);
        mPlusFabButton = findViewById(R.id.backActivity_fab_button);
        mDeleteButton = findViewById(R.id.title_delete_button);
        mDrawerLayout = findViewById(R.id.blackActivity_drawer_layout);
        mSwitchButton = findViewById(R.id.titel_switch_button);
        mBcPicImg = findViewById(R.id.bc_pic_img);
        mPagerAdapter = new ABlackViewPagerAdapter(getSupportFragmentManager(), mFragments);
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

        initMainUI(); // 初始化主页
        // 如果当前页面为主页，则不显示删除按钮
        if (mViewPager.getCurrentItem() == 0) {
            mDeleteButton.setVisibility(View.GONE);
        } else {
            mDeleteButton.setVisibility(View.VISIBLE);
        }

        // 注入事件监听器
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
    private void initMainUI() {
        loadBingPic(); // 加载背景图片
        getSupportFragmentManager().beginTransaction()
                .add(R.id.start_frame_layout, FBlackChooseFragment
                        .newInstance(FBlackChooseFragment.STATE_SWAP_PAGE)).commit();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.end_frame_layout, FBlackChooseFragment
                        .newInstance(FBlackChooseFragment.STATE_PLUS_PAGE)).commit();
        // 首次运行，设置首页的偏好的地方
        if (getIntent().hasExtra("weather_id")) {
            mHomePageWeatherId = getIntent().getStringExtra("weather_id");
            addFragment(FBlackHomeFragment.newInstance(mHomePageWeatherId));
            getIntent().removeExtra("weather_id");
        } else {
            initFragments();
        }
    }

    private void initFragments() {
        List<String> strings = SharedPreferenceUtils.fetchPagesWeatherId(
                this, SharedPreferenceUtils.PAGES_WEATHER_ID_KEY);
        if (strings != null && strings.size() > 0) {
            int i = 0;
            for (String str : strings) {
                Log.d(TAG, "debug initFragments: " + i++ + "weatherId:" + str);
                if (strings != null) {
                    addFragment(FBlackHomeFragment.newInstance(str));
                }
            }
            mViewPager.setCurrentItem(0);
        }
    }

    public void plusPage(String newWeatherId) {
        SavedWeatherIds savedWeatherIds = new SavedWeatherIds();
        if (savedWeatherIds != null) {
            addFragment(FBlackHomeFragment.newInstance(newWeatherId));
        }
    }

    public void swapCurrentPage(String newWeatherId) {
        if (newWeatherId != null) {
            // 删除本地化数据
            int curPosition = mViewPager.getCurrentItem();
            SharedPreferenceUtils.removeKeyValuePair(this,
                    mFragments.get(curPosition).getWeatherId());
            // 切换城市
            swapFragment(FBlackHomeFragment.newInstance(newWeatherId));
        }
    }

    private void addFragment(FBlackHomeFragment blackHomeFragment) {
        if (mFragments.size() < PAGE_LIMIT) {
            mFragments.add(blackHomeFragment);
            mPagerAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "页面数到达上限", Toast.LENGTH_SHORT).show();
        }
        mViewPager.setCurrentItem(mFragments.size() - 1);
    }

    private void swapFragment(FBlackHomeFragment blackHomeFragment) {
        int position = mViewPager.getCurrentItem();
        mFragments.set(position, blackHomeFragment);
        mPagerAdapter.notifyDataSetChanged();
    }

    private void delFragment() {
        // 不删除第一个fragment即home page
        int curPosition = mViewPager.getCurrentItem();
        if (curPosition > 0) {
            // 删除本地数据
            String curWeatherId = mFragments.get(curPosition).getWeatherId();
            SharedPreferenceUtils.removeKeyValuePair(this,
                    curWeatherId);
            mFragments.remove(curPosition);
            mPagerAdapter.notifyDataSetChanged();
        }
        mViewPager.setCurrentItem(curPosition - 1, false);
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 保存数据
        List<String> pagesWeatherIds = new ArrayList<>();
        if (pagesWeatherIds.size() > 0) {
            pagesWeatherIds.clear();
        }
        int i = 0;
        for (FBlackHomeFragment bhf : mFragments) {
            pagesWeatherIds.add(bhf.getWeatherId());
            Log.d(TAG, "debug onPause: " + i++ +" - "+ bhf.getWeatherId() );
        }
        SharedPreferenceUtils.savedPagesWeatherId(this,
                SharedPreferenceUtils.PAGES_WEATHER_ID_KEY, pagesWeatherIds);
    }

    /**
     * 加载背景图片
     */
    private void loadBingPic() {
        Glide.with(this).load(R.drawable.bg).into(mBcPicImg);
    }

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

//}
