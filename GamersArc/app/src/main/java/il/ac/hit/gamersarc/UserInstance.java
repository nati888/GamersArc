package il.ac.hit.gamersarc;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class UserInstance {

    interface OnGetUserListener{
        void onGetUser();
    }

    private il.ac.hit.gamersarc.UserInstance.OnGetUserListener callBackUserGet;
    private static il.ac.hit.gamersarc.UserInstance userInstance = null;
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

    public static il.ac.hit.gamersarc.UserInstance getInstance()
    {
        if (userInstance == null)
            userInstance = new il.ac.hit.gamersarc.UserInstance();

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

    public void setCallBackUserGet(il.ac.hit.gamersarc.UserInstance.OnGetUserListener callBackUserGet) {
        this.callBackUserGet = callBackUserGet;
    }
}
