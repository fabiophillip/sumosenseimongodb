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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.phiworks.sumosenseinew.DadosPartidasAnteriores;



import android.os.AsyncTask;
import android.util.Log;

public class PegarDadosUltimasPartidasTask extends AsyncTask<String, String, Void>  
{
	private DadosPartidasAnteriores telaDadosPartidasAnteriores;
	private DBCollection colecaoPartidas;
	private LinkedList<DadosPartidaParaOLog> dadosPartidasParaLog;
	
	
	public PegarDadosUltimasPartidasTask(DadosPartidasAnteriores telaDadosPartidasAnteriores)
	{
		this.telaDadosPartidasAnteriores = telaDadosPartidasAnteriores;
	}
	
	public void conectarMongo(String emailJogador)
	{
		// To directly connect to a single MongoDB server (note that this will not auto-discover the primary even
		// if it's a member of a replica set:
		// or
		MongoClient mongoClient;
		try {
			mongoClient = new MongoClient( "192.168.0.105" , 27017 );//IP MUDA
			DB db = mongoClient.getDB( "pairg_sumosensei_app" );
			this.colecaoPartidas = db.getCollection("partidas");
			BasicDBObject query = new BasicDBObject("email", emailJogador);
			 DBCursor cursorPercorreDocsJlpt = colecaoPartidas.find(query);
				Iterator<DBObject> iteraSobreColecao = cursorPercorreDocsJlpt.iterator();
				this.dadosPartidasParaLog = new LinkedList<DadosPartidaParaOLog>();
				while(iteraSobreColecao.hasNext())
				{
					DBObject umObjetoDaColecaoJlpt = iteraSobreColecao.next();
					String email = (String) umObjetoDaColecaoJlpt.get("email");
					BasicDBList categoria = (BasicDBList) umObjetoDaColecaoJlpt.get("categoria");
					String data = (String) umObjetoDaColecaoJlpt.get("data");
					String pontuacao = (String) umObjetoDaColecaoJlpt.get("pontuacao");
	            	String palavrasAcertadasString = (String)umObjetoDaColecaoJlpt.get("palavrasacertadas");
	            	String palavrasErradasString = (String)umObjetoDaColecaoJlpt.get("palavraserradas");
	            	BasicDBList palavrasJogadasArray = (BasicDBList) umObjetoDaColecaoJlpt.get("palavrasjogadas");
	            	String jogoAssociado = (String) umObjetoDaColecaoJlpt.get("jogoassociado"); //se eh o karuta kanji ou sumo sensei
	            	String eMailAdversario = (String)umObjetoDaColecaoJlpt.get("emailadversario");
	            	String voceGanhouOuPerdeu = (String)umObjetoDaColecaoJlpt.get("voceganhououperdeu");
					
					
				    DadosPartidaParaOLog dadosLog = new DadosPartidaParaOLog();
				    LinkedList<String> categoriasTreinadas = this.extrairListaArray(categoria);
	            	dadosLog.setCategoria(categoriasTreinadas);
	            	dadosLog.setData(data);
	            	dadosLog.setEmail(email);
	            	dadosLog.seteMailAdversario(eMailAdversario);
	            	dadosLog.setJogoAssociado(jogoAssociado);
	            	int pontuacaoInteiro = Integer.valueOf(pontuacao);
	            	dadosLog.setPontuacao(pontuacaoInteiro);
	            	dadosLog.setVoceGanhouOuPerdeu(voceGanhouOuPerdeu);
	            	
	            	LinkedList<KanjiTreinar> palavrasAcertadas = extrairKanjisTreinar(palavrasAcertadasString);
	            	LinkedList<KanjiTreinar> palavrasErradas = extrairKanjisTreinar(palavrasErradasString);
	            	LinkedList<KanjiTreinar> palavrasJogadas = extrairKanjisTreinarArray(palavrasJogadasArray);
	            	dadosLog.setPalavrasAcertadas(palavrasAcertadas);
	            	dadosLog.setPalavrasErradas(palavrasErradas);
	            	dadosLog.setPalavrasJogadas(palavrasJogadas);
	            	
	            	dadosPartidasParaLog.add(dadosLog);
				   
				}
				
				 this.telaDadosPartidasAnteriores.atualizarListViewComAsUltimasPartidas(dadosPartidasParaLog);
			
			
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}

		
	}
	
	@Override
	protected Void doInBackground(String... emailJogador) 
	{
		this.conectarMongo(emailJogador[0]);
		return null;
		
	} 
		
	protected void onPostExecute(Void v) 
	{
		this.telaDadosPartidasAnteriores.atualizarListViewComAsUltimasPartidas(dadosPartidasParaLog);
		        
	}

	/*pega a string do bd e transforma em montes de kanjis como era antes de enviar ao bd. Ex: au|verbos;kau|verbos...*/
	private LinkedList<KanjiTreinar> extrairKanjisTreinar(String kanjisTreinarEmString)
	{
		if(kanjisTreinarEmString.contains(";") == false)
		{
			//nao ha kanjis nessa string, vamos retornar linkedlist vazia
			return new LinkedList<KanjiTreinar>();
		}
		else
		{
			LinkedList<KanjiTreinar> kanjisTreinar = new LinkedList<KanjiTreinar>();
			String[] kanjisECategoriasComBarra = kanjisTreinarEmString.split(";");
			for(int i = 0; i < kanjisECategoriasComBarra.length; i++)
			{
				String umKanjiECategoria = kanjisECategoriasComBarra[i];
				String[] kanjiECategoria = umKanjiECategoria.split("\\|");
				String kanji = kanjiECategoria[0];
				String categoria = kanjiECategoria[1];
				
				KanjiTreinar kanjiTreinar = ArmazenaKanjisPorCategoria.pegarInstancia().acharKanji(categoria, kanji);
				kanjisTreinar.add(kanjiTreinar);
			}
			
			return kanjisTreinar;
		}
	}
	/*pega a string do bd e transforma em montes de kanjis como era antes de enviar ao bd. Ex: au|verbos;kau|verbos...*/
	private LinkedList<KanjiTreinar> extrairKanjisTreinarArray(BasicDBList kanjisECategoriasComBarra)
	{
		LinkedList<KanjiTreinar> kanjisTreinar = new LinkedList<KanjiTreinar>();
		
		for(int i = 0; i < kanjisECategoriasComBarra.size(); i++)
		{
			DBObject umKanjiECategoria = (DBObject)kanjisECategoriasComBarra.get(i);
			String kanji = (String) umKanjiECategoria.get("kanji");
			String categoria = (String) umKanjiECategoria.get("categoria");
			
			KanjiTreinar kanjiTreinar = ArmazenaKanjisPorCategoria.pegarInstancia().acharKanji(categoria, kanji);
			kanjisTreinar.add(kanjiTreinar);
		}
		
		return kanjisTreinar;
		
	}
	
	private LinkedList<String> extrairListaArray(BasicDBList listaEmDBList)
	{
		LinkedList<String> listaDoArray = new LinkedList<String>();
		
		for(int i = 0; i < listaEmDBList.size(); i++)
		{
			String umKanjiECategoria = (String)listaEmDBList.get(i);
			listaDoArray.add(umKanjiECategoria);
		}
		
		return listaDoArray;
		
	}

}
