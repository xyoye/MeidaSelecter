package com.xyoye.mediaselector;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.xyoye.mediaselector.ui.SelectPhotoActivity;
import com.xyoye.mediaselector.ui.SelectVideoActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PHOTO_PERMISSION = 1001;
    private static final int REQUEST_VIDEO_PERMISSION = 1002;
    private String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ActivityCompat.requestPermissions(this, permissions, 1003);
    }

    @OnClick({R.id.photo_select, R.id.video_select})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.photo_select:
                if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) ||
                        (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED))
                    ActivityCompat.requestPermissions(this, permissions, REQUEST_PHOTO_PERMISSION);
                else
                    startActivity(new Intent(this, SelectPhotoActivity.class));
                break;
            case R.id.video_select:
                if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) ||
                        (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED))
                    ActivityCompat.requestPermissions(this, permissions, REQUEST_VIDEO_PERMISSION);
                else
                    startActivity(new Intent(this, SelectVideoActivity.class));
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if (requestCode == REQUEST_PHOTO_PERMISSION){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED){
                startActivity(new Intent(this, SelectPhotoActivity.class));
            }
            return;
        }else if (requestCode == REQUEST_VIDEO_PERMISSION){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED){
                startActivity(new Intent(this, SelectVideoActivity.class));
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
