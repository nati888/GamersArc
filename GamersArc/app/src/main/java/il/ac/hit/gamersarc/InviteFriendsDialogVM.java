package il.ac.hit.gamersarc;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class InviteFriendsDialogVM extends ViewModel {

    private MutableLiveData<ArrayList<User>> myFriends = new MutableLiveData<>();
    private ArrayList<String> myFriendsIdsTemp = new ArrayList<>();
    private ArrayList<User> myFriendsTemp = new ArrayList<>();
    private DataBaseClass dataBaseClass = DataBaseClass.getInstance();
    private User currentUser = UserInstance.getInstance().getUser();

    public InviteFriendsDialogVM() {
        setMyFriendsIds();
    }

    public MutableLiveData<ArrayList<User>> getMyFriends() {
        return myFriends;
    }


    // set friendsIds list in order to get friends User object.
    public void setMyFriendsIds(){
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myFriendsIdsTemp.clear();
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    myFriendsIdsTemp.add(snapshot1.getKey());
                }
                myFriendsIdsTemp.remove("false");

                setMyFriends();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };


        dataBaseClass.retrieveFriendsIds(currentUser.getUserId(),listener);
    }

    // get user friends objects by friends id's.
    public void setMyFriends() {

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myFriendsTemp.clear();
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    if(myFriendsIdsTemp.contains(snapshot1.getKey())){
                        User user = snapshot1.getValue(User.class);
                        myFriendsTemp.add(user);
                    }

                }
                myFriends.setValue(myFriendsTemp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        dataBaseClass.retrieveAllUsersList(listener);
    }


}
