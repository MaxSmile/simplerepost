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
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import ch.dbrgn.android.simplerepost.AuthHelper;
import ch.dbrgn.android.simplerepost.R;
import ch.dbrgn.android.simplerepost.ToastHelper;


public class RepostActivity extends ActionBarActivity {

    // Log tag
    public static final String LOG_TAG = RepostActivity.class.getName();

    // Intent parameters
    public static final String PARAM_FILENAME = "Filename";

    // Views
    private ImageView mPreviewImageView;


    /*** Lifecycle methods ***/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repost);

        // Load filename from intent
        final String filename = getIntent().getStringExtra(PARAM_FILENAME);

        // Assign views to members
        mPreviewImageView = (ImageView)findViewById(R.id.media_preview);

        // Add watermark to image
        final Bitmap bitmap = addWatermark(filename, R.raw.dark40);

        // Show image on view
        if (bitmap != null) {
            mPreviewImageView.setImageBitmap(bitmap);
        } else {
            // Something went wrong. Return to previous activity.
            ToastHelper.showGenericErrorToast(this);
            finish();
        }
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
        final String mediaPath = Environment.getExternalStorageDirectory() + "/";
        Log.d(LOG_TAG, "Media path: " + mediaPath);
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

    /**
     * Add the watermark from the specified resource file onto the
     * specified image.
     */
    private Bitmap addWatermark(String filename, int watermarkResourceFile) {
        // Read background into drawable
        BitmapDrawable background = null;
        try {
            final InputStream is = openFileInput(filename);
            background = new BitmapDrawable(getResources(), is);
            is.close();
        } catch (FileNotFoundException e) {
            ToastHelper.showShortToast(this, "Could not find downloaded image on filesystem");
            Log.e(LOG_TAG, "IOException: " + e.toString());
            return null;
        } catch (IOException e) {
            Log.w(LOG_TAG, "Could not close InputStream");
        }

        // Read watermark into Drawable
        final InputStream is = getResources().openRawResource(watermarkResourceFile);
        final BitmapDrawable watermark = new BitmapDrawable(getResources(), is);
        try {
            is.close();
        } catch (IOException e) {
            Log.w(LOG_TAG, "Could not close InputStream");
        }

        // Get dimensions
        int w = background.getBitmap().getWidth();
        int h = background.getBitmap().getHeight();

        // Create canvas for final output
        Bitmap watermarked = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(watermarked);

        // Write background onto canvas
        background.setBounds(0, 0, w, h);
        background.draw(canvas);
        background.getBitmap().recycle();

        // write watermark onto canvas
        watermark.setBounds(0, 0, w, h);
        watermark.draw(canvas);
        watermark.getBitmap().recycle();

        return watermarked;
    }

}