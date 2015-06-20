package jp.itnav.derushio.aedmapper;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import jp.itnav.derushio.aedmapper.adapter.CustomInfoAdapter;
import jp.itnav.derushio.aedmapper.database.AedDatabaseManager;

public class AedMapActivity extends AppCompatActivity {

	private Toolbar mToolbar;
	private GoogleMap mMap; // Might be null if Google Play services APK is not available.

	private CustomInfoAdapter mCustomInfoAdapter;

	private AedDatabaseManager mAedDatabaseManager;

	private ArrayList<IdHolder> mIdHolderList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_aed_map);

		mAedDatabaseManager = new AedDatabaseManager(this);
		mIdHolderList = new ArrayList<>();

		initializeToolbar();
		setUpMapIfNeeded();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
	}

	private void initializeToolbar() {
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		if (mToolbar == null) {
			throw new IllegalStateException("Layout is required to include a Toolbar with id " + "'toolbar'");
		}

		mToolbar.setTitle(getApplicationInfo().name);
		mToolbar.setTitleTextColor(Color.WHITE);
		setSupportActionBar(mToolbar);
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
			mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
					.getMap();
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
		mCustomInfoAdapter = new CustomInfoAdapter(this);
		mMap.setInfoWindowAdapter(mCustomInfoAdapter);
		mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker marker) {
				Intent intent = new Intent(AedMapActivity.this, AedViewActivity.class);
				int markerId = convertMarkerId(marker.getId());
				if (0 <= markerId && markerId < mIdHolderList.size()) {
					intent.putExtra(AedViewActivity.ID, mIdHolderList.get(convertMarkerId(marker.getId())).getId());
				}
				intent.putExtra(AedViewActivity.LAT, marker.getPosition().latitude);
				intent.putExtra(AedViewActivity.LNG, marker.getPosition().longitude);
				startActivity(intent);
			}
		});

		mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
			@Override
			public void onMyLocationChange(Location location) {
				mMap.setOnMyLocationChangeListener(null);

				LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
				CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(myLatLng, 16, 0, 0));
				mMap.animateCamera(cameraUpdate);
			}
		});

		mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
			@Override
			public void onMapLongClick(LatLng latLng) {
				MarkerOptions markerOptions = new MarkerOptions();
				markerOptions.draggable(true);
				markerOptions.position(latLng);
				markerOptions.title("新しいAED");
				mMap.addMarker(markerOptions);
			}
		});

		Cursor cursor = mAedDatabaseManager.getAllDataCursor(mAedDatabaseManager.getAedDatabaseHelper().itemId);
		cursor.moveToFirst();
		for (int i = 0; i < cursor.getCount(); i++) {
			long id = cursor.getLong(mAedDatabaseManager.getAedDatabaseHelper().itemId.index);
			String title = mAedDatabaseManager.findTitleById(id);
			LatLng latLng = mAedDatabaseManager.findLatLngById(id);

			if (latLng != null) {
				MarkerOptions markerOptions = new MarkerOptions();
				markerOptions.title(title);
				markerOptions.position(latLng);
				Marker marker = mMap.addMarker(markerOptions);
				mIdHolderList.add(convertMarkerId(marker.getId()), new IdHolder(id));
				cursor.moveToNext();
			} else {
				mAedDatabaseManager.deleteAedData(id);
			}
		}
	}

	private int convertMarkerId(String markerId) {
		return Integer.parseInt(markerId.substring(1, markerId.length()));
	}

	public class IdHolder {
		private long mId;

		public IdHolder(long id) {
			mId = id;
		}

		public long getId() {
			return mId;
		}
	}
}
