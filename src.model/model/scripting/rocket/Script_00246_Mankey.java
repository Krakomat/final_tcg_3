package model.scripting.rocket;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00246_Mankey extends PokemonCardScript {

	public Script_00246_Mankey(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Mischief", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Anger", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Mischief"))
			this.Mischief();
		else
			this.Anger();
	}

	private void Mischief() {
		Player enemy = this.getEnemyPlayer();
		gameModel.sendTextMessageToAllPlayers(enemy.getName() + " shuffles his deck!", "");
		gameModel.getAttackAction().shufflePosition(PositionID.getDeckPosition(enemy.getColor()));
		gameModel.sendGameModelToAllPlayers(Sounds.SHUFFLE);
	}

	private void Anger() {
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
