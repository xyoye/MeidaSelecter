package com.xyoye.mediaselector.bean;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

/**
 * Created by xyoye on 2019/1/27.
 */

public class VideoBean implements Parcelable {
    private long id;
    private Uri uri;
    private String videoPath;
    private long duration;

    private VideoBean(long id, String videoPath, long duration) {
        this.id = id;
        this.videoPath = videoPath;
        Uri contentUri = MediaStore.Files.getContentUri("external");
        this.uri = ContentUris.withAppendedId(contentUri, id);
        this.duration = duration;
    }

    protected VideoBean(Parcel in) {
        id = in.readLong();
        videoPath = in.readString();
        uri = in.readParcelable(Uri.class.getClassLoader());
        duration = in.readLong();
    }

    public Uri getUri() {
        return uri;
    }

    public String getVideoPath() {
        return videoPath;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(videoPath);
        dest.writeParcelable(uri, flags);
        dest.writeLong(duration);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VideoBean> CREATOR = new Creator<VideoBean>() {
        @Override
        public VideoBean createFromParcel(Parcel in) {
            return new VideoBean(in);
        }

        @Override
        public VideoBean[] newArray(int size) {
            return new VideoBean[size];
        }
    };

    public static VideoBean valueOf(Cursor cursor) {
        return new VideoBean(cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)),
                cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA)),
                cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION)));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof VideoBean)) {
            return false;
        }

        VideoBean other = (VideoBean) obj;
        return id == other.id
                && (videoPath != null && videoPath.equals(other.videoPath))
                    || (videoPath == null && other.videoPath == null)
                && (uri != null && uri.equals(other.uri)
                    || (uri == null && other.uri == null))
                && duration == other.duration;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + Long.valueOf(id).hashCode();
        if (videoPath != null)
            result = 31 * result + videoPath.hashCode();
        if (uri != null)
            result = 31 * result + uri.hashCode();
        result = 31 * result + Long.valueOf(duration).hashCode();
        return result;
    }
}
