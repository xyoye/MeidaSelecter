package com.xyoye.mediaselector.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.blankj.utilcode.util.ConvertUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.xyoye.mediaselector.R;
import com.xyoye.mediaselector.bean.PhotoBean;

import java.lang.ref.WeakReference;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by xyoye on 2019/1/27.
 */

public class PhotoAdapter extends RecyclerViewCursorAdapter {
    private WeakReference<Context> mContext;
    private Set<PhotoBean> mCheckedPhoto;
    private onPhotoItemClickListener mListener;
    private int mCheckMaxSize;
    private int mImageResize;

    public PhotoAdapter(Cursor c, int maxSize) {
        super(c);
        this.mCheckMaxSize = maxSize;
        mCheckedPhoto =  new LinkedHashSet<>();
    }

    public void setPhotoCheckListener(onPhotoItemClickListener listener){
        mListener = listener;
    }

    public Set<PhotoBean> getCheckedPhoto(){
        return mCheckedPhoto;
    }

    public int getCheckMaxSize(){
        return mCheckMaxSize;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, Cursor cursor) {
        PhotoBean photoBean = PhotoBean.valueOf(cursor);
        Context context = mContext.get();
        if (context == null)
            return;
        int resize = getImageResize(context);
        RequestOptions options = new RequestOptions()
                .override(resize, resize)
                .centerCrop();
        if (photoBean.isCapture()){
            ((PhotoViewHolder) holder).checkIv.setVisibility(View.GONE);
            Glide.with(context)
                    .load(R.mipmap.ic_capture)
                    .apply(options)
                    .into(((PhotoViewHolder) holder).photoIv);

            ((PhotoViewHolder) holder).itemView.setOnClickListener(v -> {
                if (mListener != null){
                    mListener.onCaptureCheck();
                }
            });
        }else {
            ((PhotoViewHolder) holder).checkIv.setVisibility(View.VISIBLE);
            ((PhotoViewHolder) holder).checkIv
                    .setImageResource(mCheckedPhoto.contains(photoBean) ? R.mipmap.ic_item_checked : R.mipmap.ic_item_uncheck);

            Glide.with(context)
                    .load(photoBean.getContentUri())
                    .apply(options)
                    .into(((PhotoViewHolder) holder).photoIv);

            ((PhotoViewHolder) holder).itemView.setOnClickListener(v -> {
                if (mCheckedPhoto.contains(photoBean)){
                    mCheckedPhoto.remove(photoBean);
                }else if (mCheckedPhoto.size() < mCheckMaxSize){
                    mCheckedPhoto.add(photoBean);
                }
                if (mListener != null){
                    mListener.onPhotoCheck(mCheckedPhoto.size());
                }
                notifyDataSetChanged();
            });
        }

    }

    @Override
    protected int getItemViewType(int position, Cursor cursor) {
        return 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = new WeakReference<>(viewGroup.getContext());
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_photo, viewGroup, false);
        return new PhotoViewHolder(view, i);
    }

    private int getImageResize(Context context) {
        if (mImageResize == 0) {
            int spanCount = 3;
            int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            int availableWidth = screenWidth - ConvertUtils.dp2px(3) * (spanCount - 1);
            mImageResize = availableWidth / spanCount;
            mImageResize = (int) (mImageResize * 0.85f);
        }
        return mImageResize;
    }

    private static class PhotoViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        ImageView photoIv;
        ImageView checkIv;
        int position;

        PhotoViewHolder(View itemView, int position) {
            super(itemView);
            this.position = position;
            this.itemView = itemView;
            this.photoIv = itemView.findViewById(R.id.photo_iv);
            this.checkIv = itemView.findViewById(R.id.check_iv);
        }
    }

    public interface onPhotoItemClickListener{
        void onPhotoCheck(int size);

        void onCaptureCheck();
    }
}
