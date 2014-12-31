/**
 * SimpleRepost -- A simple Instagram reposting Android app.
 * Copyright (C) 2014--2014 Danilo Bargen
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

package ch.dbrgn.android.simplerepost.events;

import retrofit.RetrofitError;

public class ApiErrorEvent {

    private final RetrofitError error;

    public ApiErrorEvent(RetrofitError error) {
        this.error = error;
    }

    public RetrofitError getError() {
        return error;
    }

    /**
     * Return a custom-built error message based on the error kind.
     */
    public String getErrorMessage() {
        switch (error.getKind()) {
            case NETWORK:
                return "An IOException occurred while communicating to the server.";
            case CONVERSION:
                return "An exception was thrown while (de)serializing a body.";
            case HTTP:
                return "A non-200 HTTP status code was received from the server.";
            case UNEXPECTED:
                return "An internal error occurred while attempting to execute a request.";
        }
        return "An unknown error occured.";
    }

}