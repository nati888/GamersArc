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
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StrangerVM extends AndroidViewModel {

    DataBaseClass dataBaseClass = DataBaseClass.getInstance();
    UserInstance userInstance = UserInstance.getInstance();
    private User currentUser = userInstance.getUser() ;
    private final String API_TOKEN_KEY = "AAAA1Zp43-k:APA91bG2LGRbDitz5cKuyq8NhaWiCa_SCMx_CdVm2MeAwFTxCJpEpf1QMZt2hecz0etzk2ggiTp9np0EwCEfeuO-JQpknA5Wc7MbnUwj80rq9gtcvzR_E1b0s6_MnBL7D9JOrs0YjPh5";

    //stranger display
    MutableLiveData<User> strangerUserForDisplay = new MutableLiveData<>();

    public StrangerVM(@NonNull Application application) {
        super(application);
    }

    public void fetchStrangerUser(String userId){
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User user = snapshot.getValue(User.class);
                    strangerUserForDisplay.setValue(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dataBaseClass.retrieveUserById(userId, listener);
    }

    public StorageReference getStrangerProfileImageReference(String strangerId){
        return dataBaseClass.retrieveImageStorageReference(strangerId);
    }

    public MutableLiveData<User> getStrangerUserForDisplay(){
        return strangerUserForDisplay;
    }

    public void onSendFriendRequest(String strangerId) {//from recycler

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
        JSONObject notificationObject = new JSONObject();
        notificationObject.put("title", "New friend request!");
        notificationObject.put("body", "don't  keep them waiting");
        rootObject.put("notification", notificationObject);

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
}
