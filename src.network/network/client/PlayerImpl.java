package network.client;

import java.util.ArrayList;
import java.util.List;

import com.jme3.system.JmeContext;

import common.utilities.Pair;
import network.server.PokemonGameManager;
import network.tcp.borders.ClientBorder;
import network.tcp.borders.ServerMain;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.AccountType;
import model.enums.Color;
import model.enums.DistributionMode;
import model.enums.Element;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.interfaces.GameModelUpdate;
import model.interfaces.Position;

public class PlayerImpl extends AccountImpl implements Player, GuiToPlayerCommunication {

	private PokemonGameView view;
	private Color color;
	private PokemonGameManager server;
	private final PlayerImpl self;
	private GameModelUpdate localGameModel;

	/**
	 * Creates a new {@link PlayerImpl} object with the given parameters.
	 * 
	 * @param id
	 * @param name
	 * @param password
	 * @return
	 */
	public static Player createNewPlayer(long id, String name, String password) {
		Player p = new PlayerImpl(id, name, password);
		return p;
	}

	public PlayerImpl(long id, String name, String password) {
		super(id, name, password);
		this.accountType = AccountType.REAL_PLAYER;
		this.color = null;
		this.self = this;
	}

	public void setGUI(PokemonGameView view) {
		this.view = view;
	}

	@Override
	public List<Card> playerChoosesCards(List<Card> cards, int amount, boolean exact, String message) {
		return view.userChoosesCards(cards, amount, exact, message);
	}

	@Override
	public List<PositionID> playerChoosesPositions(List<PositionID> positionList, int amount, boolean exact, String message) {
		return view.userChoosesPositions(positionList, amount, exact, message);
	}

	@Override
	public List<Element> playerChoosesElements(List<Element> elements, int amount, boolean exact, String message) {
		return view.userChoosesElements(elements, amount, exact, message);
	}

	@Override
	public List<String> playerChoosesAttacks(List<Card> attackOwner, List<String> attacks, int amount, boolean exact, String message) {
		return view.userChoosesAttacks(attackOwner, attacks, amount, exact, message);
	}

	@Override
	public List<Card> playerPaysEnergyCosts(List<Element> costs, List<Card> energyCards) {
		return view.userPaysEnergyCosts(costs, energyCards);
	}

	@Override
	public List<Integer> playerDistributesDamage(List<Position> positionList, List<Integer> damageList, List<Integer> maxDistList, DistributionMode mode) {
		return view.userDistributesDamage(positionList, damageList, maxDistList, mode);
	}

	@Override
	public void playerUpdatesGameModel(GameModelUpdate gameModelUpdate) {
		this.localGameModel = gameModelUpdate;
		view.userUpdatesGameModel(this.localGameModel, color);
	}

	private GameModelUpdate getFreshGameModel() {
		if (this.localGameModel == null)
			this.localGameModel = server.getGameModelForPlayer(self);
		return this.localGameModel;
	}

	@Override
	public void playerMakesMove() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				GameModelUpdate gameModel = getFreshGameModel();

				if (color == Color.BLUE) {
					List<Pair<Position, Integer>> choosableCards = new ArrayList<>();
					// Check hand:
					Position handPos = gameModel.getPosition(PositionID.BLUE_HAND);
					for (int i = 0; i < handPos.getCards().size(); i++) {
						List<String> actions = server.getPlayerActions(i, PositionID.BLUE_HAND, self);
						if (!actions.isEmpty())
							choosableCards.add(new Pair<Position, Integer>(handPos, i));
					}
					// Check active & bench:
					Position activePosition = gameModel.getPosition(PositionID.BLUE_ACTIVEPOKEMON);
					List<String> actions = server.getPlayerActions(activePosition.size() - 1, PositionID.BLUE_ACTIVEPOKEMON, self);
					if (!actions.isEmpty())
						choosableCards.add(new Pair<Position, Integer>(activePosition, activePosition.size() - 1));
					for (int i = 1; i <= 5; i++) {
						Position benchPosition = gameModel.getPosition(PositionID.valueOf("BLUE_BENCH_" + i));
						if (benchPosition.size() > 0) {
							actions = server.getPlayerActions(benchPosition.size() - 1, PositionID.valueOf("BLUE_BENCH_" + i), self);
							if (!actions.isEmpty())
								choosableCards.add(new Pair<Position, Integer>(benchPosition, benchPosition.size() - 1));
						}
					}
					// Make positions choosable:
					for (Pair<Position, Integer> p : choosableCards)
						view.setCardChoosable(p.getKey(), p.getValue(), true);
				} else if (color == Color.RED) {
					List<Pair<Position, Integer>> choosableCards = new ArrayList<>();
					// Check hand:
					Position handPos = gameModel.getPosition(PositionID.RED_HAND);
					for (int i = 0; i < handPos.getCards().size(); i++) {
						List<String> actions = server.getPlayerActions(i, PositionID.RED_HAND, self);
						if (!actions.isEmpty())
							choosableCards.add(new Pair<Position, Integer>(handPos, i));
					}
					// Check active & bench:
					Position activePosition = gameModel.getPosition(PositionID.RED_ACTIVEPOKEMON);
					List<String> actions = server.getPlayerActions(activePosition.size() - 1, PositionID.RED_ACTIVEPOKEMON, self);
					if (!actions.isEmpty())
						choosableCards.add(new Pair<Position, Integer>(activePosition, activePosition.size() - 1));
					for (int i = 1; i <= 5; i++) {
						Position benchPosition = gameModel.getPosition(PositionID.valueOf("RED_BENCH_" + i));
						if (benchPosition.size() > 0) {
							actions = server.getPlayerActions(benchPosition.size() - 1, PositionID.valueOf("RED_BENCH_" + i), self);
							if (!actions.isEmpty())
								choosableCards.add(new Pair<Position, Integer>(benchPosition, benchPosition.size() - 1));
						}
					}
					// Make positions choosable:
					for (Pair<Position, Integer> p : choosableCards)
						view.setCardChoosable(p.getKey(), p.getValue(), true);
				} else
					System.err.println("Received playerMakesMove() from server, but no color assigned to this player");

				// Make EndTurnButton visible:
				view.setEndTurnButtonVisible(true);
			}
		}).start();
	}

	@Override
	public void playHandCard(int index) {
		server.playerPlaysCard(this, index);
	}

	@Override
	public List<PlayerAction> getPlayerActionsForHandCard(int index) {
		List<String> actions = null;
		if (this.color.equals(Color.BLUE))
			actions = server.getPlayerActions(index, PositionID.BLUE_HAND, this);
		else if (this.color.equals(Color.RED))
			actions = server.getPlayerActions(index, PositionID.RED_HAND, this);
		if (actions == null) {
			System.err.println("Error in getPlayerActionsForHandCard(...) in class PlayerImpl: No color set!");
			return new ArrayList<>();
		}
		List<PlayerAction> pActionList = new ArrayList<>();
		for (String s : actions)
			pActionList.add(PlayerAction.valueOf(s));
		return pActionList;
	}

	@Override
	public List<PlayerAction> getPlayerActionsForArenaPosition(PositionID positionID) {
		List<String> actions = null;
		if (this.color.equals(Color.BLUE))
			actions = server.getPlayerActions(0, positionID, this);
		else if (this.color.equals(Color.RED))
			actions = server.getPlayerActions(0, positionID, this);
		if (actions == null) {
			System.err.println("Error in getPlayerActionsForArenaPosition(...) in class PlayerImpl: No color set!");
			return new ArrayList<>();
		}
		ArrayList<PlayerAction> pActionList = new ArrayList<>();
		for (String s : actions)
			pActionList.add(PlayerAction.valueOf(s));
		return pActionList;
	}

	@Override
	public List<String> getAttackNames(PositionID posID) {
		return this.server.getAttacksForPosition(posID);
	}

	@Override
	public void attack(int i) {
		PositionID posID = this.color == Color.BLUE ? PositionID.BLUE_ACTIVEPOKEMON : PositionID.RED_ACTIVEPOKEMON;
		GameModelUpdate gameModel = server.getGameModelForPlayer(this);
		Position pos = gameModel.getPosition(posID);
		this.server.executeAttack(this, ((PokemonCard) pos.getTopCard()).getAttackNames().get(i));
	}

	@Override
	public List<String> getPokePowerNames(PositionID posID) {
		return this.server.getPokePowerForPosition(posID);
	}

	@Override
	public void pokemonPower(PositionID posID) {
		GameModelUpdate gameModel = server.getGameModelForPlayer(this);
		Position pos = gameModel.getPosition(posID);
		this.server.executePokemonPower(this, ((PokemonCard) pos.getTopCard()).getPokemonPowerNames().get(0), posID);
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public void receiveGameDeleted() {
		view.userReceivesGameTextMessage("The game has been deleted!");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		view.stopGame();
	}

	@Override
	public void playerReceivesGameTextMessage(String message) {
		view.userReceivesGameTextMessage(message);
	}

	@Override
	public void playerReceivesCardMessage(String message, Card card) {
		view.userReceivesCardMessage(message, card);
	}

	@Override
	public void playerReceivesCardMessage(String message, List<Card> cardList) {
		view.userReceivesCardMessage(message, cardList);
	}

	@Override
	public void startGame() {
		view.startGame();
	}

	public void exit() {
		view.stopGame();
	}

	@Override
	public void retreatPokemon() {
		this.server.retreatPokemon(this);
	}

	public void setServer(PokemonGameManager server) {
		this.server = server;
	}

	@Override
	public void sendEndTurnToServer() {
		this.server.endTurn(this);
	}

	@Override
	public void createGame() {
		System.out.println("[Server] Starting server...!");

		ServerMain main = new ServerMain();
		new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("[Server] Calling start...!");
				main.start(JmeContext.Type.Headless);
				System.out.println("[Server] Start sucessful!");
			}
		}).start();

		// Wait for server to start:
		while (!main.isStarted())
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		System.out.println("[Server] Started!");

		ClientBorder playerBorder = new ClientBorder(this);
		this.setServer(playerBorder);

		playerBorder.connectAsPlayer(this, ServerMain.SERVER_LOCALHOST, ServerMain.GAME_PW);
	}

	@Override
	public void connectToGame(String ipAdress) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				ClientBorder botBorder = new ClientBorder(self);
				self.setServer(botBorder);

				// Register at server:
				botBorder.connectAsPlayer(self, ipAdress, ServerMain.GAME_PW);
			}
		}).start();
	}

	@Override
	public Account asAccount() {
		return this;
	}
}
