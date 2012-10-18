package br.com.casadocodigo.boaviagem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
//import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

public class GastoListActivity extends ListActivity implements OnItemClickListener{
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		String[] de = {"data", "descricao", "valor", "categoria"};
		int[] para = {R.id.data, R.id.descricao, R.id.valor, R.id.categoria};
		
		SimpleAdapter adapter = new SimpleAdapter(this, listarGastos(), R.layout.lista_gasto, de, para);
		
		adapter.setViewBinder(new GastoViewBinder());
		
		setListAdapter(adapter);
		getListView().setOnItemClickListener(this);
		
		//setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listarGastos()));
		ListView listView = getListView();
		listView.setOnItemClickListener(this);
		
		registerForContextMenu(getListView());
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.gasto_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item){
		if(item.getItemId() == R.id.remover){
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			gastos.remove(info.position);
			return true;
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
		Map<String, Object> map = gastos.get(position);
		String descricao = (String) map.get("descricao");
		String mensagem = "Gasto selecionada: " + descricao;
		Toast.makeText(this, mensagem,Toast.LENGTH_SHORT).show();
	}
	
	private List<Map<String, Object>> gastos;
	
	private List<Map<String, Object>> listarGastos(){
		gastos = new ArrayList<Map<String, Object>>();
		
		Map<String, Object> item = new HashMap<String, Object>();
		item.put("data", "04/02/2012");
		item.put("descricao", "Diária Hotel");
		item.put("valor", "R$ 260,00");
		item.put("categoria", R.color.categoria_hospedagem);
		gastos.add(item);
		// pode adicionar mais informações de viagens
		return gastos;

	}
	
	private String dataAnterior = "";
	
	private class GastoViewBinder implements ViewBinder {
		
		@Override
		public boolean setViewValue(View view, Object data, String TextRepresentation){
			if(view.getId() == R.id.data){
				if(!dataAnterior.equals(data)){
					TextView textView = (TextView) view;
					textView.setText(TextRepresentation);
					dataAnterior = TextRepresentation;
					view.setVisibility(View.VISIBLE);
				}else{
					view.setVisibility(View.GONE);
				}
				return true;
			}
			
			if(view.getId() == R.id.categoria){
				Integer id = (Integer) data;
				view.setBackgroundColor(getResources().getColor(id));
				return true;
			}
			return false;
		}
	}
}
