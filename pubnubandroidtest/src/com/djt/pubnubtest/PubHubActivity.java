package com.djt.pubnubtest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;

public class PubHubActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	private static final List<String> CHANNELS = createChannelList();
	
	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	Pubnub pubnub;
	SharedPreferences prefs;
	Context context;
	public static String SENDER_ID;
	public static String REG_ID;
	private static final String APP_VERSION = "3.6.1";

	static String PUBLISH_KEY = "pub-c-9e5804a3-89ba-47a6-b0a9-d83bd992a0a2";
	static String SUBSCRIBE_KEY = "sub-c-a79867ae-7e1e-11e3-a993-02ee2ddab7fe";
	static String CIPHER_KEY = "";
	static String SECRET_KEY = "";
	static String ORIGIN = "pubsub";
	static String AUTH_KEY;
	static 	String UUID;
	Boolean SSL = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pub_hub);
		prefs = getSharedPreferences(
                "PUBNUB_DEV_CONSOLE", Context.MODE_PRIVATE);
		init();
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
	}

	private static List<String> createChannelList() {
		List<String> channels = new ArrayList<String>(3);
		channels.add("NTP_DEV");
		channels.add("NTP_UAT");
		channels.add("NTP_PROD");
		return Collections.unmodifiableList(channels);
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager
				.beginTransaction()
				.replace(R.id.container,
						PlaceholderFragment.newInstance(position + 1)).commit();
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		}
		subscribe(number);
	}

	private void subscribe(int sectionNumber) {

		if (sectionNumber > CHANNELS.size()) {
			// default to first section
			sectionNumber=1;
		}
		
        String channel = CHANNELS.get(sectionNumber-1);

        try {
            pubnub.subscribe(channel, new Callback() {
                @Override
                public void connectCallback(String channel,
                                            Object message) {
                    notifyUser("SUBSCRIBE : CONNECT on channel:"
                            + channel
                            + " : "
                            + message.getClass()
                            + " : "
                            + message.toString());
                }

                @Override
                public void disconnectCallback(String channel,
                                               Object message) {
                    notifyUser("SUBSCRIBE : DISCONNECT on channel:"
                            + channel
                            + " : "
                            + message.getClass()
                            + " : "
                            + message.toString());
                }

                @Override
                public void reconnectCallback(String channel,
                                              Object message) {
                    notifyUser("SUBSCRIBE : RECONNECT on channel:"
                            + channel
                            + " : "
                            + message.getClass()
                            + " : "
                            + message.toString());
                }

                @Override
                public void successCallback(String channel,
                                            Object message) {
                    notifyUser("SUBSCRIBE : " + channel + " : "
                            + message.getClass() + " : "
                            + message.toString());
                }

                @Override
                public void errorCallback(String channel,
                                          PubnubError error) {
                    notifyUser("SUBSCRIBE : ERROR on channel "
                            + channel + " : "
                            + error.toString());
                }
            }, 1000);

        } catch (Exception e) {
        	Log.e("SUBERR", e.getMessage());
        }
    
	}
	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.pub_hub, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_pub_hub,
					container, false);
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((PubHubActivity) activity).onSectionAttached(getArguments()
					.getInt(ARG_SECTION_NUMBER));
		}
	}

	private void notifyUser(Object message) {
		try {
			if (message instanceof JSONObject) {
				final JSONObject obj = (JSONObject) message;
				this.runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(getApplicationContext(), obj.toString(),
								Toast.LENGTH_LONG).show();

						Log.i("Received msg : ", String.valueOf(obj));
					}
				});

			} else if (message instanceof String) {
				final String obj = (String) message;
				this.runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(getApplicationContext(), obj,
								Toast.LENGTH_LONG).show();
						Log.i("Received msg : ", obj.toString());
						setFragmentTextView(obj);
					}

				});

			} else if (message instanceof JSONArray) {
				final JSONArray obj = (JSONArray) message;
				this.runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(getApplicationContext(), obj.toString(),
								Toast.LENGTH_LONG).show();
						Log.i("Received msg : ", obj.toString());
					}
				});
			} else {
				Log.e("SUBSCRIBE", message!=null ? message.toString() : "null");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void init() {

		Map<String, String> map = getCredentials();

		// PUBLISH_KEY = map.get("PUBLISH_KEY");
		// SUBSCRIBE_KEY = map.get("SUBSCRIBE_KEY");
		// SECRET_KEY = map.get("SECRET_KEY");
		// CIPHER_KEY = map.get("CIPHER_KEY");
		// SSL = (map.get("SSL") == "true")?true:false;
		// SENDER_ID = map.get("SENDER_ID");
		// AUTH_KEY = map.get("AUTH_KEY");
		// ORIGIN = map.get("ORIGIN");
		// REG_ID = map.get("REG_ID");

		// The following hardcodes this demo app to run against our beta
		// environment and config.

		// PUBLISH_KEY = "demo-36";
		// SUBSCRIBE_KEY = "demo-36";
		// SECRET_KEY = "demo-36";
		// CIPHER_KEY = map.get("CIPHER_KEY");
		// SSL = (map.get("SSL") == "true")?true:false;
		// SENDER_ID = map.get("SENDER_ID");
		// AUTH_KEY = map.get("AUTH_KEY");
		// ORIGIN = "dara24.devbuild";
		// REG_ID = map.get("REG_ID");
		// SENDER_ID = "506053237730";

		pubnub = new Pubnub(PUBLISH_KEY, SUBSCRIBE_KEY, SECRET_KEY, CIPHER_KEY,
				SSL);
		pubnub.setCacheBusting(false);
		pubnub.setOrigin(ORIGIN);
		pubnub.setAuthKey(AUTH_KEY);

		// A SENDER_ID corresponds with a Server API Key with GCM.
		// The above Sender ID (506053237730) corresponds to this Server API
		// Key:
		// AIzaSyBNHRBzCKW9oUtTItl9qmLEVmRgG4SBys4

		// If you use the PubNub demo-36 API keys, we've already associated it
		// on the server-side,
		// you can use this Sender ID in your demo app without needing to config
		// anything server-side (with Google or PubNub)

		// If you want to use your own keys, you can use this SenderID,
		// But you will need to upload AIzaSyBNHRBzCKW9oUtTItl9qmLEVmRgG4SBys4
		// as your
		// GCM API Key to your Web Portal

		// Or, you use your own PN keyset, replace the above SENDER_ID with your
		// own Sender ID, and upload to the web
		// portal your own associated Server API Key

		// More info on this process here:
		// http://developer.android.com/google/gcm/gs.html

	}

	  private void saveCredentials() {
	        SharedPreferences.Editor editor = prefs.edit();
	        editor.putString("PUBLISH_KEY", PUBLISH_KEY);
	        editor.putString("SUBSCRIBE_KEY", SUBSCRIBE_KEY);
	        editor.putString("SECRET_KEY", SECRET_KEY);
	        editor.putString("AUTH_KEY", AUTH_KEY);
	        editor.putString("CIPHER_KEY", CIPHER_KEY);
	        editor.putString("ORIGIN", ORIGIN);
	        editor.putString("UUID", UUID);
	        editor.putString("SSL", SSL.toString());
	        editor.putString("SENDER_ID", SENDER_ID);
	        editor.commit();
	    }

	    private Map<String, String> getCredentials() {
	        Map<String, String> map = new LinkedHashMap<String, String>();
	        map.put("PUBLISH_KEY", prefs.getString("PUBLISH_KEY", PUBLISH_KEY));
	        map.put("SUBSCRIBE_KEY", prefs.getString("SUBSCRIBE_KEY", SUBSCRIBE_KEY));
	        map.put("SECRET_KEY", prefs.getString("SECRET_KEY", SECRET_KEY));
	        map.put("CIPHER_KEY", prefs.getString("CIPHER_KEY", CIPHER_KEY));
	        map.put("AUTH_KEY", prefs.getString("AUTH_KEY", null));
	        map.put("ORIGIN", prefs.getString("ORIGIN", "pubsub"));
	        map.put("UUID", prefs.getString("UUID", null));
	        map.put("SSL", prefs.getString("SSL", "false"));
	        map.put("SENDER_ID", prefs.getString("SENDER_ID", null));
	        return map;
	    }
	    
		private void setFragmentTextView(String obj) {
			((TextView)findViewById(R.id.section_label)).setText(obj);
		}

}
