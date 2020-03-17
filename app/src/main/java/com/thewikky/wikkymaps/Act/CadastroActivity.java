package com.thewikky.wikkymaps.Act;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.thewikky.wikkymaps.Connection.ConexaoFirebase;
import com.thewikky.wikkymaps.Model.DadosU;
import com.thewikky.wikkymaps.R;

public class CadastroActivity extends AppCompatActivity {

    private EditText edtEmailParaLogin, edtSenhaParaLogin;
    private EditText edtNomeCompleto, edtCpf, edtDtNasc, edtCidNasc;
    private Spinner spnRacaCor, spnEstNasc;
    private RadioGroup rgSexo;
    private RadioButton rbMasc, rbFem;
    private EditText edtCep, edtEndereco, edtBairro, edtCidade, edtEstado, edtPais;
    private EditText edtTelefone, edtEmailContato;
    private String newName;
    private AlertDialog alert;
    private int form;
    private DadosU dadosU;
    private String uUid;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        setTitle("Cadastro");
        auth = ConexaoFirebase.getAuth();
        user = auth.getCurrentUser();
        form = 0;
        //uUid = UidGenerate.generate();

        startComponent();
        callMask();

        Intent it = getIntent();
        form = it.getIntExtra("form", 0);
        if(form == 1){
            setTitle("Atualizar Dados");
            DadosU ndadosU = (DadosU) it.getSerializableExtra("dados");
            getDados(ndadosU);
        }
    }//fim onCreate

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Voltar");
        builder.setMessage("Certeza que deseja Volta?\nToda informação será perdida.");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //super.onBackPressed();
                finish();
            }
        });
        builder.setNegativeButton("Não", null);
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cadastro, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_cad_criar:
                AlertDialog.Builder builder = new AlertDialog.Builder(CadastroActivity.this);
                builder.setTitle("Criar Cadastro");
                builder.setMessage("Criar Apenas Login ou Cadastro Completo com Login?");
                builder.setPositiveButton("Completo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = edtEmailParaLogin.getText().toString();
                        String senha = edtSenhaParaLogin.getText().toString();
                        DadosU fillDadosU = fillGetDados();
                        registerWithLogin(email, senha, fillDadosU);
                    }
                });
                builder.setNegativeButton("Apenas Login", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = edtEmailParaLogin.getText().toString();
                        String senha = edtSenhaParaLogin.getText().toString();
                        registerOnlyLogin(email, senha);
                    }
                });
                builder.show();
                break;

            case R.id.action_cad_limpar:
                clearComponent();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startComponent(){
        edtEmailParaLogin = (EditText)   findViewById(R.id.edtEmailParaLogin);
        edtSenhaParaLogin = (EditText)   findViewById(R.id.edtSenhaParaLogin);
        edtNomeCompleto   = (EditText)   findViewById(R.id.edtNomeCompleto);
        edtCpf            = (EditText)   findViewById(R.id.edtCpf);
        edtDtNasc         = (EditText)   findViewById(R.id.edtDtNasc);
        edtCidNasc        = (EditText)   findViewById(R.id.edtCidNasc);
        spnEstNasc        = (Spinner)    findViewById(R.id.spnEstNasc);
        spnRacaCor        = (Spinner)    findViewById(R.id.spnRacaCor);
        rgSexo            = (RadioGroup) findViewById(R.id.rgSexo);
        rbMasc            = (RadioButton)findViewById(R.id.rbMasc);
        rbFem             = (RadioButton)findViewById(R.id.rbFem);
        edtCep           = (EditText)    findViewById(R.id.edtCep);
        edtEndereco       = (EditText)   findViewById(R.id.edtEndereco);
        edtBairro         = (EditText)   findViewById(R.id.edtBairro);
        edtCidade         = (EditText)   findViewById(R.id.edtCidade);
        edtEstado         = (EditText)   findViewById(R.id.edtEstado);
        edtPais           = (EditText)   findViewById(R.id.edtPais);
        edtTelefone       = (EditText)   findViewById(R.id.edtTelefone);
        edtEmailContato   = (EditText)   findViewById(R.id.edtEmailContato);
    }

    private void callMask(){
        //Máscara para Data de Nascimento
        SimpleMaskFormatter smfDtNasc = new SimpleMaskFormatter("NN/NN/NNNN");
        MaskTextWatcher mtwDtNasc = new MaskTextWatcher(edtDtNasc, smfDtNasc);
        edtDtNasc.addTextChangedListener(mtwDtNasc);
        //Máscara para Telefone
        SimpleMaskFormatter smfTelefone = new SimpleMaskFormatter("(NN)NNNN-NNNN");
        MaskTextWatcher mtwTelefone = new MaskTextWatcher(edtTelefone, smfTelefone);
        edtTelefone.addTextChangedListener(mtwTelefone);
        //Máscara para CPF
        SimpleMaskFormatter smfCpf = new SimpleMaskFormatter("NNN.NNN.NNN-NN");
        MaskTextWatcher mtwCpf = new MaskTextWatcher(edtCpf, smfCpf);
        edtCpf.addTextChangedListener(mtwCpf);
        //Máscara para CEP
        SimpleMaskFormatter smfCep = new SimpleMaskFormatter("NN.NNN-NNN");
        MaskTextWatcher mtwCep = new MaskTextWatcher(edtCep, smfCep);
        edtCep.addTextChangedListener(mtwCep);
    }

    private void registerWithLogin(String email, String senha, DadosU dados){
        if( checkComponent(0) ){
            registerOnlyLogin(email, senha);
            dados.save( auth.getUid() );
        }
    }

    private void registerOnlyLogin(String email, String senha){
        final String putEmail = email;
        final String putSenha = senha;
        if ( checkEmailSenha(putEmail, putSenha) ){
            auth = ConexaoFirebase.getAuth();
            auth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener
                    (CadastroActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if( task.isSuccessful() ){
                        uUid = auth.getUid();
                        LoginParaSetID(putEmail, putSenha);
                        Toast.makeText(CadastroActivity.this, "Cadastro de Login Realizado com Sucesso.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void LoginParaSetID(String email, String senha){
        auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    user = auth.getCurrentUser();
                    Toast.makeText(CadastroActivity.this, "This uUid:"+uUid, Toast.LENGTH_SHORT).show();
                }
            }
        });
        /*AlertDialog.Builder builder = new AlertDialog.Builder(CadastroActivity.this);
        final EditText input = new EditText(CadastroActivity.this);
        input.setInputType(1);
        input.setHint("Insira Aqui..");
        builder.setView(input);
        builder.setTitle("Identificador");
        builder.setMessage("Defina um Nome como Identificador");
        builder.setPositiveButton("Pronto", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newName = input.getText().toString();
                user = auth.getCurrentUser();
                UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                        .setDisplayName( newName ).build();
                user.updateProfile(profile);
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();*/
    }

    private DadosU fillGetDados(){
        DadosU nDU      = new DadosU();
        String estNasc  = String.valueOf( spnEstNasc.getSelectedItem() );
        String racaCor  = String.valueOf( spnRacaCor.getSelectedItem() );
        String sexo     = "";
        if(rbMasc.isChecked()){
            sexo = "Masculino";
        }
        if(rbFem.isChecked()){
            sexo = "Feminino";
        }
        nDU.setUid( auth.getUid() );
        nDU.setNome(edtNomeCompleto.getText().toString());
        nDU.setCpf(edtCpf.getText().toString());
        nDU.setDtNasc(edtDtNasc.getText().toString());
        nDU.setCidNasc(edtCidNasc.getText().toString());
        nDU.setEstNasc(estNasc);
        nDU.setRacacor(racaCor);
        nDU.setSexo(sexo);
        nDU.setCep(edtCep.getText().toString());
        nDU.setEndereco(edtEndereco.getText().toString());
        nDU.setBairro(edtBairro.getText().toString());
        nDU.setCidade(edtCidade.getText().toString());
        nDU.setEstado(edtEstado.getText().toString());
        nDU.setPais(edtPais.getText().toString());
        nDU.setTelefone(edtTelefone.getText().toString());
        nDU.setEmail(edtEmailContato.getText().toString());
        return nDU;
    }

    private void getDados(DadosU getU){
        edtNomeCompleto.setText(getU.getNome());
        edtCpf         .setText(getU.getCpf());
        edtDtNasc      .setText(getU.getDtNasc());
        edtCidNasc     .setText(getU.getCidNasc());
      //edtCep         .setText(geteU.getCep);
        edtEndereco    .setText(getU.getEndereco());
        edtBairro      .setText(getU.getBairro());
        edtCidade      .setText(getU.getCidade());
        edtEstado      .setText(getU.getEstado());
        edtPais        .setText(getU.getPais());
        edtTelefone    .setText(getU.getTelefone());
        edtEmailContato.setText(getU.getEmail());
        if( getU.getSexo().toString().equals("Masculino") ){
            rbMasc.setChecked(true);
            rbMasc.setChecked(false);
        }else{
            rbMasc.setChecked(false);
            rbMasc.setChecked(true);
        }
        String[] itemsRacaCor= getResources().getStringArray(R.array.spinner_racacor);
        ArrayAdapter<String> arrayAdapterRacaCor = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, itemsRacaCor);
        spnRacaCor.setPrompt("PROMPT");

        String[] itemsEstNasc= getResources().getStringArray(R.array.spinner_racacor);
        ArrayAdapter<String> arrayAdapterEstNasc = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, itemsRacaCor);
        spnEstNasc.setPrompt( getU.getEstNasc() );
    }

    private void clearComponent(){
        edtEmailParaLogin.setText("");
        edtSenhaParaLogin.setText("");
        edtNomeCompleto.setText("");
        edtCpf.setText("");
        edtDtNasc.setText("");
        edtCidNasc.setText("");
        edtEndereco.setText("");
        edtBairro.setText("");
        edtCidade.setText("");
        edtEstado.setText("");
        edtPais.setText("");
        edtTelefone.setText("");
        edtEmailContato.setText("");
    }

    private boolean checkEmailSenha(String email, String senha){
        if( !( Patterns.EMAIL_ADDRESS.matcher( email ).matches() ) ){
            edtEmailParaLogin.requestFocus();
            Toast.makeText(this, "E-Mail para Login Inválido.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(senha.length() < 8){
            edtSenhaParaLogin.requestFocus();
            Toast.makeText(this, "A Senha deve ter no mínimo 8 caracteres", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private boolean checkComponent(int stage){
        String checkEmail = edtEmailParaLogin.getText().toString();
        String checkSenha = edtSenhaParaLogin.getText().toString();
        String checkCpf = "[0-9][0-9][0-9].[0-9][0-9][0-9].[0-9][0-9][0-9]-[0-9][0-9]";
        String checkDtNasc = "[0-9][0-9]/[0-9][0-9]/[0-9][0-9][0-9][0-9]";

        if( checkEmailSenha(checkEmail, checkSenha) || stage==1){
            if ( edtNomeCompleto.getText().toString().equals("")  ){
                edtNomeCompleto.requestFocus();
                Toast.makeText(this, "Digite o Nome Completo.", Toast.LENGTH_SHORT).show();
                return false;
            }
            if( edtCpf.getText().toString().equals("") || !( edtCpf.getText().toString().matches(checkCpf) ) ){
                edtCpf.requestFocus();
                Toast.makeText(this, "CPF Inválido.", Toast.LENGTH_SHORT).show();
                return false;
            }
            if( edtDtNasc.getText().toString().equals("") || !( edtDtNasc.getText().toString().matches(checkDtNasc) ) ){
                edtDtNasc.requestFocus();
                Toast.makeText(this, "Data de Nascimento Inválida.", Toast.LENGTH_SHORT).show();
                return false;
            }
            if(edtCidNasc.getText().toString().equals("") ){
                edtCidNasc.setText("Nao Informado");
            }
            if(!( rbMasc.isChecked() ) && !( rbFem.isChecked() ) ){
                Toast.makeText(this, "Selecione seu Gênero", Toast.LENGTH_SHORT).show();
                return false;
            }
            if(edtEndereco.getText().toString().equals("") ){
                //edtEndereco.setText("Nao Informado");
            }
            if(edtBairro.getText().toString().equals("") ){
                //edtBairro.setText("Nao Informado");
            }
            if(edtCidade.getText().toString().equals("") ){
                //edtCidade.setText("Nao Informado");
            }
            if(edtEstado.getText().toString().equals("") ){
                //edtEstado.setText("Nao Informado");
            }
            if(edtPais.getText().toString().equals("") ){
                //edtPais.setText("Nao Informado");
            }
            if(edtTelefone.getText().toString().equals("") ){
                //edtTelefone.setText("Nao Informado");
            }
            if(edtEmailContato.getText().toString().equals("") ){
                //edtEmail.setText("Nao Informado");
            }
        }else{
            return false;
        }
        return true;
    }
}
