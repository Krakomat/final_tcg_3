package model.scripting.fossil;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00189_Psyduck extends PokemonCardScript {

	public Script_00189_Psyduck(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		this.addAttack("Headache", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.WATER);
		this.addAttack("Fury Swipes", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Headache"))
			this.headache();
		else
			this.furySwipes();
	}

	private void headache() {
		Player enemy = this.getEnemyPlayer();

		gameModel.getGameModelParameters().setAllowedToPlayTrainerCards((short) 2);
		gameModel.sendTextMessageToAllPlayers(enemy.getName() + " can't play Trainer cards on his next turn!", "");
		gameModel.sendGameModelToAllPlayers("");
	}

	private void furySwipes() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " flips 3 coins...", "");
		int numberHeads = gameModel.getAttackAction().flipCoinsCountHeads(3);
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, numberHeads * 10, true);
	}
}
