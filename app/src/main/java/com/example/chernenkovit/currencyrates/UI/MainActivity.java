package com.example.chernenkovit.currencyrates.UI;

import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TimePicker;

import com.example.chernenkovit.currencyrates.R;
import com.example.chernenkovit.currencyrates.UpdateService;

import java.util.ArrayList;
import java.util.List;

import static com.example.chernenkovit.currencyrates.utils.Const.SELECTED_DATE;
import static com.example.chernenkovit.currencyrates.utils.Const.SELECTED_TIME_HOUR;
import static com.example.chernenkovit.currencyrates.utils.Const.SELECTED_TIME_MINUTE;
import static com.example.chernenkovit.currencyrates.utils.Const.SHARED_PREF_SELECTED_DATE;

/** Main activity class with service connection, tabs attachment and menu implementation. */
public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private boolean bound = false;
    private ServiceConnection sConn;
    private Intent bindIntent;
    public UpdateService updateService;
    private SharedPreferences sharedPreferences;
    private TimePickerDialog timePickerDialog;
    private int hour;
    private int minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        bindIntent = new Intent(this, UpdateService.class);
        startService(bindIntent);
        sConn = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder binder) {
                updateService = ((UpdateService.MyBinder) binder).getService();
                bound = true;
            }

            public void onServiceDisconnected(ComponentName name) {
                bound = false;
            }
        };
        sharedPreferences = getSharedPreferences(SHARED_PREF_SELECTED_DATE, Context.MODE_PRIVATE);
        hour = sharedPreferences.getInt(SELECTED_TIME_HOUR, 9);
        minute = sharedPreferences.getInt(SELECTED_TIME_MINUTE, 00);
    }

    public UpdateService getService() {
        return updateService;
    }

    //set selected date into SharedPreferences
    public void setSelectedDate(String selectedDate) {
        sharedPreferences.edit()
                .putString(SELECTED_DATE, selectedDate)
                .apply();
    }

    //get selected date from SharedPreferences
    public String getSelectedDate() {
        return sharedPreferences.getString(SELECTED_DATE, "");
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CurrentRatesFragment(), getString(R.string.tab_title_current_rates));
        adapter.addFragment(new SelectedDateRatesFragment(), getString(R.string.tab_title_selected_day_rates));
        adapter.addFragment(new ChartFragment(), getString(R.string.tab_title_charts));
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(bindIntent, sConn, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!bound) return;
        unbindService(sConn);
        bound = false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //update time selection implementation
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_update_time:
                timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        hour = i;
                        minute = i1;
                        sharedPreferences.edit()
                                .putInt(SELECTED_TIME_HOUR, hour)
                                .putInt(SELECTED_TIME_MINUTE, minute)
                                .apply();
                        updateService.updateInTime();
                    }
                }, hour, minute, true);
                timePickerDialog.show();
                return true;
            case R.id.menu_update_now:
                updateService.update();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return new CurrentRatesFragment();
            else if (position == 1)
                return new SelectedDateRatesFragment();
            else
                return new ChartFragment();
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}

