package model.game;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;

import common.utilities.Pair;
import common.utilities.Triple;
import network.serialization.TCGSerializer;
import network.tcp.messages.ByteString;
import model.enums.GameState;
import model.interfaces.GameModelUpdate;

public class GameModelParameters {
	private int turnNumber;
	private GameState gameState;
	private boolean energyPlayed, retreatExecuted;
	private boolean noEnergyPayment, activated_00296_Misty, vermillionCityGymAttackModifier;
	private short allowedToPlayTrainerCards, allowedToPlayPokemonPower;
	private List<Integer> lauchschlagUsed_00027_Porenta, power_Active_00164_Muk, power_Active_00153_Aerodactyl, power_Activated_00117_Venomoth, power_Activated_00119_Vileplume,
			power_Activated_00143_Mankey, power_Activated_00155_Dragonite, power_Activated_00156_Gengar, power_Activated_00188_Omanite, power_Activated_00251_Rattata,
			power_Activated_00239_Drowzee, power_Activated_00235_Charmander, power_Activated_00224_DarkKadabra, power_Activated_00221_DarkGloom,
			power_Activated_00218_DarkDragonair, power_Activated_00212_DarkVileplume, power_Activated_00375_ErikasBellsprout, power_Activated_00362_ErikasOddish,
			power_Activated_00356_ErikasVictreebel;
	private List<Pair<Integer, Integer>> attackUsed;
	private List<Triple<Integer, String, Integer>> blockedAttacks;

	public GameModelParameters() {
		gameState = GameState.PREGAME;
		turnNumber = 0;
		energyPlayed = false;
		retreatExecuted = false;
		vermillionCityGymAttackModifier = false;
		this.noEnergyPayment = false;
		this.activated_00296_Misty = false;
		this.blockedAttacks = new ArrayList<>();
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
		this.power_Activated_00375_ErikasBellsprout = new ArrayList<>();
		this.power_Activated_00362_ErikasOddish = new ArrayList<>();
		this.power_Activated_00356_ErikasVictreebel = new ArrayList<>();
		this.attackUsed = new ArrayList<>();
		this.allowedToPlayTrainerCards = 0;
		this.allowedToPlayPokemonPower = 0;
	}

	public GameModelParameters(GameModelUpdate gameModelUpdate) {
		this.setTurnNumber(gameModelUpdate.getGameModelParameters().getTurnNumber());
		this.setGameState(gameModelUpdate.getGameModelParameters().getGameState());
		this.setEnergyPlayed(gameModelUpdate.getGameModelParameters().isEnergyPlayed());
		this.setRetreatExecuted(gameModelUpdate.getGameModelParameters().isRetreatExecuted());
		this.setVermillionCityGymAttackModifier(gameModelUpdate.getGameModelParameters().isVermillionCityGymAttackModifier());
		this.setNoEnergyPayment(gameModelUpdate.getGameModelParameters().isNoEnergyPayment());
		this.setActivated_00296_Misty(gameModelUpdate.getGameModelParameters().isActivated_00296_Misty());
		this.blockedAttacks = new ArrayList<>();
		for (Triple<Integer, String, Integer> i : gameModelUpdate.getGameModelParameters().getBlockedAttacks())
			this.blockedAttacks.add(i);

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
		this.power_Activated_00375_ErikasBellsprout = new ArrayList<>();
		this.power_Activated_00362_ErikasOddish = new ArrayList<>();
		this.power_Activated_00356_ErikasVictreebel = new ArrayList<>();
		this.attackUsed = new ArrayList<>();
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
		for (Integer i : gameModelUpdate.getGameModelParameters().getPower_Activated_00375_ErikasBellsprout())
			this.power_Activated_00375_ErikasBellsprout.add(i);
		for (Integer i : gameModelUpdate.getGameModelParameters().getPower_Activated_00362_ErikasOddish())
			this.power_Activated_00362_ErikasOddish.add(i);
		for (Integer i : gameModelUpdate.getGameModelParameters().getPower_Activated_00356_ErikasVictreebel())
			this.power_Activated_00356_ErikasVictreebel.add(i);
		for (Pair<Integer, Integer> i : gameModelUpdate.getGameModelParameters().getAttackUsed())
			this.attackUsed.add(i);

		this.setAllowedToPlayTrainerCards(gameModelUpdate.getGameModelParameters().isAllowedToPlayTrainerCards());
		this.setAllowedToPlayPokemonPower(gameModelUpdate.getGameModelParameters().isAllowedToPlayPokemonPower());
	}

	public GameModelParameters copy() {
		GameModelParameters copy = new GameModelParameters();
		copy.setTurnNumber(turnNumber);
		copy.setEnergyPlayed(energyPlayed);
		copy.setGameState(gameState);
		copy.setVermillionCityGymAttackModifier(vermillionCityGymAttackModifier);
		copy.setNoEnergyPayment(noEnergyPayment);
		copy.setActivated_00296_Misty(activated_00296_Misty);
		copy.setRetreatExecuted(retreatExecuted);
		for (Triple<Integer, String, Integer> i : this.getBlockedAttacks())
			copy.getBlockedAttacks().add(i);

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
		for (Integer i : this.getPower_Activated_00375_ErikasBellsprout())
			copy.getPower_Activated_00375_ErikasBellsprout().add(i);
		for (Integer i : this.getPower_Activated_00362_ErikasOddish())
			copy.getPower_Activated_00362_ErikasOddish().add(i);
		for (Integer i : this.getPower_Activated_00356_ErikasVictreebel())
			copy.getPower_Activated_00356_ErikasVictreebel().add(i);
		for (Pair<Integer, Integer> i : this.getAttackUsed())
			copy.getAttackUsed().add(i);
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

		// vermillionCityGymAttackModifier:
		bString = serializer.unpackByteString(unpacker);
		this.vermillionCityGymAttackModifier = serializer.unpackBool(bString);

		// noEnergyPayment:
		bString = serializer.unpackByteString(unpacker);
		this.noEnergyPayment = serializer.unpackBool(bString);

		// activated_00296_Misty:
		bString = serializer.unpackByteString(unpacker);
		this.activated_00296_Misty = serializer.unpackBool(bString);

		// blockedAttacks
		bString = serializer.unpackByteString(unpacker);
		this.blockedAttacks = serializer.unpackBlockedAttacksList(bString);

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

		// power_Activated_00375_ErikasBellsprout:
		bString = serializer.unpackByteString(unpacker);
		this.power_Activated_00375_ErikasBellsprout = serializer.unpackIntList(bString);

		// power_Activated_00362_ErikasOddish:
		bString = serializer.unpackByteString(unpacker);
		this.power_Activated_00362_ErikasOddish = serializer.unpackIntList(bString);

		// power_Activated_00356_ErikasVictreebel:
		bString = serializer.unpackByteString(unpacker);
		this.power_Activated_00356_ErikasVictreebel = serializer.unpackIntList(bString);

		// attackUsed:
		bString = serializer.unpackByteString(unpacker);
		this.attackUsed = serializer.unpackIntegerPairList(bString);

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

		// activated_00296_Misty:
		ByteString mistyActive = serializer.packBool(activated_00296_Misty);
		packer.packBinaryHeader(mistyActive.length());
		packer.writePayload(mistyActive.copyAsBytes());

		// retreatExecuted:
		ByteString retreat = serializer.packBool(retreatExecuted);
		packer.packBinaryHeader(retreat.length());
		packer.writePayload(retreat.copyAsBytes());

		// vermillionCityGymAttackModifier:
		ByteString b = serializer.packBool(vermillionCityGymAttackModifier);
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// noEnergyPayment:
		b = serializer.packBool(noEnergyPayment);
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// blockedAttacks
		b = serializer.packBlockedAttacksList(blockedAttacks);
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// lauchschlagUsed_00027_Porenta
		b = serializer.packIntList(lauchschlagUsed_00027_Porenta);
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

		// power_Activated_00375_ErikasBellsprout:
		b = serializer.packIntList(power_Activated_00375_ErikasBellsprout);
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// power_Activated_00362_ErikasOddish:
		b = serializer.packIntList(power_Activated_00362_ErikasOddish);
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// power_Activated_00356_ErikasVictreebel:
		b = serializer.packIntList(power_Activated_00356_ErikasVictreebel);
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// attackUsed:
		b = serializer.packIntPairList(attackUsed);
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// allowedToPlayTrainerCards:
		b = serializer.packShort(allowedToPlayTrainerCards);
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// allowedToPlayPokemonPower:
		b = serializer.packShort(allowedToPlayPokemonPower);
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

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

	public List<Pair<Integer, Integer>> getAttackUsed() {
		return attackUsed;
	}

	public void setAttackUsed(List<Pair<Integer, Integer>> attackUsed) {
		this.attackUsed = attackUsed;
	}

	public List<Triple<Integer, String, Integer>> getBlockedAttacks() {
		return blockedAttacks;
	}

	public void setBlockedAttacks(List<Triple<Integer, String, Integer>> blockedAttacks) {
		this.blockedAttacks = blockedAttacks;
	}

	public void updateBlockedAttacks() {
		List<Triple<Integer, String, Integer>> blockedAttacksNew = new ArrayList<>();
		for (Triple<Integer, String, Integer> attack : this.blockedAttacks) {
			if (attack.getAction() != 1) {
				blockedAttacksNew.add(new Triple<Integer, String, Integer>(attack.getKey(), attack.getValue(), attack.getAction() - 1));
			}
		}
		this.blockedAttacks = blockedAttacksNew;
	}

	public boolean attackIsBlocked(String attackName, int gameID) {
		for (Triple<Integer, String, Integer> attack : this.blockedAttacks) {
			if (attack.getValue().equals(attackName) && attack.getKey() == gameID) {
				return true;
			}
		}
		return false;
	}

	public boolean isActivated_00296_Misty() {
		return activated_00296_Misty;
	}

	public void setActivated_00296_Misty(boolean activated_00296_Misty) {
		this.activated_00296_Misty = activated_00296_Misty;
	}

	public boolean isVermillionCityGymAttackModifier() {
		return vermillionCityGymAttackModifier;
	}

	public void setVermillionCityGymAttackModifier(boolean vermillionCityGymAttackModifier) {
		this.vermillionCityGymAttackModifier = vermillionCityGymAttackModifier;
	}

	public List<Integer> getPower_Activated_00375_ErikasBellsprout() {
		return power_Activated_00375_ErikasBellsprout;
	}

	public void setPower_Activated_00375_ErikasBellsprout(List<Integer> power_Activated_00375_ErikasBellsprout) {
		this.power_Activated_00375_ErikasBellsprout = power_Activated_00375_ErikasBellsprout;
	}

	public List<Integer> getPower_Activated_00362_ErikasOddish() {
		return power_Activated_00362_ErikasOddish;
	}

	public void setPower_Activated_00362_ErikasOddish(List<Integer> powerActivated_00362_ErikasOddish) {
		this.power_Activated_00362_ErikasOddish = powerActivated_00362_ErikasOddish;
	}

	public List<Integer> getPower_Activated_00356_ErikasVictreebel() {
		return power_Activated_00356_ErikasVictreebel;
	}

	public void setPower_Activated_00356_ErikasVictreebel(List<Integer> power_Activated_00356_ErikasVictreebel) {
		this.power_Activated_00356_ErikasVictreebel = power_Activated_00356_ErikasVictreebel;
	}
}
