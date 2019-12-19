package com.example.pdfscanner.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.pdfscanner.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    TextView txtvSignIn;
    EditText edtFName,edtLName,edtAddress,edtPhone,edtUsername,edtPassword,edtBirthdate;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnSignUp=(Button)findViewById(R.id.btnSignUp);

        txtvSignIn=(TextView)findViewById(R.id.txtvSignIn);
        edtFName=(EditText)findViewById(R.id.edtFirstName);
        edtLName=(EditText)findViewById(R.id.edtLastName);
        edtAddress=(EditText)findViewById(R.id.edtAddress);
        edtPhone=(EditText)findViewById(R.id.edtPhone);
        edtBirthdate=(EditText)findViewById(R.id.edtBirthdate);
        edtUsername=(EditText)findViewById(R.id.edtUsername);
        edtPassword=(EditText)findViewById(R.id.edtPassword);

        btnSignUp.setOnClickListener(this);
        txtvSignIn.setOnClickListener(this);
    }
    @Override
    public void onClick(View view){
        switch(view.getId()) {
            case R.id.btnSignUp:
                String fname=edtFName.getText().toString();
                String lname=edtLName.getText().toString();
                String address=edtAddress.getText().toString();
                String phone=edtPhone.getText().toString();
                String birthdate=edtBirthdate.getText().toString();
                String username=edtUsername.getText().toString();
                String password=edtPassword.getText().toString();

                if(fname!=""&&lname!=""&&address!=""&&phone!=""&&birthdate!=""&&username!=""&&password!="")
                {
                    android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage("RegisterActivity success!").create().show();
                }
                if(fname!=""&&lname!=""&&address!=""&&phone!=""&&birthdate!=""&&username!=""&&password!="")
                {
                    android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage("RegisterActivity failed!").create().show();
                }
                break;
            case R.id.txtvSignIn:
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
                break;
            case R.id.edtBirthdate:
                pickDate();
                break;

        }
    }
    private void pickDate(){
        final Calendar calendar=Calendar.getInstance();
        int day=calendar.get(Calendar.DATE);
        int month=calendar.get(Calendar.MONTH);
        int year=calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(i,i1,i2);
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
                edtBirthdate.setText(simpleDateFormat.format(calendar.getTime()));
            }
        },year,month,day);
        datePickerDialog.show();
    }
}
