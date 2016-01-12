package model.game;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;

import com.google.common.base.Preconditions;

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
	private Map<String, List<Integer>> activatedEffectMap;
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
		this.activatedEffectMap = new HashMap<>();
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

		this.activatedEffectMap = new HashMap<>();

		this.attackUsed = new ArrayList<>();
		for (String s : gameModelUpdate.getGameModelParameters().getActivatedEffectMap().keySet()) {
			List<Integer> list = new ArrayList<>();
			for (Integer i : gameModelUpdate.getGameModelParameters().getActivatedEffectMap().get(s))
				list.add(i);
			this.activatedEffectMap.put(s, list);
		}
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

		for (String s : this.getActivatedEffectMap().keySet()) {
			List<Integer> list = new ArrayList<>();
			for (Integer i : this.getActivatedEffectMap().get(s))
				list.add(i);
			copy.getActivatedEffectMap().put(s, list);
		}

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

		// activatedEffectMap
		bString = serializer.unpackByteString(unpacker);
		this.activatedEffectMap = serializer.unpackMap(bString);

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

		// activatedEffectMap
		b = serializer.packMap(activatedEffectMap);
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

	public short isAllowedToPlayTrainerCards() {
		return allowedToPlayTrainerCards;
	}

	public void setAllowedToPlayTrainerCards(short allowedToPlayTrainerCards) {
		this.allowedToPlayTrainerCards = allowedToPlayTrainerCards;
	}

	public short isAllowedToPlayPokemonPower() {
		return allowedToPlayPokemonPower;
	}

	public void setAllowedToPlayPokemonPower(short allowedToPlayPokemonPower) {
		this.allowedToPlayPokemonPower = allowedToPlayPokemonPower;
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

	public Map<String, List<Integer>> getActivatedEffectMap() {
		return activatedEffectMap;
	}

	/**
	 * Returns true, if the effect of the given card id is active.
	 * 
	 * @param cardID
	 * @return
	 */
	public boolean activeEffect(String cardID) {
		return this.activatedEffectMap.containsKey(cardID);
	}

	/**
	 * Returns true, if the effect of the given card id is active.
	 * 
	 * @param cardID
	 * @param gameID
	 * @return
	 */
	public boolean activeEffect(String cardID, Integer gameID) {
		if (!this.activatedEffectMap.containsKey(cardID))
			return false;
		if (!this.activatedEffectMap.get(cardID).contains(gameID))
			return false;
		return true;
	}

	/**
	 * Returns a list with all the gameIDs that are currently associated with the given effect or an empty list if no gameID matches this statement.
	 * 
	 * @param cardID
	 * @return
	 */
	public List<Integer> getActiveEffectGameIDs(String cardID) {
		return this.activatedEffectMap.get(cardID) != null ? this.activatedEffectMap.get(cardID) : new ArrayList<>();
	}

	/**
	 * Activates the given effect, by putting the given pair into the activatedEffects map.
	 * 
	 * @param cardID
	 * @param gameID
	 */
	public void activateEffect(String cardID, Integer gameID) {
		if (this.activatedEffectMap.containsKey(cardID)) {
			Preconditions.checkArgument(!this.activatedEffectMap.get(cardID).contains(gameID),
					"Error: GameID is already contained in activatedEffectsMap: (" + cardID + ", " + gameID + ")");
			this.activatedEffectMap.get(cardID).add(gameID);
		} else {
			List<Integer> list = new ArrayList<>();
			list.add(gameID);
			this.activatedEffectMap.put(cardID, list);
		}
	}

	/**
	 * Deactivates the given effect, by removing the given pair from the activatedEffects map.
	 * 
	 * @param cardID
	 * @param gameID
	 */
	public void deactivateEffect(String cardID, Integer gameID) {
		Preconditions.checkArgument(this.activatedEffectMap.containsKey(cardID), "Error: Key not found: " + cardID);
		Preconditions.checkArgument(this.activatedEffectMap.get(cardID).contains(gameID),
				"Error: GameID is not contained in activatedEffectsMap: (" + cardID + ", " + gameID + ")");

		if (this.activatedEffectMap.get(cardID).size() == 1)
			this.activatedEffectMap.remove(cardID);
		else
			this.activatedEffectMap.get(cardID).remove(gameID);
	}
}
