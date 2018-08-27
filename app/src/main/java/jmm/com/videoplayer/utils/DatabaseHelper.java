package jmm.com.videoplayer.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Favourite.db";
    public static final String TABLE_NAME = "favourite_table";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String THUMB = "thumb";
    public static final String FOLDER = "folder";
    public static final String TIME = "time";
    public static final String RESOLUTION = "resolution";
    public static final String DATE = "date";
    public static final String SIZE = "size";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("create table " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,THUMB TEXT,FOLDER TEXT, TIME TEXT,RESOLUTION TEXT,DATE TEXT,SIZE TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean insertdata(String name,String thumb,String folder,String time,String resolution,String date,String size) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, name);
        contentValues.put(THUMB, thumb);
        contentValues.put(FOLDER, folder);
        contentValues.put(TIME, time);
        contentValues.put(RESOLUTION, resolution);
        contentValues.put(DATE, date);
        contentValues.put(SIZE, size);

        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;

        }
    }

    public Cursor getalldata() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + TABLE_NAME+" order by ID desc ", null);
        return cursor;
    }

    public boolean updatedata(String id,String name) {

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, name);
        contentValues.put(ID, id);
        database.update(TABLE_NAME, contentValues, "ID = ?", new String[] { name });

        return true;
    }

    public Integer deletedata(String name){
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        return sqLiteDatabase.delete(TABLE_NAME,"NAME = ? ",new String[] {name});

    }
    public int getalldatacount() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + TABLE_NAME+" order by ID desc ", null);
        return cursor.getCount();
    }

}
