package jp.itnav.derushio.aedmapper.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by derushio on 15/05/02.
 */
public class AedDatabaseHelper extends SQLiteOpenHelper {
	private Context mContext;
	private String mName;
	private SQLiteDatabase.CursorFactory mFactory;
	private int mVersion;

	public AedDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
		super(context, name, factory, version);
		mContext = context;
		mName = name;
		mFactory = factory;
		mVersion = version;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
