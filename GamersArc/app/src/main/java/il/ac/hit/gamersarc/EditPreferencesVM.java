package il.ac.hit.gamersarc;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import il.ac.hit.gamersarc.UserPreferences;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class EditPreferencesVM extends ViewModel {
    private DataBaseClass dataBaseClass = DataBaseClass.getInstance();
    private MutableLiveData<UserPreferences> preferencesMutableLiveData = new MutableLiveData<>();
    MutableLiveData<Integer> toAge = new MutableLiveData<>();
    MutableLiveData<Integer> fromAge = new MutableLiveData<>();

    public EditPreferencesVM() {

    }

    public void getUserPreferences(){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    UserPreferences userPreferences = snapshot.getValue(UserPreferences.class);
                    preferencesMutableLiveData.setValue(userPreferences);
                    fromAge.setValue(userPreferences.getFromAge());
                    toAge.setValue(userPreferences.getToAge());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        dataBaseClass.retrieveUserPreferences(valueEventListener);
    }

    public MutableLiveData<UserPreferences> getPreferencesMutableLiveData() {
        return preferencesMutableLiveData;
    }

    public MutableLiveData<Integer> getFromAge() {
        return fromAge;
    }

    public MutableLiveData<Integer> getToAge() {
        return toAge;
    }
    public void savePreferences(UserPreferences userPreferences) {
        dataBaseClass.updateUserPreferences(userPreferences);
    }

    public void updateLiveData(UserPreferences userPreferences) {
        preferencesMutableLiveData.setValue(userPreferences);
    }

    public void setFromAge(Integer fromAge) {
        this.fromAge.setValue(fromAge);
    }

    public void setToAge(Integer toAge) {
        this.toAge.setValue(toAge);
    }
}
