package model.scripting.giovanni;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;
import network.client.Player;

public class Script_00481_GiovannisPersian extends PokemonCardScript {

	public Script_00481_GiovannisPersian(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Ambush", att1Cost);

		this.addPokemonPower("Call the Boss");
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		// Cannot be manually activated!
		return false;
	}

	@Override
	public void playFromHand() {
		super.playFromHand();

		if (this.gameModel.getGameModelParameters().isAllowedToPlayPokemonPower() == 0 && !this.gameModel.getGameModelParameters().activeEffect("00164")) {
			Player player = this.getCardOwner();
			boolean usePP = player.playerDecidesYesOrNo("Do you want to use Call the Boss?");
			if (usePP) {
				gameModel.sendCardMessageToAllPlayers(this.card.getName() + " activates Call the Boss!", card, "");
				Card giovanni = null;
				for (Card c : gameModel.getPosition(ownDeck()).getCards())
					if (c.getCardId().equals("00482"))
						giovanni = c;
				if (giovanni != null) {
					gameModel.sendCardMessageToAllPlayers(getCardOwner().getName() + " puts Giovanni into his hand!", giovanni, "");
					gameModel.getAttackAction().moveCard(ownDeck(), ownHand(), giovanni.getGameID(), true);
				} else
					gameModel.sendTextMessageToAllPlayers("No Giovanni card found in the deck!", "");
			}
		}
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		gameModel.sendTextMessageToAllPlayers("If heads then this attack does 20 more damage!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 40, true);
		} else
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
	}
}
