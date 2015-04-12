package model.scripting.jungle;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00109_Kangaskhan extends PokemonCardScript {

	public Script_00109_Kangaskhan(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Fetch", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Comet Punch", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Fetch"))
			this.fetch();
		else
			this.cometPunch();
	}

	private void fetch() {
		gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " draws a card!", "");
		this.gameModel.getAttackAction().playerDrawsCards(1, getCardOwner());
	}

	private void cometPunch() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " flips 4 coins...", "");
		int numberHeads = gameModel.getAttackAction().flipCoinsCountHeads(4);
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, numberHeads * 20, true);
	}
}
