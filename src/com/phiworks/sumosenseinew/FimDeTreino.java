package com.phiworks.sumosenseinew;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import lojinha.ConcreteDAOAcessaDinheiroDoJogador;
import lojinha.DAOAcessaDinheiroDoJogador;
import lojinha.TransformaPontosEmCredito;

import bancodedados.KanjiTreinar;

import com.google.android.gms.internal.gu;
import com.phiworks.dapartida.GuardaDadosDaPartida;
import com.phiworks.domodocasual.AdapterListViewSalasCriadas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FimDeTreino extends ActivityDoJogoComSom {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fim_de_treino);
		
		
		TextView textviewTextoFinal = (TextView)findViewById(R.id.texto_vitoria_treino);
		int numeroAcertos = GuardaDadosDaPartida.getInstance().getRoundDaPartida() - 1;
		
		
		String fraseFinal = "" + numeroAcertos;
		if(numeroAcertos < 10)
		{
			fraseFinal = fraseFinal + " " + getResources().getString(R.string.vitoriaTeppo0);
			
		}
		else if(numeroAcertos < 20)
		{
			fraseFinal = fraseFinal + " " + getResources().getString(R.string.vitoriaTeppo1);
		}
		else if(numeroAcertos < 30)
		{
			fraseFinal = fraseFinal + " " +  getResources().getString(R.string.vitoriaTeppo2);
		}
		else if(numeroAcertos < 40)
		{
			fraseFinal = fraseFinal + " " +  getResources().getString(R.string.vitoriaTeppo3);
		}
		else
		{
			fraseFinal = fraseFinal + " " +  getResources().getString(R.string.vitoriaTeppo4);
		}
		textviewTextoFinal.setText(fraseFinal);

		final Animation animScale = AnimationUtils.loadAnimation(this, R.anim.anim_scale);
		animScale.setRepeatCount(Animation.INFINITE);
		View viewTextoFinalEFundo = findViewById(R.id.nihonball_e_text_final);
		//agora n�o tem mais a anima��o...
		//viewTextoFinalEFundo.startAnimation(animScale);
		
		//mudar fontes para fonte mais estilizada
		TextView tituloPalavrasErradas = (TextView)findViewById(R.id.label_palavras_erradas);
		TextView labelKanjiErrado = (TextView) findViewById(R.id.label_kanji_errado);
		TextView labelHiraganaErrado = (TextView) findViewById(R.id.label_hiragana_errado);
		TextView labelTraducaoErrada = (TextView) findViewById(R.id.label_traducao_errada);
		TextView labelQuantosErros = (TextView) findViewById(R.id.label_quantos_erros);
		TextView labelTextoVitoria = (TextView) findViewById(R.id.texto_vitoria_treino);
		TextView labelTextoErrouNada = (TextView) findViewById(R.id.texto_errou_nada);
	    String fontpath = "fonts/gilles_comic_br.ttf";
	    Typeface tf = Typeface.createFromAsset(getAssets(), fontpath);
	    tituloPalavrasErradas.setTypeface(tf);
	    labelKanjiErrado.setTypeface(tf);
	    labelHiraganaErrado.setTypeface(tf);
	    labelTraducaoErrada.setTypeface(tf);
	    labelQuantosErros.setTypeface(tf);
	    labelTextoVitoria.setTypeface(tf);
	    labelTextoErrouNada.setTypeface(tf);
	    
	    
	    calcularEMostrarKanjisErrados();
		
		//adicionar dinheirinho da partida no final
		int creditosAdicionarAoJogador = TransformaPontosEmCredito.converterPontosEmCredito(numeroAcertos);
		DAOAcessaDinheiroDoJogador daoDinheiroJogador = ConcreteDAOAcessaDinheiroDoJogador.getInstance();
		daoDinheiroJogador.adicionarCredito(creditosAdicionarAoJogador, this);
		String textoGanhouCreditoNaPartida = creditosAdicionarAoJogador + "x ";
		TextView textviewCreditoGanhoNaPartida = (TextView)findViewById(R.id.texto_moeda_ganha_no_jogo);
		textviewCreditoGanhoNaPartida.setText(textoGanhouCreditoNaPartida);
		
	}



	private void calcularEMostrarKanjisErrados() {
		
		//agora essa tarefa � de uma task para n�o ocupar tempo de processamento
		
		ProgressDialog barraProgressoFinalTerminouJogo =  ProgressDialog.show(FimDeTreino.this, getResources().getString(R.string.aviso_tempo_acaboou), getResources().getString(R.string.por_favor_aguarde));
		TaskCalculaPalavrasErradas taskParaMostrarKanjisErrados = 
				new TaskCalculaPalavrasErradas(barraProgressoFinalTerminouJogo, this);
		taskParaMostrarKanjisErrados.execute("");
		/*//agrupar palavras erradas para se ter uma contagem de quantas vezes a palavra foi errada
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
	    AdapterListViewKanjisErrados adapterKanjisErrados = new AdapterListViewKanjisErrados
				(this, R.layout.item_lista_palavras_erradas, arrayListKanjisErrados);

		 // Assign adapter to ListView
	    ListView listviewKanjisErrados = (ListView) findViewById(R.id.lista_palavras_erradas);
		listviewKanjisErrados.setAdapter(adapterKanjisErrados);*/
	}

	

	@Override
	public void onSignInFailed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSignInSucceeded() {
		// TODO Auto-generated method stub
		
	}
	
	/*public void encerrarFinalPartida(ArrayList<DadosDeKanjiErrado> arrayListKanjisErrados, ProgressDialog loadingDaTelaEmEspera)
	{
		// TODO Auto-generated method stub
		 // Assign adapter to ListView
		  //e, enfim, criar o adapter para a listView das palavras erradas
	    final AdapterListViewKanjisErrados adapterKanjisErrados = new AdapterListViewKanjisErrados
				(this, R.layout.item_lista_palavras_erradas, arrayListKanjisErrados);
	    ListView listviewKanjisErrados = (ListView) this.findViewById(R.id.lista_palavras_erradas);
		listviewKanjisErrados.setAdapter(adapterKanjisErrados);
		// TODO Auto-generated method stub
		loadingDaTelaEmEspera.dismiss();
	}*/
	
	
	
	
	public void voltarAoMenuPrincipal(View v)
	{
		Intent intentVoltaMenuPrincipal = new Intent(FimDeTreino.this, MainActivity.class);
    	intentVoltaMenuPrincipal.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(intentVoltaMenuPrincipal);
	}
	
	public void jogarNovamente(View v)
	{
		Intent intentVoltaMenuPrincipal = new Intent(FimDeTreino.this, EscolhaNivelActivity.class);
    	intentVoltaMenuPrincipal.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(intentVoltaMenuPrincipal);
    	finish();
	}

}
