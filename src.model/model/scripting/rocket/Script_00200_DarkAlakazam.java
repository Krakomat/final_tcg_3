package model.scripting.rocket;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00200_DarkAlakazam extends PokemonCardScript {

	public Script_00200_DarkAlakazam(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		att1Cost.add(Element.PSYCHIC);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Teleport Blast", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.PSYCHIC);
		att2Cost.add(Element.PSYCHIC);
		att2Cost.add(Element.PSYCHIC);
		this.addAttack("Mind Shock", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Teleport Blast"))
			this.TeleportBlast();
		else
			this.MindShock();
	}

	private void TeleportBlast() {
		Player player = this.getCardOwner();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);

		if (gameModel.getFullBenchPositions(player.getColor()).size() > 0) {
			boolean switchPkmn = player.playerDecidesYesOrNo("Do you want to switch " + this.card.getName() + " with one of your bench pokemon?");
			if (switchPkmn) {
				PositionID posID = player.playerChoosesPositions(gameModel.getFullBenchPositions(player.getColor()), 1, true, "Choose a new active Pokemon!").get(0);
				List<Card> cardList = new ArrayList<>();
				cardList.add(this.card);
				cardList.add(gameModel.getPosition(posID).getTopCard());
				gameModel.sendCardMessageToAllPlayers(player.getName() + " swaps " + this.card.getName() + " with "
						+ gameModel.getPosition(posID).getTopCard().getName() + "!", cardList, "");
				gameModel.getAttackAction().swapPokemon(ownActive(), posID);
				gameModel.sendGameModelToAllPlayers("");
			}
		}
	}

	private void MindShock() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, false);
	}
}
