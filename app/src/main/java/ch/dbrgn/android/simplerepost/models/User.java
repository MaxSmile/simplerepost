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

public class User implements Parcelable {

    /*** Fields ***/

    private String id;
    private String username;
    private String full_name;
    private String profile_picture;
    private String bio;
    private String website;


    /*** Constructors ***/

    public User(String id, String username, String full_name, String profile_picture, String bio, String website) {
        this.id = id;
        this.username = username;
        this.full_name = full_name;
        this.profile_picture = profile_picture;
        this.bio = bio;
        this.website = website;
    }

    public User(Parcel in) {
        readFromParcel(in);
    }


    /*** Getters ***/

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return full_name;
    }

    public String getFullNameOrUsername() {
        if (full_name.equals("")) {
            return username;
        }
        return full_name;
    }

    public String getProfilePicture() {
        return profile_picture;
    }

    public String getBio() {
        return bio;
    }

    public String getWebsite() {
        return website;
    }


    /*** Implement parcelable interface ***/

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }
        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(username);
        dest.writeString(full_name);
        dest.writeString(profile_picture);
        dest.writeString(bio);
        dest.writeString(website);
    }

    private void readFromParcel(Parcel in) {
        id = in.readString();
        username = in.readString();
        full_name = in.readString();
        profile_picture = in.readString();
        bio = in.readString();
        website = in.readString();
    }
}