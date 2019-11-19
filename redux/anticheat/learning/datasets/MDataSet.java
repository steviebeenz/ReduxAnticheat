package redux.anticheat.learning.datasets;

import java.util.ArrayList;
import java.util.List;

public class MDataSet extends DataSet {

	private final List<Double> distances;

	public MDataSet() {
		distances = new ArrayList<Double>();
	}

	public void add(double speed) {
		distances.add(speed);
	}

	public double getStddev() {
		float stddev = 0;
		for (final double singleData : distances) {
			stddev += Math.pow(singleData - getMean(), 2);
		}
		return Math.sqrt(stddev / getLength());
	}

	public double getMean() {
		return (getSum() / getLength());
	}

	public double getDeltaStddev() {
		double delta_stddev = 0;
		final double[] deltas = getDeltas();
		for (final double delta : deltas) {
			delta_stddev += Math.pow(delta - getDeltaMean(), 2);
		}
		return Math.sqrt(delta_stddev / getDeltaLength());
	}

	public double getDeltaMean() {
		return (getDeltaSum() / getDeltaLength());
	}

	public double getDeltaSum() {
		double delta_sum = 0;
		final double[] deltas = getDeltas();
		for (final double delta_line : deltas) {
			delta_sum += delta_line;
		}
		return delta_sum;
	}

	public double[] getDeltas() {
		final double[] deltas = new double[getDeltaLength()];
		for (int i = 0; i <= getDeltaLength() - 1; i++) {
			deltas[i] = Math.abs(distances.get(i + 1) - distances.get(i));
		}
		return deltas;
	}

	public Double[] getAllDump() {
		return new Double[] { getStddev(), getDeltaStddev(), getMean(), getDeltaMean() };
	}

	public int getDeltaLength() {
		return getLength() - 1;
	}

	public int getLength() {
		return distances.size();
	}

	public float getSum() {
		float sum = 0;
		for (final double singleData : distances) {
			sum += singleData;
		}
		return sum;
	}

}
