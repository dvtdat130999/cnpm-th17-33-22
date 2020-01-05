package com.example.pdfscanner.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pdfscanner.R;

public class LoginActivity extends AppCompatActivity {
    TextView txtvRegister;
    EditText edtUsername, edtPassword;
    Button btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txtvRegister = (TextView) findViewById(R.id.txtvRegister);
        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();

                DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                // User user = new User(username, password);
                User currentUser = db.Authenticate(username, password);

                //Check Authentication is successful or not
                if (currentUser != null) {
                    //Snackbar.make(btnSignIn, "Successfully Logged in!", Snackbar.LENGTH_LONG).show();
                    android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("LoginActivity success!").create().show();
                    Intent mainlayout = new Intent(LoginActivity.this, ScanActivity.class);
                    startActivity(mainlayout);

                } else {

                    //User Logged in Failed
                    //Snackbar.make(btnSignIn, "Failed to log in , please try again", Snackbar.LENGTH_LONG).show();
                    android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("LoginActivity failed!").setNegativeButton("Retry",null).create().show();
                }
            }

//                if(username.equals("admin") && password.equals("admin"))
//                {
//                    android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(LoginActivity.this);
//                    builder.setMessage("LoginActivity success!").create().show();
//                    Intent mainlayout = new Intent (LoginActivity.this, ScanActivity.class);
//                    startActivity(mainlayout);
//                }
//                else
//                {
//                    android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(LoginActivity.this);
//                    builder.setMessage("LoginActivity failed!").setNegativeButton("Retry",null).create().show();
//
//                }

//                if(username!=""&&password!="")
//                {
//                    android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(LoginActivity.this);
//                    builder.setMessage("LoginActivity success!").create().show();
//
//
//                }
//                else
//                {
//                    android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(LoginActivity.this);
//                    builder.setMessage("LoginActivity failed!").setNegativeButton("Retry",null).create().show();
//
//                }
        });
        txtvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });
    }
}
