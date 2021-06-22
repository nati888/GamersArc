package il.ac.hit.gamersarc;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendsTabVM extends ViewModel {

    private ArrayList<User> friends = new ArrayList<>();
    private ArrayList<String> friendsIds = new ArrayList<>();
    private MutableLiveData<ArrayList<User>> friendsLiveData = new MutableLiveData<>();

    private DataBaseClass dataBaseClass = DataBaseClass.getInstance();
    private RegisterClass registerClass = RegisterClass.getInstance();

    private MutableLiveData<Boolean> swipeLayoutBool = new MutableLiveData<>();

    public FriendsTabVM() {

    }

    public void getFriendsIds (){
        swipeLayoutBool.setValue(true);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendsIds.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){

                    friendsIds.add(snapshot1.getKey());
                }
                friendsIds.remove("false");

                getFriendsUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dataBaseClass.retrieveFriendsIds(registerClass.getUserId(), listener);

    }

    private void getFriendsUsers() {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friends.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    if (friendsIds.contains(snapshot1.getKey())){
                        User user = snapshot1.getValue(User.class);
                        friends.add(user);
                    }
                }

                friendsLiveData.setValue(friends);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dataBaseClass.retrieveAllUsersList(listener);
    }

    public MutableLiveData<ArrayList<User>> getFriendsLiveData(){

        return friendsLiveData;
    }

    public void removeFriend(String userId, String friendId){
        dataBaseClass.removeFriend(userId, friendId);
        getFriendsIds();
    }

    public MutableLiveData<Boolean> getSwipeLayoutBool(){
        return swipeLayoutBool;
    }

}
