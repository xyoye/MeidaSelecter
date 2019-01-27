package com.xyoye.mediaselector.loader;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.xyoye.mediaselector.bean.AlbumBean;

import java.lang.ref.WeakReference;

/**
 * Created by xyoye on 2019/1/27.
 */

public class LoaderCollection implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String ALBUM_FLAG = "album_flag";
    private static final int ALBUM_LOADER_ID = 1;
    private static final int PHOTO_LOADER_ID = 2;
    private static final int VIDEO_LOADER_ID = 3;

    private boolean mLoadFinished;
    private LoaderCallback mCallback;
    private LoaderManager mLoaderManager;
    private WeakReference<Context> mContext;

    public LoaderCollection(FragmentActivity activity, LoaderCallback callback){
        this.mCallback = callback;
        this.mLoaderManager = LoaderManager.getInstance(activity);
        this.mContext = new WeakReference<>(activity);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loadId, @Nullable Bundle bundle) {
        Context context = mContext.get();
        if (mContext.get() == null) {
            return new Loader<>(context);
        }
        mLoadFinished = false;
        if (loadId == ALBUM_LOADER_ID)
            //实例化相册加载器
            return LoaderFactory.AlbumLoader.newInstance(context);
        else if (loadId == PHOTO_LOADER_ID){
            if (bundle == null)
                return new Loader<>(context);
            AlbumBean albumBean = bundle.getParcelable(ALBUM_FLAG);
            if (albumBean == null) {
                return new Loader<>(context);
            }
            //实例化图片加载器
            return LoaderFactory.ImageLoader.newInstance(context, albumBean);
        }else {
            //实例化视频加载器
            return LoaderFactory.VideoLoader.newInstance(context);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        Context context = mContext.get();
        if (context == null) {
            return;
        }

        if (!mLoadFinished) {
            mLoadFinished = true;
            //加载完成回调cursor
            mCallback.onLoaded(cursor);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Context context = mContext.get();
        if (context == null) {
            return;
        }

        mCallback.onReset();
    }

    public void onDestroy() {
        if (mLoaderManager != null) {
            //销毁加载器
            mLoaderManager.destroyLoader(ALBUM_LOADER_ID);
            mLoaderManager.destroyLoader(PHOTO_LOADER_ID);
            mLoaderManager.destroyLoader(VIDEO_LOADER_ID);
        }
        mCallback = null;
    }

    //第一次加载
    public void loadAlbum() {
        mLoaderManager.initLoader(ALBUM_LOADER_ID, null, this);
    }

    //重新加载
    public void reloadAlbum(){
        mLoaderManager.restartLoader(ALBUM_LOADER_ID, null, this);
    }

    public void loadPhoto(AlbumBean albumBean){
        Bundle args = new Bundle();
        args.putParcelable(ALBUM_FLAG, albumBean);
        mLoaderManager.initLoader(PHOTO_LOADER_ID, args, this);
    }
    public void reloadPhoto(AlbumBean albumBean){
        Bundle args = new Bundle();
        args.putParcelable(ALBUM_FLAG, albumBean);
        mLoaderManager.restartLoader(PHOTO_LOADER_ID, args, this);
    }

    public void loadVideo(){
        mLoaderManager.initLoader(VIDEO_LOADER_ID, null, this);
    }

    public void reloadVideo(){
        mLoaderManager.restartLoader(VIDEO_LOADER_ID, null, this);
    }

    public interface LoaderCallback {
        void onLoaded(Cursor cursor);

        void onReset();
    }
}
