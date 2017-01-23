package com.example.chernenkovit.currencyrates.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.chernenkovit.currencyrates.R;

import static com.example.chernenkovit.currencyrates.data.DBHelper.SELECTED_RATES_BUY_NB_COLUMN;
import static com.example.chernenkovit.currencyrates.data.DBHelper.SELECTED_RATES_BUY_PB_COLUMN;
import static com.example.chernenkovit.currencyrates.data.DBHelper.SELECTED_RATES_CURRENCY_COLUMN;
import static com.example.chernenkovit.currencyrates.data.DBHelper.SELECTED_RATES_DATE_COLUMN;
import static com.example.chernenkovit.currencyrates.data.DBHelper.SELECTED_RATES_SALE_NB_COLUMN;
import static com.example.chernenkovit.currencyrates.data.DBHelper.SELECTED_RATES_SALE_PB_COLUMN;

/**
 * Custom adapter for data presenting.
 */
public class SelectedDateRatesAdapter extends CursorAdapter {
    String selectedDate;

    public SelectedDateRatesAdapter(Context context, Cursor c, ListView listView, int flags, String selectedDate) {
        super(context, c, flags);
        this.selectedDate = selectedDate;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.list_item_selected_date, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String date = cursor.getString(cursor.getColumnIndex(SELECTED_RATES_DATE_COLUMN));
        viewHolder.tv_currency_selected_date.setText(date);
        String currency = cursor.getString(cursor.getColumnIndex(SELECTED_RATES_CURRENCY_COLUMN));
        viewHolder.tv_currency.setText("1 " + currency);
        if (currency.equals("USD") || currency.equals("EUR") || currency.equals("RUB")) {
            viewHolder.eventsListRow.setBackgroundColor(context.getResources().getColor(R.color.indigo_light));
        } else {
            viewHolder.eventsListRow.setBackgroundColor(context.getResources().getColor(R.color.white));
        }
        viewHolder.tv_privatBank.setText(R.string.PrBank_title);
        viewHolder.tv_nbu.setText(R.string.NBU_title);
        String salePB = cursor.getString(cursor.getColumnIndex(SELECTED_RATES_SALE_PB_COLUMN));
        if (salePB.equals("0")) viewHolder.tv_sale_privatBank.setText("-");
        else viewHolder.tv_sale_privatBank.setText(salePB);
        String saleNB = cursor.getString(cursor.getColumnIndex(SELECTED_RATES_SALE_NB_COLUMN));
        viewHolder.tv_sale_nbu.setText(saleNB);
        String buyPB = cursor.getString(cursor.getColumnIndex(SELECTED_RATES_BUY_PB_COLUMN));
        if (buyPB.equals("0")) viewHolder.tv_buy_privatBank.setText("-");
        else viewHolder.tv_buy_privatBank.setText(buyPB);
        String buyNB = cursor.getString(cursor.getColumnIndex(SELECTED_RATES_BUY_NB_COLUMN));
        viewHolder.tv_buy_nbu.setText(buyNB);
    }

    private static class ViewHolder {
        final TextView tv_currency_selected_date;
        final TextView tv_privatBank;
        final TextView tv_nbu;
        final TextView tv_sale_privatBank;
        final TextView tv_sale_nbu;
        final TextView tv_buy_privatBank;
        final TextView tv_buy_nbu;
        final TextView tv_currency;
        final View eventsListRow;

        ViewHolder(View view) {
            tv_currency_selected_date = (TextView) view.findViewById(R.id.tv_currency_selected_date);
            tv_privatBank = (TextView) view.findViewById(R.id.tv_privatBank);
            tv_nbu = (TextView) view.findViewById(R.id.tv_nbu);
            tv_sale_privatBank = (TextView) view.findViewById(R.id.tv_sale_privatBank);
            tv_sale_nbu = (TextView) view.findViewById(R.id.tv_sale_nbu);
            tv_buy_privatBank = (TextView) view.findViewById(R.id.tv_buy_privatBank);
            tv_buy_nbu = (TextView) view.findViewById(R.id.tv_buy_nbu);
            tv_currency = (TextView) view.findViewById(R.id.tv_currency);
            eventsListRow = (View) view.findViewById(R.id.eventsListRow);
        }
    }
}
