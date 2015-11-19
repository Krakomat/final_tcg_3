package gui2d.animations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;

import network.serialization.TCGSerializer;
import network.tcp.messages.ByteString;

public class CardDrawAnimation extends Animation {

	// TODO which information for card draw?

	public CardDrawAnimation() {
		this.animationType = AnimationType.CARD_DRAW;
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

		packer.close();
		ByteString bString = new ByteString(out.toByteArray());
		return bString;
	}
}
