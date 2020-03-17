package com.thewikky.wikkymaps.Model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.thewikky.wikkymaps.Connection.ConexaoFirebase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DadosU {

    private String uid;;

    private String emailLogin;
    private String senhaLogin;

    private String nome;
    private String cpf;
    private String dtNasc;
    private String cidNasc; //município que nasceu
    private String estNasc;

    private String racacor;
    private String sexo;

    private String cep;
    private String endereco;
    private String bairro;
    private String cidade;
    private String estado;
    private String pais;

    private String telefone;
    private String email;

    public DadosU(){
    }

    public void save(String thisUid){
        DatabaseReference reference = ConexaoFirebase.getDatabase();
        //reference.child("usuario").child(thisUid).child("dados").setValue(this);
        reference.child("usuario").child(uid).child("dados").child("my").setValue(this);
        //reference.child("usuario").child(uid).child("marker").child(nome).setValue(this);
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> HashMapLoc = new HashMap<>();

        HashMapLoc.put( "nome", getNome() );
        HashMapLoc.put( "cpf", getCpf() );
        HashMapLoc.put( "dtNasc", getDtNasc() );
        HashMapLoc.put( "cidNasc", getCidNasc() );
        HashMapLoc.put( "estNasc", getEstNasc() );
        HashMapLoc.put( "racacor", getRacacor() );
        HashMapLoc.put( "sexo", getSexo() );
        HashMapLoc.put( "cep", getCep() );
        HashMapLoc.put( "endereco", getEndereco() );
        HashMapLoc.put( "bairro", getBairro() );
        HashMapLoc.put( "cidade", getCidade() );
        HashMapLoc.put( "estado", getEstado() );
        HashMapLoc.put( "pais", getPais() );
        HashMapLoc.put( "telefone", getTelefone() );
        HashMapLoc.put( "email", getEmail() );

        return HashMapLoc;
    }

    public void update(){
        DatabaseReference reference = ConexaoFirebase.getDatabase();
        reference.child("usuario").child(uid).child("dados").setValue(this);
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmailLogin() {
        return emailLogin;
    }

    public void setEmailLogin(String emailLogin) {
        this.emailLogin = emailLogin;
    }

    public String getSenhaLogin() {
        return senhaLogin;
    }

    public void setSenhaLogin(String senhaLogin) {
        this.senhaLogin = senhaLogin;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getDtNasc() {
        return dtNasc;
    }

    public void setDtNasc(String dtNasc) {
        this.dtNasc = dtNasc;
    }

    public String getCidNasc() {
        return cidNasc;
    }

    public void setCidNasc(String cidNasc) {
        this.cidNasc = cidNasc;
    }

    public String getEstNasc() {
        return estNasc;
    }

    public void setEstNasc(String estNasc) {
        this.estNasc = estNasc;
    }

    public String getRacacor() {
        return racacor;
    }

    public void setRacacor(String racacor) {
        this.racacor = racacor;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
