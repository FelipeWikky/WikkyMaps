package com.thewikky.wikkymaps.Dao;

public class ScriptSQL {

    public static String createTable(){
        StringBuilder sql = new StringBuilder();

        sql.append("CREATE TABLE INFO (");
        sql.append("CODIGO INTEGER NOT NULL, ");
        sql.append("EMAIL VARCHAR (50) NOT NULL DEFAULT(''), ");
        sql.append("SENHA VARCHAR (50) NOT NULL DEFAULT(''), ");
        sql.append("LEMBRAR VARCHAR(2) NOT NULL DEFAULT(0) );");

        return sql.toString();
    }

    public static String deleteTable(){
        StringBuilder sql = new StringBuilder();

        sql.append("DROP TABLE INFO;");
        System.out.println("Deletado");
        return sql.toString();
    }

}
