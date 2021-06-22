package il.ac.hit.gamersarc;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Objects;

public class SignUpVM extends ViewModel {
    private String fullName;
    private String email;
    private String password;
    private String passwordConfirm;
    private String gender;
    private String runningLevel;
    private int startAge =1 ;
    private int endAge = 120;
    private int year = 0;
    private int month = 0;
    private int dayOfMonth = 0;
    private String partnerLevel;
    private String partnerGender;
    private Uri userImage;
    private RegisterClass registerClass = RegisterClass.getInstance();
    private DataBaseClass dataBaseClass = DataBaseClass.getInstance();
    private UserInstance userInstance = UserInstance.getInstance();

    private MutableLiveData<String> dateStringLiveData = new MutableLiveData<>();
    private MutableLiveData<Uri> userImageLiveData = new MutableLiveData<>();

    private MutableLiveData<Boolean> progressBar1LiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> progressBar2LiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> progressBar3LiveData = new MutableLiveData<>();

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public int getStartAge() {
        return startAge;
    }

    public void setStartAge(int startAge) {
        this.startAge = startAge;
    }

    public int getEndAge() {
        return endAge;
    }

    public void setEndAge(int endAge) {
        this.endAge = endAge;
    }

    public String getPartnerLevel() {
        return partnerLevel;
    }

    public void setPartnerLevel(String partnerLevel) {
        this.partnerLevel = partnerLevel;
    }

    public String getPartnerGender() {
        return partnerGender;
    }

    public void setPartnerGender(String partnerGender) {
        this.partnerGender = partnerGender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setRunningLevel(String runningLevel) {
        this.runningLevel = runningLevel;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public void setDateStringLiveData(String date) {
        dateStringLiveData.setValue(date);
    }

    public void setUserImageLiveData(Uri uri) {
        userImageLiveData.setValue(uri);
    }

    public MutableLiveData<Uri> getUserImageLiveData(){
       return userImageLiveData;
    }

    public MutableLiveData<String> getDateStringLiveData(){
        return dateStringLiveData;
    }

    public MutableLiveData<Boolean> getProgressBar1LiveData(){
        return progressBar1LiveData;
    }
    public MutableLiveData<Boolean> getProgressBar2LiveData(){
        return progressBar2LiveData;
    }

    public MutableLiveData<Boolean> getProgressBar3LiveData(){
        return progressBar3LiveData;
    }

    public void setProgressBar1LiveData(Boolean bool){
        progressBar1LiveData.setValue(bool);
    }

    public void setProgressBar2LiveData(Boolean bool){
        progressBar2LiveData.setValue(bool);
    }

    public void setProgressBar3LiveData(Boolean bool){
        progressBar3LiveData.setValue(bool);
    }

    public String getGender() {
        return gender;
    }

    public String getRunningLevel() {
        return runningLevel;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public boolean setDataNext1(String fullName , String password , String passwordConfirm , String email) {

        if (this.password.equals(this.passwordConfirm)) {
            setProgressBar1LiveData(true);

            registerClass.signUpUser(this.email, this.password);
            return true;
        }
        return false;

    }




    public void setDataNext2(Uri userImage ,int year,int month,int dayOfMonth, String gender , String runningLevel){

        setProgressBar2LiveData(true);

        final User user = new User("0",registerClass.getUserId(),this.fullName,this.gender,this.year,this.month,this.dayOfMonth,this.runningLevel,userInstance.getUser().getLongitude(),userInstance.getUser().getLatitude());

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(task.isSuccessful()){
                    String token = Objects.requireNonNull(task.getResult()).getToken();
                    user.setUserToken(token);
                    dataBaseClass.createUser(user);
                }
            }
        });
    }

    public void setDataNext3(int startAge , int endAge , String partnerGender , String partnerLevel){

        setProgressBar3LiveData(true);

        UserPreferences userPreferences=new UserPreferences(this.startAge, this.endAge,this.partnerGender,this.partnerLevel);
        UserLists userLists=new UserLists();
        dataBaseClass.createPreferences(userPreferences);
        dataBaseClass.createUserLists(userLists);

        clearAll();

    }

    private void clearAll() {

        gender = null;
        runningLevel = null;
        startAge = 0;
        endAge = 0;
        year =0;
        month = 0;
        dayOfMonth = 0;
        partnerGender = null;
        partnerLevel = null;
        dateStringLiveData = new MutableLiveData<>();
        userImageLiveData = new MutableLiveData<>();

    }

    public void saveProfileImage(Uri imageUri){
        dataBaseClass.saveProfilePicture(imageUri);
    }


}
