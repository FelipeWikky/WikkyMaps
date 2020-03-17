package com.thewikky.wikkymaps.Model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.thewikky.wikkymaps.Connection.ConexaoFirebase;

import java.util.HashMap;
import java.util.Map;

public class LocationU {
    private String latitude;
    private String longitude;
    private String nome;
    private String oldNome;
    private int number;

    public LocationU() {
    }

    public void save(String uid){
        DatabaseReference reference = ConexaoFirebase.getDatabase();
        reference.child("usuario").child(uid).child("marker").child(nome).setValue(this);
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> HashMapLoc = new HashMap<>();

        HashMapLoc.put( "nome", getNome() );
        HashMapLoc.put( "latitude", getLatitude() );
        HashMapLoc.put( "longitude", getLongitude() );

        return HashMapLoc;
    }

    public void edit2(String uid){
        DatabaseReference reference = ConexaoFirebase.getDatabase().child("usuario").child(uid).child("marker");
        reference.child( oldNome ).removeValue();
        save(uid);
    }

    public String getNome(){
        return nome;
    }

    public void setNome(String nome){
        this.nome = nome;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getOldNome() {
        return oldNome;
    }

    public void setOldNome(String oldName) {
        this.oldNome = oldName;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
