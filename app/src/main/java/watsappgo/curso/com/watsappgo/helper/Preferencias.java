package watsappgo.curso.com.watsappgo.helper;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by gilson on 22/08/2017.
 */

public class Preferencias {

    private Context contexto;
    private SharedPreferences preferences;
    private static final String NOME_ARQUIVO = "watsapp.preferencias";
    private static final int MODE = 0;

    private static final String CHAVE_IDENTIFICADOR = "identificadorUsuarioLogado";
    private static final String CHAVE_NOME = "nomeUsuarioLogado";

    private SharedPreferences.Editor editor;


    //criando o metodo contrutor , com contexto para salvar as informações
    public Preferencias(Context contextoParametro){

        contexto = contextoParametro;
        preferences = contexto.getSharedPreferences(NOME_ARQUIVO,MODE);
        //criando o editor que é uma interface para fazer as edições no arquivo
        editor = preferences.edit();

    }

    //criando metodo que faz a gravação
    public void salvarDados (String identificadorUsuario,String nomeUsuario){
        editor.putString(CHAVE_IDENTIFICADOR,identificadorUsuario);
        editor.putString(CHAVE_NOME,nomeUsuario);
        editor.commit();
    }

    //recuperando o identificador
    public String getIdentificador(){
        return preferences.getString(CHAVE_IDENTIFICADOR,null);
    }


    //recuperando o nome
    public String getNome(){
        return preferences.getString(CHAVE_NOME,null);
    }

}
