package br.com.casadocodigo.boaviagem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class ViagemListActivity extends ListActivity implements OnItemClickListener,
														OnClickListener, ViewBinder {
	
	private List<Map<String, Object>> viagens;
	private AlertDialog alertDialog;
	private AlertDialog dialogConfirmacao;
	private int viagemSelecionada;
	private DatabaseHelper helper;
	private SimpleDateFormat dateFormat;
	private Double valorLimite;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		helper = new DatabaseHelper(this);
		dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		
		String[] de = {"imagem","destino","data","total", "barraProgresso"};
		
		int[] para = {R.id.tipoViagem, R.id.destino, R.id.data, R.id.valor, R.id.barraProgresso};
		
		SimpleAdapter adapter = new SimpleAdapter(this, listarViagens(), R.layout.lista_viagem, de, para); 
		
		adapter.setViewBinder(this);
		
		setListAdapter(adapter);
		
		SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(this);
		String valor = preferencias.getString("valor_limite", "-1");
		valorLimite = Double.valueOf(valor);
		
		getListView().setOnItemClickListener(this);
		this.alertDialog =  criarAlertDialog();
		this.dialogConfirmacao = criaDialogConfirmacao();
	}
	
	private List<Map<String, Object>> listarViagens(){
		
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT _id, tipo_viagem, destino, " + 
				"data_chegada, data_saida, orcamento FROM viagem", null);
		
		cursor.moveToFirst();
		
		
		viagens = new ArrayList<Map<String, Object>>();
		
		for(int i = 0; i < cursor.getCount(); i++){
			Map<String, Object> item = new HashMap<String, Object>();
			
			String id = cursor.getString(0);
			int tipoViagem   = cursor.getInt(1);
			String destino   = cursor.getString(2);
			long dataChegada = cursor.getLong(3);
			long dataSaida   = cursor.getLong(4);
			double orcamento =  100;// cursor.getDouble(5);
			
			item.put("id", id);
			
			if(tipoViagem == Constantes.VIAGEM_LAZER){
				item.put("imagem", R.drawable.lazer);
			}else{
				item.put("imagem", R.drawable.negocios);
			}
			
			item.put("destino", destino);
			
			Date dataChegadaDate = new Date(dataChegada);
			Date dataSaidaDate   = new Date(dataSaida);
			
			String periodo = dateFormat.format(dataChegadaDate) + "a" 
					+ dateFormat.format(dataSaidaDate);
			item.put("data", periodo);
			
			double totalGasto = calcularTotalGasto(db, id);
			
			item.put("total", "Gasto total R$ " + totalGasto);
			valorLimite = (double) 200;
			double alerta = orcamento * valorLimite / 100;
			Double [] valores = new Double[]{orcamento, alerta, totalGasto};
			
			viagens.add(item);
			
			cursor.moveToNext();
		}
		
		cursor.close();
		return viagens;
		
	}
	
	private double calcularTotalGasto(SQLiteDatabase db, String id){
		Cursor cursor = db.rawQuery("SELECT SUM(valor) FROM gasto WHERE viagem_id = ?", new String[]{id});
		cursor.moveToFirst();
		double total =  cursor.getDouble(0);
		cursor.close();
		return total;
	}
	
	public void onItemClick(AdapterView<?> parente, View view, int position, long id){
		
		this.viagemSelecionada  = position;
		alertDialog.show();
		
		Map<String, Object> map = viagens.get(position);
		String destino = (String) map.get("destino");
		String mensagem = "Viagem selecionada" + destino;
		Toast.makeText(getApplicationContext(), mensagem, Toast.LENGTH_SHORT).show();
		startActivity(new Intent(this, GastoListActivity.class));
	}
	
	@Override
	public void onClick(DialogInterface dialog, int item){
		switch (item) {
		case 0:
			startActivity(new Intent(this, ViagemActivity.class));
			break;
		
		case 1:
			startActivity(new Intent(this, GastoActivity.class));
			break;
		
		case 2:
			startActivity(new Intent(this, GastoListActivity.class));
			break;
		
		case 3:
			dialogConfirmacao.show();
			break;
		
		case DialogInterface.BUTTON_POSITIVE:
			viagens.remove(viagemSelecionada);
			getListView().invalidateViews();
			break;
		
		case DialogInterface.BUTTON_NEGATIVE:
			dialogConfirmacao.dismiss();
			break;
			
		default:
			break;
		}
	}
	
	private AlertDialog criarAlertDialog(){
		final CharSequence[] items = {
				getString(R.string.editar),
				getString(R.string.novo_gasto),
				getString(R.string.gastos_realizados),
				getString(R.string.remover)};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.opcoes);
		builder.setItems(items, this);		
		
		return builder.create();
	}
	
	private AlertDialog criaDialogConfirmacao(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.confirmacao_exclusao_viagem);
		builder.setPositiveButton(getString(R.string.sim), this);
		builder.setNegativeButton(R.string.nao, this);
		
		return builder.create();
	}
	
	@Override
	public boolean setViewValue(View view, Object data,	String textRepresentation) {
		if (view.getId() == R.id.barraProgresso) {
			Double valores[] = (Double[]) data;
			valores[0] = (double) 1;
			ProgressBar progressBar = (ProgressBar) view;
			progressBar.setMax(valores[0].intValue());
			progressBar.setSecondaryProgress(valores[1].intValue());
			progressBar.setProgress(valores[2].intValue());
			return true;
		}
		return false;
	}

}
