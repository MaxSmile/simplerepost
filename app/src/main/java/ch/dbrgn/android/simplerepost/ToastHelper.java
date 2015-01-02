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
package ch.dbrgn.android.simplerepost;

import android.content.Context;
import android.widget.Toast;

public class ToastHelper {

    public static final String LOG_TAG = ToastHelper.class.getName();

    private ToastHelper() {
        // Don't instantiate
    }

    public static void showToast(Context context, String message, int duration) {
        final Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

    public static void showShortToast(Context context, String message) {
        showToast(context, message, Toast.LENGTH_SHORT);
    }

    public static void showLongToast(Context context, String message) {
        showToast(context, message, Toast.LENGTH_LONG);
    }

    public static void showGenericErrorToast(Context context) {
        final String message = "Something went wrong, please try again.";
        showShortToast(context, message);
    }

}