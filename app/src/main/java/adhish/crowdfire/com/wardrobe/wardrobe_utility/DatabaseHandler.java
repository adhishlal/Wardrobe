package adhish.crowdfire.com.wardrobe.wardrobe_utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper{
	private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "crowdfire";
 
    //Table Names
    private static final String TABLE_TOP_COLLECTION= "wardrobe_top_collection";
    private static final String TABLE_BOTTOM_COLLECTION= "wardrobe_bottom_collection";
    private static final String TABLE_WARDROBE_FAV= "wardrobe_fav_collection";

    //Wardrobe Collection Table Cols
    private static final String KEY_ID = "id";
	private static final String KEY_IMAGE_TOP="imagetop";

    //Wardrobe Collection Table Cols Bottom
    private static final String KEY_IMAGE_BOTTOM="imagebottom";

    //favs
    private static final String KEY_IMAGE_TOP_ID="imagetopid";
    private static final String KEY_IMAGE_BOTTOM_ID="imagebottomid";


    public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

        String CREATE_IMAGE_COLLECTION_TABLE="CREATE TABLE "+TABLE_TOP_COLLECTION+"("
                +KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +KEY_IMAGE_TOP + " BLOB);";

        String CREATE_IMAGE_COLLECTION_TABLE_BOTTOM="CREATE TABLE "+TABLE_BOTTOM_COLLECTION+"("
                +KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +KEY_IMAGE_BOTTOM + " BLOB);";

        String CREATE_IMAGE_TABLE_FAV="CREATE TABLE "+TABLE_WARDROBE_FAV+"("
                +KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +KEY_IMAGE_TOP_ID+" INTEGER,"
                +KEY_IMAGE_BOTTOM_ID + " INTEGER);";

		db.execSQL(CREATE_IMAGE_COLLECTION_TABLE);
        db.execSQL(CREATE_IMAGE_COLLECTION_TABLE_BOTTOM);
        db.execSQL(CREATE_IMAGE_TABLE_FAV);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOP_COLLECTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOTTOM_COLLECTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WARDROBE_FAV);
        // Create tables again
        onCreate(db);
	}


	public void addTop(byte[] top) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_IMAGE_TOP, top);

		// Inserting Row
		db.insert(TABLE_TOP_COLLECTION, null, values);
		db.close(); // Closing database connection

	}

    public void addBottom(byte[] bottom) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_IMAGE_BOTTOM, bottom);

        // Inserting Row
        db.insert(TABLE_BOTTOM_COLLECTION, null, values);
        db.close(); // Closing database connection

    }

    public void addFav(int topID,int bottomID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_IMAGE_TOP_ID, topID);
        values.put(KEY_IMAGE_BOTTOM_ID, bottomID);

        // Inserting Row
        db.insert(TABLE_WARDROBE_FAV, null, values);
        db.close(); // Closing database connection

    }

	public void updateTop(byte[] top,String id) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(KEY_IMAGE_TOP, top);

		// Updating Row
		db.update(TABLE_TOP_COLLECTION, values, id + "=" + id, null);
		db.close(); // Closing database connection
	}

    public void updateBottom(byte[] bottom,String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_IMAGE_BOTTOM, bottom);

        // Updating Row
        db.update(TABLE_BOTTOM_COLLECTION, values, id + "=" + id, null);
        db.close(); // Closing database connection
    }

    public void updateFav(int topID,int bottomID,String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_IMAGE_TOP_ID, topID);
        values.put(KEY_IMAGE_BOTTOM_ID, bottomID);

        // Updating Row
        db.update(TABLE_WARDROBE_FAV, values, id + "=" + id, null);
        db.close(); // Closing database connection
    }

	public List<byte[]> getTop(){

        String selectQuery = "SELECT  * FROM " + TABLE_TOP_COLLECTION+";";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        byte[] bytes;
        // Move to first row
        cursor.moveToFirst();
        List<byte[]> list=new ArrayList<>();

        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
        {
            bytes=cursor.getBlob(cursor.getColumnIndex(KEY_IMAGE_TOP));
            list.add(bytes);
        }

        cursor.close();
        db.close();

        return list;
	}


    public List<byte[]> getBottom(){

        String selectQuery = "SELECT  * FROM " + TABLE_BOTTOM_COLLECTION+";";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        byte[] bytes;
        // Move to first row
        cursor.moveToFirst();
        List<byte[]> list=new ArrayList<>();

        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
        {
            bytes=cursor.getBlob(cursor.getColumnIndex(KEY_IMAGE_BOTTOM));
            list.add(bytes);
        }

        cursor.close();
        db.close();

        return list;
    }

    public List getFav(){

        String selectQuery = "SELECT  * FROM " + TABLE_WARDROBE_FAV+";";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        String topID,bottomID;
        // Move to first row
        cursor.moveToFirst();
        List list=new ArrayList<>();

        HashMap<String,String> params=new HashMap<>();


        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
        {
            topID=cursor.getInt(cursor.getColumnIndex(KEY_IMAGE_TOP_ID))+"";
            bottomID=cursor.getInt(cursor.getColumnIndex(KEY_IMAGE_BOTTOM_ID))+"";

            params.put("top",topID);
            params.put("bottom",bottomID);

            list.add(params);

        }

        cursor.close();
        db.close();

        return list;
    }

	public int getTopCount() {
		String countQuery = "SELECT  * FROM " + TABLE_TOP_COLLECTION;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int rowCount = cursor.getCount();
		db.close();
		cursor.close();

		// return row count
		return rowCount;
	}

    public int getBottomCount() {
        String countQuery = "SELECT  * FROM " + TABLE_BOTTOM_COLLECTION;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        // return row count
        return rowCount;
    }
    public int getFavCount() {
        String countQuery = "SELECT  * FROM " + TABLE_WARDROBE_FAV;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        // return row count
        return rowCount;
    }


    public void resetTables(){
	        SQLiteDatabase db = this.getWritableDatabase();
	        // Delete All Rows
        db.delete(TABLE_TOP_COLLECTION, null, null); //Delete Top Collection Table
        db.delete(TABLE_BOTTOM_COLLECTION, null, null); //Delete Bottom Collection Table
        db.delete(TABLE_WARDROBE_FAV, null, null); //Delete Fav Collection Table
	        db.close();
	    }
}
