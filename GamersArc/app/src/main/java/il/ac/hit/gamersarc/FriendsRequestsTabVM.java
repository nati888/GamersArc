package il.ac.hit.gamersarc;

import android.app.Application;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FriendsRequestsTabVM extends AndroidViewModel {

    private ArrayList<User> friendsRequests = new ArrayList<>();
    private ArrayList<String> friendsRequestsIds = new ArrayList<>();
    MutableLiveData<ArrayList<User>> friendsRequestsLiveData = new MutableLiveData<>();
    private DataBaseClass dataBaseClass = DataBaseClass.getInstance();
    private RegisterClass registerClass = RegisterClass.getInstance();
    private final String API_TOKEN_KEY = "AAAAfwvvO64:APA91bG6RWYJYEROIIoBMpzKm6kMdCbqDdqpzhynZ4YnFKEiQ0vu5QuLfJdGTtlixdzqBoL2Ul99A5Mf9kspOh8Whz9U-AY1-7rQTBiOUNUeYZM3UHh4A7Tm4Kb-u4Hrv98zApJn76NQ";
    private MutableLiveData<Boolean> swipeLayoutBool = new MutableLiveData<>();

    public FriendsRequestsTabVM(@NonNull Application application) {
        super(application);

    }


    public void getFriendsRequestsIds(){
        swipeLayoutBool.setValue(true);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendsRequestsIds.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    friendsRequestsIds.add(snapshot1.getKey());
                }
                friendsRequestsIds.remove("false");
                getFriendRequestUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        dataBaseClass.retrieveFriendsRequestsIds(registerClass.getUserId(), listener);

    }

    private void getFriendRequestUsers() {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendsRequests.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    if (friendsRequestsIds.contains(snapshot1.getKey())){
                        User user = snapshot1.getValue(User.class);
                        friendsRequests.add(user);
                    }
                }
                friendsRequestsLiveData.setValue(friendsRequests);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dataBaseClass.retrieveAllUsersList(listener);

    }

    public MutableLiveData<ArrayList<User>> getFriendsRequestsLiveData(){
        return friendsRequestsLiveData;
    }

    public void onRequestAccepted(String userId, String newFriendId, String fullName, String newFriendToken) throws JSONException {
        //database
        dataBaseClass.acceptFriendRequest(userId, newFriendId);
        getFriendsRequestsIds();
        //notification
        createNotificationJson(userId, fullName, newFriendToken);

    }

    private void createNotificationJson(String userId, String fullName, String newFriendToken) throws JSONException {
        final JSONObject rootObject = new JSONObject();
        rootObject.put("to", newFriendToken);
        final JSONObject data = new JSONObject();

        String accepted = getApplication().getApplicationContext().getString(R.string.accepted_your_request);
        String sendThemAMessage = getApplication().getApplicationContext().getString(R.string.send_them_a_message);
        data.put("messageType", "friendRequestAccepted");
        data.put("title", fullName + " " + accepted);
        data.put("body", sendThemAMessage);
        data.put("usedId", userId);
        rootObject.put("data", data);

        sendNotification(rootObject);

    }

    private void sendNotification(final JSONObject rootObject) {
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
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "key=" + API_TOKEN_KEY);
                return headers;

            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return rootObject.toString().getBytes();
            }
        };

        queue.add(request);
        queue.start();

    }

    public void onRequestRemoved(String userId, String strangerId){
        //database
        dataBaseClass.removeFriendRequest(userId, strangerId);
        getFriendsRequestsIds();
    }

    public MutableLiveData<Boolean> getSwipeLayoutBool(){
        return swipeLayoutBool;
    }
}
