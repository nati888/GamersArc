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

public class GamersDialogVM extends ViewModel {


    private String eventId;


    MutableLiveData<Uri> managerImageUriLiveData = new MutableLiveData<>();
    MutableLiveData<User> manager = new MutableLiveData<>();
    MutableLiveData<ArrayList<User>> gamersLiveData = new MutableLiveData<>();
    private ArrayList<String> gamersIds = new ArrayList<>();
    private ArrayList<User> gamers = new ArrayList<>();
    DataBaseClass dataBaseClass = DataBaseClass.getInstance();

    public GamersDialogVM(String eventId) {
        this.eventId = eventId;
        getManagerId();
        getgamersIds();
    }

    private void getgamersIds() {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gamersIds.clear();
                if (snapshot.exists()){
                    for (DataSnapshot snapshot1: snapshot.getChildren()){
                        gamersIds.add(snapshot1.getKey());
                    }
                }
                gamersIds.remove("false");
                getgamers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dataBaseClass.retrievegamersIds(eventId, listener);
    }

    private void getgamers() {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gamers.clear();
                if(snapshot.exists()){
                    for(DataSnapshot snapshot1 : snapshot.getChildren()){
                        if (gamersIds.contains(snapshot1.getKey())){
                            User user = snapshot1.getValue(User.class);
                            gamers.add(user);
                        }
                    }
                    gamersLiveData.setValue(gamers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dataBaseClass.retrieveAllUsersList(listener);
    }

    private void getManagerId(){
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String managerId = snapshot.getValue(String.class);
                    getManagerName(managerId);

                    getManagerImageUri(managerId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        dataBaseClass.retrieveEventManagerId(eventId, listener);
    }


    private void getManagerImageUri (String managerId){
        OnSuccessListener<Uri> listener = new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                managerImageUriLiveData.setValue(uri);
            }
        };

        dataBaseClass.getImageUserId(managerId, listener);
    }

    private void getManagerName(String managerId) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User managerUser = snapshot.getValue(User.class);
                    manager.setValue(managerUser);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dataBaseClass.retrieveUserById(managerId, listener);
    }

    public MutableLiveData<User> getManager(){
        return manager;
    }



    public MutableLiveData<Uri> getManagerImageUriLiveData(){
        return managerImageUriLiveData;
    }

    public MutableLiveData<ArrayList<User>> getgamersLiveData(){
        return gamersLiveData;
    }
}
