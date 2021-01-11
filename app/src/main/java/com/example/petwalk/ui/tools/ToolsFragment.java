package com.example.petwalk.ui.tools;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.petwalk.R;
import com.example.petwalk.models.Usuarios;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ToolsFragment extends Fragment {

    private ToolsViewModel toolsViewModel;
    private Usuarios user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        user = (Usuarios) getActivity().getIntent().getSerializableExtra("user");
        return inflater.inflate(R.layout.fragment_proveedor, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FirebaseDatabase fd = FirebaseDatabase.getInstance();
        DatabaseReference dr = fd.getReferenceFromUrl("https://petwalk-cb147-default-rtdb.firebaseio.com/").child("Users");

        Button btnSave = getActivity().findViewById(R.id.button3);

        btnSave.setOnClickListener(v -> {

            user.setUsertype( 2 );
            EditText edit = getActivity().findViewById(R.id.editText);
            user.setAboutme( edit.getText().toString() );
            edit = getActivity().findViewById(R.id.editText2);
            user.setPrice( edit.getText().toString() );

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put( user.getUserid(), user );

            dr.updateChildren( hashMap )
                    .addOnSuccessListener( sl -> {
                        Toast.makeText(getContext(), "Bienvenido al grupo de los paseadores", Toast.LENGTH_SHORT);
                    });

        });

    }
}