package com.thewikky.wikkymaps.Act;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.thewikky.wikkymaps.Connection.ConexaoFirebase;
import com.thewikky.wikkymaps.Dao.ScriptDao;
import com.thewikky.wikkymaps.Dao.UserOpenHelper;
import com.thewikky.wikkymaps.Model.User;
import com.thewikky.wikkymaps.R;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private ImageView imgLogin;
    private EditText edtEmail, edtSenha;
    private CheckBox chkMostrarSenha, chkLembrarSenha;
    private Button btnFazerLogin;
    private TextView txtCadastroUsuario;
    private String itEmail, itSenha;

    private FirebaseAuth auth;

    private SQLiteDatabase conexao;
    private ScriptDao dao;
    private UserOpenHelper openHelper;
    private List<User> user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login");

        createConnection();
        user = new ArrayList<>();
        user = dao.readAll();

        imgLogin           = (ImageView)findViewById(R.id.imgLogin);
        edtEmail           = (EditText) findViewById(R.id.edtEmail);
        edtSenha           = (EditText) findViewById(R.id.edtSenha);
        chkMostrarSenha    = (CheckBox) findViewById(R.id.chkMostrarSenha);
        chkLembrarSenha    = (CheckBox) findViewById(R.id.chkLembrarSenha);
        btnFazerLogin      = (Button)   findViewById(R.id.btnFazerLogin);
        txtCadastroUsuario = (TextView) findViewById(R.id.txtCadastroUsuario);
        itEmail = "";
        itSenha = "";

        //Checar se existe lembrar email e senha\\
        if(user.size() > 0 ){
            String emailC   = user.get(0).getEmail();
            String senhaC   = user.get(0).getSenha();
            String remember = user.get(0).getLembrar();
            if(remember.equals("1")){
                edtEmail.setText( emailC );
                edtSenha.setText( senhaC );
                chkLembrarSenha.setChecked(true);
            }
        }

        btnFazerLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString();
                String senha = edtSenha.getText().toString();

                if( ( email.equals("") ) || (senha.equals("")) ){
                    Toast.makeText(LoginActivity.this, "Como deseja fazer Login sem Inserir os Dados?", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(LoginActivity.this, "Conectando...", Toast.LENGTH_SHORT).show();
                    btnFazerLogin.setEnabled(false);
                    //updateCheck();
                    startLogin(email, senha);
                }
            }
        });

        txtCadastroUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirCadastro();
            }
        });
    }//fim onCreate

    private void startLogin(String email, String senha) {
        updateCheck();
        auth = ConexaoFirebase.getAuth();
        auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if ( task.isSuccessful() ){
                    Toast.makeText(LoginActivity.this, "Login Efetuado com Sucesso.", Toast.LENGTH_LONG).show();
                    abrirPrincipal();
                    btnFazerLogin.setEnabled(true);
                }else{
                    Toast.makeText(LoginActivity.this, "Algo está Incorreto.", Toast.LENGTH_SHORT).show();
                    btnFazerLogin.setEnabled(true);
                }
            }
        });
    }

    private void abrirPrincipal(){
        Intent it = new Intent(LoginActivity.this, ActPrincipal.class);
        startActivity(it);
    }
    private void abrirCadastro(){
        finish();
        Intent it = new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity(it);
    }

    private void createConnection(){
        try{
            openHelper = new UserOpenHelper(this);
            conexao = openHelper.getWritableDatabase();
            Toast.makeText(this, "Successful.", Toast.LENGTH_SHORT).show();
            dao = new ScriptDao(conexao);
        }catch (SQLiteException ex) {
            AlertDialog.Builder dlg = new AlertDialog.Builder(this);
            dlg.setTitle("Erro");
            dlg.setMessage( ex.getMessage() );
            dlg.setNeutralButton("OK", null);
            dlg.show();
        }
    }

    private void updateCheck(){
        if( (user.size() == 0) && ( chkLembrarSenha.isChecked()) ){//caso nunca tenha marcado pra lembrar senha
            String emailC = edtEmail.getText().toString();
            String senhaC = edtSenha.getText().toString();
            User u = new User();
            u.setCodigo(1);
            u.setEmail(emailC);
            u.setSenha(senhaC);
            u.setLembrar("1");
            dao.create(u);
            //Toast.makeText(this, "Entrou 1", Toast.LENGTH_SHORT).show();
        }
        if( user.size() >= 1 && !( chkLembrarSenha.isChecked() ) ){//caso nao queira mais lembrar senha
            String emailC = edtEmail.getText().toString();
            String senhaC = edtSenha.getText().toString();
            User u = new User();
            u.setCodigo(1);
            u.setEmail( emailC );
            u.setSenha( senhaC );
            u.setLembrar("0");
            dao.update(u);
            //Toast.makeText(this, "Entrou 2", Toast.LENGTH_SHORT).show();
        }
        if( user.size() >= 1 && chkLembrarSenha.isChecked() ){//caso ja tenha lembrado senha e quer continuar lembrando
            String emailC = edtEmail.getText().toString();
            String senhaC = edtSenha.getText().toString();
            User u = new User();
            u.setCodigo(1);
            u.setEmail( emailC );
            u.setSenha( senhaC );
            u.setLembrar("1");
            dao.update(u);
            //Toast.makeText(this, "Entrou 3", Toast.LENGTH_SHORT).show();
        }
    }
}
