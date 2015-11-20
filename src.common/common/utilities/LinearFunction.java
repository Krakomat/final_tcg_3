package common.utilities;

import com.google.common.base.Preconditions;

public class LinearFunction {

	private float m, b;

	public LinearFunction(float x1, float y1, float x2, float y2) {
		Preconditions.checkArgument(x1 != x2, "Error: x1 = x2!");
		this.m = (y2 - y1) / (x2 - x1);
		this.b = (x1 * y2 - x2 * y1) / (x1 - x2);
		System.out.println(m);
		System.out.println(b);
	}

	public float function(float x) {
		return this.m * x + this.b;
	}
}
