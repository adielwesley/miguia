package devimap.com.miguiaprevia;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by nossopc on 01/10/16.
 */
public class TagMapDAO {
    private SQLiteDatabase dataBase;

    public TagMapDAO (Context context) {
        dataBase = (new DataBase(context)).getWritableDatabase();
    }

    // Método para recuperar um mapa de tags pelo nome
    public TagMap getTagMap (String mapPlaceName) {
        TagMap tagMap = null;

        String sqlQuery = "SELECT * FROM Maps WHERE mapname='"+ mapPlaceName + "'";
        Cursor cursor= this.dataBase.rawQuery(sqlQuery, null);

        if (cursor.moveToNext()) {
            int mapID = cursor.getInt(0);
            int amountOfTags = cursor.getInt(2);
            String adjMatrixString = cursor.getString(3);

            int[][] adjMatrix = decodeMatrix(adjMatrixString, amountOfTags);
            ArrayList<Tag> tagList = getTagList(mapID); // recupera dados sobre as tags da tabela Tags

            tagMap = new TagMap(mapID, mapPlaceName, amountOfTags, adjMatrix, tagList/*, context*/);
        }

        cursor.close();
        return tagMap;
    }

    public boolean addTagMap (TagMap tagMap) {
        String adjMatrixString;

        // Insere dados sobre as tags na tabela Tags
        for (Tag tag:
                tagMap.getTagList()) {
            try {
                int intersection;
                if(tag.getIntersection()){
                    intersection = 1;
                }
                else {
                    intersection=0;
                }
                String sqlCommand = "INSERT INTO Tags VALUES ('" +
                        tag.getId() + "', '" + tag.getPosi_X() + "', '" +
                        tag.getPosi_Y() + "', '" + tag.getKey() + "', '" + tag.getPlace()
                        + "', '" + intersection + "', '" + tagMap.getMapID() + "')";
                //Log.d("Debug",sqlCommand);
                this.dataBase.execSQL(sqlCommand);
            } catch (SQLException e) {
                Log.e("SQL Error", e.getMessage());
                return false;
            }
        }

        adjMatrixString = encodeMatrix(tagMap.getGraph(), tagMap.getAmountOfTags());

        // Insere dados sobre mapa de tags na tabela Maps
        try {
            String sqlCommand = "INSERT INTO Maps VALUES ('" +
                    tagMap.getMapID() + "', '" + tagMap.getMapPlaceName() + "', '" +
                    tagMap.getAmountOfTags() + "', '" + adjMatrixString + "')";
            this.dataBase.execSQL(sqlCommand);

            return true;
        } catch (SQLException e) {
            Log.e("SQL Error", e.getMessage());
            return false;
        }
    }

    public boolean searchMap(String mapPlaceName) {
        String sqlQuery = "SELECT * FROM Maps WHERE mapname='"+ mapPlaceName + "'";
        Cursor cursor= this.dataBase.rawQuery(sqlQuery, null);
        boolean ret = cursor.moveToNext();
        cursor.close();
        return ret;
    }

    public boolean searchMap(int id) {
        String sqlQuery = "SELECT * FROM Maps WHERE id='"+ id + "'";
        Cursor cursor= this.dataBase.rawQuery(sqlQuery, null);
        return cursor.moveToNext();
    }

    public Cursor getTagMaps() {
        return this.dataBase.rawQuery("SELECT rowid AS _id, " +
        "mapname FROM Maps ORDER BY mapname", null);
    }

    private String encodeMatrix(int[][] adjMatrix, int amountOfTags) {
        String adjMatrixString = "";

        for (int i = 0; i < amountOfTags; i++) {
            for (int j = 0; j < amountOfTags; j++) {
                adjMatrixString = adjMatrixString.concat(Integer.toString(adjMatrix[i][j]));

            }
        }
        Log.i("adjMatrixString", adjMatrixString);

        return adjMatrixString;
    }

    private int[][] decodeMatrix(String adjMatrixString, int amountOfTags) {
        int[][] adjMatrix = new int[amountOfTags][amountOfTags];

        char[] tagNeighborhood;
        int start;
        int end;

        for (int i = 0; i < amountOfTags; i++) {
            tagNeighborhood = new char[amountOfTags];
            start = i*amountOfTags;
            end = start+amountOfTags;
            adjMatrixString.getChars(start,end,tagNeighborhood,0);
            for (int j = 0; j < amountOfTags; j++) {
                if (tagNeighborhood[j] == '0') {
                    adjMatrix[i][j] = 0;
                } else {
                    adjMatrix[i][j] = 1;
                }
            }
        }

        return  adjMatrix;
    }

    // Recebe o mapID de um mapa
    // Retorna uma lista das tags pertencentes ao mapa que possui esse mapID
    public ArrayList<Tag> getTagList (int mapID) {
        ArrayList<Tag> tagList = new ArrayList<>();
        Cursor cursor = this.dataBase.rawQuery("SELECT * FROM Tags WHERE mapid='" + mapID +"'", null);

        int id;
        int posX;
        int posY;
        String placeName;
        String key;
        boolean intersection;
        while (cursor.moveToNext()) {
            //Log.i("Tags", "Recuperou Tag do BD, mapID = " + cursor.getInt(6));
            id = cursor.getInt(0);
            posX = cursor.getInt(1);
            posY = cursor.getInt(2);
            key = cursor.getString(3);
            placeName = cursor.getString(4);
            if (cursor.getInt(5) == 1)
                intersection = true;
            else
                intersection = false;

            Tag tag = new Tag(id, posX, posY, key, placeName, intersection);
            tagList.add(tag);
        }

        return tagList;
    }

    public void removeItem(int mapId) {
        dataBase.execSQL("DELETE FROM Maps WHERE id='" + mapId + "'");
        dataBase.execSQL("DELETE FROM Tags WHERE mapid='" + mapId + "'");
    }

    public void closeConnection () {
        dataBase.close();
    }
}
