package model.scripting.baseEdition;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.Database;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.CardType;
import model.enums.Color;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00076_PokZuechter extends TrainerCardScript {

	public Script_00076_PokZuechter(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		List<Card> handCards = gameModel.getPosition(ownHand()).getCards();

		for (int i = 0; i < handCards.size(); i++) {
			Card c = handCards.get(i);
			if (c.getCardType().equals(CardType.STAGE2POKEMON)) {
				PokemonCard stage1Pokemon = (PokemonCard) Database.searchForCardName(((PokemonCard) c).getEvolvesFrom());
				PokemonCard basicPokemon = (PokemonCard) Database.searchForCardName(stage1Pokemon.getEvolvesFrom());
				if (gameModel.getPlayerOnTurn().getColor() == Color.BLUE) {
					if (!gameModel.getPosition(PositionID.BLUE_ACTIVEPOKEMON).isEmpty()
							&& ((PokemonCard) gameModel.getPosition(PositionID.BLUE_ACTIVEPOKEMON).getTopCard()).getName().equals(basicPokemon.getName())
							&& ((PokemonCard) gameModel.getPosition(PositionID.BLUE_ACTIVEPOKEMON).getTopCard()).getPlayedInTurn() < gameModel.getTurnNumber())
						return PlayerAction.PLAY_TRAINER_CARD;
					if (!gameModel.getPosition(PositionID.BLUE_BENCH_1).isEmpty()
							&& ((PokemonCard) gameModel.getPosition(PositionID.BLUE_BENCH_1).getTopCard()).getName().equals(basicPokemon.getName())
							&& ((PokemonCard) gameModel.getPosition(PositionID.BLUE_BENCH_1).getTopCard()).getPlayedInTurn() < gameModel.getTurnNumber())
						return PlayerAction.PLAY_TRAINER_CARD;
					if (!gameModel.getPosition(PositionID.BLUE_BENCH_2).isEmpty()
							&& ((PokemonCard) gameModel.getPosition(PositionID.BLUE_BENCH_2).getTopCard()).getName().equals(basicPokemon.getName())
							&& ((PokemonCard) gameModel.getPosition(PositionID.BLUE_BENCH_2).getTopCard()).getPlayedInTurn() < gameModel.getTurnNumber())
						return PlayerAction.PLAY_TRAINER_CARD;
					if (!gameModel.getPosition(PositionID.BLUE_BENCH_3).isEmpty()
							&& ((PokemonCard) gameModel.getPosition(PositionID.BLUE_BENCH_3).getTopCard()).getName().equals(basicPokemon.getName())
							&& ((PokemonCard) gameModel.getPosition(PositionID.BLUE_BENCH_3).getTopCard()).getPlayedInTurn() < gameModel.getTurnNumber())
						return PlayerAction.PLAY_TRAINER_CARD;
					if (!gameModel.getPosition(PositionID.BLUE_BENCH_4).isEmpty()
							&& ((PokemonCard) gameModel.getPosition(PositionID.BLUE_BENCH_4).getTopCard()).getName().equals(basicPokemon.getName())
							&& ((PokemonCard) gameModel.getPosition(PositionID.BLUE_BENCH_4).getTopCard()).getPlayedInTurn() < gameModel.getTurnNumber())
						return PlayerAction.PLAY_TRAINER_CARD;
					if (!gameModel.getPosition(PositionID.BLUE_BENCH_5).isEmpty()
							&& ((PokemonCard) gameModel.getPosition(PositionID.BLUE_BENCH_5).getTopCard()).getName().equals(basicPokemon.getName())
							&& ((PokemonCard) gameModel.getPosition(PositionID.BLUE_BENCH_5).getTopCard()).getPlayedInTurn() < gameModel.getTurnNumber())
						return PlayerAction.PLAY_TRAINER_CARD;
				} else {
					if (!gameModel.getPosition(PositionID.RED_ACTIVEPOKEMON).isEmpty()
							&& ((PokemonCard) gameModel.getPosition(PositionID.RED_ACTIVEPOKEMON).getTopCard()).getName().equals(basicPokemon.getName())
							&& ((PokemonCard) gameModel.getPosition(PositionID.RED_ACTIVEPOKEMON).getTopCard()).getPlayedInTurn() < gameModel.getTurnNumber())
						return PlayerAction.PLAY_TRAINER_CARD;
					if (!gameModel.getPosition(PositionID.RED_BENCH_1).isEmpty()
							&& ((PokemonCard) gameModel.getPosition(PositionID.RED_BENCH_1).getTopCard()).getName().equals(basicPokemon.getName())
							&& ((PokemonCard) gameModel.getPosition(PositionID.RED_BENCH_1).getTopCard()).getPlayedInTurn() < gameModel.getTurnNumber())
						return PlayerAction.PLAY_TRAINER_CARD;
					if (!gameModel.getPosition(PositionID.RED_BENCH_2).isEmpty()
							&& ((PokemonCard) gameModel.getPosition(PositionID.RED_BENCH_2).getTopCard()).getName().equals(basicPokemon.getName())
							&& ((PokemonCard) gameModel.getPosition(PositionID.RED_BENCH_2).getTopCard()).getPlayedInTurn() < gameModel.getTurnNumber())
						return PlayerAction.PLAY_TRAINER_CARD;
					if (!gameModel.getPosition(PositionID.RED_BENCH_3).isEmpty()
							&& ((PokemonCard) gameModel.getPosition(PositionID.RED_BENCH_3).getTopCard()).getName().equals(basicPokemon.getName())
							&& ((PokemonCard) gameModel.getPosition(PositionID.RED_BENCH_3).getTopCard()).getPlayedInTurn() < gameModel.getTurnNumber())
						return PlayerAction.PLAY_TRAINER_CARD;
					if (!gameModel.getPosition(PositionID.RED_BENCH_4).isEmpty()
							&& ((PokemonCard) gameModel.getPosition(PositionID.RED_BENCH_4).getTopCard()).getName().equals(basicPokemon.getName())
							&& ((PokemonCard) gameModel.getPosition(PositionID.RED_BENCH_4).getTopCard()).getPlayedInTurn() < gameModel.getTurnNumber())
						return PlayerAction.PLAY_TRAINER_CARD;
					if (!gameModel.getPosition(PositionID.RED_BENCH_5).isEmpty()
							&& ((PokemonCard) gameModel.getPosition(PositionID.RED_BENCH_5).getTopCard()).getName().equals(basicPokemon.getName())
							&& ((PokemonCard) gameModel.getPosition(PositionID.RED_BENCH_5).getTopCard()).getPlayedInTurn() < gameModel.getTurnNumber())
						return PlayerAction.PLAY_TRAINER_CARD;
				}
			}
		}
		return null;
	}

	@Override
	public void playFromHand() {
		Player player = this.getCardOwner();
		ArrayList<PositionID> positionList = new ArrayList<PositionID>();

		List<Card> handCards = gameModel.getPosition(ownHand()).getCards();

		for (int i = 0; i < handCards.size(); i++) {
			Card c = handCards.get(i);
			if (c.getCardType().equals(CardType.STAGE2POKEMON)) {
				PokemonCard stage1Pokemon = (PokemonCard) Database.searchForCardName(((PokemonCard) c).getEvolvesFrom());
				PokemonCard basicPokemon = (PokemonCard) Database.searchForCardName(stage1Pokemon.getEvolvesFrom());
				if (player.getColor() == Color.BLUE) {
					if (!gameModel.getPosition(PositionID.BLUE_ACTIVEPOKEMON).isEmpty()
							&& ((PokemonCard) gameModel.getPosition(PositionID.BLUE_ACTIVEPOKEMON).getTopCard()).getName().equals(basicPokemon.getName())
							&& ((PokemonCard) gameModel.getPosition(PositionID.BLUE_ACTIVEPOKEMON).getTopCard()).getPlayedInTurn() < gameModel.getTurnNumber())
						positionList.add(PositionID.BLUE_ACTIVEPOKEMON);
					if (!gameModel.getPosition(PositionID.BLUE_BENCH_1).isEmpty()
							&& ((PokemonCard) gameModel.getPosition(PositionID.BLUE_BENCH_1).getTopCard()).getName().equals(basicPokemon.getName())
							&& ((PokemonCard) gameModel.getPosition(PositionID.BLUE_BENCH_1).getTopCard()).getPlayedInTurn() < gameModel.getTurnNumber())
						positionList.add(PositionID.BLUE_BENCH_1);
					if (!gameModel.getPosition(PositionID.BLUE_BENCH_2).isEmpty()
							&& ((PokemonCard) gameModel.getPosition(PositionID.BLUE_BENCH_2).getTopCard()).getName().equals(basicPokemon.getName())
							&& ((PokemonCard) gameModel.getPosition(PositionID.BLUE_BENCH_2).getTopCard()).getPlayedInTurn() < gameModel.getTurnNumber())
						positionList.add(PositionID.BLUE_BENCH_2);
					if (!gameModel.getPosition(PositionID.BLUE_BENCH_3).isEmpty()
							&& ((PokemonCard) gameModel.getPosition(PositionID.BLUE_BENCH_3).getTopCard()).getName().equals(basicPokemon.getName())
							&& ((PokemonCard) gameModel.getPosition(PositionID.BLUE_BENCH_3).getTopCard()).getPlayedInTurn() < gameModel.getTurnNumber())
						positionList.add(PositionID.BLUE_BENCH_3);
					if (!gameModel.getPosition(PositionID.BLUE_BENCH_4).isEmpty()
							&& ((PokemonCard) gameModel.getPosition(PositionID.BLUE_BENCH_4).getTopCard()).getName().equals(basicPokemon.getName())
							&& ((PokemonCard) gameModel.getPosition(PositionID.BLUE_BENCH_4).getTopCard()).getPlayedInTurn() < gameModel.getTurnNumber())
						positionList.add(PositionID.BLUE_BENCH_4);
					if (!gameModel.getPosition(PositionID.BLUE_BENCH_5).isEmpty()
							&& ((PokemonCard) gameModel.getPosition(PositionID.BLUE_BENCH_5).getTopCard()).getName().equals(basicPokemon.getName())
							&& ((PokemonCard) gameModel.getPosition(PositionID.BLUE_BENCH_5).getTopCard()).getPlayedInTurn() < gameModel.getTurnNumber())
						positionList.add(PositionID.BLUE_BENCH_5);
				} else {
					if (!gameModel.getPosition(PositionID.RED_ACTIVEPOKEMON).isEmpty()
							&& ((PokemonCard) gameModel.getPosition(PositionID.RED_ACTIVEPOKEMON).getTopCard()).getName().equals(basicPokemon.getName())
							&& ((PokemonCard) gameModel.getPosition(PositionID.RED_ACTIVEPOKEMON).getTopCard()).getPlayedInTurn() < gameModel.getTurnNumber())
						positionList.add(PositionID.RED_ACTIVEPOKEMON);
					if (!gameModel.getPosition(PositionID.RED_BENCH_1).isEmpty()
							&& ((PokemonCard) gameModel.getPosition(PositionID.RED_BENCH_1).getTopCard()).getName().equals(basicPokemon.getName())
							&& ((PokemonCard) gameModel.getPosition(PositionID.RED_BENCH_1).getTopCard()).getPlayedInTurn() < gameModel.getTurnNumber())
						positionList.add(PositionID.RED_BENCH_1);
					if (!gameModel.getPosition(PositionID.RED_BENCH_2).isEmpty()
							&& ((PokemonCard) gameModel.getPosition(PositionID.RED_BENCH_2).getTopCard()).getName().equals(basicPokemon.getName())
							&& ((PokemonCard) gameModel.getPosition(PositionID.RED_BENCH_2).getTopCard()).getPlayedInTurn() < gameModel.getTurnNumber())
						positionList.add(PositionID.RED_BENCH_2);
					if (!gameModel.getPosition(PositionID.RED_BENCH_3).isEmpty()
							&& ((PokemonCard) gameModel.getPosition(PositionID.RED_BENCH_3).getTopCard()).getName().equals(basicPokemon.getName())
							&& ((PokemonCard) gameModel.getPosition(PositionID.RED_BENCH_3).getTopCard()).getPlayedInTurn() < gameModel.getTurnNumber())
						positionList.add(PositionID.RED_BENCH_3);
					if (!gameModel.getPosition(PositionID.RED_BENCH_4).isEmpty()
							&& ((PokemonCard) gameModel.getPosition(PositionID.RED_BENCH_4).getTopCard()).getName().equals(basicPokemon.getName())
							&& ((PokemonCard) gameModel.getPosition(PositionID.RED_BENCH_4).getTopCard()).getPlayedInTurn() < gameModel.getTurnNumber())
						positionList.add(PositionID.RED_BENCH_4);
					if (!gameModel.getPosition(PositionID.RED_BENCH_5).isEmpty()
							&& ((PokemonCard) gameModel.getPosition(PositionID.RED_BENCH_5).getTopCard()).getName().equals(basicPokemon.getName())
							&& ((PokemonCard) gameModel.getPosition(PositionID.RED_BENCH_5).getTopCard()).getPlayedInTurn() < gameModel.getTurnNumber())
						positionList.add(PositionID.RED_BENCH_5);
				}
			}
		}
		PositionID chosenPosition = player.playerChoosesPositions(positionList, 1, true, "Choose a Pokemon to evolve!").get(0);
		PokemonCard chosenBasicPokemon = (PokemonCard) gameModel.getPosition(chosenPosition).getTopCard();
		ArrayList<Card> stage2Cards = new ArrayList<Card>();
		for (int i = 0; i < handCards.size(); i++) {
			Card c = handCards.get(i);
			if (c.getCardType().equals(CardType.STAGE2POKEMON)) {
				PokemonCard stage1Pokemon = (PokemonCard) Database.searchForCardName(((PokemonCard) c).getEvolvesFrom());
				PokemonCard basicPokemon = (PokemonCard) Database.searchForCardName(stage1Pokemon.getEvolvesFrom());
				if (basicPokemon.getName().equals(chosenBasicPokemon.getName()))
					stage2Cards.add(c);
			}
		}
		Card c = player.playerChoosesCards(stage2Cards, 1, true, "Choose a stage2 pokemon!").get(0);
		gameModel.getAttackAction().evolvePokemon(chosenPosition, c.getGameID());
		
		// Discard trainer card:
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID());
	}
}
