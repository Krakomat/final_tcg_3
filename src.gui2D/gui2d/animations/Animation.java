package gui2d.animations;

import java.io.IOException;

import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;

import network.serialization.TCGSerializer;
import network.tcp.messages.ByteString;

/**
 * Abstract class for animation objects, which contain all information for the client gui to execute the animation.
 * 
 * @author Michael
 *
 */
public abstract class Animation {

	protected enum AnimationType {
		CARD_DRAW;
	}

	public static Animation unpackAnimation(ByteString byteString) throws IOException {
		TCGSerializer serializer = new TCGSerializer();
		MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(byteString.asInputStream());

		// Unpack animationType:
		ByteString bString = serializer.unpackByteString(unpacker);
		AnimationType animationType = AnimationType.valueOf(serializer.unpackString(bString));

		Animation animation = null;
		switch (animationType) {
		case CARD_DRAW:
			animation = new CardDrawAnimation();
			break;
		}

		unpacker.close();
		return animation;
	}

	protected AnimationType animationType;

	public abstract ByteString packAnimation();
}
