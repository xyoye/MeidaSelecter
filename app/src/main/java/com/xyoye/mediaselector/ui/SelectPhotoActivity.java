package com.xyoye.mediaselector.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v4.os.EnvironmentCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.xyoye.mediaselector.R;
import com.xyoye.mediaselector.adapter.PhotoAdapter;
import com.xyoye.mediaselector.bean.AlbumBean;
import com.xyoye.mediaselector.bean.PhotoBean;
import com.xyoye.mediaselector.loader.LoaderCollection;
import com.xyoye.mediaselector.utils.AnimeHelper;
import com.xyoye.mediaselector.utils.SpacesItemDecoration;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xyoye on 2019/1/27.
 */

public class SelectPhotoActivity extends AppCompatActivity implements LoaderCollection.LoaderCallback, AlbumFragment.AlbumActionListener {
    private static final int TAKE_PHOTO_FLAG = 101;

    @BindView(R.id.toolbar_text)
    TextView toolbarText;
    @BindView(R.id.toolbar_image)
    ImageView toolbarImage;
    @BindView(R.id.photo_rv)
    RecyclerView photoRv;
    @BindView(R.id.select_bt)
    Button selectBt;

    private LoaderCollection photoCollection;
    private PhotoAdapter photoAdapter;

    private boolean isAlbumShow = false;
    private boolean isScannedPhoto = false;
    private String mCurrentPhotoPath;
    private AlbumBean mAlbumBean;

    private AlbumFragment albumFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_photo);
        ButterKnife.bind(this);

        albumFragment = AlbumFragment.newInstance();
        fragmentManager = getSupportFragmentManager();

        photoAdapter = new PhotoAdapter(null, 5);
        photoAdapter.setPhotoCheckListener(new PhotoAdapter.onPhotoItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onPhotoCheck(int size) {
                selectBt.setText("完成("+size+"/"+ photoAdapter.getCheckMaxSize()+")");
            }

            @Override
            public void onCaptureCheck() {
                dispatchCaptureIntent(SelectPhotoActivity.this, TAKE_PHOTO_FLAG);
            }
        });
        photoRv.setLayoutManager(new GridLayoutManager(this, 3));
        photoRv.addItemDecoration(new SpacesItemDecoration( ConvertUtils.dp2px(3),3));
        photoRv.setAdapter(photoAdapter);

        mAlbumBean = new AlbumBean("-1", "", "所有图片", 0);
        photoCollection = new LoaderCollection(this, this);
        photoCollection.loadPhoto(mAlbumBean);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        photoCollection.onDestroy();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLoaded(Cursor cursor) {
        //是否为拍摄后第一次加载数据，已选中数量小于最大选中
        //选中当前第一个数据（第0个数据为照相机item）
        int checkSize = photoAdapter.getCheckedPhoto().size();
        if (isScannedPhoto && checkSize < photoAdapter.getCheckMaxSize()){
            isScannedPhoto = false;
            cursor.moveToPosition(1);
            PhotoBean photoBean = PhotoBean.valueOf(cursor);
            photoAdapter.getCheckedPhoto().add(photoBean);
            selectBt.setText("完成("+photoAdapter.getCheckedPhoto().size()+"/5)");
        }
        //加载照片数据
        photoAdapter.swapCursor(cursor);
    }

    @Override
    public void onReset() {
        photoAdapter.swapCursor(null);
    }

    private void onAlbumSelected(AlbumBean albumBean) {
        //执行相册布局隐藏动画，切换照片数据
        resetFragment();
        photoCollection.reloadPhoto(albumBean);
    }

    //打开照相机
    public void dispatchCaptureIntent(Context context, int requestCode) {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (captureIntent.resolveActivity(context.getPackageManager()) != null) {
            File photoFile;
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = String.format("JPEG_%s.jpg", timeStamp);

            //选择picture文件夹作为图片保存文件夹
            File storageDir  = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PICTURES);
            if (!storageDir.exists()) storageDir.mkdirs();

            //拍照后保存数据的文件
            photoFile = new File(storageDir, imageFileName);
            if (!Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(photoFile))) {
                return;
            }
            mCurrentPhotoPath = photoFile.getAbsolutePath();
            Uri mCurrentPhotoUri = FileProvider.getUriForFile(context,"com.taobubao.textile.fileprovider", photoFile);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentPhotoUri);
            captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                List<ResolveInfo> resInfoList = context.getPackageManager()
                        .queryIntentActivities(captureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    context.grantUriPermission(packageName, mCurrentPhotoUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            }
            startActivityForResult(captureIntent, requestCode);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == TAKE_PHOTO_FLAG){
                //拍照后通知系统扫描照片，将照片加入ContentProvider
                MediaScannerConnection.scanFile(this, new String[]{mCurrentPhotoPath}, new String[]{"image/jpeg"}, (path, uri) ->
                    runOnUiThread(() -> {
                        //将状态置为已扫描
                        isScannedPhoto = true;
                        //重新扫描相册列表
                        albumFragment.reloadAlbum();
                        //重新扫描当前相册
                        photoCollection.reloadPhoto(mAlbumBean);
                    }
                ));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick({R.id.back_iv, R.id.preview_tv, R.id.select_bt, R.id.toolbar_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                SelectPhotoActivity.this.finish();
                break;
            case R.id.preview_tv:
                ArrayList<PhotoBean> checkedBean = new ArrayList<>(photoAdapter.getCheckedPhoto());
                if (checkedBean.size() == 0){
                    ToastUtils.showShort("未选择图片");
                    return;
                }
                Intent intent = new Intent(SelectPhotoActivity.this, PreviewActivity.class);
                intent.putParcelableArrayListExtra("preview_data", checkedBean);
                startActivity(intent);
                break;
            case R.id.select_bt:
                ArrayList<PhotoBean> checkedPhotoList = new ArrayList<>(photoAdapter.getCheckedPhoto());
                StringBuilder checkedPhoto = new StringBuilder();
                for (int i=0; i<checkedPhotoList.size(); i++){
                    checkedPhoto.append(checkedPhotoList.get(i).getPhotoPath()).append("\n\n");
                }
                ToastUtils.showShort(checkedPhoto.toString());
                break;
            case R.id.toolbar_ll:
                resetFragment();
                break;
        }
    }

    private void resetFragment(){
        if (isAlbumShow) {
            FragmentManager manager = getSupportFragmentManager();
            AlbumFragment fragment = (AlbumFragment)manager.findFragmentByTag(AlbumFragment.class.getSimpleName());
            if (fragment != null) {
                AnimeHelper.rotateDown(toolbarImage, 300);
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.setCustomAnimations(0, R.anim.fragment_hide);
                transaction.remove(fragment);
                transaction.commitAllowingStateLoss();
            }
        } else {
            AnimeHelper.rotateUp(toolbarImage, 300);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.fragment_show, 0);
            transaction.add(R.id.album_container, albumFragment, AlbumFragment.class.getSimpleName());
            transaction.commitAllowingStateLoss();
        }
        isAlbumShow = !isAlbumShow;
    }

    @Override
    public void onAlbumCheck(AlbumBean albumBean) {
        mAlbumBean = albumBean;
        toolbarText.setText(albumBean.getDisplayName());
        onAlbumSelected(albumBean);
    }

    @Override
    public void onAlbumLoaded(AlbumBean albumBean) {
        photoCollection.loadPhoto(mAlbumBean);
    }
}
