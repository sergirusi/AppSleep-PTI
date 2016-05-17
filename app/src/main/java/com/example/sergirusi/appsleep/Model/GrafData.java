package com.example.sergirusi.appsleep.Model;

import io.realm.RealmObject;

/**
 * Created by SergiRusi on 16/05/2016.
 */
public class GrafData extends RealmObject {

    private float movData;

    public float getMovData() {
        return movData;
    }

    public void setMovData(float movData) {
        this.movData = movData;
    }
}
