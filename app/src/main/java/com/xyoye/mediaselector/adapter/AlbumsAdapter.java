package com.xyoye.mediaselector.adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xyoye.mediaselector.R;
import com.xyoye.mediaselector.bean.AlbumBean;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by xyoye on 2019/1/27.
 */

public class AlbumsAdapter extends RecyclerViewCursorAdapter {
    private WeakReference<Context> mContext;
    private onAlbumItemClickListener mListener;

    public AlbumsAdapter(Cursor c) {
        super(c);
    }

    public void setOnAlbumItemClickListener(onAlbumItemClickListener listener){
        mListener = listener;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, Cursor cursor) {
        AlbumBean albumBean = AlbumBean.valueOf(cursor);
        ((AlbumsAdapter.AlbumViewHolder) holder).name.setText(albumBean.getDisplayName());
        ((AlbumsAdapter.AlbumViewHolder) holder).count.setText(String.valueOf(albumBean.getCount()));

        Context context = mContext.get();
        if (context != null)
        Glide.with(context)
                .load( Uri.fromFile(new File(albumBean.getCoverPath())))
                .into(((AlbumViewHolder) holder).cover);

        ((AlbumViewHolder) holder).itemView.setOnClickListener(v -> {
            if (mListener != null)
                mListener.onClick(v, albumBean);
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
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_album, viewGroup, false);
        return new AlbumViewHolder(view);
    }

    private static class AlbumViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        ImageView cover;
        TextView name;
        TextView count;

        AlbumViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.cover = itemView.findViewById(R.id.album_cover);
            this.name = itemView.findViewById(R.id.album_name);
            this.count = itemView.findViewById(R.id.album_count);
        }
    }

    public interface onAlbumItemClickListener{
        void onClick(View v, AlbumBean albumBean);
    }
}
