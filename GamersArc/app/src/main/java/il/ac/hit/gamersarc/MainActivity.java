package il.ac.hit.gamersarc;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.installations.FirebaseInstallations;


public class MainActivity extends AppCompatActivity implements MessagesFragment.OnClickOnMessages, DataBaseClass.OnChangeUserListener, BottomNavBarFragment.OnNavigationListener, WelcomeFragment.OnRegisterClick, DataBaseClass.OnUserCreateListener
        ,SignUp3Fragment.OnSignUpLastListener, RegisterClass.SignUpStatusListener, DataBaseClass.OnUserPreferenceCreateListener,DataBaseClass.OnUpdateUserPreferences,RegisterClass.SignInStatusListener,DataBaseClass.OnUserListsListener, CreateEventFragment.OnMapListener,
        MapFragment.OnCreateEventListener,  CreateEventFragment.OnBackFromCreateEventListener{


    private FragmentManager fragmentManager;
    final String WELCOMEFRAGMENTTAG="welcomefregmenttag";
    final String SIGNUP1TAG="signup1tag";
    final String SIGNUP2TAG="signup2tag";
    final String SIGNUP3TAG="signup3tag";
    private static final String MESSAGES_TAG = "messagestag";
    private static final String MESSAGES_TAG2 = "messagestag2";
    final String HOME_TAG="homeTag";
    final String CREATEEVENT_TAG="eventtag";
    final String FRIEND_TAB_TAG = "friendTabTag";
    final String MAP_TAG="map_tag";
    final String SIGN_IN = "signIn";
    final String TOOLBAR_TAG="toolbartag";
    final String FIND_PEOPLE = "findPeople";
    final String FIND_EVENTS = "findEvents";
    final String STRANGER_FRAGMENT = "strangerFragment";
    final String NAV_TAG = "nav";
    final String PROFiLE_TAG = "profiletag";


    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseDatabase firebaseDatabase ;
    private boolean isPreferencesCreated;
    private boolean isUserListsCreated;
    private UserInstance userInstance;
    private DrawerLayout drawerLayout;
    CreateEventFragment createEventFragment;
    RelativeLayout toolbarLayout;
    RegisterClass registerClass;
    DataBaseClass dataBaseClass;
    Fragment toolBarFragment;
    SharedPreferences sp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        sp = getSharedPreferences("details", MODE_PRIVATE);

        userInstance = UserInstance.getInstance();
        registerClass = RegisterClass.getInstance();
        dataBaseClass = DataBaseClass.getInstance();
        registerClass.setSignUpListener(this);
        dataBaseClass.setCallBackCreate(this);
        dataBaseClass.setCallBackPreferenceCreate(this);
        registerClass.setSignInListener(this);
        dataBaseClass.setCallBackUserLists(this);
        dataBaseClass.setOnChangeUserListener(this);
        dataBaseClass.setOnUpdateUserPreferences(this);

        createEventFragment = CreateEventFragment.getCreateEventFragment(false);
        toolbarLayout = findViewById(R.id.toolbarLayout);


        authStateListener= firebaseAuth -> {

            final FirebaseUser user=firebaseAuth.getCurrentUser();
            if(user!=null) { //sign up or sign in

                RelativeLayout layout = findViewById(R.id.toolbarLayout);
                layout.setVisibility(View.VISIBLE);

                ValueEventListener listener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("gender")){
                            if (!sp.getBoolean("isChangingConfigurations", false)){

                                fragmentManager.beginTransaction().replace(R.id.rootLayout,new HomeFragment(),HOME_TAG).commit();
                                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                                fragmentManager.beginTransaction().replace(R.id.toolbarLayout,new ToolBarFragment(),TOOLBAR_TAG).commit();
                                fragmentManager.beginTransaction().replace(R.id.layoutBottomNavgtionBar,new BottomNavBarFragment(),NAV_TAG).commit();}
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };

                dataBaseClass.isUserExists(registerClass.getUserId(), listener);

                FirebaseInstallations.getInstance().getId().addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()){
                            String token = task.getResult();
                            dataBaseClass.saveUserToken(token);
                        }
                    }
                });



                getLocationUpdates();

            }

            else {


                if (!sp.getBoolean("isChangingConfigurations", false)){
                    fragmentManager.beginTransaction().replace(R.id.rootLayout,new WelcomeFragment(),WELCOMEFRAGMENTTAG).commit();
                    toolBarFragment =getSupportFragmentManager().findFragmentByTag(TOOLBAR_TAG);
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    RelativeLayout layout = findViewById(R.id.toolbarLayout);
                    layout.setVisibility(View.GONE);

                    if(toolBarFragment!=null) {
                        fragmentManager.beginTransaction().remove(toolBarFragment).commit();
                        fragmentManager.beginTransaction().remove(getSupportFragmentManager().findFragmentByTag(NAV_TAG)).commit();

                    }
                }

            }

        };

        registerClass.addStateListener(authStateListener);

        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.NavigationSide);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.signOutSidebar:


                        SharedPreferences.Editor editor=sp.edit();
                        editor.putBoolean("isChangingConfigurations",false);
                        editor.commit();

                        Fragment fragment = fragmentManager.findFragmentByTag(FRIEND_TAB_TAG);
                        if(fragment!=null){
                            fragmentManager.beginTransaction().remove(fragment);
                        }
                        drawerLayout.closeDrawers();
                        dataBaseClass.deleteToken(registerClass.getUserId());
                        registerClass.signOut();
                        break;
                    case R.id.editProfileSidebar:
                        drawerLayout.closeDrawers();
                        fragmentManager.beginTransaction().replace(R.id.rootLayout, new EditProfileFragment(), "editProfile").addToBackStack(null).commit();
                        break;
                    case R.id.EditPreferencesSidebar :
                        drawerLayout.closeDrawers();
                        fragmentManager.beginTransaction().replace(R.id.rootLayout , new EditPreferencesFragment(), HOME_TAG).addToBackStack(null).commit();
                        break;
                }
                return false;
            }
        });


    }


    @Override
    public void onSignInClick() {

        fragmentManager.beginTransaction().replace(R.id.rootLayout,new SignInFragment(),SIGN_IN).addToBackStack(null).commit();
    }

    @Override
    public void onSignUpClick() {

        fragmentManager.beginTransaction().replace(R.id.rootLayout,new SignUp1Fragment(),SIGNUP1TAG).addToBackStack(null).commit();
    }



    @Override
    public void onSignUpLast() {

    }


    @Override
    public void onClickMessages(User user) {
        MessagesFragment2 messagesFragment2 = MessagesFragment2.newInstance(user);
        fragmentManager.beginTransaction().replace(R.id.rootLayout,messagesFragment2,MESSAGES_TAG2).addToBackStack(null).commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor editor=sp.edit();
        editor.putBoolean("isChangingConfigurations",false);
        editor.commit();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isChangingConfigurations()){
            SharedPreferences.Editor editor=sp.edit();
            editor.putBoolean("isChangingConfigurations",true);
            editor.commit();

        }

        registerClass.removeStateListener(authStateListener);
    }

    @Override
    public void onSuccessSignUp(String userId) {
        fragmentManager.beginTransaction().replace(R.id.rootLayout, new SignUp2Fragment(), SIGNUP2TAG).addToBackStack(null).commit();

    }

    @Override
    public void onSuccessUserLists() {
        isUserListsCreated=true;
        moveToHomeFragment();
    }

    @Override
    public void onFailedUserLists() {
        isUserListsCreated=false;
        moveToHomeFragment();

    }

    @Override
    public void onNavChange(String page) {
        switch (page){
            case "home":

                fragmentManager.beginTransaction().replace(R.id.rootLayout,new HomeFragment(),HOME_TAG).addToBackStack(null).commit();
                break;
            case "group":

                fragmentManager.beginTransaction().replace(R.id.rootLayout,new FriendsFragment(),FRIEND_TAB_TAG).addToBackStack(null).commit();
                break;
            case "location":

                fragmentManager.beginTransaction().replace(R.id.rootLayout,new EventsFragment(),"events").addToBackStack(null).commit();
                break;
            case "profile":

                fragmentManager.beginTransaction().replace(R.id.rootLayout,new ProfileFragment(),PROFiLE_TAG).addToBackStack(null).commit();
                break;
            case "message":

                fragmentManager.beginTransaction().replace(R.id.rootLayout,new MessagesFragment(),MESSAGES_TAG).addToBackStack(null).commit();
                break;
        }
    }


    @Override
    public void onFailedSignUp(String problem) {

    }

    @Override
    public void onSuccessCreate() {
        fragmentManager.beginTransaction().replace(R.id.rootLayout,new SignUp3Fragment(),SIGNUP3TAG).addToBackStack(null).commit();

    }

    @Override
    public void onFailedCreate(String problem) {

    }

    @Override
    public void onSuccessPreferenceCreate() {

        isPreferencesCreated=true;

    }

    @Override
    public void onFailedPreferenceCreate() {
        isPreferencesCreated=false;

    }

    @Override
    public void onSuccessSignIn(String userId) {

        for(int i=0; i<fragmentManager.getBackStackEntryCount(); i++){
            fragmentManager.popBackStack();
        }
        fragmentManager.beginTransaction().replace(R.id.toolbarLayout,new ToolBarFragment(),TOOLBAR_TAG).commit();

        fragmentManager.beginTransaction().replace(R.id.rootLayout,new HomeFragment(),HOME_TAG).commit();

        fragmentManager.beginTransaction().replace(R.id.layoutBottomNavgtionBar,new BottomNavBarFragment(),NAV_TAG).commit();



    }

    @Override
    public void onFailedSignIn(String problem) {

    }

    public void getLocationUpdates(){
        CurrentLocationListener.getInstance(getApplicationContext()).observe(this, new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                if(location!=null){

                    double longitude=location.getLongitude();
                    double latitude=location.getLatitude();
                    userInstance.getUser().setLongitude(longitude);
                    userInstance.getUser().setLatitude(latitude);
                    dataBaseClass.updateLocation(longitude,latitude);

                }
                else
                    Toast.makeText(MainActivity.this,R.string.no_location_detected,Toast.LENGTH_LONG).show();
            }
        });
    }

    public void moveToHomeFragment(){
        if (isPreferencesCreated && isUserListsCreated){

            for(int i=0; i<fragmentManager.getBackStackEntryCount(); i++){
                fragmentManager.popBackStack();
            }

            fragmentManager.beginTransaction().replace(R.id.rootLayout,new HomeFragment(),HOME_TAG).commit();
            fragmentManager.beginTransaction().replace(R.id.toolbarLayout,new ToolBarFragment(),TOOLBAR_TAG).commit();
            fragmentManager.beginTransaction().replace(R.id.layoutBottomNavgtionBar,new BottomNavBarFragment(),"nav").commit();
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        }

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            drawerLayout.openDrawer(GravityCompat.START);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapOkClick() {
        fragmentManager.beginTransaction().replace(R.id.rootLayout, new MapFragment(), MAP_TAG).addToBackStack(null).commit();
    }

    @Override
    public void onCreateEventFromMap(boolean isNew) {
        getSupportFragmentManager().popBackStack();
        fragmentManager.beginTransaction().replace(R.id.rootLayout, CreateEventFragment.getCreateEventFragment(false), CREATEEVENT_TAG).addToBackStack(null).commit();
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onSuccessUpdatePreferences() {
        fragmentManager.beginTransaction().replace(R.id.rootLayout,new HomeFragment(), CREATEEVENT_TAG).addToBackStack(null).commit();
    }

    @Override

    public void toHomeFromCreateEvent() {

        fragmentManager.beginTransaction().replace(R.id.rootLayout, new HomeFragment(), HOME_TAG).commit();}

    public void onChangeUserSuccess() {
        fragmentManager.beginTransaction().replace(R.id.rootLayout,new HomeFragment(),HOME_TAG).addToBackStack(null).commit();
    }

    @Override
    public void onChangeUserFailed() {

    }


}






