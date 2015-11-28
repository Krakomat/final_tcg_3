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

public class Script_00219_DarkElectrode extends PokemonCardScript {

	public Script_00219_DarkElectrode(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Rolling Tackle", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.LIGHTNING);
		att2Cost.add(Element.LIGHTNING);
		this.addAttack("Energy Bomb", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Rolling Tackle"))
			this.RollingTackle();
		else
			this.EnergyBomb();
	}

	private void RollingTackle() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);
	}

	private void EnergyBomb() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);

		if (gameModel.getFullBenchPositions(getCardOwner().getColor()).isEmpty()) {
			// Discard all energy cards:
			gameModel.getAttackAction().removeAllEnergyFromPosition(attacker);
			gameModel.sendGameModelToAllPlayers("");
		} else {
			List<Card> energyCards = this.card.getCurrentPosition().getEnergyCards();
			for (Card energyCard : energyCards) {
				PositionID benchPos = getCardOwner().playerChoosesPositions(gameModel.getFullBenchPositions(getCardOwner().getColor()), 1, true,
						"Choose a Pokemon that should get " + energyCard.getName() + " from " + this.card.getName()).get(0);
				gameModel.getAttackAction().moveCard(this.card.getCurrentPosition().getPositionID(), benchPos, energyCard.getGameID(), false);

				// Execute animation:
				Animation animation = new CardMoveAnimation(this.card.getCurrentPosition().getPositionID(), benchPos, energyCard.getCardId(), Sounds.EQUIP);
				gameModel.sendAnimationToAllPlayers(animation);
				gameModel.sendGameModelToAllPlayers("");
			}
		}
	}
}
