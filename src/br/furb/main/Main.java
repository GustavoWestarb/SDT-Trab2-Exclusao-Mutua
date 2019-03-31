package br.furb.main;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
	private static final int MAX_RANDOM = 99999;
	private static final String STR_REQUISICAO = 	" [REQUISI��O] ";
	private static final String STR_ELEICAO = 	 	"    [ELEI��O] ";
	private static final String STR_PROCESSO = 	 	"   [PROCESSO] ";
	private static final String STR_COORDENADOR = 	"[COORDENADOR] ";
	private static final String STR_EXCLUSAO = 	    "[EXCLUS�O M�TUA] ";
	private static final String STR_SEPARADOR = 	"------------------------------------------------------------------------------------------------";
	
	public static ArrayList<Processo> processos = new ArrayList<Processo>();
	public static ArrayList<Processo> processosEspera = new ArrayList<Processo>();
	public static Coordenador coordenador = new Coordenador();
	
	
	public static void main(String[] args) {
		
		
		/**
		 * Thread respons�vel pela cria��o de processos.
		 * Toda vez que um novo processo � criado, ele � adicionado a lista de processos
		 * 
		 * Processo recebe um n�mero random em seu construtor, que ser� o ID do processo.
		 * Cria um novo processo na lista de processos.
		 */
		Runnable criarProcesso = new Runnable() {	
			@Override
			public void run() {
				synchronized (processos) {
					processos.add(new Processo(Util.generateRandomNumber(MAX_RANDOM)));
				}
			}
		};
		
		/**
		 * Thread respons�vel por um processo aleat�rio fazer uma requisi��o ao coordenador para verificar se o mesmo
		 * ainda est� ativo ou n�o.
		 * Se n�o estiver, o processo que fez a requisi��o chama uma elei��o para um novo coordenador.
		 */
		Runnable fazerRequest = new Runnable() {	
			@Override
			public void run() {
				synchronized (coordenador) {
					if (processos.size() > 0) {
						System.out.println(STR_SEPARADOR);
						System.out.println(STR_REQUISICAO + "Um processo est� fazendo requisi��o ao coordenador");
						Processo processo = null;
						processo = getProcessoDaLista(processos, coordenador);
						
						if (processo.requestCoordenador(coordenador)) {
							//Ok requisi��o respondida pelo coordenador
							System.out.println(STR_REQUISICAO + "Coordenador de ID "+ coordenador.getProcesso().getIdProcesso() +" respondeu a requisi��o do processo de ID " + processo.getIdProcesso());
						} else {
							//Erro na requisi��o, coordenador n�o respondeu
							System.out.println(STR_REQUISICAO + "Coordenador n�o respondeu a requisi��o do processo de ID  "+ processo.getIdProcesso() +", faz elei��o");

							//Como o coordenador n�o respondeu, ser� chamado o processo de elei��o
							System.out.println(STR_SEPARADOR);
							coordenador.setProcesso(Bully.executeRecursive(processos, processo, STR_ELEICAO));
							System.out.println(STR_ELEICAO + "Processo " + coordenador.getProcesso().getIdProcesso() + " se tornou coordenador.");
						}
					}
				}
			}
		};
		
		
		/**
		 * Thread respons�vel por inativar um processo aleat�rio da lista de processos.
		 */
		Runnable inativarProcesso = new Runnable() {
			@Override
			public void run() {
				
				if (processos.size() > 0) {
					Processo processo = null;
					processo = getProcessoDaLista(processos, coordenador);
					if (processo != null) {
						synchronized(processos) {
							processo.setAtivo(false);
							System.out.println(STR_SEPARADOR);
							System.out.println(STR_PROCESSO + "Processo de ID " + processo.getIdProcesso() + " inativado");
						}
					}
				}
				
			}
		};
		
		/**
		 * Thread respons�vel por inativar o coordenador.
		 */
		Runnable inativarCoordenador = new Runnable() {
			@Override
			public void run() {
				if (coordenador.getProcesso() != null) {
					synchronized(coordenador) {
						
						coordenador.getProcesso().setAtivo(false);
						System.out.println(STR_SEPARADOR);
						System.out.println(STR_COORDENADOR + "Coordenador com processo de ID " + coordenador.getProcesso().getIdProcesso() + " inativado");
						
						processosEspera = null;
						
						System.out.println(STR_SEPARADOR);
						System.out.println(STR_COORDENADOR + "Lista de processos a espera, foi removida junto ao coordenador");
					}
				}
			}
		};
		
		/**
		 * Thread respons�vel por gerenciar a lista de processamento do recurso.
		 */
		Runnable gerenciarProcessamento = new Runnable() {
			@Override
			public void run() {
				
				if (processos.isEmpty() == false) {
					 
					int indexProcesso = (int) (Math.random() * processos.size());
										
					if (processos.get(indexProcesso).isAtivo()) {
						if (processosEspera.isEmpty() && !coordenador.getProcesso().isConsumindo()) {
							
							coordenador.getProcesso().setConsumindo(true);
							System.out.println(STR_SEPARADOR);
							System.out.println(STR_EXCLUSAO + "Processo de ID " + processos.get(indexProcesso).getIdProcesso() + " consumindo o recurso.");
							
							//Chama aqui a rotina que vai fazer o time de utiliza��o do recurso
							new Recurso();
						}else {
							processosEspera.add(processos.get(indexProcesso));
							System.out.println(STR_SEPARADOR);
							System.out.println(STR_EXCLUSAO + "Processo de ID " + processos.get(indexProcesso).getIdProcesso() + " est� aguardando o recurso ser liberado.");
						}
					}
				}
			}
		};
		
		/*
		 * Cria uma instancia do executor de tarefas e faz a chamadas das tarefas de
		 * cria��o de processo, requisi��o ao coordenador, inativa��o de processos e coordenador
		 * segundo os tempos descritos no enunciado.
		 * Quando � feita a requisi�o para o coordenador e n�o responde, � chamada na requisi�o.
		 */
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		
		//Executa a cria��o de um novo processo a cada X segundos.
		executor.scheduleAtFixedRate(criarProcesso, 0, 40, TimeUnit.SECONDS);
		//Executa o gerenciamento de consumo de recurso a cada X segundos.
		executor.scheduleAtFixedRate(gerenciarProcessamento, 10, 15, TimeUnit.SECONDS);
		//Executa a requisi��o ao coordenador a cada X segundos.
		executor.scheduleAtFixedRate(fazerRequest, 25, 25, TimeUnit.SECONDS);
		//Executa a inativa��o de um processo a cada X segundos.
		executor.scheduleAtFixedRate(inativarProcesso, 80, 80, TimeUnit.SECONDS);
		//Executa a inativa��o do coordenador a cada X segundos.
		executor.scheduleAtFixedRate(inativarCoordenador, 60, 60, TimeUnit.SECONDS);
		
	}
	
	
	/**
	 * 
	 * M�todo respons�vel por retornar um processo aleat�rio da lista de processos.
	 * Caso o processo encontrado seja  o coordenador ou estiver inativo, ele � executado novamente, at�
	 * encontrar um processo que n�o � coordenador e e est� ativo.
	 * 
	 * @param processos
	 * @param coordenador
	 * @return
	 */
	public static Processo getProcessoDaLista(ArrayList<Processo> processos, Coordenador coordenador) {
		Processo processo;
		synchronized(processos) {
			if (coordenador.getProcesso() != null) {
				//Pega processo aleatoriamente, mas caso o processo pego aleatoriamente for coordenador ou estiver inativo, pega outro.
				do {
					processo = processos.get((int)Util.generateRandomNumber(processos.size()));
				} while (processo.getIdProcesso() == coordenador.getProcesso().getIdProcesso() || !processo.isAtivo());
			} else {
				//Pega processo aleatoriamento, mas caso esteja inativo, pega outro.
				do {
					processo = processos.get((int)Util.generateRandomNumber(processos.size()));
				} while (!processo.isAtivo());
			}
			return processo;
		}
	}
}
