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
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignInFragment extends Fragment implements RegisterClass.SignInFailListener {
    SignUpVM viewModel;
    RegisterClass registerClass;
    final int LOCATION_PERMISSION_REQUEST=0;
    String email;
    String password;
    TextInputEditText emailEt;
    TextInputEditText passwordEt;
    TextInputLayout emailInputLayout;
    TextInputLayout passwordInputLayout;
    ProgressBar progressBar;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        viewModel= new ViewModelProvider(getActivity()).get(SignUpVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.sign_in,container ,false);
         emailEt=root.findViewById(R.id.emailEtSignIn);
         passwordEt=root.findViewById(R.id.passwordEtSignIn);
         emailInputLayout = root.findViewById(R.id.sign_in_et_layout_email);
         passwordInputLayout = root.findViewById(R.id.sign_in_et_layout_password);
         progressBar = root.findViewById(R.id.signIn_progressBar);

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

             }
         });

        Button signInBtn = root.findViewById(R.id.signIn);

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerClass=RegisterClass.getInstance();
                registerClass.setSignInFailListener(il.ac.hit.gamersarc.SignInFragment.this);
                email=emailEt.getText().toString();
                password=passwordEt.getText().toString();

                String emptyFieldErrorStr = "required_field";


                if(email.equals("") || password.equals("")){
                    if (email.equals(""))
                        emailInputLayout.setError(emptyFieldErrorStr);
                    if (password.equals(""))
                        passwordInputLayout.setError(emptyFieldErrorStr);
                }else {
                    if(Build.VERSION.SDK_INT>=23){

                        int hasLocationPermission= ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
                        int hasLocationPermission1= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION);
                        if(hasLocationPermission!= PackageManager.PERMISSION_GRANTED || hasLocationPermission1!=PackageManager.PERMISSION_GRANTED){

                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},LOCATION_PERMISSION_REQUEST);
                        }
                        else {
                            progressBar.setVisibility(View.VISIBLE);
                            registerClass.signInUser(email,password);
                        }
                    }
                    else{

                        progressBar.setVisibility(View.VISIBLE);
                        registerClass.signInUser(email,password);
                    }
                }
            }
        });
        return root;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==LOCATION_PERMISSION_REQUEST){

            if(grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED){

                progressBar.setVisibility(View.VISIBLE);
                registerClass.signInUser(email,password);
            }
        }
    }



    @Override
    public void onFailedSignIn(String problem) {

        progressBar.setVisibility(View.INVISIBLE);
        switch (problem){
            case "The email address is badly formatted.":
                String error = "email_badly_formatted";
                emailInputLayout.setError(error);
                break;
            case "There is no user record corresponding to this identifier. The user may have been deleted.":
                String error2 =  "no_such_user";
                emailInputLayout.setError(error2);
                break;
            case "The password is invalid or the user does not have a password.":
                String error3 = "wrong_password";
                passwordInputLayout.setError(error3);
                break;
            default:
                String error4 ="failed_sign_in";
                Toast.makeText(getContext(),error4,Toast.LENGTH_LONG).show();
                break;

        }
    }
}
