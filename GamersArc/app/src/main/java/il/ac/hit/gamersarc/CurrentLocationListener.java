package il.ac.hit.gamersarc;

import android.content.Context;
import android.location.Location;

import androidx.lifecycle.LiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class CurrentLocationListener extends LiveData<Location> {

    private static com.example.runtime.CurrentLocationListener instance;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;

    public static com.example.runtime.CurrentLocationListener getInstance(Context appContext) {
        if (instance == null) {
            instance = new com.example.runtime.CurrentLocationListener(appContext);
        }
        return instance;
    }

    private CurrentLocationListener(final Context appContext) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(appContext);
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){

                    setValue(location);}

            }
        });
        createLocationRequest();
    }


    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(10000);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onActive() {
        super.onActive();
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (location != null)
                    setValue(location);
            }
        }
    };

    @Override
    protected void onInactive() {
        super.onInactive();
        if (mLocationCallback != null)
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

}
