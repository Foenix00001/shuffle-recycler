package org.foenix.shufflerecycler.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Foenix on 12.12.2016.
 */

class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Items.db";
    private static final int DATABASE_VERSION = 1;
    private final Context mContext;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
        createDataBaseIfNotExist();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    private void createDataBaseIfNotExist() {
        if (!isDataBaseExist()) {
            SQLiteDatabase db = this.getReadableDatabase();
            db.close();
            try {
                copyDataBase(mContext.getAssets().open(DATABASE_NAME),
                        mContext.getDatabasePath(DATABASE_NAME).toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void copyDataBase(InputStream is, String name) throws IOException {
        OutputStream os = new FileOutputStream(name);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }
        os.flush();
        os.close();
        is.close();
    }

    private boolean isDataBaseExist() {
        File dbFile = mContext.getDatabasePath(DATABASE_NAME);
        return dbFile.exists();
    }
}
