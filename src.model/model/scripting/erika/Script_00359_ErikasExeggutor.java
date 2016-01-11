package model.scripting.erika;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00359_ErikasExeggutor extends PokemonCardScript {

	public Script_00359_ErikasExeggutor(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		this.addAttack("Psychic Exchange", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.GRASS);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Stomp", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Psychic Exchange"))
			this.PsychicExchange();
		else
			this.Stomp();
	}

	private void PsychicExchange() {
		gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " shuffles his hand into his deck!", "");
		List<Card> list = gameModel.getPosition(ownHand()).getCards();
		List<Card> cardList = new ArrayList<>();
		for (Card c : list)
			cardList.add(c);
		for (Card c : cardList)
			gameModel.getAttackAction().moveCard(ownHand(), ownDeck(), c.getGameID(), true);
		gameModel.getAttackAction().shufflePosition(enemyDeck());
		gameModel.sendGameModelToAllPlayers(Sounds.SHUFFLE);

		// Draw 5 cards:
		gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " draws 5 cards!", "");
		gameModel.getAttackAction().playerDrawsCards(5, getCardOwner());
	}

	private void Stomp() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		gameModel.sendTextMessageToAllPlayers("If heads then this attack does 10 more damage!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 40, true);
		} else
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);
	}
}
