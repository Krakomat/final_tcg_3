package network.serialization;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.List;

import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;

import model.database.Card;
import model.database.Deck;
import model.database.DynamicPokemonCondition;
import model.database.EnergyCard;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.CardType;
import model.enums.Color;
import model.enums.DistributionMode;
import model.enums.Edition;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.enums.Rarity;
import model.game.GameModelUpdateImpl;
import model.game.PositionImpl;
import model.interfaces.GameModelUpdate;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;
import network.tcp.messages.ByteString;

/**
 * Class responsible for serializing the send entities for network messages.
 * 
 * @author Michael
 *
 */
public class TCGSerializer {

	public ByteString packInt(Integer i) {
		return new ByteString(i);
	}

	public Integer unpackInt(ByteString b) {
		return new Integer((int) b.asLong());
	}

	public ByteString packShort(short s) {
		return new ByteString(s);
	}

	public short unpackShort(ByteString b) {
		return (short) b.asLong();
	}

	public ByteString packBool(boolean b) {
		return new ByteString(b ? 1 : 0);
	}

	public boolean unpackBool(ByteString b) {
		if (b.asLong() > 1 || b.asLong() < 0)
			try {
				throw new IOException("Wrong byte string input in unpackBool");
			} catch (IOException e) {
				e.printStackTrace();
			}
		boolean bool = b.asLong() == 1 ? true : false;
		return bool;
	}

	public ByteString packString(String s) {
		return new ByteString(s);
	}

	public String unpackString(ByteString b) {
		try {
			return b.string();
		} catch (CharacterCodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public ByteString packPositionID(PositionID posID) {
		return this.packString(posID.toString());
	}

	public PositionID unpackPositionID(ByteString b) {
		return PositionID.valueOf(unpackString(b));
	}

	public ByteString packElement(Element element) {
		return this.packString(element.toString());
	}

	public Element unpackElement(ByteString b) {
		return Element.valueOf(unpackString(b));
	}

	public ByteString packColor(Color color) {
		return this.packString(color.toString());
	}

	public Color unpackColor(ByteString b) {
		return Color.valueOf(unpackString(b));
	}

	public ByteString packCondition(DynamicPokemonCondition condition) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MessagePacker packer = MessagePack.newDefaultPacker(out);

		// Condition duration:
		ByteString b = this.packInt(condition.getRemainingTurns());
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// Condition:
		b = this.packString(condition.getCondition().toString());
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		packer.close();
		return new ByteString(out.toByteArray());
	}

	public DynamicPokemonCondition unpackCondition(ByteString b) throws IOException {
		MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(b.asInputStream());

		DynamicPokemonCondition cond = new DynamicPokemonCondition();

		// Condition duration:
		ByteString bString = unpackByteString(unpacker);
		cond.setRemainingTurns(this.unpackInt(bString));

		// Condition:
		bString = unpackByteString(unpacker);
		cond.setCondition(PokemonCondition.valueOf(this.unpackString(bString)));

		unpacker.close();
		return cond;
	}

	public ByteString packDistibutionMode(DistributionMode mode) {
		return this.packString(mode.toString());
	}

	public DistributionMode unpackDistibutionMode(ByteString b) {
		return DistributionMode.valueOf(unpackString(b));
	}

	public ByteString packIntList(List<Integer> list) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MessagePacker packer = MessagePack.newDefaultPacker(out);

		packer.packArrayHeader(list.size());
		for (Integer i : list) {
			ByteString b = packInt(i);
			packer.packBinaryHeader(b.length());
			packer.writePayload(b.copyAsBytes());
		}
		packer.close();
		return new ByteString(out.toByteArray());
	}

	public List<Integer> unpackIntList(ByteString b) throws IOException {
		MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(b.asInputStream());

		ArrayList<Integer> ints = new ArrayList<>();
		int size = unpacker.unpackArrayHeader();
		for (int i = 0; i < size; i++) {
			ByteString bString = unpackByteString(unpacker);
			ints.add(unpackInt(bString));
		}
		unpacker.close();
		return ints;
	}

	public ByteString packStringList(List<String> list) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MessagePacker packer = MessagePack.newDefaultPacker(out);

		packer.packArrayHeader(list.size());
		for (String i : list) {
			ByteString b = packString(i);
			packer.packBinaryHeader(b.length());
			packer.writePayload(b.copyAsBytes());
		}
		packer.close();
		return new ByteString(out.toByteArray());
	}

	public List<String> unpackStringList(ByteString b) throws IOException {
		MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(b.asInputStream());

		ArrayList<String> strings = new ArrayList<>();
		int size = unpacker.unpackArrayHeader();
		for (int i = 0; i < size; i++) {
			ByteString bString = unpackByteString(unpacker);
			strings.add(unpackString(bString));
		}
		unpacker.close();
		return strings;
	}

	public ByteString packPositionIDList(List<PositionID> list) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MessagePacker packer = MessagePack.newDefaultPacker(out);

		packer.packArrayHeader(list.size());
		for (PositionID i : list) {
			ByteString b = packPositionID(i);
			packer.packBinaryHeader(b.length());
			packer.writePayload(b.copyAsBytes());
		}
		packer.close();
		return new ByteString(out.toByteArray());
	}

	public List<PositionID> unpackPositionIDList(ByteString b) throws IOException {
		MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(b.asInputStream());

		ArrayList<PositionID> posIDs = new ArrayList<>();
		int size = unpacker.unpackArrayHeader();
		for (int i = 0; i < size; i++) {
			ByteString bPos = unpackByteString(unpacker);
			posIDs.add(unpackPositionID(bPos));
		}
		unpacker.close();
		return posIDs;
	}

	public ByteString packConditionList(List<DynamicPokemonCondition> list) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MessagePacker packer = MessagePack.newDefaultPacker(out);

		packer.packArrayHeader(list.size());
		for (DynamicPokemonCondition i : list) {
			ByteString b = packCondition(i);
			packer.packBinaryHeader(b.length());
			packer.writePayload(b.copyAsBytes());
		}
		packer.close();
		return new ByteString(out.toByteArray());
	}

	public List<DynamicPokemonCondition> unpackConditionList(ByteString b) throws IOException {
		MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(b.asInputStream());

		List<DynamicPokemonCondition> posIDs = new ArrayList<>();
		int size = unpacker.unpackArrayHeader();
		for (int i = 0; i < size; i++) {
			ByteString bPos = unpackByteString(unpacker);
			posIDs.add(unpackCondition(bPos));
		}
		unpacker.close();
		return posIDs;
	}

	public ByteString packElementList(List<Element> list) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MessagePacker packer = MessagePack.newDefaultPacker(out);

		packer.packArrayHeader(list.size());
		for (Element i : list) {
			ByteString b = packElement(i);
			packer.packBinaryHeader(b.length());
			packer.writePayload(b.copyAsBytes());
		}
		packer.close();
		return new ByteString(out.toByteArray());
	}

	public List<Element> unpackElementList(ByteString b) throws IOException {
		MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(b.asInputStream());

		ArrayList<Element> posIDs = new ArrayList<>();
		int size = unpacker.unpackArrayHeader();
		for (int i = 0; i < size; i++) {
			ByteString bPos = unpackByteString(unpacker);
			posIDs.add(unpackElement(bPos));
		}
		unpacker.close();
		return posIDs;
	}

	/**
	 * Unpacks the top byte string from this unpacker.
	 * 
	 * @param unpacker
	 * @return
	 * @throws IOException
	 */
	private ByteString unpackByteString(MessageUnpacker unpacker) throws IOException {
		int intSize = unpacker.unpackBinaryHeader();
		byte[] buf = new byte[intSize];
		unpacker.readPayload(buf);
		ByteString bString = new ByteString(buf);
		return bString;
	}

	private ByteString packRarity(Rarity rarity) {
		return this.packString(rarity.toString());
	}

	private Rarity unpackRarity(ByteString b) {
		return Rarity.valueOf(unpackString(b));
	}

	private ByteString packEdition(Edition edition) {
		return this.packString(edition.toString());
	}

	private Edition unpackEdition(ByteString b) {
		return Edition.valueOf(unpackString(b));
	}

	private ByteString packCardType(CardType cardType) {
		return this.packString(cardType.toString());
	}

	private CardType unpackCardType(ByteString b) {
		return CardType.valueOf(unpackString(b));
	}

	public ByteString packCard(Card card) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MessagePacker packer = MessagePack.newDefaultPacker(out);

		if (card instanceof PokemonCard)
			packer.packByte((byte) 1); // PokemonCard type indicator
		else if (card instanceof TrainerCard)
			packer.packByte((byte) 2); // TrainerCard type indicator
		else if (card instanceof EnergyCard)
			packer.packByte((byte) 3); // EnergyCard type indicator
		else
			packer.packByte((byte) 4); // DummyCard type indicator

		// CardID:
		ByteString b = packString(card.getCardId());
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// ImagePath:
		b = packString(card.getImagePath());
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// Name:
		b = packString(card.getName());
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// CardType:
		if (card.getCardType() != null)
			b = packCardType(card.getCardType());
		else
			b = new ByteString("-"); // null
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// Edition:
		if (card.getEdition() != null)
			b = packEdition(card.getEdition());
		else
			b = new ByteString("-"); // null
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// Rarity:
		if (card.getRarity() != null)
			b = packRarity(card.getRarity());
		else
			b = new ByteString("-"); // null
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// GameID:
		b = packInt(card.getGameID());
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// isVisibleForPlayerBlue:
		b = packBool(card.isVisibleForPlayerBlue());
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// isVisibleForPlayerRed:
		b = packBool(card.isVisibleForPlayerRed());
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// PlayedInTurn:
		b = packInt(card.getPlayedInTurn());
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		if (card instanceof PokemonCard) {
			PokemonCard c = (PokemonCard) card;

			// element:
			b = packElement(c.getElement());
			packer.packBinaryHeader(b.length());
			packer.writePayload(b.copyAsBytes());

			// hitpoints:
			b = packInt(c.getHitpoints());
			packer.packBinaryHeader(b.length());
			packer.writePayload(b.copyAsBytes());

			// weakness:
			if (c.getWeakness() == null)
				b = new ByteString("-"); // null
			else
				b = packElement(c.getWeakness());
			packer.packBinaryHeader(b.length());
			packer.writePayload(b.copyAsBytes());

			// resistance:
			if (c.getResistance() == null)
				b = new ByteString("-"); // null
			else
				b = packElement(c.getResistance());
			packer.packBinaryHeader(b.length());
			packer.writePayload(b.copyAsBytes());

			// retreatCosts:
			b = packElementList(c.getRetreatCosts());
			packer.packBinaryHeader(b.length());
			packer.writePayload(b.copyAsBytes());

			// evolvesFrom:
			b = packString(c.getEvolvesFrom());
			packer.packBinaryHeader(b.length());
			packer.writePayload(b.copyAsBytes());

			// damage marks:
			b = packInt(c.getDamageMarks());
			packer.packBinaryHeader(b.length());
			packer.writePayload(b.copyAsBytes());

			// current weakness:
			if (c.getCurrentWeakness() == null)
				b = new ByteString("-"); // null
			else
				b = packElement(c.getCurrentWeakness());
			packer.packBinaryHeader(b.length());
			packer.writePayload(b.copyAsBytes());

			// current resistance:
			if (c.getCurrentResistance() == null)
				b = new ByteString("-"); // null
			else
				b = packElement(c.getCurrentResistance());
			packer.packBinaryHeader(b.length());
			packer.writePayload(b.copyAsBytes());

			// priceValueable:
			b = packBool(c.isPriceValueable());
			packer.packBinaryHeader(b.length());
			packer.writePayload(b.copyAsBytes());

			if (c.getCardScript() != null) {
				PokemonCardScript script = (PokemonCardScript) c.getCardScript();
				// Attack Names:
				b = packStringList(script.getAttackNames());
			} else
				b = new ByteString("-"); // null
			packer.packBinaryHeader(b.length());
			packer.writePayload(b.copyAsBytes());

			if (c.getCardScript() != null) {
				PokemonCardScript script = (PokemonCardScript) c.getCardScript();

				// Pokemon Power Names:
				b = packStringList(script.getPokemonPowerNames());
			} else
				b = new ByteString("-"); // null
			packer.packBinaryHeader(b.length());
			packer.writePayload(b.copyAsBytes());

			if (c.getConditions().isEmpty())
				b = new ByteString("-"); // null
			else {
				b = packConditionList(c.getConditions());
			}
			packer.packBinaryHeader(b.length());
			packer.writePayload(b.copyAsBytes());

		}
		if (card instanceof EnergyCard) {
			EnergyCard c = (EnergyCard) card;

			// isBasicEnergy:
			b = packBool(c.isBasisEnergy());
			packer.packBinaryHeader(b.length());
			packer.writePayload(b.copyAsBytes());

			// providedEnergy:
			b = packElementList(c.getProvidedEnergy());
			packer.packBinaryHeader(b.length());
			packer.writePayload(b.copyAsBytes());
		}
		if (card instanceof TrainerCard) {
			// nothing to do here
		}

		packer.close();
		return new ByteString(out.toByteArray());
	}

	public Card unpackCard(ByteString b) throws IOException {
		MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(b.asInputStream());

		Card card;
		int type = unpacker.unpackByte();
		switch (type) {
		case 1:
			card = new PokemonCard();
			break;
		case 2:
			card = new TrainerCard();
			break;
		case 3:
			card = new EnergyCard();
			break;
		case 4:
			card = new Card();
			break;
		default:
			throw new IOException("Wrong card type in unpackCard");
		}

		// CardID:
		ByteString bString = unpackByteString(unpacker);
		card.setCardId(unpackString(bString));

		// ImagePath:
		bString = unpackByteString(unpacker);
		card.setImagePath(unpackString(bString));

		// Name:
		bString = unpackByteString(unpacker);
		card.setName(unpackString(bString));

		// CardType:
		bString = unpackByteString(unpacker);
		if (bString.string().equals("-"))
			card.setCardType(null);
		else
			card.setCardType(unpackCardType(bString));

		// Edition:
		bString = unpackByteString(unpacker);
		if (bString.string().equals("-"))
			card.setEdition(null);
		else
			card.setEdition(unpackEdition(bString));

		// Rarity:
		bString = unpackByteString(unpacker);
		if (bString.string().equals("-"))
			card.setRarity(null);
		else
			card.setRarity(unpackRarity(bString));

		// GameID:
		bString = unpackByteString(unpacker);
		card.setGameID(unpackInt(bString));

		// isVisibleForPlayerBlue:
		bString = unpackByteString(unpacker);
		card.setVisibleForPlayerBlue(unpackBool(bString));

		// isVisibleForPlayerRed:
		bString = unpackByteString(unpacker);
		card.setVisibleForPlayerRed(unpackBool(bString));

		// PlayedInTurn:
		bString = unpackByteString(unpacker);
		card.setPlayedInTurn(unpackInt(bString));

		if (card instanceof PokemonCard) {
			PokemonCard c = (PokemonCard) card;

			// element:
			bString = unpackByteString(unpacker);
			c.setElement(unpackElement(bString));

			// hitpoints:
			bString = unpackByteString(unpacker);
			c.setHitpoints(unpackInt(bString));

			// weakness:
			bString = unpackByteString(unpacker);
			if (bString.string().equals("-"))
				c.setWeakness(null);
			else
				c.setWeakness(unpackElement(bString));

			// resistance:
			bString = unpackByteString(unpacker);
			if (bString.string().equals("-"))
				c.setResistance(null);
			else
				c.setResistance(unpackElement(bString));

			// retreatCosts:
			bString = unpackByteString(unpacker);
			c.setRetreatCosts(unpackElementList(bString));

			// evolvesFrom:
			bString = unpackByteString(unpacker);
			c.setEvolvesFrom(unpackString(bString));

			// damage marks:
			bString = unpackByteString(unpacker);
			c.setDamageMarks(unpackInt(bString));

			// current weakness:
			bString = unpackByteString(unpacker);
			if (bString.string().equals("-"))
				c.setCurrentWeakness(null);
			else
				c.setCurrentWeakness(unpackElement(bString));

			// current resistance:
			bString = unpackByteString(unpacker);
			if (bString.string().equals("-"))
				c.setCurrentResistance(null);
			else
				c.setCurrentResistance(unpackElement(bString));

			// price valueable:
			bString = unpackByteString(unpacker);
			c.setPriceValueable(unpackBool(bString));

			// Attack Names:
			bString = unpackByteString(unpacker);
			List<String> attackNames = null;
			try {
				attackNames = unpackStringList(bString);
			} catch (Exception e) {
				// e.printStackTrace();
			}
			if (attackNames == null)
				c.setAttackNames(new ArrayList<>());
			else
				c.setAttackNames(unpackStringList(bString));

			// Pokemon Power Names:
			bString = unpackByteString(unpacker);
			List<String> powerNames = null;
			try {
				powerNames = unpackStringList(bString);
			} catch (Exception e) {
				// e.printStackTrace();
			}
			if (powerNames == null)
				c.setPokemonPowerNames(new ArrayList<>());
			else
				c.setPokemonPowerNames(unpackStringList(bString));

			// Conditions:
			bString = unpackByteString(unpacker);
			List<DynamicPokemonCondition> conditions = null;
			try {
				conditions = unpackConditionList(bString);
			} catch (Exception e) {
				// e.printStackTrace();
			}
			if (conditions == null)
				c.setConditions(new ArrayList<>());
			else
				c.setConditions(conditions);

		}
		if (card instanceof EnergyCard) {
			EnergyCard c = (EnergyCard) card;

			// isBasicEnergy:
			bString = unpackByteString(unpacker);
			c.setBasisEnergy(unpackBool(bString));

			// provided energy:
			bString = unpackByteString(unpacker);
			c.setProvidedEnergy(unpackElementList(bString));
		}
		if (card instanceof TrainerCard) {
			// nothing to do here
		}

		unpacker.close();
		return card;
	}

	public ByteString packCardList(List<Card> list) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MessagePacker packer = MessagePack.newDefaultPacker(out);

		packer.packArrayHeader(list.size());
		for (Card c : list) {
			ByteString b = packCard(c);
			packer.packBinaryHeader(b.length());
			packer.writePayload(b.copyAsBytes());
		}
		packer.close();
		return new ByteString(out.toByteArray());
	}

	public List<Card> unpackCardList(ByteString b) throws IOException {
		MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(b.asInputStream());

		ArrayList<Card> ints = new ArrayList<>();
		int size = unpacker.unpackArrayHeader();
		for (int i = 0; i < size; i++) {
			ByteString bString = unpackByteString(unpacker);
			ints.add(unpackCard(bString));
		}
		unpacker.close();
		return ints;
	}

	public ByteString packPosition(Position pos) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MessagePacker packer = MessagePack.newDefaultPacker(out);

		// PosID:
		ByteString b = this.packPositionID(pos.getPositionID());
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// Color:
		b = this.packColor(pos.getColor());
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// isVisibleForPlayerBlue:
		b = packBool(pos.isVisibleForPlayer(Color.BLUE));
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// isVisibleForPlayerRed:
		b = packBool(pos.isVisibleForPlayer(Color.RED));
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// Card list:
		b = this.packCardList(pos.getCards());
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		packer.close();
		return new ByteString(out.toByteArray());
	}

	public Position unpackPosition(ByteString b) throws IOException {
		MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(b.asInputStream());
		Position pos = new PositionImpl();

		// PosID:
		ByteString bString = unpackByteString(unpacker);
		pos.setPositionID(unpackPositionID(bString));

		// Color:
		bString = unpackByteString(unpacker);
		pos.setColor(unpackColor(bString));

		// isVisibleForPlayerBlue:
		bString = unpackByteString(unpacker);
		pos.setVisible(unpackBool(bString), Color.BLUE);

		// isVisibleForPlayerRed:
		bString = unpackByteString(unpacker);
		pos.setVisible(unpackBool(bString), Color.RED);

		// Card list:
		bString = unpackByteString(unpacker);
		pos.setCards(unpackCardList(bString));

		unpacker.close();
		return pos;
	}

	public ByteString packPositionList(List<Position> list) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MessagePacker packer = MessagePack.newDefaultPacker(out);

		packer.packArrayHeader(list.size());
		for (Position c : list) {
			ByteString b = packPosition(c);
			packer.packBinaryHeader(b.length());
			packer.writePayload(b.copyAsBytes());
		}
		packer.close();
		return new ByteString(out.toByteArray());
	}

	public List<Position> unpackPositionList(ByteString b) throws IOException {
		MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(b.asInputStream());

		ArrayList<Position> ints = new ArrayList<>();
		int size = unpacker.unpackArrayHeader();
		for (int i = 0; i < size; i++) {
			ByteString bString = unpackByteString(unpacker);
			ints.add(unpackPosition(bString));
		}
		unpacker.close();
		return ints;
	}

	public ByteString packGameModelUpdate(GameModelUpdate update) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MessagePacker packer = MessagePack.newDefaultPacker(out);

		ByteString energyPlay = this.packBool(update.isEnergyPlayAllowed());
		packer.packBinaryHeader(energyPlay.length());
		packer.writePayload(energyPlay.copyAsBytes());

		ByteString retreatPlay = this.packBool(update.isRetreatAllowed());
		packer.packBinaryHeader(retreatPlay.length());
		packer.writePayload(retreatPlay.copyAsBytes());

		ByteString turnNumber = this.packShort(update.getTurnNumber());
		packer.packBinaryHeader(turnNumber.length());
		packer.writePayload(turnNumber.copyAsBytes());

		ByteString b = this.packPositionList(update.getPositionList());
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		packer.close();
		return new ByteString(out.toByteArray());
	}

	public GameModelUpdate unpackGameModelUpdate(ByteString b) throws IOException {
		MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(b.asInputStream());

		GameModelUpdate update = new GameModelUpdateImpl();

		ByteString energyPlay = unpackByteString(unpacker);
		update.setEnergyPlayAllowed(this.unpackBool(energyPlay));

		ByteString retreatPlay = unpackByteString(unpacker);
		update.setRetreatAllowed(this.unpackBool(retreatPlay));

		ByteString turnNumber = unpackByteString(unpacker);
		update.setTurnNumber(this.unpackShort(turnNumber));

		ByteString bString = unpackByteString(unpacker);
		update.setPositionList(this.unpackPositionList(bString));

		unpacker.close();
		return update;
	}

	public ByteString packDeck(Deck deck) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MessagePacker packer = MessagePack.newDefaultPacker(out);

		// Deck name:
		ByteString b = this.packString(deck.getName());
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		// Deck cards:
		b = this.packStringList(deck.getCards());
		packer.packBinaryHeader(b.length());
		packer.writePayload(b.copyAsBytes());

		packer.close();
		return new ByteString(out.toByteArray());
	}

	public Deck unpackDeck(ByteString b) throws IOException {
		MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(b.asInputStream());

		Deck deck = new Deck();

		ByteString bString = unpackByteString(unpacker);
		deck.setName(this.unpackString(bString));

		bString = unpackByteString(unpacker);
		deck.setCards(this.unpackStringList(bString));

		unpacker.close();
		return deck;
	}
}
