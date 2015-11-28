package model.scripting.rocket;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00247_Meowth extends PokemonCardScript {

	public Script_00247_Meowth(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Coin Hurl", att1Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		Player player = this.getCardOwner();
		Player enemy = this.getEnemyPlayer();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		if (gameModel.getFullBenchPositions(enemy.getColor()).size() > 0) {
			PositionID defender = player.playerChoosesPositions(gameModel.getFullArenaPositions(enemy.getColor()), 1, true,
					"Choose a pokemon that receives the damage!").get(0);
			Card defendingPokemon = gameModel.getPosition(defender).getTopCard();

			gameModel.sendTextMessageToAllPlayers("If heads then " + defendingPokemon.getName() + " receives 20 damage!", "");
			Coin c = gameModel.getAttackAction().flipACoin();
			if (c == Coin.HEADS) {
				this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, false);
				gameModel.sendGameModelToAllPlayers("");
			}
		}
	}
}
