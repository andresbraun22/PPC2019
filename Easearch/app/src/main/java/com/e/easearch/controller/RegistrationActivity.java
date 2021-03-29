package com.e.easearch.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.e.easearch.R;
import com.e.easearch.dao.UserDAO;
import com.e.easearch.model.User;

import java.util.UUID;

public class RegistrationActivity extends Activity implements View.OnClickListener{

    private EditText etName, etSurname, etUserName, etPassword1, etPassword2;
    private Button btnRegister;

    private UserDAO userDAO = new UserDAO(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        etName = findViewById(R.id.etName);
        etSurname = findViewById(R.id.etSurname);
        etUserName = findViewById(R.id.etUserName);
        etPassword1 = findViewById(R.id.etPassword1);
        etPassword2 = findViewById(R.id.etPassword2);

        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);

    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btnRegister:

                User u = new User();
                u.setUid(UUID.randomUUID().toString());
                u.setName(etName.getText().toString());
                u.setSurname(etSurname.getText().toString());
                u.setUsername(etUserName.getText().toString());
                u.setPassword(etPassword1.getText().toString());

                String p1 = etPassword1.getText().toString();
                String p2 = etPassword2.getText().toString();

                if(!u.isNull()||p2.equals("")){
                    validation();
                    break;
                }

                if(p1.equals(p2)){
                    if(userDAO.insertUser(u)){
                        Toast.makeText(this, R.string.reg_ok, Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(this, R.string.ya_reg, Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(this, R.string.pw_no_coincide, Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    public void validation(){
        String n = etName.getText().toString();
        String s = etSurname.getText().toString();
        String un = etUserName.getText().toString();
        String pw1 = etPassword1.getText().toString();
        String pw2 = etPassword2.getText().toString();
        if(n.equals("")){
            etName.setError("Required");
        } else if(s.equals("")){
            etSurname.setError("Required");
        } else if(un.equals("")){
            etUserName.setError("Required");
        } else if(pw1.equals("")){
            etPassword1.setError("Required");
        } else if(pw2.equals("")){
            etPassword2.setError("Required");
        }
    }

}

