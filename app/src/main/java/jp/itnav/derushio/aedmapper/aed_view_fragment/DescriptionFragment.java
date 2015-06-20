package jp.itnav.derushio.aedmapper.aed_view_fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import jp.itnav.derushio.aedmapper.R;

/**
 * Created by derushio on 15/05/13.
 */
public class DescriptionFragment extends Fragment {
	private View mRootView;
	private EditText mEditDescription;
	private String mInitDescription;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		if (mRootView != null) {
			ViewGroup parent = (ViewGroup) mRootView.getParent();
			if (parent != null) {
				parent.removeView(mRootView);
			}
		}

		try {
			if (mRootView == null) {
				mRootView = inflater.inflate(R.layout.fragment_description, container, false);
			}

			if (mEditDescription == null) {
				mEditDescription = (EditText) mRootView.findViewById(R.id.edit_discription);
				if (mInitDescription != null) {
					mEditDescription.setText(mInitDescription);
					mInitDescription = null;
				}
			}

			return mRootView;
		} catch (InflateException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void setDescription(String description) {
		if (mEditDescription != null) {
			mEditDescription.setText(description);
		} else {
			mInitDescription = description;
		}
	}

	public String getDescription() {
		if (mEditDescription != null) {
			return mEditDescription.getText().toString();
		} else if (mInitDescription != null) {
			return mInitDescription;
		} else {
			return null;
		}
	}
}
