/**
 * SimpleRepost -- A simple Instagram reposting Android app.
 * Copyright (C) 2014--2014 Danilo Bargen
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

package ch.dbrgn.android.simplerepost.api;

import ch.dbrgn.android.simplerepost.models.MediaResponse;
import ch.dbrgn.android.simplerepost.models.Profile;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface MediaApi {

    @GET("/media/shortcode/{media_shortcode}")
    void getMediaByShortcode(
            @Path("media_shortcode") String shortcode,
            @Query("access_token") String access_token,
            Callback<MediaResponse> callback
    );

    @GET("/media/shortcode/{media_id}")
    void getMedia(
            @Path("media_id") String id,
            @Query("access_token") String access_token,
            Callback<MediaResponse> callback
    );

}