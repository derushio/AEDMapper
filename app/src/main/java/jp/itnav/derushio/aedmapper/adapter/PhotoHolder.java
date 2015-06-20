package jp.itnav.derushio.aedmapper.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import jp.itnav.derushio.aedmapper.R;

/**
 * Created by derushio on 15/05/05.
 */
public class PhotoHolder extends RecyclerView.ViewHolder {
	public CardView cardView;
	public ImageView imageView;

	public PhotoHolder(View itemView) {
		super(itemView);

		cardView = (CardView)itemView;
		imageView = (ImageView)itemView.findViewById(R.id.imageview);
	}
}
