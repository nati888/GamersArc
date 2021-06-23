package il.ac.hit.gamersarc;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignUp1Fragment extends Fragment implements RegisterClass.SignUpFailListener {


    SignUpVM viewModel;
    final int LOCATION_PERMISSION_REQUEST=0;
    TextInputEditText fullNameEt;
    TextInputEditText passwordEt;
    TextInputEditText emailEt;
    TextInputEditText passwordConfirmEt;

    TextInputLayout fullNameInputLayout;
    TextInputLayout emailInputLayout;
    TextInputLayout passwordInputLayout;
    TextInputLayout passwordConfirmInputLayout;
    RegisterClass registerClass;
    ProgressBar progressBar;


    String fullName;
    String email;
    String password;
    String passwordConfirm;




    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        viewModel= new ViewModelProvider(getActivity()).get(SignUpVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.sign_up1,container ,false);
        fullNameEt=root.findViewById(R.id.fullNameEt);
        passwordEt=root.findViewById(R.id.passwordEt);
        emailEt=root.findViewById(R.id.emailEt);
        passwordConfirmEt=root.findViewById(R.id.passwordConfirmEt);

        fullNameInputLayout = root.findViewById(R.id.signUp1_name_inputLayout);
        emailInputLayout = root.findViewById(R.id.signUp1_email_inputLayout);
        passwordInputLayout = root.findViewById(R.id.signUp1_password_inputLayout);
        passwordConfirmInputLayout = root.findViewById(R.id.signUp1_passwordConfirm_inputLayout);
        Button nextButton = root.findViewById(R.id.next1);

        registerClass = RegisterClass.getInstance();
        registerClass.setFailListener(this);

        progressBar = root.findViewById(R.id.signUp1_progressBar);
        viewModel.getProgressBar1LiveData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean){
                    progressBar.setVisibility(View.VISIBLE);
                } else{
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

        fullNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fullNameInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.setFullName(s.toString());
            }
        });

        emailEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.setEmail(s.toString());
            }
        });

        passwordEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.setPassword(s.toString());
            }
        });

        passwordConfirmEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordConfirmInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.setPasswordConfirm(s.toString());
            }
        });



        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fullName=fullNameEt.getText().toString();
                password=passwordEt.getText().toString();
                passwordConfirm=passwordConfirmEt.getText().toString();
                email=emailEt.getText().toString();
                String emptyFieldErrorStr = "required_field";

                if (fullName.equals("") || password.equals("") || passwordConfirm.equals("") || email.equals("")){
                    if (fullName.equals(""))
                        fullNameInputLayout.setError(emptyFieldErrorStr);
                    if (email.equals(""))
                        emailInputLayout.setError(emptyFieldErrorStr);
                    if (password.equals(""))
                        passwordInputLayout.setError(emptyFieldErrorStr);
                    if (passwordConfirm.equals("")){
                        passwordConfirmInputLayout.setError(emptyFieldErrorStr);
                    }

                } else {
                    if(Build.VERSION.SDK_INT>=23){

                        int hasLocationPermission= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION);
                        int hasLocationPermission1= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION);
                        if(hasLocationPermission!=PackageManager.PERMISSION_GRANTED || hasLocationPermission1!=PackageManager.PERMISSION_GRANTED){

                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},LOCATION_PERMISSION_REQUEST);
                        }
                        else {
                            if(!viewModel.setDataNext1(fullName,password,passwordConfirm,email)){


                                String passwordsConfirmError = "passwords_not_identical";
                                passwordConfirmInputLayout.setError(passwordsConfirmError);
                                passwordInputLayout.setError(passwordsConfirmError);
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                    else{

                        if(!viewModel.setDataNext1(fullName,password,passwordConfirm,email)){

                            String passwordsConfirmError = "passwords_not_identical";
                            passwordConfirmInputLayout.setError(passwordsConfirmError);
                            passwordInputLayout.setError(passwordsConfirmError);
                            progressBar.setVisibility(View.INVISIBLE);
                        }

                    }
                }



            }
        });
        return root;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==LOCATION_PERMISSION_REQUEST){

            if(grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED){

                if(!viewModel.setDataNext1(fullName,password,passwordConfirm,email)){


                    String passwordsConfirmError = "passwords_not_identical";
                    passwordConfirmInputLayout.setError(passwordsConfirmError);
                    passwordInputLayout.setError(passwordsConfirmError);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        }
    }



    @Override
    public void onFailedSignUp(String problem) {

        progressBar.setVisibility(View.INVISIBLE);
        switch (problem){
            case "Password must be at least 8 letters":
                String error = "password_must_be_8_letters";
                passwordInputLayout.setError(error);
                break;
            case "Invalid Credentials":
                String error2 = "email_badly_formatted";
                emailInputLayout.setError(error2);
                break;
            case "User already exists":
                String error3 ="user_already_exists";
                emailInputLayout.setError(error3);
                break;
            case "Error":
                Toast.makeText(getContext(),"failed_to_sign_up",Toast.LENGTH_SHORT).show();
        }
    }
}
