package il.ac.hit.gamersarc;

import android.content.Context;
import android.location.Geocoder;
import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

public class ProfileVM extends ViewModel /*implements DataBaseClass.OnGetUserImage*/ {
    private DataBaseClass dataBaseClass = DataBaseClass.getInstance();
    private MutableLiveData<Uri> liveDataImage = new MutableLiveData<>();
    private MutableLiveData<User> liveDataUser = new MutableLiveData<>();
    private UserInstance userInstance;

    public ProfileVM() {

        this.userInstance = UserInstance.getInstance();
        liveDataUser.setValue(userInstance.getUser());
    }

    public void setNewUser(){
        liveDataUser.setValue(userInstance.getUser());
    }

    public MutableLiveData<User> getLiveDataUser() {
        return liveDataUser;
    }

    public User getUser() {
        return userInstance.getUser();
    }

    public String getAddress(Context context, double latitude, double longitude){
        if(latitude!=0 || longitude!=0){
            Geocoder geocoder = new Geocoder(context , Locale.getDefault());
            try {
                return geocoder.getFromLocation(latitude,longitude,1).get(0).getLocality();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "";
    }

    public int getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = Integer.valueOf(age);


        return ageInt;
    }

    public void getImageFromData(){
        OnSuccessListener<Uri> listener = new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                liveDataImage.setValue(uri);
            }
        };
        dataBaseClass.getImageUserId(userInstance.getUser().getUserId(), listener);
    }

    public MutableLiveData<Uri> getImageLivedata() {
        return liveDataImage;
    }


}
