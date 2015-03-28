package model.interfaces;

import java.util.ArrayList;

import network.client.Player;
import model.database.PokemonCard;
import model.enums.Color;
import model.enums.PositionID;

/**
 * A game field stores all positions of the game.
 * 
 * @author Michael
 *
 */
public interface GameField {
	/**
	 * Returns a lisz with all price positions, that are not empty for the given player.
	 * 
	 * @param color
	 * @return
	 */
	public ArrayList<PositionID> getNonEmptyPriceList(Color color);

	public ArrayList<Position> getAllPositions();

	public Position getPosition(PositionID posID);

	public Position getBlueHand();

	public void setBlueHand(Position blueHand);

	public Position getBlueBench1();

	public void setBlueBench1(Position blueBench1);

	public Position getBlueBench2();

	public void setBlueBench2(Position blueBench2);

	public Position getBlueBench3();

	public void setBlueBench3(Position blueBench3);

	public Position getBlueBench4();

	public void setBlueBench4(Position blueBench4);

	public Position getBlueBench5();

	public void setBlueBench5(Position blueBench5);

	public Position getBlueActive();

	public void setBlueActive(Position blueActive);

	public Position getBlueDiscardPile();

	public void setBlueDiscardPile(Position blueDiscardPile);

	public Position getBlueDeck();

	public void setBlueDeck(Position blueDeck);

	public Position getRedHand();

	public void setRedHand(Position redHand);

	public Position getRedBench1();

	public void setRedBench1(Position redBench1);

	public Position getRedBench2();

	public void setRedBench2(Position redBench2);

	public Position getRedBench3();

	public void setRedBench3(Position redBench3);

	public Position getRedBench4();

	public void setRedBench4(Position redBench4);

	public Position getRedBench5();

	public void setRedBench5(Position redBench5);

	public Position getRedActive();

	public void setRedActive(Position redActive);

	public Position getRedDiscardPile();

	public void setRedDiscardPile(Position redDiscardPile);

	public Position getRedDeck();

	public void setRedDeck(Position redDeck);

	public Position getBluePrice1();

	public void setBluePrice1(Position bluePrice1);

	public Position getBluePrice2();

	public void setBluePrice2(Position bluePrice2);

	public Position getBluePrice3();

	public void setBluePrice3(Position bluePrice3);

	public Position getBluePrice4();

	public void setBluePrice4(Position bluePrice4);

	public Position getBluePrice5();

	public void setBluePrice5(Position bluePrice5);

	public Position getBluePrice6();

	public void setBluePrice6(Position bluePrice6);

	public Position getRedPrice1();

	public void setRedPrice1(Position redPrice1);

	public Position getRedPrice2();

	public void setRedPrice2(Position redPrice2);

	public Position getRedPrice3();

	public void setRedPrice3(Position redPrice3);

	public Position getRedPrice4();

	public void setRedPrice4(Position redPrice4);

	public Position getRedPrice5();

	public void setRedPrice5(Position redPrice5);

	public Position getRedPrice6();

	public void setRedPrice6(Position redPrice6);

	/**
	 * Returns all bench positions with cards on them from the players arena fields.
	 * 
	 * @param playerColor
	 * @param playerBlue
	 * @param playerRed
	 * @return
	 */
	ArrayList<PositionID> getFullBenchPositions(Color playerColor, Player playerBlue, Player playerRed);

	/**
	 * Returns all positions with cards on them from the players arena fields.
	 * 
	 * @param playerColor
	 * @param playerBlue
	 * @param playerRed
	 * @return
	 */
	ArrayList<PositionID> getFullArenaPositions(Color playerColor, Player playerBlue, Player playerRed);

	/**
	 * Returns an array list of the positionsIDs, if the given card can be put on a basic pokemon(in the arena of the given player), which evolves into the given
	 * card. Also checks, evolution is allowed in this turn(one cannot put a pokemon on the bench and evolve it in the same turn).
	 * 
	 * @param c
	 * @param color
	 * @param turnNumber
	 * @return
	 */
	ArrayList<PositionID> getPositionsForEvolving(PokemonCard c, Color color, int turnNumber);
}
