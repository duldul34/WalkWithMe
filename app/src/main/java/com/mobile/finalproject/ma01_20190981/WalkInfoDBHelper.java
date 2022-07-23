package com.mobile.finalproject.ma01_20190981;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WalkInfoDBHelper extends SQLiteOpenHelper {
	
	private final static String DB_NAME = "walkInfo_db";
	public final static String TABLE_NAME = "walkInfo_table";
	public final static String COL_ID = "_id";
    public final static String COL_MOVE_KM = "move_km";
    public final static String COL_LEVEL = "level";
	public final static String COL_KCAL = "kcal";
	public final static String COL_TIME = "time";
	public final static String COL_DATE = "date";
	public final static String COL_PHOTO_PATH = "photo_path";

	public WalkInfoDBHelper(Context context) {
		super(context, DB_NAME, null, 1);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table " + TABLE_NAME + " ( " + COL_ID + " integer primary key autoincrement,"
				+ COL_MOVE_KM + " REAL, " + COL_LEVEL + " integer, " + COL_KCAL + " REAL, "
				+ COL_TIME + " integer, " + COL_DATE + " REAL, " + COL_PHOTO_PATH + " text);");
	
//		샘플 데이터
		db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, 0, 1, 0, 0, " + System.currentTimeMillis() + ", null);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + TABLE_NAME);
        onCreate(db);
	}

}
