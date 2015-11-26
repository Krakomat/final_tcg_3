package gui2d.animations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;

import model.enums.PositionID;
import network.serialization.TCGSerializer;
import network.tcp.messages.ByteString;

public class CardMoveAnimation extends Animation {
	private PositionID fromPosition, toPosition;
	private String cardID;
	private String soundEffect;

	public CardMoveAnimation(PositionID fromPosition, PositionID toPosition, String cardID, String sound) {
		super();
		this.animationType = AnimationType.CARD__MOVE;
		this.fromPosition = fromPosition;
		this.toPosition = toPosition;
		this.cardID = cardID;
		this.soundEffect = sound;
	}

	@Override
	public ByteString packAnimation() throws IOException {
		TCGSerializer serializer = new TCGSerializer();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MessagePacker packer = MessagePack.newDefaultPacker(out);

		// Animation type:
		ByteString b = serializer.packString(this.animationType.toString());
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// from PositionID:
		b = serializer.packPositionID(fromPosition);
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// to PositionID:
		b = serializer.packPositionID(toPosition);
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// cardID:
		b = serializer.packString(cardID);
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// soundEffect:
		b = serializer.packString(soundEffect.toString());
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		packer.close();
		ByteString bString = new ByteString(out.toByteArray());
		return bString;
	}

	public String getSoundEffect() {
		return soundEffect;
	}

	public String getCardID() {
		return cardID;
	}

	public PositionID getFromPosition() {
		return fromPosition;
	}

	public PositionID getToPosition() {
		return toPosition;
	}
}
