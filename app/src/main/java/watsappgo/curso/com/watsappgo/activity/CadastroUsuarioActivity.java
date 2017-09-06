package watsappgo.curso.com.watsappgo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import watsappgo.curso.com.watsappgo.R;
import watsappgo.curso.com.watsappgo.helper.Base64Custom;
import watsappgo.curso.com.watsappgo.helper.Preferencias;
import watsappgo.curso.com.watsappgo.model.Usuario;

import watsappgo.curso.com.watsappgo.config.ConfiguracaoFirebase;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class CadastroUsuarioActivity extends AppCompatActivity {

    private EditText nome;
    private EditText email;
    private EditText senha;
    private Button botaoCadastrar;
    private Usuario usuario;

    private FirebaseAuth autenticacao;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        nome = (EditText) findViewById(R.id.edit_cadastro_nomeId);
        email = (EditText) findViewById(R.id.edit_cadastro_emailId);
        senha = (EditText) findViewById(R.id.edit_cadastro_senhaId);
        botaoCadastrar = (Button) findViewById(R.id.botaoCadastrarId);

        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usuario = new Usuario();
                usuario.setNome( nome.getText().toString() );
                usuario.setEmail(email.getText().toString());
                usuario.setSenha(senha.getText().toString());
                cadastrarUsuario();
            }
        });

    }

    private void cadastrarUsuario(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(CadastroUsuarioActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if( task.isSuccessful() ){

                    Toast.makeText(CadastroUsuarioActivity.this, "Sucesso ao cadastrar usuário", Toast.LENGTH_LONG ).show();

                    FirebaseUser usuarioFirebase = task.getResult().getUser();

                    String identificadorUsuario = Base64Custom.codificarBase64(usuario.getEmail());

                    usuario.setId(identificadorUsuario);

                    //chamando o metodo para salvar o usuario no FireBase.
                    usuario.salvar();

                    //salvando os dados do e-mail nas preferencias.
                    Preferencias preferencias = new Preferencias(CadastroUsuarioActivity.this);
                    preferencias.salvarDados(identificadorUsuario,usuario.getNome());

                    abrirLoginUsuario();

                }else {

                    String erroExcecao = "";

                    try {
                        throw task.getException();

                    }catch (FirebaseAuthWeakPasswordException e){
                        erroExcecao = " Digite uma senha mais forte, com mais letras e números !";
                    }
                    catch (FirebaseAuthInvalidCredentialsException e){
                        erroExcecao = " E-mail com formato invalido! Verifique...";
                    }
                    catch (FirebaseAuthUserCollisionException e){
                        erroExcecao = " Já existe um cadastro no App com este e-mail ! ";
                    }
                    catch (Exception e){
                        erroExcecao = " Erro ao efetuar cadastro, entre em contato com o Suporte ! ";
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroUsuarioActivity.this, "Erro : "+erroExcecao, Toast.LENGTH_LONG ).show();
                }
            }
        });
    }

    public void abrirLoginUsuario(){
        Intent intent = new Intent(CadastroUsuarioActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }


}
