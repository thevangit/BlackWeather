package com.blackweather.android;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class BlackFragPagerAdapter extends FragmentPagerAdapter {

    private List<BlackHomeFragment> mFragments;

    private int mPosition;

    private boolean canReplace;

    private Fragment replaceFragment;

    private FragmentManager mFm;

    public BlackFragPagerAdapter(FragmentManager fm, List<BlackHomeFragment> fragments) {
        super(fm);
        mFm = fm;
        mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    /**
     * 动态替换fragment
     *
     * @param position 指定替换已有fragment的position,第一个是0;
     * @param fragment 待替换的新的Fragment;
     */
    public void replaceFragment(int position, Fragment fragment) {
        if (position >= 0 && position < mFragments.size()) {
            this.replaceFragment = fragment;
            this.mPosition = position;
            canReplace = true;
            this.notifyDataSetChanged();
        }
    }

    @Override
    public int getItemPosition(Object object) {
        //TODO 这是重点继续研究
        //return super.getItemPosition(object);//默认是不改变
        return POSITION_NONE;//可以即时刷新
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //得到缓存的fragment
        Fragment fragment = (Fragment) super.instantiateItem(container,
                position);
        //得到tag，这点很重要
        String fragmentTag = fragment.getTag();
        if (canReplace && mPosition == position) {
            //如果这个fragment需要更新
            FragmentTransaction ft = mFm.beginTransaction();
            //移除旧的fragment
            ft.remove(fragment);
            //换成新的fragment
            fragment = replaceFragment;
            //添加新fragment时必须用前面获得的tag，这点很重要
            ft.add(container.getId(), fragment, fragmentTag);
            ft.attach(fragment);
            ft.commit();
            //复位更新标志
            canReplace = false;
        }
        return fragment;
    }

}
