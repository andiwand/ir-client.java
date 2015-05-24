package at.stefl.irclient.java;

import java.util.Map;

import at.stefl.commons.util.collection.CollectionUtil;
import at.stefl.commons.util.object.ObjectTransformer;

public enum PacketType {

	SEND_REQUEST(0), SEND_RESPONSE(1), RECEIVE_REQUEST(2), RECEIVE_RESPONSE(3), NETWORK_REQUEST(
			4), NETWORK_RESPONSE(5);

	private static final ObjectTransformer<PacketType, Integer> TYPE_KEY_GENERATOR = new ObjectTransformer<PacketType, Integer>() {
		@Override
		public Integer transform(PacketType value) {
			return value.type;
		}
	};

	private static final Map<Integer, PacketType> BY_TYPE_MAP = CollectionUtil
			.toHashMap(TYPE_KEY_GENERATOR, values());

	public static PacketType getByType(int type) {
		return BY_TYPE_MAP.get(type);
	}

	private PacketType(int type) {
		this.type = type;
	}

	private final int type;

	public int getType() {
		return type;
	}

}