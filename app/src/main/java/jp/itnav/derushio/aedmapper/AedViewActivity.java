package jp.itnav.derushio.aedmapper;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import jp.itnav.derushio.aedmapper.adapter.ViewPagerAdapter;
import jp.itnav.derushio.aedmapper.aed_view_fragment.DescriptionFragment;
import jp.itnav.derushio.aedmapper.aed_view_fragment.MapFragment;
import jp.itnav.derushio.aedmapper.aed_view_fragment.PhotosFragment;
import jp.itnav.derushio.aedmapper.database.AedDatabaseManager;


public class AedViewActivity extends AppCompatActivity {
	public static final String ID = "ID";
	public static final String LAT = "LAT";
	public static final String LNG = "LNG";

	private long mId;

	private AedDatabaseManager mAedDataBaseManager;

	// View
	private Toolbar mToolbar;
	private TabWidget mTabWidget;
	private View mIndicator;
	private ViewPager mViewPager;
	// View

	// ViewPagerClass
	private FragmentManager mFragmentManager;
	private ViewPagerAdapter mViewPagerAdapter;
	// ViewPagerClass

	private MapFragment mMapFragment;
	private PhotosFragment mPhotosFragment;
	private DescriptionFragment mDescriptionFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_aed_view);

		mAedDataBaseManager = new AedDatabaseManager(this);

		Intent intent = getIntent();
		mId = intent.getLongExtra(ID, -1);
		if (mId == -1) {
			mId = mAedDataBaseManager.addAedData("新規データ");
		}

		initializeToolbar();
		mViewPager = (ViewPager) findViewById(R.id.viewpager);

		mFragmentManager = getSupportFragmentManager();
		mViewPagerAdapter = new ViewPagerAdapter(this, mFragmentManager);

		mViewPager.setAdapter(mViewPagerAdapter);

		TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();

		mTabWidget = (TabWidget) findViewById(android.R.id.tabs);
		mIndicator = findViewById(R.id.indicator);

		for (int i = 0; i < mViewPagerAdapter.getCount(); i++) {
			TextView textView = (TextView) getLayoutInflater().inflate(R.layout.tab_textview, null);
			textView.setText(mViewPagerAdapter.getPageTitle(i));
			tabHost.addTab(tabHost.newTabSpec(String.valueOf(i)).setIndicator(textView).setContent(android.R.id.tabcontent));
		}

		tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				mViewPager.setCurrentItem(Integer.valueOf(tabId));
			}
		});

		mViewPager.setOnPageChangeListener(new PageChangeListener());

		mMapFragment = (MapFragment) mViewPagerAdapter.getItem(0);
		mPhotosFragment = (PhotosFragment) mViewPagerAdapter.getItem(1);
		mDescriptionFragment = (DescriptionFragment) mViewPagerAdapter.getItem(2);

		LatLng latLng = mAedDataBaseManager.findLatLngById(mId);
		if (latLng == null) {
			double lat = intent.getDoubleExtra(LAT, -1);
			double lng = intent.getDoubleExtra(LNG, -1);

			if (lat != -1 && lng != -1) {
				latLng = new LatLng(lat, lng);
				mMapFragment.setLatLng(latLng);
			}
		} else {
			mMapFragment.setLatLng(latLng);
		}

		if (mToolbar.getTitle() != null) {
			mMapFragment.setMarkerTitle(mToolbar.getTitle().toString());
		}

		String address = mAedDataBaseManager.findAddressById(mId);
		if (address != null) {
			mMapFragment.setAddress(address);
		}

		List<Uri> photoUriList = mAedDataBaseManager.findPhotoUrisById(mId);
		if (photoUriList != null) {
			mPhotosFragment.setPhotoUriList(photoUriList);
		}

		String description = mAedDataBaseManager.findDescriptionById(mId);
		if (description != null) {
			mDescriptionFragment.setDescription(description);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_aed_view, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_change_name) {
			final AppCompatDialog appCompatDialog = new AppCompatDialog(this);
			View view = LayoutInflater.from(this).inflate(R.layout.dialog_change_name, null, false);
			appCompatDialog.setContentView(view);
			appCompatDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

			final EditText editName = (EditText) view.findViewById(R.id.edit_name);
			Button buttonOk = (Button) view.findViewById(R.id.button_ok);
			Button buttonCancel = (Button) view.findViewById(R.id.button_cancel);

			editName.setText(mToolbar.getTitle().toString());

			buttonOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mToolbar.setTitle(editName.getText().toString());

					if (mToolbar.getTitle() != null) {
						mMapFragment.setMarkerTitle(mToolbar.getTitle().toString());
					}
					appCompatDialog.dismiss();
				}
			});

			buttonCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					appCompatDialog.dismiss();
				}
			});

			appCompatDialog.show();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		mAedDataBaseManager.updateMemoData(mId, mToolbar.getTitle().toString(), mMapFragment.getAddress(), mMapFragment.getLatLng(), mPhotosFragment.getPhotoUriList(), mDescriptionFragment.getDescription());
		Intent intent = new Intent(this, AedMapActivity.class);
		startActivity(intent);
		finish();
	}

	private void initializeToolbar() {
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		if (mToolbar == null) {
			throw new IllegalStateException("Layout is required to include a Toolbar with id " + "'toolbar'");
		}

		mToolbar.setTitle(mAedDataBaseManager.findTitleById(mId));
		mToolbar.setTitleTextColor(Color.WHITE);
		setSupportActionBar(mToolbar);
	}

	private class PageChangeListener implements ViewPager.OnPageChangeListener {
		private int scrollingState = ViewPager.SCROLL_STATE_IDLE;

		@Override
		public void onPageSelected(int position) {
			if (scrollingState == ViewPager.SCROLL_STATE_IDLE) {
				updateIndicatorPosition(position, 0);
			}
			mTabWidget.setCurrentTab(position);
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			scrollingState = state;
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			updateIndicatorPosition(position, positionOffset);
		}

		private void updateIndicatorPosition(int position, float positionOffset) {
			View tabView = mTabWidget.getChildTabViewAt(position);

			int indicatorWidth = tabView.getWidth();
			int indicatorLeft = (int) ((position + positionOffset) * indicatorWidth);

			final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mIndicator.getLayoutParams();
			layoutParams.width = indicatorWidth;
			layoutParams.setMargins(indicatorLeft, 0, 0, 0);
			mIndicator.setLayoutParams(layoutParams);
		}
	}
}
