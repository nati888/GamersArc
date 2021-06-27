package il.ac.hit.gamersarc;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import il.ac.hit.gamersarc.EditPreferencesVM;
import il.ac.hit.gamersarc.UserPreferences;
import com.google.android.material.slider.RangeSlider;

import java.util.ArrayList;

public class EditPreferencesFragment extends Fragment {

    private il.ac.hit.gamersarc.EditPreferencesVM editPreferencesVM;
    private String gender;
    private String level;
    private int from ;
    private int to  ;
    private ArrayList<Integer> fromAgesArray = new ArrayList<>();
    private ArrayList<Integer> toAgesArray = new ArrayList<>();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        editPreferencesVM = new ViewModelProvider(getActivity()).get(il.ac.hit.gamersarc.EditPreferencesVM.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int i;
        for (i = 0; i < 121 ; i++) {
            Integer age = i;
            fromAgesArray.add(age);
            toAgesArray.add(age);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.edit_preferences,container,false);

        final RadioButton radioButtonMale = root.findViewById(R.id.maleRBPartnerEditP);
        final RadioButton radioButtonFemale = root.findViewById(R.id.femaleRBPartnerEditP);
        final RadioButton radioButtonEasy = root.findViewById(R.id.easyRBPartnerEditP);
        final RadioButton radioButtonMedium = root.findViewById(R.id.mediumRBPartnerEditP);
        final RadioButton radioButtonExpert = root.findViewById(R.id.expertRBPartnerEditP);
        final RadioButton radioButtonBoth = root.findViewById(R.id.bothRBPartnerEditP);
        final RangeSlider slider =root.findViewById(R.id.RangeSlider_multiple_thumbs);

        editPreferencesVM.getUserPreferences();

        editPreferencesVM.getFromAge().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                from = integer;
                slider.setValues((float)to,(float)from);
            }
        });

        editPreferencesVM.getToAge().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                to = integer;
                slider.setValues((float)to,(float)from);
            }
        });

        editPreferencesVM.getPreferencesMutableLiveData().observe(getViewLifecycleOwner(), new Observer<UserPreferences>() {
            @Override
            public void onChanged(UserPreferences userPreferences) {
                switch (userPreferences.getGender()){
                    case "male" :
                        radioButtonMale.setChecked(true);
                        break;
                    case "female" :
                        radioButtonFemale.setChecked(true);
                        break;
                    case "both" :
                        radioButtonBoth.setChecked(true);
                        break;
                }
                switch (userPreferences.getRuningLevel()){
                    case "easy" :
                        radioButtonEasy.setChecked(true);
                        break;
                    case "medium" :
                        radioButtonMedium.setChecked(true);
                        break;
                    case "expert" :
                        radioButtonExpert.setChecked(true);
                        break;
                }
            }
        });




        slider.addOnSliderTouchListener(new RangeSlider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull RangeSlider RangeSlider) {

            }

            @Override
            public void onStopTrackingTouch(@NonNull RangeSlider slider) {
                float fromLocal = slider.getValues().get(0);
                float toLocal = slider.getValues().get(1);
                from = (int) fromLocal;
                to = (int) toLocal;
            }
        });



        Button buttonDone = root.findViewById(R.id.signUpDoneEditP);

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioButtonEasy.isChecked())
                    level = "easy" ;
                else if(radioButtonMedium.isChecked())
                    level = "medium";
                else
                    level = "expert";
                if(radioButtonMale.isChecked())
                    gender = "male";
                else if(radioButtonFemale.isChecked())
                    gender = "female";
                else
                    gender= "both";


                UserPreferences userPreferences = new UserPreferences(from,to,gender,level);
                editPreferencesVM.updateLiveData(userPreferences);
                editPreferencesVM.savePreferences(userPreferences);
                Toast.makeText(getContext(), getString(R.string.Saved_successfully),Toast.LENGTH_LONG).show();

            }
        });

        return root;
    }
}
