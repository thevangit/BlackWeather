package com.blackweather.android;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
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

import com.baidu.location.LocationClient;
import com.blackweather.android.data.City;
import com.blackweather.android.data.County;
import com.blackweather.android.data.Province;
import com.blackweather.android.gson.Weather;
import com.blackweather.android.utilities.JsonUtils;
import com.blackweather.android.utilities.NetworkUtils;
import com.blackweather.android.utilities.ToastUtils;

import org.litepal.LitePal;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<String[]> {


    private static final String TAG = ChooseFragment.class.getSimpleName();

    private int mState;
    public static final int STATE_PLUS_PAGE = 1;
    public static final int STATE_SWAP_PAGE = 2;

    // 使用整型数据来表示当前fragment所显示的列表的层级
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    //    private ProgressDialog mProgressDialog;
//    private ProgressBar mProgressBar;
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
    private ProgressBar mProgressBar;
//    private TextView mCurrentChooseView;

    private SwipeRefreshLayout mRefreshLayout;

    private Location mLocation;

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

        Log.d(TAG, "debug4 onCreateView: " + "得到执行");

        View view = inflater.inflate(R.layout.fragment_choose, container, false);
        mTitleText = view.findViewById(R.id.title_choose_text);
        mBackButton = view.findViewById(R.id.title_choose_back_button);
        mListView = view.findViewById(R.id.list_view);
//        mDataList.add("1");
        mAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_expandable_list_item_1,
                mDataList);
        mListView.setAdapter(mAdapter);
        mCurrentLocationView = view.findViewById(R.id.choose_current_location);
        mProgressBar = view.findViewById(R.id.choose_progress);
        mRefreshLayout = view.findViewById(R.id.choose_refresh);
        mProgressBar.setVisibility(View.GONE);
        mCurrentLocationView.setVisibility(View.GONE);
        // 处理运行时权限
        List<String> permissionList = checkPermissionList();
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList
                    .toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(getActivity(), permissions, 1);
        } else {
            fetchLongAndLat();
        }

//        getActivity().getSupportLoaderManager().initLoader(1, null,
//                this);
        return view;
    }

    private List<String> checkPermissionList() {
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(BlackApplication.getContext(), Manifest.permission
                .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(BlackApplication.getContext(), Manifest.permission
                .ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
//        if (ContextCompat.checkSelfPermission(BlackApplication.getContext(), Manifest.permission
//                .READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            permissionList.add(Manifest.permission.READ_PHONE_STATE);
//        }
//        if (ContextCompat.checkSelfPermission(BlackApplication.getContext(), Manifest.permission
//                .WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        }
        return permissionList;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            ToastUtils.getInstance(BlackApplication.getContext())
                                    .show("需要同意所有权限才能运行 error:101");
                            getActivity().finish();
                            return;
                        }
                    }
                    fetchLongAndLat();
//                    requestLocation();
                } else {
                    ToastUtils.getInstance(BlackApplication.getContext())
                            .show("需要同意所有权限才能运行 error:102");
                    getActivity().finish();
                }
                break;
            default:
        }
    }

    /**
     * 获取经纬度
     */
    private void fetchLongAndLat() {
        mLocation = queryLocation();
        if (mLocation != null) {
            mLongitude = mLocation.getLongitude();
            mLatitude = mLocation.getLatitude();
            Log.d(TAG, "debug4 onCreateView: " + mLongitude + "," + mLatitude);
        }
    }

    public Location queryLocation() {
        LocationManager lm = (LocationManager) getActivity()
                .getSystemService(Context.LOCATION_SERVICE);
        String provider = judgeProvider(lm);
        if (provider != null) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission
                    .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission
                    .ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            Log.d(TAG, "debug4 queryLocation: " + lm.getLastKnownLocation(provider).toString());
            return lm.getLastKnownLocation(provider);
        } else {
            ToastUtils.getInstance(BlackApplication.getContext()).show("error: 103");
            return null;
        }
    }

    private String judgeProvider(LocationManager lm) {
        List<String> providerList = lm.getProviders(true);
        if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            return LocationManager.NETWORK_PROVIDER;
        } else if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            return LocationManager.GPS_PROVIDER;
        } else {
            ToastUtils.getInstance(BlackApplication.getContext())
                    .show("需要同意所有权限才能运行 error:104");
//            Toast.makeText(getActivity(), "存在未同意的权限", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void showCurrentLocation() {
        try {
            if (mLatitude != 0 && mLongitude != 0) {
                URL url = NetworkUtils.buildUrlWithLonngtitdeLatitude(mLongitude, mLatitude);
                new LocationTask().execute(url);
            } else {
                mRefreshLayout.setRefreshing(false);
//                ToastUtils.getInstance(BlackApplication.getContext())
//                        .show("获取位置信息失败 error:107（缺少权限）");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(TAG, "debug4 onActivityCreated: " + " 得到执行");
        showCurrentLocation();
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showCurrentLocation();
            }
        });

        // 定位到的location
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
        mProgressBar.setVisibility(View.VISIBLE);

        //        showProgressDialog();
        NetworkUtils.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 通过runOnUiThread()方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.getInstance(BlackApplication.getContext())
                                .show("加载信息失败 error:105");
                        mProgressBar.setVisibility(View.GONE);
//                        closeProgressDialog();
//                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
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
                            mProgressBar.setVisibility(View.GONE);
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

    class LocationTask extends AsyncTask<URL, Boolean, String[]> {

        @Override
        protected String[] doInBackground(URL... urls) {
            if (urls != null) {
                try {
                    final String[] strings = new String[2];
                    String weatherStr;
                    weatherStr = NetworkUtils.sendRequestWithHttpConnection(urls[0]);
                    Weather weather = JsonUtils.handleWeatherResponse(weatherStr);
                    if (weather != null && "ok".equals(weather.status)) {
                        strings[0] = weather.basic.location;
                        strings[1] = weather.basic.weatherId;
                        publishProgress(false);
                        return strings;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    publishProgress(false);
                    return null;
                }
            }
            publishProgress(false);
            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            if (strings != null) {
                super.onPostExecute(strings);
                mCurrentLocation = strings[0];
                mCurrentWeatherId = strings[1];
                mCurrentLocationView.setText("当前位置：" + mCurrentLocation);
                mCurrentLocationView.setVisibility(View.VISIBLE);
            } else {
                mRefreshLayout.setRefreshing(false);
                ToastUtils.getInstance(BlackApplication.getContext())
                        .show("获取当前位置失败 error:106");
//
//                Toast.makeText(getActivity(), "获取当前位置失败", Toast.LENGTH_SHORT)
//                        .show();
            }
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            super.onProgressUpdate(values);
            mRefreshLayout.setRefreshing(values[0]);
        }
    }

    /* - - - - - - - - - - - - - - - - - - */
    /* - - - Loader callback methods - - - */
    /* - - - - - - - - - - - - - - - - - - */

    @NonNull
    @Override
    public Loader<String[]> onCreateLoader(int i, @Nullable Bundle bundle) {
        URL url = null;
        try {
            url = NetworkUtils.buildUrlWithLonngtitdeLatitude(mLongitude, mLatitude);
            Log.d(TAG, "debug4 onCreateLoader: " + url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new LocationLoader(getActivity(), url);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String[]> loader, String[] strings) {
        if (strings == null) {
            return;
        }
        mCurrentLocation = strings[0];
        mCurrentWeatherId = strings[1];
        mCurrentLocationView.setText("当前城市：" + mCurrentLocation);
        mCurrentLocationView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {
        fetchLongAndLat();
    }
}

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        mLocationClient.stop();
//    }

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
//}

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

//
//    private void requestLocation() {
////        Log.d(TAG, "debug3 requestLocation: " + "执行");
//        mLocationClient.start();
//    }


//        mLocationClient = new LocationClient(BlackApplication.getContext());
//        mLocationClient.registerLocationListener(new BDLocationListener() {
//            @Override
//            public void onReceiveLocation(BDLocation bdLocation) {
//                final double longitude = bdLocation.getLongitude();
//                final double latitude = bdLocation.getLatitude();
//                getActivity().getSupportLoaderManager().initLoader(1, null,
//                        new LoaderManager.LoaderCallbacks<String[]>() {
//                            @NonNull
//                            @Override
//                            public Loader<String[]> onCreateLoader(int i, @Nullable Bundle bundle) {
//                                URL url = null;
//                                try {
//                                    url = NetworkUtils.buildUrlWithLonngtitdeLatitude(longitude, latitude);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                                return new LocationLoader(getActivity(), url);
//                            }
//
//                            @Override
//                            public void onLoadFinished(@NonNull Loader<String[]> loader, String[] strings) {
//                                if (strings == null) {
////                                    Toast.makeText(getActivity(), "未知错误",
////                                            Toast.LENGTH_SHORT).show();
////                                    mProgressBar.setVisibility(View.GONE);
////                                    mCurrentLocationView.setVisibility(View.GONE);
////                                    mCurrentChooseView.setVisibility(View.VISIBLE);
//                                    return;
//                                }
//                                mCurrentLocation = strings[0];
//                                mCurrentWeatherId = strings[1];
//                                mCurrentLocationView.setText("当前城市：" + mCurrentLocation);
////                                mProgressBar.setVisibility(View.GONE);
////                                mCurrentChooseView.setVisibility(View.GONE);
//                                mCurrentLocationView.setVisibility(View.VISIBLE);
//                            }
//
//                            @Override
//                            public void onLoaderReset(@NonNull Loader<String[]> loader) {
//                            }
//                        });
//            }
//        });

/////////////////////////////