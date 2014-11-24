package com.phiworks.domodocasual;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
import com.phiworks.sumosenseinew.ActivityQueEsperaAtePegarOsKanjis;
import com.phiworks.sumosenseinew.TelaModoCasual;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class BuscaSalasModoCasualTask extends AsyncTask<String, String, Void>
{
	private InputStream inputStream = null;
	private String result = ""; 
	private ProgressDialog loadingDaTelaEmEspera;//eh o dialog da tela em espera pelo resultado do web service
	private TelaModoCasual activityQueEsperaAtePegarAsSalas;
	private DBCursor cursor;
	private LinkedList<SalaAbertaModoCasual> salasModoCasual;

	public BuscaSalasModoCasualTask(ProgressDialog loadingDaTela, TelaModoCasual activityQueEsperaAteRequestTerminar)
	{
		this.loadingDaTelaEmEspera = loadingDaTela;
		this.activityQueEsperaAtePegarAsSalas = activityQueEsperaAteRequestTerminar;
	}

	@Override
	protected Void doInBackground(String... arg0) 
	{
		//LCC: http://10.9.99.239/amit/pegarjlptjson.php
				//sala de aula: http://10.5.26.127/amit/pegarjlptjson.php

		       //String url_select = "http://app.karutakanji.pairg.dimap.ufrn.br/pegarjlptjson.php";//android nao aceita localhost, tem de ser seu IP
				String url_select = "http://server.sumosensei.pairg.dimap.ufrn.br/app/pegarsalasmodocasualjson.php";
				//String url_select = "http://192.168.0.110/amit/pegarjlptjson.php";
		       
				ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

				 try 
			        {
			        	MongoClient mongo = new MongoClient("10.5.29.51", 27017);
						DB db = mongo.getDB("pairg_sumosensei_app");
						DBCollection collection = db.getCollection("salasmodocasual");
						DBObject searchBySalaAberta = new BasicDBObject("salaaberta", "sim");
						this.cursor = collection.find(searchBySalaAberta);
						
						this.salasModoCasual = new LinkedList<SalaAbertaModoCasual>();
			            while(cursor.hasNext() == true) 
			            {
			            	DBObject objetoDB = cursor.next();
			            	ObjectId idSalaMongo = (ObjectId) objetoDB.get( "_id" );
			            	int id_sala = idSalaMongo._inc();
							if(id_sala < 0)
							{
								id_sala = -id_sala;
							}
							
							List<String> categoriasList = (List<String>) objetoDB.get("categorias");
							LinkedList<String> categoriasLinkedList = new LinkedList<String>();
							for(int i = 0; i < categoriasList.size(); i++)
							{
								categoriasLinkedList.add(categoriasList.get(i));
								
							}
							
			         
			                String email_do_criador = (String) objetoDB.get("emaildocriador");
			                String dan_do_criador = (String) objetoDB.get("dandocriador");
			                
			                SalaAbertaModoCasual novaSalaModoCasual = new SalaAbertaModoCasual();
			                novaSalaModoCasual.setIdDaSala(id_sala);
			                novaSalaModoCasual.setCategoriasSelecionadas(categoriasLinkedList);
			                novaSalaModoCasual.setNivelDoUsuario(dan_do_criador);
			                novaSalaModoCasual.setNomeDeUsuario(email_do_criador);
			                
			                salasModoCasual.add(novaSalaModoCasual);

			            } // End Loop
						
			        } catch (IllegalStateException e3) {
			            Log.e("IllegalStateException", e3.toString());
			            e3.printStackTrace();
			        } catch (IOException e4) {
			            Log.e("IOException", e4.toString());
			            e4.printStackTrace();
			        }
		        
				return null;
	}
	
	 protected void onPostExecute(Void v) {
		 //parse JSON data
	        try {
	            //vamos reverter a ordem das salas, para coloca-las do mais recente pro menos
	            ArrayList<SalaAbertaModoCasual> salasModoCasualRevert = new ArrayList<SalaAbertaModoCasual>();
	            Iterator<SalaAbertaModoCasual> iteradorReverso = salasModoCasual.descendingIterator();
	            while(iteradorReverso.hasNext() == true)
	            {
	            	SalaAbertaModoCasual umaSala = iteradorReverso.next();
	            	salasModoCasualRevert.add(umaSala);
	            }
	            
	            if(activityQueEsperaAtePegarAsSalas.getSalasCarregadasModoCasual() == null)
	            {
	            	//nao ha listas de salas anteriores para comparar
	            	this.activityQueEsperaAtePegarAsSalas.mostrarListaComSalasAposCarregar(salasModoCasualRevert,false);
		            this.loadingDaTelaEmEspera.dismiss();
	            }
	            else
	            {
	            	//sera que a GUI precisa mesmo ser atualizada? E a lista de salas obtidas eh a mesma da apresentada?
	            	ArrayList<SalaAbertaModoCasual> antigasSalasCarregadasModoCasual = this.activityQueEsperaAtePegarAsSalas.getSalasCarregadasModoCasual();

		            boolean acheiSalasDiferentes = false;
		            boolean novasSalasForamAdicionadas = false;
		            if(antigasSalasCarregadasModoCasual != null && antigasSalasCarregadasModoCasual.size() == salasModoCasualRevert.size())
		            {
		            	
		            	//as arrayLists tem o mesmo tamanho. Então são iguais? Vamos ver...
		            	for(int u = 0; u < antigasSalasCarregadasModoCasual.size(); u++)
		            	{
		            		SalaAbertaModoCasual umaSalaAntigaCarregada = antigasSalasCarregadasModoCasual.get(u);
		            		SalaAbertaModoCasual salaAtualCarregada = salasModoCasualRevert.get(u);
		            		if(umaSalaAntigaCarregada.getIdDaSala() != salaAtualCarregada.getIdDaSala())
		            		{
		            			novasSalasForamAdicionadas = true;
		            			acheiSalasDiferentes = true;
		            			break;
		            		}
		            	}
		            }
		            else
		            {
		            	acheiSalasDiferentes = true;
		            	if(antigasSalasCarregadasModoCasual != null && antigasSalasCarregadasModoCasual.size() < salasModoCasualRevert.size())
		            	{
		            		novasSalasForamAdicionadas = true;
		            	}
		            	
		            }
		            if(acheiSalasDiferentes == true)
		            {
		            	
	        			
		            	this.activityQueEsperaAtePegarAsSalas.mostrarListaComSalasAposCarregar(salasModoCasualRevert, novasSalasForamAdicionadas);
		            }
		            
		            if(loadingDaTelaEmEspera!= null && loadingDaTelaEmEspera.isShowing())
	   			 	{
	   				 	this.loadingDaTelaEmEspera.dismiss();	 
	   			 	}
	            }
	           
	        } catch (Exception e) {
	            Log.e("JSONException", "Error: " + e.toString());
	            this.loadingDaTelaEmEspera.dismiss();
	        }
		 

	    }
}
