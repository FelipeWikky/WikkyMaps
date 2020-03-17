package com.thewikky.wikkymaps.Model;

public class User {

    private int codigo;
    private String email;
    private String senha;
    private String lembrar;

    public User() {
    }

    public User(int cod, String email, String senha, String lembr){
        this.codigo = cod;
        this.email = email;
        this.senha = senha;
        this.lembrar = lembr;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getLembrar() {
        return lembrar;
    }

    public void setLembrar(String lembrar) {
        this.lembrar = lembrar;
    }
}
