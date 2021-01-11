package com.example.petwalk.ui.pets;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.petwalk.R;
import com.example.petwalk.models.PetModel;
import com.example.petwalk.models.Usuarios;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PetFragment extends Fragment {

    private SendViewModel sendViewModel;
    private static final int REQUEST_CODE = 100;
    private AdaptadorPets rAdapter = null;
    private ArrayList<PetModel> list = null;
    private Usuarios user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_send, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        user = (Usuarios)getActivity().getIntent().getSerializableExtra("user");

        getPetsData(user.getUserid());

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);

        fab.setOnClickListener(v -> {
            Intent form = new Intent(getContext(), AddPets.class);
            form.putExtra("usuario", user);
            startActivityForResult(form, REQUEST_CODE);
        });
    }

    private void getPetsData(String userid) {
        FirebaseDatabase fd = FirebaseDatabase.getInstance();
        DatabaseReference dr = fd.getReferenceFromUrl("https://petwalk-cb147-default-rtdb.firebaseio.com/").child("Pets");
        
        dr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list = new ArrayList<>();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    PetModel pet = ds.getValue(PetModel.class);
                    if( pet.getDuenio().equals( userid ) ){
                        list.add(pet);
                    }
                }
                ListView listaPets = getView().findViewById(R.id.my_recycler_view);
                rAdapter = new AdaptadorPets( getContext() );
                listaPets.setAdapter(rAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    class AdaptadorPets extends ArrayAdapter{

        Context _context;
        AdaptadorPets( Context context ){
            super(context, R.layout.pets_info, list);
            _context = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
            View item = inflater.inflate( R.layout.pets_info, parent, false );

            byte[] decodedString = Base64.decode(list.get(position).getPet_photo(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            ImageView iv = item.findViewById(R.id.imageView2);
            iv.setImageBitmap(decodedByte);

            TextView textView1 = item.findViewById( R.id.pet_name );
            textView1.setText(list.get(position).getPet_name() );
            textView1 = item.findViewById( R.id.pet_raza );
            textView1.setText(list.get(position).getPet_raza() );
            textView1 = item.findViewById( R.id.pet_color );
            textView1.setText(list.get(position).getPet_color() );
            textView1 = item.findViewById( R.id.pet_age);
            textView1.setText(list.get(position).getPet_age() );
            textView1 = item.findViewById( R.id.pet_size);
            textView1.setText(list.get(position).getPet_size() );
            textView1 = item.findViewById( R.id.last_walk);
            textView1.setText("Ultimo recorrido: "+list.get(position).getLast_walk() );
            textView1 = item.findViewById( R.id.total_distance);
            textView1.setText(list.get(position).getDistance() + " mts");
            return item;
        }
    }

}