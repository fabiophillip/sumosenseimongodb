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

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;


import android.os.AsyncTask;
import android.util.Log;

public class DesativarSalaEscolhidaDoBdTask extends AsyncTask<String, String, Void> 
{
	private InputStream inputStream = null;
	private String result = ""; 

	
	
	@Override
	protected Void doInBackground(String... arg0) 
	{
		String url_select = "http://server.sumosensei.pairg.dimap.ufrn.br/app/desativarsalaporid.php";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		String emailDoCriador = arg0[0];
		
		try
		{
			MongoClient mongo = new MongoClient("10.5.29.51", 27017);
			DB db = mongo.getDB("pairg_sumosensei_app");
			DBCollection collection = db.getCollection("salasmodocasual");
			BasicDBObject searchByEmailCriador = new BasicDBObject("emaildocriador", emailDoCriador );
			DBCursor cursor = collection.find(searchByEmailCriador);
			
			while(cursor.hasNext())
			{
				BasicDBObject objetoDB = (BasicDBObject) cursor.next();
				String salaaberta = (String) objetoDB.get("salaaberta");
				String emailCriador = (String) objetoDB.get("emaildocriador");
				String danCriador = (String) objetoDB.get("dandocriador");
				List<String> categoriasList = (List<String>) objetoDB.get("categorias");
				
				if(salaaberta.compareTo("sim") == 0)
				{
					//encontramos a sala aberta criada agora pouco
					BasicDBObject novoObjetoDB = new BasicDBObject();
					novoObjetoDB.put("salaaberta","não");
					novoObjetoDB.append("emaildocriador", emailCriador);
					novoObjetoDB.append("dandocriador", danCriador);
					novoObjetoDB.append("categorias", categoriasList);
					collection.update(objetoDB,novoObjetoDB);
					
				}
			}
		}
		catch (IOException e4) {
            Log.e("IOException", e4.toString());
            e4.printStackTrace();
        }
		
		return null;
	}
	
	 protected void onPostExecute(Void v) {
		 
	 }
	

}
