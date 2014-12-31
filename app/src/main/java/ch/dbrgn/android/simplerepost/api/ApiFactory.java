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

package ch.dbrgn.android.simplerepost.api;

/**
 * A static class that creates and returns API class instances via the static `getXxxApi()` methods.
 */
public class ApiFactory {

    private ApiFactory() {
        // No instances
    }

    public static UserApi getUserApi() {
        return RestAdapterFactory.build().create(UserApi.class);
    }

}
