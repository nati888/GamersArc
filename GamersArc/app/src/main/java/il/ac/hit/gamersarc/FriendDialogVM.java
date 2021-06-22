package il.ac.hit.gamersarc;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;


public class FriendDialogVM extends ViewModel {

    private String friendId;
    MutableLiveData<String> runningLevelLiveData = new MutableLiveData<>();
    MutableLiveData<String> nameLiveData = new MutableLiveData<>();
    MutableLiveData<String> genderLiveData = new MutableLiveData<>();
    MutableLiveData<Uri> imageUriLiveData = new MutableLiveData<>();
    private ArrayList<User> mutualFriends = new ArrayList<>();
    MutableLiveData<ArrayList<User>> mutualFriendsLiveData = new MutableLiveData<>();
    MutableLiveData<String> addressLiveData = new MutableLiveData<>();
    private ArrayList<String> friendFriendsIds = new ArrayList<>();
    private ArrayList<String> userFriendsIds = new ArrayList<>();
    private DataBaseClass dataBaseClass = DataBaseClass.getInstance();
    private RegisterClass registerClass = RegisterClass.getInstance();
    private UserInstance userInstance = UserInstance.getInstance();

    public FriendDialogVM(String friendId) {
       this.friendId = friendId;
       getFriendUser();

        getFriendImageUri();
       getFriendFriendsIdsList();
    }

    private void getFriendFriendsIdsList() {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendFriendsIds.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    friendFriendsIds.add(snapshot1.getKey());
                }
                friendFriendsIds.remove("false");
                getUserFriendsIds();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        dataBaseClass.retrieveFriendsIds(friendId, listener);
    }

    private void getUserFriendsIds() {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userFriendsIds.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    userFriendsIds.add(snapshot1.getKey());
                }
                userFriendsIds.remove("false");
                findMutualFriends();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        dataBaseClass.retrieveFriendsIds(registerClass.getUserId(), listener);

    }

    private void findMutualFriends() {
        final ArrayList<String> mutualFriendsIds = new ArrayList<>(userFriendsIds);
        mutualFriendsIds.retainAll(friendFriendsIds);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    mutualFriends.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()){
                        if (mutualFriendsIds.contains(snapshot1.getKey())){
                            User user = snapshot1.getValue(User.class);
                            mutualFriends.add(user);
                        }
                    }
                    mutualFriendsLiveData.setValue(mutualFriends);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dataBaseClass.retrieveAllUsersList(listener);
    }


    private void getFriendImageUri(){
        OnSuccessListener<Uri> listener = new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
               imageUriLiveData.setValue(uri);
            }
        };

        dataBaseClass.getImageUserId(friendId, listener);
    }

    private void getFriendUser() {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    User friendUser = snapshot.getValue(User.class);
                    int age = getAge(friendUser.getYear(), friendUser.getMonth(), friendUser.getDayOfMonth());
                    String name = friendUser.getFullName();

                    double longitude = friendUser.getLongitude();
                    double latitude = friendUser.getLatitude();
                    double distance = haversine(latitude, longitude, userInstance.getUser().getLatitude(), userInstance.getUser().getLongitude());
                    int meters = (int)(distance * 1000);
                    addressLiveData.setValue(meters + "");
                    nameLiveData.setValue(name + ", " + age);
                    genderLiveData.setValue(friendUser.getGender());
                    runningLevelLiveData.setValue(friendUser.getRunningLevel());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dataBaseClass.retrieveUserById(friendId, listener);

    }

    public MutableLiveData<String> getRunningLevelLiveData(){
        return runningLevelLiveData;
    }

    public MutableLiveData<String> getNameLiveData(){
        return nameLiveData;
    }

    public MutableLiveData<String> getGenderLiveData(){
        return genderLiveData;
    }



    public MutableLiveData<Uri> getImageUriLiveData (){
      return imageUriLiveData;
    }

    public MutableLiveData<ArrayList<User>> getMutualFriendsLiveData(){
        return mutualFriendsLiveData;
    }

    public MutableLiveData<String> getAddressLiveData(){
        return addressLiveData;
    }

    private int getAge(int year, int month, int day){
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

    private double haversine(double lat1, double lon1,
                             double lat2, double lon2)
    {
        // distance between latitudes and longitudes
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        // convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // apply formulae
        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.pow(Math.sin(dLon / 2), 2) *
                        Math.cos(lat1) *
                        Math.cos(lat2);
        double rad = 6371;
        double c = 2 * Math.asin(Math.sqrt(a));
        return rad * c; //distance in km
    }

}
