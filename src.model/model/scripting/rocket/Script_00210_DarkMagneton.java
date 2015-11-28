package model.scripting.rocket;

import gui2d.animations.Animation;
import gui2d.animations.CardMoveAnimation;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00210_DarkMagneton extends PokemonCardScript {

	public Script_00210_DarkMagneton(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Sonicboom", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.LIGHTNING);
		att2Cost.add(Element.LIGHTNING);
		this.addAttack("Magnetic Lines", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Sonicboom"))
			this.Sonicboom();
		else
			this.MagneticLines();
	}

	private void Sonicboom() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, false);
	}

	private void MagneticLines() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);

		if (gameModel.getPosition(enemyActive()).getBasicEnergyCards().size() > 0 && gameModel.getFullBenchPositions(this.getEnemyPlayer().getColor()).size() > 0) {
			Player player = this.getCardOwner();
			Card c = player.playerChoosesCards(gameModel.getPosition(enemyActive()).getBasicEnergyCards(), 1, true,
					"Choose a energy card to move from the defending pokemon!").get(0);

			PositionID posID = player.playerChoosesPositions(gameModel.getFullBenchPositions(this.getEnemyPlayer().getColor()), 1, true,
					"Choose a pokemon to attach " + c.getName() + " to!").get(0);

			gameModel.sendTextMessageToAllPlayers(player.getName() + " moves an energy card from the defending pokemon!", "");
			gameModel.getAttackAction().moveCard(defender, posID, c.getGameID(), false);

			// Execute animation:
			Animation animation = new CardMoveAnimation(defender, posID, c.getCardId(), Sounds.EQUIP);
			gameModel.sendAnimationToAllPlayers(animation);
			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
