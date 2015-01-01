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
package ch.dbrgn.android.simplerepost.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import ch.dbrgn.android.simplerepost.AuthHelper;
import ch.dbrgn.android.simplerepost.R;


public class RepostActivity extends ActionBarActivity {

    // Log tag
    public static final String LOG_TAG = RepostActivity.class.getName();

    // Intent parameters
    public static final String PARAM_FILENAME = "Filename";

    // Private members
    private Bitmap mBitmap = null;
    private ImageView mPreviewImageView;


    /*** Lifecycle methods ***/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repost);

        // Assign views to members
        mPreviewImageView = (ImageView)findViewById(R.id.media_preview);

        // Load bitmap into preview view
        final String filename = getIntent().getStringExtra(PARAM_FILENAME);
        try {
            FileInputStream stream = openFileInput(filename);
            mBitmap = BitmapFactory.decodeStream(stream);
            stream.close();
        } catch (IOException e) {
            final String message = "Could not find image on filesystem";
            final Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
            toast.show();
            Log.e(LOG_TAG, "IOException: " + e.toString());
            return;
        }

        Log.d(LOG_TAG, "Height: " + mBitmap.getHeight() + ", width: " + mBitmap.getWidth());
        mPreviewImageView.setImageBitmap(mBitmap);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    /*** Menu ***/

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
            Log.i(LOG_TAG, "Logging out...");
            AuthHelper.logout(this);
        }

        return super.onOptionsItemSelected(item);
    }


    /*** UI event handlers ***/

    public void eventRepost(View view) {
        final String type = "image/*";
        final String filename = "/myPhoto.jpg";
        final String mediaPath = Environment.getExternalStorageDirectory() + filename;
        final String captionText = "<< media caption >>";

        createInstagramIntent(type, mediaPath, captionText);
    }


    /*** Private helper methods ***/

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

}