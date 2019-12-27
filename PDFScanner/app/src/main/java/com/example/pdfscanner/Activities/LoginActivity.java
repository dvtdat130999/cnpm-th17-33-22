package com.example.pdfscanner.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.pdfscanner.R;

public class LoginActivity extends AppCompatActivity {
    TextView txtvRegister;
    EditText edtUsername,edtPassword;
    Button btnSignIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtvRegister=(TextView)findViewById(R.id.txtvRegister);
        edtUsername=(EditText)findViewById(R.id.edtUsername);
        edtPassword=(EditText)findViewById(R.id.edtPassword);
        btnSignIn=(Button)findViewById(R.id.btnSignIn);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username=edtUsername.getText().toString();
                String password=edtPassword.getText().toString();

                if(username.equals("admin") && password.equals("admin"))
                {
                    android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("LoginActivity success!").create().show();
                    Intent mainlayout = new Intent (LoginActivity.this, MainActivity.class)
                    startActivity(mainlayout);
                }
                else
                {
                    android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("LoginActivity failed!").setNegativeButton("Retry",null).create().show();

                }

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
            }
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
