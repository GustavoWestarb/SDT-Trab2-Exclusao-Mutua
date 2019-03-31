package br.furb.main;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 
 * @author Bruno G. Vigentas, Luciane Tedesco, Gustavo Westarb & Rodrigo Soares
 *
 */
public class Util {
	/**
	 * Método que retorna um número aleatório no range de ZERO à max.
	 * 
	 * @param max - range final do número aleatório.
	 * @return número aleatório.
	 */
	public static long generateRandomNumber(int max) {
		Random random = new Random();
		return ThreadLocalRandom.current().nextLong(0, max);
	}
}
