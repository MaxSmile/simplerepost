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
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import ch.dbrgn.android.simplerepost.Config;
import ch.dbrgn.android.simplerepost.R;
import ch.dbrgn.android.simplerepost.models.Media;
import ch.dbrgn.android.simplerepost.models.User;
import ch.dbrgn.android.simplerepost.utils.AuthHelper;
import ch.dbrgn.android.simplerepost.utils.ToastHelper;


public class RepostActivity extends ActionBarActivity {

    // Log tag
    public static final String LOG_TAG = RepostActivity.class.getName();

    // Intent parameters
    public static final String PARAM_FILENAME = "Filename";
    public static final String PARAM_MEDIA = "Media";
    public static final String PARAM_USER = "User";

    // Shared preference keys
    private static final String PREF_WATERMARK_STYLE = "WatermarkStyle";

    // Private members
    private Media mMedia;
    private String mFilename;
    private Bitmap mWatermarkedBitmap;
    private User mCurrentUser;


    /*** Lifecycle methods ***/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repost);

        // Load intent parameters
        final Intent intent = getIntent();
        mFilename = intent.getStringExtra(PARAM_FILENAME);
        mMedia = intent.getParcelableExtra(PARAM_MEDIA);
        mCurrentUser = intent.getParcelableExtra(PARAM_USER);
    }


    /*** Menu ***/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_repost, menu);

        // Get spinner
        MenuItem item = menu.findItem(R.id.spinner_style);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);

        // Prepare data
        String[] spinnerArray = new String[Config.REPOST_STYLES.size()];
        Iterator<String> styleIterator = Config.REPOST_STYLES.keySet().iterator();
        for (int i = 0; i < Config.REPOST_STYLES.size(); i++) {
            spinnerArray[i] = styleIterator.next();
        }

        // Set adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        spinner.setAdapter(adapter);

        // Load style from shared preferences
        int initialStyle = loadWatermarkStyle(0);

        // Validate stored style to prevent array IndexOutOfBoundsException
        if (initialStyle < 0 || initialStyle >= Config.REPOST_STYLES.size()) {
            Log.i(LOG_TAG, "Invalid stored watermark style. Setting it to \"0\".");
            initialStyle = 0;
        }

        // Set default style
        spinner.setSelection(initialStyle);

        // Handle clicks
        spinner.setOnItemSelectedListener(onWatermarkStyleSelected());

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.spinner_style) {
            return true;
        } else if (id == R.id.action_logout) {
            Log.i(LOG_TAG, "Logging out...");
            AuthHelper.logout(this);
        }

        return super.onOptionsItemSelected(item);
    }

    private AdapterView.OnItemSelectedListener onWatermarkStyleSelected() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Fix text color
                CheckedTextView textView = (CheckedTextView)view;
                textView.setTextColor(Color.WHITE);

                // Update watermark
                final int style = (int) Config.REPOST_STYLES.values().toArray()[position];
                updateWatermark(style);

                // Store choice in shared preferences
                storeWatermarkStyle(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) { } // Ignore
        };
    }

    /**
     * Store watermark style in shared preferences file.
     */
    private void storeWatermarkStyle(int position) {
        SharedPreferences settings = getSharedPreferences(Config.SHARED_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(PREF_WATERMARK_STYLE, position);
        Log.i(LOG_TAG, "Storing watermark style \"" + position + "\" to shared preferences");
        editor.apply();
    }

    /**
     * Load watermark style from shared preferences.
     *
     * If no stored style can be found, the specifieddefault value will be returned.
     *
     * If stored style exists but cannot be read as integer, the key will be cleared
     * and the default value will be returned.
     */
    private int loadWatermarkStyle(int defaultValue) {
        SharedPreferences settings = getSharedPreferences(Config.SHARED_PREFS_NAME, MODE_PRIVATE);
        try {
            final int loadedStyle = settings.getInt(PREF_WATERMARK_STYLE, defaultValue);
            Log.i(LOG_TAG, "Loaded watermark style \"" + loadedStyle + "\" from shared preferences");
            return loadedStyle;
        } catch (ClassCastException e) {
            Log.w(LOG_TAG, "Could not load watermark style as int from shared preferences");
            SharedPreferences.Editor editor = settings.edit();
            editor.remove(PREF_WATERMARK_STYLE);
            editor.apply();
            Log.i(LOG_TAG, "Cleared waterma1rk style from shared preferences");
            return defaultValue;
        }
    }


    /*** UI event handlers ***/

    @SuppressWarnings({"StringBufferReplaceableByString", "StringConcatenationInsideStringBufferAppend"})
    public void eventRepost(View view) {
        // Check whether bitmap is available
        if (mWatermarkedBitmap == null) {
            Log.e(LOG_TAG, "No bitmap is available.");
            ToastHelper.showGenericErrorToast(this);
            return;
        }

        // Save to external storage
        File file = saveToExternalStorage(mMedia.getId(), mWatermarkedBitmap);
        if (file == null) {
            // No logging / notification here, that should be handled by the save method.
            return;
        }

        // Prepare text
        StringBuilder builder = new StringBuilder();
        final String clap = "\uD83D\uDC4F";
        builder.append("Congrats!\n.\n");
        builder.append("★ Visit Rapperswil Feature ★\n");
        builder.append("Picture by - @" + mMedia.getUser().getUsername() + "\n");
        builder.append("Selected by - @" + mCurrentUser.getUsername() + "\n.\n");
        builder.append("Show ❤" + clap + " to the original post as well, thanks.\n.\n");
        builder.append("For a chance to get featured follow @visitrapperswil ");
        builder.append("and tag #rapperswil or #visitrapperswil.");

        // Prepare intent
        final String type = "image/*";
        final String mediaPath = file.getPath();
        final String captionText = builder.toString();

        // Create intent
        createInstagramIntent(type, mediaPath, captionText);
    }


    /*** Private helper methods ***/

    private void updateWatermark(int repostStyle) {
        // Add watermark to image
        mWatermarkedBitmap = addWatermark(mFilename, repostStyle);

        // Show image on view
        if (mWatermarkedBitmap != null) {
            ImageView mPreviewImageView = (ImageView)findViewById(R.id.media_preview);
            mPreviewImageView.setImageBitmap(mWatermarkedBitmap);
        } else {
            // Something went wrong. Return to previous activity.
            ToastHelper.showGenericErrorToast(this);
            finish();
        }
    }

    /**
     * Save the specified bitmap to the external storage. The
     * fileIdentifier parameter should be unique among all instagram
     * posts. If the file can be written, a File object pointing
     * to it will be returned.
     */
    private File saveToExternalStorage(String fileIdentifier, Bitmap bitmap) {
        // Check whether external storage is writeable
        final String externalStorageState = Environment.getExternalStorageState();
        boolean isWritable = externalStorageState.equals(Environment.MEDIA_MOUNTED);
        if (!isWritable) {
            ToastHelper.showShortToast(this, "External storage is not writeable.");
            return null;
        }

        // Create directory
        final File pubDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        final File directory = new File(pubDirectory, Config.PICTURES_DIRECTORY_NAME);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                ToastHelper.showShortToast(this, "Could not create storage directory.");
                return null;
            }
        }

        // Write file
        final File file = new File(directory, fileIdentifier + ".png");
        if (file.exists()) {
            Log.w(LOG_TAG, "File " + file.toString() + " already exists");
            // This can only happen if the file has been reposted before.
            // In that case, overwrite.
            if (!file.delete()) {
                Log.e(LOG_TAG, "File " + file.toString() + " could not be deleted.");
                ToastHelper.showGenericErrorToast(this);
            } else {
                Log.i(LOG_TAG, "Deleted file " + file);
            }
        }
        try {
            FileOutputStream os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, os);
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
            ToastHelper.showGenericErrorToast(this);
        }

        return file;
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

        // Write watermark onto canvas
        watermark.setBounds(0, 0, w, h);
        watermark.draw(canvas);
        watermark.getBitmap().recycle();

        return watermarked;
    }

}