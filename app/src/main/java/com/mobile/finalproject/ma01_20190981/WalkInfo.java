package com.mobile.finalproject.ma01_20190981;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.PolylineOptions;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WalkInfo implements Serializable {
    private long id;
    private float moveKm;
    private int level;
    private float kcal;
    private int time;
    private long date;
    private String photoPath;

    public WalkInfo () {
        this.moveKm = 0;
        this.time = 0;
        this.kcal = 0;
    }

    public WalkInfo(long id, float moveKm, int level, float kcal, int time, long date) {
        this.id = id;
        this.moveKm = moveKm;
        this.level = level;
        this.kcal = kcal;
        this.time = time;
        this.date = date;
    }

    public WalkInfo(long id, float moveKm, int level, float kcal, int time, long date, String photoPath) {
        this.id = id;
        this.moveKm = moveKm;
        this.level = level;
        this.kcal = kcal;
        this.time = time;
        this.date = date;
        this.photoPath = photoPath;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public float getMoveKm() {
        return moveKm;
    }

    public void setMoveKm(float moveKm) {
        this.moveKm = moveKm;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public float getKcal() {
        return kcal;
    }

    public void setKcal(float kcal) {
        this.kcal = kcal;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public String toString() {
        String str = "";

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String walkDateStr = formatter.format(date);

        str += "- 일자: " + walkDateStr + "\n";
        str += "- 걸은 거리: " + moveKm + "km\n";
        str += "- 소모 칼로리: " + kcal + "kcal\n";
        str += "- 운동 시간: " + Integer.toString(time / 3600) + "시 " + Integer.toString(time / 60 % 60) + "분 " + Integer.toString(time % 60) + "초";

        return str;
    }
}
