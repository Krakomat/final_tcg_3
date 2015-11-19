package network.tcp.borders;

import gui2d.animations.Animation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jme3.network.HostedConnection;

import model.database.Card;
import model.database.Deck;
import model.enums.AccountType;
import model.enums.Color;
import model.enums.DistributionMode;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.GameModelUpdate;
import model.interfaces.Position;
import network.client.Player;
import network.serialization.TCGSerializer;
import network.server.PokemonGameManager;
import network.tcp.messages.ByteString;
import network.tcp.messages.Method;
import network.tcp.messages.QueryMessage;
import network.tcp.messages.RespondMessage;

/**
 * Simulates an artificial Player for the game manager. If the game manager calls some operation on the player, it is being send to the respective hosted connection,
 * which receives the message via the client listener.
 * 
 * @author Michael
 *
 */
public class ServerBorder implements Player {

	private HostedConnection client;
	private TCGSerializer serializer;
	private RespondMessage respondMessage;
	private Color color; // Stored at server and client separately
	private String name;
	private String password;

	public ServerBorder(HostedConnection client) {
		this.client = client;
		this.serializer = new TCGSerializer();
		this.respondMessage = null;
		this.color = null;
	}

	@Override
	public void startGame() {
		Method method = Method.PLAYER_START_GAME;
		QueryMessage message = new QueryMessage(method);
		message.logSendMessage("ServerBorder");
		client.send(message);
	}

	@Override
	public void setDeck(Deck deck) {
		throw new UnsupportedOperationException("ServerBorder does not support setDeck");
	}

	@Override
	public long getID() {
		throw new UnsupportedOperationException("ServerBorder does not support getID");
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public Deck getDeck() {
		QueryMessage qMessage = new QueryMessage(Method.PLAYER_GET_DECK);
		qMessage.logSendMessage("ServerBorder");
		this.respondMessage = null;
		this.client.send(qMessage);

		this.waitForResponse();

		RespondMessage rMessage = this.respondMessage;
		try {
			Deck d = serializer.unpackDeck(rMessage.getParameters().get(0));
			return d;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public AccountType getAccountType() {
		throw new UnsupportedOperationException("ServerBorder does not support getAccountType");
	}

	@Override
	public List<Card> playerChoosesCards(List<Card> cards, int amount, boolean exact, String message) {
		QueryMessage qMessage;
		try {
			List<ByteString> parameters = new ArrayList<>();
			parameters.add(serializer.packCardList(cards));
			parameters.add(serializer.packInt(amount));
			parameters.add(serializer.packBool(exact));
			parameters.add(serializer.packString(message));
			qMessage = new QueryMessage(Method.PLAYER_CHOOSE_CARDS, parameters);
			qMessage.logSendMessage("ServerBorder");
			this.respondMessage = null;
			this.client.send(qMessage);

			this.waitForResponse();

			RespondMessage rMessage = this.respondMessage;
			List<Card> answerList = serializer.unpackCardList(rMessage.getParameters().get(0));
			return answerList;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<PositionID> playerChoosesPositions(List<PositionID> list, int amount, boolean exact, String message) {
		QueryMessage qMessage;
		try {
			List<ByteString> parameters = new ArrayList<>();
			parameters.add(serializer.packPositionIDList(list));
			parameters.add(serializer.packInt(amount));
			parameters.add(serializer.packBool(exact));
			parameters.add(serializer.packString(message));
			qMessage = new QueryMessage(Method.PLAYER_CHOOSE_POSITION, parameters);
			qMessage.logSendMessage("ServerBorder");
			this.respondMessage = null;
			this.client.send(qMessage);

			this.waitForResponse();

			RespondMessage rMessage = this.respondMessage;
			List<PositionID> answerList = serializer.unpackPositionIDList(rMessage.getParameters().get(0));
			return answerList;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<Element> playerChoosesElements(List<Element> elements, int amount, boolean exact, String message) {
		QueryMessage qMessage;
		try {
			List<ByteString> parameters = new ArrayList<>();
			parameters.add(serializer.packElementList(elements));
			parameters.add(serializer.packInt(amount));
			parameters.add(serializer.packBool(exact));
			parameters.add(serializer.packString(message));
			qMessage = new QueryMessage(Method.PLAYER_CHOOSE_ELEMENT, parameters);
			qMessage.logSendMessage("ServerBorder");
			this.respondMessage = null;
			this.client.send(qMessage);

			this.waitForResponse();

			RespondMessage rMessage = this.respondMessage;
			List<Element> answerList = serializer.unpackElementList(rMessage.getParameters().get(0));
			return answerList;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<String> playerChoosesAttacks(List<Card> attackOwner, List<String> attacks, int amount, boolean exact, String message) {
		QueryMessage qMessage;
		try {
			List<ByteString> parameters = new ArrayList<>();
			parameters.add(serializer.packCardList(attackOwner));
			parameters.add(serializer.packStringList(attacks));
			parameters.add(serializer.packInt(amount));
			parameters.add(serializer.packBool(exact));
			parameters.add(serializer.packString(message));
			qMessage = new QueryMessage(Method.PLAYER_CHOOSE_ATTACK, parameters);
			qMessage.logSendMessage("ServerBorder");
			this.respondMessage = null;
			this.client.send(qMessage);

			this.waitForResponse();

			RespondMessage rMessage = this.respondMessage;
			List<String> answerList = serializer.unpackStringList(rMessage.getParameters().get(0));
			return answerList;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<Card> playerPaysEnergyCosts(List<Element> costs, List<Card> energyCards) {
		QueryMessage qMessage;
		try {
			List<ByteString> parameters = new ArrayList<>();
			parameters.add(serializer.packElementList(costs));
			parameters.add(serializer.packCardList(energyCards));
			qMessage = new QueryMessage(Method.PLAYER_PAY_ENERGY_COST, parameters);
			qMessage.logSendMessage("ServerBorder");
			this.respondMessage = null;
			this.client.send(qMessage);

			this.waitForResponse();

			RespondMessage rMessage = this.respondMessage;
			List<Card> answerList = serializer.unpackCardList(rMessage.getParameters().get(0));
			return answerList;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void playerReceivesSound(String sound) {
		QueryMessage qMessage = new QueryMessage(Method.PLAYER_RECIEVE_SOUND, serializer.packString(sound));
		qMessage.logSendMessage("ServerBorder");
		this.client.send(qMessage);
	}

	@Override
	public void playerReceivesAnimation(Animation animation) {
		this.respondMessage = null;
		QueryMessage qMessage;
		try {
			qMessage = new QueryMessage(Method.PLAYER_RECEIVE_ANIMATION, serializer.packAnimation(animation));
			qMessage.logSendMessage("ServerBorder");
			this.client.send(qMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.waitForResponse();
		// Continue!
	}

	@Override
	public void playerReceivesGameTextMessage(String message, String sound) {
		List<ByteString> parameters = new ArrayList<>();
		parameters.add(serializer.packString(message));
		parameters.add(serializer.packString(sound));
		QueryMessage qMessage = new QueryMessage(Method.PLAYER_REVEIVE_TEXT_MESSAGE, parameters);
		qMessage.logSendMessage("ServerBorder");
		this.client.send(qMessage);
	}

	@Override
	public void playerReceivesCardMessage(String message, Card card, String sound) {
		try {
			List<ByteString> parameters = new ArrayList<>();
			parameters.add(serializer.packString(message));
			parameters.add(serializer.packCard(card));
			parameters.add(serializer.packString(sound));
			QueryMessage qMessage = new QueryMessage(Method.PLAYER_RECEIVE_CARD_MESSAGE, parameters);
			qMessage.logSendMessage("ServerBorder");
			this.client.send(qMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void playerReceivesCardMessage(String message, List<Card> cardList, String sound) {
		try {
			List<ByteString> parameters = new ArrayList<>();
			parameters.add(serializer.packString(message));
			parameters.add(serializer.packCardList(cardList));
			parameters.add(serializer.packString(sound));
			QueryMessage qMessage = new QueryMessage(Method.PLAYER_RECEIVE_CARDS_MESSAGE, parameters);
			qMessage.logSendMessage("ServerBorder");
			this.client.send(qMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void playerUpdatesGameModel(GameModelUpdate gameModelUpdate, String sound) {
		QueryMessage qMessage;
		try {
			List<ByteString> parameters = new ArrayList<>();
			parameters.add(serializer.packGameModelUpdate(gameModelUpdate));
			parameters.add(serializer.packString(sound));
			qMessage = new QueryMessage(Method.PLAYER_UPDATE_GAMEMODEL, parameters);
			qMessage.logSendMessage("ServerBorder");
			this.client.send(qMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void playerMakesMove() {
		QueryMessage qMessage = new QueryMessage(Method.PLAYER_MAKE_MOVE);
		qMessage.logSendMessage("ServerBorder");
		this.client.send(qMessage);
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;

		QueryMessage qMessage = new QueryMessage(Method.PLAYER_SETCOLOR, serializer.packColor(color));
		qMessage.logSendMessage("ServerBorder");
		this.client.send(qMessage);
	}

	@Override
	public void receiveGameDeleted() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setServer(PokemonGameManager server) {
		throw new UnsupportedOperationException("ServerBorder does not support setServer");
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public List<Integer> playerDistributesDamage(List<Position> positionList, List<Integer> damageList, List<Integer> maxDistList, DistributionMode mode) {
		QueryMessage qMessage;
		try {
			List<ByteString> parameters = new ArrayList<>();
			parameters.add(serializer.packPositionList(positionList));
			parameters.add(serializer.packIntList(damageList));
			parameters.add(serializer.packIntList(maxDistList));
			parameters.add(serializer.packDistibutionMode(mode));
			qMessage = new QueryMessage(Method.PLAYER_DISTRIBUTE_DAMAGE, parameters);
			qMessage.logSendMessage("ServerBorder");
			this.respondMessage = null;
			this.client.send(qMessage);

			this.waitForResponse();

			RespondMessage rMessage = this.respondMessage;
			List<Integer> answerList = serializer.unpackIntList(rMessage.getParameters().get(0));
			return answerList;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setRespondMessage(RespondMessage respondMessage) {
		this.respondMessage = respondMessage;
	}

	public HostedConnection getHostedConnection() {
		return this.client;
	}

	private void waitForResponse() {
		while (this.respondMessage == null)
			sleep(100);
	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void exit() {
		// Do nothing here
	}
}
