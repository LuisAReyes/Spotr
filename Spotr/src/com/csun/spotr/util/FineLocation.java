package com.csun.spotr.util;

import java.util.Timer;
import java.util.TimerTask;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class FineLocation {
	private static final String TAG = "(FineLocation)";
	Timer clock;
	LocationManager lm;
	LocationResult locationResult;
	boolean gpsEnabled = false;
	boolean networkEnabled = false;

	public boolean getLocation(Context context, LocationResult result) {
		locationResult = result;
		if (lm == null)
			lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		// exceptions will be thrown if provider is not permitted.
		try {
			gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		}
		catch (Exception ex) {
			Log.e(TAG, ex.getMessage());
		}
		
		try {
			networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		}
		catch (Exception ex) {
			Log.e(TAG, ex.getMessage());
		}

		// don't start listeners if no provider is enabled
		if (!gpsEnabled && !networkEnabled)
			return false;

		if (gpsEnabled)
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsListener);
		if (networkEnabled)
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, networkListener);
		
		clock = new Timer();
		clock.schedule(new GetLastLocation(), 20000);
		return true;
	}

	LocationListener gpsListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			clock.cancel();
			locationResult.gotLocation(location);
			lm.removeUpdates(this);
			lm.removeUpdates(networkListener);
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	LocationListener networkListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			clock.cancel();
			locationResult.gotLocation(location);
			lm.removeUpdates(this);
			lm.removeUpdates(gpsListener);
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	class GetLastLocation extends TimerTask {
		@Override
		public void run() {
			lm.removeUpdates(gpsListener);
			lm.removeUpdates(networkListener);
			Location netLoc = null;
			Location gpsLoc = null;
			
			if (gpsEnabled)
				gpsLoc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			
			if (networkEnabled)
				netLoc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

			// if there are both values use the latest one
			if (gpsLoc != null && netLoc != null) {
				if (gpsLoc.getTime() > netLoc.getTime())
					locationResult.gotLocation(gpsLoc);
				else
					locationResult.gotLocation(netLoc);
				return;
			}

			if (gpsLoc != null) {
				locationResult.gotLocation(gpsLoc);
				return;
			}
			if (netLoc != null) {
				locationResult.gotLocation(netLoc);
				return;
			}
			
			locationResult.gotLocation(null);
		}
	}

	public static abstract class LocationResult {
		public abstract void gotLocation(Location location);
	}
}