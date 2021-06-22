package il.ac.hit.gamersarc;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import il.ac.hit.gamersarc.Model.LastMessage;
import il.ac.hit.gamersarc.Model.Message;
import il.ac.hit.gamersarc.Model.SortFriendMessage;
import il.ac.hit.gamersarc.Model.UserWithLastMessage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


public class MessagesVM extends ViewModel {
    private MutableLiveData<ArrayList<UserWithLastMessage>> friends = new MutableLiveData<>();
    private ArrayList<String> usersIdFromDatabase = new ArrayList<>();
    private ArrayList<UserWithLastMessage> usersFromDatabase = new ArrayList<>();
    private DataBaseClass dataBaseClass;

    public MessagesVM() {
        this.dataBaseClass = DataBaseClass.getInstance();
    }

    public void getFriendsId() {
        usersIdFromDatabase.clear();
        usersFromDatabase.clear();
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String userId = snapshot1.getKey();
                        usersIdFromDatabase.add(userId);
                    }
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String userId = snapshot1.getKey();

                        if(!userId.equals("false")){
                            getFriends(userId);
                        }

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        dataBaseClass.retrieveAllFriends(valueEventListener);

    }

    public void saveIfOpen(boolean open,String id){
        dataBaseClass.saveIfOpenTheLastMessage(open,id);
    }

    private void getFriends(final String id){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                getLastMessage(user,id);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
            dataBaseClass.getUserWithId(valueEventListener,id);
    }

    private void getLastMessage(final User user,String id){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    if(snapshot.child("content").exists()){
                    LastMessage message = snapshot.getValue(LastMessage.class);
                    usersFromDatabase.add(new UserWithLastMessage(user,new Message(message.getContent(),message.getTime(),message.getId(),message.getUserIdSent()),message.isNew()));
                    }
                    else {                         {
                            usersFromDatabase.add(new UserWithLastMessage(user,new Message("","",-1,""),false));
                        }
                    }
                }
                else{
                    usersFromDatabase.add(new UserWithLastMessage(user,new Message("","",-1,""),false));
                }

                Log.d("aaa",usersFromDatabase.size()+"");
                Log.d("aaa",usersIdFromDatabase.size()+"");
                if(usersFromDatabase.size()==usersIdFromDatabase.size()-1)
                {
                    Collections.sort(usersFromDatabase,new SortFriendMessage());
                    friends.setValue(usersFromDatabase);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        dataBaseClass.getLastMessage(id,valueEventListener);
    }

    public MutableLiveData<ArrayList<UserWithLastMessage>> getFriends() {
        return friends;
    }
}
