package model.game;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;

import network.serialization.TCGSerializer;
import network.tcp.messages.ByteString;
import model.enums.GameState;

public class GameModelParameters {
	private int turnNumber;
	private GameState gameState;
	private boolean energyPlayed, retreatExecuted;
	private boolean noEnergyPayment; // no payment for attacks if true

	public GameModelParameters() {
		gameState = GameState.PREGAME;
		turnNumber = 0;
		energyPlayed = false;
		retreatExecuted = false;
		this.noEnergyPayment = false;
	}

	public GameModelParameters(ByteString b) throws IOException {
		MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(b.asInputStream());
		TCGSerializer serializer = new TCGSerializer();

		// Turn Number:
		ByteString bString = serializer.unpackByteString(unpacker);
		this.turnNumber = serializer.unpackInt(bString);

		// GameState:
		bString = serializer.unpackByteString(unpacker);
		this.gameState = GameState.valueOf(serializer.unpackString(bString));

		// energyPlayed:
		bString = serializer.unpackByteString(unpacker);
		this.energyPlayed = serializer.unpackBool(bString);

		// retreatExecuted:
		bString = serializer.unpackByteString(unpacker);
		this.retreatExecuted = serializer.unpackBool(bString);

		// noEnergyPayment:
		bString = serializer.unpackByteString(unpacker);
		this.noEnergyPayment = serializer.unpackBool(bString);

		unpacker.close();
	}

	public ByteString toByteString() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MessagePacker packer = MessagePack.newDefaultPacker(out);
		TCGSerializer serializer = new TCGSerializer();

		// Turn Number:
		ByteString turn = serializer.packInt(turnNumber);
		packer.packBinaryHeader(turn.length());
		packer.writePayload(turn.copyAsBytes());

		// GameState:
		ByteString state = serializer.packString(gameState.toString());
		packer.packBinaryHeader(state.length());
		packer.writePayload(state.copyAsBytes());

		// energyPlayed:
		ByteString energy = serializer.packBool(energyPlayed);
		packer.packBinaryHeader(energy.length());
		packer.writePayload(energy.copyAsBytes());

		// retreatExecuted:
		ByteString retreat = serializer.packBool(retreatExecuted);
		packer.packBinaryHeader(retreat.length());
		packer.writePayload(retreat.copyAsBytes());

		// noEnergyPayment:
		ByteString energyPayment = serializer.packBool(noEnergyPayment);
		packer.packBinaryHeader(energyPayment.length());
		packer.writePayload(energyPayment.copyAsBytes());

		packer.close();
		return new ByteString(out.toByteArray());
	}

	public GameModelParameters copy() {
		GameModelParameters copy = new GameModelParameters();
		copy.setTurnNumber(turnNumber);
		copy.setEnergyPlayed(energyPlayed);
		copy.setGameState(gameState);
		copy.setNoEnergyPayment(noEnergyPayment);
		copy.setRetreatExecuted(retreatExecuted);
		return copy;
	}

	public int getTurnNumber() {
		return turnNumber;
	}

	public void setTurnNumber(int turnNumber) {
		this.turnNumber = turnNumber;
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

	public boolean isEnergyPlayed() {
		return energyPlayed;
	}

	public void setEnergyPlayed(boolean energyPlayed) {
		this.energyPlayed = energyPlayed;
	}

	public boolean isRetreatExecuted() {
		return retreatExecuted;
	}

	public void setRetreatExecuted(boolean retreatExecuted) {
		this.retreatExecuted = retreatExecuted;
	}

	public boolean isNoEnergyPayment() {
		return noEnergyPayment;
	}

	public void setNoEnergyPayment(boolean noEnergyPayment) {
		this.noEnergyPayment = noEnergyPayment;
	}
}
