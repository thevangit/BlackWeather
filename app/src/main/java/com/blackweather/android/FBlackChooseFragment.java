package com.blackweather.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blackweather.android.litePalDatabase.City;
import com.blackweather.android.litePalDatabase.County;
import com.blackweather.android.litePalDatabase.Province;
import com.blackweather.android.utilities.JsonUtils;
import com.blackweather.android.utilities.NetworkUtils;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FBlackChooseFragment extends Fragment {

    private static final String TAG = FBlackChooseFragment.class.getSimpleName();

    private OnLocationSelectedListener mListener;

    interface OnLocationSelectedListener{
        public void onPlusLocationSelected();
        public void onSwapLocationSelected();
    }

    private int mState;
    public static final int STATE_PLUS_PAGE = 1;
    public static final int STATE_SWAP_PAGE = 2;

    public static FBlackChooseFragment newInstance(int stateData) {
        FBlackChooseFragment bhf = new FBlackChooseFragment();
        Bundle args = new Bundle();
        args.putInt("state", stateData);
        bhf.setArguments(args);
        return bhf;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mState = args.getInt("state");
        }
    }

    // 使用整型数据来表示当前fragment所显示的列表的层级
    public static final int LEVEL_PROVINCE = 0;

    public static final int LEVEL_CITY = 1;

    public static final int LEVEL_COUNTY = 2;

    // TODO(1) 将ProgressDialog更改为ProgressBar
    // 进度对话框，在API26中ProgressDialog被声明不赞成使用，应使用的替代方法是ProgressBar
//    private ProgressDialog mProgressDialog;
    private ProgressBar mProgressBar;

    private TextView mTitleText;

    private Button mBackButton;

    private ListView mListView;

    private ArrayAdapter<String> mAdapter;

    private List<String> mDataList = new ArrayList<>();

    /**
     * 省列表
     */
    private List<Province> mProvinceList;

    /**
     * 市列表
     */
    private List<City> mCityList;

    /**
     * 县列表
     */
    private List<County> mCountyList;

    /**
     * 选中的省份
     */
    private Province mSelectedProvince;

    /**
     * 选中的城市
     */
    private City mSelectedCity;

    /**
     * 当前选中的级别
     */
    private int mCurrentLevel;

    private final String CITY_ADDRESS = "http://guolin.tech/api/china";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_area, container, false);
        mTitleText = view.findViewById(R.id.title_text);
        mBackButton = view.findViewById(R.id.back_button);
        mListView = view.findViewById(R.id.list_view);
//        mDataList.add("1");
        mAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_expandable_list_item_1,
                mDataList);
        mListView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCurrentLevel == LEVEL_PROVINCE) {
                    mSelectedProvince = mProvinceList.get(position);
                    queryCities();
                } else if (mCurrentLevel == LEVEL_CITY) {
                    mSelectedCity = mCityList.get(position);
                    queryCounties();
                } else if (mCurrentLevel == LEVEL_COUNTY) {
                    String weatherId = mCountyList.get(position).getWeatherId();
                    String location = mCountyList.get(position).getCountyName();
                    if (getActivity() instanceof BlackMainActivity) {
                        Intent intent = new Intent(getActivity(), BlackHomeActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    } else if (getActivity() instanceof BlackHomeActivity) {
                        BlackHomeActivity activity = (BlackHomeActivity) getActivity();
                        if (mState == STATE_PLUS_PAGE) {
                            activity.plusPage(weatherId);
                            activity.getDrawerLayout().closeDrawers();
                        }else if (mState == STATE_SWAP_PAGE) {
                            activity.swapCurrentPage(weatherId);
                            activity.getDrawerLayout().closeDrawers();
                        }
                        Log.d(TAG, "debug state: " + mState + "-" + location + " weather_id: "
                                + weatherId);
                    }
                }
            }
        });
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentLevel == LEVEL_COUNTY) {
                    queryCities();
            } else if (mCurrentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
        }
    });
        queryProvinces();
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryProvinces() {
        Log.i("MainActivity", "queryProvinces: 执行" );
        // TODO(2) 改变string resource的hard-code
        mTitleText.setText("中国");
        mBackButton.setVisibility(View.GONE);
        mProvinceList = LitePal.findAll(Province.class);
        if (mProvinceList.size() > 0) {
            mDataList.clear();
            for (Province province : mProvinceList) {
                mDataList.add(province.getProvinceName());
            }
            // 通知adapter数据data改变了
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            // 最开始的层级为省级
            mCurrentLevel = LEVEL_PROVINCE;
        } else {
//            String address = "http://guolin.tech/api/china";
            queryFormServer(CITY_ADDRESS, "province");
        }
    }

    /**
     * 查询选中的省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCities(){
        Log.i("MainActivity", "queryCities: 执行" );
        mTitleText.setText(mSelectedProvince.getProvinceName());
        mBackButton.setVisibility(View.VISIBLE);
        mCityList = LitePal.where("provinceId = ?", String.valueOf(mSelectedProvince
            .getId())).find(City.class);
        if (mCityList.size() > 0) {
            mDataList.clear();
            for (City city : mCityList) {
                mDataList.add(city.getCityName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mCurrentLevel = LEVEL_CITY;
        } else {
            int provinceCode = mSelectedProvince.getProvinceCode();
            String address = CITY_ADDRESS + "/" + provinceCode;
            queryFormServer(address, "city");
        }
    }

    /**
     * 查询选中市中所有的县，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCounties() {
        Log.i("MainActivity", "queryCounties: 执行" );
        mTitleText.setText(mSelectedCity.getCityName());
        mBackButton.setVisibility(View.VISIBLE);
        mCountyList = LitePal.where("cityId = ?", String.valueOf(mSelectedCity
                .getId())).find(County.class);
        if (mCountyList.size() > 0) {
            mDataList.clear();
            for (County county : mCountyList) {
                mDataList.add(county.getCountyName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mCurrentLevel = LEVEL_COUNTY;
        } else {
            int provinceCode = mSelectedProvince.getProvinceCode();
            int cityCode = mSelectedCity.getCityCode();
            String address = CITY_ADDRESS + "/" + provinceCode + "/" + cityCode;
            queryFormServer(address, "county");
        }
    }

    /**
     * 根据传入的地址和类型从服务器上查询省市县数据, type是final的是应为匿名类需要访问该变量
     */
    private void queryFormServer(String address, final String type) {
        Log.i("MainActivity", "queryFormServer:执行 ");
//        showProgressDialog();
        NetworkUtils.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 通过runOnUiThread()方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    result = JsonUtils.handleProvinceResponse(responseText);
                } else if ("city".equals(type)) {
                    result = JsonUtils.handleCityResponse(responseText,
                            mSelectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = JsonUtils.handleCountyResponse(responseText,
                            mSelectedCity.getId());
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }

//    /**
//     * Show ProgressDialog
//     */
//    private void showProgressDialog() {
//        Log.i("MainActivity", "showProgressDialog: 执行");
//        if (mProgressBar == null) {
//            mProgressBar = new ProgressBar(getActivity());
//            // .dialog.setCancelable(false):dialog弹出后会点击屏幕或物理返回键，dialog不消失
//            // .dialog.setCanceledOnTouchOutside(false):dialog弹出后会点击屏幕，dialog不消失,
//            // 点击物理返回键dialog消失
//            mProgressDialog.setCanceledOnTouchOutside(false);
//        }
//        mProgressBar.show();
//    }
//
//    /**
//     * close ProgressDialog
//     */
//    private void closeProgressDialog() {
//        Log.i("MainActivity", "closeProgressDialog: 执行");
//        if (mProgressDialog != null) {
//            mProgressDialog.dismiss();
//        }
//    }
}
