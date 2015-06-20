package jp.itnav.derushio.aedmapper.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import jp.itnav.derushio.sqldatabaselib.DatabaseItem;

/**
 * Created by derushio on 15/05/04.
 */
public class AedDatabaseManager {

	private static final String TABLE_NAME = "AED_DATABASE";
	private static final int TABLE_VERSION = 1;

	private AedDatabaseHelper mAedDatabaseHelper;
	private SQLiteDatabase mAedDatabase;

	public AedDatabaseManager(Context context) {
		mAedDatabaseHelper = new AedDatabaseHelper(context, TABLE_NAME, null, TABLE_VERSION);
		mAedDatabase = mAedDatabaseHelper.getWritableDatabase();
	}

	public Cursor getAllDataCursor(DatabaseItem orderByItem) {
		String orderBy = orderByItem.name + " ASC";
		return mAedDatabase.query(TABLE_NAME, null, null, null, null, null, orderBy);
	}

	public long addAedData(String title, String address, LatLng latLng, List<Uri> photoUris, String description) {
		return mAedDatabase.insert(TABLE_NAME, "", makeContentValues(title, address, latLng, photoUris, description));
	}

	public long addAedData(String title) {
		return addAedData(title, null, null, null, null);
	}

	public void updateMemoData(long id, String title, String address, LatLng latLng, List<Uri> photoUris, String description) {
		String whereCause = mAedDatabaseHelper.itemId.name + "==?";
		String whereCauseArgs[] = {("" + id)};

		mAedDatabase.update(TABLE_NAME, makeContentValues(title, address, latLng, photoUris, description), whereCause, whereCauseArgs);
	}

	public void deleteAedData(long id) {
		String whereCause = mAedDatabaseHelper.itemId.name + "==?";
		String whereCauseArgs[] = {("" + id)};
		mAedDatabase.delete(TABLE_NAME, whereCause, whereCauseArgs);
	}

	private ContentValues makeContentValues(String title, String address, LatLng latLng, List<Uri> photoUris, String description) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(mAedDatabaseHelper.itemTitle.name, title);

		if (address != null) {
			contentValues.put(mAedDatabaseHelper.itemAddress.name, address);
		}
		if (latLng != null) {
			contentValues.put(mAedDatabaseHelper.itemLatLng.name, latLngStatementGenerator(latLng));
		}
		if (photoUris != null) {
			contentValues.put(mAedDatabaseHelper.itemPhotoUris.name, photoUrisStatementGenerator(photoUris));
		}
		if (description != null) {
			contentValues.put(mAedDatabaseHelper.itemDescription.name, description);
		}

		return contentValues;
	}

	private String latLngStatementGenerator(LatLng latLng) {
		return latLng.latitude + "," + latLng.longitude;
	}

	private String photoUrisStatementGenerator(List<Uri> photoUris) {
		String statement = "";

		Log.d("Urisize", "" + photoUris.size());

		for (int i = 0; i < photoUris.size(); i++) {
			statement += photoUris.get(i).getPath() + ",";
		}

		return statement;
	}

	public Cursor findRecordById(long id) {
		String selection = mAedDatabaseHelper.itemId.name + "==?";
		String selectionArgs[] = {("" + id)};
		return mAedDatabase.query(TABLE_NAME, null, selection, selectionArgs, null, null, null);
	}

	public String findTitleById(long id) {
		Cursor cursor = findRecordById(id);
		cursor.moveToFirst();

		return cursor.getString(mAedDatabaseHelper.itemTitle.index);
	}

	public String findAddressById(long id) {
		Cursor cursor = findRecordById(id);
		cursor.moveToFirst();

		return cursor.getString(mAedDatabaseHelper.itemAddress.index);
	}

	public LatLng findLatLngById(long id) {

		Cursor cursor = findRecordById(id);
		cursor.moveToFirst();

		String latLngStatement = cursor.getString(mAedDatabaseHelper.itemLatLng.index);

		if (latLngStatement != null) {
			float lat = Float.parseFloat(latLngStatement.substring(0, latLngStatement.indexOf(",")));
			float lng = Float.parseFloat(latLngStatement.substring(latLngStatement.indexOf(",") + 1, latLngStatement.length()));
			Log.d("glog", "" + lat + lng);
			return new LatLng(lat, lng);
		}

		return null;
	}

	public List<Uri> findPhotoUrisById(long id) {
		Cursor cursor = findRecordById(id);
		cursor.moveToFirst();

		String pictureUrisStatement = cursor.getString(mAedDatabaseHelper.itemPhotoUris.index);

		if (pictureUrisStatement != null) {
			List<Uri> pictureUris = new ArrayList<>();
			while (!pictureUrisStatement.equals(",") && !pictureUrisStatement.equals("")) {
				Uri uri = Uri.parse(pictureUrisStatement.substring(0, pictureUrisStatement.indexOf(",")));
				pictureUris.add(uri);
				pictureUrisStatement = pictureUrisStatement.substring(pictureUrisStatement.indexOf(","), pictureUrisStatement.length());
			}

			Log.d("uris", "" + pictureUris.size());
			return pictureUris;
		}

		return null;
	}

	public String findDescriptionById(long id) {
		Cursor cursor = findRecordById(id);
		cursor.moveToFirst();

		return cursor.getString(mAedDatabaseHelper.itemDescription.index);
	}

	public AedDatabaseHelper getAedDatabaseHelper() {
		return mAedDatabaseHelper;
	}
}
