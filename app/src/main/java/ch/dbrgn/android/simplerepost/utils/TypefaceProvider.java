/**
 * SimpleRepost -- A simple Instagram reposting Android app.
 * Copyright (C) 2014-2015 Danilo Bargen
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
package ch.dbrgn.android.simplerepost.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;


public class TypefaceProvider {

    // Configuration
    private final static String FONTS_PATH = "fonts/";

    // Cache map
    private static Map<Font, Typeface> CACHE = new HashMap<Font, Typeface>();

    // Font registry enum
    public static enum Font {
        MONTSERRAT_REGULAR ("Montserrat-Regular.ttf"),
        MONTSERRAT_BOLD ("Montserrat-Bold.ttf");

        private final String filename;

        Font(String filename) {
            this.filename = filename;
        }

        public String getFilename() {
            return filename;
        }
    }

    private TypefaceProvider() {
        // Don't instantiate
    }

    public static Typeface getTypeface(Context context, Font font) {
        Typeface typeface = CACHE.get(font);
        if (typeface == null) {
            final AssetManager manager = context.getAssets();
            final String path = FONTS_PATH + font.getFilename();
            typeface = Typeface.createFromAsset(manager, path);
            CACHE.put(font, typeface);
        }
        return typeface;
    }

}