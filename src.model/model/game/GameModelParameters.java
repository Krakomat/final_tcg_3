package model.game;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;

import network.serialization.TCGSerializer;
import network.tcp.messages.ByteString;
import model.enums.GameState;
import model.interfaces.GameModelUpdate;

public class GameModelParameters {
	private int turnNumber;
	private GameState gameState;
	private boolean energyPlayed, retreatExecuted;
	private boolean noEnergyPayment; // no payment for attacks if true
	private short allowedToPlayTrainerCards;
	private List<Integer> power_Active_00164_Muk, power_Active_00153_Aerodactyl, power_Activated_00117_Venomoth, power_Activated_00119_Vileplume,
			power_Activated_00143_Mankey, power_Activated_00155_Dragonite, power_Activated_00156_Gengar, power_Activated_00188_Omanite,
			power_Activated_00251_Rattata, power_Activated_00239_Drowzee, power_Activated_00235_Charmander, power_Activated_00224_DarkKadabra;

	public GameModelParameters() {
		gameState = GameState.PREGAME;
		turnNumber = 0;
		energyPlayed = false;
		retreatExecuted = false;
		this.noEnergyPayment = false;
		this.power_Active_00164_Muk = new ArrayList<>();
		this.power_Active_00153_Aerodactyl = new ArrayList<>();
		this.power_Activated_00143_Mankey = new ArrayList<>();
		this.power_Activated_00155_Dragonite = new ArrayList<>();
		this.power_Activated_00156_Gengar = new ArrayList<>();
		this.power_Activated_00117_Venomoth = new ArrayList<>();
		this.power_Activated_00119_Vileplume = new ArrayList<>();
		this.power_Activated_00188_Omanite = new ArrayList<>();
		this.power_Activated_00251_Rattata = new ArrayList<>();
		this.power_Activated_00239_Drowzee = new ArrayList<>();
		this.power_Activated_00235_Charmander = new ArrayList<>();
		this.power_Activated_00224_DarkKadabra = new ArrayList<>();
		this.allowedToPlayTrainerCards = 0;
	}

	public GameModelParameters(GameModelUpdate gameModelUpdate) {
		this.setTurnNumber(gameModelUpdate.getGameModelParameters().getTurnNumber());
		this.setGameState(GameState.RUNNING);
		this.setEnergyPlayed(gameModelUpdate.getGameModelParameters().isEnergyPlayed());
		this.setRetreatExecuted(gameModelUpdate.getGameModelParameters().isRetreatExecuted());
		this.setNoEnergyPayment(gameModelUpdate.getGameModelParameters().isNoEnergyPayment());
		this.setPower_Active_00164_Muk(gameModelUpdate.getGameModelParameters().getPower_Active_00164_Muk());
		this.setPower_Active_00153_Aerodactyl(gameModelUpdate.getGameModelParameters().getPower_Active_00153_Aerodactyl());
		this.setPower_Activated_00143_Mankey(gameModelUpdate.getGameModelParameters().isPower_Activated_00143_Mankey());
		this.setPower_Activated_00155_Dragonite(gameModelUpdate.getGameModelParameters().isPower_Activated_00155_Dragonite());
		this.setPower_Activated_00156_Gengar(gameModelUpdate.getGameModelParameters().getPower_Activated_00156_Gengar());
		this.setPower_Activated_00117_Venomoth(gameModelUpdate.getGameModelParameters().isPower_Activated_00117_Venomoth());
		this.setPower_Activated_00119_Vileplume(gameModelUpdate.getGameModelParameters().isPower_Activated_00119_Vileplume());
		this.setPower_Activated_00188_Omanite(gameModelUpdate.getGameModelParameters().getPower_Activated_00188_Omanite());
		this.setPower_Activated_00251_Rattata(gameModelUpdate.getGameModelParameters().getPower_Activated_00251_Rattata());
		this.setPower_Activated_00239_Drowzee(gameModelUpdate.getGameModelParameters().getPower_Activated_00239_Drowzee());
		this.setPower_Activated_00235_Charmander(gameModelUpdate.getGameModelParameters().getPower_Activated_00235_Charmander());
		this.setPower_Activated_00224_DarkKadabra(gameModelUpdate.getGameModelParameters().getPower_Activated_00224_DarkKadabra());
		this.setAllowedToPlayTrainerCards(gameModelUpdate.getGameModelParameters().isAllowedToPlayTrainerCards());
	}

	public GameModelParameters copy() {
		GameModelParameters copy = new GameModelParameters();
		copy.setTurnNumber(turnNumber);
		copy.setEnergyPlayed(energyPlayed);
		copy.setGameState(gameState);
		copy.setNoEnergyPayment(noEnergyPayment);
		copy.setRetreatExecuted(retreatExecuted);
		copy.setPower_Active_00164_Muk(this.getPower_Active_00164_Muk());
		copy.setPower_Active_00153_Aerodactyl(this.getPower_Active_00153_Aerodactyl());
		copy.setPower_Activated_00143_Mankey(this.isPower_Activated_00143_Mankey());
		copy.setPower_Activated_00155_Dragonite(this.isPower_Activated_00155_Dragonite());
		copy.setPower_Activated_00156_Gengar(this.getPower_Activated_00156_Gengar());
		copy.setPower_Activated_00117_Venomoth(this.isPower_Activated_00117_Venomoth());
		copy.setPower_Activated_00119_Vileplume(this.isPower_Activated_00119_Vileplume());
		copy.setPower_Activated_00188_Omanite(this.getPower_Activated_00188_Omanite());
		copy.setPower_Activated_00251_Rattata(this.getPower_Activated_00251_Rattata());
		copy.setPower_Activated_00239_Drowzee(this.getPower_Activated_00239_Drowzee());
		copy.setPower_Activated_00235_Charmander(this.getPower_Activated_00235_Charmander());
		copy.setPower_Activated_00224_DarkKadabra(this.getPower_Activated_00224_DarkKadabra());
		copy.setAllowedToPlayTrainerCards(this.isAllowedToPlayTrainerCards());
		return copy;
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

		// power_Active_00164_Muk
		bString = serializer.unpackByteString(unpacker);
		this.power_Active_00164_Muk = serializer.unpackIntList(bString);

		// power_Activated_00143_Mankey:
		bString = serializer.unpackByteString(unpacker);
		this.power_Activated_00143_Mankey = serializer.unpackIntList(bString);

		// power_Activated_00155_Dragonite:
		bString = serializer.unpackByteString(unpacker);
		this.power_Activated_00155_Dragonite = serializer.unpackIntList(bString);

		// power_Activated_00156_Gengar:
		bString = serializer.unpackByteString(unpacker);
		this.power_Activated_00156_Gengar = serializer.unpackIntList(bString);

		// power_Active_00153_Aerodactyl
		bString = serializer.unpackByteString(unpacker);
		this.power_Active_00153_Aerodactyl = serializer.unpackIntList(bString);

		// power_Activated_00117_Venomoth:
		bString = serializer.unpackByteString(unpacker);
		this.power_Activated_00117_Venomoth = serializer.unpackIntList(bString);

		// power_Activated_00119_Vileplume:
		bString = serializer.unpackByteString(unpacker);
		this.power_Activated_00119_Vileplume = serializer.unpackIntList(bString);

		// power_Activated_00188_Omanite:
		bString = serializer.unpackByteString(unpacker);
		this.power_Activated_00188_Omanite = serializer.unpackIntList(bString);

		// power_Activated_00251_Rattata:
		bString = serializer.unpackByteString(unpacker);
		this.power_Activated_00251_Rattata = serializer.unpackIntList(bString);

		// power_Activated_00239_Drowzee:
		bString = serializer.unpackByteString(unpacker);
		this.power_Activated_00239_Drowzee = serializer.unpackIntList(bString);

		// power_Activated_00235_Charmander:
		bString = serializer.unpackByteString(unpacker);
		this.power_Activated_00235_Charmander = serializer.unpackIntList(bString);

		// power_Activated_00224_DarkKadabra:
		bString = serializer.unpackByteString(unpacker);
		this.power_Activated_00224_DarkKadabra = serializer.unpackIntList(bString);

		// allowedToPlayTrainerCards:
		bString = serializer.unpackByteString(unpacker);
		this.allowedToPlayTrainerCards = serializer.unpackShort(bString);

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

		// power_Active_00164_Muk
		ByteString b = serializer.packIntList(power_Active_00164_Muk);
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// power_Activated_00143_Mankey:
		b = serializer.packIntList(power_Activated_00143_Mankey);
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// power_Activated_00155_Dragonite:
		b = serializer.packIntList(power_Activated_00155_Dragonite);
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// power_Activated_00156_Gengar:
		b = serializer.packIntList(power_Activated_00156_Gengar);
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// power_Active_00153_Aerodactyl
		b = serializer.packIntList(power_Active_00153_Aerodactyl);
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// power_Activated_00117_Venomoth:
		b = serializer.packIntList(power_Activated_00117_Venomoth);
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// power_Activated_00119_Vileplume:
		b = serializer.packIntList(power_Activated_00119_Vileplume);
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// power_Activated_00188_Omanite:
		b = serializer.packIntList(power_Activated_00188_Omanite);
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// power_Activated_00251_Rattata:
		b = serializer.packIntList(power_Activated_00251_Rattata);
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// power_Activated_00239_Drowzee:
		b = serializer.packIntList(power_Activated_00239_Drowzee);
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// power_Activated_00235_Charmander:
		b = serializer.packIntList(power_Activated_00235_Charmander);
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// power_Activated_00224_DarkKadabra:
		b = serializer.packIntList(power_Activated_00224_DarkKadabra);
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// allowedToPlayTrainerCards:
		b = serializer.packShort(allowedToPlayTrainerCards);
		packer.packBinaryHeader(energyPayment.length());
		packer.writePayload(energyPayment.copyAsBytes());

		packer.close();
		return new ByteString(out.toByteArray());
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

	public List<Integer> getPower_Active_00164_Muk() {
		return power_Active_00164_Muk;
	}

	public void setPower_Active_00164_Muk(List<Integer> power_Active_00164_Muk) {
		this.power_Active_00164_Muk = power_Active_00164_Muk;
	}

	public List<Integer> isPower_Activated_00143_Mankey() {
		return power_Activated_00143_Mankey;
	}

	public void setPower_Activated_00143_Mankey(List<Integer> power_Activated_00143_Mankey) {
		this.power_Activated_00143_Mankey = power_Activated_00143_Mankey;
	}

	public List<Integer> getPower_Active_00153_Aerodactyl() {
		return power_Active_00153_Aerodactyl;
	}

	public void setPower_Active_00153_Aerodactyl(List<Integer> power_Active_00153_Aerodactyl) {
		this.power_Active_00153_Aerodactyl = power_Active_00153_Aerodactyl;
	}

	public List<Integer> isPower_Activated_00155_Dragonite() {
		return power_Activated_00155_Dragonite;
	}

	public void setPower_Activated_00155_Dragonite(List<Integer> power_Activated_00155_Dragonite) {
		this.power_Activated_00155_Dragonite = power_Activated_00155_Dragonite;
	}

	public List<Integer> isPower_Activated_00117_Venomoth() {
		return power_Activated_00117_Venomoth;
	}

	public void setPower_Activated_00117_Venomoth(List<Integer> power_Activated_00117_Venomoth) {
		this.power_Activated_00117_Venomoth = power_Activated_00117_Venomoth;
	}

	public List<Integer> isPower_Activated_00119_Vileplume() {
		return power_Activated_00119_Vileplume;
	}

	public void setPower_Activated_00119_Vileplume(List<Integer> power_Activated_00119_Vileplume) {
		this.power_Activated_00119_Vileplume = power_Activated_00119_Vileplume;
	}

	public List<Integer> getPower_Activated_00156_Gengar() {
		return power_Activated_00156_Gengar;
	}

	public void setPower_Activated_00156_Gengar(List<Integer> power_Activated_00156_Gengar) {
		this.power_Activated_00156_Gengar = power_Activated_00156_Gengar;
	}

	public List<Integer> getPower_Activated_00188_Omanite() {
		return power_Activated_00188_Omanite;
	}

	public void setPower_Activated_00188_Omanite(List<Integer> power_Activated_00188_Omanite) {
		this.power_Activated_00188_Omanite = power_Activated_00188_Omanite;
	}

	public short isAllowedToPlayTrainerCards() {
		return allowedToPlayTrainerCards;
	}

	public void setAllowedToPlayTrainerCards(short allowedToPlayTrainerCards) {
		this.allowedToPlayTrainerCards = allowedToPlayTrainerCards;
	}

	public List<Integer> getPower_Activated_00251_Rattata() {
		return power_Activated_00251_Rattata;
	}

	public void setPower_Activated_00251_Rattata(List<Integer> power_Activated_00251_Rattata) {
		this.power_Activated_00251_Rattata = power_Activated_00251_Rattata;
	}

	public List<Integer> getPower_Activated_00239_Drowzee() {
		return power_Activated_00239_Drowzee;
	}

	public void setPower_Activated_00239_Drowzee(List<Integer> power_Activated_00239_Drowzee) {
		this.power_Activated_00239_Drowzee = power_Activated_00239_Drowzee;
	}

	public List<Integer> getPower_Activated_00235_Charmander() {
		return power_Activated_00235_Charmander;
	}

	public void setPower_Activated_00235_Charmander(List<Integer> power_Activated_00235_Charmander) {
		this.power_Activated_00235_Charmander = power_Activated_00235_Charmander;
	}

	public List<Integer> getPower_Activated_00224_DarkKadabra() {
		return power_Activated_00224_DarkKadabra;
	}

	public void setPower_Activated_00224_DarkKadabra(List<Integer> power_Activated_00224_DarkKadabra) {
		this.power_Activated_00224_DarkKadabra = power_Activated_00224_DarkKadabra;
	}
}
