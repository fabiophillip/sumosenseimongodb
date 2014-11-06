package bancodedados;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.jar.JarOutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.phiworks.sumosenseinew.ActivityMultiplayerQueEsperaAtePegarOsKanjis;
import com.phiworks.sumosenseinew.ActivityQueEsperaAtePegarOsKanjis;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

/**
 * Essa classe vai solicitar a um servidor php por listas de kanjis para treino no jogo
 * 
 *
 */
public class SolicitaKanjisParaTreinoTask extends AsyncTask<String, String, Void> {
	
	private InputStream inputStream = null;
	private String result = ""; 
	private ProgressDialog loadingDaTelaEmEspera;//eh o dialog da tela em espera pelo resultado do web service
	private ActivityQueEsperaAtePegarOsKanjis activityQueEsperaAtePegarOsKanjis;
	private DBCollection colecaoJlpt;
	
	public SolicitaKanjisParaTreinoTask(ProgressDialog loadingDaTela, ActivityQueEsperaAtePegarOsKanjis activityQueEsperaAteRequestTerminar)
	{
		this.loadingDaTelaEmEspera = loadingDaTela;
		this.activityQueEsperaAtePegarOsKanjis = activityQueEsperaAteRequestTerminar;
	}
	
	public void conectarMongo()
	{
		// To directly connect to a single MongoDB server (note that this will not auto-discover the primary even
		// if it's a member of a replica set:
		// or
		MongoClient mongoClient;
		try {
			mongoClient = new MongoClient( "10.5.20.164" , 27017 );//IP MUDA
			DB db = mongoClient.getDB( "pairg_sumosensei_app" );
			this.colecaoJlpt = db.getCollection("jlpt");
			 DBCursor cursorPercorreDocsJlpt = colecaoJlpt.find();
				Iterator<DBObject> iteraSobreColecao = cursorPercorreDocsJlpt.iterator();
				while(iteraSobreColecao.hasNext())
				{
					DBObject umObjetoDaColecaoJlpt = iteraSobreColecao.next();
					String hiraganaDoKanji = (String) umObjetoDaColecaoJlpt.get("hiragana");
					Integer jlptEmInteiro = (Integer) umObjetoDaColecaoJlpt.get("jlpt");
					String jlptAssociado = String.valueOf(jlptEmInteiro);
				    String categoriaAssociada = (String) umObjetoDaColecaoJlpt.get("categoria");
				    String kanji = (String) umObjetoDaColecaoJlpt.get("kanji");
				    String traducaoEmPortugues = (String) umObjetoDaColecaoJlpt.get("traducao");
				    Integer dificuldadeEmInt = (Integer)umObjetoDaColecaoJlpt.get("dificuldade");
					String dificuldadeDoKanji = String.valueOf(dificuldadeEmInt);
					int dificuldadeDoKanjiEmNumero; 
				    try
				    {
				    	dificuldadeDoKanjiEmNumero = Integer.valueOf(dificuldadeDoKanji);
				    }
				    catch(NumberFormatException exc)
				    {
				    	dificuldadeDoKanjiEmNumero = 1;
				    }
				    
				    KanjiTreinar novoKanjiTreinar = new KanjiTreinar(jlptAssociado, categoriaAssociada, kanji, 
				    		traducaoEmPortugues, hiraganaDoKanji, dificuldadeDoKanjiEmNumero);
				    //vamos só ver se o kanji tem uma lista de possiveis ciladas...
					
				    	if(umObjetoDaColecaoJlpt.containsField("possiveis ciladas") == true)
				    	{
				    		String possiveisCiladas = (String) umObjetoDaColecaoJlpt.get("possiveis ciladas");
				    		String [] ciladasEmArray = possiveisCiladas.split(";");
				    		LinkedList<String> possiveisCiladasLinkedList = new LinkedList<String>();
				    		for(int j = 0; j < ciladasEmArray.length; j++)
				    		{
				    			possiveisCiladasLinkedList.add(ciladasEmArray[j]);
				    		}
				    		novoKanjiTreinar.setPossiveisCiladasKanji(possiveisCiladasLinkedList);
				    	}
				    	
				    
				    
				    //e, por fim, armazenar esses kanjis na lista de ArmazenaKanjisPorCategoria
				    ArmazenaKanjisPorCategoria.pegarInstancia().adicionarKanjiACategoria(categoriaAssociada, novoKanjiTreinar);
				}
			
			
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}

		
	}
	
	@Override
    protected Void doInBackground(String... params) {
		
		this.conectarMongo();
		
		/*//antigo:"http://server.sumosensei.pairg.dimap.ufrn.br/app/pegarjlptjson.php";
       String url_select = "http://server.sumosensei.pairg.dimap.ufrn.br/app/pegarjlptjson.php";//android nao aceita localhost, tem de ser seu IP
       
       
       ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

        try {
            // Set up HTTP post

            // HttpClient is more then less deprecated. Need to change to URLConnection
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost(url_select);
            httpPost.setEntity(new UrlEncodedFormEntity(param));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();

            // Read content & Log
            inputStream = httpEntity.getContent();
        } catch (UnsupportedEncodingException e1) {
            Log.e("UnsupportedEncodingException", e1.toString());
            e1.printStackTrace();
        } catch (ClientProtocolException e2) {
            Log.e("ClientProtocolException", e2.toString());
            e2.printStackTrace();
        } catch (IllegalStateException e3) {
            Log.e("IllegalStateException", e3.toString());
            e3.printStackTrace();
        } catch (IOException e4) {
            Log.e("IOException", e4.toString());
            e4.printStackTrace();
        }
        // Convert response to string using String Builder
        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder sBuilder = new StringBuilder();

            String line = null;
            while ((line = bReader.readLine()) != null) {
            	if(line.startsWith("<meta") == false)//pula linha de metadados
            	{
            		 sBuilder.append(line + "\n");
            	}
               
            }

            inputStream.close();
            result = sBuilder.toString();

        } catch (Exception e) {
            Log.e("StringBuilding & BufferedReader", "Error converting result " + e.toString());
        }*/
        
        if(this.activityQueEsperaAtePegarOsKanjis instanceof ActivityMultiplayerQueEsperaAtePegarOsKanjis)
        {
        	//se trata da activity do multiplayer
        	ActivityMultiplayerQueEsperaAtePegarOsKanjis activityMultiplayer = (ActivityMultiplayerQueEsperaAtePegarOsKanjis) this.activityQueEsperaAtePegarOsKanjis;
        	if(activityMultiplayer.jogadorEhHost() == true)
        	{
        		 while(activityMultiplayer.oGuestTerminouDeCarregarListaDeCategorias() == false)
        	        {
        	        	//espera...
        	        	try {
        					Thread.sleep(1000);
        				} catch (InterruptedException e) {
        					// TODO Auto-generated catch block
        					e.printStackTrace();
        				}
        	        }
        	}
        }
       
        
		return null;
    } // protected Void doInBackground(String... params)
	
	 protected void onPostExecute(Void v) {
	       
	        ArmazenaKanjisPorCategoria armazenadorKanjis = ArmazenaKanjisPorCategoria.pegarInstancia();
	        this.activityQueEsperaAtePegarOsKanjis.mostrarListaComKanjisAposCarregar();
	        this.loadingDaTelaEmEspera.dismiss();
	    } // protected void onPostExecute(Void v)
} //class MyAsyncTask extends AsyncTask<String, String, Void>

