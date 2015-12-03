package model.game;

import java.util.ArrayList;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Color;
import model.enums.PositionID;
import model.interfaces.GameField;
import model.interfaces.GameModelUpdate;
import model.interfaces.Position;

public class GameFieldImpl implements GameField {

	private Position blueHand, blueBench1, blueBench2, blueBench3, blueBench4, blueBench5, blueActive, blueDiscardPile, bluePrice1, bluePrice2, bluePrice3, bluePrice4, bluePrice5,
			bluePrice6, blueDeck, redHand, redBench1, redBench2, redBench3, redBench4, redBench5, redActive, redDiscardPile, redPrice1, redPrice2, redPrice3, redPrice4, redPrice5,
			redPrice6, redDeck, stadium;

	public GameFieldImpl() {
		initPositions();
	}

	public GameFieldImpl(GameModelUpdate gameModelUpdate) {
		blueHand = gameModelUpdate.getPosition(PositionID.BLUE_HAND);
		blueBench1 = gameModelUpdate.getPosition(PositionID.BLUE_BENCH_1);
		blueBench2 = gameModelUpdate.getPosition(PositionID.BLUE_BENCH_2);
		blueBench3 = gameModelUpdate.getPosition(PositionID.BLUE_BENCH_3);
		blueBench4 = gameModelUpdate.getPosition(PositionID.BLUE_BENCH_4);
		blueBench5 = gameModelUpdate.getPosition(PositionID.BLUE_BENCH_5);
		blueActive = gameModelUpdate.getPosition(PositionID.BLUE_ACTIVEPOKEMON);
		blueDiscardPile = gameModelUpdate.getPosition(PositionID.BLUE_DISCARDPILE);
		bluePrice1 = gameModelUpdate.getPosition(PositionID.BLUE_PRICE_1);
		bluePrice2 = gameModelUpdate.getPosition(PositionID.BLUE_PRICE_2);
		bluePrice3 = gameModelUpdate.getPosition(PositionID.BLUE_PRICE_3);
		bluePrice4 = gameModelUpdate.getPosition(PositionID.BLUE_PRICE_4);
		bluePrice5 = gameModelUpdate.getPosition(PositionID.BLUE_PRICE_5);
		bluePrice6 = gameModelUpdate.getPosition(PositionID.BLUE_PRICE_6);
		blueDeck = gameModelUpdate.getPosition(PositionID.BLUE_DECK);

		redHand = gameModelUpdate.getPosition(PositionID.RED_HAND);
		redBench1 = gameModelUpdate.getPosition(PositionID.RED_BENCH_1);
		redBench2 = gameModelUpdate.getPosition(PositionID.RED_BENCH_2);
		redBench3 = gameModelUpdate.getPosition(PositionID.RED_BENCH_3);
		redBench4 = gameModelUpdate.getPosition(PositionID.RED_BENCH_4);
		redBench5 = gameModelUpdate.getPosition(PositionID.RED_BENCH_5);
		redActive = gameModelUpdate.getPosition(PositionID.RED_ACTIVEPOKEMON);
		redDiscardPile = gameModelUpdate.getPosition(PositionID.RED_DISCARDPILE);
		redPrice1 = gameModelUpdate.getPosition(PositionID.RED_PRICE_1);
		redPrice2 = gameModelUpdate.getPosition(PositionID.RED_PRICE_2);
		redPrice3 = gameModelUpdate.getPosition(PositionID.RED_PRICE_3);
		redPrice4 = gameModelUpdate.getPosition(PositionID.RED_PRICE_4);
		redPrice5 = gameModelUpdate.getPosition(PositionID.RED_PRICE_5);
		redPrice6 = gameModelUpdate.getPosition(PositionID.RED_PRICE_6);
		redDeck = gameModelUpdate.getPosition(PositionID.RED_DECK);

		stadium = gameModelUpdate.getPosition(PositionID.STADIUM);
	}

	private void initPositions() {
		blueHand = new PositionImpl(PositionID.BLUE_HAND, Color.BLUE);
		blueBench1 = new PositionImpl(PositionID.BLUE_BENCH_1, Color.BLUE);
		blueBench2 = new PositionImpl(PositionID.BLUE_BENCH_2, Color.BLUE);
		blueBench3 = new PositionImpl(PositionID.BLUE_BENCH_3, Color.BLUE);
		blueBench4 = new PositionImpl(PositionID.BLUE_BENCH_4, Color.BLUE);
		blueBench5 = new PositionImpl(PositionID.BLUE_BENCH_5, Color.BLUE);
		blueActive = new PositionImpl(PositionID.BLUE_ACTIVEPOKEMON, Color.BLUE);
		blueDiscardPile = new PositionImpl(PositionID.BLUE_DISCARDPILE, Color.BLUE);
		bluePrice1 = new PositionImpl(PositionID.BLUE_PRICE_1, Color.BLUE);
		bluePrice2 = new PositionImpl(PositionID.BLUE_PRICE_2, Color.BLUE);
		bluePrice3 = new PositionImpl(PositionID.BLUE_PRICE_3, Color.BLUE);
		bluePrice4 = new PositionImpl(PositionID.BLUE_PRICE_4, Color.BLUE);
		bluePrice5 = new PositionImpl(PositionID.BLUE_PRICE_5, Color.BLUE);
		bluePrice6 = new PositionImpl(PositionID.BLUE_PRICE_6, Color.BLUE);
		blueDeck = new PositionImpl(PositionID.BLUE_DECK, Color.BLUE);

		redHand = new PositionImpl(PositionID.RED_HAND, Color.RED);
		redBench1 = new PositionImpl(PositionID.RED_BENCH_1, Color.RED);
		redBench2 = new PositionImpl(PositionID.RED_BENCH_2, Color.RED);
		redBench3 = new PositionImpl(PositionID.RED_BENCH_3, Color.RED);
		redBench4 = new PositionImpl(PositionID.RED_BENCH_4, Color.RED);
		redBench5 = new PositionImpl(PositionID.RED_BENCH_5, Color.RED);
		redActive = new PositionImpl(PositionID.RED_ACTIVEPOKEMON, Color.RED);
		redDiscardPile = new PositionImpl(PositionID.RED_DISCARDPILE, Color.RED);
		redPrice1 = new PositionImpl(PositionID.RED_PRICE_1, Color.RED);
		redPrice2 = new PositionImpl(PositionID.RED_PRICE_2, Color.RED);
		redPrice3 = new PositionImpl(PositionID.RED_PRICE_3, Color.RED);
		redPrice4 = new PositionImpl(PositionID.RED_PRICE_4, Color.RED);
		redPrice5 = new PositionImpl(PositionID.RED_PRICE_5, Color.RED);
		redPrice6 = new PositionImpl(PositionID.RED_PRICE_6, Color.RED);
		redDeck = new PositionImpl(PositionID.RED_DECK, Color.RED);

		stadium = new PositionImpl(PositionID.STADIUM, Color.WHITE);
	}

	public ArrayList<Position> getAllPositions() {
		ArrayList<Position> list = new ArrayList<Position>();
		list.add(blueHand);
		list.add(blueBench1);
		list.add(blueBench2);
		list.add(blueBench3);
		list.add(blueBench4);
		list.add(blueBench5);
		list.add(blueActive);
		list.add(blueDiscardPile);
		list.add(bluePrice1);
		list.add(bluePrice2);
		list.add(bluePrice3);
		list.add(bluePrice4);
		list.add(bluePrice5);
		list.add(bluePrice6);
		list.add(blueDeck);

		list.add(redHand);
		list.add(redBench1);
		list.add(redBench2);
		list.add(redBench3);
		list.add(redBench4);
		list.add(redBench5);
		list.add(redActive);
		list.add(redDiscardPile);
		list.add(redPrice1);
		list.add(redPrice2);
		list.add(redPrice3);
		list.add(redPrice4);
		list.add(redPrice5);
		list.add(redPrice6);
		list.add(redDeck);

		list.add(stadium);
		return list;
	}

	public Position getBlueHand() {
		return blueHand;
	}

	public void setBlueHand(Position blueHand) {
		this.blueHand = blueHand;
	}

	public Position getBlueBench1() {
		return blueBench1;
	}

	public void setBlueBench1(Position blueBench1) {
		this.blueBench1 = blueBench1;
	}

	public Position getBlueBench2() {
		return blueBench2;
	}

	public void setBlueBench2(Position blueBench2) {
		this.blueBench2 = blueBench2;
	}

	public Position getBlueBench3() {
		return blueBench3;
	}

	public void setBlueBench3(Position blueBench3) {
		this.blueBench3 = blueBench3;
	}

	public Position getBlueBench4() {
		return blueBench4;
	}

	public void setBlueBench4(Position blueBench4) {
		this.blueBench4 = blueBench4;
	}

	public Position getBlueBench5() {
		return blueBench5;
	}

	public void setBlueBench5(Position blueBench5) {
		this.blueBench5 = blueBench5;
	}

	public Position getBlueActive() {
		return blueActive;
	}

	public void setBlueActive(Position blueActive) {
		this.blueActive = blueActive;
	}

	public Position getBlueDiscardPile() {
		return blueDiscardPile;
	}

	public void setBlueDiscardPile(Position blueDiscardPile) {
		this.blueDiscardPile = blueDiscardPile;
	}

	public Position getBlueDeck() {
		return blueDeck;
	}

	public void setBlueDeck(Position blueDeck) {
		this.blueDeck = blueDeck;
	}

	public Position getRedHand() {
		return redHand;
	}

	public void setRedHand(Position redHand) {
		this.redHand = redHand;
	}

	public Position getRedBench1() {
		return redBench1;
	}

	public void setRedBench1(Position redBench1) {
		this.redBench1 = redBench1;
	}

	public Position getRedBench2() {
		return redBench2;
	}

	public void setRedBench2(Position redBench2) {
		this.redBench2 = redBench2;
	}

	public Position getRedBench3() {
		return redBench3;
	}

	public void setRedBench3(Position redBench3) {
		this.redBench3 = redBench3;
	}

	public Position getRedBench4() {
		return redBench4;
	}

	public void setRedBench4(Position redBench4) {
		this.redBench4 = redBench4;
	}

	public Position getRedBench5() {
		return redBench5;
	}

	public void setRedBench5(Position redBench5) {
		this.redBench5 = redBench5;
	}

	public Position getRedActive() {
		return redActive;
	}

	public void setRedActive(Position redActive) {
		this.redActive = redActive;
	}

	public Position getRedDiscardPile() {
		return redDiscardPile;
	}

	public void setRedDiscardPile(Position redDiscardPile) {
		this.redDiscardPile = redDiscardPile;
	}

	public Position getRedDeck() {
		return redDeck;
	}

	public void setRedDeck(Position redDeck) {
		this.redDeck = redDeck;
	}

	public Position getBluePrice1() {
		return bluePrice1;
	}

	public void setBluePrice1(Position bluePrice1) {
		this.bluePrice1 = bluePrice1;
	}

	public Position getBluePrice2() {
		return bluePrice2;
	}

	public void setBluePrice2(Position bluePrice2) {
		this.bluePrice2 = bluePrice2;
	}

	public Position getBluePrice3() {
		return bluePrice3;
	}

	public void setBluePrice3(Position bluePrice3) {
		this.bluePrice3 = bluePrice3;
	}

	public Position getBluePrice4() {
		return bluePrice4;
	}

	public void setBluePrice4(Position bluePrice4) {
		this.bluePrice4 = bluePrice4;
	}

	public Position getBluePrice5() {
		return bluePrice5;
	}

	public void setBluePrice5(Position bluePrice5) {
		this.bluePrice5 = bluePrice5;
	}

	public Position getBluePrice6() {
		return bluePrice6;
	}

	public void setBluePrice6(Position bluePrice6) {
		this.bluePrice6 = bluePrice6;
	}

	public Position getRedPrice1() {
		return redPrice1;
	}

	public void setRedPrice1(Position redPrice1) {
		this.redPrice1 = redPrice1;
	}

	public Position getRedPrice2() {
		return redPrice2;
	}

	public void setRedPrice2(Position redPrice2) {
		this.redPrice2 = redPrice2;
	}

	public Position getRedPrice3() {
		return redPrice3;
	}

	public void setRedPrice3(Position redPrice3) {
		this.redPrice3 = redPrice3;
	}

	public Position getRedPrice4() {
		return redPrice4;
	}

	public void setRedPrice4(Position redPrice4) {
		this.redPrice4 = redPrice4;
	}

	public Position getRedPrice5() {
		return redPrice5;
	}

	public void setRedPrice5(Position redPrice5) {
		this.redPrice5 = redPrice5;
	}

	public Position getRedPrice6() {
		return redPrice6;
	}

	public void setRedPrice6(Position redPrice6) {
		this.redPrice6 = redPrice6;
	}

	@Override
	public Position getStadium() {
		return stadium;
	}

	@Override
	public void setStadium(Position stadium) {
		this.stadium = stadium;
	}

	@Override
	public ArrayList<PositionID> getNonEmptyPriceList(Color color) {
		ArrayList<PositionID> posList = new ArrayList<>();
		if (color == Color.BLUE) {
			if (!this.bluePrice1.isEmpty())
				posList.add(this.bluePrice1.getPositionID());
			if (!this.bluePrice2.isEmpty())
				posList.add(this.bluePrice2.getPositionID());
			if (!this.bluePrice3.isEmpty())
				posList.add(this.bluePrice3.getPositionID());
			if (!this.bluePrice4.isEmpty())
				posList.add(this.bluePrice4.getPositionID());
			if (!this.bluePrice5.isEmpty())
				posList.add(this.bluePrice5.getPositionID());
			if (!this.bluePrice6.isEmpty())
				posList.add(this.bluePrice6.getPositionID());
		} else {
			if (!this.redPrice1.isEmpty())
				posList.add(this.redPrice1.getPositionID());
			if (!this.redPrice2.isEmpty())
				posList.add(this.redPrice2.getPositionID());
			if (!this.redPrice3.isEmpty())
				posList.add(this.redPrice3.getPositionID());
			if (!this.redPrice4.isEmpty())
				posList.add(this.redPrice4.getPositionID());
			if (!this.redPrice5.isEmpty())
				posList.add(this.redPrice5.getPositionID());
			if (!this.redPrice6.isEmpty())
				posList.add(this.redPrice6.getPositionID());
		}
		return posList;
	}

	@Override
	public Position getPosition(PositionID posID) {
		switch (posID) {
		case BLUE_ACTIVEPOKEMON:
			return this.getBlueActive();
		case BLUE_BENCH_1:
			return this.getBlueBench1();
		case BLUE_BENCH_2:
			return this.getBlueBench2();
		case BLUE_BENCH_3:
			return this.getBlueBench3();
		case BLUE_BENCH_4:
			return this.getBlueBench4();
		case BLUE_BENCH_5:
			return this.getBlueBench5();
		case BLUE_DECK:
			return this.getBlueDeck();
		case BLUE_DISCARDPILE:
			return this.getBlueDiscardPile();
		case BLUE_HAND:
			return this.getBlueHand();
		case BLUE_PRICE_1:
			return this.getBluePrice1();
		case BLUE_PRICE_2:
			return this.getBluePrice2();
		case BLUE_PRICE_3:
			return this.getBluePrice3();
		case BLUE_PRICE_4:
			return this.getBluePrice4();
		case BLUE_PRICE_5:
			return this.getBluePrice5();
		case BLUE_PRICE_6:
			return this.getBluePrice6();
		case RED_ACTIVEPOKEMON:
			return this.getRedActive();
		case RED_BENCH_1:
			return this.getRedBench1();
		case RED_BENCH_2:
			return this.getRedBench2();
		case RED_BENCH_3:
			return this.getRedBench3();
		case RED_BENCH_4:
			return this.getRedBench4();
		case RED_BENCH_5:
			return this.getRedBench5();
		case RED_DECK:
			return this.getRedDeck();
		case RED_DISCARDPILE:
			return this.getRedDiscardPile();
		case RED_HAND:
			return this.getRedHand();
		case RED_PRICE_1:
			return this.getRedPrice1();
		case RED_PRICE_2:
			return this.getRedPrice2();
		case RED_PRICE_3:
			return this.getRedPrice3();
		case RED_PRICE_4:
			return this.getRedPrice4();
		case RED_PRICE_5:
			return this.getRedPrice5();
		case RED_PRICE_6:
			return this.getRedPrice6();
		case STADIUM:
			return this.getStadium();
		default:
			return null;
		}
	}

	@Override
	public ArrayList<PositionID> getFullBenchPositions(Color playerColor, Player playerBlue, Player playerRed) {
		ArrayList<PositionID> positionList = new ArrayList<PositionID>();
		if (playerBlue.getColor() == playerColor) {
			if (!this.getPosition(PositionID.BLUE_BENCH_1).isEmpty())
				positionList.add(PositionID.BLUE_BENCH_1);
			if (!this.getPosition(PositionID.BLUE_BENCH_2).isEmpty())
				positionList.add(PositionID.BLUE_BENCH_2);
			if (!this.getPosition(PositionID.BLUE_BENCH_3).isEmpty())
				positionList.add(PositionID.BLUE_BENCH_3);
			if (!this.getPosition(PositionID.BLUE_BENCH_4).isEmpty())
				positionList.add(PositionID.BLUE_BENCH_4);
			if (!this.getPosition(PositionID.BLUE_BENCH_5).isEmpty())
				positionList.add(PositionID.BLUE_BENCH_5);
		} else if (playerRed.getColor() == playerColor) {
			if (!this.getPosition(PositionID.RED_BENCH_1).isEmpty())
				positionList.add(PositionID.RED_BENCH_1);
			if (!this.getPosition(PositionID.RED_BENCH_2).isEmpty())
				positionList.add(PositionID.RED_BENCH_2);
			if (!this.getPosition(PositionID.RED_BENCH_3).isEmpty())
				positionList.add(PositionID.RED_BENCH_3);
			if (!this.getPosition(PositionID.RED_BENCH_4).isEmpty())
				positionList.add(PositionID.RED_BENCH_4);
			if (!this.getPosition(PositionID.RED_BENCH_5).isEmpty())
				positionList.add(PositionID.RED_BENCH_5);
		} else
			System.err.println("Wrong call of method 'getTopPokemonOnField'!");
		return positionList;
	}

	@Override
	public ArrayList<PositionID> getFullArenaPositions(Color playerColor, Player playerBlue, Player playerRed) {
		ArrayList<PositionID> positionList = new ArrayList<PositionID>();
		if (playerBlue.getColor() == playerColor) {
			if (!this.getPosition(PositionID.BLUE_ACTIVEPOKEMON).isEmpty())
				positionList.add(PositionID.BLUE_ACTIVEPOKEMON);
			if (!this.getPosition(PositionID.BLUE_BENCH_1).isEmpty())
				positionList.add(PositionID.BLUE_BENCH_1);
			if (!this.getPosition(PositionID.BLUE_BENCH_2).isEmpty())
				positionList.add(PositionID.BLUE_BENCH_2);
			if (!this.getPosition(PositionID.BLUE_BENCH_3).isEmpty())
				positionList.add(PositionID.BLUE_BENCH_3);
			if (!this.getPosition(PositionID.BLUE_BENCH_4).isEmpty())
				positionList.add(PositionID.BLUE_BENCH_4);
			if (!this.getPosition(PositionID.BLUE_BENCH_5).isEmpty())
				positionList.add(PositionID.BLUE_BENCH_5);
		} else if (playerRed.getColor() == playerColor) {
			if (!this.getPosition(PositionID.RED_ACTIVEPOKEMON).isEmpty())
				positionList.add(PositionID.RED_ACTIVEPOKEMON);
			if (!this.getPosition(PositionID.RED_BENCH_1).isEmpty())
				positionList.add(PositionID.RED_BENCH_1);
			if (!this.getPosition(PositionID.RED_BENCH_2).isEmpty())
				positionList.add(PositionID.RED_BENCH_2);
			if (!this.getPosition(PositionID.RED_BENCH_3).isEmpty())
				positionList.add(PositionID.RED_BENCH_3);
			if (!this.getPosition(PositionID.RED_BENCH_4).isEmpty())
				positionList.add(PositionID.RED_BENCH_4);
			if (!this.getPosition(PositionID.RED_BENCH_5).isEmpty())
				positionList.add(PositionID.RED_BENCH_5);
		} else
			System.err.println("Wrong call of method 'getTopPokemonOnField'!");
		return positionList;
	}

	@Override
	public ArrayList<PositionID> getPositionsForEvolving(PokemonCard c, Color color, int turnNumber) {
		ArrayList<PositionID> posList = new ArrayList<PositionID>();
		if (color == Color.BLUE) {
			Card topCard = this.getPosition(PositionID.BLUE_ACTIVEPOKEMON).getTopCard();
			if (topCard != null && c.getEvolvesFrom().equals(topCard.getName()) && turnNumber > topCard.getPlayedInTurn())
				posList.add(PositionID.BLUE_ACTIVEPOKEMON);

			topCard = this.getPosition(PositionID.BLUE_BENCH_1).getTopCard();
			if (topCard != null && c.getEvolvesFrom().equals(topCard.getName()) && turnNumber > topCard.getPlayedInTurn())
				posList.add(PositionID.BLUE_BENCH_1);

			topCard = this.getPosition(PositionID.BLUE_BENCH_2).getTopCard();
			if (topCard != null && c.getEvolvesFrom().equals(topCard.getName()) && turnNumber > topCard.getPlayedInTurn())
				posList.add(PositionID.BLUE_BENCH_2);

			topCard = this.getPosition(PositionID.BLUE_BENCH_3).getTopCard();
			if (topCard != null && c.getEvolvesFrom().equals(topCard.getName()) && turnNumber > topCard.getPlayedInTurn())
				posList.add(PositionID.BLUE_BENCH_3);

			topCard = this.getPosition(PositionID.BLUE_BENCH_4).getTopCard();
			if (topCard != null && c.getEvolvesFrom().equals(topCard.getName()) && turnNumber > topCard.getPlayedInTurn())
				posList.add(PositionID.BLUE_BENCH_4);

			topCard = this.getPosition(PositionID.BLUE_BENCH_5).getTopCard();
			if (topCard != null && c.getEvolvesFrom().equals(topCard.getName()) && turnNumber > topCard.getPlayedInTurn())
				posList.add(PositionID.BLUE_BENCH_5);
		} else if (color == Color.RED) {
			Card topCard = this.getPosition(PositionID.RED_ACTIVEPOKEMON).getTopCard();
			if (topCard != null && c.getEvolvesFrom().equals(topCard.getName()) && turnNumber > topCard.getPlayedInTurn())
				posList.add(PositionID.RED_ACTIVEPOKEMON);

			topCard = this.getPosition(PositionID.RED_BENCH_1).getTopCard();
			if (topCard != null && c.getEvolvesFrom().equals(topCard.getName()) && turnNumber > topCard.getPlayedInTurn())
				posList.add(PositionID.RED_BENCH_1);

			topCard = this.getPosition(PositionID.RED_BENCH_2).getTopCard();
			if (topCard != null && c.getEvolvesFrom().equals(topCard.getName()) && turnNumber > topCard.getPlayedInTurn())
				posList.add(PositionID.RED_BENCH_2);

			topCard = this.getPosition(PositionID.RED_BENCH_3).getTopCard();
			if (topCard != null && c.getEvolvesFrom().equals(topCard.getName()) && turnNumber > topCard.getPlayedInTurn())
				posList.add(PositionID.RED_BENCH_3);

			topCard = this.getPosition(PositionID.RED_BENCH_4).getTopCard();
			if (topCard != null && c.getEvolvesFrom().equals(topCard.getName()) && turnNumber > topCard.getPlayedInTurn())
				posList.add(PositionID.RED_BENCH_4);

			topCard = this.getPosition(PositionID.RED_BENCH_5).getTopCard();
			if (topCard != null && c.getEvolvesFrom().equals(topCard.getName()) && turnNumber > topCard.getPlayedInTurn())
				posList.add(PositionID.RED_BENCH_5);
		} else
			System.err.println("Wrong player send to method 'returnPositionsForEvolving'!");
		return posList;
	}
}
