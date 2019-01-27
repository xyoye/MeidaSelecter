package com.xyoye.mediaselector.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xyoye.mediaselector.R;
import com.xyoye.mediaselector.bean.PhotoBean;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by xyoye on 2019/1/27.
 */

public class PreviewFragment extends Fragment {
    @BindView(R.id.preview_iv)
    ImageView previewIv;

    private Unbinder unbinder;
    private Context mContext;

    public static PreviewFragment newInstance(PhotoBean photoBean) {
        PreviewFragment fragment = new PreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("photo", photoBean);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preview, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        Bundle bundle = getArguments();
        if (bundle == null)
            return;
        PhotoBean photoBean = bundle.getParcelable("photo");
        if (photoBean == null)
            return;

        Glide.with(mContext)
                .load(photoBean.getContentUri())
                .into(previewIv);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
    }
}
