package com.example.sergirusi.appsleep;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton imageProfile;

    private RecyclerView recyclerView;

    private String[] navigationDrawerItemTitles;

    private DrawerLayout drawerLayout;

    private ListView drawerList;

    private ActionBarDrawerToggle drawerToggle;

    private CharSequence drawerTitle;

    private CharSequence title;

    private Toolbar toolbar;

    private View header;

    private LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = new DrawerLayout(this);
        drawerList = new ListView(this);
        findViews();
        init();
        createChart();
    }

    private void createChart() {
        chart.invalidate();
        chart.setBackgroundColor(getResources().getColor(R.color.MaterialDark));
        chart.setDescription("Sleeping Time Chart");
        chart.setDescriptionColor(getResources().getColor(R.color.Orange));
        chart.setGridBackgroundColor(getResources().getColor(R.color.White));

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

        LineDataSet setComp1 = new LineDataSet(valsComp, "Company 1");
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);

        // use the interface ILineDataSet
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(setComp1);

        LineData data = new LineData(xVals, dataSets);
        chart.setData(data);
        chart.invalidate(); // refresh
        chart.notifyDataSetChanged();
    }

    private void findViews() {
        header = (View) getLayoutInflater().inflate(R.layout.nav_header, null);
        imageProfile = (ImageButton) header.findViewById(R.id.profileImg);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        chart = (LineChart) findViewById(R.id.chart1);
    }

    private void init() {
        imageProfile.setOnClickListener(this);
        drawerList.addHeaderView(header);

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
        ObjectDrawerItem[] drawerItem = new ObjectDrawerItem[1];

        drawerItem[0] = new ObjectDrawerItem(R.mipmap.ic_home, "Inicio", null);


        return new DrawerItemCustomAdapter(this, R.layout.listview_item_row, drawerItem);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.profileImg) {
            Intent loggin = new Intent(this, RegistrationActivity.class);
            startActivity(loggin);
        }
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
                fragment = new TestFragment();
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
}
