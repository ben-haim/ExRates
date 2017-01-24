package com.example.chernenkovit.ExRates;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.chernenkovit.ExRates.UI.MainActivity;
import com.example.chernenkovit.ExRates.api.PBApi;
import com.example.chernenkovit.ExRates.data.DBHelper;
import com.example.chernenkovit.ExRates.loaders.SelectedDateRatesCursorLoader;
import com.example.chernenkovit.ExRates.model.CurrentRates;
import com.example.chernenkovit.ExRates.model.DateRates;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

import static com.example.chernenkovit.ExRates.data.DBHelper.CURRENT_RATES_BASE_CURRENCY_COLUMN;
import static com.example.chernenkovit.ExRates.data.DBHelper.CURRENT_RATES_BUY_COLUMN;
import static com.example.chernenkovit.ExRates.data.DBHelper.CURRENT_RATES_CURRENCY_COLUMN;
import static com.example.chernenkovit.ExRates.data.DBHelper.CURRENT_RATES_DATE_COLUMN;
import static com.example.chernenkovit.ExRates.data.DBHelper.CURRENT_RATES_UNIT_COLUMN;
import static com.example.chernenkovit.ExRates.data.DBHelper.MONTH_RATES_BASE_CURRENCY_COLUMN;
import static com.example.chernenkovit.ExRates.data.DBHelper.MONTH_RATES_BUY_NB_COLUMN;
import static com.example.chernenkovit.ExRates.data.DBHelper.MONTH_RATES_BUY_PB_COLUMN;
import static com.example.chernenkovit.ExRates.data.DBHelper.MONTH_RATES_CURRENCY_COLUMN;
import static com.example.chernenkovit.ExRates.data.DBHelper.MONTH_RATES_DATE_COLUMN;
import static com.example.chernenkovit.ExRates.data.DBHelper.MONTH_RATES_SALE_NB_COLUMN;
import static com.example.chernenkovit.ExRates.data.DBHelper.MONTH_RATES_SALE_PB_COLUMN;
import static com.example.chernenkovit.ExRates.data.DBHelper.SELECTED_RATES_BASE_CURRENCY_COLUMN;
import static com.example.chernenkovit.ExRates.data.DBHelper.SELECTED_RATES_BUY_NB_COLUMN;
import static com.example.chernenkovit.ExRates.data.DBHelper.SELECTED_RATES_BUY_PB_COLUMN;
import static com.example.chernenkovit.ExRates.data.DBHelper.SELECTED_RATES_CURRENCY_COLUMN;
import static com.example.chernenkovit.ExRates.data.DBHelper.SELECTED_RATES_DATE_COLUMN;
import static com.example.chernenkovit.ExRates.data.DBHelper.SELECTED_RATES_SALE_NB_COLUMN;
import static com.example.chernenkovit.ExRates.data.DBHelper.SELECTED_RATES_SALE_PB_COLUMN;
import static com.example.chernenkovit.ExRates.data.DBHelper.TABLE_NAME_CURRENT_RATES;
import static com.example.chernenkovit.ExRates.data.DBHelper.TABLE_NAME_MONTH_RATES;
import static com.example.chernenkovit.ExRates.data.DBHelper.TABLE_NAME_SELECTED_RATES;
import static com.example.chernenkovit.ExRates.loaders.ChartLoader.MONTH_RATES_DB_URI;
import static com.example.chernenkovit.ExRates.loaders.CurrentRatesCursorLoader.CURRENT_DATE_RATES_DB_URI;
import static com.example.chernenkovit.ExRates.utils.Const.NOTIFICATION_ICON_ID;
import static com.example.chernenkovit.ExRates.utils.Const.NOTIFICATION_ID_CURRENT_RATES;
import static com.example.chernenkovit.ExRates.utils.Const.SELECTED_DATE;
import static com.example.chernenkovit.ExRates.utils.Const.SELECTED_TIME_HOUR;
import static com.example.chernenkovit.ExRates.utils.Const.SELECTED_TIME_MINUTE;
import static com.example.chernenkovit.ExRates.utils.Const.SHARED_PREF_SELECTED_DATE;
import static com.example.chernenkovit.ExRates.utils.Utils.getNotificationIcon;

/**
 * Main app service for loading and storing data.
 */
public class UpdateService extends Service {
    MyBinder binder = new MyBinder();
    private static final String CURRENT_RATE_URL = "https://privat24.privatbank.ua/";
    private static final String DATE_RATE_URL = " https://api.privatbank.ua/";

    private PBApi pbApiXML;
    private PBApi pbApiJSON;
    private DBHelper dbHelper;
    private SQLiteDatabase database;
    public List<String> daysList;
    NotificationManager notificationManager;
    private float usdCurrentRate, eurCurrentRate;
    private String currentRatesDate;
    int hour, minute;
    private SharedPreferences sharedPreferences;

    public UpdateService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(NOTIFICATION_ICON_ID, getNotificationIcon(this));
        Intent hideIntent = new Intent(this, HideNotificationService.class);
        startService(hideIntent);

        //check if user is online
        if (!isOnline()) Toast.makeText(this, "No internet connection!", Toast.LENGTH_LONG).show();

        sharedPreferences = getSharedPreferences(SHARED_PREF_SELECTED_DATE, Context.MODE_PRIVATE);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();
        daysList = new ArrayList<String>();

        createCurrentDateRestClientXML();
        createDateRestClientJSON();

        //load and save data on service starting
        update();
    }

    //check if user is online
    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void update() {
        //load current rates (XML)
//        loadCurrentDateRatesRu();
        loadCurrentDateRatesUa();

        //load month rates (JSON)
        getMonthRates();
    }

    //update in selected time
    public void updateInTime() {
        hour = sharedPreferences.getInt(SELECTED_TIME_HOUR, 9);
        minute = sharedPreferences.getInt(SELECTED_TIME_MINUTE, 00);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        Date alarmTime = calendar.getTime();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                update();
            }
        }, alarmTime);
    }

    //rest client with XML converter
    private void createCurrentDateRestClientXML() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CURRENT_RATE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
        pbApiXML = retrofit.create(PBApi.class);
    }

    //load current rates
    public void loadCurrentDateRatesRu() {

        //get current date rates for "ru"
        pbApiXML.getCurrentRates("ru").enqueue(new Callback<CurrentRates>() {
            @Override
            public void onResponse(final Call<CurrentRates> call, Response<CurrentRates> response) {
                CurrentRates currentRates = response.body();
                if (response.isSuccessful()) {
                    Cursor cursor = database.query(TABLE_NAME_CURRENT_RATES,
                            null, CURRENT_RATES_BASE_CURRENCY_COLUMN + "=?",
                            new String[]{"RU"},
                            null,
                            null,
                            null);
                    if (cursor != null && cursor.moveToFirst()) {
                        database.delete(TABLE_NAME_CURRENT_RATES, CURRENT_RATES_BASE_CURRENCY_COLUMN + "=?", new String[]{"RU"});
                        getContentResolver().notifyChange(CURRENT_DATE_RATES_DB_URI, null);
                    }
                    if (cursor != null) {
                        cursor.close();
                    }
                    for (int i = 0; i < currentRates.getExchangeRate().size(); i++) {
                        String currency = currentRates.getExchangeRate().get(i).getCcy();
                        String baseCurrency = currentRates.getExchangeRate().get(i).getBase_ccy();
                        String buyRate = currentRates.getExchangeRate().get(i).getBuy();
                        String unitRate = currentRates.getExchangeRate().get(i).getUnit();
                        String date = currentRates.getExchangeRate().get(i).getDate();
                        addCurrentRatesToDB(currency, baseCurrency, buyRate, unitRate, date);
                    }
                } else {
                    Timer retryTimer = new Timer();
                    retryTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            loadCurrentDateRatesRu();
                            Log.w("RETRY", "10 MIN PASSED-RETRYING");
                        }
                    }, 600000);
                }
            }

            @Override
            public void onFailure(Call<CurrentRates> call, Throwable t) {
                Timer retryTimer = new Timer();
                retryTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        loadCurrentDateRatesRu();
                        Log.w("RETRY", "10 MIN PASSED-RETRYING");
                    }
                }, 600000);
//                call.clone().enqueue(this);
            }
        });
    }

    public void loadCurrentDateRatesUa() {
        //get current date rates for "ua"
        pbApiXML.getCurrentRates("ua").enqueue(new Callback<CurrentRates>() {
            @Override
            public void onResponse(Call<CurrentRates> call, Response<CurrentRates> response) {
                CurrentRates currentRates = response.body();
                if (response.isSuccessful()) {
                    Cursor cursor = database.query(TABLE_NAME_CURRENT_RATES,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null);
                    if (cursor != null && cursor.moveToFirst()) {
                        database.delete(TABLE_NAME_CURRENT_RATES, null, null);
                        getContentResolver().notifyChange(CURRENT_DATE_RATES_DB_URI, null);
                    }
                    if (cursor != null) {
                        cursor.close();
                    }
                    for (int i = 0; i < currentRates.getExchangeRate().size(); i++) {
                        String currency = currentRates.getExchangeRate().get(i).getCcy();
                        String baseCurrency = currentRates.getExchangeRate().get(i).getBase_ccy();
                        String buyRate = currentRates.getExchangeRate().get(i).getBuy();
                        String unitRate = currentRates.getExchangeRate().get(i).getUnit();
                        String date = currentRates.getExchangeRate().get(i).getDate();
                        addCurrentRatesToDB(currency, baseCurrency, buyRate, unitRate, date);
                        currentRatesDate = date;
                        if (currency.equals("USD"))
                            usdCurrentRate = Float.parseFloat(buyRate) / 10000;
                        else if (currency.equals("EUR"))
                            eurCurrentRate = Float.parseFloat(buyRate) / 10000;
                    }
                    sendNotif();
                } else {
                    Timer retryTimer = new Timer();
                    retryTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            loadCurrentDateRatesUa();
                            Log.w("RETRY", "10 MIN PASSED-RETRYING");
                        }
                    }, 600000);
                }
            }

            @Override
            public void onFailure(Call<CurrentRates> call, Throwable t) {
                Timer retryTimer = new Timer();
                retryTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        loadCurrentDateRatesUa();
                        Log.w("RETRY", "10 MIN PASSED-RETRYING");
                    }
                }, 600000);
//                call.clone().enqueue(this);

            }
        });

    }

    //current rates notification sending on update
    private void sendNotif() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setContentIntent(contentIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("100 USD" + " - " + usdCurrentRate + /*" - " + currentRatesDate +*/
                        " \n100 EUR" + " - " + eurCurrentRate /*+ " - " + currentRatesDate*/))
                .setSmallIcon(R.drawable.ic_attach_money_white_48dp)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentTitle("NBU exchange rates today:")
                .setContentText("100 USD" + " - " + usdCurrentRate + /*" - " + currentRatesDate +*/
                        "\n100 EUR" + " - " + eurCurrentRate /*+ " - " + currentRatesDate*/)
                .setDefaults(Notification.DEFAULT_ALL);

        Notification notification = builder.build();
        notificationManager.notify(NOTIFICATION_ID_CURRENT_RATES++, notification);
    }

    //rest client with JSON converter
    private void createDateRestClientJSON() {
    /*    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();*/
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DATE_RATE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        pbApiJSON = retrofit.create(PBApi.class);
    }

    //get last 30 days rates
    private void getMonthRates() {
        //initialize last 30 days
        daysList.clear();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        for (int i = 0; i < 31; i++) {
            Calendar today = Calendar.getInstance();
            today.add(Calendar.DATE, -i);
            String day = dateFormat.format(today.getTime());
            daysList.add(day);
        }
        if (isOnline()) {
            Cursor cursor = database.query(TABLE_NAME_MONTH_RATES, null, null, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                database.delete(TABLE_NAME_MONTH_RATES, null, null);
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        for (String day : daysList) {
            loadMonthRates(day);
        }
    }

    //load last 30 days rates
    private void loadMonthRates(final String date) {
        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final DateRates dateRates = pbApiJSON.getDateRates(date).execute().body();
                    if (pbApiJSON.getDateRates(date).execute().isSuccessful()) {

                        for (int i = 0; i < dateRates.getExchangeRate().size(); i++) {
                            if (dateRates.getExchangeRate().get(i).getCurrency().equals("USD")
                                    || dateRates.getExchangeRate().get(i).getCurrency().equals("EUR")) {
                                String currency = dateRates.getExchangeRate().get(i).getCurrency();
                                String baseCurrency = dateRates.getExchangeRate().get(i).getBaseCurrency();
                                float buyNB = dateRates.getExchangeRate().get(i).getPurchaseRateNB();
                                float saleNB = dateRates.getExchangeRate().get(i).getSaleRateNB();
                                float salePB = dateRates.getExchangeRate().get(i).getSaleRate();
                                float buyPB = dateRates.getExchangeRate().get(i).getPurchaseRate();
                                addMonthRatesToDB(currency, baseCurrency, buyNB, saleNB, buyPB, salePB, date);
                            }
                        }
                    } else {
                        Timer retryTimer = new Timer();
                        retryTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                getMonthRates();
                                Log.w("RETRY", "10 MIN PASSED-RETRYING");
                            }
                        }, 600000);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }


    //load selected date rates
    public void loadSelectedDateRates() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String date = sharedPreferences.getString(SELECTED_DATE, "");
                    final DateRates dateRates = pbApiJSON.getDateRates(date).execute().body();
                    if (pbApiJSON.getDateRates(date).execute().isSuccessful()) {
                        if (dateRates.getExchangeRate().size() != 0) {
                            for (int i = 0; i < dateRates.getExchangeRate().size(); i++) {
                                String currency = dateRates.getExchangeRate().get(i).getCurrency();
                                String baseCurrency = dateRates.getExchangeRate().get(i).getBaseCurrency();
                                float buyNB = dateRates.getExchangeRate().get(i).getPurchaseRateNB();
                                float saleNB = dateRates.getExchangeRate().get(i).getSaleRateNB();
                                float salePB = dateRates.getExchangeRate().get(i).getSaleRate();
                                float buyPB = dateRates.getExchangeRate().get(i).getPurchaseRate();
                                addSelectedDateRatesToDB(currency, baseCurrency, buyNB, saleNB, salePB, buyPB, date);
                            }
                        } else {
                            Handler handler = new Handler(Looper.getMainLooper());

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "No information for selected date", Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                    } else {
                        Toast.makeText(UpdateService.this, "No data", Toast.LENGTH_LONG).show();
                        Timer retryTimer = new Timer();
                        retryTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                loadSelectedDateRates();
                                Log.w("RETRY", "10 MIN PASSED-RETRYING");
                            }
                        }, 600000);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    //storing current date rates
    private void addCurrentRatesToDB(String currency, String baseCurrency, String buyRate, String unitRate, String date) {
        ContentValues values = new ContentValues();
        values.put(CURRENT_RATES_CURRENCY_COLUMN, currency);
        values.put(CURRENT_RATES_BASE_CURRENCY_COLUMN, baseCurrency);
        values.put(CURRENT_RATES_BUY_COLUMN, buyRate);
        values.put(CURRENT_RATES_UNIT_COLUMN, unitRate);
        values.put(CURRENT_RATES_DATE_COLUMN, date);

        database.insert(TABLE_NAME_CURRENT_RATES, null, values);
        getContentResolver().notifyChange(CURRENT_DATE_RATES_DB_URI, null);
    }

    //storing last 30 days rates
    private void addMonthRatesToDB(String currency, String baseCurrency, float buyNB, float saleNB, float salePB, float buyPB, String date) {
        ContentValues values = new ContentValues();
        values.put(MONTH_RATES_CURRENCY_COLUMN, currency);
        values.put(MONTH_RATES_BASE_CURRENCY_COLUMN, baseCurrency);
        values.put(MONTH_RATES_BUY_NB_COLUMN, buyNB);
        values.put(MONTH_RATES_SALE_NB_COLUMN, saleNB);
        values.put(MONTH_RATES_BUY_PB_COLUMN, buyPB);
        values.put(MONTH_RATES_SALE_PB_COLUMN, salePB);
        values.put(MONTH_RATES_DATE_COLUMN, date);

        database.insert(TABLE_NAME_MONTH_RATES, null, values);
        getContentResolver().notifyChange(MONTH_RATES_DB_URI, null);
    }

    //storing selected date rates
    private void addSelectedDateRatesToDB(String currency, String baseCurrency, float buyNB, float saleNB, float salePB, float buyPB, String date) {
        ContentValues values = new ContentValues();
        values.put(SELECTED_RATES_CURRENCY_COLUMN, currency);
        values.put(SELECTED_RATES_BASE_CURRENCY_COLUMN, baseCurrency);
        values.put(SELECTED_RATES_BUY_NB_COLUMN, buyNB);
        values.put(SELECTED_RATES_SALE_NB_COLUMN, saleNB);
        values.put(SELECTED_RATES_BUY_PB_COLUMN, buyPB);
        values.put(SELECTED_RATES_SALE_PB_COLUMN, salePB);
        values.put(SELECTED_RATES_DATE_COLUMN, date);

        database.insert(TABLE_NAME_SELECTED_RATES, null, values);
        this.getContentResolver().notifyChange(SelectedDateRatesCursorLoader.SELECTED_DATE_RATES_DB_URI, null);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return binder;
    }

    public class MyBinder extends Binder {
        public UpdateService getService() {
            return UpdateService.this;
        }
    }

    public static class RetryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }
}
