package com.kourou.fouraccessq;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kourou.fouraccessq.FoursquareApp.FsqAuthListener;

/**
 * This is an example on how to connect Foursquare from Android app using a
 * dialog based authentication.
 * 
 * To get access to Foursquare API endpoints, first we have to get access token
 * by clicking on the Connect button. If success, the access token will be saved
 * on shared preference then can be used later to access API endpoints. In this
 * example, we use the token to get venues near the specific location by filling
 * the latitude and longitude textbox.
 * 
 * @author Lorensius W. L. T <lorenz@londatiga.net>
 * 
 */
public class Main extends Activity {
	private FoursquareApp mFsqApp;
	private ListView mListView;
	private NearbyAdapter mAdapter;
	private ArrayList<FsqVenue> mNearbyList;
	private ProgressDialog mProgress;

	public static final String CLIENT_ID = "E15YIJFE0MWKCEGT53JZK4V4IDMOI3OKUQNY43XTLUI0DJFZ";
	public static final String CLIENT_SECRET = "QUKIDPXTX5T55LAV3QTVGHTXWN2GGKS5ASRTFK2YEYPQF32F";
	double lat;
	double lon;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		final TextView nameTv = (TextView) findViewById(R.id.tv_name);
		Button connectBtn = (Button) findViewById(R.id.b_connect);

		Button goBtn = (Button) findViewById(R.id.b_go);
		mListView = (ListView) findViewById(R.id.lv_places);

		mFsqApp = new FoursquareApp(this, CLIENT_ID, CLIENT_SECRET);

		mAdapter = new NearbyAdapter(this);
		mNearbyList = new ArrayList<FsqVenue>();
		mProgress = new ProgressDialog(this);

		mProgress.setMessage("Loading data ...");

		LocationManager manager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		LocationListener loclistener = new LocationListener() {

			@Override
			public void onLocationChanged(Location location) {
				lat = location.getLatitude();
				lon = location.getLongitude();
			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub

			}

		};

		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 100,
				loclistener);

		if (mFsqApp.hasAccessToken())
			nameTv.setText("Connected as " + mFsqApp.getUserName());

		FsqAuthListener listener = new FsqAuthListener() {
			@Override
			public void onSuccess() {
				Toast.makeText(Main.this,
						"Connected as " + mFsqApp.getUserName(),
						Toast.LENGTH_SHORT).show();
				nameTv.setText("Connected as " + mFsqApp.getUserName());
			}

			@Override
			public void onFail(String error) {
				Toast.makeText(Main.this, error, Toast.LENGTH_SHORT).show();
			}
		};

		mFsqApp.setListener(listener);

		// get access token and user name from foursquare
		connectBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mFsqApp.authorize();
			}
		});

		// use access token to get nearby places
		goBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				loadNearbyPlaces(lat, lon);
			}
		});
	}

	private void loadNearbyPlaces(final double latitude, final double longitude) {
		mProgress.show();

		new Thread() {
			@Override
			public void run() {
				int what = 0;

				try {
					mNearbyList = mFsqApp.getNearby(latitude, longitude);
				} catch (Exception e) {
					what = 1;
					e.printStackTrace();
				}

				mHandler.sendMessage(mHandler.obtainMessage(what));
			}
		}.start();
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mProgress.dismiss();

			if (msg.what == 0) {
				if (mNearbyList.size() == 0) {
					Toast.makeText(Main.this, "No nearby places available",
							Toast.LENGTH_SHORT).show();
					return;
				}

				mAdapter.setData(mNearbyList);
				mListView.setAdapter(mAdapter);
			} else {
				Toast.makeText(Main.this, "Failed to load nearby places",
						Toast.LENGTH_SHORT).show();
			}
		}
	};
}