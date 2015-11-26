package model.scripting.rocket;

import java.util.ArrayList;
import java.util.List;
import model.database.PokemonCard;
import model.enums.Element;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00265_DarkRaichu extends PokemonCardScript {

	public Script_00265_DarkRaichu(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.LIGHTNING);
		this.addAttack("Thunder Wave", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.LIGHTNING);
		att2Cost.add(Element.LIGHTNING);
		this.addAttack("Selfdestruct", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Thunder Wave"))
			this.donnerwelle();
		else
			this.finale();
	}

	private void donnerwelle() {

	}

	private void finale() {

	}
}
