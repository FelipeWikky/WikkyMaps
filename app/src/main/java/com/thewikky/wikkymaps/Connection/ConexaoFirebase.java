package com.thewikky.wikkymaps.Connection;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thewikky.wikkymaps.Model.DadosU;

import java.util.ArrayList;

public class ConexaoFirebase {

    private static DatabaseReference reference;
    private static FirebaseAuth auth;

    private static ValueEventListener valueEventListener;

    public static DatabaseReference getDatabase(){
        if(reference == null){
            reference = FirebaseDatabase.getInstance().getReference();
        }
        return reference;
    }

    public static FirebaseAuth getAuth(){
        if(auth==null){
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

    public static DatabaseReference getSpecific(String uid){
        reference = ConexaoFirebase.getDatabase().child("usuario").child(uid).child("marker");
        return reference;
    }

    public static DadosU myDados(String myUid){
        DadosU myDadosU = new DadosU();
        final ArrayList<DadosU> arrayDados = new ArrayList<>();

        reference = getDatabase().child("usuario").child(myUid).child("dados");

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
