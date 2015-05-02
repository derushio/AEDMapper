package jp.itnav.derushio.aedmapper;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import jp.itnav.derushio.aedmapper.adapter.ViewPagerAdapter;


public class AedViewActivity extends AppCompatActivity {

	// View
	private Toolbar mToolbar;
	private TabWidget mTabWidget;
	private View mIndicator;
	private ViewPager mViewPager;
	// View

	// ViewPagerClass
	private FragmentManager mFragmentManager;
	private FragmentPagerAdapter mFragmentPagerAdapter;
	// ViewPagerClass

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_aed_view);

		initializeToolbar();

		mFragmentManager = getSupportFragmentManager();
		mFragmentPagerAdapter = new ViewPagerAdapter(this, mFragmentManager);

		TabHost tabHost = (TabHost) findViewById(R.id.tabhost);

		mTabWidget = (TabWidget) findViewById(android.R.id.tabs);
		mIndicator = findViewById(R.id.indicator);

		for (int i = 0; i < mFragmentPagerAdapter.getCount(); i++) {
			TextView textView = (TextView) LayoutInflater.from(this).inflate(R.layout.tab_textview, null);
			textView.setText(mFragmentPagerAdapter.getPageTitle(i));
			tabHost.addTab(tabHost.newTabSpec(String.valueOf(i)).setIndicator(textView).setContent(android.R.id.tabcontent));
		}

		tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				mViewPager.setCurrentItem(Integer.valueOf(tabId));
			}
		});

		mViewPager.setOnPageChangeListener(new PageChangeListener());
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
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
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
