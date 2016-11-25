package com.example.chernenkovit.currencyrates.UI;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chernenkovit.currencyrates.R;
import com.example.chernenkovit.currencyrates.data.DBHelper;
import com.example.chernenkovit.currencyrates.loaders.ChartLoader;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static com.example.chernenkovit.currencyrates.data.DBHelper.MONTH_RATES_CURRENCY_COLUMN;
import static com.example.chernenkovit.currencyrates.data.DBHelper.MONTH_RATES_DATE_COLUMN;
import static com.example.chernenkovit.currencyrates.data.DBHelper.MONTH_RATES_SALE_PB_COLUMN;
import static com.example.chernenkovit.currencyrates.data.DBHelper.TABLE_NAME_MONTH_RATES;
import static java.lang.Float.parseFloat;

/** Fragment with charting implementation. */
public class ChartFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int CHART_CURSOR_LOADER = 103;

    LineChart chart;
    DBHelper dbHelper;
    SQLiteDatabase database;
    Map<Date, String> mapUSD;
    Map<Date, String> mapEUR;
    DateFormat dateFormat;
    Map<Date, String> sortedMapUSD;
    Map<Date, String> sortedMapEUR;


    public ChartFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_graphic, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbHelper = new DBHelper(getActivity());
        database = dbHelper.getWritableDatabase();
        mapUSD = new HashMap<>();
        mapEUR = new HashMap<>();
        chart = (LineChart) view.findViewById(R.id.chart);

        dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        //check for records in database
        Cursor cursor = database.query(TABLE_NAME_MONTH_RATES, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            getActivity().getSupportLoaderManager().initLoader(CHART_CURSOR_LOADER, null, this);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    //load, convert and sort USD currency rates for the last 30 days term
    private void loadSortUsdData(Cursor cursor) {
        cursor = database.query(TABLE_NAME_MONTH_RATES, null, MONTH_RATES_CURRENCY_COLUMN + "=?", new String[]{"USD"}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            while (cursor.moveToNext()) {
                Date date = null;
                try {
                    date = dateFormat.parse(cursor.getString(cursor.getColumnIndex(MONTH_RATES_DATE_COLUMN)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String rate = cursor.getString(cursor.getColumnIndex(MONTH_RATES_SALE_PB_COLUMN));
                mapUSD.put(date, rate);
            }

            sortedMapUSD = new TreeMap<>(new Comparator<Date>() {
                @Override
                public int compare(Date date, Date t1) {
                    return date.compareTo(t1);
                }
            });

            for (Map.Entry<Date, String> entry : mapUSD.entrySet()) {
                sortedMapUSD.put(entry.getKey(), entry.getValue());
            }

           /* for (Map.Entry<Date, String> entry : sortedMapUSD.entrySet()) {
                String day = dateFormat.format(entry.getKey());
                String value = entry.getValue();
            }*/

        }
        if (cursor != null) {
            cursor.close();
        }
    }

    //load, convert and sort EUR currency rates for the last 30 days term
    private void loadSortEurData(Cursor cursor) {
        cursor = database.query(TABLE_NAME_MONTH_RATES, null, MONTH_RATES_CURRENCY_COLUMN + "=?", new String[]{"EUR"}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            while (cursor.moveToNext()) {
                Date date = null;
                try {
                    date = dateFormat.parse(cursor.getString(cursor.getColumnIndex(MONTH_RATES_DATE_COLUMN)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String rate = cursor.getString(cursor.getColumnIndex(MONTH_RATES_SALE_PB_COLUMN));
                mapEUR.put(date, rate);
            }

            sortedMapEUR = new TreeMap<>(new Comparator<Date>() {
                @Override
                public int compare(Date date, Date t1) {
                    return date.compareTo(t1);
                }
            });

            for (Map.Entry<Date, String> entry : mapEUR.entrySet()) {
                sortedMapEUR.put(entry.getKey(), entry.getValue());
            }

           /* for (Map.Entry<Date, String> entry : sortedMapEUR.entrySet()) {
                String day = dateFormat.format(entry.getKey());
                String value = entry.getValue();
            }*/
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    //dataset for chart
    private ArrayList<ILineDataSet> getDataSet() {
        ArrayList<ILineDataSet> dataSets = null;
        ArrayList<Entry> valueSet1 = new ArrayList<>();
        ArrayList<Entry> valueSet2 = new ArrayList<>();
        int i = 0;
        int j = 0;
        if (!sortedMapUSD.isEmpty() || !sortedMapEUR.isEmpty()) {
            for (Map.Entry<Date, String> entry : sortedMapUSD.entrySet()) {
                String value = entry.getValue();
                Entry v1e1 = new Entry(i = i + 1, parseFloat(value));
                valueSet1.add(v1e1);
            }

            for (Map.Entry<Date, String> entry : sortedMapEUR.entrySet()) {
                String value = entry.getValue();
                Entry v1e1 = new Entry(j = j + 1, Float.parseFloat(value));
                valueSet2.add(v1e1);
            }

            LineDataSet barDataSet1 = new LineDataSet(valueSet1, "PivatBank USD sale's rate for last 30 days");
            LineDataSet barDataSet2 = new LineDataSet(valueSet2, "PivatBank EUR sale's rate for last 30 days");
            barDataSet1.setColor(Color.GREEN);
            barDataSet2.setColor(Color.MAGENTA);

            dataSets = new ArrayList<>();
            dataSets.add(barDataSet1);
            dataSets.add(barDataSet2);

        }
        if (!dataSets.isEmpty()) return dataSets;
        else {
            Toast.makeText(getActivity(), "No data records loaded", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case CHART_CURSOR_LOADER:
                return new ChartLoader(getActivity(), database);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //load, convert and sort currency rates for the last 30 days term
        loadSortUsdData(cursor);
        loadSortEurData(cursor);

        //chart initialize
        LineData data = new LineData(getDataSet());
        chart.setData(data);
        chart.animateXY(1000, 1000);
        chart.getXAxis().setEnabled(false);
        chart.setDescription(null);
        chart.invalidate();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}