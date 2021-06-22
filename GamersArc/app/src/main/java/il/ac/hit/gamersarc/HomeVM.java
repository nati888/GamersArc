package il.ac.hit.gamersarc;

import android.content.Context;
import android.location.Geocoder;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.util.Locale;

public class HomeVM extends ViewModel implements DataBaseClass.OnLocationUpdateListener {

    private UserInstance userInstance = UserInstance.getInstance();
    private DataBaseClass dataBaseClass=DataBaseClass.getInstance();
    private MutableLiveData<String> location;
    private Context context;

    public HomeVM() {
        dataBaseClass.setUpdateLocationListener(this);
        location = new MutableLiveData<>();
    }



    public MutableLiveData<String> getLocation() {
        return location;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getAddress(Context context, double latitude, double longitude){
        if(context != null) {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            try {
                return geocoder.getFromLocation(latitude, longitude, 1).get(0).getLocality();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void onLocationUpdate() {
        Geocoder geocoder = new Geocoder(context,Locale.getDefault());
        try {
            location.setValue(geocoder.getFromLocation(userInstance.getUser().getLatitude(),userInstance.getUser().getLongitude(),1).get(0).getAddressLine(0));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
