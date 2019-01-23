package com.blackweather.android;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
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

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.blackweather.android.data.City;
import com.blackweather.android.data.County;
import com.blackweather.android.data.Province;
import com.blackweather.android.utilities.JsonUtils;
import com.blackweather.android.utilities.NetworkUtils;

import org.litepal.LitePal;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseFragment extends Fragment {


    private static final String TAG = ChooseFragment.class.getSimpleName();

    private int mState;
    public static final int STATE_PLUS_PAGE = 1;
    public static final int STATE_SWAP_PAGE = 2;

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
    // 省列表
    private List<Province> mProvinceList;
    // 市列表
    private List<City> mCityList;
    // 县列表
    private List<County> mCountyList;
    // 选中的省份
    private Province mSelectedProvince;
    // 选中的城市
    private City mSelectedCity;
    // 当前选中的级别
    private int mCurrentLevel;

    private final String CITY_ADDRESS = "http://guolin.tech/api/china";

    // 定位
    private LocationClient mLocationClient;
    private double mLongitude; // 经度
    private double mLatitude; // 纬度
    private String mCurrentWeatherId;
    private String mCurrentLocation;
    private TextView mCurrentLocationView;
    private ProgressBar mCurrentProgressBar;
//    private TextView mCurrentChooseView;

    public static ChooseFragment newInstance(int stateData) {
        ChooseFragment bhf = new ChooseFragment();
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose, container, false);
        mTitleText = view.findViewById(R.id.title_choose_text);
        mBackButton = view.findViewById(R.id.title_choose_back_button);
        mListView = view.findViewById(R.id.list_view);
//        mDataList.add("1");
        mAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_expandable_list_item_1,
                mDataList);
        mListView.setAdapter(mAdapter);
        mCurrentLocationView = view.findViewById(R.id.choose_current_location);
        mCurrentProgressBar = view.findViewById(R.id.choose_current_progress);
//        mCurrentChooseView = view.findViewById(R.id.choose_choose_location);
//        mCurrentProgressBar.setVisibility(View.GONE);
        mCurrentLocationView.setVisibility(View.GONE);

        mLocationClient = new LocationClient(BlackApplication.getContext());
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                final double longitude = bdLocation.getLongitude();
                final double latitude = bdLocation.getLatitude();
                getActivity().getSupportLoaderManager().initLoader(1, null,
                        new LoaderManager.LoaderCallbacks<String[]>() {
                            @NonNull
                            @Override
                            public Loader<String[]> onCreateLoader(int i, @Nullable Bundle bundle) {
                                URL url = null;
                                try {
                                    url = NetworkUtils.buildUrlWithLonngtitdeLatitude(longitude, latitude);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return new LocationLoader(getActivity(), url);
                            }

                            @Override
                            public void onLoadFinished(@NonNull Loader<String[]> loader, String[] strings) {
                                if (strings == null) {
//                                    Toast.makeText(getActivity(), "未知错误",
//                                            Toast.LENGTH_SHORT).show();
//                                    mCurrentProgressBar.setVisibility(View.GONE);
//                                    mCurrentLocationView.setVisibility(View.GONE);
//                                    mCurrentChooseView.setVisibility(View.VISIBLE);
                                    return;
                                }
                                mCurrentLocation = strings[0];
                                mCurrentWeatherId = strings[1];
                                mCurrentLocationView.setText("当前城市：" + mCurrentLocation);
//                                mCurrentProgressBar.setVisibility(View.GONE);
//                                mCurrentChooseView.setVisibility(View.GONE);
                                mCurrentLocationView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onLoaderReset(@NonNull Loader<String[]> loader) {
                            }
                        });
            }
        });

        // 处理运行时权限
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(BlackApplication.getContext(), Manifest.permission
                .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(BlackApplication.getContext(), Manifest.permission
                .READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(BlackApplication.getContext(), Manifest.permission
                .WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList
                    .toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(getActivity(), permissions, 1);
        } else {
            requestLocation();
        }

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(getActivity(), "需要同意所有全选才能运行",
                                    Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(getActivity(), "发生未知错误", Toast.LENGTH_SHORT)
                            .show();
                    getActivity().finish();
                }
                break;
            default:
        }
    }


    private void requestLocation() {
//        Log.d(TAG, "debug3 requestLocation: " + "执行");
        mLocationClient.start();
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 处理定位

//        mCurrentChooseView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mCurrentChooseView.setVisibility(View.GONE);
//                mCurrentProgressBar.setVisibility(View.VISIBLE);
//                requestLocation();
//            }
//        });

        mCurrentLocationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof BlackMainActivity) {
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    intent.putExtra("weather_id", mCurrentWeatherId);
                    startActivity(intent);
                    getActivity().finish();
                } else if (getActivity() instanceof HomeActivity) {
                    HomeActivity activity = (HomeActivity) getActivity();
                    if (mState == STATE_PLUS_PAGE) {
                        activity.plusPage(mCurrentWeatherId);
                        activity.getDrawerLayout().closeDrawers();
                    } else if (mState == STATE_SWAP_PAGE) {
                        activity.swapCurrentPage(mCurrentWeatherId);
                        activity.getDrawerLayout().closeDrawers();
                    }
                }
            }
        });


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
                        Intent intent = new Intent(getActivity(), HomeActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    } else if (getActivity() instanceof HomeActivity) {
                        HomeActivity activity = (HomeActivity) getActivity();
                        if (mState == STATE_PLUS_PAGE) {
                            activity.plusPage(weatherId);
                            activity.getDrawerLayout().closeDrawers();
                        } else if (mState == STATE_SWAP_PAGE) {
                            activity.swapCurrentPage(weatherId);
                            activity.getDrawerLayout().closeDrawers();
                        }
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
        Log.i("MainActivity", "queryProvinces: 执行");
        // TODO(2) 改变string resource的hard-code
        mTitleText.setText("中国");
        mBackButton.setVisibility(View.INVISIBLE);
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
    private void queryCities() {
        Log.i("MainActivity", "queryCities: 执行");
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
        Log.i("MainActivity", "queryCounties: 执行");
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
                Log.d(TAG, "debug onResponse: " + "执行");
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();

    }

    //    @Override
//    public void onPause() {
//        super.onPause();
//        mLocationClient.stop();
//        mLocationClient.unRegisterLocationListener(new BlackLocationListener());
//    }

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
//
//    @NonNull
//    @Override
//    public Loader<String[]> onCreateLoader(int i, @Nullable Bundle bundle) {
//        mLocationClient = new LocationClient(BlackApplication.getContext());
//        LocationLoader locationInfoLoader = new LocationLoader(getActivity(), mLocationClient);
//        return locationInfoLoader;
//    }
//
//    @Override
//    public void onLoadFinished(@NonNull Loader<String[]> loader, String[] strings) {
//        mCurrentLocation = strings[0];
//        mCurrentWeatherId = strings[1];
//        mCurrentLocationView.setText("定位：" + mCurrentLocation);
//        mCurrentLocationView.setVisibility(View.VISIBLE);
//    }
//
//    @Override
//    public void onLoaderReset(@NonNull Loader<String[]> loader) {
//
//    }
}

//        ///////////////////////
//        mLocationClient = new LocationClient(BlackApplication.getContext());
//        mLocationClient.registerLocationListener(new BlackLocationListener());
//        // 处理运行时权限
//        List<String> permissionList = new ArrayList<>();
//        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission
//                .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
//        }
//        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission
//                .READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            permissionList.add(Manifest.permission.READ_PHONE_STATE);
//        }
//        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission
//                .WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        }
//        if (!permissionList.isEmpty()) {
//            String[] permissions = permissionList
//                    .toArray(new String[permissionList.size()]);
//            ActivityCompat.requestPermissions(getActivity(), permissions, 1);
//        } else {
//            requestLocation();
//        }

//    private void requestLocation() {
//        Log.d(TAG, "debug2 requestLocation: " + "执行");
//        mLocationClient.start();
//    }


//    private void getLocationNameWithNetwork() throws MalformedURLException {
//        Log.d(TAG, "debug2 getLocationNameWithNetwork: " + "执行");
//        URL url = NetworkUtils.buildUrlWithLonngtitdeLatitude(mLongitude, mLatitude);
//        Log.d(TAG, "debug2 url: " + url.toString());
//        NetworkUtils.sendOkHttpRequest(url, new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                String responseStr = response.body().string();
//                final Weather weather = JsonUtils.handleWeatherResponse(responseStr);
//                if (getActivity() == null) return;
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (weather != null && "ok".equals(weather.status)) {
//                            Log.d(TAG, "debug2 location: " + weather.basic.location);
//                            mCurrentLocation = weather.basic.location;
//                            Log.d(TAG, "debug2 location: " + weather.basic.location);
//                            mCurrentWeatherId = weather.basic.weatherId;
//
//                            mCurrentLocationView.setText("当前城市：" + mCurrentLocation);
//                            mCurrentLocationView.setVisibility(View.VISIBLE);
//                        }
//                    }
//                });
//
//            }
//        });
//    }


//    class BlackLocationListener implements BDLocationListener {
//
//        @Override
//        public void onReceiveLocation(final BDLocation bdLocation) {
//            Log.d(TAG, "debug onReceiveLocation: " + "执行");
//            if (getActivity() != null) {
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        StringBuilder stringBuilder = new StringBuilder();
//                        stringBuilder.append("纬度：").append(bdLocation.getLatitude())
//                                .append("\n");
//                        stringBuilder.append("经度：").append(bdLocation.getLongitude())
//                                .append("\n");
//                        Log.d(TAG, "debug2 stringBuilder:" + stringBuilder);
//                        mLongitude = Double.valueOf(bdLocation.getLongitude());
//                        mLatitude = Double.valueOf(bdLocation.getLatitude());
//
//                        try {
//                            getLocationNameWithNetwork();
//                        } catch (MalformedURLException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            }
//        }
//    }


/////////////////////////////