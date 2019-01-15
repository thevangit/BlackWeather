package com.blackweather.android.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * 3.ContentProvider
 */
public class BlackWeatherProvider extends ContentProvider {

    // 定义给UriMather使用的常量id
    public static final int CODE_WEATHER = 100;
    public static final int CODE_WEATHER_WITH_DATE = 101;

    private static UriMatcher sUriMatcher = buildUriMatcher();
    private BlackWeatherDbHelper mDbHelper;

    /**
     * 1)build UriMatcher:
     */
    public static UriMatcher buildUriMatcher() {
        // step1:获取UriMatcher的实例
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // step2:添加匹配
        final String authority = BlackWeatherContract.CONTENT_AUTHORIRY;
        final String path = BlackWeatherContract.PATH_WEATHER;
        uriMatcher.addURI(authority, path, CODE_WEATHER);
        uriMatcher.addURI(authority, path + "/#", CODE_WEATHER_WITH_DATE);
        return uriMatcher;
    }

    /**
     * 2)implement onCrate():返回true表示创建完成
     */
    @Override
    public boolean onCreate() {
        mDbHelper = new BlackWeatherDbHelper(getContext());
        return true;
    }

    /**
     * 3)implement CRUD method:查询方法，根据uri查询整个表或者某一项
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        final SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case CODE_WEATHER:
                cursor = database.query(BlackWeatherContract.WeatherEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_WEATHER_WITH_DATE:
                String date = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{date};
                cursor = database.query(BlackWeatherContract.WeatherEntry.TABLE_NAME,
                        projection,
                        BlackWeatherContract.WeatherEntry.COLUMN_DATE + " = ?",
                        selectionArguments,
                        null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // 设置被观察者
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * 3)implement CRUD method:不用重写inset方法，用bulkInsert代替
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    /**
     * 3)implement CRUD method:批量插入
     */
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsInserted = 0;
        switch (sUriMatcher.match(uri)) {
            case CODE_WEATHER:
                // 开始更新数据
                database.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        String dateStr = value
                                .getAsString(BlackWeatherContract.WeatherEntry.COLUMN_DATE);
                        long id = database.insert(BlackWeatherContract.WeatherEntry.TABLE_NAME,
                                null, value);
                        if (id != -1) rowsInserted++;
                    }
                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                }
                // 设置被观察者
                if (rowsInserted > 0) getContext().getContentResolver().notifyChange(uri, null);
                return rowsInserted;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    /**
     * 3)implement CRUD method:
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted = 0;
        // 避免因删掉整个table而造成返回不了rowsDeleted
        if (selection == null) selection = "1";
        switch (sUriMatcher.match(uri)) {
            case CODE_WEATHER:
                rowsDeleted = mDbHelper.getWritableDatabase()
                        .delete(BlackWeatherContract.WeatherEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // 设置被观察者
        if (rowsDeleted != 0) getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    /**
     * 3)implement CRUD method:不用重写
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * 3)implement CRUD method:不用重写
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }
}
