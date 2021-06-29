package il.ac.hit.gamersarc;


import android.app.Application;
import android.location.Geocoder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FindPeopleVM extends AndroidViewModel {

    private MutableLiveData<ArrayList<User>> relevantUsers = new MutableLiveData<>();
    private MutableLiveData<ArrayList<String>> recentSentRequests = new MutableLiveData<>();
    private MutableLiveData<String> addressLiveData = new MutableLiveData<>();
    private ArrayList<String> recentSentRequestsArrayList = new ArrayList<>();
    private ArrayList<User> relevant = new ArrayList<>();
    private ArrayList<String> userFriendsIds = new ArrayList<>();
    private ArrayList<String> userFriendRequestsIds = new ArrayList<>();
    private User currentUser;
    private DataBaseClass dataBaseClass = DataBaseClass.getInstance();
    private ArrayList<User> usersFromDatabase = new ArrayList<>();
    private UserPreferences userPreferences;
    private final String API_TOKEN_KEY = "AAAA1Zp43-k:APA91bG2LGRbDitz5cKuyq8NhaWiCa_SCMx_CdVm2MeAwFTxCJpEpf1QMZt2hecz0etzk2ggiTp9np0EwCEfeuO-JQpknA5Wc7MbnUwj80rq9gtcvzR_E1b0s6_MnBL7D9JOrs0YjPh5";

    private MutableLiveData<Boolean> swipeLayoutBool = new MutableLiveData<>();

    public FindPeopleVM(@NonNull Application application) {
        super(application);
        retrieveUsersList();
        getSentRequests();
    }


    public void retrieveUsersList(){

        swipeLayoutBool.setValue(true);
        getAllUsersList();

    }

    //distance calculation
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
        double rad = 50000;
        double c = 2 * Math.asin(Math.sqrt(a));
        return rad * c; //distance in km
    }

    //age calculation
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


    public MutableLiveData<ArrayList<User>> getRelevantUsers(){
        //returning relevantUsersList
        return relevantUsers;
    }


    private void getAllUsersList(){

        usersFromDatabase.clear();
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot snapshot1 : snapshot.getChildren()){
                        User user = snapshot1.getValue(User.class);
                        usersFromDatabase.add(user);

                    }
                    getUserPreferences();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        //database method that gets this listener
        dataBaseClass.retrieveAllUsersList(listener);

    }



    private void getUserPreferences() {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    userPreferences = snapshot.getValue(UserPreferences.class);
                    getCurrentUser();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        //database method that gets this listener
        dataBaseClass.retrieveUserPreferences(listener);
    }

    private void getCurrentUser() {

        currentUser = UserInstance.getInstance().getUser();
        getFriendsIds();
        getUserAddress();
    }

    private void getUserAddress() {
        double longitude = currentUser.getLongitude();
        double latitude = currentUser.getLatitude();
        Geocoder geocoder = new Geocoder(getApplication().getApplicationContext(), Locale.getDefault());

        String address = "";
        try {
            if (geocoder.getFromLocation(latitude,longitude,1) != null){
                if (geocoder.getFromLocation(latitude,longitude,1).size() > 0){

                    address = geocoder.getFromLocation(latitude,longitude,1).get(0).getAddressLine(0);

                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        addressLiveData.setValue(address);
    }

    private void getFriendsIds(){
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userFriendsIds.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){

                    userFriendsIds.add(snapshot1.getKey());
                }
                userFriendsIds.remove("false");
                getFriendRequestsIds();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dataBaseClass.retrieveFriendsIds(currentUser.getUserId(), listener);
    }

    private void getFriendRequestsIds(){
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userFriendRequestsIds.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    userFriendRequestsIds.add(snapshot1.getKey());
                }
                userFriendRequestsIds.remove("false");
                findRelevantUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        dataBaseClass.retrieveFriendsRequestsIds(currentUser.getUserId(), listener);
    }

    private void findRelevantUsers() {

        relevant.clear();

        //user location
        double longitude = currentUser.getLongitude();
        double latitude = currentUser.getLatitude();
        //user preferences

        String preferredGender = userPreferences.getGender();

        String preferredLevel = userPreferences.getRuningLevel();
        int preferredFromAge = userPreferences.getFromAge();
        int preferredToAge = userPreferences.getToAge();

        if (preferredGender != null && preferredLevel != null && preferredFromAge != 0 && preferredToAge != 0){

            //choosing only relevant users from the list

            for (User user : usersFromDatabase){

                if (user.getGender() != null && user.getRunningLevel() != null && user.getUserId() != null){
                    int age = getAge(user.getYear(), user.getMonth(), user.getDayOfMonth());
                    double distance = haversine(user.getLatitude(), user.getLongitude(), latitude, longitude);


                    //also check if user not on friends list already!
                    if (!userFriendsIds.contains(user.getUserId()) && !userFriendRequestsIds.contains(user.getUserId())){

/*                        if((user.getGender().equals(preferredGender) || preferredGender.equals("both"))
                                && user.getRunningLevel().equals(preferredLevel)
                                && age >= preferredFromAge && age <= preferredToAge
                                && distance < 20
                                && !(user.getUserId().equals(currentUser.getUserId()))
                        ) {
                            relevant.add(user);

                        }*/
                        relevant.add(user);

                    }
                }


            }
        }


        relevantUsers.setValue(relevant);
    }

    public void getSentRequests(){

        recentSentRequestsArrayList.clear();

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot snapshot1 : snapshot.getChildren()){
                        recentSentRequestsArrayList.add(snapshot1.getKey());

                    }
                }

                updateSentRequestsMutable();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dataBaseClass.retrieveSentRequests(listener);


    }

    private void updateSentRequestsMutable() {
        recentSentRequests.setValue(recentSentRequestsArrayList);

    }


    //friend requests
    //updating list of recent requests that can be canceled

    public void onSendFriendRequest(final String strangerId) {//from recycler

        //update firebase
        dataBaseClass.updateSentFriendRequest(strangerId, currentUser.getUserId());
        //send fcm message
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String strangerToken = snapshot.getValue(String.class);
                    try {
                        createFriendRequestNotificationMessage(strangerToken);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dataBaseClass.retrieveUserToken(strangerId, listener);
    }

    private void createFriendRequestNotificationMessage(String token) throws JSONException {



        final JSONObject rootObject = new JSONObject();
        rootObject.put("to", token);
        JSONObject data = new JSONObject();
        String friendRequest = getApplication().getString(R.string.new_friend_request);
        String dontKeep = getApplication().getString(R.string.do_not_keep_wait);
        data.put("messageType", "friendRequest");
        data.put("title", friendRequest);
        data.put("body", dontKeep);
        rootObject.put("data", data);

        String url = "https://fcm.googleapis.com/fcm/send";

        RequestQueue queue = Volley.newRequestQueue(getApplication().getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public byte[] getBody() throws AuthFailureError {
                return rootObject.toString().getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "key=" + API_TOKEN_KEY);
                return headers;
            }
        };

        queue.add(request);
        queue.start();

    }

    public void onCancelFriendRequest(String strangerId) {//from recycler

        //update firebase
        dataBaseClass.updateCanceledFriendRequest(strangerId, currentUser.getUserId());

    }

    public MutableLiveData<ArrayList<String>> getRecentSentRequests(){
        return recentSentRequests;
    }

    public MutableLiveData<Boolean> getSwipeLayoutBool(){
        return swipeLayoutBool;
    }

    public MutableLiveData<String> getAddressLiveData(){
        return addressLiveData;
    }

}
