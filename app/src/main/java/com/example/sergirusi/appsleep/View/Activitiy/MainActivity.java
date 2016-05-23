package com.example.sergirusi.appsleep.View.Activitiy;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.sergirusi.appsleep.Adapter.DrawerItemCustomAdapter;
import com.example.sergirusi.appsleep.Model.GrafData;
import com.example.sergirusi.appsleep.Model.UserData;
import com.example.sergirusi.appsleep.Presenter.MainPresenter;
import com.example.sergirusi.appsleep.R;
import com.github.mikephil.charting.charts.LineChart;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static int REQUEST_CODE = 1234;

    public final static String CAMERA_ON = "ON";

    public final static String CAMERA_OFF = "OFF";

    boolean isCameraActivated;

    private MainPresenter mainPresenter;

    public boolean isLogged;

    private TextView profileName;

    private TextView profileStatus;

    public TextView hours;

    public TextView minutes;

    public TextView seconds;

    private ImageButton imageProfile;

    private Switch enableCamera;

    public DrawerLayout drawerLayout;

    public ListView drawerList;

    private ActionBarDrawerToggle drawerToggle;

    private CharSequence drawerTitle;

    private CharSequence title;

    public Toolbar toolbar;

    private ImageButton refreshButton;

    private View header;

    public LineChart lineChart;

    private Realm appSleepDB;

//    private RealmList<GrafData> grafDataRealmList;

    private RealmResults<UserData> result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainPresenter = new MainPresenter(this);
        drawerLayout = new DrawerLayout(this);
        drawerList = new ListView(this);

        appSleepDB = Realm.getInstance(this);
        result = appSleepDB.where(UserData.class).findAll();

        findViews();
        checkUserLogged();
        init();
    }

    public void checkUserLogged() {
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
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        refreshButton = (ImageButton) findViewById(R.id.refresh_button);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        hours = (TextView) findViewById(R.id.hours_num);
        minutes = (TextView) findViewById(R.id.minutes_num);
        seconds = (TextView) findViewById(R.id.seconds_num);
        lineChart = (LineChart) findViewById(R.id.chart1);
    }

    private void init() {
        imageProfile.setOnClickListener(this);
        enableCamera.setOnClickListener(this);
        profileStatus.setOnClickListener(this);
        refreshButton.setOnClickListener(this);
        drawerList.addHeaderView(header);
        if (isLogged) {
            profileStatus.setVisibility(View.VISIBLE);
            isCameraActivated = mainPresenter.isCameraON(profileName.getText().toString());
            mainPresenter.setTimeSlept(profileName.getText().toString());
        } else {
            profileStatus.setVisibility(View.GONE);
        }
        try {
            mainPresenter.createChart(lineChart, profileName.getText().toString());
//            mainPresenter.updateGrafDataInDB(appSleepDB, result.first());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        DrawerItemCustomAdapter adapter = mainPresenter.getDrawerItemCustomAdapter();

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
                if(isLogged) {
                    enableCamera.setChecked(isCameraActivated);
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserLogged();
        if (isLogged) {
            profileStatus.setVisibility(View.VISIBLE);
            isCameraActivated = mainPresenter.isCameraON(profileName.getText().toString());
            mainPresenter.setTimeSlept(profileName.getText().toString());
        } else {
            profileStatus.setVisibility(View.GONE);
        }
        try {
            mainPresenter.createChart(lineChart, profileName.getText().toString());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        result = appSleepDB.where(UserData.class).findAll();
        if(result.size() == 0 && isLogged) {
            appSleepDB.beginTransaction();
            UserData userData = new UserData();
            userData.setName(profileName.getText().toString());
            userData.setCameraState(isCameraActivated);
            userData.setLogState(isLogged);
            appSleepDB.copyToRealmOrUpdate(userData);
            appSleepDB.commitTransaction();
            result = appSleepDB.where(UserData.class).findAll();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profileImg:
                if(!isLogged) {
                    Intent loggin = new Intent(this, RegistrationActivity.class);
                    startActivityForResult(loggin, REQUEST_CODE);
                }
                break;

            case R.id.enableCamera:
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
                    mainPresenter.cameraTurnON(String.valueOf(data), CAMERA_ON);
                } else {
                    appSleepDB.beginTransaction();
                    changeCameraState.setCameraState(false);
                    appSleepDB.copyToRealmOrUpdate(changeCameraState);
                    appSleepDB.commitTransaction();
                    mainPresenter.cameraTurnOFF(String.valueOf(data), CAMERA_OFF);
                }
                break;

            case R.id.profileStatus:
                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.logout_dialog);
                dialog.setTitle("ATENCIÓ");

                Button buttonCancel = (Button) dialog.findViewById(R.id.dialogButtonCancel);
                Button buttonOK = (Button) dialog.findViewById(R.id.dialogButtonOK);

                buttonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                buttonOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JSONObject data = new JSONObject();
                        try {
                            data.put("user", result.first().getName());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mainPresenter.cameraTurnOFF(String.valueOf(data), CAMERA_OFF);
                        appSleepDB.beginTransaction();
                        appSleepDB.where(UserData.class).findAll().clear();
                        appSleepDB.commitTransaction();
                        lineChart.clear();
                        finish();
                        startActivity(getIntent());
                    }
                });

                dialog.show();
                break;
            case R.id.refresh_button:
                try {
                    mainPresenter.createChart(lineChart, profileName.getText().toString());
                    if(isLogged) {
                        mainPresenter.setTimeSlept(profileName.getText().toString());
                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mainPresenter.selectItem(drawerList, drawerLayout, position);
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
            mainPresenter.cameraTurnOFF(String.valueOf(data), CAMERA_OFF);
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
            result = appSleepDB.where(UserData.class).findAll();

            profileName.setText(username);
            enableCamera.setVisibility(View.VISIBLE);
        }
    }
}
