package common.utilities;

import java.util.Set;

/**
 * Models a function that is build out of multiple linear functions.
 * 
 * @author Michael
 *
 */
public class Function {
	private BijectiveMap<Pair<Float, Float>, LinearFunction> functionMap;

	public Function() {
		super();
		this.functionMap = new BijectiveMap<>();
	}

	public void addFunctionPart(float minValue, float maxValue, LinearFunction function) {
		this.functionMap.put(new Pair<Float, Float>(minValue, maxValue), function);
	}

	public float function(float x) {
		Set<Pair<Float, Float>> keySet = this.functionMap.keysAsSet();
		Pair<Float, Float> interval = null;
		for (Pair<Float, Float> key : keySet) {
			if (key.getKey() <= x && x <= key.getValue())
				interval = key;
		}

		if (interval == null)
			throw new IllegalArgumentException("No interval for this function value: " + x);

		LinearFunction f = this.functionMap.getValue(interval);
		return f.function(x);
	}
}
