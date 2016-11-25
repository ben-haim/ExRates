package com.example.chernenkovit.currencyrates.loaders;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import static com.example.chernenkovit.currencyrates.data.DBHelper.SELECTED_RATES_DATE_COLUMN;
import static com.example.chernenkovit.currencyrates.data.DBHelper.TABLE_NAME_SELECTED_RATES;

/** Cursor loader for downloading selected date rates. */
public class SelectedDateRatesCursorLoader extends CursorLoader {

    private SQLiteDatabase database;
    private Cursor cursor;
    String selectedDate;
    final ForceLoadContentObserver observer;
    public static final Uri SELECTED_DATE_RATES_DB_URI = Uri.parse("sqlite://com.example.chernenkovit.currencyrates/Selected_rates");

    public SelectedDateRatesCursorLoader(Context context, SQLiteDatabase database,String selectedDate) {
        super(context);
        this.database = database;
        this.selectedDate=selectedDate;
        this.observer = new ForceLoadContentObserver();
    }

    @Override
    public Cursor loadInBackground() {
        Cursor cursor = database.query(TABLE_NAME_SELECTED_RATES,
                null,
                SELECTED_RATES_DATE_COLUMN + "=?",
                new String[]{selectedDate},
                null,
                null,
                null);
        if (cursor != null) {
            cursor.getCount();
            cursor.registerContentObserver(this.observer);
            cursor.setNotificationUri(getContext().getContentResolver(), SELECTED_DATE_RATES_DB_URI);
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
