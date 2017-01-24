package com.apps.chernenkovit.ExRates.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.apps.chernenkovit.ExRates.R;

import static com.apps.chernenkovit.ExRates.data.DBHelper.CURRENT_RATES_BASE_CURRENCY_COLUMN;
import static com.apps.chernenkovit.ExRates.data.DBHelper.CURRENT_RATES_BUY_COLUMN;
import static com.apps.chernenkovit.ExRates.data.DBHelper.CURRENT_RATES_CURRENCY_COLUMN;

/**
 * Custom adapter for data presenting.
 */
public class CurrentRatesAdapter extends CursorAdapter {
    private float rate;
    private String convertedRate;

    public CurrentRatesAdapter(Context context, Cursor c, ListView listView, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.list_item_current_date, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
//        String date = cursor.getString(cursor.getColumnIndex(CURRENT_RATES_DATE_COLUMN));
//        viewHolder.tv_current_date.setText(date);
        String currency = cursor.getString(cursor.getColumnIndex(CURRENT_RATES_CURRENCY_COLUMN));
        viewHolder.tv_currency_current_date.setText("1 " + currency);
        String bank = cursor.getString(cursor.getColumnIndex(CURRENT_RATES_BASE_CURRENCY_COLUMN));
        if (bank.equals("UA")){
            viewHolder.tv_bank.setText(R.string.NBU);
        }
        float rawRate = Float.parseFloat(cursor.getString(cursor.getColumnIndex(CURRENT_RATES_BUY_COLUMN)));
        if (currency.equals(context.getString(R.string.EUR_title)) && bank.equals(context.getString(R.string.RU_title))) {
            rate = rawRate / 10000;
            convertedRate = rate + " RUB";
        } else if (currency.equals(context.getString(R.string.USD_title)) && bank.equals(context.getString(R.string.RU_title))) {
            rate = rawRate / 10000;
            convertedRate = rate + " RUB";
        } else if (currency.equals(context.getString(R.string.EUR_title)) && bank.equals(context.getString(R.string.UA_title))) {
            rate = rawRate / 1000000;
            convertedRate = rate + " UAH";
            viewHolder.image.setImageResource(R.drawable.eu);
            viewHolder.image.setScaleX((float) 0.8);
            viewHolder.image.setScaleY((float) 0.8);
        } else if (currency.equals("RUR") && bank.equals(context.getString(R.string.UA_title))) {
            rate = rawRate / 10000;
            convertedRate = rate + " UAH";
            viewHolder.tv_currency_current_date.setText("10 " + currency);
            viewHolder.image.setImageResource(R.drawable.russia);
            viewHolder.image.setScaleX((float) 0.8);
            viewHolder.image.setScaleY((float) 0.8);
        } else if (currency.equals(context.getString(R.string.USD_title)) && bank.equals(context.getString(R.string.UA_title))) {
            rate = rawRate / 1000000;
            convertedRate = rate + " UAH";
            viewHolder.image.setImageResource(R.drawable.usa);
            viewHolder.image.setScaleX((float) 0.8);
            viewHolder.image.setScaleY((float) 0.8);
        } else if (currency.equals("XAU") && bank.equals("UA")) {
            viewHolder.tv_currency_current_date.setText("1 t.o. \nGOLD");
            rate = rawRate / 10000;
            convertedRate = rate + " UAH";
            viewHolder.image.setImageResource(R.drawable.gold);
            viewHolder.image.setScaleX((float) 1);
            viewHolder.image.setScaleY((float) 1);
        } else if (currency.equals("XAG") && bank.equals("UA")) {
            viewHolder.tv_currency_current_date.setText("1 t.o. \nSILVER");
            rate = rawRate / 10000;
            convertedRate = rate + " UAH";
            viewHolder.image.setImageResource(R.drawable.silver);
            viewHolder.image.setScaleX((float) 1);
            viewHolder.image.setScaleY((float) 1);
        }
        viewHolder.tv_buy.setText(convertedRate);
    }

    private static class ViewHolder {
        final TextView tv_currency_current_date;
        //        final TextView tv_current_date;
        final TextView tv_bank;
        final TextView tv_buy;
        final ImageView image;

        ViewHolder(View view) {
            tv_currency_current_date = (TextView) view.findViewById(R.id.tv_currency_current_date);
//            tv_current_date = (TextView) view.findViewById(R.id.tv_current_date);
            tv_bank = (TextView) view.findViewById(R.id.tv_bank);
            tv_buy = (TextView) view.findViewById(R.id.tv_buy);
            image = (ImageView) view.findViewById(R.id.iv_currency_current_image);

        }
    }
}
