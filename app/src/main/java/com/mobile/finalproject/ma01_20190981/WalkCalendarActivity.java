package com.mobile.finalproject.ma01_20190981;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class WalkCalendarActivity extends AppCompatActivity {

    WalkInfoDBHelper helper;
    WalkInfoDBManager manager;

    TextView tvKcalCal;
    TextView tvKmCal;
    CalendarView cvWalk;
    String dateStr = null;

    boolean isWalkedDate = false;
    WalkInfo walkInfo;
    ArrayList<WalkInfo> walkList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_calendar);

        cvWalk = findViewById(R.id.cvWalk);
        tvKcalCal = findViewById(R.id.tvKcalCal);
        tvKmCal = findViewById(R.id.tvKmCal);

        helper = new WalkInfoDBHelper(this);
        manager = new WalkInfoDBManager(this);

        tvKcalCal.setText(Float.toString(manager.getTotalKcal()));
        tvKmCal.setText(Float.toString(manager.getTotalMoveKm()));
        walkList = manager.getAllWalkInfo();

        cvWalk.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // calendarView에서 선택된 값을 읽어와 문자열로 만들기
                // yyyy-mm-dd와 같은 형태
                isWalkedDate = false;
                dateStr = String.valueOf(year) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(dayOfMonth);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date date = null;
                try {
                    date = formatter.parse(dateStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                for(WalkInfo w : walkList) {
                    String walkDateStr = formatter.format(w.getDate());
                    Date walkDate =  null;
                    try {
                        walkDate = formatter.parse(walkDateStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (date.equals(walkDate)) {
                        walkInfo = w;
                        isWalkedDate = true;
                        break;
                    }
                }

                if (!isWalkedDate) {
                    Toast.makeText(WalkCalendarActivity.this, "해당 일자에 해당하는 운동 정보가 없습니다", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(WalkCalendarActivity.this, WalkCalendarDetailActivity.class);
                    intent.putExtra("dto", walkInfo);
                    intent.putExtra("date", dateStr);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        walkList = manager.getAllWalkInfo();
    }
}