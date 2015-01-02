/**
 * SimpleRepost -- A simple Instagram reposting Android app.
 * Copyright (C) 2014-2014 Danilo Bargen
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/

package ch.dbrgn.android.simplerepost.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Images implements Parcelable {

    /*** Fields ***/

    private Image thumbnail;
    private Image low_resolution;
    private Image standard_resolution;


    /*** Constructors ***/

    public Images(Image thumbnail, Image low_resolution, Image standard_resolution) {
        this.thumbnail = thumbnail;
        this.low_resolution = low_resolution;
        this.standard_resolution = standard_resolution;
    }

    public Images(Parcel in) {
        readFromParcel(in);
    }


    /*** Getters ***/

    public Image getThumbnail() {
        return thumbnail;
    }

    public Image getLowResolution() {
        return low_resolution;
    }

    public Image getStandardResolution() {
        return standard_resolution;
    }


    /*** Implement parcelable interface ***/

    public static final Parcelable.Creator<Images> CREATOR = new Parcelable.Creator<Images>() {
        @Override
        public Images createFromParcel(Parcel source) {
            return new Images(source);
        }
        @Override
        public Images[] newArray(int size) {
            return new Images[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(thumbnail, flags);
        dest.writeParcelable(low_resolution, flags);
        dest.writeParcelable(standard_resolution, flags);
    }

    private void readFromParcel(Parcel in) {
        thumbnail = in.readParcelable(Image.class.getClassLoader());
        low_resolution = in.readParcelable(Image.class.getClassLoader());
        standard_resolution = in.readParcelable(Image.class.getClassLoader());
    }
}