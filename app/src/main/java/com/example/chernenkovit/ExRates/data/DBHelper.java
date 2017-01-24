package com.example.chernenkovit.ExRates.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/** Helper class for database creating. */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "RATES_DATABASE";
    private static final int DATABASE_VERSION = 1;

    //current date rates table
    public static final String TABLE_NAME_CURRENT_RATES = "Current_rates";
    public static final String CURRENT_RATES_ID_COLUMN = "_id";
    public static final String CURRENT_RATES_CURRENCY_COLUMN = "currency";
    public static final String CURRENT_RATES_BASE_CURRENCY_COLUMN = "base_currency";
    public static final String CURRENT_RATES_BUY_COLUMN = "buy";
    public static final String CURRENT_RATES_UNIT_COLUMN = "unit";
    public static final String CURRENT_RATES_DATE_COLUMN = "date";

    //last 30 days rates table
    public static final String TABLE_NAME_MONTH_RATES = "Month_rates";
    public static final String MONTH_RATES_ID_COLUMN = "_id";
    public static final String MONTH_RATES_CURRENCY_COLUMN = "currency";
    public static final String MONTH_RATES_BASE_CURRENCY_COLUMN = "base_currency";
    public static final String MONTH_RATES_BUY_NB_COLUMN = "buy_NB";
    public static final String MONTH_RATES_SALE_NB_COLUMN = "sale_NB";
    public static final String MONTH_RATES_BUY_PB_COLUMN = "buy_PB";
    public static final String MONTH_RATES_SALE_PB_COLUMN = "sale_PB";
    public static final String MONTH_RATES_DATE_COLUMN = "date";

    //selected date rates table
    public static final String TABLE_NAME_SELECTED_RATES = "Selected_rates";
    public static final String SELECTED_RATES_ID_COLUMN = "_id";
    public static final String SELECTED_RATES_CURRENCY_COLUMN = "currency";
    public static final String SELECTED_RATES_BASE_CURRENCY_COLUMN = "base_currency";
    public static final String SELECTED_RATES_BUY_NB_COLUMN = "buy_NB";
    public static final String SELECTED_RATES_SALE_NB_COLUMN = "sale_NB";
    public static final String SELECTED_RATES_BUY_PB_COLUMN = "buy_PB";
    public static final String SELECTED_RATES_SALE_PB_COLUMN = "sale_PB";
    public static final String SELECTED_RATES_DATE_COLUMN = "date";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME_CURRENT_RATES + " (" +
                CURRENT_RATES_ID_COLUMN + " integer primary key autoincrement, " +
                CURRENT_RATES_CURRENCY_COLUMN + " text, " +
                CURRENT_RATES_BASE_CURRENCY_COLUMN + " text, " +
                CURRENT_RATES_BUY_COLUMN + " text, " +
                CURRENT_RATES_UNIT_COLUMN + " text, " +
                CURRENT_RATES_DATE_COLUMN + " text" +
                ");"
        );

        db.execSQL("create table " + TABLE_NAME_MONTH_RATES + " (" +
                MONTH_RATES_ID_COLUMN + " integer primary key autoincrement, " +
                MONTH_RATES_CURRENCY_COLUMN + " text, " +
                MONTH_RATES_BASE_CURRENCY_COLUMN + " text, " +
                MONTH_RATES_BUY_NB_COLUMN + " integer, " +
                MONTH_RATES_SALE_NB_COLUMN + " integer, " +
                MONTH_RATES_BUY_PB_COLUMN + " integer, " +
                MONTH_RATES_SALE_PB_COLUMN + " integer, " +
                MONTH_RATES_DATE_COLUMN + " text" +
                ");"
        );

        db.execSQL("create table " + TABLE_NAME_SELECTED_RATES + " (" +
                SELECTED_RATES_ID_COLUMN + " integer primary key autoincrement, " +
                SELECTED_RATES_CURRENCY_COLUMN + " text, " +
                SELECTED_RATES_BASE_CURRENCY_COLUMN + " text, " +
                SELECTED_RATES_BUY_NB_COLUMN + " integer, " +
                SELECTED_RATES_SALE_NB_COLUMN + " integer, " +
                SELECTED_RATES_BUY_PB_COLUMN + " integer, " +
                SELECTED_RATES_SALE_PB_COLUMN + " integer, " +
                SELECTED_RATES_DATE_COLUMN + " text" +
                ");"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
