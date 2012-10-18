package br.com.casadocodigo.boaviagem;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

public class ViagemActivity extends Activity{
	
	private DatabaseHelper helper;
	private EditText destino, quantidadePessoas, orcamento;
	private RadioGroup radioGroup;
	private Date dataChegada, dataSaida;
	private int ano, mes, dia;
	private Button dataChegadaButton, dataSaidaButton; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viagem);
		
		Calendar calendar = Calendar.getInstance();
		ano = calendar.get(Calendar.YEAR);
		mes = calendar.get(Calendar.MONTH);
		dia = calendar.get(Calendar.DAY_OF_MONTH);
		
		dataChegadaButton = (Button) findViewById(R.id.dataChegada);
		dataSaidaButton   = (Button) findViewById(R.id.dataSaida);
		
		destino = (EditText) findViewById(R.id.destino);
		quantidadePessoas = (EditText) findViewById(R.id.quantidade_pessoas);
		orcamento = (EditText) findViewById(R.id.orcamento);
		radioGroup = (RadioGroup) findViewById(R.id.tipoViagem);
		
		helper = new DatabaseHelper(this);
		
	}
	
	@Override
	public void onDestroy(){
		helper.close();
		super.onDestroy();
	}
	
	 public void selecionarData(View view){
	    	showDialog(view.getId());
	    }
	    
	    @Override
	    protected Dialog onCreateDialog(int id){
	    	switch (id) {
			case R.id.dataChegada:
				return new DatePickerDialog(this, dataChegadaListener, ano, mes, dia);

			case R.id.dataSaida:
				return new DatePickerDialog(this, dataSaidaListener, ano, mes, dia);
			}
	    	return null;
	    }
	    
	    private OnDateSetListener dataChegadaListener = new OnDateSetListener() {
			public void onDateSet(DatePicker view, int anoSelecionado, int mesSelecionado, int diaSelecionado) {
				dataChegada = criarData(anoSelecionado, mesSelecionado, diaSelecionado);
				dataChegadaButton.setText(dia + "/" + (mes + 1 ) + "/" + ano );
			}
		};
		
		private OnDateSetListener dataSaidaListener = new OnDateSetListener() {
			public void onDateSet(DatePicker view, int anoSelecionado, int mesSelecionado, int diaSelecionado) {
				dataSaida = criarData(anoSelecionado, mesSelecionado, diaSelecionado);
				dataSaidaButton.setText(dia + "/" + (mes + 1 ) + "/" + ano );
			}
		};
		
		private Date criarData(int anoSelecionado, int mesSelecionado, int diaSelecionado){
			Calendar calendar = Calendar.getInstance();
			calendar.set(anoSelecionado, mesSelecionado, diaSelecionado);
			return calendar.getTime();
		}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.viagem_menu, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item){
		switch (item.getItemId()) {
		case R.id.novo_gasto:
			startActivity(new Intent(this, GastoActivity.class));
			return true;
		
		case R.id.remover:
			//remover viagem do banco de dados
			return true;

		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}
	
	public void salvarViagem(View view){
		SQLiteDatabase db = helper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put("destino", destino.getText().toString());
		values.put("data_chegada", dataChegada.getTime());
		values.put("data_saida", dataSaida.getTime());
		values.put("orcamento", orcamento.getText().toString());
		values.put("quantidade_pessoas", quantidadePessoas.getText().toString());
		
		int tipo = radioGroup.getCheckedRadioButtonId();
		
		if(tipo == R.id.lazer){
			values.put("tipo_viagem", Constantes.VIAGEM_LAZER);
		}else{
			values.put("tipo_viagem", Constantes.VIAGEM_NEGOCIOS);
		}
		
		long resultado = db.insert("viagem", null, values);
		
		if(resultado != -1){
			Toast.makeText(this, getString(R.string.registro_salvo), Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(this, getString(R.string.erro_salvar), Toast.LENGTH_SHORT).show();
		}
		
	}

}
