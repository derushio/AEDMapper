package jp.itnav.derushio.aedmapper.adapter;

import android.net.Uri;
import android.view.View;

/**
 * Created by derushio on 15/05/05.
 */
public class PhotoDataSet {
	private Uri mUri;
	private View.OnClickListener mOnClickListener;

	public PhotoDataSet(Uri uri, View.OnClickListener onClickListener) {
		mUri = uri;
		mOnClickListener = onClickListener;
	}

	public Uri getUri() {
		return mUri;
	}

	public View.OnClickListener getOnClickListener() {
		return mOnClickListener;
	}
}
