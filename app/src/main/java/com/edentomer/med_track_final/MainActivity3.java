package com.edentomer.med_track_final;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.edentomer.med_track_final.R.id;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class MainActivity3 extends AppCompatActivity {

    private EditText editTextSignupFullName, editTextSignupEmail, editTextSignupDoB, editTextPwd, editTextSignupConfirmPwd;
    private ProgressBar progressBar;
    private static final String TAG= "SignUpActivity";
    private DatePickerDialog picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        getSupportActionBar().setTitle("Sign Up");

        Toast.makeText(MainActivity3.this, "You can Sign up now", Toast.LENGTH_LONG).show();

        progressBar = findViewById(R.id.progressBar);
        editTextSignupFullName = findViewById(R.id.editText_signup_full_name);
        editTextSignupEmail = findViewById(R.id.editText_signup_email);
        editTextSignupDoB = findViewById(R.id.editText_signup_dob);
        editTextPwd = findViewById(R.id.editText_signup_password);
        editTextSignupConfirmPwd = findViewById(R.id.editText_signup_confirm_password);

        //setting up date picker on edit text
        editTextSignupDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                //date picker
                picker = new DatePickerDialog(MainActivity3.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        editTextSignupDoB.setText(dayOfMonth+ "/"+ (month +1)+"/"+year);
                    }
                },year,month,day);
                picker.show();
            }
        });
        Button buttonSignup = findViewById(id.button_Signup);
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textFullName = editTextSignupFullName.getText().toString();
                String textEmail = editTextSignupEmail.getText().toString();
                String textDoB = editTextSignupDoB.getText().toString();
                String textPwd = editTextPwd.getText().toString();
                String textConfirmPwd = editTextSignupConfirmPwd.getText().toString();

                if (TextUtils.isEmpty(textFullName)) {
                    Toast.makeText(MainActivity3.this, "Please enter your full name", Toast.LENGTH_LONG).show();
                    editTextSignupFullName.setError("Full Name is required");
                    editTextSignupFullName.requestFocus();
                } else if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(MainActivity3.this, "Please enter your Email", Toast.LENGTH_LONG).show();
                    editTextSignupEmail.setError("Email is required");
                    editTextSignupEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(MainActivity3.this, "Please re-enter your Email ", Toast.LENGTH_LONG).show();
                    editTextSignupEmail.setError("Valid Email is required");
                    editTextSignupEmail.requestFocus();
                } else if (TextUtils.isEmpty(textDoB)) {
                    Toast.makeText(MainActivity3.this, "Please enter your Date of Birth", Toast.LENGTH_LONG).show();
                    editTextSignupDoB.setError("Date of birth is required");
                    editTextSignupDoB.requestFocus();
                } else if (TextUtils.isEmpty(textPwd)) {
                    Toast.makeText(MainActivity3.this, "Please enter your password", Toast.LENGTH_LONG).show();
                    editTextPwd.setError("Password id required");
                    editTextPwd.requestFocus();
                } else if (textPwd.length() < 6) {
                    Toast.makeText(MainActivity3.this, "Password should be at least 6 digits", Toast.LENGTH_LONG).show();
                    editTextPwd.setError("Password is too weak");
                    editTextPwd.requestFocus();
                } else if (TextUtils.isEmpty(textConfirmPwd)) {
                    Toast.makeText(MainActivity3.this, "Please confirm your password", Toast.LENGTH_LONG).show();
                    editTextSignupConfirmPwd.setError("Password Confirmation required");
                    editTextSignupConfirmPwd.requestFocus();
                } else if (!textPwd.equals(textConfirmPwd)) {
                    Toast.makeText(MainActivity3.this, "Please enter the same passwords", Toast.LENGTH_LONG).show();
                    editTextSignupConfirmPwd.setError("password Confirmation required");
                    editTextSignupConfirmPwd.requestFocus();
                    editTextPwd.clearComposingText();
                    editTextSignupConfirmPwd.clearComposingText();
                } else {
                    progressBar.setVisibility(view.VISIBLE);
                    signupUser(textFullName, textEmail, textDoB, textPwd);

                }
            }
        });
    }

    private void signupUser(String textFullName, String textEmail, String textDoB, String textPwd) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(textEmail,textPwd).addOnCompleteListener(MainActivity3.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity3.this,"User registered successfully",Toast.LENGTH_LONG).show();
                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    //Enter User data into firebase realtime database
                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textDoB);

                    //extracting use reference from database for register users
                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Register Users");
                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                //send verification email
                                firebaseUser.sendEmailVerification();

                                //update display name or user
                                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                                firebaseUser.updateProfile(profileChangeRequest);


                                Toast.makeText(MainActivity3.this,"user registered successfully. Please verify your email ",Toast.LENGTH_LONG);

                                //open user profile after successful registration
                                Intent intent = new Intent(MainActivity3.this,UserProfileActivity.class);
                                //to prevent user from returning back to the sign up activity
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish(); //to close activity
                            }else {
                                Toast.makeText(MainActivity3.this,"user registered failed. Please try again ",Toast.LENGTH_LONG);
                                progressBar.setVisibility(View.GONE);
                            }

                        }
                    });

                }else{
                    try {
                        throw task.getException();
                    }catch(FirebaseAuthWeakPasswordException e){
                        editTextPwd.setError("Your password is too weak,please use a mix of alphabets,numbers and special characters");
                        editTextPwd.requestFocus();
                        progressBar.setVisibility(View.GONE);
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        editTextSignupEmail.setError("Your Email is invalid or already taken,please re-enter email");
                        editTextSignupEmail.requestFocus();
                        progressBar.setVisibility(View.GONE);
                    }catch (FirebaseAuthUserCollisionException e){
                        editTextSignupEmail.setError("User is already registered with this email, please use another email");
                        editTextSignupEmail.requestFocus();
                        progressBar.setVisibility(View.GONE);
                    }catch (Exception e){
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(MainActivity3.this,e.getMessage(),Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        });
    }
}
    

           


