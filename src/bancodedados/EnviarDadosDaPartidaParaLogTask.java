package bancodedados;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
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

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;



import android.os.AsyncTask;
import android.util.Log;

public class EnviarDadosDaPartidaParaLogTask extends AsyncTask<DadosPartidaParaOLog, String, String> 
{
	private void enviarDadosDaPartidaParaMongo(DadosPartidaParaOLog dadosPartidaGravar)
	{
		String categoria = dadosPartidaGravar.getCategoriaEmString();
		String [] categoriaSplitadaPontoEVirgula = categoria.split(";");
		List<String> categoriasPraMongo = new ArrayList<String>();
		for(int u = 0; u < categoriaSplitadaPontoEVirgula.length; u++)
		{
			String umaCategoria = categoriaSplitadaPontoEVirgula[u];
			categoriasPraMongo.add(umaCategoria);
			
		}
		String email = dadosPartidaGravar.getEmail();
		String data = dadosPartidaGravar.getData();
		String pontuacao = String.valueOf(dadosPartidaGravar.getPontuacao());
		
		LinkedList<KanjiTreinar> palavrasAcertadas = dadosPartidaGravar.getPalavrasAcertadas();
		List<BasicDBObject> palavrasAcertadasPraMongo = new ArrayList<BasicDBObject>();
		for(int y = 0; y < palavrasAcertadas.size(); y++)
		{
			KanjiTreinar umaPalavraAcertada = palavrasAcertadas.get(y);
			BasicDBObject novoObjetoKanji = new BasicDBObject();
			novoObjetoKanji.append("kanji", umaPalavraAcertada.getKanji());
			novoObjetoKanji.append("categoria", umaPalavraAcertada.getCategoriaAssociada());
			palavrasAcertadasPraMongo.add(novoObjetoKanji);
		}
		
		LinkedList<KanjiTreinar> palavrasErradas = dadosPartidaGravar.getPalavrasErradas();
		List<BasicDBObject> palavrasErradasPraMongo = new ArrayList<BasicDBObject>();
		for(int y = 0; y < palavrasErradas.size(); y++)
		{
			KanjiTreinar umaPalavraErrada = palavrasErradas.get(y);
			BasicDBObject novoObjetoKanji = new BasicDBObject();
			novoObjetoKanji.append("kanji", umaPalavraErrada.getKanji());
			novoObjetoKanji.append("categoria", umaPalavraErrada.getCategoriaAssociada());
			palavrasErradasPraMongo.add(novoObjetoKanji);
		}
		
		LinkedList<KanjiTreinar> palavrasJogadas = dadosPartidaGravar.getPalavrasJogadas();
		List<BasicDBObject> palavrasJogadasPraMongo = new ArrayList<BasicDBObject>();
		for(int y = 0; y < palavrasJogadas.size(); y++)
		{
			KanjiTreinar umaPalavraJogada = palavrasJogadas.get(y);
			BasicDBObject novoObjetoKanji = new BasicDBObject();
			novoObjetoKanji.append("kanji", umaPalavraJogada.getKanji());
			novoObjetoKanji.append("categoria", umaPalavraJogada.getCategoriaAssociada());
			palavrasJogadasPraMongo.add(novoObjetoKanji);
		}
		String jogoassociado = dadosPartidaGravar.getJogoAssociado();
		
		String emailadversario = dadosPartidaGravar.geteMailAdversario();
		String voceganhououperdeu = dadosPartidaGravar.getVoceGanhouOuPerdeu();
		try
		{
			BasicDBObject documentoNovo = new BasicDBObject("email", email)
				.append("data", data)
				.append("categoria", categoriasPraMongo)
				.append("pontuacao", pontuacao)
				.append("palavrasacertadas", palavrasAcertadasPraMongo)
				.append("palavraserradas", palavrasErradasPraMongo)
				.append("palavrasjogadas", palavrasJogadasPraMongo)
				.append("jogoassociado", jogoassociado)
				.append("emailadversario", emailadversario)
				.append("voceganhououperdeu", voceganhououperdeu);
			MongoClient mongoClient = new MongoClient( "10.5.29.51" , 27017 );//IP MUDA
			DB db = mongoClient.getDB( "pairg_sumosensei_app" );
			DBCollection colecaoPartidas = db.getCollection("partidas");
			colecaoPartidas.insert(documentoNovo);
		}
		catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}
		
	}
	
	@Override
	protected String doInBackground(DadosPartidaParaOLog... dadosPartida) 
	{
		DadosPartidaParaOLog umDadosPartida = dadosPartida[0];
		this.enviarDadosDaPartidaParaMongo(umDadosPartida);
		
		 
		return "";
	}
	
	/*o banco de dados nao pode armazenar linkedlist, por isso transformaremos uma linkedlist de kanjis em string*/
	private String transformarLinkedListKanjisEmString(LinkedList<KanjiTreinar> listaKanjis)
	{
		String listaEmString = "";
		for(int i = 0; i < listaKanjis.size(); i++)
		{
			KanjiTreinar umaPalavra = listaKanjis.get(i);
			listaEmString = listaEmString + umaPalavra.getKanji() + "|" + umaPalavra.getCategoriaAssociada() + ";";
		}
		
		return listaEmString;
	}
	

}
