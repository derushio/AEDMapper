package jp.itnav.derushio.aedmapper.aed_view_fragment;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import jp.itnav.derushio.aedmapper.R;
import jp.itnav.derushio.aedmapper.adapter.PhotoAdapter;
import jp.itnav.derushio.aedmapper.adapter.PhotoDataSet;
import jp.itnav.derushio.photofilemanager.CameraAndAlbumResultFragment;

/**
 * Created by derushio on 15/05/05.
 */
public class PhotosFragment extends CameraAndAlbumResultFragment {
	private View mRootView;
	private RecyclerView mRecyclerView;
	private PhotoAdapter mPhotoAdapter;
	private List<PhotoDataSet> mPhotoDataSetList;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		if (mRootView != null) {
			ViewGroup parent = (ViewGroup) mRootView.getParent();
			if (parent != null) {
				parent.removeView(mRootView);
			}
		}

		try {
			if (mRootView == null) {
				mRootView = inflater.inflate(R.layout.fragment_photos, container, false);
			}

			if (mRecyclerView == null) {
				mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerview);

				LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
				mRecyclerView.setLayoutManager(linearLayoutManager);
				mRecyclerView.setHasFixedSize(true);
				mRecyclerView.setItemAnimator(new DefaultItemAnimator());

				if (mPhotoDataSetList == null) {
					mPhotoDataSetList = new ArrayList<>();
				}

				mPhotoAdapter = new PhotoAdapter(getActivity(), mPhotoDataSetList);
				mRecyclerView.setAdapter(mPhotoAdapter);

				mPhotoDataSetList.add(new PhotoDataSet(null, new OnCardClickListener()));

				mPhotoAdapter.notifyDataSetChanged();
			}

			return mRootView;
		} catch (InflateException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onCropFinished(File file) {
		try {
			File outImage = mPhotoFileManager.outputImage(BitmapFactory.decodeFile(file.getPath()), mPhotoFileManager.getOutputImageDir("AEDMapper"), null, false);
			mPhotoDataSetList.add(mPhotoDataSetList.size() - 1, new PhotoDataSet(Uri.fromFile(outImage), null));
			mPhotoAdapter.notifyDataSetChanged();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private class OnCardClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			startCamera();
		}
	}

	public void setPhotoUriList(List<Uri> photoUriList) {
		if (mPhotoDataSetList == null) {
			mPhotoDataSetList = new ArrayList<>();
		}

		for (Uri uri : photoUriList) {
			mPhotoDataSetList.add(new PhotoDataSet(uri, null));
		}

		if (mPhotoAdapter != null) {
			mPhotoAdapter.notifyDataSetChanged();
		}
	}

	public List<Uri> getPhotoUriList() {
		List<Uri> photoUriList = new ArrayList<>();

		for (PhotoDataSet photoDataSet : mPhotoDataSetList) {
			Uri photoUri = photoDataSet.getUri();
			if (photoUri != null) {
				photoUriList.add(photoUri);
			}
		}
		return photoUriList;
	}
}
