package gui2d.animations;

import java.io.IOException;

import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;

import model.enums.Color;
import model.enums.PositionID;
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
			String s = serializer.unpackString(bString);
			Color color = s.equals("null") ? null : Color.valueOf(s);
			animation = new CardDrawAnimation(color);
			break;
		case DAMAGE_POSITION:
			// Unpack positionID:
			bString = serializer.unpackByteString(unpacker);
			s = serializer.unpackString(bString);
			PositionID posID = s.equals("null") ? null : PositionID.valueOf(s);
			// Unpack damageAmount:
			bString = serializer.unpackByteString(unpacker);
			int number = serializer.unpackInt(bString);
			animation = new DamageAnimation(AnimationType.DAMAGE_POSITION, posID, number);
			break;
		case HEAL_POSITION:
			// Unpack positionID:
			bString = serializer.unpackByteString(unpacker);
			s = serializer.unpackString(bString);
			posID = s.equals("null") ? null : PositionID.valueOf(s);
			// Unpack damageAmount:
			bString = serializer.unpackByteString(unpacker);
			number = serializer.unpackInt(bString);
			animation = new DamageAnimation(AnimationType.HEAL_POSITION, posID, number);
			break;
		case COIN_FLIP:
			// Unpack heads:
			bString = serializer.unpackByteString(unpacker);
			boolean b = serializer.unpackBool(bString);
			animation = new CoinflipAnimation(b);
			break;
		case CARD__MOVE:
			// Unpack from positionID:
			bString = serializer.unpackByteString(unpacker);
			s = serializer.unpackString(bString);
			PositionID from = s.equals("null") ? null : PositionID.valueOf(s);
			// Unpack to positionID:
			bString = serializer.unpackByteString(unpacker);
			s = serializer.unpackString(bString);
			PositionID to = s.equals("null") ? null : PositionID.valueOf(s);
			// Unpack cardID:
			bString = serializer.unpackByteString(unpacker);
			String id = serializer.unpackString(bString);
			// Unpack sound:
			bString = serializer.unpackByteString(unpacker);
			String sound = serializer.unpackString(bString);
			animation = new CardMoveAnimation(from, to, id, sound);
			break;
		default:
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
