package bancodedados;

import java.util.LinkedList;

import com.mongodb.BasicDBList;

public class DadosPartidaParaOLog 
{
	private String email;
	private String data;
	private LinkedList<String> categoria; //pode ser mais de uma categoria separadas por ; Ex: "cotidiano 1; verbos;"
	private int pontuacao;
	private LinkedList<KanjiTreinar> palavrasAcertadas;
	private LinkedList<KanjiTreinar> palavrasErradas;
	private LinkedList<KanjiTreinar> palavrasJogadas;
	private String jogoAssociado; //se eh o karuta kanji ou sumo sensei
	private String eMailAdversario;
	private String voceGanhouOuPerdeu; //ganhou,perdeu ou empatou
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getCategoriaEmString() {
		String categoriaEmString = "";
		for(int i = 0; i < this.categoria.size(); i++)
		{
			String umaCategoria = this.categoria.get(i);
			categoriaEmString = categoriaEmString + umaCategoria + ";";
		}
		return categoriaEmString;
	}
	public void setCategoria(LinkedList<String> categoria2) {
		this.categoria = categoria2;
	}
	public int getPontuacao() {
		return pontuacao;
	}
	public void setPontuacao(int pontuacao) {
		this.pontuacao = pontuacao;
	}
	public LinkedList<KanjiTreinar> getPalavrasAcertadas() {
		return palavrasAcertadas;
	}
	public void setPalavrasAcertadas(LinkedList<KanjiTreinar> palavrasAcertadas) {
		this.palavrasAcertadas = palavrasAcertadas;
	}
	public LinkedList<KanjiTreinar> getPalavrasErradas() {
		return palavrasErradas;
	}
	public void setPalavrasErradas(LinkedList<KanjiTreinar> palavrasErradas) {
		this.palavrasErradas = palavrasErradas;
	}
	public LinkedList<KanjiTreinar> getPalavrasJogadas() {
		return palavrasJogadas;
	}
	public void setPalavrasJogadas(LinkedList<KanjiTreinar> palavrasJogadas) {
		this.palavrasJogadas = palavrasJogadas;
	}
	public String getJogoAssociado() {
		return jogoAssociado;
	}
	public void setJogoAssociado(String jogoAssociado) {
		this.jogoAssociado = jogoAssociado;
	}
	public String geteMailAdversario() {
		return eMailAdversario;
	}
	public void seteMailAdversario(String eMailAdversario) {
		this.eMailAdversario = eMailAdversario;
	}
	public String getVoceGanhouOuPerdeu() {
		return voceGanhouOuPerdeu;
	}
	public void setVoceGanhouOuPerdeu(String voceGanhouOuPerdeu) {
		this.voceGanhouOuPerdeu = voceGanhouOuPerdeu;
	}

}
