package no.DrunkTexting;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.SmsMessage;

import com.google.android.mms.pdu.*;
import com.google.android.mms.util.*;



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
                }
            }
        } else if (intent.getAction().equals("android.provider.Telephony.WAP_PUSH_RECEIVED")) {
            byte[] pushData = intent.getByteArrayExtra("data");
            PduParser parser = new PduParser();
            //TODO continue to work on the tutorial http://forum.xda-developers.com/showthread.php?t=2222703
        }
    }

    public class MMSInfo{
        public String Name = "";
        public String MimeType = "";
        public byte[] Data;
    }
}
