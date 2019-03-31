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
	 * Método que faz a requisição ao coordenador.
	 * Se o processo que fez a requisição estiver ativo, e o processo que for o coordenador
	 * também estiver ativo e diferente de null, da um retorno positivo,
	 * retornando um boolean TRUE, caso contrário retorna um boolean FALSE.
	 * 
	 * @param coordenador
	 * @return TRUE - Respondeu requisição | FALSE - não respondeu requisição.
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
