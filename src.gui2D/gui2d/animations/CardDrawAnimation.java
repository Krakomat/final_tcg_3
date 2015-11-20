package gui2d.animations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;

import model.enums.Color;
import network.serialization.TCGSerializer;
import network.tcp.messages.ByteString;

public class CardDrawAnimation extends Animation {

	private Color playerColor;

	public CardDrawAnimation(Color color) {
		this.animationType = AnimationType.CARD_DRAW;
		this.playerColor = color;
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

		// Color:
		b = serializer.packString(this.playerColor == null ? "null" : this.playerColor.toString());
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		packer.close();
		ByteString bString = new ByteString(out.toByteArray());
		return bString;
	}

	public Color getPlayerColor() {
		return playerColor;
	}
}
