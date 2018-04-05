package debugging;

import model.enums.PositionID;
import model.game.PokemonGameModelImpl;
import network.server.PokemonGameManagerImpl;

/**
 * Sets up a custom game, where one can debug specific cards really fast!
 * 
 * @author Michael
 *
 */
public class CustomGameSetup extends SetupGame {
	private PokemonGameManagerImpl server;
	private PokemonGameModelImpl gameModel;

	public CustomGameSetup() {
		this.server = this.setup();
		this.gameModel = (PokemonGameModelImpl) server.getGameModel();
	}

	private void initBoard() {
		this.initOpponent();
		this.initPlayer();
		gameModel.sendGameModelToAllPlayers("");
	}

	private void initPlayer() {
		this.gameModel.addCardOnTopOfPosition("00101", PositionID.BLUE_ACTIVEPOKEMON);
		this.gameModel.addCardOnTopOfPosition("00101", PositionID.BLUE_ACTIVEPOKEMON);
		this.gameModel.addCardOnTopOfPosition("00239", PositionID.BLUE_ACTIVEPOKEMON);
		this.gameModel.addCardOnTopOfPosition("00003", PositionID.BLUE_PRICE_1);
	}

	private void initOpponent() {
		this.gameModel.addCardOnTopOfPosition("00357", PositionID.RED_ACTIVEPOKEMON);
		this.gameModel.addCardOnTopOfPosition("00003", PositionID.RED_PRICE_1);
	}

	private void startGame() {
		this.server.startGame();
	}

	public static void main(String[] args) {
		CustomGameSetup s = new CustomGameSetup();
		s.initBoard();
		s.startGame();
	}
}
