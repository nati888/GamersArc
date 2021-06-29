package il.ac.hit.gamersarc;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUp2Fragment extends Fragment implements DataBaseClass.OnSaveImageListener{

    private SignUpVM viewModel;
    private String gender;
    private String level;
    private int yearOfBirth;
    private int monthOfBirth;
    private int dayOfMonthOfBirth;
    private final int PICK_IMAGE_REQUEST=1;
    private Uri filePath;
    private CircleImageView imageViewProfile;
    private DataBaseClass dataBaseClass;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel= new ViewModelProvider(getActivity()).get(SignUpVM.class);
        dataBaseClass = DataBaseClass.getInstance();
        dataBaseClass.setCallBackImage(this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == getActivity().RESULT_OK
                && data != null
                && data.getData() != null) {
            filePath = data.getData();
            viewModel.saveProfileImage(filePath);
            viewModel.setUserImageLiveData(filePath);
        }
       }

    @Override
    public void onSuccessImage() {
        Glide.with(getActivity()).load(filePath).into(imageViewProfile);
    }

    @Override
    public void onFailedImage() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.sign_up2,container,false);
        viewModel.setProgressBar1LiveData(false);

        imageViewProfile = root.findViewById(R.id.profileImageIV);
        final TextView textViewDate = root.findViewById(R.id.dateET);
        ImageView calendarIcon = root.findViewById(R.id.signUp2CalendarIcon);
        final RadioGroup radioGroupGender = root.findViewById(R.id.genderGroup);
        RadioGroup radioGroupLevel = root.findViewById(R.id.levelGroup);
        Button buttonNext = root.findViewById(R.id.nextButton);

        final TextView ageView=root.findViewById(R.id.age);


        final ProgressBar progressBar = root.findViewById(R.id.signUp2_progressBar);
        viewModel.getProgressBar2LiveData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean){
                    progressBar.setVisibility(View.VISIBLE);
                } else{
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

        viewModel.getDateStringLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                textViewDate.setTextColor(Color.parseColor("#fab32c"));
                textViewDate.setText(s);
            }
        });

        viewModel.getUserImageLiveData().observe(getViewLifecycleOwner(), new Observer<Uri>() {
            @Override
            public void onChanged(Uri uri) {
                Glide.with(getActivity()).load(uri).into(imageViewProfile);
            }
        });


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




        textViewDate.setOnClickListener(new View.OnClickListener() {
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

                        viewModel.setYear(year);
                        viewModel.setMonth(month +1);
                        viewModel.setDayOfMonth(dayOfMonth);
                        viewModel.setDateStringLiveData(dayOfMonthOfBirth+"/"+monthOfBirth+'/'+yearOfBirth);

                    }
                },year1,month1,dayOfMonth1);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()-1000);
                datePickerDialog.show();





            }
        });

        calendarIcon.setOnClickListener(new View.OnClickListener() {
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

                        viewModel.setYear(year);
                        viewModel.setMonth(month+1);
                        viewModel.setDayOfMonth(dayOfMonth);
                        viewModel.setDateStringLiveData(dayOfMonthOfBirth+"/"+monthOfBirth+'/'+yearOfBirth);

                    }
                },year1,month1,dayOfMonth1);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()-1000);
                datePickerDialog.show();
            }
        });

        radioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId)
                {
                    case R.id.maleRB: {
                      gender="male" ;
                      viewModel.setGender(gender);
                      break;
                    }
                    case R.id.femaleRB: {
                        gender="female" ;
                        viewModel.setGender(gender);
                        break;
                    }
                }
            }
        });

        radioGroupLevel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.easyRB:{
                        level = "easy";
                        viewModel.setRunningLevel(level);
                        break;
                    }
                    case R.id.mediumRB:{
                        level = "medium";
                        viewModel.setRunningLevel(level);
                        break;
                    }
                    case R.id.expertRB:{
                        level = "expert";
                        viewModel.setRunningLevel(level);
                        break;
                    }
                }
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (viewModel.getGender() == null ||
                viewModel.getYear() == 0 || viewModel.getMonth() == 0 || viewModel.getDayOfMonth() == 0
                || viewModel.getRunningLevel() == null){
                    Toast.makeText(getActivity(),R.string.fill_all_details,Toast.LENGTH_LONG).show();
                } else {
                    viewModel.setDataNext2(filePath,yearOfBirth,monthOfBirth,dayOfMonthOfBirth,gender,level);
                }

            }
        });

        return root;
    }





}
