package jp.itnav.derushio.aedmapper.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by derushio on 15/05/02.
 * ご利用方法
 * 登録したいアイテムをDataBaseItemのpublic static finalで宣言して
 * mDarabaseItemsに突っ込んでもうだけです。
 */
public class DatabaseHelperBase extends SQLiteOpenHelper {
	private Context mContext;
	private String mName;
	private SQLiteDatabase.CursorFactory mFactory;
	private int mVersion;

	public static final DatabaseItem ITEM_ID = new DatabaseItem("id", DatabaseItem.INTEGER_PRIMARY_KEY_AUTOINCREMENT);

	protected List<DatabaseItem> mDatabaseItems;

	public DatabaseHelperBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
		super(context, name, factory, version);
		mContext = context;
		mName = name;
		mFactory = factory;
		mVersion = version;

		mDatabaseItems = new ArrayList<>();
		mDatabaseItems.add(ITEM_ID);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(getCreateTableStatement());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	private String getCreateTableStatement() {
		String createTableStatement = "CREATE TABLE " + mName + "(";
		for (int i = 0; i < mDatabaseItems.size(); i++) {
			if (i != 0) {
				createTableStatement += ",";
			}
			createTableStatement += mDatabaseItems.get(i).NAME + " " + mDatabaseItems.get(i).TYPE;
		}
		createTableStatement += ");";

		return createTableStatement;
	}
}
