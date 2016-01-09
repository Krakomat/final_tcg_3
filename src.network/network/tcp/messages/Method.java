package network.tcp.messages;

import com.jme3.network.serializing.Serializable;

/**
 * Enumerates all the methods on the client and the server.
 * 
 * @author Michael
 *
 */
@Serializable
public enum Method {
	/* Methods from Player interface: */
	PLAYER_START_GAME, PLAYER_GET_DECK, PLAYER_CHOOSE_CARDS, PLAYER_CHOOSE_POSITION, PLAYER_CHOOSE_ELEMENT, PLAYER_CHOOSE_ATTACK, PLAYER_ANSWERS_YES_NO,
	PLAYER_PAY_ENERGY_COST, PLAYER_REVEIVE_TEXT_MESSAGE, PLAYER_RECEIVE_CARD_MESSAGE, PLAYER_RECEIVE_CARDS_MESSAGE, PLAYER_UPDATE_GAMEMODEL, PLAYER_MAKE_MOVE,
	PLAYER_GETCOLOR, PLAYER_SETCOLOR, PLAYER_RECEIVE_GAME_DELETED, PLAYER_DISTRIBUTE_DAMAGE, PLAYER_SETSERVER, PLAYER_RECIEVE_SOUND, PLAYER_RECEIVE_ANIMATION,

	/* Methods from PokemonGameManager interface: */
	SERVER_CONNECT_AS_PLAYER, SERVER_GET_PLAYER_ACTIONS, SERVER_PLAYER_PLAYS_CARD, SERVER_GET_ATTACKS_FOR_POSITION, SERVER_EXECUTE_ATTACK,
	SERVER_GET_POKEPOWER_FOR_POSITION, SERVER_EXECUTE_POKEMON_POWER, SERVER_RETREAT_POKEMON, SERVER_END_TURN, SERVER_GET_GAME_MODEL_FOR_PLAYER, SERVER_SURRENDER,
	SERVER_ACTIVATE_STADIUM;
}
