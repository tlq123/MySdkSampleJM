package com.turing.turingsdksample.adapter;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 作者：Administrator on 2017/1/19 0019 15:47
 * 邮箱：licheng@uzoo.com
 */

public class MainBean implements Parcelable {
    private String name;
    private Class cls;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class getCls() {
        return cls;
    }

    public void setCls(Class cls) {
        this.cls = cls;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeSerializable(this.cls);
    }

    public MainBean() {
    }

    protected MainBean(Parcel in) {
        this.name = in.readString();
        this.cls = (Class) in.readSerializable();
    }

    public static final Creator<MainBean> CREATOR = new Creator<MainBean>() {
        @Override
        public MainBean createFromParcel(Parcel source) {
            return new MainBean(source);
        }

        @Override
        public MainBean[] newArray(int size) {
            return new MainBean[size];
        }
    };
}
