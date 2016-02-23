package model.scripting.giovanni;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00484_GiovannisPinsir extends PokemonCardScript {

	public Script_00484_GiovannisPinsir(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Snapping Pincers", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.GRASS);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Overhead Toss", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Snapping Pincers"))
			this.SnappingPincers();
		else
			this.OverheadToss();
	}

	private void SnappingPincers() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		gameModel.sendTextMessageToAllPlayers("If heads then this attack does 10 more damage!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
		} else
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);
	}

	private void OverheadToss() {
		Player player = this.getCardOwner();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 40, true);

		if (gameModel.getFullBenchPositions(player.getColor()).size() > 0) {
			if (gameModel.getAttackAction().flipACoin() == Coin.TAILS) {
				PositionID benchDefender = player.playerChoosesPositions(gameModel.getFullBenchPositions(player.getColor()), 1, true, "Choose a pokemon that receives the damage!")
						.get(0);
				this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, benchDefender, 20, false);
			}
		} else
			gameModel.sendTextMessageToAllPlayers(player.getName() + " has no bench Pokemon!", "");
	}
}
