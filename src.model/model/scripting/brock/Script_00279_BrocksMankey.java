package model.scripting.brock;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00279_BrocksMankey extends PokemonCardScript {

	public Script_00279_BrocksMankey(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Fidget", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Karate Chop", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Fidget"))
			this.Fidget();
		else
			this.KarateChop();
	}

	private void Fidget() {
		// Shuffle deck:
		gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " shuffles his deck!", Sounds.SHUFFLE);
		gameModel.getAttackAction().shufflePosition(ownDeck());
		this.gameModel.sendGameModelToAllPlayers("");
	}

	private void KarateChop() {
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PokemonCard attackingPokemon = (PokemonCard) gameModel.getPosition(attacker).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();

		int damageOnAttacker = attackingPokemon.getDamageMarks();
		int damageAmount = 40 - damageOnAttacker;
		if (damageAmount < 0)
			damageAmount = 0;
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, damageAmount, true);
	}
}
