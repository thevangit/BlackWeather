package com.blackweather.android.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackweather.android.BlackApplication;
import com.blackweather.android.R;
import com.blackweather.android.customView.BarView;
import com.blackweather.android.customView.CircleView;
import com.blackweather.android.customView.DataView;
import com.blackweather.android.gson.Forecast;
import com.blackweather.android.gson.Weather;
import com.blackweather.android.utilities.BlackUtils;
import com.blackweather.android.utilities.PreferenceUtils;

/**
 * 定义RecyclerView的adapter
 */
public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.BlackHolder> {

    // 不同的item view的常量
    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE = 1;
    private static final float PERCENTAGE = 100;
    private static final float RATE = 15;

    private Weather mWeather;

    private final OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClickListener(int position);
    }

    public MainRecyclerAdapter(OnItemClickListener listener) {
        super();
        mListener = listener;
    }

    public void setData(Weather weather) {
        mWeather = weather;
        if (mWeather != null) {
            notifyDataSetChanged();
        }
    }

    /**
     * 定义ViewHolder类
     */
    class BlackHolder extends RecyclerView.ViewHolder {

        // today的view和future的view共有的子view
        private TextView dateTextView;
        private ImageView iconImageView;
        private TextView weatherDescriptionTextView;
        private TextView maxTempTextView;
        private TextView minTempTextView;
//        private View lineView;
        // today的view
        private TextView loctionTextView;
        private CircleView humidityCircleView;
        private DataView humidityDataView;
        private CircleView precipCircleView;
        private DataView precipDataView;
        private BarView windBarView;
        private DataView windDataView;
        private BarView uvBarView;
        private DataView uvDataView;
        private DataView highDataView;
        private DataView lowDataView;


        public BlackHolder(View itemView) {
            super(itemView);
            loctionTextView = itemView.findViewById(R.id.item_location);
            dateTextView = itemView.findViewById(R.id.date);
            iconImageView = itemView.findViewById(R.id.weather_icon);
            weatherDescriptionTextView = itemView.findViewById(R.id.weather_description);
            maxTempTextView = itemView.findViewById(R.id.high_temperature);
            minTempTextView = itemView.findViewById(R.id.low_temperature);
            humidityCircleView = itemView.findViewById(R.id.humidity_circle);
            humidityDataView = itemView.findViewById(R.id.humidity_data);
            precipCircleView = itemView.findViewById(R.id.precip_circle);
            precipDataView = itemView.findViewById(R.id.precip_data);
            windBarView = itemView.findViewById(R.id.wind_barView);
            windDataView = itemView.findViewById(R.id.wind_data);
            uvBarView = itemView.findViewById(R.id.uv_bar);
            uvDataView = itemView.findViewById(R.id.uv_data);
            highDataView = itemView.findViewById(R.id.high_temperature_data);
            lowDataView = itemView.findViewById(R.id.low_temperature_data);

            // 给ItemView设置监听
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClickListener(getAdapterPosition());
                }
            });
        }
    }

    @Override
    public BlackHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        int layoutId;
        switch (viewType) {
            case VIEW_TYPE_TODAY:
                layoutId = R.layout.item_home_first;
                break;
            case VIEW_TYPE_FUTURE:
                layoutId = R.layout.item_home;
                break;
            default:
                throw new IllegalArgumentException();
        }
        View view = LayoutInflater.from(BlackApplication.getContext()).inflate(layoutId,
                viewGroup, false);
        return new BlackHolder(view);
    }

    @Override
    public void onBindViewHolder(BlackHolder blackHolder, int position) {
        Forecast forecast = mWeather.forecastList.get(position);
        int viewType = getItemViewType(position);
        float highTempF = Float.parseFloat(forecast.tempMax);
        float lowTempf = Float.parseFloat(forecast.tempMin);
        if (!PreferenceUtils.isMetric(BlackApplication.getContext())) {
            highTempF = BlackUtils.celsiusToFahrenheit(highTempF);
            lowTempf = BlackUtils.celsiusToFahrenheit(lowTempf);
        }
        switch (viewType) {
            case VIEW_TYPE_TODAY:
                String windScaleStr = forecast.windScale;
                String windScale = windScaleStr.split("-")[1];
                float windScaleF = Float.parseFloat(windScale);

                blackHolder.loctionTextView.setText(mWeather.basic.location);
                blackHolder.dateTextView.setText(forecast.date);
                blackHolder.weatherDescriptionTextView.setText(forecast.textDay);
                blackHolder.highDataView.setData(Math.round(highTempF),"\u00b0");
                blackHolder.lowDataView.setData(Math.round(lowTempf),"\u00b0");
                blackHolder.humidityCircleView.setData(Float.parseFloat(forecast.humidity),
                        100);
                blackHolder.humidityDataView.setData(Float.parseFloat(forecast.humidity),
                        "%");
                blackHolder.precipCircleView.setData(Float.parseFloat(forecast.precip),
                        100);
                blackHolder.precipDataView.setData(Float.parseFloat(forecast.precip),
                        "%");
                blackHolder.iconImageView.setImageResource(BlackUtils.
                        getIcoResWithCode(Integer.parseInt(forecast.codeDay)));
                blackHolder.windBarView.setData(windScaleF, 15);
                blackHolder.windDataView.setData(windScaleF, null);
                blackHolder.uvBarView.setData(Float.parseFloat(forecast.uv), 15);
                blackHolder.uvDataView.setData(Float.parseFloat(forecast.uv), null);
                break;
            case VIEW_TYPE_FUTURE:
                blackHolder.dateTextView.setText(forecast.date);
                blackHolder.weatherDescriptionTextView.setText(forecast.textDay);
                blackHolder.maxTempTextView.setText(String.
                        valueOf(Math.round(highTempF)) + "\u00b0");
                blackHolder.minTempTextView.setText(String.
                        valueOf(Math.round(lowTempf)) + "\u00b0");
                blackHolder.iconImageView.setImageResource(BlackUtils.
                        getIcoResWithCode(Integer.parseInt(forecast.codeDay)));
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public int getItemCount() {
        return mWeather == null ? 0 : mWeather.forecastList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_TODAY: VIEW_TYPE_FUTURE ;
    }

}
