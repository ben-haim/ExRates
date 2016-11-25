package com.example.chernenkovit.currencyrates.loaders;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import static com.example.chernenkovit.currencyrates.data.DBHelper.TABLE_NAME_MONTH_RATES;
import static com.example.chernenkovit.currencyrates.loaders.CurrentRatesCursorLoader.CURRENT_DATE_RATES_DB_URI;

/** Cursor loader for downloading data for chart. */
public class ChartLoader extends CursorLoader {
    private SQLiteDatabase database;
    private Cursor cursor;
    final Loader.ForceLoadContentObserver observer;
    public static final Uri MONTH_RATES_DB_URI = Uri.parse("sqlite://com.example.chernenkovit.currencyrates/Month_rates");

    public ChartLoader(Context context, SQLiteDatabase database) {
        super(context);
        this.database = database;
        this.observer = new Loader.ForceLoadContentObserver();
    }

    @Override
    public Cursor loadInBackground() {
        Cursor cursor = database.query(TABLE_NAME_MONTH_RATES,
                null,
                null,
                null,
                null,
                null,
                null);
        if (cursor != null) {
            cursor.getCount();
            cursor.registerContentObserver(this.observer);
            cursor.setNotificationUri(getContext().getContentResolver(), CURRENT_DATE_RATES_DB_URI);
        }
        return cursor;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (this.cursor != null) {
            deliverResult(this.cursor);
        }
        if (takeContentChanged() || this.cursor == null) {
            forceLoad();
        }
    }
}