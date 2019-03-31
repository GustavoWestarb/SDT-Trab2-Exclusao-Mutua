package br.furb.main;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Recurso {
	
	private static final String STR_EXCLUSAO = 	    "[EXCLUS�O M�TUA] ";
	private static final String STR_SEPARADOR = 	"------------------------------------------------------------------------------------------------";

	Runnable gerenciarRecurso = new Runnable() {
		@Override
		public void run() {
			System.out.println(STR_SEPARADOR);
			System.out.println(STR_EXCLUSAO + "Utiliza��o do recurso terminado");
			Main.coordenador.getProcesso().setConsumindo(false);
			
			if (!Main.processosEspera.isEmpty()) {
								
				Main.processosEspera.remove(0);
				Main.coordenador.getProcesso().setConsumindo(true);
				new Recurso();
				System.out.println("Recurso disponivel para utiliza��o");
			}
		}
	};
	
	public Recurso(){
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		
		//Executa a cria��o de um novo processo a cada X segundos.
		executor.scheduleAtFixedRate(gerenciarRecurso, 0, 13, TimeUnit.SECONDS);
	}

}
