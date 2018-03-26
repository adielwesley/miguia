package devimap.com.miguiaprevia;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBase extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "MiGuiaDatabase.db";

    private static final String SQL_CREATE_TABLE_MAPS = "CREATE TABLE Maps (id INT PRIMARY KEY NOT NULL, mapname TEXT, amountoftags INT NOT NULL, adjmatrix TEXT)";
    private static final String SQL_CREATE_TABLE_TAGS = "CREATE TABLE Tags (id INT NOT NULL, posx INT NOT NULL, posy INT NOT NULL, hexkey TEXT, nameid TEXT, intersection INT, mapid INT)";
    private static final String SQL_DELETE_TABLE_MAPS = "DROP TABLE IF EXISTS Maps";
    private static final String SQL_DELETE_TABLE_TAGS = "DROP TABLE IF EXISTS Tags";

    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_MAPS);
        db.execSQL(SQL_CREATE_TABLE_TAGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL(SQL_DELETE_TABLE_MAPS);
            db.execSQL(SQL_DELETE_TABLE_TAGS);
            onCreate(db);
        }
    }
}
