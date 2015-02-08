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

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.dbrgn.android.simplerepost.models.User;
import ch.dbrgn.android.simplerepost.utils.AuthHelper;
import ch.dbrgn.android.simplerepost.utils.BusProvider;
import ch.dbrgn.android.simplerepost.R;
import ch.dbrgn.android.simplerepost.utils.TextValidator;
import ch.dbrgn.android.simplerepost.utils.ToastHelper;
import ch.dbrgn.android.simplerepost.api.ApiFactory;
import ch.dbrgn.android.simplerepost.api.MediaAccessType;
import ch.dbrgn.android.simplerepost.events.ApiErrorEvent;
import ch.dbrgn.android.simplerepost.events.DownloadBitmapEvent;
import ch.dbrgn.android.simplerepost.events.DownloadErrorEvent;
import ch.dbrgn.android.simplerepost.events.DownloadedBitmapEvent;
import ch.dbrgn.android.simplerepost.events.LoadCurrentUserEvent;
import ch.dbrgn.android.simplerepost.events.LoadMediaEvent;
import ch.dbrgn.android.simplerepost.events.LoadedCurrentUserEvent;
import ch.dbrgn.android.simplerepost.events.LoadedMediaEvent;
import ch.dbrgn.android.simplerepost.models.Media;
import ch.dbrgn.android.simplerepost.services.CurrentUserService;
import ch.dbrgn.android.simplerepost.services.FileDownloadService;
import ch.dbrgn.android.simplerepost.services.MediaService;
import ch.dbrgn.android.simplerepost.services.Service;


public class MainActivity extends ActionBarActivity {

    // Log tag
    public static final String LOG_TAG = MainActivity.class.getName();

    // Private members
    private ArrayList<Service> mServices = new ArrayList<>();
    private ProgressDialog mPreviewProgressDialog;
    private Media mMedia;
    private User mCurrentUser;

    // UI views
    private EditText mUrlInputView;
    private Button mPreviewButton;


    /*** Lifecycle methods ***/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Access token must be available for this activity to work
        // This is needed because this is the main activity
        if (AuthHelper.getToken(this) == null) {
            return;
        } else {
            setContentView(R.layout.activity_main);
        }

        // Set up services
        mServices.add(new CurrentUserService(ApiFactory.getUserApi(), BusProvider.getInstance()));
        mServices.add(new MediaService(ApiFactory.getMediaApi(), BusProvider.getInstance()));
        mServices.add(new FileDownloadService(BusProvider.getInstance()));

        // Initialize UI views
        mUrlInputView = (EditText)findViewById(R.id.url_input);
        mPreviewButton = (Button)findViewById(R.id.preview_button);

        // Add validator to EditText box
        mUrlInputView.addTextChangedListener(new TextValidator(mUrlInputView) {
            @Override
            public void validate(TextView textView, String text) {
                if (text.isEmpty()) {
                    mPreviewButton.setEnabled(false);
                    mUrlInputView.setError(null);
                } else {
                    if (parseShortcodeUrl(text) == null) {
                        // Invalid input
                        mUrlInputView.setError("This must be an Instagram URL in the form " +
                                "\"https://instagram.com/p/ABC123\".");
                        mPreviewButton.setEnabled(false);
                    } else {
                        mUrlInputView.setError(null);
                        mPreviewButton.setEnabled(true);
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // Access token must be available for this activity to work
        // This is needed because this is the main activity.
        // TODO: This code is duplicated (see onCreate). Clean up.
        if (AuthHelper.getToken(this) == null) {
            return;
        }

        final Bus bus = BusProvider.getInstance();

        // Clear input box
        mUrlInputView.setText("");

        // Register services on the bus
        for (Service service : mServices) {
            bus.register(service);
        }

        // Register the current class on the bus
        bus.register(this);

        // Update user info
        bus.post(new LoadCurrentUserEvent(AuthHelper.getToken(this)));
    }

    @Override
    public void onPause() {
        super.onPause();

        // Access token must be available for this activity to work
        // This is needed because this is the main activity.
        // TODO: This code is duplicated (see onCreate). Clean up.
        if (AuthHelper.getToken(this) == null) {
            return;
        }

        final Bus bus = BusProvider.getInstance();

        // Unregister services on the bus
        for (Service service : mServices) {
            bus.unregister(service);
        }

        // Unregister the current class on the bus
        bus.unregister(this);
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

        if (id == R.id.spinner_style) {
            return true;
        } else if (id == R.id.action_logout) {
            Log.i(LOG_TAG, "Logging out...");
            AuthHelper.logout(this);
        } else if (id == R.id.action_settings) {
            Log.i(LOG_TAG, "Launching settings activity");
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    /*** UI event handlers ***/

    public void eventPreview(View view) {
        // TODO: Input validation

        final String text = mUrlInputView.getText().toString();

        final String shortcode = parseShortcodeUrl(text);
        if (shortcode == null || shortcode.equals("")) {
            // TOOD: handle, highlight input field with validation error
            return;
        }

        BusProvider.getInstance().post(
                new LoadMediaEvent(
                        AuthHelper.getToken(this), MediaAccessType.SHORTCODE, shortcode
                )
        );

        // Show progress dialog
        mPreviewProgressDialog = new ProgressDialog(this);
        mPreviewProgressDialog.setMessage(getString(R.string.loading_preview));
        mPreviewProgressDialog.show();
    }


    /*** Bus event handlers ***/

    @Subscribe
    public void onLoadedCurrentUser(LoadedCurrentUserEvent event) {
        mCurrentUser = event.getUser();

        final String username = mCurrentUser.getFullNameOrUsername();
        final String welcomeText = getString(R.string.welcome_text_personalized, username);

        TextView welcomeTextView = (TextView)findViewById(R.id.welcome_text);
        welcomeTextView.setText(welcomeText);
    }

    @Subscribe
    public void onLoadedMedia(LoadedMediaEvent event) {
        mMedia = event.getMedia();

        // Verify media information was sent
        if (mMedia == null) {
            ToastHelper.showShortToast(this, "Could not download media information from Instagram");
            Log.e(LOG_TAG, "Media is null");

            // Hide progress dialog
            mPreviewProgressDialog.dismiss();

            return;
        } else if (mMedia.getType().equals("video")) {
            ToastHelper.showLongToast(this, "Reposting videos is currently not supported");
            Log.w(LOG_TAG, "User tried to repost a video");

            // Hide progress dialog
            mPreviewProgressDialog.dismiss();

            return;
        }

        String imageUrl = mMedia.getImages().getStandardResolution().getUrl();
        Log.d(LOG_TAG, "Image URL is " + imageUrl);

        BusProvider.getInstance().post(new DownloadBitmapEvent(imageUrl));
    }

    @Subscribe
    public void onDownloadedBitmap(DownloadedBitmapEvent event) {
        // Hide progress dialog
        mPreviewProgressDialog.dismiss();

        // Verify bitmap was sent
        final Bitmap bitmap = event.getBitmap();
        if (bitmap == null) {
            ToastHelper.showShortToast(this, "Could not download media from Instagram");
            Log.e(LOG_TAG, "Bitmap is null");
            return;
        }

        // Save bitmap to filesystem
        final String filename = event.getFilename() + ".original.png";
        FileOutputStream stream;
        try {
            // Open file output stream
            stream = openFileOutput(filename, MODE_PRIVATE);

            // Compress into file
            event.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);

            // Cleanup
            stream.close();
            event.getBitmap().recycle();
        } catch (IOException e) {
            ToastHelper.showShortToast(this, "Could not save the image to the filesystem");
            Log.e(LOG_TAG, "IOException: " + e.toString());
            return;
        }

        // Launch repost activity
        Log.d(LOG_TAG, "Starting repost activity...");
        Intent intent = new Intent(this, RepostActivity.class);
        intent.putExtra(RepostActivity.PARAM_FILENAME, filename);
        intent.putExtra(RepostActivity.PARAM_MEDIA, mMedia);
        intent.putExtra(RepostActivity.PARAM_USER, mCurrentUser);
        startActivity(intent);
    }

    @Subscribe
    public void onApiError(ApiErrorEvent event) {
        ToastHelper.showGenericErrorToast(this);
        Log.e(LOG_TAG, "ApiErrorEvent: " + event.getErrorMessage());

        // Hide progress dialog
        mPreviewProgressDialog.dismiss();
    }

    @Subscribe
    public void onDownloadError(DownloadErrorEvent event) {
        ToastHelper.showGenericErrorToast(this);
        Log.e(LOG_TAG, "DownloadErrorEvent: " + event.getErrorMessage());

        // Hide progress dialog
        mPreviewProgressDialog.dismiss();
    }


    /*** Private helper methods ***/

    /**
     * Parse the shortcode out of an Instagram share URL.
     */
    private String parseShortcodeUrl(String text) {
        Pattern pattern = Pattern.compile("^(?:https?://)?instagram.com/p/([a-zA-Z0-9\\-_]+)/?.*");
        Matcher matcher = pattern.matcher(text);
        if (matcher.matches()) {
            Log.d(LOG_TAG, "Shortcode is " +  matcher.group(1));
            return matcher.group(1);
        }
        return null;
    }

}