package gui2d.animations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;

import network.serialization.TCGSerializer;
import network.tcp.messages.ByteString;

public class CoinflipAnimation extends Animation {
	private boolean heads;

	public CoinflipAnimation(boolean heads) {
		super();
		this.animationType = AnimationType.COIN_FLIP;
		this.heads = heads;
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

		// PositionID:
		b = serializer.packBool(heads);
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		packer.close();
		ByteString bString = new ByteString(out.toByteArray());
		return bString;
	}

	public boolean isHeads() {
		return heads;
	}
}
