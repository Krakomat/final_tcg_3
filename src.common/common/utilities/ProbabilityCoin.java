package common.utilities;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Will be used to simulate a coin with dynamic probabilities.
 * 
 * @author mfeldma2
 *
 */
public class ProbabilityCoin {
	private Random random;

	public ProbabilityCoin() {
		random = new SecureRandom();
	}

	public ProbabilityCoin(long seed) {
		random = new SecureRandom();
		random.setSeed(seed);
	}

	/**
	 * Returns true with the given probability and false otherwise. Throws an error if the given probability is not in the range between 0 (including) and 100 (excluding).
	 * 
	 * @param successProbability
	 * @return
	 */
	public boolean tossCoin(int successProbability) {
		if (successProbability < 0 || successProbability >= 100)
			throw new IllegalArgumentException("Error: Successprobability is not in the correct range: " + successProbability);
		int outcome = random.nextInt(100);

		if (outcome > successProbability)
			return false;
		return true;
	}

	/**
	 * Returns true with the given probability and false otherwise. Throws an error if the given probability is not in the range between 0 (including) and 1 (excluding).
	 * 
	 * @param successProbability
	 * @return
	 */
	public boolean tossCoin(float successProbability) {
		if (successProbability < 0 || successProbability >= 1)
			throw new IllegalArgumentException("Error: Successprobability is not in the correct range: " + successProbability);
		float outcome = random.nextFloat();

		if (outcome >= successProbability)
			return false;
		return true;
	}

	public void setSeed(long seed) {
		this.random.setSeed(seed);
	}
}