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
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import ch.dbrgn.android.simplerepost.activities.LoginActivity;

/**
 * A static singleton class that handles the access token.
 *
 * Use `AccessTokenProvider.getToken(Context context)` to get back
 * the access token string.
 */
public class AuthHelper {

    public static final String LOG_TAG = AuthHelper.class.getName();
    private static String mAccessToken;
    private static final String SHARED_PREF_NAME = "AccessToken";

    private AuthHelper() {
        // Don't instantiate
    }

    /**
     * Retrieve the access token.
     * If no access token is found, the login activity is launched.
     */
    public static String getToken(Context context) {
        if (mAccessToken != null) {
            return mAccessToken;
        } else {
            // Load shared preferences
            SharedPreferences settings = context.getSharedPreferences(
                    Config.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
            mAccessToken = settings.getString(SHARED_PREF_NAME, null);

            // If login is needed, proceed to login activity
            if (mAccessToken == null) {
                Log.i(LOG_TAG, "No access token found, launch login activity...");
                launchLoginActivity(context);
                return null;
            } else {
                Log.i(LOG_TAG, "Access token found.");
                return mAccessToken;
            }
        }
    }

    /**
     * Clear the access token from the shared preferences.
     */
    public static void clearToken(Context context) {
        SharedPreferences settings = context.getSharedPreferences(
                Config.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(SHARED_PREF_NAME);
        editor.commit();
    }

    /**
     * Launch the login activity.
     */
    public static void launchLoginActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    /**
     * Clear the access token and launch login activity.
     */
    public static void logout(Context context) {
        clearToken(context);
        launchLoginActivity(context);
    }

}