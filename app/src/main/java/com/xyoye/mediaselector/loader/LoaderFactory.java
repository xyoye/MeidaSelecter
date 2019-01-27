package com.xyoye.mediaselector.loader;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;

import com.xyoye.mediaselector.bean.AlbumBean;

/**
 * Created by xyoye on 2019/1/27.
 */

public class LoaderFactory {
    private static final Uri queryUri = MediaStore.Files.getContentUri("external");

    //相册加载器
    static class AlbumLoader extends CursorLoader{
        private static final String[] albumColumn = {
                MediaStore.Files.FileColumns._ID,
                "bucket_id",
                "bucket_display_name",
                MediaStore.MediaColumns.DATA,
                "count"};
        private static final String[] searchColumn = {
                MediaStore.Files.FileColumns._ID,
                "bucket_id",
                "bucket_display_name",
                MediaStore.MediaColumns.DATA,
                "COUNT(*) AS count"};

        public static CursorLoader newInstance(Context context) {
            String selection =
                    MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0"
                    + ") GROUP BY (bucket_id";

            String[] selectionArgs = new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)};

            return new AlbumLoader(context, selection, selectionArgs);
        }

        AlbumLoader(@NonNull Context context, String selection, String[] selectionArgs) {
            super(context, queryUri, searchColumn, selection, selectionArgs, "datetaken DESC");
        }

        @Override
        public Cursor loadInBackground() {
            Cursor albums = super.loadInBackground();
            MatrixCursor allAlbum = new MatrixCursor(albumColumn);
            int totalCount = 0;
            String allAlbumCoverPath = "";
            if (albums != null) {
                while (albums.moveToNext()) {
                    totalCount += albums.getInt(albums.getColumnIndex("count"));
                }
                if (albums.moveToFirst()) {
                    allAlbumCoverPath = albums.getString(albums.getColumnIndex(MediaStore.MediaColumns.DATA));
                }
            }
            //添加第一列为所有图片
            allAlbum.addRow(new String[]{"-1", "-1", "所有图片", allAlbumCoverPath,
                    String.valueOf(totalCount)});

            return new MergeCursor(new Cursor[]{allAlbum, albums});
        }
    }

    //图片loader
    static class ImageLoader extends CursorLoader{
        private static boolean mEnableCapture;
        private static final String[] imageColumn = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.MediaColumns.SIZE};

        public static CursorLoader newInstance(Context context, AlbumBean albumBean) {
            mEnableCapture = albumBean.isAll();
            String selection;
            String[] selectionArgs;
            if (albumBean.isAll()){
                selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                        + " AND " + MediaStore.MediaColumns.SIZE + ">0";
                selectionArgs = new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)};
            }else {
                selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                        + " AND " + "bucket_id = ? AND " + MediaStore.MediaColumns.SIZE + ">0";
                selectionArgs = new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE), albumBean.getId() };
            }
            return new ImageLoader(context, selection, selectionArgs);
        }

        ImageLoader(@NonNull Context context, String selection, String[] selectionArgs) {
            super(context, queryUri, imageColumn, selection, selectionArgs, "datetaken DESC");
        }

        @Override
        public Cursor loadInBackground() {
            Cursor result = super.loadInBackground();
            //非“所有图片”的相册和系统不包含照相机硬件，不显示拍照按钮
            PackageManager pm = getContext().getApplicationContext().getPackageManager();
            if (!mEnableCapture || !pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
                return result;
            MatrixCursor matrixCursor = new MatrixCursor(imageColumn);
            matrixCursor.addRow(new Object[]{-1, "", "", "", 0});
            return new MergeCursor(new Cursor[]{matrixCursor, result});
        }
    }

    //视频loader
    static class VideoLoader extends CursorLoader{
        private static final String[] videoColumn = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.MediaColumns.DATA,
                MediaStore.Video.Media.DURATION
        };

        public static CursorLoader newInstance(Context context) {
            String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                            + " AND " + MediaStore.Video.Media.DURATION + ">0";

            String[] selectionArgs = new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)};

            return new VideoLoader(context, selection, selectionArgs);
        }

        VideoLoader(@NonNull Context context, String selection, String[] selectionArgs) {
            super(context, queryUri, videoColumn, selection, selectionArgs, "datetaken DESC");
        }

        @Override
        public Cursor loadInBackground() {
            return super.loadInBackground();
        }
    }
}
