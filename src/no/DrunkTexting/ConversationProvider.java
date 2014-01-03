package no.DrunkTexting;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import java.net.URI;

/**
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
