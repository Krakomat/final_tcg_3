package network.tcp.borders;

import java.io.IOException;
import java.util.List;

import model.database.Card;
import model.database.Deck;
import model.enums.Color;
import model.enums.DistributionMode;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.GameModelUpdate;
import model.interfaces.Position;
import network.client.Player;
import network.serialization.TCGSerializer;
import network.tcp.messages.Method;
import network.tcp.messages.QueryMessage;
import network.tcp.messages.RespondMessage;

import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;

/**
 * Listens to calls from the server.
 * 
 * @author Michael
 *
 */
public class ClientListener implements MessageListener<Client> {

	/** Object to delegate the calls to */
	private Player player;
	private ClientBorder clientBorder;
	private TCGSerializer serializer;

	public ClientListener(Player player, ClientBorder clientBorder) {
		this.player = player;
		this.clientBorder = clientBorder;
		this.serializer = new TCGSerializer();
	}

	@Override
	public void messageReceived(Client source, Message m) {
		if (m instanceof QueryMessage) {
			QueryMessage qMessage = (QueryMessage) m;
			System.out.println("[ClientListener] Received QueryMessage: " + qMessage.getMethod());

			try {
				switch (qMessage.getMethod()) {
				case PLAYER_CHOOSE_ATTACK:
					List<Card> attackOwnerList = serializer.unpackCardList(qMessage.getParameters().get(0));
					List<String> attacksList = serializer.unpackStringList(qMessage.getParameters().get(1));
					int amount = serializer.unpackInt(qMessage.getParameters().get(2));
					boolean exact = serializer.unpackBool(qMessage.getParameters().get(3));
					String message = serializer.unpackString(qMessage.getParameters().get(4));
					List<String> answerAttackList = player.playerChoosesAttacks(attackOwnerList, attacksList, amount, exact, message);
					RespondMessage response = new RespondMessage(Method.PLAYER_CHOOSE_ATTACK, serializer.packStringList(answerAttackList));
					this.clientBorder.getClient().send(response);
					break;
				case PLAYER_CHOOSE_CARDS:
					List<Card> cardList = serializer.unpackCardList(qMessage.getParameters().get(0));
					amount = serializer.unpackInt(qMessage.getParameters().get(1));
					exact = serializer.unpackBool(qMessage.getParameters().get(2));
					message = serializer.unpackString(qMessage.getParameters().get(3));
					List<Card> answerCardList = player.playerChoosesCards(cardList, amount, exact, message);
					response = new RespondMessage(Method.PLAYER_CHOOSE_CARDS, serializer.packCardList(answerCardList));
					this.clientBorder.getClient().send(response);
					break;
				case PLAYER_CHOOSE_ELEMENT:
					List<Element> elementList = serializer.unpackElementList(qMessage.getParameters().get(0));
					amount = serializer.unpackInt(qMessage.getParameters().get(1));
					exact = serializer.unpackBool(qMessage.getParameters().get(2));
					message = serializer.unpackString(qMessage.getParameters().get(3));
					List<Element> answerElementList = player.playerChoosesElements(elementList, amount, exact, message);
					response = new RespondMessage(Method.PLAYER_CHOOSE_ELEMENT, serializer.packElementList(answerElementList));
					this.clientBorder.getClient().send(response);
					break;
				case PLAYER_CHOOSE_POSITION:
					List<PositionID> positionList = serializer.unpackPositionIDList(qMessage.getParameters().get(0));
					amount = serializer.unpackInt(qMessage.getParameters().get(1));
					exact = serializer.unpackBool(qMessage.getParameters().get(2));
					message = serializer.unpackString(qMessage.getParameters().get(3));
					List<PositionID> answerPositionList = player.playerChoosesPositions(positionList, amount, exact, message);
					response = new RespondMessage(Method.PLAYER_CHOOSE_POSITION, serializer.packPositionIDList(answerPositionList));
					this.clientBorder.getClient().send(response);
					break;
				case PLAYER_DISTRIBUTE_DAMAGE:
					List<Position> pList = serializer.unpackPositionList(qMessage.getParameters().get(0));
					List<Integer> damageList = serializer.unpackIntList(qMessage.getParameters().get(1));
					List<Integer> maxDistList = serializer.unpackIntList(qMessage.getParameters().get(2));
					DistributionMode mode = serializer.unpackDistibutionMode(qMessage.getParameters().get(3));
					List<Integer> answerIntList = player.playerDistributesDamage(pList, damageList, maxDistList, mode);
					response = new RespondMessage(Method.PLAYER_DISTRIBUTE_DAMAGE, serializer.packIntList(answerIntList));
					this.clientBorder.getClient().send(response);
					break;
				case PLAYER_GETCOLOR:
					Color color = player.getColor();
					response = new RespondMessage(Method.PLAYER_GETCOLOR, serializer.packColor(color));
					this.clientBorder.getClient().send(response);
					break;
				case PLAYER_MAKE_MOVE:
					player.playerMakesMove();
					break;
				case PLAYER_PAY_ENERGY_COST:
					elementList = serializer.unpackElementList(qMessage.getParameters().get(0));
					cardList = serializer.unpackCardList(qMessage.getParameters().get(1));
					answerCardList = player.playerPaysEnergyCosts(elementList, cardList);
					response = new RespondMessage(Method.PLAYER_PAY_ENERGY_COST, serializer.packCardList(answerCardList));
					this.clientBorder.getClient().send(response);
					break;
				case PLAYER_RECEIVE_CARDS_MESSAGE:
					message = serializer.unpackString(qMessage.getParameters().get(0));
					cardList = serializer.unpackCardList(qMessage.getParameters().get(1));
					String sound = serializer.unpackString(qMessage.getParameters().get(2));
					player.playerReceivesCardMessage(message, cardList, sound);
					break;
				case PLAYER_RECEIVE_CARD_MESSAGE:
					message = serializer.unpackString(qMessage.getParameters().get(0));
					Card c = serializer.unpackCard(qMessage.getParameters().get(1));
					sound = serializer.unpackString(qMessage.getParameters().get(2));
					player.playerReceivesCardMessage(message, c, sound);
					break;
				case PLAYER_RECEIVE_GAME_DELETED:
					break;
				case PLAYER_RECIEVE_SOUND:
					sound = serializer.unpackString(qMessage.getParameters().get(0));
					player.playerReceivesSound(sound);
					break;
				case PLAYER_REVEIVE_TEXT_MESSAGE:
					message = serializer.unpackString(qMessage.getParameters().get(0));
					sound = serializer.unpackString(qMessage.getParameters().get(1));
					player.playerReceivesGameTextMessage(message, sound);
					break;
				case PLAYER_SETCOLOR:
					color = serializer.unpackColor(qMessage.getParameters().get(0));
					player.setColor(color);
					break;
				case PLAYER_SETSERVER:
					// Nothing to do here
					break;
				case PLAYER_START_GAME:
					player.startGame();
					break;
				case PLAYER_GET_DECK:
					Deck deck = player.getDeck();
					response = new RespondMessage(Method.PLAYER_GET_DECK, serializer.packDeck(deck));
					this.clientBorder.getClient().send(response);
					break;
				case PLAYER_UPDATE_GAMEMODEL:
					GameModelUpdate gameModel;
					gameModel = serializer.unpackGameModelUpdate(qMessage.getParameters().get(0));
					sound = serializer.unpackString(qMessage.getParameters().get(1));
					this.player.playerUpdatesGameModel(gameModel, sound);
					break;
				default:
					throw new IOException("Wrong Method type at ClientListener: " + qMessage.getMethod());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (m instanceof RespondMessage) {
			RespondMessage rMessage = (RespondMessage) m;
			this.clientBorder.setRespondMessage(rMessage);
		}
	}
}
