package il.ac.hit.gamersarc;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class RegisterClass {


    interface SignUpStatusListener{
       void onSuccessSignUp(String userId);
        void onFailedSignUp(String problem);
    }

    interface SignUpFailListener {
        void onFailedSignUp(String problem);
    }

    interface SignInStatusListener{
        void onSuccessSignIn(String userId);
        void onFailedSignIn(String problem);
    }

    interface SignInFailListener{
        void onFailedSignIn(String problem);
    }

    interface SignOutListener{
        void onSignOut();
    }

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    private static com.example.runtime.RegisterClass registerClass = null;

    private SignUpStatusListener signUpCallback;
    private SignInStatusListener signInCallback;
    private SignOutListener signOutCallback;
    private SignUpFailListener failListener;
    private SignInFailListener signInFailListener;

    public void setSignInFailListener(SignInFailListener signInFailListener) {
        this.signInFailListener = signInFailListener;
    }

    public void setFailListener(SignUpFailListener failListener) {
        this.failListener = failListener;
    }

    public void setSignUpListener (SignUpStatusListener callback){
        signUpCallback = callback;
    }

    public void setSignInListener (SignInStatusListener callback){
        signInCallback = callback;
    }

    public void setSignOutListener (SignOutListener callback){
        signOutCallback = callback;
    }



    public static com.example.runtime.RegisterClass getInstance(){
        if(registerClass == null ){
            registerClass = new com.example.runtime.RegisterClass();
        }
        return registerClass;
    };

    private RegisterClass() {
    }

    public void signUpUser(String email , String password){
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //tell listener success or fail
                if(task.isSuccessful()){
                    FirebaseUser user;
                    user = firebaseAuth.getCurrentUser();
                    signUpCallback.onSuccessSignUp(user.getDisplayName());
                } else{
                    userRegisterFailed(task);
                }
            }
        });

    }

    public void signInUser(String email , String password){
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user;
                    user = firebaseAuth.getCurrentUser();
                    signInCallback.onSuccessSignIn(user.getUid());
                }else{
                    signInCallback.onFailedSignIn(task.getException().getMessage());
                    signInFailListener.onFailedSignIn(task.getException().getMessage());
                }
            }
        });

    }

    public void signOut(){
        firebaseAuth.signOut();

    }



    public void addStateListener(FirebaseAuth.AuthStateListener authStateListener){
        firebaseAuth.addAuthStateListener(authStateListener);    }

    public void removeStateListener(FirebaseAuth.AuthStateListener authStateListener){
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    private void userRegisterFailed(Task<AuthResult> task){
        try {
            throw task.getException();
        } catch(FirebaseAuthWeakPasswordException e) {
            failListener.onFailedSignUp("Password must be at least 8 letters");
        } catch(FirebaseAuthInvalidCredentialsException e) {
            failListener.onFailedSignUp("Invalid Credentials");
        } catch(FirebaseAuthUserCollisionException e) {
            failListener.onFailedSignUp("User already exists");
        } catch(Exception e) {
            failListener.onFailedSignUp("Error");
        }

    }

    public String getUserId(){

        return firebaseAuth.getCurrentUser().getUid();

    }

    public FirebaseUser getCurrentUser(){
        return firebaseAuth.getCurrentUser();
    }


}
