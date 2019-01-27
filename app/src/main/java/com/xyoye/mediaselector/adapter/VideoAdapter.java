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
import com.xyoye.mediaselector.bean.VideoBean;

import java.lang.ref.WeakReference;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by xyoye on 2019/1/27.
 */

public class VideoAdapter extends RecyclerViewCursorAdapter {
    private WeakReference<Context> mContext;
    private onVideoItemClickListener mListener;
    private Set<VideoBean> mCheckedVideo;
    private int mImageResize;

    public void setVideoItemClickLisener(onVideoItemClickListener listener){
        this.mListener = listener;
    }

    public Set<VideoBean> getCheckedPhoto(){
        return mCheckedVideo;
    }

    public VideoAdapter(Cursor c) {
        super(c);
        mCheckedVideo =  new LinkedHashSet<>();
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, Cursor cursor) {
        VideoBean videoBean = VideoBean.valueOf(cursor);
        Context context = mContext.get();
        if (context == null)
            return;
        int resize = getImageResize(context);
        RequestOptions options = new RequestOptions()
                .override(resize, resize)
                .centerCrop();
        ((VideoViewHolder) holder).checkIv
                .setImageResource(mCheckedVideo.contains(videoBean) ? R.mipmap.ic_item_checked : R.mipmap.ic_item_uncheck);

        //根据视频id查询视频缩略图，速度较快但相对模糊
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inDither = false;
//        options.inPreferredConfig = Bitmap.Config.RGB_565;
//        bitmap = MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(), videoBean.getId(), MediaStore.Images.Thumbnails.MICRO_KIND, options);

        //通过URI加载，只有第一次加载时较慢，但比较清晰
        Glide.with(context)
                .load(videoBean.getUri())
                .apply(options)
                .into(((VideoViewHolder) holder).videoIv);

        ((VideoViewHolder) holder).itemView.setOnClickListener(v -> {
            if (mCheckedVideo.contains(videoBean)){
                mCheckedVideo.remove(videoBean);
            }else {
                mCheckedVideo.clear();
                mCheckedVideo.add(videoBean);
            }
            if (mListener != null){
                mListener.onVideoClick(mCheckedVideo.size());
            }
            notifyDataSetChanged();
        });

    }

    @Override
    protected int getItemViewType(int position, Cursor cursor) {
        return 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = new WeakReference<>(viewGroup.getContext());
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_video, viewGroup, false);
        return new VideoViewHolder(view, i);
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

    private static class VideoViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        ImageView videoIv;
        ImageView checkIv;
        int position;

        VideoViewHolder(View itemView, int position) {
            super(itemView);
            this.position = position;
            this.itemView = itemView;
            this.videoIv = itemView.findViewById(R.id.video_iv);
            this.checkIv = itemView.findViewById(R.id.check_iv);
        }
    }

    public interface onVideoItemClickListener{
        void onVideoClick(int size);
    }
}
