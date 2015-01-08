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
package ch.dbrgn.android.simplerepost;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Config {
    public static final String IG_CLIENT_ID = "e3badecc1d2f4f8e97a00995f92e21fb";
    public static final String IG_REDIRECT_URI = "https://dbrgn.ch/simplerepost/success/";
    public static final String IG_REDIRECT_URI_ENCODED = "https%3A%2F%2Fdbrgn.ch%2Fsimplerepost%2Fsuccess%2F";
    public static final String IG_API_URL = "https://api.instagram.com/v1";

    public static final String SHARED_PREFS_NAME = "SimpleRepostPreferences";

    public static final String PICTURES_DIRECTORY_NAME = "SimpleRepost";

    public static final Map<String, Integer> REPOST_STYLES;
    static {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("Dark", R.raw.visitrapperswil_dark);
        map.put("Dark Filled", R.raw.visitrapperswil_dark_filled);
        map.put("Light", R.raw.visitrapperswil_light);
        map.put("Light Filled", R.raw.visitrapperswil_light_filled);
        REPOST_STYLES = Collections.unmodifiableMap(map);
    }
}