package com.huybinh2k.computerstore.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by BinhBH on 10/10/2021.
 */
public class SuggestionDAO {

    private final SQLiteDatabase mDatabase;
    public SuggestionDAO(Context context) {
        DatabaseHelper helper = new DatabaseHelper(context);
        this.mDatabase = helper.getWritableDatabase();
    }

    /**
     * @param suggest BinhBH Thêm gợi ý vào database
     */
    public void insertSuggest(String suggest){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.SUGGESTION, suggest);
        mDatabase.insert(DatabaseHelper.SUGGESTION_TABLE,null,values);
    }

    /**
     * @return BinhBH Lấy ra tất cả gợi ý từ database
     */
    public ArrayList<String> listAllSuggestion() {
        ArrayList<String> list = new ArrayList<>();
        String sql ="SELECT DISTINCT * FROM "+ DatabaseHelper.SUGGESTION_TABLE;
        Cursor c = mDatabase.rawQuery(sql, null);
        while (c.moveToNext()) {
            String suggest = c.getString(c.getColumnIndex(DatabaseHelper.SUGGESTION));
            list.add(suggest);
        }
        c.close();
        return list;
    }

    /**
     * @param query
     * @return BinhBH Lấy ra gợi ý theo query từ database
     */
    public ArrayList<String> getSuggestionForQuery(String query) {
        if (query.isEmpty()){
            return listAllSuggestion();
        }else {
            ArrayList<String> list = new ArrayList<>();
            Cursor c = mDatabase.query(DatabaseHelper.SUGGESTION_TABLE, null,
            DatabaseHelper.SUGGESTION + " LIKE ?", new String[]{"%"+query+"%"},DatabaseHelper.SUGGESTION,null,null,null);
            while (c.moveToNext()) {
                String suggest = c.getString(c.getColumnIndex(DatabaseHelper.SUGGESTION));
                list.add(suggest);
            }
            c.close();
            return list;
        }
    }

    /**
     * @param suggest BinhBH Xóa gợi ý
     */
    public void deleteSuggest(String suggest) {
        String whereClause = DatabaseHelper.SUGGESTION+ "=?";
        String[] whereArgs = {suggest};
        mDatabase.delete(DatabaseHelper.SUGGESTION_TABLE,whereClause,whereArgs);

    }
}
