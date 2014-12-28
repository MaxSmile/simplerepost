/**
 * SimpleRepost -- A simple Instagram reposting Android app.
 * Copyright (C) 2014 Danilo Bargen
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

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;


public class MainActivity extends ActionBarActivity {

    // Log tag
    public static final String LOG_TAG = MainActivity.class.getName();
    private String mAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load shared preferences
        SharedPreferences settings = getSharedPreferences(Config.SHARED_PREFS_NAME, MODE_PRIVATE);
        this.mAccessToken = settings.getString("AccessToken", null);

        // If login is needed, proceed to login activity
        if (mAccessToken == null) {
            Log.i(LOG_TAG, "No access token found, launch login activity...");
            this.launchLoginActivity();
        } else {
            Log.i(LOG_TAG, "Access token found.");
            setContentView(R.layout.activity_main);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_logout) {
            this.logout();
        }

        return super.onOptionsItemSelected(item);
    }

    public void repostToInstagram(View view) {
        final String type = "image/*";
        final String filename = "/myPhoto.jpg";
        final String mediaPath = Environment.getExternalStorageDirectory() + filename;
        final String captionText = "<< media caption >>";

        this.createInstagramIntent(type, mediaPath, captionText);
    }

    private void createInstagramIntent(String type, String mediaPath, String caption) {
        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        // Set the MIME type
        share.setType(type);

        // Create the URI from the media
        File media = new File(mediaPath);
        Uri uri = Uri.fromFile(media);

        // Add the URI and the caption to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.putExtra(Intent.EXTRA_TEXT, caption);

        // Broadcast the Intent.
        startActivity(Intent.createChooser(share, "Share to"));
    }

    private void logout() {
        Log.i(LOG_TAG, "Logging out...");
        // Clear access token
        // TODO: Refactor this out into an auth class.
        SharedPreferences settings = getSharedPreferences(Config.SHARED_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("AccessToken");
        editor.commit();

        // Start login activity
        this.launchLoginActivity();
    }

    private void launchLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}
