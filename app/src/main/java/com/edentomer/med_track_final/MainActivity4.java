package com.edentomer.med_track_final;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity4 extends AppCompatActivity {

    private EditText editTextLoginEmail,editTextLoginPwd;
    private ProgressBar progressBar;
    private FirebaseAuth authprofile;

    private static final String TAG ="SignIn Activity";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        getSupportActionBar().setTitle("Log-in");

        editTextLoginEmail=findViewById(R.id.editText_login_email);
        editTextLoginPwd=findViewById(R.id.editText_login_pwd);
        progressBar=findViewById(R.id.progressbar1);

        authprofile = FirebaseAuth.getInstance();
        //show hide password
        ImageView imageViewShowHidePwd = findViewById(R.id.imageView_show_hide_pwd);
        imageViewShowHidePwd.setImageResource(R.drawable.closeeye);
        ImageView imageViewEmail = findViewById(R.id.imageView_email);
        imageViewEmail.setImageResource(R.drawable.email);


        imageViewShowHidePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextLoginPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    editTextLoginPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageViewShowHidePwd.setImageResource(R.drawable.closeeye);
                }else{
                    editTextLoginPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowHidePwd.setImageResource(R.drawable.openeye);
                }
            }
        });


        //login user
        Button buttonLogin = findViewById(R.id.button_Login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textEmail = editTextLoginEmail.getText().toString();
                String textPwd = editTextLoginPwd.getText().toString();

                if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(MainActivity4.this,"Please enter your email",Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Email is required");
                    editTextLoginEmail.requestFocus();
                }else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(MainActivity4.this,"Please re-enter your email",Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Valid Email is required");
                    editTextLoginEmail.requestFocus();
                }else  if (TextUtils.isEmpty(textPwd)){
                    Toast.makeText(MainActivity4.this,"Please enter your password",Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Password is required");
                    editTextLoginEmail.requestFocus();
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(textEmail,textPwd);
                }
            }
        });






    }

    private void loginUser(String email, String pwd) {
        authprofile.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity4.this,"you are logged in now",Toast.LENGTH_SHORT).show();

                    //get the instance of the corrent user
                    FirebaseUser firebaseUser = authprofile.getCurrentUser();

                    //check if email is verified before user can access their profile
                    if(firebaseUser.isEmailVerified()){
                        Toast.makeText(MainActivity4.this,"you are loged in now",Toast.LENGTH_SHORT).show();

                        //open user profile

                    }else {
                        firebaseUser.sendEmailVerification();
                        authprofile.signOut();
                        showAlertDialog();
                    }
                }else{

                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        editTextLoginEmail.setError("user doesn't exist. Please try again");
                        editTextLoginEmail.requestFocus();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        editTextLoginEmail.setError("Invalid credentials. Please try again");
                        editTextLoginEmail.requestFocus();
                    }catch (Exception e){
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(MainActivity4.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(MainActivity4.this,"something went wrong",Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity4.this);
        builder.setTitle("Email is not verified");
        builder.setMessage("please verify your email to log in");
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //to email app in new window and not in app itself
                startActivity(intent);

            }
        });
        //create the alertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(authprofile.getCurrentUser()!=null){
            Toast.makeText(MainActivity4.this,"You are already logged in",Toast.LENGTH_SHORT).show();

            //start userprofileActivity
            startActivity(new Intent(MainActivity4.this,UserProfileActivity.class));
            finish();

        }else{
            Toast.makeText(MainActivity4.this,"You can log in now",Toast.LENGTH_SHORT).show();
        }
    }
}