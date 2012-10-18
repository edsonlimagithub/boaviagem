package br.com.casadocodigo.boaviagem;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.DashPathEffect;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class BoaViagemActivity extends Activity {
	private static final String MANTER_CONECTADO = "manter_conectado";
	private EditText usuario;
	private EditText senha;
	private CheckBox manterConectado;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      setContentView(R.layout.login);
        
       usuario = (EditText) findViewById(R.id.usuario);
       senha = (EditText) findViewById(R.id.senha);
       manterConectado = (CheckBox) findViewById(R.id.manterConectado);
       
       SharedPreferences preferencias = getPreferences(MODE_PRIVATE);
       
       boolean conectado = preferencias.getBoolean(MANTER_CONECTADO, false);
       
       if(conectado){
    	   startActivity(new Intent(this, DashboardActivity.class));
       }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_boa_viagem, menu);
        return true;
    }
    
    public void entrarOnClick(View v){
    	String usuarioInformado = usuario.getText().toString();
    	String senhaInformada   = senha.getText().toString();
    	
    	if("l".equals(usuarioInformado) && "1".equals(senhaInformada)){
    		SharedPreferences preferencias = getPreferences(MODE_PRIVATE);
    		Editor editor = preferencias.edit();
    		editor.putBoolean(MANTER_CONECTADO, manterConectado.isChecked());
    		editor.commit();
    		
    		//vai para activity
    		startActivity(new Intent(this, DashboardActivity.class) );
    	}else{
    		String mensagemErro = getString(R.string.erro_autenticacao);
    		Toast toast = Toast.makeText(this, mensagemErro, Toast.LENGTH_SHORT);
    		toast.show();
    		//mostra uma mensagem de erro
    	}
    }
}
