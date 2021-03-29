package com.e.easearch.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.e.easearch.model.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserDAO {

    private ArrayList<User> users = new ArrayList<User>();

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;


    public UserDAO(Context c){
        initializeFirebase(c);
        loadUsersList();
    }


    public void initializeFirebase(Context c){
        FirebaseApp.initializeApp(c);    // Inicializamos Firebase pasandole el contexto
        firebaseDatabase = FirebaseDatabase.getInstance();      // La instancia
        databaseReference = firebaseDatabase.getReference();    // La referencia de lo que es nuestra BD
    }

    public void loadUsersList(){
        databaseReference.child("User").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();      // Limpiamos por si tiene algo almacenado en caché (persistencia)
                for(DataSnapshot objSnaptshot: dataSnapshot.getChildren()){
                    User u = objSnaptshot.getValue(User.class);
                    users.add(u);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public User login(String usrName, String pwd){
        User u = null;
        for(User ux : users) {
            if (ux.getUsername().equals(usrName) && ux.getPassword().equals(pwd)) {
                u = ux;
            }
        }
        return u;
    }

    public boolean insertUser(User u){
        if(!findUserByUserName(u.getUsername())){
            databaseReference.child("User").child(u.getUid()).setValue(u);
            return true;
        }
        return false;
    }

    public boolean findUserByUserName(String usrName){
        boolean finded = false;
        for(User ux : users){
            if(ux.getUsername().equals(usrName)){
                finded = true;
            }
        }
        return finded;
    }

}
