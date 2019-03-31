package br.furb.main;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
	private static final int MAX_RANDOM = 99999;
	private static final String STR_REQUISICAO = 	" [REQUISIÇÃO] ";
	private static final String STR_ELEICAO = 	 	"    [ELEIÇÃO] ";
	private static final String STR_PROCESSO = 	 	"   [PROCESSO] ";
	private static final String STR_COORDENADOR = 	"[COORDENADOR] ";
	private static final String STR_EXCLUSAO = 	    "[EXCLUSÃO MÚTUA] ";
	private static final String STR_SEPARADOR = 	"------------------------------------------------------------------------------------------------";
	
	public static ArrayList<Processo> processos = new ArrayList<Processo>();
	public static ArrayList<Processo> processosEspera = new ArrayList<Processo>();
	public static Coordenador coordenador = new Coordenador();
	
	
	public static void main(String[] args) {
		
		
		/**
		 * Thread responsável pela criação de processos.
		 * Toda vez que um novo processo é criado, ele é adicionado a lista de processos
		 * 
		 * Processo recebe um número random em seu construtor, que será o ID do processo.
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
		 * Thread responsável por um processo aleatório fazer uma requisição ao coordenador para verificar se o mesmo
		 * ainda está ativo ou não.
		 * Se não estiver, o processo que fez a requisição chama uma eleição para um novo coordenador.
		 */
		Runnable fazerRequest = new Runnable() {	
			@Override
			public void run() {
				synchronized (coordenador) {
					if (processos.size() > 0) {
						System.out.println(STR_SEPARADOR);
						System.out.println(STR_REQUISICAO + "Um processo está fazendo requisição ao coordenador");
						Processo processo = null;
						processo = getProcessoDaLista(processos, coordenador);
						
						if (processo.requestCoordenador(coordenador)) {
							//Ok requisição respondida pelo coordenador
							System.out.println(STR_REQUISICAO + "Coordenador de ID "+ coordenador.getProcesso().getIdProcesso() +" respondeu a requisição do processo de ID " + processo.getIdProcesso());
						} else {
							//Erro na requisição, coordenador não respondeu
							System.out.println(STR_REQUISICAO + "Coordenador não respondeu a requisição do processo de ID  "+ processo.getIdProcesso() +", faz eleição");

							//Como o coordenador não respondeu, será chamado o processo de eleição
							System.out.println(STR_SEPARADOR);
							coordenador.setProcesso(Bully.executeRecursive(processos, processo, STR_ELEICAO));
							System.out.println(STR_ELEICAO + "Processo " + coordenador.getProcesso().getIdProcesso() + " se tornou coordenador.");
						}
					}
				}
			}
		};
		
		
		/**
		 * Thread responsável por inativar um processo aleatório da lista de processos.
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
		 * Thread responsável por inativar o coordenador.
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
		 * Thread responsável por gerenciar a lista de processamento do recurso.
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
							
							//Chama aqui a rotina que vai fazer o time de utilização do recurso
							new Recurso();
						}else {
							processosEspera.add(processos.get(indexProcesso));
							System.out.println(STR_SEPARADOR);
							System.out.println(STR_EXCLUSAO + "Processo de ID " + processos.get(indexProcesso).getIdProcesso() + " está aguardando o recurso ser liberado.");
						}
					}
				}
			}
		};
		
		/*
		 * Cria uma instancia do executor de tarefas e faz a chamadas das tarefas de
		 * criação de processo, requisição ao coordenador, inativação de processos e coordenador
		 * segundo os tempos descritos no enunciado.
		 * Quando é feita a requisião para o coordenador e não responde, é chamada na requisião.
		 */
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		
		//Executa a criação de um novo processo a cada X segundos.
		executor.scheduleAtFixedRate(criarProcesso, 0, 40, TimeUnit.SECONDS);
		//Executa o gerenciamento de consumo de recurso a cada X segundos.
		executor.scheduleAtFixedRate(gerenciarProcessamento, 10, 15, TimeUnit.SECONDS);
		//Executa a requisição ao coordenador a cada X segundos.
		executor.scheduleAtFixedRate(fazerRequest, 25, 25, TimeUnit.SECONDS);
		//Executa a inativação de um processo a cada X segundos.
		executor.scheduleAtFixedRate(inativarProcesso, 80, 80, TimeUnit.SECONDS);
		//Executa a inativação do coordenador a cada X segundos.
		executor.scheduleAtFixedRate(inativarCoordenador, 60, 60, TimeUnit.SECONDS);
		
	}
	
	
	/**
	 * 
	 * Método responsável por retornar um processo aleatório da lista de processos.
	 * Caso o processo encontrado seja  o coordenador ou estiver inativo, ele é executado novamente, até
	 * encontrar um processo que não é coordenador e e está ativo.
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
