package com.example.petwalk.ui.walks;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.petwalk.R;
import com.example.petwalk.models.Usuarios;
import com.example.petwalk.models.WalksModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class WalksFragment extends Fragment {

    private FirebaseDatabase fd = null;
    private DatabaseReference myRef;
    private ValueEventListener event = null;
    private ArrayList<WalksModel> listaPaseos;
    private Usuarios user;
    private AdaptadorPaseo adapter;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        user = (Usuarios) getActivity().getIntent().getSerializableExtra("user");
        fd = FirebaseDatabase.getInstance();
        myRef = fd.getReferenceFromUrl("https://petwalk-cb147-default-rtdb.firebaseio.com/").child("Walks");
        getWalksData();


        return root;
    }

    private void getWalksData() {
        if( event != null ){
            myRef.removeEventListener(event);
        }
        event = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaPaseos = new ArrayList<>();
                for (DataSnapshot pet: dataSnapshot.getChildren()) {
                    WalksModel walk = pet.getValue( WalksModel.class );
                    if( user.getUsertype() == 1 ){
                        if( walk.getIdDuenio().equals( user.getUserid() )){
                            listaPaseos.add( walk );
                        }
                    }else{
                        if( walk.getIdPaseador().equals( user.getUserid() )){
                            listaPaseos.add( walk );
                        }
                    }
                }

                if( getView() != null ){

                    ListView list = getView().findViewById( R.id.list_walks );
                    adapter = new AdaptadorPaseo( getContext());
                    list.invalidateViews();
                    list.setAdapter(adapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        myRef.addValueEventListener(event);
    }

    class AdaptadorPaseo extends ArrayAdapter {

        Context appCompatActivity;

        AdaptadorPaseo ( Context context ){
            super( context, R.layout.wall_list, listaPaseos);
            appCompatActivity = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) appCompatActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
            View item = inflater.inflate( R.layout.wall_list, parent, false );

            ((TextView)item.findViewById(R.id.textView5)).setText( "Paseo realizado el: "+listaPaseos.get(position).getFecha() );
            ((TextView)item.findViewById(R.id.textView6)).setText( "Precio del paseo: "+listaPaseos.get(position).getTotal() );

            return item;
        }
    }

}