package model.scripting.koga;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00392_KogasPidgey extends PokemonCardScript {

	public Script_00392_KogasPidgey(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Messenger", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Wing Attack", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Messenger"))
			this.Messenger();
		else
			this.WingAttack();
	}

	private void Messenger() {
		Player player = this.getCardOwner();

		gameModel.sendTextMessageToAllPlayers(player.getName() + " shuffles " + this.card.getName() + " into his deck!", "");

		gameModel.getAttackAction().moveCard(ownActive(), ownDeck(), this.card.getGameID(), true);

		Position position = gameModel.getPosition(ownActive());
		List<Card> cards = position.getCards();
		List<Card> copyList = new ArrayList<>();
		for (Card c : cards) {
			copyList.add(c);
		}
		for (Card c : copyList) {
			gameModel.getAttackAction().moveCard(ownActive(), ownDeck(), c.getGameID(), true);
		}

		if (getSpecialPokemonCardsFromDeck().size() > 0) {
			Card c = player.playerChoosesCards(getSpecialPokemonCardsFromDeck(), 1, true, "Choose a card to put into your hand!").get(0);
			gameModel.sendCardMessageToAllPlayers(player.getName() + " takes " + c.getName() + " into his hand!", c, "");
			gameModel.getAttackAction().moveCard(ownDeck(), ownHand(), c.getGameID(), true);
		}
		gameModel.getAttackAction().shufflePosition(ownDeck());
		gameModel.sendGameModelToAllPlayers(Sounds.SHUFFLE);

		if (gameModel.getFullArenaPositions(player.getColor()).isEmpty()) {
			gameModel.sendTextMessageToAllPlayers(player.getName() + " has no active pokemon anymore!", "");
			gameModel.playerLoses(player);
		} else {
			// Choose a new active pokemon in this case:
			PositionID newActive = player.playerChoosesPositions(gameModel.getFullArenaPositions(player.getColor()), 1, true, "Choose a new active pokemon!").get(0);
			Card newActivePokmn = gameModel.getPosition(newActive).getTopCard();
			gameModel.sendCardMessageToAllPlayers(player.getName() + " chooses " + newActivePokmn.getName() + " as his new active pokemon!", newActivePokmn, "");
			gameModel.getAttackAction().movePokemonToPosition(newActive, ownActive());
			gameModel.sendGameModelToAllPlayers("");
		}
	}

	private List<Card> getSpecialPokemonCardsFromDeck() {
		List<Card> cardList = new ArrayList<>();
		for (Card c : gameModel.getPosition(ownDeck()).getPokemonCards()) {
			PokemonCard pCard = (PokemonCard) c;
			if (!pCard.getName().equals("Koga's Pidgey"))
				cardList.add(c);
		}
		return cardList;
	}

	private void WingAttack() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
	}
}
