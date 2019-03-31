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
	 * M�todo que retorna um n�mero aleat�rio no range de ZERO � max.
	 * 
	 * @param max - range final do n�mero aleat�rio.
	 * @return n�mero aleat�rio.
	 */
	public static long generateRandomNumber(int max) {
		Random random = new Random();
		return ThreadLocalRandom.current().nextLong(0, max);
	}
}
