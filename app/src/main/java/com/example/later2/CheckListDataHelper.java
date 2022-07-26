package com.example.later2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CheckListDataHelper extends SQLiteOpenHelper {

    public static final String CHECKLISTDATA_TABLE = "CHECKLISTDATA_TABLE";
    public static final String ID = "ID";
    public static final String FID = "FID";
    public static final String TITLE = "TITLE";
    public static final String COLOR = "COLOR";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String IMAGES = "IMAGES";
    public static final String URGENT = "URGENT";
    public static final String TICKED = "TICKED";
    public static final String RATING = "RATING";
    public static final String XP = "XP";
    public static final String DATEDUE = "DATEDUE";
    public static final String DATECREATED = "DATECREATED";
    public static final String DATEEDITED = "DATEEDITED";
    public static final String ITEMORDER = "ITEMORDER";
    public static final String LOCATION = "LOCATION";
    public static final String ICONIMAGE = "ICONIMAGE";
    public static final String NOTE = "NOTE";
    public static final String OTHER = "OTHER";
    public static final String DRAWING = "DRAWING";

    public CheckListDataHelper(@Nullable Context context) {
        super(context, "checklistData.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTableStatement = "CREATE TABLE " + CHECKLISTDATA_TABLE + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FID + " INT, " + TITLE + " TEXT," +
                COLOR + " TEXT, " + DESCRIPTION + " TEXT, " + IMAGES + " TEXT, " + URGENT + " BOOL, " + TICKED + " BOOL, " + RATING + " TEXT, " + XP + " INT, " + DATEDUE + " TEXT, " + DATECREATED + " TEXT," +
                DATEEDITED + " TEXT, " + ITEMORDER + " TEXT, " + LOCATION + " TEXT, " + ICONIMAGE + " TEXT, " + NOTE + " TEXT, " + OTHER + " TEXT, " + DRAWING + " TEXT )";

        sqLiteDatabase.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {    }

    public List<CheckListData> getWithFId(String Fid){
        List<CheckListData> returnList = new ArrayList<>();
        //get Data
        String queryString = "SELECT * FROM " + CHECKLISTDATA_TABLE + " WHERE " + FID + " = " + Fid;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString,null);
        if( cursor.moveToFirst()){
            do{
                int ID = cursor.getInt(0);
                int FID = cursor.getInt(1);
                String TITLE = cursor.getString(2);
                String COLOR =cursor.getString(3);
                String DESCRIPTION =cursor.getString(4);
                String IMAGES =cursor.getString(5);
                boolean URGENT =cursor.getInt(6)==1 ? true: false;
                boolean TICKED =cursor.getInt(7)==1 ? true: false;
                String RATING =cursor.getString(8);
                int XP =cursor.getInt(9);
                String DATEDUE =cursor.getString(10);
                String DATECREATED =cursor.getString(11);
                String DATEEDITED =cursor.getString(12);
                String ITEMORDER =cursor.getString(13);
                String LOCATION =cursor.getString(14);
                String ICONIMAGE =cursor.getString(15);
                String NOTE =cursor.getString(16);
                String OTHER =cursor.getString(17);
                String DRAWING =cursor.getString(18);

                CheckListData checkListData = new CheckListData(ID, FID, TITLE, COLOR,DESCRIPTION,IMAGES,URGENT,TICKED,RATING, XP,DATEDUE,DATECREATED,DATEEDITED,ITEMORDER,LOCATION,ICONIMAGE,NOTE,OTHER,DRAWING);
                returnList.add(checkListData);

            }while (cursor.moveToNext());
        }else{

        }
        cursor.close();
        db.close();
        return returnList;
    }

    public List<CheckListData> getWithId(String id){
        List<CheckListData> returnList = new ArrayList<>();
        //get Data
        String queryString = "SELECT * FROM " + CHECKLISTDATA_TABLE + " WHERE " + ID + " = " + id;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString,null);
        if( cursor.moveToFirst()){
            do{
                int ID = cursor.getInt(0);
                int FID = cursor.getInt(1);
                String TITLE = cursor.getString(2);
                String COLOR =cursor.getString(3);
                String DESCRIPTION =cursor.getString(4);
                String IMAGES =cursor.getString(5);
                boolean URGENT =cursor.getInt(6)==1 ? true: false;
                boolean TICKED =cursor.getInt(7)==1 ? true: false;
                String RATING =cursor.getString(8);
                int XP =cursor.getInt(9);
                String DATEDUE =cursor.getString(10);
                String DATECREATED =cursor.getString(11);
                String DATEEDITED =cursor.getString(12);
                String ITEMORDER =cursor.getString(13);
                String LOCATION =cursor.getString(14);
                String ICONIMAGE =cursor.getString(15);
                String NOTE =cursor.getString(16);
                String OTHER =cursor.getString(17);
                String DRAWING =cursor.getString(18);

                CheckListData checkListData = new CheckListData(ID, FID, TITLE, COLOR,DESCRIPTION,IMAGES,URGENT,TICKED,RATING, XP,DATEDUE,DATECREATED,DATEEDITED,ITEMORDER,LOCATION,ICONIMAGE,NOTE,OTHER,DRAWING);
                returnList.add(checkListData);

            }while (cursor.moveToNext());
        }else{

        }
        cursor.close();
        db.close();
        return returnList;
    }

    public boolean addCheckListData(CheckListData checkListData){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(FID, checkListData.getFId());
        cv.put(TITLE, checkListData.getTitle());
        cv.put(COLOR, checkListData.getColor());
        cv.put(DESCRIPTION, checkListData.getDescription());
        cv.put(IMAGES, checkListData.getImages());
        cv.put(URGENT, checkListData.isUrgent());
        cv.put(TICKED, checkListData.isTicked());
        cv.put(RATING, checkListData.getRating());
        cv.put(XP, checkListData.getXp());
        cv.put(DATEDUE, checkListData.getDateDue());
        cv.put(DATECREATED, checkListData.getDateCreated());
        cv.put(DATEEDITED, checkListData.getDateEdited());
        cv.put(ITEMORDER, checkListData.getOrder());
        cv.put(LOCATION, checkListData.getLocation());
        cv.put(ICONIMAGE, checkListData.getIconImage());
        cv.put(NOTE, checkListData.getNote());
        cv.put(OTHER, checkListData.getOther());
        cv.put(DRAWING, checkListData.getDrawing());

        long insert = db.insert(CHECKLISTDATA_TABLE,null,cv);
        if(insert ==-1){
            return false;
        }else{
            return true;
        }
    }

    public void editCheckListData(CheckListData checkListData){
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "UPDATE "+ CHECKLISTDATA_TABLE +
                " SET " +
                TITLE + " = '" + checkListData.getTitle() +"', "+
                COLOR + " = '" + checkListData.getColor()+"', "+
                DESCRIPTION+ " = '" + checkListData.getDescription()+"', "+
                IMAGES+ " = '" + checkListData.getImages()+"', "+
                URGENT+ " = " + checkListData.isUrgent()+", "+
                TICKED+ " = " + checkListData.isTicked()+", "+
                RATING+ " = '" + checkListData.getRating()+"', "+
                //XP+ " = '" + checkListData.getXp()+"', "+
                DATEDUE+ " = '" + checkListData.getDateDue()+"', "+
                DATECREATED+ " = '" + checkListData.getDateCreated()+"', "+
                DATEEDITED+ " = '" + checkListData.getDateEdited()+"', "+
                ITEMORDER+ " = " + checkListData.getOrder()+", "+
                LOCATION+ " = '" + checkListData.getLocation()+"', "+
                ICONIMAGE+ " = " + checkListData.getIconImage()+
                " WHERE " + ID + " = " + checkListData.getId();
        db.execSQL(queryString);
    }

    public boolean deleteCheckListData(CheckListData checkListData){
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM "+ CHECKLISTDATA_TABLE + " WHERE " + ID + " = " + checkListData.getId();
        Cursor cursor = db.rawQuery(queryString, null);
        if(cursor.moveToFirst()){
            return true;
        }else{
            return false;
        }
    }

}
