package model.database;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import network.client.Account;
import network.client.Player;
import model.enums.Edition;
import model.enums.Element;
import model.enums.PokemonCondition;

import com.jme3.asset.TextureKey;

import arenaMode.model.ArenaFighterCode;
import editor.cardeditor.CardEditorModel;

public class Database {

	public static ArrayList<Edition> editions = new ArrayList<Edition>();
	public static ArrayList<Card> cards = (new CardEditorModel()).getAllCards();
	public static HashMap<String, TextureKey> cardTextures;
	/** For the card editor */
	public static HashMap<String, TextureKey> cardThumbnailTextures;
	/** For ingame pokemon in the arena */
	public static HashMap<String, TextureKey> pokemonThumbnailTextures;
	public static HashMap<String, TextureKey> assetTextures;
	static List<Player> BOT_LIST = new ArrayList<Player>();
	public static final String ACCOUNT_FOLDER = "data/accounts/";
	public static final String BOT_FOLDER = "data/bots/";
	public static final String CARD_THUMBNAIL_PATH = "/cards/cardThumbnails/";
	public static final String POKEMON_THUMBNAIL_PATH = "/cards/pokemonThumbnails/";
	static final String CARD_BACK_PATH = "/cards/cardBack.jpg";

	public static TextureKey getTextureKey(String cardID) {
		return cardTextures.get(cardID);
	}

	public static TextureKey getCardThumbnailKey(String cardID) {
		return cardThumbnailTextures.get(cardID);
	}

	public static TextureKey getPokemonThumbnailKey(String cardID) {
		TextureKey texture = pokemonThumbnailTextures.get(cardID);
		return texture;
	}

	public static TextureKey getAssetKey(String name) {
		return assetTextures.get(name);
	}

	/**
	 * Sucht in der Datenbank nach der Karte mit übergebener ID und gibt diese zurück. Gibt null zurück, falls die Karte sich nicht in der Datenbank befindet.
	 * 
	 * @param id
	 *            ID der gesuchten Karte
	 * @return gesuchte Karte
	 */
	public static Card createCard(String id) {
		if (id.equals("00000"))
			return new Card();
		Card c = cards.get(Integer.parseInt(id) - 1);
		if (c instanceof PokemonCard) {
			PokemonCard p = new PokemonCard();
			p.setCardId(c.getCardId());
			p.setName(c.getName());
			p.setImagePath(c.getImagePath());
			p.setCardType(c.getCardType());
			p.setEdition(c.getEdition());
			p.setRarity(c.getRarity());
			p.setHitpoints(((PokemonCard) c).getHitpoints());
			p.setEvolvesFrom(((PokemonCard) c).getEvolvesFrom());
			p.setElement(((PokemonCard) c).getElement());
			p.setWeakness(((PokemonCard) c).getWeakness());
			p.setResistance(((PokemonCard) c).getResistance());
			p.setRetreatCosts(((PokemonCard) c).getRetreatCosts());
			p.setAttackNames(((PokemonCard) c).getAttackNames());
			return p;
		} else if (c instanceof TrainerCard) {
			TrainerCard p = new TrainerCard();
			p.setCardId(c.getCardId());
			p.setName(c.getName());
			p.setImagePath(c.getImagePath());
			p.setCardType(c.getCardType());
			p.setEdition(c.getEdition());
			p.setRarity(c.getRarity());
			p.setStadiumCard(((TrainerCard) c).isStadiumCard());
			return p;
		} else {
			EnergyCard p = new EnergyCard();
			p.setCardId(c.getCardId());
			p.setName(c.getName());
			p.setImagePath(c.getImagePath());
			p.setCardType(c.getCardType());
			p.setEdition(c.getEdition());
			p.setRarity(c.getRarity());
			p.setBasisEnergy(((EnergyCard) c).isBasisEnergy());
			p.setProvidedEnergy(((EnergyCard) c).getProvidedEnergy());
			return p;
		}
	}

	public static Card searchForCardName(String name) {
		for (int i = 0; i < cards.size(); i++)
			if (cards.get(i).getName().equals(name))
				return createCard(cards.get(i).getCardId());
		return null;
	}

	public static void init() {
		// Init card database:
		CardEditorModel c = new CardEditorModel();
		try {
			c.readFromDatabaseFile(new File("data/database.xml"));
			cards = c.getAllCards();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Init account list:
		BOT_LIST = Account.readFromDatabaseFile(BOT_FOLDER);
		// Init card textures:
		loadTextures();
		// Init assets:
		loadAssets();
	}

	/**
	 * For each card created in the init()-method, the respective texture is loaded and stored in a ArrayList.
	 */
	private static void loadTextures() {
		cardTextures = new HashMap<String, TextureKey>();
		pokemonThumbnailTextures = new HashMap<String, TextureKey>();
		cardThumbnailTextures = new HashMap<String, TextureKey>();
		TextureKey back = new TextureKey(CARD_BACK_PATH);
		back.setGenerateMips(false);
		cardTextures.put("00000", back);

		TextureKey thumb = new TextureKey(CARD_THUMBNAIL_PATH + "00000.jpg");
		thumb.setGenerateMips(false);
		cardThumbnailTextures.put("00000", thumb);

		for (int i = 0; i < cards.size(); i++) {
			Card c = cards.get(i);
			TextureKey texture = new TextureKey(c.getImagePath());
			texture.setGenerateMips(false);
			cardTextures.put(cards.get(i).getCardId(), texture);

			if (c instanceof PokemonCard || c.getCardId().equals("00070") || c.getCardId().equals("00199") || (c instanceof TrainerCard && ((TrainerCard) c).isStadiumCard())) {
				TextureKey thumbnail = new TextureKey(POKEMON_THUMBNAIL_PATH + c.getCardId() + ".jpg");
				thumbnail.setGenerateMips(false);
				pokemonThumbnailTextures.put(cards.get(i).getCardId(), thumbnail);
			}
			if (!c.getEdition().equals(Edition.TOKEN)) {
				TextureKey thumbnail = new TextureKey(CARD_THUMBNAIL_PATH + c.getCardId() + ".jpg");
				thumbnail.setGenerateMips(false);
				cardThumbnailTextures.put(cards.get(i).getCardId(), thumbnail);
			}
		}
	}

	private static void loadAssets() {
		assetTextures = new HashMap<String, TextureKey>();

		TextureKey attack = new TextureKey("tilesets/other/attack_enabled.png");
		attack.setGenerateMips(false);
		assetTextures.put("attack", attack);

		TextureKey swap = new TextureKey("tilesets/other/swap_enabled.png");
		swap.setGenerateMips(false);
		assetTextures.put("swap", swap);

		TextureKey pokemonPower = new TextureKey("tilesets/other/pokemonPower_enabled.png");
		pokemonPower.setGenerateMips(false);
		assetTextures.put("pokemonPower", pokemonPower);

		TextureKey stadium = new TextureKey("tilesets/other/stadium_enabled.png");
		stadium.setGenerateMips(false);
		assetTextures.put("stadium", stadium);

		TextureKey win = new TextureKey("tilesets/windows/win.png");
		win.setGenerateMips(false);
		assetTextures.put("win", win);

		TextureKey lose = new TextureKey("tilesets/windows/lose.png");
		lose.setGenerateMips(false);
		assetTextures.put("lose", lose);

		TextureKey bg = new TextureKey("tilesets/backgrounds/bg.jpg");
		bg.setGenerateMips(false);
		assetTextures.put("background", bg);

		TextureKey asset = new TextureKey("/tilesets/other/hp_filled.png");
		asset.setGenerateMips(false);
		assetTextures.put("hp_filled", asset);

		TextureKey asset2 = new TextureKey("/tilesets/other/hp_empty.png");
		asset2.setGenerateMips(false);
		assetTextures.put("hp_empty", asset2);

		TextureKey windowBgFull = new TextureKey("/tilesets/windows/WindowBG.png");
		windowBgFull.setGenerateMips(false);
		assetTextures.put("window_bg", windowBgFull);

		TextureKey logo = new TextureKey("/tilesets/backgrounds/logo.png");
		logo.setGenerateMips(false);
		assetTextures.put("logo", logo);

		TextureKey panelBG = new TextureKey("/tilesets/windows/panel.png");
		panelBG.setGenerateMips(false);
		assetTextures.put("panel_bg", panelBG);

		TextureKey windowBgTop = new TextureKey("/tilesets/windows/label_bg_top.png");
		windowBgTop.setGenerateMips(false);
		assetTextures.put("window_bg_top", windowBgTop);

		TextureKey windowBgBot = new TextureKey("/tilesets/windows/label_bg_bottom.png");
		windowBgBot.setGenerateMips(false);
		assetTextures.put("window_bg_bot", windowBgBot);

		TextureKey buttonBgNormal = new TextureKey("/tilesets/buttons/Button.png");
		buttonBgNormal.setGenerateMips(false);
		assetTextures.put("button_normal", buttonBgNormal);

		TextureKey buttonBgOver = new TextureKey("/tilesets/buttons/ButtonOver.png");
		buttonBgOver.setGenerateMips(false);
		assetTextures.put("button_over", buttonBgOver);

		TextureKey buttonBgPressed = new TextureKey("/tilesets/buttons/ButtonPressed.png");
		buttonBgPressed.setGenerateMips(false);
		assetTextures.put("button_pressed", buttonBgPressed);

		TextureKey surrender = new TextureKey("/tilesets/other/surrender.png");
		surrender.setGenerateMips(false);
		assetTextures.put("surrender", surrender);

		TextureKey lightning = new TextureKey("/tilesets/elements/elektro.png");
		lightning.setGenerateMips(false);
		assetTextures.put(Element.LIGHTNING.toString(), lightning);
		TextureKey fire = new TextureKey("/tilesets/elements/feuer.png");
		fire.setGenerateMips(false);
		assetTextures.put(Element.FIRE.toString(), fire);
		TextureKey rock = new TextureKey("/tilesets/elements/kampf.png");
		rock.setGenerateMips(false);
		assetTextures.put(Element.ROCK.toString(), rock);
		TextureKey colorless = new TextureKey("/tilesets/elements/normal.png");
		colorless.setGenerateMips(false);
		assetTextures.put(Element.COLORLESS.toString(), colorless);
		TextureKey grass = new TextureKey("/tilesets/elements/pflanze.png");
		grass.setGenerateMips(false);
		assetTextures.put(Element.GRASS.toString(), grass);
		TextureKey psychic = new TextureKey("/tilesets/elements/psycho.png");
		psychic.setGenerateMips(false);
		assetTextures.put(Element.PSYCHIC.toString(), psychic);
		TextureKey water = new TextureKey("/tilesets/elements/wasser.png");
		water.setGenerateMips(false);
		assetTextures.put(Element.WATER.toString(), water);
		TextureKey rainbow = new TextureKey("/tilesets/elements/rainbow.png");
		rainbow.setGenerateMips(false);
		assetTextures.put(Element.RAINBOW.toString(), rainbow);

		TextureKey trainerSymbol = new TextureKey("/tilesets/other/trainer.png");
		trainerSymbol.setGenerateMips(false);
		assetTextures.put("Trainer", trainerSymbol);
		TextureKey energySymbol = new TextureKey("/tilesets/other/energy.png");
		energySymbol.setGenerateMips(false);
		assetTextures.put("Energy", energySymbol);

		// Conditions:
		TextureKey ko = new TextureKey("/tilesets/conditions/ko.png");
		ko.setGenerateMips(false);
		assetTextures.put(PokemonCondition.KNOCKOUT.toString(), ko);

		TextureKey paralyzed = new TextureKey("/tilesets/conditions/paralysiert.png");
		paralyzed.setGenerateMips(false);
		assetTextures.put(PokemonCondition.PARALYZED.toString(), paralyzed);

		TextureKey sleeping = new TextureKey("/tilesets/conditions/schlafend.png");
		sleeping.setGenerateMips(false);
		assetTextures.put(PokemonCondition.ASLEEP.toString(), sleeping);

		TextureKey confused = new TextureKey("/tilesets/conditions/verwirrt.png");
		confused.setGenerateMips(false);
		assetTextures.put(PokemonCondition.CONFUSED.toString(), confused);

		TextureKey poisoned = new TextureKey("/tilesets/conditions/vergiftet.gif");
		poisoned.setGenerateMips(false);
		assetTextures.put(PokemonCondition.POISONED.toString(), poisoned);

		TextureKey powerUp = new TextureKey("/tilesets/conditions/powerup.png");
		powerUp.setGenerateMips(false);
		assetTextures.put(PokemonCondition.DAMAGEINCREASE10.toString(), powerUp);

		TextureKey harden20 = new TextureKey("/tilesets/conditions/harden20.png");
		harden20.setGenerateMips(false);
		assetTextures.put(PokemonCondition.HARDEN20.toString(), harden20);

		TextureKey harden30 = new TextureKey("/tilesets/conditions/harden30.png");
		harden30.setGenerateMips(false);
		assetTextures.put(PokemonCondition.HARDEN30.toString(), harden30);

		TextureKey blind = new TextureKey("/tilesets/conditions/blind.png");
		blind.setGenerateMips(false);
		assetTextures.put(PokemonCondition.BLIND.toString(), blind);

		TextureKey no_damage = new TextureKey("/tilesets/conditions/no_damage.png");
		no_damage.setGenerateMips(false);
		assetTextures.put(PokemonCondition.NO_DAMAGE.toString(), no_damage);

		TextureKey destiny = new TextureKey("/tilesets/conditions/destiny.png");
		destiny.setGenerateMips(false);
		assetTextures.put(PokemonCondition.DESTINY.toString(), destiny);

		TextureKey invulnerable = new TextureKey("/tilesets/conditions/invulnerable.png");
		invulnerable.setGenerateMips(false);
		assetTextures.put(PokemonCondition.INVULNERABLE.toString(), invulnerable);

		TextureKey retaliation = new TextureKey("/tilesets/conditions/retaliation.png");
		retaliation.setGenerateMips(false);
		assetTextures.put(PokemonCondition.RETALIATION.toString(), retaliation);

		TextureKey ppBlock = new TextureKey("/tilesets/conditions/ppBlock.png");
		ppBlock.setGenerateMips(false);
		assetTextures.put(PokemonCondition.POKEMON_POWER_BLOCK.toString(), ppBlock);

		TextureKey brocksProtection = new TextureKey("/tilesets/conditions/brocksProtection.jpg");
		brocksProtection.setGenerateMips(false);
		assetTextures.put(PokemonCondition.BROCKS_PROTECTION.toString(), brocksProtection);

		TextureKey noEnergy = new TextureKey("/tilesets/conditions/noEnergy.png");
		noEnergy.setGenerateMips(false);
		assetTextures.put(PokemonCondition.NO_ENERGY.toString(), noEnergy);

		TextureKey overgrowth = new TextureKey("/decks/overgrowth.jpg");
		overgrowth.setGenerateMips(false);
		assetTextures.put("overgrowth", overgrowth);

		TextureKey zapp = new TextureKey("/decks/zapp.png");
		zapp.setGenerateMips(false);
		assetTextures.put("zapp!", zapp);

		TextureKey brushfire = new TextureKey("/decks/brushfire.png");
		brushfire.setGenerateMips(false);
		assetTextures.put("brushfire", brushfire);

		TextureKey blackout = new TextureKey("/decks/blackout.png");
		blackout.setGenerateMips(false);
		assetTextures.put("blackout", blackout);

		TextureKey lightningBug = new TextureKey("/decks/lightning_bug.jpg");
		lightningBug.setGenerateMips(false);
		assetTextures.put("lightningBug", lightningBug);

		TextureKey aerger = new TextureKey("/decks/aerger.jpg");
		aerger.setGenerateMips(false);
		assetTextures.put("aerger", aerger);

		TextureKey verwuestung = new TextureKey("/decks/verwuestung.jpg");
		verwuestung.setGenerateMips(false);
		assetTextures.put("verwuestung", verwuestung);

		TextureKey baseEdition = new TextureKey("/tilesets/editions/baseLogo.png");
		baseEdition.setGenerateMips(false);
		assetTextures.put(Edition.BASE.toString(), baseEdition);

		TextureKey jungleEdition = new TextureKey("/tilesets/editions/jungleLogo.png");
		jungleEdition.setGenerateMips(false);
		assetTextures.put(Edition.JUNGLE.toString(), jungleEdition);

		TextureKey fossilEdition = new TextureKey("/tilesets/editions/fossilLogo.png");
		fossilEdition.setGenerateMips(false);
		assetTextures.put(Edition.FOSSIL.toString(), fossilEdition);

		TextureKey rocketEdition = new TextureKey("/tilesets/editions/rocketLogo.png");
		rocketEdition.setGenerateMips(false);
		assetTextures.put(Edition.ROCKET.toString(), rocketEdition);

		TextureKey brockEdition = new TextureKey("/tilesets/editions/brockLogo.png");
		brockEdition.setGenerateMips(false);
		assetTextures.put(Edition.BROCK.toString(), brockEdition);

		TextureKey mistyEdition = new TextureKey("/tilesets/editions/mistyLogo.png");
		mistyEdition.setGenerateMips(false);
		assetTextures.put(Edition.MISTY.toString(), mistyEdition);

		TextureKey ltSurgeEdition = new TextureKey("/tilesets/editions/LtSurgeLogo.png");
		ltSurgeEdition.setGenerateMips(false);
		assetTextures.put(Edition.LT_SURGE.toString(), ltSurgeEdition);

		TextureKey erikaEdition = new TextureKey("/tilesets/editions/erikaLogo.png");
		erikaEdition.setGenerateMips(false);
		assetTextures.put(Edition.ERIKA.toString(), erikaEdition);

		TextureKey grassChopper = new TextureKey("/decks/grassChopper.jpg");
		grassChopper.setGenerateMips(false);
		assetTextures.put("grassChopper", grassChopper);

		TextureKey kraftreserve = new TextureKey("/decks/kraftreserve.jpg");
		kraftreserve.setGenerateMips(false);
		assetTextures.put("kraftreserve", kraftreserve);

		TextureKey psychOut = new TextureKey("/decks/psychOut.jpg");
		psychOut.setGenerateMips(false);
		assetTextures.put("psychOut", psychOut);

		TextureKey wasserSchwall = new TextureKey("/decks/wasserSchwall.jpg");
		wasserSchwall.setGenerateMips(false);
		assetTextures.put("wasserSchwall", wasserSchwall);

		TextureKey hotWater = new TextureKey("/decks/hotWater.jpg");
		hotWater.setGenerateMips(false);
		assetTextures.put("hotWater", hotWater);

		TextureKey baseBot = new TextureKey("/decks/basic_bot.jpg");
		baseBot.setGenerateMips(false);
		assetTextures.put("baseBot", baseBot);

		TextureKey leibwaechter = new TextureKey("/decks/Leibwächter_TCG.jpg");
		leibwaechter.setGenerateMips(false);
		assetTextures.put("leibwächter", leibwaechter);

		TextureKey schlossUndRiegel = new TextureKey("/decks/Schloss_und_Riegel_TCG.jpg");
		schlossUndRiegel.setGenerateMips(false);
		assetTextures.put("schlossUndRiegel", schlossUndRiegel);

		TextureKey mamoriaArena = new TextureKey("/tilesets/arenas/mamoriaArena.jpg");
		mamoriaArena.setGenerateMips(false);
		assetTextures.put("Pewter City Gym", mamoriaArena);

		TextureKey azuriaArena = new TextureKey("/tilesets/arenas/azuriaArena.jpg");
		azuriaArena.setGenerateMips(false);
		assetTextures.put("Cerulean City Gym", azuriaArena);

		TextureKey oraniaArena = new TextureKey("/tilesets/arenas/oraniaArena.jpg");
		oraniaArena.setGenerateMips(false);
		assetTextures.put("Vermilion City Gym", oraniaArena);

		TextureKey prismaniaArena = new TextureKey("/tilesets/arenas/prismaniaArena.jpg");
		prismaniaArena.setGenerateMips(false);
		assetTextures.put("Celadon City Gym", prismaniaArena);

		TextureKey fuchsaniaArena = new TextureKey("/tilesets/arenas/fuchsaniaArena.png");
		fuchsaniaArena.setGenerateMips(false);
		assetTextures.put("Fuchsia City Gym", fuchsaniaArena);

		TextureKey saffroniaArena = new TextureKey("/tilesets/arenas/saffroniaArena.jpg");
		saffroniaArena.setGenerateMips(false);
		assetTextures.put("Saffron City Gym", saffroniaArena);

		TextureKey zinnoberinselArena = new TextureKey("/tilesets/arenas/zinnoberinselArena.jpg");
		zinnoberinselArena.setGenerateMips(false);
		assetTextures.put("Cinnabar City Gym", zinnoberinselArena);

		TextureKey vertaniaArena = new TextureKey("/tilesets/arenas/vertaniaArena.jpg");
		vertaniaArena.setGenerateMips(false);
		assetTextures.put("Viridian City Gym", vertaniaArena);

		TextureKey redCharacter = new TextureKey("/tilesets/characters/Red.png");
		redCharacter.setGenerateMips(false);
		assetTextures.put(ArenaFighterCode.MAMORIA_RED.toString(), redCharacter);
		TextureKey redCharacterThumb = new TextureKey("/tilesets/characters/Red_thumb.png");
		redCharacterThumb.setGenerateMips(false);
		assetTextures.put(ArenaFighterCode.MAMORIA_RED.toString() + "THUMB", redCharacterThumb);

		TextureKey brendanCharacter = new TextureKey("/tilesets/characters/Brendan.png");
		brendanCharacter.setGenerateMips(false);
		assetTextures.put(ArenaFighterCode.MAMORIA_BRENDAN.toString(), brendanCharacter);
		TextureKey brendanCharacterThumb = new TextureKey("/tilesets/characters/Brendan_thumb.png");
		brendanCharacterThumb.setGenerateMips(false);
		assetTextures.put(ArenaFighterCode.MAMORIA_BRENDAN.toString() + "THUMB", brendanCharacterThumb);

		TextureKey rockoCharacter = new TextureKey("/tilesets/characters/Rocko.png");
		rockoCharacter.setGenerateMips(false);
		assetTextures.put(ArenaFighterCode.MAMORIA_BROCK.toString(), rockoCharacter);
		TextureKey rockoCharacterThumb = new TextureKey("/tilesets/characters/Rocko_thumb.png");
		rockoCharacterThumb.setGenerateMips(false);
		assetTextures.put(ArenaFighterCode.MAMORIA_BROCK.toString() + "THUMB", rockoCharacterThumb);

		TextureKey lyraCharacter = new TextureKey("/tilesets/characters/Lyra.png");
		lyraCharacter.setGenerateMips(false);
		assetTextures.put(ArenaFighterCode.AZURIA_LYRA.toString(), lyraCharacter);
		TextureKey lyraCharacterThumb = new TextureKey("/tilesets/characters/Lyra_thumb.png");
		lyraCharacterThumb.setGenerateMips(false);
		assetTextures.put(ArenaFighterCode.AZURIA_LYRA.toString() + "THUMB", lyraCharacterThumb);

		TextureKey mayCharacter = new TextureKey("/tilesets/characters/May.png");
		mayCharacter.setGenerateMips(false);
		assetTextures.put(ArenaFighterCode.AZURIA_MAY.toString(), mayCharacter);
		TextureKey mayCharacterThumb = new TextureKey("/tilesets/characters/May_thumb.png");
		mayCharacterThumb.setGenerateMips(false);
		assetTextures.put(ArenaFighterCode.AZURIA_MAY.toString() + "THUMB", mayCharacterThumb);

		TextureKey mistyCharacter = new TextureKey("/tilesets/characters/Misty.png");
		mistyCharacter.setGenerateMips(false);
		assetTextures.put(ArenaFighterCode.AZURIA_MISTY.toString(), mistyCharacter);
		TextureKey mistyCharacterThumb = new TextureKey("/tilesets/characters/Misty_thumb.png");
		mistyCharacterThumb.setGenerateMips(false);
		assetTextures.put(ArenaFighterCode.AZURIA_MISTY.toString() + "THUMB", mistyCharacterThumb);

		TextureKey leafCharacter = new TextureKey("/tilesets/characters/Leaf.png");
		leafCharacter.setGenerateMips(false);
		assetTextures.put(ArenaFighterCode.ORANIA_LEAF.toString(), leafCharacter);
		TextureKey leafCharacterThumb = new TextureKey("/tilesets/characters/Leaf_thumb.png");
		leafCharacterThumb.setGenerateMips(false);
		assetTextures.put(ArenaFighterCode.ORANIA_LEAF.toString() + "THUMB", leafCharacterThumb);

		TextureKey nateCharacter = new TextureKey("/tilesets/characters/Nate.png");
		nateCharacter.setGenerateMips(false);
		assetTextures.put(ArenaFighterCode.ORANIA_NATE.toString(), nateCharacter);
		TextureKey nateCharacterThumb = new TextureKey("/tilesets/characters/Nate_thumb.png");
		nateCharacterThumb.setGenerateMips(false);
		assetTextures.put(ArenaFighterCode.ORANIA_NATE.toString() + "THUMB", nateCharacterThumb);

		TextureKey ltSurgeCharacter = new TextureKey("/tilesets/characters/MajorBob.png");
		ltSurgeCharacter.setGenerateMips(false);
		assetTextures.put(ArenaFighterCode.ORANIA_LTSURGE.toString(), ltSurgeCharacter);
		TextureKey ltSurgeCharacterThumb = new TextureKey("/tilesets/characters/MajorBob_thumb.png");
		ltSurgeCharacterThumb.setGenerateMips(false);
		assetTextures.put(ArenaFighterCode.ORANIA_LTSURGE.toString() + "THUMB", ltSurgeCharacterThumb);

		TextureKey rosaCharacter = new TextureKey("/tilesets/characters/Rosa.png");
		rosaCharacter.setGenerateMips(false);
		assetTextures.put(ArenaFighterCode.PRISMANIA_ROSA.toString(), rosaCharacter);
		TextureKey rosaCharacterThumb = new TextureKey("/tilesets/characters/Rosa_thumb.png");
		rosaCharacterThumb.setGenerateMips(false);
		assetTextures.put(ArenaFighterCode.PRISMANIA_ROSA.toString() + "THUMB", rosaCharacterThumb);

		TextureKey serenaCharacter = new TextureKey("/tilesets/characters/Serena.png");
		serenaCharacter.setGenerateMips(false);
		assetTextures.put(ArenaFighterCode.PRISMANIA_SERENA.toString(), serenaCharacter);
		TextureKey serenaCharacterThumb = new TextureKey("/tilesets/characters/Serena_thumb.png");
		serenaCharacterThumb.setGenerateMips(false);
		assetTextures.put(ArenaFighterCode.PRISMANIA_SERENA.toString() + "THUMB", serenaCharacterThumb);

		TextureKey erikaCharacter = new TextureKey("/tilesets/characters/Erika.png");
		erikaCharacter.setGenerateMips(false);
		assetTextures.put(ArenaFighterCode.PRISMANIA_ERIKA.toString(), erikaCharacter);
		TextureKey erikaCharacterThumb = new TextureKey("/tilesets/characters/Erika_thumb.png");
		erikaCharacterThumb.setGenerateMips(false);
		assetTextures.put(ArenaFighterCode.PRISMANIA_ERIKA.toString() + "THUMB", erikaCharacterThumb);
	}

	public static Player getBot(String name) {
		for (Player player : BOT_LIST)
			if (player.getName().equals(name))
				return player;
		return null;
	}

	/**
	 * Creates an instance of a full card library and returns it.
	 * 
	 * @return
	 */
	public static CardLibrary getFullCardLibrary() {
		List<String> cardList = new ArrayList<>();
		for (Card c : cards) {
			if (!c.getEdition().equals(Edition.TOKEN))
				for (int i = 0; i < 60; i++)
					cardList.add(c.getCardId());
		}
		CardLibrary lib = new CardLibrary(cardList);
		return lib;
	}

	/**
	 * Reads the account folder for player files. Returns the player if only one player file is contained in the account folder. Returns null, if no account is contained in the
	 * folder.
	 * 
	 * @return
	 * @throws IOException
	 *             when the folder contains multiple account files
	 */
	public static Player readAccountFolder() throws IOException {
		File dir = new File(ACCOUNT_FOLDER);

		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".xml");
			}
		});

		if (files.length == 0)
			return null;

		if (files.length > 1)
			throw new IOException("Error: Multiple account files contained in folder!");

		Player player = Account.loadAccount(files[0].getName());
		return player;
	}
}