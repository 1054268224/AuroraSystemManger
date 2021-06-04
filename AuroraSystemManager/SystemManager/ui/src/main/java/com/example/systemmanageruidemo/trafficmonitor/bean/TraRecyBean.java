package com.example.systemmanageruidemo.trafficmonitor.bean;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

public class TraRecyBean implements Parcelable {
    String packageName;
    Drawable imageId;
    String name;
    long usedTraSize;
    boolean islimit;
    long qiantai;
    long houtai;
    String uid="";
    int key;


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(packageName);
//        try {
//            dest.writeParcelable((Parcelable) imageId, flags);
//        } catch (Throwable ex) {
//            ex.printStackTrace();
//        }
        dest.writeString(name);
        dest.writeLong(usedTraSize);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dest.writeBoolean(islimit);
        }
        dest.writeLong(qiantai);
        dest.writeLong(houtai);
        dest.writeString(uid);
        dest.writeInt(key);
    }

    public TraRecyBean(Parcel source) {
        packageName = source.readString();
//        try {
//            imageId = source.readParcelable(this.getClass().getClassLoader());
//        } catch (Throwable ex) {
//            ex.printStackTrace();
//        }
        name = source.readString();
        usedTraSize = source.readLong();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            islimit = source.readBoolean();
        }
        qiantai = source.readLong();
        houtai = source.readLong();
        uid=source.readString();
        key=source.readInt();
    }

    public long getQiantai() {
        return qiantai;
    }

    public void setQiantai(long qiantai) {
        this.qiantai = qiantai;
    }

    public long getHoutai() {
        return houtai;
    }

    public void setHoutai(long houtai) {
        this.houtai = houtai;
    }

    boolean isInvalidControlApp; //禁止被控制

    public boolean isInvalidControlApp() {
        return isInvalidControlApp;
    }

    public void setInvalidControlApp(boolean invalidControlApp) {
        isInvalidControlApp = invalidControlApp;
    }

    public TraRecyBean(String packageName, String name) {
        this.packageName = packageName;
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getImageId() {
        return imageId;
    }

    public void setImageId(Drawable imageId) {
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUsedTraSize() {
        return usedTraSize;
    }

    public void setUsedTraSize(long usedTraSize) {
        this.usedTraSize = usedTraSize;
    }

    public boolean isIslimit() {
        return islimit;
    }

    public void setIslimit(boolean islimit) {
        this.islimit = islimit;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TraRecyBean> CREATOR = new Creator<TraRecyBean>() {

        /**
         * 供外部类反序列化本类数组使用
         */
        @Override
        public TraRecyBean[] newArray(int size) {
            return new TraRecyBean[size];
        }

        /**
         * 从Parcel中读取数据
         */
        @Override
        public TraRecyBean createFromParcel(Parcel source) {
            return new TraRecyBean(source);
        }
    };


}
