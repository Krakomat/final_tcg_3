package gui2d.animations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;

import com.google.common.base.Preconditions;

import model.enums.PositionID;
import network.serialization.TCGSerializer;
import network.tcp.messages.ByteString;

public class DamageAnimation extends Animation {
	private PositionID damagedPosition;
	private int damageAmount;

	public DamageAnimation(PositionID damagedPosition, int damageAmount) {
		super();
		Preconditions.checkArgument(damagedPosition != null, "Error: damagedPosition = null!");
		this.animationType = AnimationType.DAMAGE_POSITION;
		this.damagedPosition = damagedPosition;
		this.damageAmount = damageAmount;
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
		b = serializer.packPositionID(damagedPosition);
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// damageAmount:
		b = serializer.packInt(damageAmount);
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		packer.close();
		ByteString bString = new ByteString(out.toByteArray());
		return bString;
	}

	public PositionID getDamagedPosition() {
		return damagedPosition;
	}

	public int getDamageAmount() {
		return damageAmount;
	}

}
