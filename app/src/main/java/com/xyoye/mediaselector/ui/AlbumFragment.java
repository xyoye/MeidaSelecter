package com.xyoye.mediaselector.ui;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xyoye.mediaselector.R;
import com.xyoye.mediaselector.adapter.AlbumsAdapter;
import com.xyoye.mediaselector.bean.AlbumBean;
import com.xyoye.mediaselector.loader.LoaderCollection;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by xyoye on 2019/1/27.
 */

public class AlbumFragment extends Fragment implements LoaderCollection.LoaderCallback {
    @BindView(R.id.album_rv)
    RecyclerView albumRv;

    private Unbinder unbinder;
    private AlbumActionListener mActionListener;
    private LoaderCollection albumCollection;
    private AlbumsAdapter albumsAdapter;
    private AlbumBean mAlbumBean;

    public static AlbumFragment newInstance() {
        return new AlbumFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void initView(){
        albumsAdapter = new AlbumsAdapter(null);
        albumsAdapter.setOnAlbumItemClickListener((v, album) -> {
            mActionListener.onAlbumCheck(album);
        });
        albumRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        albumRv.setAdapter(albumsAdapter);

        //加载相册
        albumCollection = new LoaderCollection(getActivity(), this);
        albumCollection.loadAlbum();
    }

    @Override
    public void onLoaded(Cursor cursor) {
        //加载相册数据
        albumsAdapter.swapCursor(cursor);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            cursor.moveToPosition(0);
            mAlbumBean = AlbumBean.valueOf(cursor);
            mActionListener.onAlbumLoaded(mAlbumBean);
        });
    }

    @Override
    public void onReset() {
        albumsAdapter.swapCursor(null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context != null){
            if (context instanceof AlbumActionListener)
                mActionListener = (AlbumActionListener)context;
            else
                throw new RuntimeException("Activity必须实现AlbumListener");
        }
    }

    public void reloadAlbum(){
        if (albumCollection != null)
            albumCollection.reloadAlbum();
    }

    public interface AlbumActionListener{
        void onAlbumCheck(AlbumBean albumBean);

        void onAlbumLoaded(AlbumBean albumBean);
    }
}
