package com.e.easearch.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.e.easearch.R;
import com.e.easearch.dao.UserDAO;
import com.e.easearch.model.User;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etUsername, etPassword;
    private Button btnLogin, btnReg;

    private UserDAO userDAO = new UserDAO(this);

    SharedPreferences pref;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        btnReg = findViewById(R.id.btnReg);
        btnReg.setOnClickListener(this);

        pref = getSharedPreferences("user_details", Context.MODE_PRIVATE);

        intent = new Intent(this,PanelActivity.class);
        if(pref.contains("username") && pref.contains("password")){
            startActivity(intent);
            finish();
        }

    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btnLogin:

                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                User u = userDAO.login(username, password);

                if (username.equals("") || password.equals("")){
                    Toast.makeText(this, R.string.campos_vacios, Toast.LENGTH_SHORT).show();
                } else if(u != null){
                    savePreferences(username, password, u.getName());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, R.string.datos_incorrectos, Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.btnReg:

                Intent intentReg = new Intent(this, RegistrationActivity.class);
                startActivity(intentReg);
                break;
        }
    }

    private void savePreferences(String usrname, String pwd, String name){
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("username", usrname);           // par clave,valor
        editor.putString("password", pwd);               // par clave,valor
        editor.putString("name", name);               // par clave,valor
        editor.commit();
    }

}
