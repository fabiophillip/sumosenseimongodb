package com.phiworks.sumosenseinew;

import java.util.LinkedList;

import bancodedados.DadosPartidaParaOLog;
import bancodedados.KanjiTreinar;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MostrarDadosUmaPartida extends ActivityDoJogoComSom implements View.OnClickListener
{
	private DadosPartidaParaOLog dadosUmaPartida;
	private String oQueAtualmenteListViewMostra; //pode ter 3 valores: palavrastreinadas,palavrasacertadas e palavraserradas
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mostrar_dados_uma_partida);
		
		SingletonDadosDeUmaPartidaASerMostrada conheceDadosUmaPartida = SingletonDadosDeUmaPartidaASerMostrada.getInstance();
		this.dadosUmaPartida = conheceDadosUmaPartida.getDadosUmaPartida();
		
		this.mostrarTodosOsDadosDaPartidaExcetoAsPalavras();
		this.fazerListViewMostrarPalavrasTreinadas();
		
		findViewById(R.id.setaEsquerda).setOnClickListener(this);
		findViewById(R.id.setaDireita).setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mostrar_dados_uma_partida, menu);
		return true;
	}
	
	
	private void mostrarTodosOsDadosDaPartidaExcetoAsPalavras()
	{
		TextView textViewDataPartida = (TextView) findViewById(R.id.dataDaPartida);
		TextView textViewEmailAdversario = (TextView) findViewById(R.id.emailDoAdversario);
		TextView textViewQuemVenceu = (TextView) findViewById(R.id.quemVenceu);
		TextView textViewPontuacao = (TextView) findViewById(R.id.suaPontuacao);
		TextView textViewCategorias = (TextView) findViewById(R.id.categorias);
		
		String labelDataPartida = getResources().getString(R.string.data_da_partida);
		String labelEmailAdversario = getResources().getString(R.string.email_do_adversario);
		String labelQuemVenceu = getResources().getString(R.string.quem_venceu);
		String labelSuaPontuacao = getResources().getString(R.string.suaPontuacao);
		String labelCategorias = getResources().getString(R.string.categorias);
		
		textViewDataPartida.setText(labelDataPartida + " " + this.dadosUmaPartida.getData());
		textViewEmailAdversario.setText(labelEmailAdversario + " " +this.dadosUmaPartida.geteMailAdversario());
		
		String ganhou = getResources().getString(R.string.ganhou);
		String perdeu = getResources().getString(R.string.perdeu);
		String empatou = getResources().getString(R.string.empatou);
		
		if(this.dadosUmaPartida.getVoceGanhouOuPerdeu().compareTo(ganhou) == 0)
		{
			String voce = getResources().getString(R.string.voce);
			textViewQuemVenceu.setText(labelQuemVenceu + " " + voce);
		}
		else if(this.dadosUmaPartida.getVoceGanhouOuPerdeu().compareTo(perdeu) == 0)
		{
			String adversario = getResources().getString(R.string.adversario);
			textViewQuemVenceu.setText(labelQuemVenceu + " " + adversario);
		}
		else
		{
			textViewQuemVenceu.setText(labelQuemVenceu + " " + empatou);
		}
		
		textViewPontuacao.setText(labelSuaPontuacao + " " + String.valueOf(this.dadosUmaPartida.getPontuacao()));
		
		String categoriasSeparadasPorVirgulas = this.dadosUmaPartida.getCategoriaEmString().replaceAll(";", ",");
		String ultimoCaractereDasCategorias = String.valueOf(categoriasSeparadasPorVirgulas.charAt(categoriasSeparadasPorVirgulas.length() - 1));
		if(ultimoCaractereDasCategorias.compareTo(",") == 0)
    	{
			categoriasSeparadasPorVirgulas = categoriasSeparadasPorVirgulas.substring(0,categoriasSeparadasPorVirgulas.length() - 1); //vamos tirar o ultimo caractere da string
    	}
		
		textViewCategorias.setText(labelCategorias + " " + categoriasSeparadasPorVirgulas);
	}
	
	private void fazerListViewMostrarPalavrasTreinadas()
	{
		this.oQueAtualmenteListViewMostra = "palavrastreinadas";
		
		TextView textViewTituloListView = (TextView)findViewById(R.id.tituloListView);
		String stringPalavrasTreinadas = getResources().getString(R.string.palavrasTreinadas);
		textViewTituloListView.setText(stringPalavrasTreinadas);
		
		LinkedList<KanjiTreinar> palavrasTreinadas = this.dadosUmaPartida.getPalavrasJogadas();
		int tamanhoValues = palavrasTreinadas.size();
		String[] values = new String[tamanhoValues];
        int percorredorValues = 0;
        
        for(int i = 0; i < palavrasTreinadas.size(); i++)
        {
        	KanjiTreinar umaPalavraTreinada = palavrasTreinadas.get(i);
        	String oQueApareceraComoItemNaLista = umaPalavraTreinada.getKanji() + " - " +
        					umaPalavraTreinada.getHiraganaDoKanji() + " - " +
        					umaPalavraTreinada.getTraducaoEmPortugues();
        	values[percorredorValues] = oQueApareceraComoItemNaLista;
        	percorredorValues = percorredorValues + 1;
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
        (this,android.R.layout.simple_list_item_1, android.R.id.text1, values){
            @Override
            public boolean isEnabled(int position) {
                return false;
            }
        };
;


        // Assign adapter to ListView
        ListView listView = (ListView) findViewById(R.id.listViewPalavrasAcertadasErradasTreinadas);
        listView.setAdapter(adapter);
	}
	
	private void fazerListViewMostrarPalavrasAcertadas()
	{
		this.oQueAtualmenteListViewMostra = "palavrasacertadas";
		
		TextView textViewTituloListView = (TextView)findViewById(R.id.tituloListView);
		String stringPalavrasAcertadas = getResources().getString(R.string.palavrasAcertadas);
		textViewTituloListView.setText(stringPalavrasAcertadas);
		
		LinkedList<KanjiTreinar> palavrasAcertadas = this.dadosUmaPartida.getPalavrasAcertadas();
		int tamanhoValues = palavrasAcertadas.size();
		String[] values = new String[tamanhoValues];
        int percorredorValues = 0;
        
        for(int i = 0; i < palavrasAcertadas.size(); i++)
        {
        	KanjiTreinar umaPalavraAcertada = palavrasAcertadas.get(i);
        	String oQueApareceraComoItemNaLista = umaPalavraAcertada.getKanji() + " - " +
        					umaPalavraAcertada.getHiraganaDoKanji() + " - " +
        					umaPalavraAcertada.getTraducaoEmPortugues();
        	values[percorredorValues] = oQueApareceraComoItemNaLista;
        	percorredorValues = percorredorValues + 1;
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
        (this,android.R.layout.simple_list_item_1, android.R.id.text1, values){
            @Override
            public boolean isEnabled(int position) {
                return false;
            }
        };
;


        // Assign adapter to ListView
        ListView listView = (ListView) findViewById(R.id.listViewPalavrasAcertadasErradasTreinadas);
        listView.setAdapter(adapter); 
	}
	
	private void fazerListViewMostrarPalavrasErradas()
	{
		this.oQueAtualmenteListViewMostra = "palavraserradas";
		
		TextView textViewTituloListView = (TextView)findViewById(R.id.tituloListView);
		String stringPalavrasErradas = getResources().getString(R.string.palavrasErradas);
		textViewTituloListView.setText(stringPalavrasErradas);
		
		LinkedList<KanjiTreinar> palavrasErradas = this.dadosUmaPartida.getPalavrasErradas();
		int tamanhoValues = palavrasErradas.size();
		String[] values = new String[tamanhoValues];
        int percorredorValues = 0;
        
        for(int i = 0; i < palavrasErradas.size(); i++)
        {
        	KanjiTreinar umaPalavraErrada = palavrasErradas.get(i);
        	String oQueApareceraComoItemNaLista = umaPalavraErrada.getKanji() + " - " +
        					umaPalavraErrada.getHiraganaDoKanji() + " - " +
        					umaPalavraErrada.getTraducaoEmPortugues();
        	values[percorredorValues] = oQueApareceraComoItemNaLista;
        	percorredorValues = percorredorValues + 1;
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
        (this,android.R.layout.simple_list_item_1, android.R.id.text1, values){
            @Override
            public boolean isEnabled(int position) {
                return false;
            }
        };
;


        // Assign adapter to ListView
        ListView listView = (ListView) findViewById(R.id.listViewPalavrasAcertadasErradasTreinadas);
        listView.setAdapter(adapter); 
	}
	
	private void usuarioClicouSetaEsquerda()
	{
		if(this.oQueAtualmenteListViewMostra.compareTo("palavrastreinadas") == 0)
		{
			this.fazerListViewMostrarPalavrasErradas();
		}
		else if(this.oQueAtualmenteListViewMostra.compareTo("palavrasacertadas") == 0)
		{
			this.fazerListViewMostrarPalavrasTreinadas();
		}
		else
		{
			this.fazerListViewMostrarPalavrasAcertadas();
		}
	}
	
	private void usuarioClicouSetaDireita()
	{
		if(this.oQueAtualmenteListViewMostra.compareTo("palavrastreinadas") == 0)
		{
			this.fazerListViewMostrarPalavrasAcertadas();
		}
		else if(this.oQueAtualmenteListViewMostra.compareTo("palavrasacertadas") == 0)
		{
			this.fazerListViewMostrarPalavrasErradas();
		}
		else
		{
			this.fazerListViewMostrarPalavrasTreinadas();
		}
	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) {
	    case R.id.setaEsquerda:
	    	usuarioClicouSetaEsquerda();
	    	break;
	    case R.id.setaDireita:
	    	usuarioClicouSetaDireita();
	    	break;
		}
		
	}

	@Override
	public void onSignInFailed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSignInSucceeded() {
		// TODO Auto-generated method stub
		
	}
}
