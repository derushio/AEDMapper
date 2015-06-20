package jp.itnav.derushio.aedmapper.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import jp.itnav.derushio.sqldatabaselib.DatabaseHelperBase;
import jp.itnav.derushio.sqldatabaselib.DatabaseItem;

/**
 * Created by derushio on 15/05/02.
 */
public class AedDatabaseHelper extends DatabaseHelperBase {
	public DatabaseItem itemTitle = new DatabaseItem("title", DatabaseItem.TYPE_TEXT);
	public DatabaseItem itemAddress = new DatabaseItem("address", DatabaseItem.TYPE_TEXT);
	public DatabaseItem itemLatLng = new DatabaseItem("latlng", DatabaseItem.TYPE_TEXT);
	public DatabaseItem itemPhotoUris = new DatabaseItem("item_photos", DatabaseItem.TYPE_TEXT);
	public DatabaseItem itemDescription = new DatabaseItem("description", DatabaseItem.TYPE_TEXT);

	public AedDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
		super(context, name, factory, version);

		mDatabaseItems.add(itemTitle);
		mDatabaseItems.add(itemAddress);
		mDatabaseItems.add(itemLatLng);
		mDatabaseItems.add(itemPhotoUris);
		mDatabaseItems.add(itemDescription);
	}
}
