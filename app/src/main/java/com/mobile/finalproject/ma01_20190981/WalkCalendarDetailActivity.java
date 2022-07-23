package com.mobile.finalproject.ma01_20190981;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WalkCalendarDetailActivity extends AppCompatActivity {

    TextView tvDateCalDetail;
    TextView tvKmCalDetail;
    TextView tvKcalCalDetail;
    TextView tvHourCalDetail;
    TextView tvMinCalDetail;
    TextView tvSecCalDetail;
    ImageView ivPhoto;

    private static final int REQUEST_TAKE_PHOTO = 200;
    private String mCurrentPhotoPath;
    WalkInfo walkInfo;

    WalkInfoDBManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_calendar_detail);

        manager = new WalkInfoDBManager(this);

        tvDateCalDetail = findViewById(R.id.tvDateCalDetail);
        tvKmCalDetail = findViewById(R.id.tvKmCalDetail);
        tvKcalCalDetail = findViewById(R.id.tvKcalCalDetail);
        tvHourCalDetail = findViewById(R.id.tvHourCalDetail);
        tvMinCalDetail = findViewById(R.id.tvMinCalDetail);
        tvSecCalDetail = findViewById(R.id.tvSecCalDetail);
        ivPhoto = findViewById(R.id.ivPhoto);

        Intent intent = getIntent();
        walkInfo = (WalkInfo) intent.getSerializableExtra("dto");
        tvKmCalDetail.setText(Float.toString(walkInfo.getMoveKm()));
        tvKcalCalDetail.setText(Float.toString(walkInfo.getKcal()));
        tvHourCalDetail.setText(Float.toString(walkInfo.getTime() / 3600));
        tvMinCalDetail.setText(Integer.toString(walkInfo.getTime() / 60 % 60));
        tvSecCalDetail.setText(Integer.toString(walkInfo.getTime() % 60));
        if (walkInfo.getPhotoPath() != null) {
            mCurrentPhotoPath = walkInfo.getPhotoPath();
            Log.d("photo", mCurrentPhotoPath);
            setPic();
        }

        String date = intent.getStringExtra("date");
        tvDateCalDetail.setText(date);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddPic:
                dispatchTakePictureIntent();
                break;
            case R.id.btnSave:
                if (mCurrentPhotoPath != null) {
                    walkInfo.setPhotoPath(mCurrentPhotoPath);
                    manager.modifyWalkInfo(walkInfo);
                    manager.close();
                }
                else {
                    Toast.makeText(WalkCalendarDetailActivity.this, "먼저 사진을 촬영해 주세요", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnShare:
                Intent Sharing_intent = new Intent(Intent.ACTION_SEND);
                Sharing_intent.setType("text/plain");

                String Test_Message = walkInfo.toString();

                Sharing_intent.putExtra(Intent.EXTRA_TEXT, Test_Message);

                Intent Sharing = Intent.createChooser(Sharing_intent, "공유");
                startActivity(Sharing);
                break;
        }
    }

    /*원본 사진 파일 저장*/
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;

            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this,
                        "com.mobile.finalproject.ma01_20190981",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }

    }

    /*사진의 크기를 ImageView에서 표시할 수 있는 크기로 변경*/
    private void setPic() {
        // Get the dimensions of the View
        ivPhoto.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int targetW = ivPhoto.getMeasuredWidth();
        int targetH = ivPhoto.getMeasuredHeight();

        /*int targetW = ivPhoto.getWidth();
        int targetH = ivPhoto.getHeight();*/

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        ivPhoto.setImageBitmap(bitmap);
    }

    /*현재 시간 정보를 사용하여 파일 정보 생성*/
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            setPic();
        }
    }
}