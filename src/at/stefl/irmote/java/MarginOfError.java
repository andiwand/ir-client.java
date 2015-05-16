package at.stefl.irmote.java;

public class MarginOfError {

	private double frequency;
	private boolean frequencyAbsolute;
	private double time;
	private boolean timeAbsolute;

	public MarginOfError(double frequency, double time) {
		this.frequency = Math.abs(frequency);
		this.frequencyAbsolute = frequency < 0;
		this.time = Math.abs(time);
		this.timeAbsolute = time < 0;
	}

	public boolean checkAbsolute(double measured, double reference,
			double absoluteError) {
		if (Double.isInfinite(absoluteError))
			return true;
		return (measured >= (reference - absoluteError))
				& (measured <= (reference + absoluteError));
	}

	public boolean checkRelative(double measured, double reference,
			double relativeError) {
		if (Double.isInfinite(relativeError))
			return true;
		return checkAbsolute(measured, reference, reference * relativeError);
	}

	public boolean checkFrequency(double measured, double reference) {
		if (frequencyAbsolute)
			return checkAbsolute(measured, reference, frequency);
		return checkRelative(measured, reference, frequency);
	}

	public boolean checkTime(double measured, double reference) {
		if (timeAbsolute)
			return checkAbsolute(measured, reference, time);
		return checkRelative(measured, reference, time);
	}

}