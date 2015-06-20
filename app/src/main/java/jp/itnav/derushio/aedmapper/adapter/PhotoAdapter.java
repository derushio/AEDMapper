package jp.itnav.derushio.aedmapper.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.util.List;

import jp.itnav.derushio.aedmapper.R;
import jp.itnav.derushio.autophotosampling.AutoPhotoSampling;

/**
 * Created by derushio on 15/05/05.
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

	private Context mContext;
	private List<PhotoDataSet> mPhotoDataSetList;

	public PhotoAdapter(Context context, List<PhotoDataSet> photoDataSetList) {
		mContext = context;
		mPhotoDataSetList = photoDataSetList;
	}

	@Override
	public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(mContext).inflate(R.layout.cardview_photo, parent, false);
		return new PhotoHolder(view);
	}

	@Override
	public void onBindViewHolder(final PhotoHolder holder, int position) {
		final PhotoDataSet data = mPhotoDataSetList.get(position);

		Log.d("position", "" + position);

		holder.imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {

				if (data.getUri() != null) {
					AutoPhotoSampling.autoPhotoSampling(holder.imageView, data.getUri());
				} else {
					AutoPhotoSampling.autoPhotoSampling(holder.imageView, mContext.getResources(), R.mipmap.icon_photo);
				}
				holder.imageView.getViewTreeObserver().removeOnPreDrawListener(this);
				return false;
			}
		});
		holder.cardView.setOnClickListener(data.getOnClickListener());
	}

	@Override
	public int getItemCount() {
		return mPhotoDataSetList.size();
	}
}
