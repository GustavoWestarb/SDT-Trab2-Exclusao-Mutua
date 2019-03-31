package br.furb.main;

/**
 * 
 * @author Bruno G. Vigentas, Luciane Tedesco, Gustavo Westarb & Rodrigo Soares
 *
 */
public class Processo {

	private long idProcesso;

	private boolean ativo;
	
	private boolean consumindo;

	public Processo(long idProcesso) {
		this.idProcesso = idProcesso;
		this.ativo = true;
		System.out.println("------------------------------------------------------------------------------------------------");
		System.out.println("   [PROCESSO] Processo de ID " + idProcesso + " iniciado");
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
	public long getIdProcesso() {
		return idProcesso;
	}
	
	public boolean isConsumindo() {
		return consumindo;
	}
	
	public void setConsumindo(boolean consumindo) {
		this.consumindo = consumindo;
	}
	
	/**
	 * M�todo que faz a requisi��o ao coordenador.
	 * Se o processo que fez a requisi��o estiver ativo, e o processo que for o coordenador
	 * tamb�m estiver ativo e diferente de null, da um retorno positivo,
	 * retornando um boolean TRUE, caso contr�rio retorna um boolean FALSE.
	 * 
	 * @param coordenador
	 * @return TRUE - Respondeu requisi��o | FALSE - n�o respondeu requisi��o.
	 */
	public boolean requestCoordenador(Coordenador coordenador) {
		if (ativo) {
			if (coordenador.getProcesso() != null && coordenador.getProcesso().isAtivo()) {
				return true;
			}
		}
		return false;
	}

}
