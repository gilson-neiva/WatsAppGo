package watsappgo.curso.com.watsappgo.helper;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gilson on 22/08/2017.
 */

public class Permissao {

    // quando um metodo é staticon não é preciso instanciar a classe para chama-lo...
    public static boolean validarPermissoes(int requestCode, Activity activity, String[] permissoes) {

        //verificando se a versão é superior a API 23...
        if (Build.VERSION.SDK_INT >= 23){

            List<String> listaPermissoes  = new ArrayList<String>();

            /*Percorre as permissões necessarias passadas verificando uma a uma se já tem a
            permissão liberada*/
            for (String permissao : permissoes){

                Boolean validaPermissao = ContextCompat.checkSelfPermission(activity,permissao) == PackageManager.PERMISSION_GRANTED;

                if ( !validaPermissao ){
                    listaPermissoes.add(permissao);
                }

            //caso a lista esteja vazia , não é necessario solicitar a permissao
                if (listaPermissoes.isEmpty()){
                    return true;
                }

            // convertendo uma objeto do tipo list, para String
                String[] novasPermissoes = new String[listaPermissoes.size()];
                listaPermissoes.toArray(novasPermissoes); //novas permissões agora é um array de Strings

             //solicita a permissao
                ActivityCompat.requestPermissions(activity,novasPermissoes,requestCode);

            }

        }

        return true;
    }

}
