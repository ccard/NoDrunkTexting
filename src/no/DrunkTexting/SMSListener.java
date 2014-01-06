package no.DrunkTexting;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.SmsMessage;

import android.util.Log;
import com.google.android.mms.pdu.*;

import java.io.*;
import java.nio.ByteBuffer;


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
public class SMSListener extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle message = intent.getExtras();
            if (null != message){
                SMS_DataBase help = new SMS_DataBase(context);
                Object[] pdusObj = (Object [])message.get("pdus");
                SmsMessage[] messages = new SmsMessage[pdusObj.length];

                //geting sms info from pdu
                for (int i = 0; i < pdusObj.length; i++){
                    messages[i] = SmsMessage.createFromPdu((byte [])pdusObj[i]);
                }

                for (SmsMessage m : messages){
                    ContentValues c = new SMS_DataBase.ContentBuilder()
                                    .setAddress(m.getDisplayOriginatingAddress())
                                    .setRead("0")
                                    .setBody(m.getDisplayMessageBody())
                                    .setDate((int)(SystemClock.currentThreadTimeMillis()))
                                    .setRecieveSend("1")
                                    .build();
                    help.newReceivedSMS(c);
                    help.close();
                }
            }
        } else if (intent.getAction().equals("android.provider.Telephony.WAP_PUSH_RECEIVED")) {
            SMS_DataBase help = new SMS_DataBase(context);
            byte[] pushData = intent.getByteArrayExtra("data");
            PduParser parser = new PduParser(pushData);
            GenericPdu pdu = parser.parse();

            if (pdu.getMessageType() == PduHeaders.MESSAGE_TYPE_DELIVERY_IND)
            {
                int status = ((DeliveryInd)pdu).getStatus();
                if (status == PduHeaders.STATUS_RETRIEVED)
                {
                    String uristring = "content://mms/part";
                    int id = ByteBuffer.wrap(((DeliveryInd) pdu).getMessageId()).getInt();
                    String from = pdu.getFrom().getString();
                    String body = "";

                    Cursor c = context.getContentResolver().query(Uri.parse(uristring),null,
                            "mid="+id,null,null);

                    if (c.moveToFirst()){
                        do {
                            String partId = c.getString(c.getColumnIndex("_id"));
                            String type = c.getString(c.getColumnIndex("ct"));
                            if ("text/plain".equals(type)){
                                String data = c.getString(c.getColumnIndex("_data"));
                                if (null != data){
                                    body = getMmsText(partId,context);
                                } else {
                                    body = c.getString(c.getColumnIndex("text"));
                                }
                            } else {
                                //TODO create method for getting images
                            }
                        } while (c.moveToNext());
                    }

                    ContentValues cv = new SMS_DataBase.ContentBuilder()
                                        .setAddress(from)
                                        .setRead("0")
                                        .setBody(body)
                                        .setDate((int) ((DeliveryInd) pdu).getDate())
                                        .setRecieveSend("1")
                                        .build();

                    help.newReceivedSMS(cv);
                    help.close();
                }
            }
        }
    }

    private String getMmsText(String id, Context context){
        Uri partURI = Uri.parse("content://mms/part/"+id);
        InputStream in = null;
        StringBuilder body = new StringBuilder();

        try {
            in = context.getContentResolver().openInputStream(partURI);
            if (null != in) {
                BufferedReader read = new BufferedReader(new InputStreamReader(in,"UTF-8"));
                String temp;
                while ((temp = read.readLine()) != null) body.append(temp);
            }
        } catch (FileNotFoundException e) {
            Log.e("MMS PARSER",e.getStackTrace().toString());
        } catch (UnsupportedEncodingException e) {
            Log.e("MMS PARSER",e.getStackTrace().toString());
        }  catch (IOException e) {
            Log.e("MMS PARSER",e.getStackTrace().toString());
        }finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e("MMS PARSER",e.getStackTrace().toString());
                }
            }
        }
        return body.toString();
    }


}
