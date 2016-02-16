package model.scripting.sabrina;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00419_SabrinasHaunter extends PokemonCardScript {

	public Script_00419_SabrinasHaunter(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		att1Cost.add(Element.PSYCHIC);
		this.addAttack("Nightmare", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.PSYCHIC);
		att2Cost.add(Element.PSYCHIC);
		att2Cost.add(Element.PSYCHIC);
		this.addAttack("Shadow Attack", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Nightmare"))
			this.Nightmare();
		else
			this.ShadowAttack();
	}

	private void Nightmare() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is asleep!", "");
		gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.ASLEEP);
		gameModel.sendGameModelToAllPlayers("");
	}

	private void ShadowAttack() {
		Player player = this.getCardOwner();
		Player enemy = this.getEnemyPlayer();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		Element attackerElement = ((PokemonCard) this.card).getElement();

		if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
			if (gameModel.getFullBenchPositions(enemy.getColor()).size() > 0) {
				PositionID benchDefender = player.playerChoosesPositions(gameModel.getFullBenchPositions(enemy.getColor()), 1, true, "Choose a pokemon that receives the damage!")
						.get(0);
				this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, benchDefender, 30, false);
			} else
				gameModel.sendTextMessageToAllPlayers(getEnemyPlayer().getName() + " has no bench Pokemon!", "");
		}
	}
}
