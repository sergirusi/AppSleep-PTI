package com.example.sergirusi.appsleep.Presenter;

import android.app.Dialog;
import android.app.Fragment;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sergirusi.appsleep.Connection.HttpGraphicGetData;
import com.example.sergirusi.appsleep.Connection.HttpLogInGetConnection;
import com.example.sergirusi.appsleep.Model.UserData;
import com.example.sergirusi.appsleep.View.Activitiy.MainActivity;
import com.example.sergirusi.appsleep.Adapter.DrawerItemCustomAdapter;
import com.example.sergirusi.appsleep.Connection.HttpPostConnection;
import com.example.sergirusi.appsleep.Model.ObjectDrawerItem;
import com.example.sergirusi.appsleep.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import io.realm.Realm;

/**
 * Created by SergiRusi on 05/05/2016.
 */
public class MainPresenter implements Presenter {

    private final String[] navigationDrawerItemTitles;

    private MainActivity context;

    private JSONArray grafData;

    private float[] dbGrafData;

    public MainPresenter(MainActivity context) {
        this.context = context;
        navigationDrawerItemTitles = context.getResources().getStringArray(R.array.navigation_drawer_items_array);
    }

    @Override
    public void resume() {
        context.checkUserLogged();
    }

    @Override
    public void pause() {

    }

    public void createChart(LineChart lineChart, String username) throws ExecutionException, InterruptedException {
        lineChart.invalidate();
        lineChart.setBackgroundColor(context.getResources().getColor(R.color.Transparent));
        lineChart.setGridBackgroundColor(context.getResources().getColor(R.color.White));
        lineChart.setBorderColor(context.getResources().getColor(R.color.Orange));
        lineChart.setDrawingCacheBackgroundColor(context.getResources().getColor(R.color.Orange));

        ArrayList<Entry> valsComp = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();
        if(context.isLogged) {
            AsyncTask<String, String, JSONObject> asyncTask = new HttpGraphicGetData().execute(username);
            JSONObject obj = asyncTask.get();
            if (obj == null) {
                Entry e = new Entry(0, 0);
                valsComp.add(e);
                xVals.add("1min");
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.error_dialog);
                dialog.setTitle("ERROR");
                TextView tv = (TextView) dialog.findViewById(R.id.error_dialog_text);
                tv.setText("S'ha produït un error de connexió. Si-us-plau comprovi la connexió i torni-ho a intentar més tard.");
                dialog.show();
            } else {
                try {
                    grafData = obj.getJSONArray("graf");;
                    dbGrafData = new float[grafData.length()];
                    for (int i = 0; i < grafData.length(); i++) {
                        Entry graphData = new Entry(Float.valueOf(String.valueOf(grafData.get(i))), i);
                        dbGrafData[i] = Float.valueOf(String.valueOf(grafData.get(i)));
                        valsComp.add(graphData);
                        xVals.add(i + 1 + "min");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Entry e = null;
//            if(oldGrafData != null) {
//                for(int i = 0; i < oldGrafData.length; i++) {
//                    e = new Entry(Float.valueOf(String.valueOf(oldGrafData[i])), i);
//                    valsComp.add(e);
//                    xVals.add(i + 1 + "min");
//                }
//            } else {
                e = new Entry(0, 0);
                valsComp.add(e);
                xVals.add("1min");
//            }
        }

        LineDataSet setComp1 = new LineDataSet(valsComp, "Dades del son");
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp1.setColor(R.color.Orange);

        // use the interface ILineDataSet
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(setComp1);

        LineData data = new LineData(xVals, dataSets);
        lineChart.setData(data);
        lineChart.invalidate();
        lineChart.notifyDataSetChanged();
    }

    public void updateGrafDataInDB(Realm appSleepDB, UserData userData) {
        appSleepDB.beginTransaction();
        appSleepDB.copyToRealmOrUpdate(userData);
        appSleepDB.commitTransaction();
    }

    public DrawerItemCustomAdapter getDrawerItemCustomAdapter() {
        ObjectDrawerItem[] drawerItem = new ObjectDrawerItem[4];

        String[] items = context.getResources().getStringArray(R.array.navigation_drawer_items_array);

        drawerItem[0] = new ObjectDrawerItem(R.mipmap.ic_home, items[0], null);
        drawerItem[1] = new ObjectDrawerItem(R.mipmap.ic_day, items[1], null);
        drawerItem[2] = new ObjectDrawerItem(R.mipmap.ic_week, items[2], null);
        drawerItem[3] = new ObjectDrawerItem(R.mipmap.ic_month, items[3], null);


        return new DrawerItemCustomAdapter(context, R.layout.listview_item_row, drawerItem);
    }

    public void cameraTurnOFF(String s, String cameraOff) {
        new HttpPostConnection().execute(s, cameraOff);
    }

    public void cameraTurnON(String s, String cameraOn) {
        new HttpPostConnection().execute(s, cameraOn);
    }

    public boolean isCameraON(String username) {
        AsyncTask<String, String, JSONObject> asyncTask = new HttpLogInGetConnection().execute(username);
        String state = "false";
        try {
            JSONObject jsonObject = asyncTask.get();
            state = jsonObject.getString("cam");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return state.equals("true");
    }

    public void setTimeSlept(String username) {
        String timeString = getTimeSlept(username);
        int time = Integer.valueOf(timeString);
    }

    public String getTimeSlept(String username) {
        AsyncTask<String, String, JSONObject> asyncTask = new HttpLogInGetConnection().execute(username);
        String time = "0";
        try {
            JSONObject jsonObject = asyncTask.get();
            time = jsonObject.getString("temps");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return time;
    }


    public void selectItem(ListView drawerList, DrawerLayout drawerLayout, int position) {

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
            android.app.FragmentManager fragmentManager = context.getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            drawerList.setItemChecked(position, true);
            drawerList.setSelection(position);
            context.setTitle(navigationDrawerItemTitles[position]);
            drawerLayout.closeDrawer(drawerList);

        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }
}
