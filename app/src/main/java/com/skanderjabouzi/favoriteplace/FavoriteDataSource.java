package com.skanderjabouzi.favoriteplace;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class FavoriteDataSource {

	// Database fields
	private SQLiteDatabase database;
	private DBHelper dbHelper;

	public FavoriteDataSource(Context context) {
		dbHelper = new DBHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
		Log.i("FavoriteDataSource", "open");
	}
	
	public boolean isOpen()
	{
		return database.isOpen();
	} 

	public void close() {
		dbHelper.close();
	}

	void addFavorite(Favorite Favorite) {
		ContentValues values = new ContentValues();
		values.put("id", Favorite.getId());
		values.put("latitude", Favorite.getLatitude());
		values.put("longitude", Favorite.getLongitude());
		values.put("city", Favorite.getCity());
		values.put("country", Favorite.getCountry());

		database.insert("Favorite", null, values);
		database.close();
	}

	// Getting single Favorite
	Favorite getFavorite(int id) {
		Cursor cursor = database.query("favorite", new String[] { "id",
				"latitude", "longitude", "city", "country"}," id = ?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		Favorite Favorite = new Favorite();
		Favorite.setId(cursor.getLong(0));
		Favorite.setLatitude(cursor.getString(1));
		Favorite.setLongitude(cursor.getString(2));
		Favorite.setCity(cursor.getString(3));
		Favorite.setCountry(cursor.getString(4));
		cursor.close();
		
		// return Favorite
		return Favorite;
	}

	// Updating single Favorite
	public int updateFavorite(Favorite Favorite) {
		ContentValues values = new ContentValues();
		values.put("latitude", Favorite.getLatitude());
		values.put("longitude", Favorite.getLongitude());
		values.put("city", Favorite.getCity());
		values.put("country", Favorite.getCountry());

		// updating row
		return database.update("favorite", values," id = ?",
				new String[] { String.valueOf(Favorite.getId()) });
	}

	// Deleting single Favorite
	public void deleteFavorite(Favorite Favorite) {
		database.delete("favorite"," id = ?",
				new String[] { String.valueOf(Favorite.getId()) });
		database.close();
	}


	// Getting Favorite Count
	public int getFavoriteCount() {
		String countQuery = "SELECT  * FROM " + "favorite";
		Cursor cursor = database.rawQuery(countQuery, null);
		cursor.close();

		// return count
		return cursor.getCount();
	}
}
