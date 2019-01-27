package com.xyoye.mediaselector.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.TextView;

import com.xyoye.mediaselector.R;
import com.xyoye.mediaselector.bean.PhotoBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xyoye on 2019/1/27.
 */

public class PreviewActivity extends AppCompatActivity {
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.position_tv)
    TextView positionTv;

    @SuppressLint("SetTextI18n")
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        setContentView(R.layout.activity_preview);

        ButterKnife.bind(this);
        List<PhotoBean> previewList = getIntent().getParcelableArrayListExtra("preview_data");

        if (previewList == null || previewList.size() < 1) {
            finish();
            return;
        }

        List<PreviewFragment> fragmentList = new ArrayList<>();
        for (int i = 0; i < previewList.size(); i++) {
            PhotoBean photoBean = previewList.get(i);
            PreviewFragment fragment = PreviewFragment.newInstance(photoBean);
            fragmentList.add(fragment);
        }

        PreviewFragmentAdapter fragmentAdapter = new PreviewFragmentAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onPageSelected(int i) {
                positionTv.setText((i+1)+"/"+fragmentList.size());
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        viewPager.setAdapter(fragmentAdapter);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setCurrentItem(0);
        positionTv.setText("1/"+fragmentList.size());
    }

    @OnClick(R.id.back_iv)
    public void onViewClicked() {
        PreviewActivity.this.finish();
    }

    private class PreviewFragmentAdapter extends FragmentPagerAdapter {
        private List<PreviewFragment> list;

        private PreviewFragmentAdapter(FragmentManager supportFragmentManager, List<PreviewFragment> list) {
            super(supportFragmentManager);
            this.list = list;
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }
    }
}
