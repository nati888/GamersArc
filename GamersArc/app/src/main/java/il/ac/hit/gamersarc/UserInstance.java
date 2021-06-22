package il.ac.hit.gamersarc;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class UserInstance {

    interface OnGetUserListener{
        void onGetUser();
    }

    private com.example.runtime.UserInstance.OnGetUserListener callBackUserGet;
    private static com.example.runtime.UserInstance userInstance = null;
    private User user;
    private DataBaseClass dataBaseClass = DataBaseClass.getInstance();

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            User userNew;

            userNew = snapshot.getValue(User.class);
            user = userNew;
            callBackUserGet.onGetUser();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };



    private UserInstance(){
        this.user= new User();
    }

    public static com.example.runtime.UserInstance getInstance()
    {
        if (userInstance == null)
            userInstance = new com.example.runtime.UserInstance();

        return userInstance;
    }

    public void getUserFromDataBase(){
        dataBaseClass.getUser(valueEventListener);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCallBackUserGet(com.example.runtime.UserInstance.OnGetUserListener callBackUserGet) {
        this.callBackUserGet = callBackUserGet;
    }
}
