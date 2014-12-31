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

package ch.dbrgn.android.simplerepost.events;

import ch.dbrgn.android.simplerepost.api.MediaAccessType;

public class LoadMediaPreviewEvent {

    private final String accessToken;
    private final MediaAccessType idType;
    private final String idValue;

    public LoadMediaPreviewEvent(String accessToken, MediaAccessType idType, String idValue) {
        this.accessToken = accessToken;
        this.idType = idType;
        this.idValue = idValue;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public MediaAccessType getIdType() {
        return idType;
    }

    public String getIdValue() {
        return idValue;
    }

}