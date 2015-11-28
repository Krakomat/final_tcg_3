package model.scripting.rocket;

import gui2d.animations.Animation;
import gui2d.animations.CardMoveAnimation;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00252_Slowpoke extends PokemonCardScript {

	public Script_00252_Slowpoke(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Afternoon Nap", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.PSYCHIC);
		this.addAttack("Headbutt", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Afternoon Nap"))
			this.AfternoonNap();
		else
			this.Headbutt();
	}

	private void AfternoonNap() {
		List<Card> deck = gameModel.getPosition(ownDeck()).getCards();
		boolean done = false;
		for (Card c : deck) {
			if (c.getCardId().equals("00101")) {
				List<Card> cardList = new ArrayList<Card>();
				cardList.add(this.card);
				cardList.add(c);
				this.gameModel.sendCardMessageToAllPlayers(this.getCardOwner().getName() + " attaches an Energy-Card to " + this.card.getName(), cardList, "");
				this.gameModel.getAttackAction().moveCard(ownDeck(), this.card.getCurrentPosition().getPositionID(), c.getGameID(), false);
				this.gameModel.setEnergyPlayed(true);

				// Execute animation:
				Animation animation = new CardMoveAnimation(ownDeck(), this.card.getCurrentPosition().getPositionID(), card.getCardId(), Sounds.EQUIP);
				gameModel.sendAnimationToAllPlayers(animation);

				this.gameModel.sendGameModelToAllPlayers("");
				done = true;
				break;
			}
		}
		
		if (!done)
			this.gameModel.sendTextMessageToAllPlayers("Deck does not contain Psychic energy!", "");
		
		// Shuffle deck:
		gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " shuffles his deck!", Sounds.SHUFFLE);
		gameModel.getAttackAction().shufflePosition(ownDeck());
		this.gameModel.sendGameModelToAllPlayers("");
	}

	private void Headbutt() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);
	}
}
