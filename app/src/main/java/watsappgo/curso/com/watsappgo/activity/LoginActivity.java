package watsappgo.curso.com.watsappgo.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Random;

import javax.security.auth.login.LoginException;

import watsappgo.curso.com.watsappgo.R;
import watsappgo.curso.com.watsappgo.config.ConfiguracaoFirebase;
import watsappgo.curso.com.watsappgo.helper.Base64Custom;
import watsappgo.curso.com.watsappgo.helper.Permissao;
import watsappgo.curso.com.watsappgo.helper.Preferencias;
import watsappgo.curso.com.watsappgo.model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText senha;
    private Button botaoLogar;

    private Usuario usuario;

    private String identificadorUsuarioLogado;

    private FirebaseAuth autenticacao;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        verificarUsuarioLogado();

        email = (EditText) findViewById(R.id.edit_login_emailId);
        senha = (EditText) findViewById(R.id.edit_login_senhaId);
        botaoLogar = (Button) findViewById(R.id.botaoLogarId);


        botaoLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                usuario = new Usuario();
                usuario.setEmail(email.getText().toString() );
                usuario.setSenha(senha.getText().toString() );
                validarLogin();


            }
        });

    }

    //metodo que faz a validação do Login.
    private void validarLogin(){

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()

        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){



                    identificadorUsuarioLogado = Base64Custom.codificarBase64(usuario.getEmail());

                    //recuperando instancia do firebase para buscar o nome do usuario.
                    firebase = ConfiguracaoFirebase.getFirebase()
                            .child("usuarios")
                            .child(identificadorUsuarioLogado);

                    //realizando uma consulta no firebase.
                    valueEventListenerUsuario = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Usuario usuarioRecuperado = dataSnapshot.getValue(Usuario.class);

                            //salvando os dados  nas preferencias.
                            Preferencias preferencias = new Preferencias(LoginActivity.this);
                            preferencias.salvarDados(identificadorUsuarioLogado,usuarioRecuperado.getNome());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };

                    firebase.addListenerForSingleValueEvent(valueEventListenerUsuario);


                    Toast.makeText(LoginActivity.this,"Sucesso ao realizar Login",Toast.LENGTH_SHORT).show();
                    abrirTelaPrincipal();
                }else {
                    Toast.makeText(LoginActivity.this,"Erro ao realizar Login",Toast.LENGTH_SHORT).show();
                }
            }

            
        });

    }

    //metodo que cria a Intent da tela principal após o login.
    private  void abrirTelaPrincipal(){

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity( intent );
        finish();

    }

    //metodo que cria a Intent do tela de cadastro no texto...
    public void abrirCadastroUsuario(View view){

        Intent intent = new Intent(LoginActivity.this, CadastroUsuarioActivity.class);
        startActivity( intent );

    }

    //metodo que faz a vefificação se o usuario já esta logado , quando ele entra no App.
    private void verificarUsuarioLogado(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        if(autenticacao.getCurrentUser() != null){
            abrirTelaPrincipal();
        }
    }


}