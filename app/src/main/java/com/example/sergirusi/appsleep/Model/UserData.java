package com.example.sergirusi.appsleep.Model;

import org.json.JSONArray;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by SergiRusi on 23/04/2016.
 */
public class UserData extends RealmObject {

    @PrimaryKey
    private String name;

    private boolean logState;

    private boolean cameraState;

    public UserData() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLogState() {
        return logState;
    }

    public void setLogState(boolean logState) {
        this.logState = logState;
    }

    public boolean isCameraState() {
        return cameraState;
    }

    public void setCameraState(boolean cameraState) {
        this.cameraState = cameraState;
    }

}
