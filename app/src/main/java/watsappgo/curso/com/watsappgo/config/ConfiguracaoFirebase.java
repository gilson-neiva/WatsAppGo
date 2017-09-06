package watsappgo.curso.com.watsappgo.config;

/**
 * Created by gilson on 23/08/2017.
 */

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;

//Quando é utilizado a palavra FINAL na classe , ela não pode ser extendida.

public final class ConfiguracaoFirebase {

    private static DatabaseReference referenciaFirebase;
    private static FirebaseAuth autenticacao;

    //criando metodo para retornar a referenciar do firebase.
    // quando um metodo é declarado como static não é preciso criar uma instancia da classe para utiliza-lo.
    public static DatabaseReference getFirebase(){

        if (referenciaFirebase == null ){
            referenciaFirebase = FirebaseDatabase.getInstance().getReference();
        }

        return  referenciaFirebase;
    }

    public static FirebaseAuth getFirebaseAutenticacao(){
        if( autenticacao == null ){
            autenticacao = FirebaseAuth.getInstance();
        }
        return autenticacao;
    }

}
