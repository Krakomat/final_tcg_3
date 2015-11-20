package gui2d.animations;

import java.io.IOException;

import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;

import model.enums.Color;
import network.serialization.TCGSerializer;
import network.tcp.messages.ByteString;

/**
 * Abstract class for animation objects, which contain all information for the client gui to execute the animation.
 * 
 * @author Michael
 *
 */
public abstract class Animation {

	public static Animation unpackAnimation(ByteString byteString) throws IOException {
		TCGSerializer serializer = new TCGSerializer();
		MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(byteString.asInputStream());

		// Unpack animationType:
		ByteString bString = serializer.unpackByteString(unpacker);
		AnimationType animationType = AnimationType.valueOf(serializer.unpackString(bString));

		Animation animation = null;
		switch (animationType) {
		case CARD_DRAW:
			// Unpack color:
			bString = serializer.unpackByteString(unpacker);
			String c = serializer.unpackString(bString);
			Color color = c.equals("null") ? null : Color.valueOf(c);
			animation = new CardDrawAnimation(color);
			break;
		}

		unpacker.close();
		return animation;
	}

	protected AnimationType animationType;

	public abstract ByteString packAnimation() throws IOException;

	public AnimationType getAnimationType() {
		return animationType;
	}
}
