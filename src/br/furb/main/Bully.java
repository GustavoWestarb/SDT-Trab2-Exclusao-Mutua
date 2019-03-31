package br.furb.main;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * 
 * @author Bruno G. Vigentas, Luciane Tedesco, Gustavo Westarb & Rodrigo Soares
 *
 */
public class Bully {
	
	/**
	 * Executa a elei��o, ser� buscado o processo com o maior id
	 * Se um processo com ID maior responder, o processo que chamou a elei��o desiste
	 * Caso n�o exista nenhum processo com ID maior que o processo que solicitou a elei��o, ele mesmo se torna o coordenador
	 * Ap�s isso � setado o novo coordernado;
	 * */
	public static Processo executeRecursive(ArrayList<Processo> processos, Processo processo, String str) {
		System.out.println(str + "Processo de ID " + processo.getIdProcesso() + " iniciou processo de elei��o");
		Processo maiorProcesso = processo;
		
		processos.remove(processos.indexOf(processo));
		for (Processo processoDaLista : processos) {
			if (processoDaLista.getIdProcesso() > processo.getIdProcesso() && processoDaLista.isAtivo()) {
				System.out.println(str + "Processo de ID " + processo.getIdProcesso() + " desistiu porque um ID maior respondeu");	
				maiorProcesso = executeRecursive(processos, processoDaLista, str);
				break;
			}
		}
		
		return maiorProcesso;
	}
	
}
