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

public class PhotoBean implements Parcelable {

    private final long id;
    private final String mimeType;
    private final Uri uri;
    private final String photoPath;
    private final long size;

    private PhotoBean(long id, String mimeType, String photoPath, long size) {
        this.id = id;
        this.mimeType = mimeType;
        this.photoPath = photoPath;
        Uri contentUri;
        if ("image".startsWith(mimeType)) {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else {
            contentUri = MediaStore.Files.getContentUri("external");
        }
        this.uri = ContentUris.withAppendedId(contentUri, id);
        this.size = size;
    }

    private PhotoBean(Parcel source) {
        id = source.readLong();
        mimeType = source.readString();
        photoPath = source.readString();
        uri = source.readParcelable(Uri.class.getClassLoader());
        size = source.readLong();
    }


    public static final Creator<PhotoBean> CREATOR = new Creator<PhotoBean>() {
        @Override
        public PhotoBean createFromParcel(Parcel source) {
            return new PhotoBean(source);
        }

        @Override
        public PhotoBean[] newArray(int size) {
            return new PhotoBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(mimeType);
        dest.writeString(photoPath);
        dest.writeParcelable(uri, 0);
        dest.writeLong(size);
    }

    public static PhotoBean valueOf(Cursor cursor) {
        return new PhotoBean(cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)),
                cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE)),
                cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA)),
                cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE)));
    }

    public Uri getContentUri() {
        return uri;
    }

    public boolean isGif() {
        return mimeType.equals("image/gif");
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public boolean isCapture(){
        return id == -1;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PhotoBean)) {
            return false;
        }

        PhotoBean other = (PhotoBean) obj;
        return id == other.id
                && (mimeType != null && mimeType.equals(other.mimeType)
                    || (mimeType == null && other.mimeType == null))
                && (photoPath != null && photoPath.equals(other.photoPath)
                    || (photoPath == null && other.photoPath == null))
                && (uri != null && uri.equals(other.uri)
                    || (uri == null && other.uri == null))
                && size == other.size;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + Long.valueOf(id).hashCode();
        if (mimeType != null)
            result = 31 * result + mimeType.hashCode();
        if (photoPath != null)
            result = 31 * result + photoPath.hashCode();
        if (uri != null)
            result = 31 * result + uri.hashCode();
        result = 31 * result + Long.valueOf(size).hashCode();
        return result;
    }
}
