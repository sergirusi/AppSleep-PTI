package com.example.sergirusi.appsleep.Activities;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.sergirusi.appsleep.Adapter.DrawerItemCustomAdapter;
import com.example.sergirusi.appsleep.Connection.HttpPostConnection;
import com.example.sergirusi.appsleep.Model.ObjectDrawerItem;
import com.example.sergirusi.appsleep.Model.UserData;
import com.example.sergirusi.appsleep.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static int REQUEST_CODE = 1234;

    public final static String CAMERA_ON = "ON";

    public final static String CAMERA_OFF = "OFF";

    private boolean isLogged;

    private TextView profileName;

    private TextView profileStatus;

    private ImageButton imageProfile;

    private Switch enableCamera;

    private String[] navigationDrawerItemTitles;

    private DrawerLayout drawerLayout;

    private ListView drawerList;

    private ActionBarDrawerToggle drawerToggle;

    private CharSequence drawerTitle;

    private CharSequence title;

    private Toolbar toolbar;

    private View header;

    private LineChart chart;

    private PieChart pieChart;

    private Realm appSleepDB;

    private RealmResults<UserData> result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = new DrawerLayout(this);
        drawerList = new ListView(this);

        appSleepDB = Realm.getInstance(this);
        result = appSleepDB.where(UserData.class).findAll();

        findViews();
        checkUserLogged();
        init();
        createChart();
    }

    private void checkUserLogged() {
        if (result.size() != 0) {
            isLogged = true;
            String username = result.first().getName();
            profileName.setText(username);
            enableCamera.setVisibility(View.VISIBLE);
        } else {
            isLogged = false;
            enableCamera.setVisibility(View.INVISIBLE);
        }
    }

    private void findViews() {
        header = (View) getLayoutInflater().inflate(R.layout.nav_header, null);
        imageProfile = (ImageButton) header.findViewById(R.id.profileImg);
        profileName = (TextView) header.findViewById(R.id.profile_username);
        profileStatus = (TextView) header.findViewById(R.id.profileStatus);
        enableCamera = (Switch) header.findViewById(R.id.enableCamera);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        chart = (LineChart) findViewById(R.id.chart1);
        pieChart = (PieChart) findViewById(R.id.chart2);
    }

    private void createChart() {
        chart.invalidate();
        chart.setBackgroundColor(getResources().getColor(R.color.Transparent));
        chart.setGridBackgroundColor(getResources().getColor(R.color.White));
        chart.setBorderColor(getResources().getColor(R.color.Orange));
        chart.setDrawingCacheBackgroundColor(getResources().getColor(R.color.Orange));


        ArrayList<Entry> valsComp = new ArrayList<Entry>();

        ArrayList<String> xVals = new ArrayList<String>();

        int maxX = new Random().nextInt(15 + 1);
        if (maxX == 0) maxX = 7;
        for (int i = 0; i <= maxX; i++) {
            int valueI = new Random().nextInt(50 + 1);
            float valueF = new Random().nextFloat() + valueI;
            Entry c1e = new Entry(valueF, i);
            valsComp.add(c1e);
            xVals.add(i + 1 + ".Q");
        }

        LineDataSet setComp1 = new LineDataSet(valsComp, "Dades del son");
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp1.setColor(R.color.Orange);

        // use the interface ILineDataSet
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(setComp1);

        LineData data = new LineData(xVals, dataSets);
        chart.setData(data);
        chart.invalidate(); // refresh
        chart.notifyDataSetChanged();

//        ArrayList<String> pieVals = new ArrayList<>(5);
//        pieVals.add(xVals.get(0));
//        pieVals.add(xVals.get(1));
//        pieVals.add(xVals.get(2));
//        pieVals.add(xVals.get(3));
//        pieVals.add(xVals.get(4));
//
//        pieChart.invalidate();
//        pieChart.setDrawCenterText(true);
//        pieChart.setDrawHoleEnabled(false);
//        pieChart.setRotationAngle(90);
//        pieChart.setRotationEnabled(false);
//        pieChart.setTouchEnabled(false);
//        pieChart.setCenterText("MyChart");
//        pieChart.setCenterTextSize(24);
//        pieChart.setDrawHoleEnabled(true);
//        pieChart.setDrawSliceText(false);
//
//        pieChart.animateXY(1000, 1000);
//
//        pieChart.getLegend().setEnabled(false);
//
//        PieData data2 = new PieData(pieVals);
//        pieChart.setData(data2);
//        pieChart.invalidate();
//        pieChart.notifyDataSetChanged();
    }

    private void init() {
        imageProfile.setOnClickListener(this);
        enableCamera.setOnClickListener(this);
        drawerList.addHeaderView(header);

        if (isLogged) {
            profileStatus.setVisibility(View.VISIBLE);
        } else {
            profileStatus.setVisibility(View.GONE);
        }

        navigationDrawerItemTitles = getResources().getStringArray(R.array.navigation_drawer_items_array);

        DrawerItemCustomAdapter adapter = getDrawerItemCustomAdapter();

        drawerList.setAdapter(adapter);
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        title = drawerTitle = getTitle();

        setSupportActionBar(toolbar);

        setDrawerActions();

        drawerLayout.setDrawerListener(drawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void setDrawerActions() {
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(title);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(drawerTitle);
            }
        };
    }

    @NonNull
    private DrawerItemCustomAdapter getDrawerItemCustomAdapter() {
        ObjectDrawerItem[] drawerItem = new ObjectDrawerItem[4];

        String[] items = getResources().getStringArray(R.array.navigation_drawer_items_array);

        drawerItem[0] = new ObjectDrawerItem(R.mipmap.ic_home, items[0], null);
        drawerItem[1] = new ObjectDrawerItem(R.mipmap.ic_day, items[1], null);
        drawerItem[2] = new ObjectDrawerItem(R.mipmap.ic_week, items[2], null);
        drawerItem[3] = new ObjectDrawerItem(R.mipmap.ic_month, items[3], null);


        return new DrawerItemCustomAdapter(this, R.layout.listview_item_row, drawerItem);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.profileImg && !isLogged) {
            Intent loggin = new Intent(this, RegistrationActivity.class);
            startActivityForResult(loggin, REQUEST_CODE);
        } else if (v.getId() == R.id.enableCamera) {
            UserData changeCameraState = result.first();
            String dbUsername = changeCameraState.getName();
            JSONObject data = new JSONObject();
            try {
                data.put("user", dbUsername);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (!changeCameraState.isCameraState()) {
                appSleepDB.beginTransaction();
                changeCameraState.setCameraState(true);
                appSleepDB.copyToRealmOrUpdate(changeCameraState);
                appSleepDB.commitTransaction();
                cameraTurnON(String.valueOf(data), CAMERA_ON);
            } else {
                appSleepDB.beginTransaction();
                changeCameraState.setCameraState(false);
                appSleepDB.copyToRealmOrUpdate(changeCameraState);
                appSleepDB.commitTransaction();
                cameraTurnOFF(String.valueOf(data), CAMERA_OFF);
            }

        }
    }

    private void cameraTurnOFF(String s, String cameraOff) {
        new HttpPostConnection().execute(s, cameraOff);
    }

    private void cameraTurnON(String s, String cameraOn) {
        new HttpPostConnection().execute(s, cameraOn);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {

        Fragment fragment = null;

        switch (position) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;

            default:
                break;
        }

        if (fragment != null) {
            android.app.FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

            drawerList.setItemChecked(position, true);
            drawerList.setSelection(position);
            setTitle(navigationDrawerItemTitles[position]);
            drawerLayout.closeDrawer(drawerList);

        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        this.title = title;
        getSupportActionBar().setTitle(title);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (result.size() != 0) {
            JSONObject data = new JSONObject();
            try {
                data.put("user", result.first().getName());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            cameraTurnOFF(String.valueOf(data), CAMERA_OFF);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String username = data.getStringExtra("name");

            appSleepDB.beginTransaction();

            UserData userData = appSleepDB.createObject(UserData.class);

            userData.setLogState(true);
            userData.setCameraState(false);
            userData.setName(username);

            appSleepDB.commitTransaction();


            profileName.setText(username);
            enableCamera.setVisibility(View.VISIBLE);
        }
    }
}
