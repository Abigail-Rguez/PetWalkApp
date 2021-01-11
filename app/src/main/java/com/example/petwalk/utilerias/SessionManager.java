package com.example.petwalk.utilerias;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import com.example.petwalk.Home;
import com.example.petwalk.MainActivity;
import com.example.petwalk.models.Usuarios;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SessionManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    public SessionManager(Context context) {
        this._context = context;
        pref = PreferenceManager.getDefaultSharedPreferences(this._context);
        editor = pref.edit();
    }

    public void createLoginSession(String email){
        editor.putString("email",email);
        editor.commit();
    }

    public void checkLogin(){
        if(pref.contains("email")){
            Intent i = new Intent(_context, Home.class);
            DatabaseReference root = FirebaseDatabase.getInstance().getReferenceFromUrl("https://petwalk-cb147-default-rtdb.firebaseio.com/");
            DatabaseReference users = root.child("Users");
            users.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for ( DataSnapshot u : dataSnapshot.getChildren() ) {
                        Usuarios login = u.getValue(Usuarios.class);
                        if( login.getUsermail().equals( pref.getString("email", "") ) ){
                            i.putExtra("user", login);
                        }
                    }
                    i.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                    _context.startActivity( i );
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void LogOutUser(){
        editor.clear();
        editor.commit();
        Intent i = new Intent( _context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        _context.startActivity(i);
    }

    public void enableService() {
        editor.putInt("service",1);
        editor.commit();
    }

    public boolean checkServices() {
        if(pref.contains("service")){
            return true;
        }
        return false;
    }

    public void disableService() {
        editor.remove("service");
        editor.commit();
    }
}
