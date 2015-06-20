package jp.itnav.derushio.aedmapper.aed_view_fragment;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import jp.itnav.derushio.aedmapper.R;
import jp.itnav.derushio.aedmapper.adapter.CustomInfoAdapter;

/**
 * Created by derushio on 15/05/02.
 */
public class MapFragment extends Fragment {

	private GoogleMap mMap; // Might be null if Google Play services APK is not available.
	private View mRootView;
	private EditText mEditAddress;
	private Button mSetAddressButton;

	private CustomInfoAdapter mCustomInfoAdapter;

	private LatLng mLatLng;
	private Marker mMarker;
	private String mTitle;
	private String mAddress;

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
				mRootView = inflater.inflate(R.layout.fragment_map, container, false);
			}

			if (mEditAddress == null) {
				mEditAddress = (EditText) mRootView.findViewById(R.id.edit_address);

				if (mAddress != null) {
					mEditAddress.setText(mAddress);
				} else {
					Geocoder geocoder = new Geocoder(getActivity(), Locale.JAPAN);
					List<Address> addressList;
					if (mMarker != null) {
						try {
							addressList = geocoder.getFromLocation(mMarker.getPosition().latitude, mMarker.getPosition().longitude, 1);
							setAddress(addressList.get(0));
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else if (mLatLng != null) {
						try {
							addressList = geocoder.getFromLocation(mLatLng.latitude, mLatLng.longitude, 1);
							setAddress(addressList.get(0));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}

			if (mSetAddressButton == null) {
				mSetAddressButton = (Button) mRootView.findViewById(R.id.button_get_address);
				mSetAddressButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Geocoder geocoder = new Geocoder(getActivity(), Locale.JAPAN);
						List<Address> addressList;
						if (mMarker != null) {
							try {
								addressList = geocoder.getFromLocation(mMarker.getPosition().latitude, mMarker.getPosition().longitude, 1);
								setAddress(addressList.get(0));
							} catch (IOException e) {

							}
						}
					}
				});
			}

			setUpMapIfNeeded();

			return mRootView;
		} catch (InflateException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void onResume() {
		super.onResume();
		setUpMapIfNeeded();
	}

	/**
	 * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
	 * installed) and the map has not already been instantiated.. This will ensure that we only ever
	 * call {@link #setUpMap()} once when {@link #mMap} is not null.
	 * <p/>
	 * If it isn't installed {@link SupportMapFragment} (and
	 * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
	 * install/update the Google Play services APK on their device.
	 * <p/>
	 * A user can return to this FragmentActivity after following the prompt and correctly
	 * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
	 * have been completely destroyed during this process (it is likely that it would only be
	 * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
	 * method in {@link #onResume()} to guarantee that it will be called.
	 */
	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				setUpMap();
			}
		}
	}

	/**
	 * This is where we can add markers or lines, add listeners or move the camera. In this case, we
	 * just add a marker near Africa.
	 * <p/>
	 * This should only be called once and when we are sure that {@link #mMap} is not null.
	 */
	private void setUpMap() {
		mMap.setMyLocationEnabled(true);
		mCustomInfoAdapter = new CustomInfoAdapter(getActivity());
		mMap.setInfoWindowAdapter(mCustomInfoAdapter);
		mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
			@Override
			public void onMarkerDragStart(Marker marker) {

			}

			@Override
			public void onMarkerDrag(Marker marker) {

			}

			@Override
			public void onMarkerDragEnd(Marker marker) {
				mMarker = marker;
			}
		});

		mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
			@Override
			public void onMapLongClick(LatLng latLng) {
				mMap.clear();
				MarkerOptions markerOptions = new MarkerOptions();
				markerOptions.draggable(true);
				markerOptions.position(latLng);
				if (mTitle != null) {
					markerOptions.title(mTitle);
				}
				mMarker = mMap.addMarker(markerOptions);
			}
		});

		if (mLatLng != null) {
			MarkerOptions markerOptions = new MarkerOptions();
			markerOptions.draggable(true);
			markerOptions.position(mLatLng);
			mLatLng = null;
			if (mTitle != null) {
				markerOptions.title(mTitle);
			}
			mMarker = mMap.addMarker(markerOptions);

			CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(mMarker.getPosition(), 16, 0, 0));
			mMap.animateCamera(cameraUpdate);
		} else {
			mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
				@Override
				public void onMyLocationChange(Location location) {
					mMap.setOnMyLocationChangeListener(null);

					LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
					CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 16, 0, 0));
					mMap.animateCamera(cameraUpdate);
				}
			});
		}
	}

	public void setAddress(Address addressList) {
		String address = "";
		for (int i = 0; (addressList.getAddressLine(i)) != null; i++) {
			address += addressList.getAddressLine(i);
		}
		mEditAddress.setText(address);
	}

	public void setLatLng(LatLng latLng) {
		mLatLng = latLng;

		if (mMap != null) {
			CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(mLatLng, 16, 0, 0));
			mMap.animateCamera(cameraUpdate);
		}
	}

	public LatLng getLatLng() {
		return mMarker.getPosition();
	}

	public void setMarkerTitle(String title) {
		mTitle = title;
		if (mMarker != null) {
			mMarker.setTitle(mTitle);
			if (mMarker.isInfoWindowShown()) {
				mMarker.hideInfoWindow();
				mMarker.showInfoWindow();
			}
		}
	}

	public void setAddress(String address) {
		mAddress = address;
	}

	public String getAddress() {
		return mEditAddress.getText().toString();
	}
}