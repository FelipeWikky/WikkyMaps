package com.thewikky.wikkymaps.Fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.thewikky.wikkymaps.Connection.ConexaoFirebase;
import com.thewikky.wikkymaps.Connection.LocationAdapter;
import com.thewikky.wikkymaps.Model.LocationU;
import com.thewikky.wikkymaps.R;

import java.util.ArrayList;

public class MarkerFragment extends Fragment {

    public MarkerFragment() {
        // Required empty public constructor
    }

    private DatabaseReference reference;
    private ValueEventListener valueEventListener;
    private ArrayAdapter<LocationU> adapter;
    private ArrayList<LocationU> locs;
    private ListView listView;
    private Button btnFecharTelaMarker;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_marker, container, false);
        listView = (ListView) view.findViewById(R.id.lstMarker2);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locs = new ArrayList<>();
        callMarker();
        adapter = new LocationAdapter(getActivity(), locs);
        listView.setAdapter(adapter);
        auth = ConexaoFirebase.getAuth();
        user = auth.getCurrentUser();
        reference = ConexaoFirebase.getDatabase().child("usuario").child(user.getUid()).child("marker");
    }


    @Override
    public void onStart() {
        super.onStart();
        reference.addValueEventListener(valueEventListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        reference.removeEventListener(valueEventListener);
    }

    private void callMarker(){
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                locs.clear();
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    LocationU lU = data.getValue(LocationU.class);
                    locs.add(lU);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }
}
