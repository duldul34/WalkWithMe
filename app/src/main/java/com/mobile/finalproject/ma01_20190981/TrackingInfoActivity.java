package com.mobile.finalproject.ma01_20190981;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class TrackingInfoActivity extends AppCompatActivity {

    public static final String TAG = "TrackingInfoActivity";

    EditText etTarget;
    ListView lvList;
    String apiAddress;

    String query;

    TrackingInfoAdapter adapter;
    ArrayList<TrackingInfo> resultList;
    TrackingInfoXmlParser parser;
    TrackingInfoNetworkManager networkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_info);

        etTarget = findViewById(R.id.etTarget);
        lvList = findViewById(R.id.lvList);

        resultList = new ArrayList();
        adapter = new TrackingInfoAdapter(this, R.layout.listview_trackinginfo, resultList);
        lvList.setAdapter(adapter);

        apiAddress = getResources().getString(R.string.api_url);
        parser = new TrackingInfoXmlParser();
        networkManager = new TrackingInfoNetworkManager(this);
        networkManager.setServiceKey(getResources().getString(R.string.service_key));

        new NetworkAsyncTask().execute(apiAddress, " ");

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TrackingInfo dto = resultList.get(position);
                Intent intent = new Intent(TrackingInfoActivity.this, TrackingInfoDetailActivity.class);
                intent.putExtra("dto", dto);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 임시 파일 삭제

    }


    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnSearch:
                query = etTarget.getText().toString();  // UTF-8 인코딩 필요
                // OpenAPI 주소와 query 조합 후 서버에서 데이터를 가져옴
                // 가져온 데이터는 파싱 수행 후 어댑터에 설정
                new NetworkAsyncTask().execute(apiAddress, query);
                break;
        }
    }


    class NetworkAsyncTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDlg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg = ProgressDialog.show(TrackingInfoActivity.this, "Wait", "Downloading...");
        }

        @Override
        protected String doInBackground(String... strings) {
            String address = strings[0];
            String query = strings[1];

            String apiURL = null;
            try {
                apiURL = address + URLEncoder.encode(query, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            System.out.println(apiURL);
            String result = networkManager.downloadContents(apiURL);
            return result;
        }


        @Override
        protected void onPostExecute(String result) {
            // parsing - 수행시간이 짧을 경우 이 부분에서 수행하는 것을 고려
            Log.i(TAG, result);
            progressDlg.dismiss();

            ArrayList<TrackingInfo> parserdList = parser.parse(result);     // 오픈API 결과의 파싱 수행

            if (parserdList.size() == 0) {
                Toast.makeText(TrackingInfoActivity.this, "No data!", Toast.LENGTH_SHORT).show();
            } else {
                resultList.clear();
                resultList.addAll(parserdList);
                adapter.notifyDataSetChanged();
            }
        }
    }
}