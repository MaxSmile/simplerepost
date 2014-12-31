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
package ch.dbrgn.android.simplerepost.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.ArrayList;

import ch.dbrgn.android.simplerepost.BusProvider;
import ch.dbrgn.android.simplerepost.Config;
import ch.dbrgn.android.simplerepost.R;
import ch.dbrgn.android.simplerepost.api.ApiFactory;
import ch.dbrgn.android.simplerepost.events.ApiErrorEvent;
import ch.dbrgn.android.simplerepost.events.LoadCurrentUserEvent;
import ch.dbrgn.android.simplerepost.events.LoadedCurrentUserEvent;
import ch.dbrgn.android.simplerepost.services.CurrentUserService;
import ch.dbrgn.android.simplerepost.services.Service;


public class MainActivity extends ActionBarActivity {

    // Log tag
    public static final String LOG_TAG = MainActivity.class.getName();

    // Private members
    private String mAccessToken; // Use getAccessToken()
    private ImageView mPreviewImageView;
    private ArrayList<Service> mServices = new ArrayList<>();


    /*** Lifecycle methods ***/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Access token must be available for this activity to work
        if (getAccessToken() != null) {
            setContentView(R.layout.activity_main);
        }

        // Assign views to members
        mPreviewImageView = (ImageView)findViewById(R.id.image_preview);

        // Set up services
        final CurrentUserService currentUserService = new CurrentUserService(ApiFactory.getUserApi(), BusProvider.getInstance());
        mServices.add(currentUserService);
    }

    @Override
    public void onResume() {
        super.onResume();

        final Bus bus = BusProvider.getInstance();

        // Register services on the bus
        for (Service service : mServices) {
            bus.register(service);
        }

        // Register the current class on the bus
        bus.register(this);

        // Update user info
        bus.post(new LoadCurrentUserEvent(getAccessToken()));
    }

    @Override
    public void onPause() {
        super.onPause();

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_logout) {
            logout();
        }

        return super.onOptionsItemSelected(item);
    }


    /*** UI event handlers ***/

    public void repostToInstagram(View view) {
        final String type = "image/*";
        final String filename = "/myPhoto.jpg";
        final String mediaPath = Environment.getExternalStorageDirectory() + filename;
        final String captionText = "<< media caption >>";

        createInstagramIntent(type, mediaPath, captionText);
    }


    /*** Bus event handlers ***/

/*    @Subscribe
    public void onPreviewImageLoaded(PreviewImageLoadedEvent event) {
        Uri imageURI = event.getImageURI();
        mPreviewImageView.setImageURI(imageURI);
    }
*/

    @Subscribe
    public void onLoadedCurrentUserEvent(LoadedCurrentUserEvent event) {
        final String username = event.getUser().getFullNameOrUsername();
        final String welcomeText = getString(R.string.welcome_text_personalized, username);

        TextView welcomeTextView = (TextView)findViewById(R.id.welcome_text);
        welcomeTextView.setText(welcomeText);
    }

    @Subscribe
    public void onApiError(ApiErrorEvent event) {
        final String message = "Something went wrong, please try again.";
        final Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
        Log.e(LOG_TAG, event.getErrorMessage());
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
     * Clear access token and start login activity.
     */
    private void logout() {
        Log.i(LOG_TAG, "Logging out...");
        // Clear access token
        // TODO: Refactor this out into an auth class.
        SharedPreferences settings = getSharedPreferences(Config.SHARED_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("AccessToken");
        editor.commit();

        // Start login activity
        launchLoginActivity();
    }

    /**
     * Launch the login activity.
     */
    private void launchLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * Return the access token. Fetch it from the config if necessary.
     *
     * If no access token is available, redirect to login activity.
     */
    private String getAccessToken() {
        if (mAccessToken != null) {
            return mAccessToken;
        } else {
            // Load shared preferences
            SharedPreferences settings = getSharedPreferences(Config.SHARED_PREFS_NAME, MODE_PRIVATE);
            mAccessToken = settings.getString("AccessToken", null);

            // If login is needed, proceed to login activity
            if (mAccessToken == null) {
                Log.i(LOG_TAG, "No access token found, launch login activity...");
                launchLoginActivity();
                return null;
            } else {
                Log.i(LOG_TAG, "Access token found.");
                return mAccessToken;
            }
        }
    }
}