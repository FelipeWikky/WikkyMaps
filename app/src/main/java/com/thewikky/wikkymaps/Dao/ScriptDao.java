package com.thewikky.wikkymaps.Dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.thewikky.wikkymaps.Model.User;
import java.util.ArrayList;
import java.util.List;

public class ScriptDao {

    private SQLiteDatabase conexao;

    public ScriptDao(SQLiteDatabase connection) {
        this.conexao = connection;
    }

    /* C R U D */

    public void create(User user){
        ContentValues values = new ContentValues();
        values.put("CODIGO", 1);
        values.put("EMAIL", user.getEmail());
        values.put("SENHA", user.getSenha());
        values.put("LEMBRAR", user.getLembrar());
        conexao.insertOrThrow("INFO", null, values);
    }

    public List<User> readAll(){
        List<User> users = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT CODIGO, EMAIL, SENHA, LEMBRAR FROM INFO ");

        Cursor result = conexao.rawQuery(sql.toString(), null);
        if(result.getCount() > 0){
            result.moveToFirst();
            do{
                User user = new User();
                user.setEmail  ( result.getString( result.getColumnIndexOrThrow("EMAIL"  ) ) );
                user.setSenha  ( result.getString( result.getColumnIndexOrThrow("SENHA"  ) ) );
                user.setLembrar( result.getString( result.getColumnIndexOrThrow("LEMBRAR") ) );
                users.add(user);
            }while (result.moveToNext()) ;
        }
        return users;
    }

    public User readOne(){
        User user  = new User();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT CODIGO, EMAIL, SENHA, LEMBRAR ");
        sql.append(" FROM INFO ");
        sql.append(" WHERE CODIGO = ? ");

        String[] parametro = new String[1];
        parametro[0] = String.valueOf(1);

        Cursor result = conexao.rawQuery(sql.toString(), parametro);

        if(result.getCount() > 0){
            result.moveToFirst();
            user.setEmail  ( result.getString( result.getColumnIndexOrThrow("EMAIL")   ) );
            user.setSenha  ( result.getString( result.getColumnIndexOrThrow("SENHA")   ) );
            user.setLembrar( result.getString( result.getColumnIndexOrThrow("LEMBRAR") ) );
            return user;
        }
        return null;
    }

    public void update(User user){
        ContentValues values = new ContentValues();
        values.put("CODIGO", "1");
        values.put("EMAIL", user.getEmail());
        values.put("SENHA", user.getSenha());
        values.put("LEMBRAR", user.getLembrar());

        String[] parametro = new String[1];
        parametro[0] = String.valueOf(1);
        conexao.update("INFO", values, "CODIGO = ?", parametro);
    }

    public void delete(){

    }
}
