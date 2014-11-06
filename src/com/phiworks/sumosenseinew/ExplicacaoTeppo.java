package com.phiworks.sumosenseinew;


import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class ExplicacaoTeppo extends ActivityDoJogoComSom {

	private boolean mostrarRegrasNovamente;//inicialmente isso é falso
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_explicacao_teppo);
		mostrarRegrasNovamente = true;

		
	}
	
	public void irParaSelecionarCategoriasTeppo(View v)
	{
		Intent iniciaTelaTreinoIndividual = new Intent(ExplicacaoTeppo.this, EscolhaNivelActivity.class);
		iniciaTelaTreinoIndividual.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(iniciaTelaTreinoIndividual);
		finish();
	}
	
	public void mudarValorMostrarRegrasTreinamento(View v)
	{
		Button checkboxMostrarRegrasNovamente = (Button)findViewById(R.id.checkbox_mostrar_regras_treinamento);
		if(this.mostrarRegrasNovamente == false)
		{
			this.mostrarRegrasNovamente = true;
			checkboxMostrarRegrasNovamente.setBackground(getResources().getDrawable(R.drawable.checkbox_desmarcada_regras_treinamento));
			
		}
		else
		{
			this.mostrarRegrasNovamente = false;
			checkboxMostrarRegrasNovamente.setBackground(getResources().getDrawable(R.drawable.checkbox_marcada_regras_treinamento));
			
		}
		ArmazenaMostrarRegrasTreinamento guardaConfiguracoes = ArmazenaMostrarRegrasTreinamento.getInstance();
		guardaConfiguracoes.alterarMostrarRegrasDoTreinamento(getApplicationContext(), mostrarRegrasNovamente);
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
