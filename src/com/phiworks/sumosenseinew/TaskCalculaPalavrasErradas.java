package com.phiworks.sumosenseinew;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import bancodedados.KanjiTreinar;

import com.phiworks.dapartida.GuardaDadosDaPartida;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.View;
import android.webkit.WebView.FindListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TaskCalculaPalavrasErradas extends AsyncTask<String, Integer, Void> {

	private FimDeTreino telaDeFimDeTreino;
	private ProgressDialog loadingDaTelaEmEspera;
	public TaskCalculaPalavrasErradas(ProgressDialog loadingDaTela, FimDeTreino telaDeFimDeTreino)
	{
		this.telaDeFimDeTreino = telaDeFimDeTreino;
		this.loadingDaTelaEmEspera = loadingDaTela;
	}
	@Override
	protected Void doInBackground(String... params) {
		//agrupar palavras erradas para se ter uma contagem de quantas vezes a palavra foi errada
	    HashMap<String, Integer> kanjiEQuantasVezesErrou = new HashMap<String, Integer>();
	    //guardar tambem uma referencia rapida para o kanji errado
	    HashMap<String, KanjiTreinar> textoKanjiEObjetoKanji = new HashMap<String, KanjiTreinar>();
	    GuardaDadosDaPartida guardaPalavrasErradasNaPartida = GuardaDadosDaPartida.getInstance();
	    LinkedList<KanjiTreinar> kanjisErradosNaPartida = guardaPalavrasErradasNaPartida.getKanjisErradosNaPartida();
	    for(int i = 0; i < kanjisErradosNaPartida.size(); i++)
	    {
	    	KanjiTreinar umKanjiErrado = kanjisErradosNaPartida.get(i);
	    	if(kanjiEQuantasVezesErrou.containsKey(umKanjiErrado.getKanji()) == true)
	    	{
	    		Integer quantasVezesKanjiFoiErrado = kanjiEQuantasVezesErrou.get(umKanjiErrado.getKanji());
	    		quantasVezesKanjiFoiErrado = quantasVezesKanjiFoiErrado + 1;
	    		kanjiEQuantasVezesErrou.put(umKanjiErrado.getKanji(), quantasVezesKanjiFoiErrado);
	    	}
	    	else
	    	{
	    		kanjiEQuantasVezesErrou.put(umKanjiErrado.getKanji(), 1);//errou uma vez ainda
	    		textoKanjiEObjetoKanji.put(umKanjiErrado.getKanji(), umKanjiErrado);
	    	}
	    }
	    
	    //criar agora uma lista com os dados dos kanjis errados
	    ArrayList<DadosDeKanjiErrado> arrayListKanjisErrados = new ArrayList<DadosDeKanjiErrado>();
	    Iterator<String> iteraSobreTextosKanjisErrados = textoKanjiEObjetoKanji.keySet().iterator();
	    while(iteraSobreTextosKanjisErrados.hasNext())
	    {
	    	String textoUmKanjiErrado = iteraSobreTextosKanjisErrados.next();
	    	
	    	DadosDeKanjiErrado novoDadoKanjiErrado = new DadosDeKanjiErrado();
	    	KanjiTreinar umKanjiErrado = textoKanjiEObjetoKanji.get(textoUmKanjiErrado);
	    	novoDadoKanjiErrado.setKanjiErrado(umKanjiErrado.getKanji());
	    	novoDadoKanjiErrado.setHiraganaErrado(umKanjiErrado.getHiraganaDoKanji());
	    	novoDadoKanjiErrado.setTraducaoErrada(umKanjiErrado.getTraducaoEmPortugues());
	    	String textoKanjiErrado = umKanjiErrado.getKanji();
	    	Integer quantasVezesKanjiFoiErrado = kanjiEQuantasVezesErrou.get(textoKanjiErrado);
	    	if(quantasVezesKanjiFoiErrado != null)
	    	{
	    		novoDadoKanjiErrado.setQuantasVezesKanjiFoiErrado(quantasVezesKanjiFoiErrado);
	    	}
	    	arrayListKanjisErrados.add(novoDadoKanjiErrado);
	    }
	    
	    //e, enfim, criar o adapter para a listView das palavras erradas
	    ArrayList<DadosDeKanjiErrado> kanjisErradosListaVazia = new ArrayList<DadosDeKanjiErrado>();
	    final AdapterListViewKanjisErrados adapterKanjisErrados = new AdapterListViewKanjisErrados
				(this.telaDeFimDeTreino, R.layout.item_lista_palavras_erradas, kanjisErradosListaVazia);

	    this.telaDeFimDeTreino.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				 // Assign adapter to ListView
			    ListView listviewKanjisErrados = (ListView) telaDeFimDeTreino.findViewById(R.id.lista_palavras_erradas);
				listviewKanjisErrados.setAdapter(adapterKanjisErrados);
				RelativeLayout campoKanjisErrados = (RelativeLayout) telaDeFimDeTreino.findViewById(R.id.campo_palavras_erradas);
				TextView textoNaoErrouNada = (TextView) telaDeFimDeTreino.findViewById(R.id.texto_errou_nada);
				if(adapterKanjisErrados.getArrayListKanjisErrados().size() == 0)
				{
					//usuario nao errou nenhum kanji
					
					campoKanjisErrados.setVisibility(View.INVISIBLE);
					textoNaoErrouNada.setVisibility(View.VISIBLE);
				}
				else
				{
					//usuario errou algo
					campoKanjisErrados.setVisibility(View.VISIBLE);
					textoNaoErrouNada.setVisibility(View.INVISIBLE);
				}
				// TODO Auto-generated method stub
				loadingDaTelaEmEspera.dismiss();
			}
		});
	    
	    adapterKanjisErrados.setListItems(arrayListKanjisErrados);
		
		return null;
	}

}
