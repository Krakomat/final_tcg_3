package model.scripting.ltSurge;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00345_LtSurgesEevee extends PokemonCardScript {

	public Script_00345_LtSurgesEevee(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Suprise", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Scratch", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Suprise"))
			this.Suprise();
		else
			this.Scratch();
	}

	private void Suprise() {
		Position hand = gameModel.getPosition(enemyHand());
		int handSize = hand.size();
		if (handSize > 0) {
			Random r = new SecureRandom();
			int index = r.nextInt(handSize);
			Card c = hand.getCardAtIndex(index);
			gameModel.sendCardMessageToAllPlayers(getEnemyPlayer().getName() + " shuffles " + c.getName() + " into his deck!", c, "");

			// Put onto deck:
			gameModel.getAttackAction().moveCard(enemyHand(), enemyDeck(), c.getGameID(), true);
			// Shuffle deck:
			gameModel.getAttackAction().shufflePosition(enemyDeck());
			this.gameModel.sendGameModelToAllPlayers(Sounds.SHUFFLE);
		} else {
			gameModel.sendTextMessageToAllPlayers(getEnemyPlayer().getName() + " has no hand cards!", "");
		}
	}

	private void Scratch() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
	}
}
