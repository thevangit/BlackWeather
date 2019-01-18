package com.blackweather.android;

import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackweather.android.customView.CircleView;
import com.blackweather.android.customView.DataView;
import com.blackweather.android.gson.Forecast;
import com.blackweather.android.gson.Weather;

/**
 * 定义RecyclerView的adapter
 */
public class BlackAdapter extends RecyclerView.Adapter<BlackAdapter.BlackHolder> {

    // 不同的item view的常量
    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE = 1;

    private Weather mWeather;

    public BlackAdapter() {
        super();
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
        private ImageView moreImageView;
        // today的view
        private CircleView humidityCircleView;
        private DataView humidityDataView;
        private CircleView precipCircleView;
        private DataView precipDataView;

        public BlackHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.date);
            iconImageView = itemView.findViewById(R.id.weather_icon);
            weatherDescriptionTextView = itemView.findViewById(R.id.weather_description);
            maxTempTextView = itemView.findViewById(R.id.high_temperature);
            minTempTextView = itemView.findViewById(R.id.low_temperature);
            moreImageView = itemView.findViewById(R.id.more);
            humidityCircleView = itemView.findViewById(R.id.humidity_circle);
            humidityDataView = itemView.findViewById(R.id.humidity_data);
            precipCircleView = itemView.findViewById(R.id.precip_circle);
            precipDataView = itemView.findViewById(R.id.precip_data);
        }
    }

    @Override
    public BlackHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        int layoutId;
        switch (viewType) {
            case VIEW_TYPE_TODAY:
                layoutId = R.layout.home_today_item;
                break;
            case VIEW_TYPE_FUTURE:
                layoutId = R.layout.home_list_item;
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
        switch (viewType) {
            case VIEW_TYPE_TODAY:
                blackHolder.dateTextView.setText(forecast.date);
                blackHolder.weatherDescriptionTextView.setText(forecast.textDay);
                blackHolder.maxTempTextView.setText(forecast.tempMax + "\u00b0");
                blackHolder.minTempTextView.setText(forecast.tempMin + "\u00b0");
                blackHolder.humidityCircleView.setData(Float.parseFloat(forecast.humidity),
                        100);
                blackHolder.humidityDataView.setData(Float.parseFloat(forecast.humidity),
                        "%");
                blackHolder.precipCircleView.setData(Float.parseFloat(forecast.precip),
                        100);
                blackHolder.precipDataView.setData(Float.parseFloat(forecast.precip),
                        "%");
                break;
            case VIEW_TYPE_FUTURE:
                blackHolder.dateTextView.setText(forecast.date);
                blackHolder.weatherDescriptionTextView.setText(forecast.textDay);
                blackHolder.maxTempTextView.setText(forecast.tempMax + "\u00b0");
                blackHolder.minTempTextView.setText(forecast.tempMin + "\u00b0");
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public int getItemCount() {
        return mWeather.forecastList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_TODAY: VIEW_TYPE_FUTURE ;
    }

}
