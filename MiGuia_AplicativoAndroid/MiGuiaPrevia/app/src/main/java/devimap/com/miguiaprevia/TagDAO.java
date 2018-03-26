package devimap.com.miguiaprevia;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.Serializable;

/**
 * Created by nossopc on 01/10/16.
 */
public class TagDAO implements Serializable {
    private SQLiteDatabase dataBase;

    public TagDAO (Context context) {
        dataBase = (new DataBase(context)).getWritableDatabase();
    }

    public Tag getTagByHexKey (String key, int mapID) {
        Tag tag = null;

        String sqlQuery = "SELECT * FROM Tags WHERE hexkey='"+ key + "' AND mapid='" + mapID + "'";
        Cursor cursor= this.dataBase.rawQuery(sqlQuery, null);

        if (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            int posX = cursor.getInt(1);
            int posY = cursor.getInt(2);
            String placeName = cursor.getString(4);
            boolean intersection;
            if (cursor.getInt(5) == 1)
                intersection = true;
            else
                intersection = false;

            tag = new Tag(id, posX, posY, key, placeName, intersection);
        }

        cursor.close();
        return tag;
    }

    public Tag getTagByPlaceName (String placeName, int mapID) {
        Tag tag = null;

        String sqlQuery = "SELECT * FROM Tags WHERE nameid='"+ placeName + "' AND mapid='" + mapID + "'";
        Cursor cursor= this.dataBase.rawQuery(sqlQuery, null);

        if (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            int posX = cursor.getInt(1);
            int posY = cursor.getInt(2);
            String key = cursor.getString(3);
            boolean intersection;
            if (cursor.getInt(5) == 1)
                intersection = true;
            else
                intersection = false;

            tag = new Tag(id, posX, posY, key, placeName, intersection);
        }

        cursor.close();
        return tag;
    }

    public int getTagIdByHexKey (String key, int mapID) {
        String sqlQuery = "SELECT * FROM Tags WHERE hexkey='"+ key + "' AND mapid='" + mapID + "'";
        Cursor cursor= this.dataBase.rawQuery(sqlQuery, null);

        if (cursor.moveToFirst()) {
            //Log.i("Tags", "Encontrou tag com hex=" + key);
            int id = cursor.getInt(0);
            cursor.close();
            return id;
        }
       // Log.i("Tags", "Não encontrou tag com hex=" + key);

        cursor.close();
        return -1;
    }

    public int getTagIdByPlaceName (String placeName, int mapID) {
        String sqlQuery = "SELECT * FROM Tags WHERE nameid='"+ placeName + "' AND mapid='" + mapID + "'";
        Cursor cursor= this.dataBase.rawQuery(sqlQuery, null);

        if (cursor.moveToFirst()) {
          //  Log.i("Tags", "Encontrou tag com place=" + placeName);
            int id = cursor.getInt(0);
            cursor.close();
            return id;
        } else {
           // Log.i("Tags", "Não encontrou tag com place=" + placeName);
        }

        cursor.close();
        return -1;
    }

    public void closeConnection () {
        dataBase.close();
    }
}
