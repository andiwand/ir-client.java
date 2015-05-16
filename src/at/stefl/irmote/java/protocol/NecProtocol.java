package at.stefl.irmote.java.protocol;

import at.stefl.irmote.java.MarginOfError;
import at.stefl.irmote.java.frame.NecFrame;
import at.stefl.irmote.java.frame.RawFrame;

public class NecProtocol extends IrProtocol<NecFrame> {

	public static final String NAME = "NEC";
	public static final NecProtocol INSTANCE = new NecProtocol();

	public static final double FREQUENCY = 38222;
	public static final double TIME_INIT_MARK = 9000e-6;
	public static final double TIME_INIT_SPACE = 4500e-6;
	public static final double TIME_REPEAT_SPACE = 2250e-6;
	public static final double TIME_BIT_MARK = 562.5e-6;
	public static final double TIME_ZERO_SPACE = 562.5e-6;
	public static final double TIME_ONE_SPACE = 1687.5e-6;
	public static final double TIME_END_MARK = 562.5e-6;
	public static final int TIMES = 67;
	public static final int TIMES_REPEAT = 3;
	public static final int REPEAT = 0xffffffff;

	private NecProtocol() {
		super(NAME);
	}

	@Override
	public double getFrequency() {
		return FREQUENCY;
	}

	@Override
	public Class<NecFrame> getFrameClass() {
		return NecFrame.class;
	}

	@Override
	public RawFrame encode(NecFrame frame) {
		RawFrame result = new RawFrame();
		result.frequency = FREQUENCY;

		int data = frame.getData();
		if (data == REPEAT) {
			result.times = new double[] { TIME_INIT_MARK, TIME_REPEAT_SPACE,
					TIME_END_MARK };
		} else {
			result.times = new double[TIMES];
			int i = 0;

			result.times[i++] = TIME_INIT_MARK;
			result.times[i++] = TIME_INIT_SPACE;

			for (int j = 0; j < 32; j++) {
				if (((data >> j) & 1) != 0) {
					result.times[i++] = TIME_BIT_MARK;
					result.times[i++] = TIME_ONE_SPACE;
				} else {
					result.times[i++] = TIME_BIT_MARK;
					result.times[i++] = TIME_ZERO_SPACE;
				}
			}

			result.times[i++] = TIME_END_MARK;
		}

		return result;
	}

	@Override
	public NecFrame decode(RawFrame raw, MarginOfError moe) {
		if (!moe.checkFrequency(raw.frequency, FREQUENCY))
			return null;
		if ((raw.times.length != TIMES) && (raw.times.length != TIMES_REPEAT))
			return null;

		int data = 0;
		int i = 0;
		if (!moe.checkTime(raw.times[i++], TIME_INIT_MARK))
			return null;

		if (moe.checkTime(raw.times[i], TIME_INIT_SPACE)) {
			i++;
			for (int j = 0; j < 32; j++) {
				if (!moe.checkTime(raw.times[i++], TIME_BIT_MARK))
					return null;
				if (moe.checkTime(raw.times[i], TIME_ONE_SPACE)) {
					data |= 1 << j;
				} else if (moe.checkTime(raw.times[i], TIME_ZERO_SPACE)) {
				} else {
					return null;
				}
				i++;
			}
		} else if (moe.checkTime(raw.times[i], TIME_REPEAT_SPACE)) {
			i++;
			data = REPEAT;
		} else {
			return null;
		}

		if (!moe.checkTime(raw.times[i++], TIME_END_MARK))
			return null;

		return new NecFrame(data);
	}

}