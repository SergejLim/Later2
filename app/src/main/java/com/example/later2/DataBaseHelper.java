package com.example.later2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper{


    public static final String CHECKLIST_TABLE = "CHECKLIST_TABLE";
    public static final String TITLE = "TITLE";
    public static final String COLOR = "COLOR";
    public static final String ICON = "ICON";
    public static final String PASSWORD = "PASSWORD";
    public static final String FAVOURITE = "FAVOURITE";
    public static final String CHCK_KEY = "CHCK_KEY";
    public static final String SHARED = "SHARED";
    public static final String LAYOUT = "LAYOUT";
    public static final String TYPE = "TYPE";
    public static final String DATE_CREATED = "DATE_CREATED";
    public static final String DATE_EDITED = "DATE_EDITED";
    public static final String NUMBER_IN_LIST = "NUMBER_IN_LIST";
    public static final String CHCK_ORDER = "CHCK_ORDER";
    public static final String NUMBER_UNTICKED = "NUMBER_UNTICKED";

    public static final String ID = "ID";

    public DataBaseHelper(@Nullable Context context) {
        super(context, "checklists.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTableStatement = "CREATE TABLE " + CHECKLIST_TABLE + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TITLE
                + " TEXT, " + COLOR + " TEXT, " + ICON + " TEXT, " + PASSWORD + " TEXT, " +
                FAVOURITE + " BOOL, " + CHCK_KEY + " TEXT, " + SHARED + " BOOL, " + LAYOUT + " TEXT" +
                ", " + TYPE + " TEXT, " + DATE_CREATED + " TEXT, " + DATE_EDITED + " TEXT, " + NUMBER_IN_LIST + " INT, " + CHCK_ORDER + " TEXT "+", "+ NUMBER_UNTICKED + " INT)";

        sqLiteDatabase.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    public boolean addCheckList(CheckLists checkLists){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TITLE, checkLists.getTitle());
        cv.put(COLOR, checkLists.getColor());
        cv.put(ICON, checkLists.getIcon());
        cv.put(PASSWORD, checkLists.getPassword());
        cv.put(FAVOURITE, checkLists.isFavourite());
        cv.put(CHCK_KEY, checkLists.getKey());
        cv.put(SHARED, checkLists.getShared());
        cv.put(LAYOUT, checkLists.getLayout());
        cv.put(TYPE, checkLists.getType());
        cv.put(DATE_CREATED, checkLists.getDateCreated());
        cv.put(DATE_EDITED, checkLists.getDateEdited());
        cv.put(NUMBER_IN_LIST, checkLists.getNumberInList());
        cv.put(CHCK_ORDER, checkLists.getOrder());
        cv.put(NUMBER_UNTICKED, checkLists.getNumberUnticked());

        long insert = db.insert(CHECKLIST_TABLE,null,cv);
        if(insert ==-1){
            return false;
        }else{
            return true;
        }
    }

    public List<CheckLists> getAll(){
        List<CheckLists> returnList = new ArrayList<>();

        //get Data

        String queryString = "SELECT * FROM " + CHECKLIST_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString,null);
        System.out.println("NUMBER : " +cursor.getColumnCount());
        if( cursor.moveToFirst()){
            do{

                 int ID = cursor.getInt(0);
                 String TITLE = cursor.getString(1);
                 String COLOR =cursor.getString(2);
                 String ICON = cursor.getString(3);
                 String PASSWORD = cursor.getString(4);
                 boolean FAVOURITE =  cursor.getInt(5) ==1 ? true: false;
                 String CHCK_KEY = cursor.getString(6);
                 String SHARED = cursor.getString(7);
                 String LAYOUT = cursor.getString(8);
                 String TYPE = cursor.getString(9);
                 String DATE_CREATED = cursor.getString(10);
                 String DATE_EDITED = cursor.getString(11);
                 int NUMBER_IN_LIST = cursor.getInt(12);
                 String CHCK_ORDER = cursor.getString(13);
                 int NUMBER_UNTICKED = cursor.getInt(14);

                 CheckLists checkLists = new CheckLists(ID, TITLE, COLOR,ICON,PASSWORD,FAVOURITE,CHCK_KEY,SHARED,LAYOUT,TYPE,DATE_CREATED,DATE_EDITED,NUMBER_IN_LIST,CHCK_ORDER,NUMBER_UNTICKED);
                 returnList.add(checkLists);

            }while (cursor.moveToNext());
        }else{

        }
        cursor.close();
        db.close();
        return returnList;
    }

    public void alterTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "ALTER TABLE "+ CHECKLIST_TABLE + " ADD COLUMN " + NUMBER_UNTICKED + " INT DEFAULT 0;";
        System.out.println(queryString);
        db.execSQL(queryString);
    }

    public void editCheckList(CheckLists checkLists){
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "UPDATE "+ CHECKLIST_TABLE +
                " SET " +
                TITLE + " = '" + checkLists.getTitle() +"', "+
        COLOR + " = '" + checkLists.getColor()+"', "+
        ICON+ " = '" + checkLists.getIcon()+"', "+
        PASSWORD+ " = '" + checkLists.getPassword()+"', "+
        //FAVOURITE+ " = " + checkLists.isFavourite()+", "+
        CHCK_KEY+ " = '" + checkLists.getKey()+"', "+
        SHARED+ " = '" + checkLists.getShared()+"', "+
        LAYOUT+ " = '" + checkLists.getLayout()+"', "+
        TYPE+ " = '" + checkLists.getType()+"', "+
        DATE_CREATED+ " = '" + checkLists.getDateCreated()+"', "+
        DATE_EDITED+ " = '" + checkLists.getDateEdited()+"', "+
        NUMBER_IN_LIST+ " = " + checkLists.getNumberInList()+", "+
        CHCK_ORDER+ " = '" + checkLists.getOrder()+"', "+
        NUMBER_UNTICKED+ " = " + checkLists.getNumberUnticked()+
                " WHERE " + ID + " = " + checkLists.getId();
        System.out.println(queryString);
         db.rawQuery(queryString, null);
    }

    public boolean deleteCheckList(CheckLists checkLists){
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM "+ CHECKLIST_TABLE + " WHERE " + ID + " = " + checkLists.getId();
        Cursor cursor = db.rawQuery(queryString, null);
        if(cursor.moveToFirst()){
            return true;
        }else{
            return false;
        }
    }


}
