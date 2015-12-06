package model.game;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;

import common.utilities.Pair;
import network.serialization.TCGSerializer;
import network.tcp.messages.ByteString;
import model.enums.GameState;
import model.interfaces.GameModelUpdate;

public class GameModelParameters {
	private int turnNumber;
	private GameState gameState;
	private boolean energyPlayed, retreatExecuted;
	private boolean noEnergyPayment; // no payment for attacks if true
	private short allowedToPlayTrainerCards, allowedToPlayPokemonPower;
	private List<Integer> lauchschlagUsed_00027_Porenta, power_Active_00164_Muk, power_Active_00153_Aerodactyl, power_Activated_00117_Venomoth, power_Activated_00119_Vileplume,
			power_Activated_00143_Mankey, power_Activated_00155_Dragonite, power_Activated_00156_Gengar, power_Activated_00188_Omanite, power_Activated_00251_Rattata,
			power_Activated_00239_Drowzee, power_Activated_00235_Charmander, power_Activated_00224_DarkKadabra, power_Activated_00221_DarkGloom,
			power_Activated_00218_DarkDragonair, power_Activated_00212_DarkVileplume;
	private List<Pair<Integer, Integer>> lieLowUsed_00287_BrocksDugtrio, tunnelingUsed_00269_BrocksOnix;

	public GameModelParameters() {
		gameState = GameState.PREGAME;
		turnNumber = 0;
		energyPlayed = false;
		retreatExecuted = false;
		this.noEnergyPayment = false;
		this.lauchschlagUsed_00027_Porenta = new ArrayList<>();
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
		this.power_Activated_00221_DarkGloom = new ArrayList<>();
		this.power_Activated_00218_DarkDragonair = new ArrayList<>();
		this.power_Activated_00212_DarkVileplume = new ArrayList<>();
		this.lieLowUsed_00287_BrocksDugtrio = new ArrayList<>();
		this.tunnelingUsed_00269_BrocksOnix = new ArrayList<>();
		this.allowedToPlayTrainerCards = 0;
		this.allowedToPlayPokemonPower = 0;
	}

	public GameModelParameters(GameModelUpdate gameModelUpdate) {
		this.setTurnNumber(gameModelUpdate.getGameModelParameters().getTurnNumber());
		this.setGameState(gameModelUpdate.getGameModelParameters().getGameState());
		this.setEnergyPlayed(gameModelUpdate.getGameModelParameters().isEnergyPlayed());
		this.setRetreatExecuted(gameModelUpdate.getGameModelParameters().isRetreatExecuted());
		this.setNoEnergyPayment(gameModelUpdate.getGameModelParameters().isNoEnergyPayment());
		this.lauchschlagUsed_00027_Porenta = new ArrayList<>();
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
		this.power_Activated_00221_DarkGloom = new ArrayList<>();
		this.power_Activated_00218_DarkDragonair = new ArrayList<>();
		this.power_Activated_00212_DarkVileplume = new ArrayList<>();
		this.lieLowUsed_00287_BrocksDugtrio = new ArrayList<>();
		this.tunnelingUsed_00269_BrocksOnix = new ArrayList<>();
		for (Integer i : gameModelUpdate.getGameModelParameters().getLauchschlagUsed_00027_Porenta())
			this.lauchschlagUsed_00027_Porenta.add(i);
		for (Integer i : gameModelUpdate.getGameModelParameters().getPower_Active_00164_Muk())
			this.power_Active_00164_Muk.add(i);
		for (Integer i : gameModelUpdate.getGameModelParameters().getPower_Active_00153_Aerodactyl())
			this.power_Active_00153_Aerodactyl.add(i);
		for (Integer i : gameModelUpdate.getGameModelParameters().isPower_Activated_00143_Mankey())
			this.power_Activated_00143_Mankey.add(i);
		for (Integer i : gameModelUpdate.getGameModelParameters().isPower_Activated_00155_Dragonite())
			this.power_Activated_00155_Dragonite.add(i);
		for (Integer i : gameModelUpdate.getGameModelParameters().getPower_Activated_00156_Gengar())
			this.power_Activated_00156_Gengar.add(i);
		for (Integer i : gameModelUpdate.getGameModelParameters().isPower_Activated_00117_Venomoth())
			this.power_Activated_00117_Venomoth.add(i);
		for (Integer i : gameModelUpdate.getGameModelParameters().isPower_Activated_00119_Vileplume())
			this.power_Activated_00119_Vileplume.add(i);
		for (Integer i : gameModelUpdate.getGameModelParameters().getPower_Activated_00188_Omanite())
			this.power_Activated_00188_Omanite.add(i);
		for (Integer i : gameModelUpdate.getGameModelParameters().getPower_Activated_00251_Rattata())
			this.power_Activated_00251_Rattata.add(i);
		for (Integer i : gameModelUpdate.getGameModelParameters().getPower_Activated_00239_Drowzee())
			this.power_Activated_00239_Drowzee.add(i);
		for (Integer i : gameModelUpdate.getGameModelParameters().getPower_Activated_00235_Charmander())
			this.power_Activated_00235_Charmander.add(i);
		for (Integer i : gameModelUpdate.getGameModelParameters().getPower_Activated_00224_DarkKadabra())
			this.power_Activated_00224_DarkKadabra.add(i);
		for (Integer i : gameModelUpdate.getGameModelParameters().getPower_Activated_00221_DarkGloom())
			this.power_Activated_00221_DarkGloom.add(i);
		for (Integer i : gameModelUpdate.getGameModelParameters().getPower_Activated_00218_DarkDragonair())
			this.power_Activated_00218_DarkDragonair.add(i);
		for (Integer i : gameModelUpdate.getGameModelParameters().getPower_Activated_00212_DarkVileplume())
			this.power_Activated_00212_DarkVileplume.add(i);
		for (Pair<Integer, Integer> i : gameModelUpdate.getGameModelParameters().getLieLowUsed_00287_BrocksDugtrio())
			this.lieLowUsed_00287_BrocksDugtrio.add(i);
		for (Pair<Integer, Integer> i : gameModelUpdate.getGameModelParameters().getTunnelingUsed_00269_BrocksOnix())
			this.tunnelingUsed_00269_BrocksOnix.add(i);

		this.setAllowedToPlayTrainerCards(gameModelUpdate.getGameModelParameters().isAllowedToPlayTrainerCards());
		this.setAllowedToPlayPokemonPower(gameModelUpdate.getGameModelParameters().isAllowedToPlayPokemonPower());
	}

	public GameModelParameters copy() {
		GameModelParameters copy = new GameModelParameters();
		copy.setTurnNumber(turnNumber);
		copy.setEnergyPlayed(energyPlayed);
		copy.setGameState(gameState);
		copy.setNoEnergyPayment(noEnergyPayment);
		copy.setRetreatExecuted(retreatExecuted);
		for (Integer i : this.getLauchschlagUsed_00027_Porenta())
			copy.getLauchschlagUsed_00027_Porenta().add(i);
		for (Integer i : this.getPower_Active_00164_Muk())
			copy.getPower_Active_00164_Muk().add(i);
		for (Integer i : this.getPower_Active_00153_Aerodactyl())
			copy.getPower_Active_00153_Aerodactyl().add(i);
		for (Integer i : this.isPower_Activated_00143_Mankey())
			copy.isPower_Activated_00143_Mankey().add(i);
		for (Integer i : this.isPower_Activated_00155_Dragonite())
			copy.isPower_Activated_00155_Dragonite().add(i);
		for (Integer i : this.getPower_Activated_00156_Gengar())
			copy.getPower_Activated_00156_Gengar().add(i);
		for (Integer i : this.isPower_Activated_00117_Venomoth())
			copy.isPower_Activated_00117_Venomoth().add(i);
		for (Integer i : this.isPower_Activated_00119_Vileplume())
			copy.isPower_Activated_00119_Vileplume().add(i);
		for (Integer i : this.getPower_Activated_00188_Omanite())
			copy.getPower_Activated_00188_Omanite().add(i);
		for (Integer i : this.getPower_Activated_00251_Rattata())
			copy.getPower_Activated_00251_Rattata().add(i);
		for (Integer i : this.getPower_Activated_00239_Drowzee())
			copy.getPower_Activated_00239_Drowzee().add(i);
		for (Integer i : this.getPower_Activated_00235_Charmander())
			copy.getPower_Activated_00235_Charmander().add(i);
		for (Integer i : this.getPower_Activated_00224_DarkKadabra())
			copy.getPower_Activated_00224_DarkKadabra().add(i);
		for (Integer i : this.getPower_Activated_00221_DarkGloom())
			copy.getPower_Activated_00221_DarkGloom().add(i);
		for (Integer i : this.getPower_Activated_00218_DarkDragonair())
			copy.getPower_Activated_00218_DarkDragonair().add(i);
		for (Integer i : this.getPower_Activated_00212_DarkVileplume())
			copy.getPower_Activated_00212_DarkVileplume().add(i);
		for (Pair<Integer, Integer> i : this.getLieLowUsed_00287_BrocksDugtrio())
			copy.getLieLowUsed_00287_BrocksDugtrio().add(i);
		for (Pair<Integer, Integer> i : this.getTunnelingUsed_00269_BrocksOnix())
			copy.getTunnelingUsed_00269_BrocksOnix().add(i);
		copy.setAllowedToPlayTrainerCards(this.isAllowedToPlayTrainerCards());
		copy.setAllowedToPlayPokemonPower(this.isAllowedToPlayPokemonPower());
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

		// lauchschlagUsed_00027_Porenta
		bString = serializer.unpackByteString(unpacker);
		this.lauchschlagUsed_00027_Porenta = serializer.unpackIntList(bString);

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

		// power_Activated_00221_DarkGloom:
		bString = serializer.unpackByteString(unpacker);
		this.power_Activated_00221_DarkGloom = serializer.unpackIntList(bString);

		// power_Activated_00218_DarkDragonair:
		bString = serializer.unpackByteString(unpacker);
		this.power_Activated_00218_DarkDragonair = serializer.unpackIntList(bString);

		// power_Activated_00212_DarkVileplume:
		bString = serializer.unpackByteString(unpacker);
		this.power_Activated_00212_DarkVileplume = serializer.unpackIntList(bString);

		// lieLowUsed_00287_BrocksDugtrio:
		bString = serializer.unpackByteString(unpacker);
		this.lieLowUsed_00287_BrocksDugtrio = serializer.unpackIntegerPairList(bString);

		// tunnelingUsed_00269_BrocksOnix:
		bString = serializer.unpackByteString(unpacker);
		this.tunnelingUsed_00269_BrocksOnix = serializer.unpackIntegerPairList(bString);

		// allowedToPlayTrainerCards:
		bString = serializer.unpackByteString(unpacker);
		this.allowedToPlayTrainerCards = serializer.unpackShort(bString);

		// allowedToPlayPokemonPower:
		bString = serializer.unpackByteString(unpacker);
		this.allowedToPlayPokemonPower = serializer.unpackShort(bString);

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

		// lauchschlagUsed_00027_Porenta
		ByteString b = serializer.packIntList(lauchschlagUsed_00027_Porenta);
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// power_Active_00164_Muk
		b = serializer.packIntList(power_Active_00164_Muk);
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

		// power_Activated_00221_DarkGloom:
		b = serializer.packIntList(power_Activated_00221_DarkGloom);
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// power_Activated_00218_DarkDragonair:
		b = serializer.packIntList(power_Activated_00218_DarkDragonair);
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// power_Activated_00212_DarkVileplume:
		b = serializer.packIntList(power_Activated_00212_DarkVileplume);
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// lieLowUsed_00287_BrocksDugtrio:
		b = serializer.packIntPairList(lieLowUsed_00287_BrocksDugtrio);
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// tunnelingUsed_00269_BrocksOnix:
		b = serializer.packIntPairList(tunnelingUsed_00269_BrocksOnix);
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// allowedToPlayTrainerCards:
		b = serializer.packShort(allowedToPlayTrainerCards);
		packer.packBinaryHeader(energyPayment.length());
		packer.writePayload(energyPayment.copyAsBytes());

		// allowedToPlayPokemonPower:
		b = serializer.packShort(allowedToPlayPokemonPower);
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

	public List<Integer> getPower_Activated_00221_DarkGloom() {
		return power_Activated_00221_DarkGloom;
	}

	public void setPower_Activated_00221_DarkGloom(List<Integer> power_Activated_00221_DarkGloom) {
		this.power_Activated_00221_DarkGloom = power_Activated_00221_DarkGloom;
	}

	public List<Integer> getPower_Activated_00218_DarkDragonair() {
		return power_Activated_00218_DarkDragonair;
	}

	public void setPower_Activated_00218_DarkDragonair(List<Integer> power_Activated_00218_DarkDragonair) {
		this.power_Activated_00218_DarkDragonair = power_Activated_00218_DarkDragonair;
	}

	public List<Integer> getPower_Activated_00212_DarkVileplume() {
		return power_Activated_00212_DarkVileplume;
	}

	public void setPower_Activated_00212_DarkVileplume(List<Integer> power_Activated_00212_DarkVileplume) {
		this.power_Activated_00212_DarkVileplume = power_Activated_00212_DarkVileplume;
	}

	public short isAllowedToPlayPokemonPower() {
		return allowedToPlayPokemonPower;
	}

	public void setAllowedToPlayPokemonPower(short allowedToPlayPokemonPower) {
		this.allowedToPlayPokemonPower = allowedToPlayPokemonPower;
	}

	public List<Integer> getLauchschlagUsed_00027_Porenta() {
		return lauchschlagUsed_00027_Porenta;
	}

	public void setLauchschlagUsed_00027_Porenta(List<Integer> lauchschlagUsed_00027_Porenta) {
		this.lauchschlagUsed_00027_Porenta = lauchschlagUsed_00027_Porenta;
	}

	public List<Pair<Integer, Integer>> getLieLowUsed_00287_BrocksDugtrio() {
		return lieLowUsed_00287_BrocksDugtrio;
	}

	public void setLieLowUsed_00287_BrocksDugtrio(List<Pair<Integer, Integer>> lieLowUsed_00287_BrocksDugtrio) {
		this.lieLowUsed_00287_BrocksDugtrio = lieLowUsed_00287_BrocksDugtrio;
	}

	public List<Pair<Integer, Integer>> getTunnelingUsed_00269_BrocksOnix() {
		return tunnelingUsed_00269_BrocksOnix;
	}

	public void setTunnelingUsed_00269_BrocksOnix(List<Pair<Integer, Integer>> tunnelingUsed_00269_BrocksOnix) {
		this.tunnelingUsed_00269_BrocksOnix = tunnelingUsed_00269_BrocksOnix;
	}
}
