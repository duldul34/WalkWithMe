package com.mobile.finalproject.ma01_20190981;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class WalkInfoDBManager {

    WalkInfoDBHelper walkInfoDBHelper = null;
    Cursor cursor = null;

    public WalkInfoDBManager(Context context) {
        walkInfoDBHelper = new WalkInfoDBHelper(context);
    }

    // DB의 모든 컬럼을 반환
    public ArrayList<WalkInfo> getAllWalkInfo() {
        ArrayList walkInfoList = new ArrayList(); // DB의 테이블에서 레코드를 읽어와 Book DTO에 기록 후 bookList에 추가
        SQLiteDatabase db = walkInfoDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + walkInfoDBHelper.TABLE_NAME, null);
        while(cursor.moveToNext()) {
            long id = cursor.getInt(cursor.getColumnIndex(walkInfoDBHelper.COL_ID));
            float moveKm = cursor.getFloat(cursor.getColumnIndex(walkInfoDBHelper.COL_MOVE_KM));
            int level = cursor.getInt(cursor.getColumnIndex(walkInfoDBHelper.COL_LEVEL));
            float kcal = cursor.getFloat(cursor.getColumnIndex(walkInfoDBHelper.COL_KCAL));
            int time = cursor.getInt(cursor.getColumnIndex(walkInfoDBHelper.COL_TIME));
            long date = cursor.getLong(cursor.getColumnIndex(walkInfoDBHelper.COL_DATE));
            String photoPath = cursor.getString(cursor.getColumnIndex(walkInfoDBHelper.COL_PHOTO_PATH));

            WalkInfo walkInfo = new WalkInfo(id, moveKm, level, kcal, time, date, photoPath);

            walkInfoList.add ( walkInfo );
        }
        cursor.close();
        walkInfoDBHelper.close();
        return walkInfoList;
    }

    // DB에 walkInfo 추가
    public boolean addNewWalkInfo(WalkInfo newWalkInfo) {
        SQLiteDatabase db = walkInfoDBHelper.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(walkInfoDBHelper.COL_MOVE_KM, newWalkInfo.getMoveKm());
        value.put(walkInfoDBHelper.COL_LEVEL, newWalkInfo.getLevel());
        value.put(walkInfoDBHelper.COL_KCAL, newWalkInfo.getKcal());
        value.put(walkInfoDBHelper.COL_TIME, newWalkInfo.getTime());
        value.put(walkInfoDBHelper.COL_DATE, newWalkInfo.getDate());
        value.put(walkInfoDBHelper.COL_PHOTO_PATH, newWalkInfo.getPhotoPath());
        // 데이터 삽입이 정상적으로 이루어질 경우 1 이상, 이상이 있을 경우 0 반환
        long count = db.insert(walkInfoDBHelper.TABLE_NAME, null, value);
        if (count > 0) return true;
        return false;
    }

    // _id를 기준으로 walkInfo 수정
    public boolean modifyWalkInfo (WalkInfo walkInfo) {
        SQLiteDatabase sqLiteDatabase = walkInfoDBHelper.getWritableDatabase();
        ContentValues row = new ContentValues();
        row.put(walkInfoDBHelper.COL_MOVE_KM, walkInfo.getMoveKm());
        if (getTotalMoveKm() >= 5 && (getTotalMoveKm() / walkInfo.getLevel() * 5 >= 1)) {
            row.put(walkInfoDBHelper.COL_LEVEL, walkInfo.getLevel() + 1);
        }
        else {
            row.put(walkInfoDBHelper.COL_LEVEL, walkInfo.getLevel());
        }
        row.put(walkInfoDBHelper.COL_KCAL, walkInfo.getKcal());
        row.put(walkInfoDBHelper.COL_TIME, walkInfo.getTime());
        row.put(walkInfoDBHelper.COL_DATE, walkInfo.getDate());
        if (walkInfo.getPhotoPath() != null)
            row.put(WalkInfoDBHelper.COL_PHOTO_PATH, walkInfo.getPhotoPath());
        String whereClause = WalkInfoDBHelper.COL_ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(walkInfo.getId()) };
        int result = sqLiteDatabase.update(walkInfoDBHelper.TABLE_NAME, row, whereClause, whereArgs);
        walkInfoDBHelper.close();
        if (result > 0) return true;
        return false;
    }

    // _id 를 기준으로 DB에서 walkInfo 삭제
    public boolean removeWalkInfo(long id) {
        SQLiteDatabase sqLiteDatabase = walkInfoDBHelper.getWritableDatabase();
        String whereClause = walkInfoDBHelper.COL_ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(id) };
        int result = sqLiteDatabase.delete(walkInfoDBHelper.TABLE_NAME, whereClause, whereArgs);
        walkInfoDBHelper.close();
        if (result > 0) return true;
        return false;
    }

    // 가장 최근 walkInfo 반환
    public WalkInfo getRecentWalkInfo() {
        SQLiteDatabase sqLiteDatabase = walkInfoDBHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + walkInfoDBHelper.TABLE_NAME + " ORDER BY " + walkInfoDBHelper.COL_DATE + " DESC", null);
        WalkInfo walkInfo = null;
        if (cursor.moveToNext()) {
            long id = cursor.getInt(cursor.getColumnIndex(walkInfoDBHelper.COL_ID));
            float moveKm = cursor.getFloat(cursor.getColumnIndex(walkInfoDBHelper.COL_MOVE_KM));
            int level = cursor.getInt(cursor.getColumnIndex(walkInfoDBHelper.COL_LEVEL));
            float kcal = cursor.getFloat(cursor.getColumnIndex(walkInfoDBHelper.COL_KCAL));
            int time = cursor.getInt(cursor.getColumnIndex(walkInfoDBHelper.COL_TIME));
            long date = cursor.getLong(cursor.getColumnIndex(walkInfoDBHelper.COL_DATE));
            String photoPath = cursor.getString(cursor.getColumnIndex(walkInfoDBHelper.COL_PHOTO_PATH));

            walkInfo = new WalkInfo(id, moveKm, level, kcal, time, date, photoPath);
        }
        return walkInfo;
    }

    // 총 km 반환
    public float getTotalMoveKm() {
        SQLiteDatabase sqLiteDatabase = walkInfoDBHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT move_km FROM " + walkInfoDBHelper.TABLE_NAME, null);
        float result = 0;
        if (cursor.moveToNext()) {
            float moveKm = cursor.getFloat(cursor.getColumnIndex(walkInfoDBHelper.COL_MOVE_KM));
            result += moveKm;
        }
        return result;
    }

    // 총 kcal 반환
    public float getTotalKcal() {
        SQLiteDatabase sqLiteDatabase = walkInfoDBHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT kcal FROM " + walkInfoDBHelper.TABLE_NAME, null);
        float result = 0;
        if (cursor.moveToNext()) {
            float kcal = cursor.getFloat(cursor.getColumnIndex(walkInfoDBHelper.COL_KCAL));
            result += kcal;
        }
        return result;
    }

    // close 수행
    public void close() {
        if (walkInfoDBHelper != null) walkInfoDBHelper.close();
        if (cursor != null) cursor.close();
    };
}
