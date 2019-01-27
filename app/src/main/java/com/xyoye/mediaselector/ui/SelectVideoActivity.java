package com.xyoye.mediaselector.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.xyoye.mediaselector.R;
import com.xyoye.mediaselector.adapter.VideoAdapter;
import com.xyoye.mediaselector.bean.VideoBean;
import com.xyoye.mediaselector.loader.LoaderCollection;
import com.xyoye.mediaselector.utils.SpacesItemDecoration;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xyoye on 2019/1/27.
 */

public class SelectVideoActivity extends AppCompatActivity implements LoaderCollection.LoaderCallback {
    @BindView(R.id.video_rv)
    RecyclerView videoRv;

    private VideoAdapter videoAdapter;
    private LoaderCollection videoCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_video);
        ButterKnife.bind(this);

        videoAdapter = new VideoAdapter(null);
        DefaultItemAnimator animator = (DefaultItemAnimator)videoRv.getItemAnimator();
        if (animator != null)
            animator.setSupportsChangeAnimations(false);
        videoRv.setLayoutManager(new GridLayoutManager(SelectVideoActivity.this, 3));
        videoRv.addItemDecoration(new SpacesItemDecoration( ConvertUtils.dp2px(3), 3));
        videoRv.setAdapter(videoAdapter);

        videoCollection = new LoaderCollection(this, this);
        videoCollection.loadVideo();
    }

    @OnClick({R.id.back_iv, R.id.preview_tv, R.id.select_bt})
    public void onViewClicked(View view) {
        ArrayList<VideoBean> checkedVideo = new ArrayList<>(videoAdapter.getCheckedPhoto());
        switch (view.getId()) {
            case R.id.back_iv:
                SelectVideoActivity.this.finish();
                break;
            case R.id.preview_tv:
                if (checkedVideo.size() > 0){
                    Intent intent = new Intent(SelectVideoActivity.this, PlayerActivity.class);
                    intent.putExtra("url", checkedVideo.get(0).getVideoPath());
                    startActivity(intent);
                }
                break;
            case R.id.select_bt:
                if (checkedVideo.size() == 0){
                    ToastUtils.showShort("请选择视频");
                }else {
                    ToastUtils.showShort(checkedVideo.get(0).getVideoPath());
                }
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoCollection.onDestroy();
    }

    @Override
    public void onLoaded(Cursor cursor) {
        videoAdapter.swapCursor(cursor);
    }

    @Override
    public void onReset() {
        videoAdapter.swapCursor(null);
    }
}
