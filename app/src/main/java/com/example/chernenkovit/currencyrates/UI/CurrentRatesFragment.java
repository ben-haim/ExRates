package com.example.chernenkovit.currencyrates.UI;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.chernenkovit.currencyrates.R;
import com.example.chernenkovit.currencyrates.adapters.CurrentRatesAdapter;
import com.example.chernenkovit.currencyrates.data.DBHelper;
import com.example.chernenkovit.currencyrates.loaders.CurrentRatesCursorLoader;

import static com.example.chernenkovit.currencyrates.data.DBHelper.TABLE_NAME_CURRENT_RATES;

/** Fragment for current rates with loader implementation. */
public class CurrentRatesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int CURSOR_LOADER_CURRENT_RATES = 102;

    private ListView currentRateslistView;
    private CurrentRatesAdapter currentRatesAdapter;
    private DBHelper dbHelper;
    private SQLiteDatabase database;


    public CurrentRatesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_current_rates, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentRateslistView = (ListView) view.findViewById(R.id.lv_current_rates);
        LayoutInflater inflater = getLayoutInflater(null);
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.header_current_rates, currentRateslistView,
                false);
        currentRateslistView.addHeaderView(header, null, false);

        dbHelper = new DBHelper(getActivity());
        database = dbHelper.getWritableDatabase();

        Cursor cursor = database.query(TABLE_NAME_CURRENT_RATES,
                null,
                null,
                null,
                null,
                null,
                null);
        if (cursor == null && !cursor.moveToFirst())
            Toast.makeText(getActivity(), "No current rates", Toast.LENGTH_SHORT).show();
        else {
            currentRatesAdapter = new CurrentRatesAdapter(getActivity(), cursor, currentRateslistView, 0);
            currentRateslistView.setAdapter(currentRatesAdapter);
            getActivity().getSupportLoaderManager().initLoader(CURSOR_LOADER_CURRENT_RATES, null, this);
        }


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case CURSOR_LOADER_CURRENT_RATES:
                return new CurrentRatesCursorLoader(getActivity(), database);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        currentRatesAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
