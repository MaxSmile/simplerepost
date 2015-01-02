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

public class Media implements Parcelable {

    /*** Fields ***/

    private String id;
    private String type;
    private Long created_time;
    private String link;
    private User user;
    private Images images;
    private Caption caption;


    /*** Constructors ***/

    public Media(String id, String type, Long created_time, String link, User user, Images images, Caption caption) {
        this.id = id;
        this.type = type;
        this.created_time = created_time;
        this.link = link;
        this.user = user;
        this.images = images;
        this.caption = caption;
    }

    public Media(Parcel in) {
        readFromParcel(in);
    }


    /*** Getters ***/

    public String getType() {
        return type;
    }

    public Long getCreatedTime() {
        return created_time;
    }

    public String getLink() {
        return link;
    }

    public Images getImages() {
        return images;
    }

    public Caption getCaption() {
        return caption;
    }

    public String getId() {
        return id;
    }

    public User getUser() {
        return user;
    }


    /*** Implement parcelable interface ***/

    public static final Parcelable.Creator<Media> CREATOR = new Parcelable.Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel source) {
            return new Media(source);
        }
        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(type);
        dest.writeLong(created_time);
        dest.writeString(link);
        dest.writeParcelable(user, flags);
        dest.writeParcelable(images, flags);
        dest.writeParcelable(caption, flags);
    }

    private void readFromParcel(Parcel in) {
        id = in.readString();
        type = in.readString();
        created_time = in.readLong();
        link = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
        images = in.readParcelable(Images.class.getClassLoader());
        caption = in.readParcelable(Caption.class.getClassLoader());
    }

}