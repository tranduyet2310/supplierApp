package com.example.suppileragrimart.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class SupplierIntro implements Parcelable {
    private long id;
    private String description;
    private String type;
    private long supplierId;
    private List<Image> images;

    public SupplierIntro() {
    }

    protected SupplierIntro(Parcel in) {
        id = in.readLong();
        description = in.readString();
        type = in.readString();
        supplierId = in.readLong();
    }

    public static final Creator<SupplierIntro> CREATOR = new Creator<SupplierIntro>() {
        @Override
        public SupplierIntro createFromParcel(Parcel in) {
            return new SupplierIntro(in);
        }

        @Override
        public SupplierIntro[] newArray(int size) {
            return new SupplierIntro[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(long supplierId) {
        this.supplierId = supplierId;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(description);
        dest.writeString(type);
        dest.writeLong(supplierId);
    }
}
