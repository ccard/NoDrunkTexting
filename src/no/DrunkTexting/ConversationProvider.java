package no.DrunkTexting;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import java.net.URI;

/**
 * {description}
 * Copyright (C) 2014 Christopher Tyler Card
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * Created by Ch on 1/3/14.
 */
public class ConversationProvider extends ContentProvider{

    private static final UriMatcher validURI = new UriMatcher(UriMatcher.NO_MATCH);

    public final static Uri CONVERSATIONS_URI = Uri.parse("content://com.no.DrunkTexting.provider/conversations");
    public final static Uri CONVERSATION_URI = Uri.parse("content://com.no.DrunkTexting.provider/conversation");
    static{
        validURI.addURI("com.no.DrunkTexting.provider", "conversations", 1);
        validURI.addURI("com.no.DrunkTexting.provider","conversation",2);
    }
    private SMS_DataBase db_helper;

    @Override
    public boolean onCreate() {
        db_helper = new SMS_DataBase(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor c = null;
        switch (validURI.match(uri)){
            case 1:
                c = db_helper.getConversations();
                break;
            case 2:
                c = db_helper.getAConversation(Integer.parseInt(selection));
                break;
            default:
                break;
        }

        c.setNotificationUri(getContext().getContentResolver(),uri);

        return c;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        getContext().getContentResolver().notifyChange(uri,null);
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
