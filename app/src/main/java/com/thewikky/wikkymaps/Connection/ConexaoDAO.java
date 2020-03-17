package com.thewikky.wikkymaps.Connection;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.thewikky.wikkymaps.Model.DadosU;

import java.util.ArrayList;

public class ConexaoDAO {

    private DatabaseReference reference;
    private ValueEventListener valueEventListener;

    public DadosU myDados(String myUid){
        DadosU myDadosU = new DadosU();
        final ArrayList<DadosU> arrayDados = new ArrayList<>();

        reference = ConexaoFirebase.getDatabase().child("usuario").child(myUid).child("dados");

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for( DataSnapshot dados : dataSnapshot.getChildren() ){
                    DadosU newDados = dados.getValue(DadosU.class);
                    arrayDados.add(newDados);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        myDadosU = arrayDados.get(0);
        return myDadosU;
    }
}
