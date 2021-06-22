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

public class RunnersDialogVM extends ViewModel {


    private String eventId;


    MutableLiveData<Uri> managerImageUriLiveData = new MutableLiveData<>();
    MutableLiveData<User> manager = new MutableLiveData<>();
    MutableLiveData<ArrayList<User>> runnersLiveData = new MutableLiveData<>();
    private ArrayList<String> runnersIds = new ArrayList<>();
    private ArrayList<User> runners = new ArrayList<>();
    DataBaseClass dataBaseClass = DataBaseClass.getInstance();

    public RunnersDialogVM(String eventId) {
        this.eventId = eventId;
        getManagerId();
        getRunnersIds();
    }

    private void getRunnersIds() {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                runnersIds.clear();
                if (snapshot.exists()){
                    for (DataSnapshot snapshot1: snapshot.getChildren()){
                        runnersIds.add(snapshot1.getKey());
                    }
                }
                runnersIds.remove("false");
                getRunners();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dataBaseClass.retrieveRunnersIds(eventId, listener);
    }

    private void getRunners() {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                runners.clear();
                if(snapshot.exists()){
                    for(DataSnapshot snapshot1 : snapshot.getChildren()){
                        if (runnersIds.contains(snapshot1.getKey())){
                            User user = snapshot1.getValue(User.class);
                            runners.add(user);
                        }
                    }
                    runnersLiveData.setValue(runners);
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

    public MutableLiveData<ArrayList<User>> getRunnersLiveData(){
        return runnersLiveData;
    }
}
