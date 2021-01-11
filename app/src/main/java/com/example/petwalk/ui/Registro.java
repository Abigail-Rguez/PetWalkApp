package com.example.petwalk.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.petwalk.R;
import com.example.petwalk.models.Usuarios;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class Registro<pubilc> extends AppCompatActivity implements View.OnClickListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageButton profile = null;
    private EditText editText;
    private FirebaseAuth auth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        profile = findViewById(R.id.picture1);

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        db = firebaseDatabase.getReferenceFromUrl("https://petwalk-cb147-default-rtdb.firebaseio.com/");

        Button sign = findViewById(R.id.sign);
        sign.setOnClickListener(this);

    }

    public static String encode_save_Image(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        return imageEncoded;

    }

    public void dispatchTakePictureIntent(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            profile.setImageBitmap(imageBitmap);
        }
    }

    @Override
    public void onClick(View v) {
        Usuarios user = new Usuarios();
        editText = findViewById(R.id.name);
        user.setUsername(editText.getText().toString());
        editText = findViewById(R.id.phone);
        user.setUserphone(editText.getText().toString());
        editText = findViewById(R.id.mail);
        user.setUsermail(editText.getText().toString());
        editText = findViewById(R.id.password);
        user.setUserpass(editText.getText().toString());
        if( editText.getText().toString().length() >= 8 ){
            Bitmap image = ((BitmapDrawable) profile.getDrawable()).getBitmap();
            if (image != null) {
                user.setUserphoto(encode_save_Image(image));
                user.setUsertype(1);
                registrarUsuario(user);
            }else{
                Toast.makeText(getApplicationContext(), "Porfavor agrega una foto a tu perfil", Toast.LENGTH_SHORT).show();
            }
        }else
            Toast.makeText(getApplicationContext(), "La contraseña debe ser mayor de 8 caracteres", Toast.LENGTH_SHORT).show();

    }

    private void registrarUsuario(Usuarios user) {
        auth.createUserWithEmailAndPassword(user.getUsermail(), user.getUserpass())
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()){
                        DatabaseReference child = db.child("Users");
                        String id = db.push().getKey();

                        user.setUserid(id);
                        HashMap<String, Object> hm = new HashMap<>();
                        hm.put(id, user);

                        child.updateChildren(hm)
                                .addOnSuccessListener(v -> {
                                    Toast.makeText(this, "Registro completado, por favor inicia sesión", Toast.LENGTH_LONG).show();
                                    finish();
                                });
                    }else{

                    }
                });
    }
}

