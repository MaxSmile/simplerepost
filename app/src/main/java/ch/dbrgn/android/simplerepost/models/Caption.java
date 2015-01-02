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

public class Caption implements Parcelable {

    /*** Fields ***/

    private Long id;
    private Long created_time;
    private String text;
    private User from;


    /*** Constructors ***/

    public Caption(Long id, Long created_time, String text, User from) {
        this.id = id;
        this.created_time = created_time;
        this.text = text;
        this.from = from;
    }

    public Caption(Parcel in) {
        readFromParcel(in);
    }


    /*** Getters ***/

    public Long getId() {
        return id;
    }

    public Long getCreatedTime() {
        return created_time;
    }

    public String getText() {
        return text;
    }

    public User getUser() {
        return from;
    }


    /*** Implement parcelable interface ***/

    public static final Parcelable.Creator<Caption> CREATOR = new Parcelable.Creator<Caption>() {
        @Override
        public Caption createFromParcel(Parcel source) {
            return new Caption(source);
        }
        @Override
        public Caption[] newArray(int size) {
            return new Caption[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(created_time);
        dest.writeString(text);
        dest.writeParcelable(from, flags);
    }

    private void readFromParcel(Parcel in) {
        id = in.readLong();
        created_time = in.readLong();
        text = in.readString();
        from = in.readParcelable(User.class.getClassLoader());
    }

}