package com.phiworks.domodocasual;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.phiworks.dapartida.ActivityPartidaMultiplayer;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

public class CriarSalaDoModoCasualTask extends AsyncTask<DadosDaSalaModoCasual, String, String>
{
	private ActivityPartidaMultiplayer activityDoMultiplayer;
	private ProgressDialog popupDeProgresso;
	private String result = "";
	private InputStream inputStream = null;
	private int idSalaModoCasual;

	public CriarSalaDoModoCasualTask(ProgressDialog loadingDaTela, ActivityPartidaMultiplayer telaDoMultiplayer)
	{
		this.activityDoMultiplayer = telaDoMultiplayer;
		this.popupDeProgresso = loadingDaTela;
	}

	@Override
	protected String doInBackground(DadosDaSalaModoCasual... params) {
		DadosDaSalaModoCasual dadosDaSala = params[0];
		String emailQuemCriouSala = dadosDaSala.getUsernameQuemCriouSala();
		String categoriasSeparadasPorVirgula = dadosDaSala.getCategoriasSelecionadasEmString();
		String tituloDoJogador = dadosDaSala.getTituloDoJogador();
		
		String url_select = "http://server.sumosensei.pairg.dimap.ufrn.br/app/inserir_nova_sala.php";//android nao aceita localhost, tem de ser seu IP
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
		try
		{
			MongoClient mongo = new MongoClient("10.5.29.51", 27017);
			DB db = mongo.getDB("pairg_sumosensei_app");
			DBCollection collection = db.getCollection("salasmodocasual");
			BasicDBObject document = new BasicDBObject();
            document.append("emaildocriador", emailQuemCriouSala);
            document.append("dandocriador", tituloDoJogador);
            document.append("salaaberta", "sim");
            String[] categorias = categoriasSeparadasPorVirgula.split(",");
            List<String> categoriasList = new ArrayList<String>();
            for(int i = 0; i < categorias.length; i++)
            {
            	categoriasList.add(categorias[i]);
            }
            document.append("categorias", categoriasList);
			collection.insert(document);
            
			//agora vamos pegar o id da sala que acabamos de criar
			DBObject searchByEmailCriador = new BasicDBObject("emaildocriador", emailQuemCriouSala);
			DBCursor cursor = collection.find(searchByEmailCriador);
			while(cursor.hasNext())
			{
				DBObject objetoDB = cursor.next();
				String salaaberta = (String) objetoDB.get("salaaberta");
				
				if(salaaberta.compareTo("sim") == 0)
				{
					//encontramos a sala aberta criada agora pouco
					ObjectId idSalaMongo = (ObjectId) objetoDB.get( "_id" );
					this.idSalaModoCasual = idSalaMongo._inc();
					if(this.idSalaModoCasual < 0)
					{
						this.idSalaModoCasual = -this.idSalaModoCasual;
					}
					break;
				}
			}
            
           
            
           
            
		}
		catch (IllegalStateException e3) {
            Log.e("IllegalStateException", e3.toString());
            e3.printStackTrace();
        } catch (IOException e4) {
            Log.e("IOException", e4.toString());
            e4.printStackTrace();
        }
		return "";
	}
	
	@Override
	protected void onPostExecute(String v) {
		this.popupDeProgresso.dismiss();
		this.activityDoMultiplayer.setarIdDaSala(idSalaModoCasual);
		this.activityDoMultiplayer.criarSalaQuickMatch(idSalaModoCasual);
		
		
	}
	
}
