package com.example.chernenkovit.currencyrates.UI;


import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import com.example.chernenkovit.currencyrates.R;
import com.example.chernenkovit.currencyrates.adapters.SelectedDateRatesAdapter;
import com.example.chernenkovit.currencyrates.data.DBHelper;
import com.example.chernenkovit.currencyrates.loaders.SelectedDateRatesCursorLoader;

import java.util.Calendar;

import static com.example.chernenkovit.currencyrates.data.DBHelper.SELECTED_RATES_DATE_COLUMN;
import static com.example.chernenkovit.currencyrates.data.DBHelper.TABLE_NAME_SELECTED_RATES;


/** Fragment for selected date rates with loader implementation. */
public class SelectedDateRatesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int CURSOR_LOADER_SELECTED_DATE_RATES = 101;
    DatePickerDialog datePickerDialog;
    FloatingActionButton fab_select_date;
    int selectedYear, selectedMonth, selectedDay;
    int nowYear, nowMonth, nowDay, minYear, minMonth, minDay;
    View header;
    ListView lv_selected_date_rates;
    String selectedDate;
    DBHelper dbHelper;
    SQLiteDatabase database;
    SelectedDateRatesAdapter selectedDateRatesAdapter;

    public SelectedDateRatesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_selected_date_rates, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lv_selected_date_rates = (ListView) view.findViewById(R.id.lv_selected_date_rates);
        LayoutInflater inflater = getLayoutInflater(null);
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.header_selected_date_rates, lv_selected_date_rates,
                false);
        lv_selected_date_rates.addHeaderView(header, null, false);

        //set today's date
        Calendar c = Calendar.getInstance();
        nowYear = c.get(Calendar.YEAR);
        nowMonth = c.get(Calendar.MONTH);
        nowDay = c.get(Calendar.DAY_OF_MONTH);

        //initialize FAB
        fab_select_date = (FloatingActionButton) view.findViewById(R.id.fab_select_date);
        fab_select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogue();
            }
        });

        //check for records in database
        dbHelper = new DBHelper(getActivity());
        database = dbHelper.getWritableDatabase();
        if (((MainActivity) getActivity()).getSelectedDate() != null) {
            selectedDate = ((MainActivity) getActivity()).getSelectedDate();

            Cursor cursor = database.query(TABLE_NAME_SELECTED_RATES,
                    null,
                    SELECTED_RATES_DATE_COLUMN + "=?",
                    new String[]{selectedDate},
                    null,
                    null,
                    null);
            if (cursor == null && !cursor.moveToFirst())
                Toast.makeText(getActivity(), "No rates", Toast.LENGTH_SHORT).show();
            else {
                selectedDateRatesAdapter = new SelectedDateRatesAdapter(getActivity(), cursor, lv_selected_date_rates, 0, selectedDate);
                lv_selected_date_rates.setAdapter(selectedDateRatesAdapter);
                getActivity().getSupportLoaderManager().initLoader(CURSOR_LOADER_SELECTED_DATE_RATES, null, this);
            }
        } else Toast.makeText(getActivity(), "Please, select a date", Toast.LENGTH_LONG).show();
    }

    //show date picker dialog and set date limits
    private void showDialogue() {
        datePickerDialog = new DatePickerDialog(getActivity(), selectDateCallback, nowYear, nowMonth, nowDay);
        Calendar c = Calendar.getInstance();
        c.set(2015, 00, 01);
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    //date picker dialog select listener
    private DatePickerDialog.OnDateSetListener selectDateCallback = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            selectedYear = i;
            selectedMonth = i1 + 1;
            selectedDay = i2;
            selectedDate = selectedDay + "." + selectedMonth + "." + selectedYear;
            ((MainActivity) getActivity()).setSelectedDate(selectedDate);
//            selectedDate = ((MainActivity) getActivity()).getSelectedDate();
            ((MainActivity) getActivity()).getService().loadSelectedDateRates();
            getActivity().getSupportLoaderManager().restartLoader(CURSOR_LOADER_SELECTED_DATE_RATES, null, SelectedDateRatesFragment.this);
        }
    };

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case CURSOR_LOADER_SELECTED_DATE_RATES:
                return new SelectedDateRatesCursorLoader(getActivity(), database, selectedDate);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        selectedDateRatesAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
