package com.booking.triplogger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "TripLogger.db";

    public static final String USERSTABLE_NAME = "usersTable";
    public static final String TRIPTABLE_NAME = "tripTable";

    public static final String USERSCOLUMN_ID = "id";
    public static final String USERSCOLUMN_NAME = "name";
    public static final String USERSCOLUMN_USERID = "userid";
    public static final String USERSCOLUMN_EMAIL = "email";
    public static final String USERSCOLUMN_GENDER = "gender";
    public static final String USERSCOLUMN_COMMENT = "comment";

    public static final String TRIPSCOLUMN_TRIP_ID = "id";
    public static final String TRIPSCOLUMN_TITLE= "title";
    public static final String TRIPSCOLUMN_DATE = "date";
    public static final String TRIPSCOLUMN_TRIPTYPE = "triptype";
    public static final String TRIPSCOLUMN_DESTINATION = "destination";
    public static final String TRIPSCOLUMN_DURATION = "duration";
    public static final String TRIPSCOLUMN_COMMENTTRIP = "commenttrip";
    public static final String TRIPSCOLUMN_PHOTO = "photo";
    public static final String TRIPSCOLUMN_LATITUDE = "latitude";
    public static final String TRIPSCOLUMN_LONGITUDE = "longitude";
    private HashMap hp;

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " +USERSTABLE_NAME+
                        "(id integer primary key, name text,email text, gender text,comment text,userid text)"
        );
        db.execSQL(
                "create table " +TRIPTABLE_NAME+
                        "(id integer primary key, title text,date text, triptype text,destination text" +
                        ",duration text,commenttrip text,photo text,latitude text,longitude text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+USERSTABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+TRIPTABLE_NAME);
        onCreate(db);
    }

    public boolean insertUserDetails (String name,String email, String gender,String comment, String userid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USERSCOLUMN_NAME, name);
        contentValues.put(USERSCOLUMN_EMAIL, email);
        contentValues.put(USERSCOLUMN_GENDER, gender);
        contentValues.put(USERSCOLUMN_COMMENT, comment);
        contentValues.put(USERSCOLUMN_USERID, userid);
        db.insert(USERSTABLE_NAME, null, contentValues);
        return true;
    }


    public boolean insertTripDetails (String title,String date, String triptype,String destination,String duration,String commenttrip,String photo, String latitude, String longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TRIPSCOLUMN_TITLE, title);
        contentValues.put(TRIPSCOLUMN_DATE, date);
        contentValues.put(TRIPSCOLUMN_TRIPTYPE, triptype);
        contentValues.put(TRIPSCOLUMN_DESTINATION, destination);
        contentValues.put(TRIPSCOLUMN_DURATION, duration);
        contentValues.put(TRIPSCOLUMN_COMMENTTRIP, commenttrip);
        contentValues.put(TRIPSCOLUMN_PHOTO, photo);
        contentValues.put(TRIPSCOLUMN_LATITUDE, latitude);
        contentValues.put(TRIPSCOLUMN_LONGITUDE, longitude);
        db.insert(TRIPTABLE_NAME, null, contentValues);
        return true;
    }

    public boolean updateUserDetails (Integer id,String name,String email, String gender,String comment,String userid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USERSCOLUMN_NAME, name);
        contentValues.put(USERSCOLUMN_EMAIL, email);
        contentValues.put(USERSCOLUMN_GENDER, gender);
        contentValues.put(USERSCOLUMN_COMMENT, comment);
        contentValues.put(USERSCOLUMN_USERID, userid);
        db.update(USERSTABLE_NAME, contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }


    public boolean updateTripDetails (Integer id,String title,String date, String triptype,String destination,String duration,String commenttrip,String photo, String latitude, String longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TRIPSCOLUMN_TITLE, title);
        contentValues.put(TRIPSCOLUMN_DATE, date);
        contentValues.put(TRIPSCOLUMN_TRIPTYPE, triptype);
        contentValues.put(TRIPSCOLUMN_DESTINATION, destination);
        contentValues.put(TRIPSCOLUMN_DURATION, duration);
        contentValues.put(TRIPSCOLUMN_COMMENTTRIP, commenttrip);
        contentValues.put(TRIPSCOLUMN_PHOTO, photo);
        contentValues.put(TRIPSCOLUMN_LATITUDE, latitude);
        contentValues.put(TRIPSCOLUMN_LONGITUDE, longitude);
        db.update(TRIPTABLE_NAME, contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteTripDetails (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TRIPTABLE_NAME,
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<UserModelDetails> getAllUsersDetails() {
        ArrayList<UserModelDetails> array_list = new ArrayList<UserModelDetails>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "select * from "+USERSTABLE_NAME, null );
        res.moveToFirst();

        while(!res.isAfterLast()){
            UserModelDetails userModel = new UserModelDetails();
            userModel.setName(res.getString(res.getColumnIndex(USERSCOLUMN_NAME)));
            userModel.setEmail(res.getString(res.getColumnIndex(USERSCOLUMN_EMAIL)));
            userModel.setGender(res.getString(res.getColumnIndex(USERSCOLUMN_GENDER)));
            userModel.setComment(res.getString(res.getColumnIndex(USERSCOLUMN_COMMENT)));
            userModel.setUserID(res.getString(res.getColumnIndex(USERSCOLUMN_USERID)));
            userModel.setId(res.getString(res.getColumnIndex(USERSCOLUMN_ID)));
            array_list.add(userModel);
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<TripModelDetails> getAllTripsDetails() {
        ArrayList<TripModelDetails> array_list = new ArrayList<TripModelDetails>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "select * from "+TRIPTABLE_NAME, null );
        res.moveToFirst();

        while(!res.isAfterLast()){
            TripModelDetails tripModel = new TripModelDetails();
            tripModel.setId(res.getString(res.getColumnIndex(TRIPSCOLUMN_TRIP_ID)));
            tripModel.setTitle(res.getString(res.getColumnIndex(TRIPSCOLUMN_TITLE)));
            tripModel.setDate(res.getString(res.getColumnIndex(TRIPSCOLUMN_DATE)));
            tripModel.setTripType(res.getString(res.getColumnIndex(TRIPSCOLUMN_TRIPTYPE)));
            tripModel.setDestination(res.getString(res.getColumnIndex(TRIPSCOLUMN_DESTINATION)));
            tripModel.setDuration(res.getString(res.getColumnIndex(TRIPSCOLUMN_DURATION)));
            tripModel.setComment(res.getString(res.getColumnIndex(TRIPSCOLUMN_COMMENTTRIP)));
            tripModel.setPhoto(res.getString(res.getColumnIndex(TRIPSCOLUMN_PHOTO)));
            tripModel.setLatitude(res.getString(res.getColumnIndex(TRIPSCOLUMN_LATITUDE)));
            tripModel.setLongitude(res.getString(res.getColumnIndex(TRIPSCOLUMN_LONGITUDE)));
            array_list.add(tripModel);
            res.moveToNext();
        }
        return array_list;
    }

}