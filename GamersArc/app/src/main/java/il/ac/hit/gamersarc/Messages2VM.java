package il.ac.hit.gamersarc;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import il.ac.hit.gamersarc.model.Message;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Messages2VM extends AndroidViewModel {
    private MutableLiveData<Uri> liveDataImage = new MutableLiveData<>();
    private MutableLiveData<String> liveDataFullName = new MutableLiveData<>();
    private DataBaseClass dataBaseClass = DataBaseClass.getInstance();
    private MutableLiveData<List<Message>> messageListLiveData;
    private User user = UserInstance.getInstance().getUser();
    private final String API_TOKEN_KEY = "AAAA1Zp43-k:APA91bG2LGRbDitz5cKuyq8NhaWiCa_SCMx_CdVm2MeAwFTxCJpEpf1QMZt2hecz0etzk2ggiTp9np0EwCEfeuO-JQpknA5Wc7MbnUwj80rq9gtcvzR_E1b0s6_MnBL7D9JOrs0YjPh5";


    private String activeConversationFriendId;

    public Messages2VM(@NonNull Application application) {
        super(application);
        messageListLiveData = new MutableLiveData<>();
    }

    public void setName(String name) {
        liveDataFullName.setValue(name);
    }

    public void setActiveConversationFriendId(String activeConversationFriendId) {
        this.activeConversationFriendId = activeConversationFriendId;
    }

    public void setImage(String id) {
        OnSuccessListener onSuccessListener = new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                liveDataImage.setValue((Uri)o);
            }
        };
        dataBaseClass.getImageUserId(id,onSuccessListener);

    }


    public LiveData<String> getLiveDataFullName() {
        return liveDataFullName;
    }

    public LiveData<Uri> getLiveDataImage() {
        return liveDataImage;
    }

    public LiveData<List<Message>> getMessagesLiveData(String id) {
        return messageListLiveData;
    }

    public void addToList(String message){
        List<Message> list = new ArrayList<>(messageListLiveData.getValue() == null ? new ArrayList<Message>() : messageListLiveData.getValue());
        String pattern = "dd-MM-yyyy HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());
        list.add(new Message(message,date,list.size(),activeConversationFriendId));
        messageListLiveData.setValue(list);
    }

    public void sendMessage(String message,String token,String name) throws JSONException {
        List<Message> list = new ArrayList<>(messageListLiveData.getValue() == null ? new ArrayList<Message>() : messageListLiveData.getValue());


        String pattern = "dd-MM-yyyy HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());
        Message msg = new Message(message, date,list.size(),user.getUserId());
        list.add(msg);
        messageListLiveData.setValue(list);

        dataBaseClass.sendMessage(msg , activeConversationFriendId);
        createNotificationMessage(token,msg,name);
    }

    public void getAllMessages(){
        final List<Message> list = new ArrayList<>(messageListLiveData.getValue() == null ? new ArrayList<Message>() : messageListLiveData.getValue());
        list.clear();
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        Message userId = snapshot1.getValue(Message.class);
                        list.add(userId);
                    }
                    messageListLiveData.setValue(list);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        dataBaseClass.retrieveMessages(activeConversationFriendId,valueEventListener);
    }

    public void saveIfOpen(boolean open,String id){
        dataBaseClass.saveIfOpenTheLastMessage(open,id);
    }


    private void createNotificationMessage(String token,Message message,String name) throws JSONException {

        final JSONObject rootObject = new JSONObject();
        rootObject.put("to", token);
        JSONObject data = new JSONObject();
        data.put("messageType","message");
        data.put("title", "New message from "+ name);
        data.put("body",message.getContent());


        data.put("userId",UserInstance.getInstance().getUser().getUserId());
        data.put("time",message.getTime());

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



}
