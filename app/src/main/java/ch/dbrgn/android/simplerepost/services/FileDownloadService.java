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

package ch.dbrgn.android.simplerepost.services;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;

import ch.dbrgn.android.simplerepost.api.MediaApi;
import ch.dbrgn.android.simplerepost.events.ApiErrorEvent;
import ch.dbrgn.android.simplerepost.events.DownloadBitmapEvent;
import ch.dbrgn.android.simplerepost.events.DownloadErrorEvent;
import ch.dbrgn.android.simplerepost.events.DownloadedBitmapEvent;
import ch.dbrgn.android.simplerepost.events.LoadMediaPreviewEvent;
import ch.dbrgn.android.simplerepost.events.LoadedMediaPreviewEvent;
import ch.dbrgn.android.simplerepost.models.ImageBitmap;
import ch.dbrgn.android.simplerepost.models.MediaResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;



public class FileDownloadService implements Service {

    public static final String LOG_TAG = FileDownloadService.class.getName();

    private final Bus mBus;

    public FileDownloadService(Bus bus) {
        mBus = bus;
    }

    @Subscribe
    public void onDownloadBitmap(DownloadBitmapEvent event) {
        new DownloadBitmapTask().execute(event.getUrl());
    }

    /**
     * Async task that downloads the bitmap in a background thread.
     */
    private class DownloadBitmapTask extends AsyncTask<String, Void, ImageBitmap> {

        @Override
        protected ImageBitmap doInBackground(String... params) {
            final String url = params[0];
            return downloadBitmap(url);

        }

        @Override
        protected void onPostExecute(ImageBitmap imageBitmap) {
            super.onPostExecute(imageBitmap);

            if (imageBitmap != null) {
                mBus.post(new DownloadedBitmapEvent(imageBitmap));
            }
        }

    }

    /**
     * Function that downloads the url and returns a Bitmap instance.
     *
     * If an error occurs, a DownloadErrorEvent is posted into the
     * bus and null is returned.
     *
     * You should probably run this code in a background thread!
     */
    private ImageBitmap downloadBitmap(String url) {
        final DefaultHttpClient client = new DefaultHttpClient();
        Bitmap bitmap = null;

        // Parse filename out of URL
        final String[] parts = url.split("/");
        final String filename = parts[parts.length - 1];

        final HttpGet getRequest = new HttpGet(url);
        try {
            // Do the request
            HttpResponse response = client.execute(getRequest);

            // Check status code
            final int statusCode = response.getStatusLine().getStatusCode();

            // Handle error case
            if (statusCode != HttpStatus.SC_OK) {
                final String message = "Error " + statusCode + " while retrieving bitmap from " + url;
                Log.w(LOG_TAG, message);
                mBus.post(new DownloadErrorEvent(message));
                return null;
            }

            // Get the data
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    // Get contents from the stream
                    inputStream = entity.getContent();

                    // Decode stream data back into image Bitmap that android understands
                    bitmap = BitmapFactory.decodeStream(inputStream);
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (Exception e) {
            // You Could provide a more explicit error message for IOException
            getRequest.abort();
            final String message = "Something went wrong while retrieving bitmap from " + url + e.toString();
            Log.e(LOG_TAG, message);
            mBus.post(new DownloadErrorEvent(message));
        }

        return new ImageBitmap(bitmap, filename);
    }

}