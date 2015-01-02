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

package ch.dbrgn.android.simplerepost.events;

import ch.dbrgn.android.simplerepost.models.Images;
import ch.dbrgn.android.simplerepost.models.Media;
import ch.dbrgn.android.simplerepost.models.User;

public class LoadedMediaEvent {

    private final Media media;

    public LoadedMediaEvent(Media media) {
        this.media = media;
    }

    public Media getMedia() {
        return media;
    }

    public Images getImages() {
        return media.getImages();
    }

    public User getUser() {
        return media.getUser();
    }

}