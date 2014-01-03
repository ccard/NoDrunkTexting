package no.DrunkTexting;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


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
public class SMS_DataBase extends SQLiteOpenHelper {

    private final static int SCHEMA_VERSION = 1;
    private final static String DATABASE_NAME = "DrunkTexting.db";


    public static String BODY= "body";
    public static String DATE = "date";
    public static String RECEIVED_SENT= "received_sent";
    public static String CONTACT_INFO= "contact_info";
    public static String ADDRESS= "address";
    public static String READ= "read";
    public static String ID= "_ID";

    private final static String CONVERSATION = "CREATE TABLE conversations (" +
            ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
            BODY+" TEXT, " +
            DATE+" INTEGER" +
            RECEIVED_SENT+" INTEGER, " +
            CONTACT_INFO+" REFERENCES conversation_info (_id) " +
            "NOT DEFERRABLE ON CONFLICT FAIL);";
    private final static String CONTACT = "CREATE TABLE conversation_info (" +
            ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ADDRESS+" TEXT UNIQUE ON CONFLICT FAIL, " +
            READ+" INTEGER);";

    private final static String INDEX = "CREATE INDEX converse " +
            "ON conversations("+CONTACT_INFO+");";

    public SMS_DataBase(Context context) {
        super(context,DATABASE_NAME,null,SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CONVERSATION);
        db.execSQL(CONTACT);
        db.execSQL(INDEX);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO change if table schema are updated
    }

    public Cursor getConversations(){

        String sql = "SELECT conversation_info."+ID+" AS "+ID+", " +
                "conversation_info."+ADDRESS+" AS "+ADDRESS+" "+
                ", conversation_info."+READ+" AS "+READ+"," +
                " conversations."+BODY+" AS "+BODY+" "+
                "FROM conversation_info,conversations " +
                "WHERE "+ID+"=conversations."+ID+" " +
                "GROUP BY "+ID+" ORDER BY "+READ+" DESC;";
        return getReadableDatabase().rawQuery(sql, null);
    }

    public Cursor getAConversation(int id){
        String sql = "SELECT * FROM conversations " +
                "WHERE "+CONTACT_INFO+" = " + id +" ORDER " +
                "BY "+DATE+" ASC;";
        return getReadableDatabase().rawQuery(sql,null);
    }

    public void newReceivedSMS(ContentValues vals){

        int id = getConversationId((String)(vals.get(ADDRESS)));

        if (id < 0) {
            id = newConversation((String)(vals.get(ADDRESS)));
            if (id < 0) return;
        } else {
            if (!updateReadStatus(id,vals.getAsInteger(READ))) return;
        }

        ContentValues cv = new ContentValues();
        cv.put(BODY,vals.getAsString(BODY));
        cv.put(DATE,vals.getAsInteger(DATE));
        cv.put(RECEIVED_SENT,vals.getAsInteger(RECEIVED_SENT));
        cv.put(CONTACT_INFO,id);

        getWritableDatabase().insert("conversations",null,cv);
    }

    private int newConversation(String address){

        ContentValues cv = new ContentValues();
        cv.put(ADDRESS,address);
        cv.put(READ,0);

        return (int)getWritableDatabase().insert("conversation_info",
                ADDRESS,cv);
    }

    private int getConversationId(String address){

        String sql = "SELECT "+ID+" FROM conversation_info " +
                "WHERE "+ADDRESS+" = " + address + ";";
        Cursor c = getReadableDatabase().rawQuery(sql,null);
        int id;
        if (c.getCount() == 1) c.moveToFirst();

        id = (c.getCount() == 1 ? c.getInt(0) : -1);

        c.close();
        return id;
    }

    public boolean updateReadStatus(int id,int read){
        String[] args = {Integer.toString(id)};
        ContentValues cv = new ContentValues();
        cv.put(READ,read);
        return (getWritableDatabase().update("conversation_info",cv,
                ID+"=?",args) == 1);
    }

    public static class ContentBuilder {
        private ContentValues cv;

        public ContentBuilder() {
            cv = new ContentValues();
        }

        public void setAddress(String address){
            cv.put(ADDRESS,address);
        }

        public void setBody(String body){
            cv.put(BODY,body);
        }

        public void setRead(String read){
            cv.put(READ,(read == "1" ? 1:0));
        }

        public void setDate(String date){
            SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd kk:mm:ss.sss");
            Date d = null;
            try {
                d = sdf.parse(date);
            } catch (ParseException e) {
                Log.e("#Date parse#", e.getStackTrace().toString());
            }

            if (null != d){
                cv.put(DATE,d.getTime());
            } else {
                cv.put(DATE,System.currentTimeMillis());
            }
        }

        public void setRecieveSend(String recv_sent){
            cv.put(RECEIVED_SENT,
                    Integer.parseInt(recv_sent));
        }

        public ContentValues build(){
            return cv;
        }
    }
}
