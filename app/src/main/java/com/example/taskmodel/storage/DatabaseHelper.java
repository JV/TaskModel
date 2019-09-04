package com.example.taskmodel.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


// use this class for using data from local database

public class DatabaseHelper extends SQLiteOpenHelper {

    private static String DATABASE_NAME = "elementsDatabase.db";
    private static final String TABLE_NAME = "elements";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAZIV = "naziv";
    public static final String COLUMN_POCETAK = "pocetak";
    public static final String COLUMN_KRAJ = "kraj";
    public static final String COLUMN_TAG = "tag";

    String DB_PATH = null;
    private SQLiteDatabase myDb;

    private Context myContext;

    public DatabaseHelper(Context context) {

        super(context, DATABASE_NAME, null, 2);
        this.myContext = context;
        this.DB_PATH = "/data/data/" + context.getPackageName() + "/" + "databases/";
    }

    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();

        if (dbExist) {

        } else {
            this.getWritableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {

            }
        }
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH + DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLException e) {

        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    private void copyDataBase() throws IOException {
        InputStream myInput = myContext.getAssets().open("databases/" + DATABASE_NAME);
        String outFileName = DB_PATH + DATABASE_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) != -1) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public boolean insertData(String naziv, String id, String pocetak, String kraj, String tag) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAZIV, naziv);
        contentValues.put(COLUMN_ID, id);
        contentValues.put(COLUMN_POCETAK, pocetak);
        contentValues.put(COLUMN_KRAJ, kraj);
        contentValues.put(COLUMN_TAG, tag);

        long result = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public void openDataBase() throws SQLException {
        String myPath = DB_PATH + DATABASE_NAME;
        myDb = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }


    @Override
    public synchronized void close() {
        if (myDb != null) {
            myDb.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if (i < i1) {
            try {
                copyDataBase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.disableWriteAheadLogging();
    }

    public Cursor getAllData(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {

        return myDb.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
    }
}
