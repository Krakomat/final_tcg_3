package model.scripting.giovanni;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00483_GiovannisNidoqueen extends PokemonCardScript {

	public Script_00483_GiovannisNidoqueen(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		att1Cost.add(Element.GRASS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Mega Kick", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.GRASS);
		att2Cost.add(Element.GRASS);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Love Lariat", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Mega Kick"))
			this.MegaKick();
		else
			this.LoveLariat();
	}

	private void MegaKick() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 40, true);
	}

	private void LoveLariat() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			if (getGiovannisNidokingPokemonInArena().size() > 0) {
				this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 100, true);
			} else
				this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 50, true);
		} else
			gameModel.sendTextMessageToAllPlayers("Horn Thrust does nothing!", "");
	}

	private List<PositionID> getGiovannisNidokingPokemonInArena() {
		List<PositionID> erg = new ArrayList<>();
		for (PositionID posID : gameModel.getFullArenaPositions(getCardOwner().getColor()))
			if (gameModel.getPosition(posID).getTopCard().getCardId().equals("00480"))
				erg.add(posID);
		return erg;
	}
}
