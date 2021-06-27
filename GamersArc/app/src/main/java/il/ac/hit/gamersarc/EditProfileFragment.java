package il.ac.hit.gamersarc;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import il.ac.hit.gamersarc.UserInstance;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileFragment extends Fragment {

    private String gender;
    private String level;
    private String fullName;
    private int yearOfBirth;
    private int monthOfBirth;
    private int dayOfMonthOfBirth;
    private UserInstance userInstance = UserInstance.getInstance();
    private final int PICK_IMAGE_REQUEST=1;
    private Uri filePath;
    private CircleImageView imageViewProfile;
    private EditProfileVM editProfileVM;
    private TextView textViewFullName;
    private TextView textViewDate;
    RadioButton radioButtonMale;
    RadioButton radioButtonFemale;
    RadioGroup radioGroupGender;
    RadioGroup radioGroupLevel;
    RadioButton radioButtonEasy;
    RadioButton radioButtonMedium;
    RadioButton radioButtonExpert;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == getActivity().RESULT_OK
                && data != null
                && data.getData() != null) {
            filePath = data.getData();
            Glide.with(getActivity()).load(filePath).into(imageViewProfile);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        editProfileVM = new ViewModelProvider(getActivity()).get(EditProfileVM.class);
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.edit_profile_fragment,container,false);
        ImageButton imageButton = root.findViewById(R.id.dateEditImageButton);
        textViewDate = root.findViewById(R.id.dateEditProfileET);
        textViewFullName = root.findViewById(R.id.fullNameEditProfileEt);
        radioGroupGender = root.findViewById(R.id.genderGroupEditProfile);
        radioGroupLevel = root.findViewById(R.id.levelGroupEditProfile);
        radioButtonMale = root.findViewById(R.id.maleEditProfileRB);
       radioButtonFemale = root.findViewById(R.id.femaleEditProfileRB);
        radioButtonEasy = root.findViewById(R.id.easyEditProfileRB);
        radioButtonMedium = root.findViewById(R.id.mediumEditProfileRB);
        radioButtonExpert = root.findViewById(R.id.expertEditProfileRB);

        Button buttonDone= root.findViewById(R.id.saveEditProfileButton);
        final Context context=getContext();
        imageViewProfile = root.findViewById(R.id.imageEditProfile);
        editProfileVM.getImageFromData();

        editProfileVM.setUserLiveData(userInstance.getUser());

        Observer<String> resultObserverImage = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Glide.with(context).load(s).into(imageViewProfile);

            }
        };


        editProfileVM.getImageLivedata().observe(getViewLifecycleOwner() , resultObserverImage);

        editProfileVM.getUserLiveData().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                textViewDate.setText(user.getDayOfMonth() + "." + user.getMonth() + "." + user.getYear());
                textViewFullName.setText(user.getFullName());
                if(user.getGender().equals("male")){
                    radioButtonMale.setChecked(true);
                }
                else {
                    radioButtonFemale.setChecked(true);
                }
                switch (userInstance.getUser().getRunningLevel()){
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

        gender = userInstance.getUser().getGender();
        dayOfMonthOfBirth = userInstance.getUser().getDayOfMonth();
        monthOfBirth = userInstance.getUser().getMonth();
        yearOfBirth = userInstance.getUser().getYear();
        level = userInstance.getUser().getRunningLevel();
        textViewFullName.setText(userInstance.getUser().getFullName());
        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(
                        Intent.createChooser(
                                intent,
                                getString(R.string.Select_Image_from_here)),
                        PICK_IMAGE_REQUEST);
            }
        });



        radioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId)
                {
                    case R.id.maleEditProfileRB: {
                        gender="male" ;
                        break;
                    }
                    case R.id.femaleEditProfileRB: {
                        gender="female" ;
                        break;
                    }
                }
            }
        });

        radioGroupLevel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.easyEditProfileRB:{
                        level = "easy";
                        break;
                    }
                    case R.id.mediumEditProfileRB:{
                        level = "medium";
                        break;
                    }
                    case R.id.expertEditProfileRB:{
                        level = "expert";
                        break;
                    }
                }
            }
        });



        CircleImageView imageButtonEditText = root.findViewById(R.id.changeFullNameImage);
        imageButtonEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());

                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_name, null);
                dialogBuilder.setView(dialogView);

                final EditText editText = (EditText) dialogView.findViewById(R.id.editTextDialog);

                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                Button buttonNameDialog = dialogView.findViewById(R.id.buttonDialog);
                buttonNameDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        textViewFullName.setText(editText.getText().toString());
                        alertDialog.dismiss();
                    }
                });
        }});


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar=Calendar.getInstance();
                int year1=calendar.get(calendar.YEAR);
                int month1=calendar.get(calendar.MONTH);
                int dayOfMonth1=calendar.get(calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),android.R.style.Theme_Holo_Light_Dialog, new DatePickerDialog.OnDateSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        yearOfBirth=year;
                        monthOfBirth=month+1;
                        dayOfMonthOfBirth=dayOfMonth;
                        textViewDate.setText(dayOfMonthOfBirth+"."+monthOfBirth+'.'+yearOfBirth);


                    }
                },year1,month1,dayOfMonth1);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()-1000);
                datePickerDialog.show();
            }
        });




        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullName = textViewFullName.getText().toString();
                userInstance.getUser().setFullName(fullName);
                userInstance.getUser().setGender(gender);
                userInstance.getUser().setRunningLevel(level);
                userInstance.getUser().setYear(yearOfBirth);
                userInstance.getUser().setMonth(monthOfBirth);
                userInstance.getUser().setDayOfMonth(dayOfMonthOfBirth);
                editProfileVM.saveUserEdit();

                if(filePath!=null)
                    editProfileVM.saveUserImageEdit(filePath);
            }
        });

        return root;
    }
}


