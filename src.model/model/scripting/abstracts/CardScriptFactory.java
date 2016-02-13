package model.scripting.abstracts;

import model.database.Card;
import model.database.EnergyCard;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.interfaces.PokemonGame;
import model.scripting.baseEdition.*;
import model.scripting.brock.Script_00266_BrocksRhydon;
import model.scripting.brock.Script_00267_Brock;
import model.scripting.brock.Script_00268_BrocksGolem;
import model.scripting.brock.Script_00269_BrocksOnix;
import model.scripting.brock.Script_00270_BrocksRhyhorn;
import model.scripting.brock.Script_00271_BrocksSandslash;
import model.scripting.brock.Script_00272_BrocksZubat;
import model.scripting.brock.Script_00273_BrocksGeodude;
import model.scripting.brock.Script_00274_BrocksGolbat;
import model.scripting.brock.Script_00275_BrocksGraveler;
import model.scripting.brock.Script_00276_BrocksLickitung;
import model.scripting.brock.Script_00277_BrocksGeodude;
import model.scripting.brock.Script_00278_BrocksMankey;
import model.scripting.brock.Script_00279_BrocksMankey;
import model.scripting.brock.Script_00280_BrocksOnix;
import model.scripting.brock.Script_00281_BrocksRhyhorn;
import model.scripting.brock.Script_00282_BrocksSandshrew;
import model.scripting.brock.Script_00283_BrocksSandshrew;
import model.scripting.brock.Script_00284_BrocksZubat;
import model.scripting.brock.Script_00285_BrocksTrainingMethod;
import model.scripting.brock.Script_00286_PewterCityGym;
import model.scripting.brock.Script_00287_BrocksDugtrio;
import model.scripting.brock.Script_00288_BrocksGraveler;
import model.scripting.brock.Script_00289_BrocksPrimeape;
import model.scripting.brock.Script_00290_BrocksSandslash;
import model.scripting.brock.Script_00291_BrocksDiglett;
import model.scripting.brock.Script_00292_BrocksGeodude;
import model.scripting.brock.Script_00293_BrocksProtection;
import model.scripting.erika.Script_00351_ErikasClefable;
import model.scripting.erika.Script_00352_ErikasDragonair;
import model.scripting.erika.Script_00353_ErikasVileplume;
import model.scripting.erika.Script_00354_Erika;
import model.scripting.erika.Script_00355_ErikasClefairy;
import model.scripting.erika.Script_00356_Erikas_Victreebel;
import model.scripting.erika.Script_00357_ErikasDratini;
import model.scripting.erika.Script_00358_ErikasExeggcute;
import model.scripting.erika.Script_00359_ErikasExeggutor;
import model.scripting.erika.Script_00360_ErikasGloom;
import model.scripting.erika.Script_00361_ErikasGloom;
import model.scripting.erika.Script_00362_ErikasOddish;
import model.scripting.erika.Script_00363_ErikasWeepinbell;
import model.scripting.erika.Script_00364_ErikasWeepinbell;
import model.scripting.erika.Script_00365_ErikasBellsprout;
import model.scripting.erika.Script_00366_ErikasBellsprout;
import model.scripting.erika.Script_00367_ErikasExeggcute;
import model.scripting.erika.Script_00368_ErikasOddish;
import model.scripting.erika.Script_00369_ErikasTangela;
import model.scripting.erika.Script_00370_CeladonCityGym;
import model.scripting.erika.Script_00371_ErikasMaids;
import model.scripting.erika.Script_00372_ErikasPerfume;
import model.scripting.erika.Script_00373_GoodManners;
import model.scripting.erika.Script_00374_ErikasVenusaur;
import model.scripting.erika.Script_00375_ErikasBellsprout;
import model.scripting.erika.Script_00376_ErikasBulbasaur;
import model.scripting.erika.Script_00377_ErikasClefairy;
import model.scripting.erika.Script_00378_ErikasIvysaur;
import model.scripting.erika.Script_00379_ErikasJigglypuff;
import model.scripting.erika.Script_00380_ErikasOddish;
import model.scripting.erika.Script_00381_ErikasParas;
import model.scripting.erika.Script_00382_ErikasKindness;
import model.scripting.fossil.Script_00153_Aerodactyl;
import model.scripting.fossil.Script_00154_Articuno;
import model.scripting.fossil.Script_00155_Dragonite;
import model.scripting.fossil.Script_00156_Gengar;
import model.scripting.fossil.Script_00157_Haunter;
import model.scripting.fossil.Script_00158_Hitmonlee;
import model.scripting.fossil.Script_00159_Hypno;
import model.scripting.fossil.Script_00160_Kabutops;
import model.scripting.fossil.Script_00161_Lapras;
import model.scripting.fossil.Script_00162_Magneton;
import model.scripting.fossil.Script_00163_Moltres;
import model.scripting.fossil.Script_00164_Muk;
import model.scripting.fossil.Script_00165_Raichu;
import model.scripting.fossil.Script_00166_Zapdos;
import model.scripting.fossil.Script_00167_Arbok;
import model.scripting.fossil.Script_00168_Cloyster;
import model.scripting.fossil.Script_00169_Gastly;
import model.scripting.fossil.Script_00170_Golbat;
import model.scripting.fossil.Script_00171_Golduck;
import model.scripting.fossil.Script_00172_Golem;
import model.scripting.fossil.Script_00173_Graveler;
import model.scripting.fossil.Script_00174_Kingler;
import model.scripting.fossil.Script_00175_Magmar;
import model.scripting.fossil.Script_00176_Omastar;
import model.scripting.fossil.Script_00177_Sandslash;
import model.scripting.fossil.Script_00178_Seadra;
import model.scripting.fossil.Script_00179_Slowbro;
import model.scripting.fossil.Script_00180_Tentacruel;
import model.scripting.fossil.Script_00181_Weezing;
import model.scripting.fossil.Script_00182_Ekans;
import model.scripting.fossil.Script_00183_Geodude;
import model.scripting.fossil.Script_00184_Grimer;
import model.scripting.fossil.Script_00185_Horsea;
import model.scripting.fossil.Script_00186_Kabuto;
import model.scripting.fossil.Script_00187_Krabby;
import model.scripting.fossil.Script_00188_Omanyte;
import model.scripting.fossil.Script_00189_Psyduck;
import model.scripting.fossil.Script_00190_Shellder;
import model.scripting.fossil.Script_00191_Slowpoke;
import model.scripting.fossil.Script_00192_Tentacool;
import model.scripting.fossil.Script_00193_Zubat;
import model.scripting.fossil.Script_00194_MrFuji;
import model.scripting.fossil.Script_00195_EnergySearch;
import model.scripting.fossil.Script_00196_Gambler;
import model.scripting.fossil.Script_00197_Recycle;
import model.scripting.fossil.Script_00198_MysteriousFossil;
import model.scripting.fossil.Script_00199_FossilToken;
import model.scripting.jungle.Script_00105_Clefable;
import model.scripting.jungle.Script_00106_Electrode;
import model.scripting.jungle.Script_00107_Flareon;
import model.scripting.jungle.Script_00108_Jolteon;
import model.scripting.jungle.Script_00109_Kangaskhan;
import model.scripting.jungle.Script_00110_MrMime;
import model.scripting.jungle.Script_00111_Nidoqueen;
import model.scripting.jungle.Script_00112_Pidgeot;
import model.scripting.jungle.Script_00113_Pinsir;
import model.scripting.jungle.Script_00114_Scyther;
import model.scripting.jungle.Script_00115_Snorlax;
import model.scripting.jungle.Script_00116_Vaporeon;
import model.scripting.jungle.Script_00117_Venomoth;
import model.scripting.jungle.Script_00118_Victreebel;
import model.scripting.jungle.Script_00119_Vileplume;
import model.scripting.jungle.Script_00120_Wigglytuff;
import model.scripting.jungle.Script_00121_Butterfree;
import model.scripting.jungle.Script_00122_Dodrio;
import model.scripting.jungle.Script_00123_Exeggutor;
import model.scripting.jungle.Script_00124_Fearow;
import model.scripting.jungle.Script_00125_Gloom;
import model.scripting.jungle.Script_00126_Lickitung;
import model.scripting.jungle.Script_00127_Marowack;
import model.scripting.jungle.Script_00128_Nidorina;
import model.scripting.jungle.Script_00129_Parasect;
import model.scripting.jungle.Script_00130_Persian;
import model.scripting.jungle.Script_00131_Primeape;
import model.scripting.jungle.Script_00132_Rapidash;
import model.scripting.jungle.Script_00133_Rhydon;
import model.scripting.jungle.Script_00134_Seaking;
import model.scripting.jungle.Script_00135_Tauros;
import model.scripting.jungle.Script_00136_Weepinbell;
import model.scripting.jungle.Script_00137_Bellsprout;
import model.scripting.jungle.Script_00138_Cubone;
import model.scripting.jungle.Script_00139_Eevee;
import model.scripting.jungle.Script_00140_Exeggcute;
import model.scripting.jungle.Script_00141_Goldeen;
import model.scripting.jungle.Script_00142_Jigglypuff;
import model.scripting.jungle.Script_00143_Mankey;
import model.scripting.jungle.Script_00144_Meowth;
import model.scripting.jungle.Script_00145_NidoranF;
import model.scripting.jungle.Script_00146_Oddish;
import model.scripting.jungle.Script_00147_Paras;
import model.scripting.jungle.Script_00148_Pikachu;
import model.scripting.jungle.Script_00149_Rhyhorn;
import model.scripting.jungle.Script_00150_Spearow;
import model.scripting.jungle.Script_00151_Venonat;
import model.scripting.jungle.Script_00152_Pokeball;
import model.scripting.koga.Script_00383_KogasBeedrill;
import model.scripting.koga.Script_00384_KogasDitto;
import model.scripting.koga.Script_00385_Koga;
import model.scripting.koga.Script_00386_KogasArbok;
import model.scripting.koga.Script_00387_KogasMuk;
import model.scripting.koga.Script_00388_KogasPidgeotto;
import model.scripting.koga.Script_00389_KogasGolbat;
import model.scripting.koga.Script_00390_KogasKakuna;
import model.scripting.koga.Script_00391_KogasKoffing;
import model.scripting.koga.Script_00392_KogasPidgey;
import model.scripting.koga.Script_00393_KogasWeezing;
import model.scripting.koga.Script_00394_KogasEkans;
import model.scripting.koga.Script_00395_KogasGrimer;
import model.scripting.koga.Script_00396_KogasKoffing;
import model.scripting.koga.Script_00397_KogasPidgey;
import model.scripting.koga.Script_00398_KogasTangela;
import model.scripting.koga.Script_00399_KogasWeedle;
import model.scripting.koga.Script_00400_KogasZubat;
import model.scripting.koga.Script_00401_FuchsiaCityGym;
import model.scripting.koga.Script_00402_KogasNinjaTrick;
import model.scripting.ltSurge.Script_00326_LtSurgesElectabuzz;
import model.scripting.ltSurge.Script_00327_LtSurgesFearow;
import model.scripting.ltSurge.Script_00328_LtSurgesMagneton;
import model.scripting.ltSurge.Script_00329_LtSurge;
import model.scripting.ltSurge.Script_00330_LtSurgesElectabuzz;
import model.scripting.ltSurge.Script_00331_LtSurgesRaichu;
import model.scripting.ltSurge.Script_00332_LtSurgesMagnemite;
import model.scripting.ltSurge.Script_00333_LtSurgesRaticate;
import model.scripting.ltSurge.Script_00334_LtSurgesSpearow;
import model.scripting.ltSurge.Script_00335_LtSurgesMagnemite;
import model.scripting.ltSurge.Script_00336_LtSurgesPikachu;
import model.scripting.ltSurge.Script_00337_LtSurgesRattata;
import model.scripting.ltSurge.Script_00338_LtSurgesSpearow;
import model.scripting.ltSurge.Script_00339_LtSurgesVoltorb;
import model.scripting.ltSurge.Script_00340_LtSurgesTreaty;
import model.scripting.ltSurge.Script_00341_SecretMission;
import model.scripting.ltSurge.Script_00342_VermillionCityGym;
import model.scripting.ltSurge.Script_00343_LtSurgesRaichu;
import model.scripting.ltSurge.Script_00344_LtSurgesJolteon;
import model.scripting.ltSurge.Script_00345_LtSurgesEevee;
import model.scripting.ltSurge.Script_00346_LtSurgesElectrode;
import model.scripting.ltSurge.Script_00347_LtSurgesRaticate;
import model.scripting.ltSurge.Script_00348_LtSurgesPikachu;
import model.scripting.ltSurge.Script_00349_LtSurgesRattata;
import model.scripting.ltSurge.Script_00350_LtSurgesVoltorb;
import model.scripting.misty.Script_00294_MistysSeadra;
import model.scripting.misty.Script_00295_MistysTentacruel;
import model.scripting.misty.Script_00296_Misty;
import model.scripting.misty.Script_00297_MistysCloyster;
import model.scripting.misty.Script_00298_MistysGoldeen;
import model.scripting.misty.Script_00299_MistysPoliwrath;
import model.scripting.misty.Script_00300_MistysTentacool;
import model.scripting.misty.Script_00301_MistysPoliwhirl;
import model.scripting.misty.Script_00302_MistysPsyduck;
import model.scripting.misty.Script_00303_MistysSeaking;
import model.scripting.misty.Script_00304_MistysStarmie;
import model.scripting.misty.Script_00305_MistysTentacool;
import model.scripting.misty.Script_00306_MistysGoldeen;
import model.scripting.misty.Script_00307_MistysHorsea;
import model.scripting.misty.Script_00308_MistysPoliwag;
import model.scripting.misty.Script_00309_MistysSeel;
import model.scripting.misty.Script_00310_MistysShellder;
import model.scripting.misty.Script_00311_MistysStaryu;
import model.scripting.misty.Script_00312_CelureanCityGym;
import model.scripting.misty.Script_00313_MistysWrath;
import model.scripting.misty.Script_00314_MistysDuel;
import model.scripting.misty.Script_00315_MistysGolduck;
import model.scripting.misty.Script_00316_MistysGyarados;
import model.scripting.misty.Script_00317_MistysDewgong;
import model.scripting.misty.Script_00318_MistysHorsea;
import model.scripting.misty.Script_00319_MistysMagikarp;
import model.scripting.misty.Script_00320_MistysPoliwag;
import model.scripting.misty.Script_00321_MistysPsyduck;
import model.scripting.misty.Script_00322_MistysSeel;
import model.scripting.misty.Script_00323_MistysStaryu;
import model.scripting.misty.Script_00324_MistysWish;
import model.scripting.misty.Script_00325_MistysTears;
import model.scripting.rocket.Script_00200_DarkAlakazam;
import model.scripting.rocket.Script_00201_DarkArbok;
import model.scripting.rocket.Script_00202_DarkBlastoise;
import model.scripting.rocket.Script_00203_DarkCharizard;
import model.scripting.rocket.Script_00204_DarkDragonite;
import model.scripting.rocket.Script_00205_DarkDugtrio;
import model.scripting.rocket.Script_00206_DarkGolbat;
import model.scripting.rocket.Script_00207_DarkGyarados;
import model.scripting.rocket.Script_00208_DarkHypno;
import model.scripting.rocket.Script_00209_DarkMachamp;
import model.scripting.rocket.Script_00210_DarkMagneton;
import model.scripting.rocket.Script_00211_DarkSlowbro;
import model.scripting.rocket.Script_00212_DarkVileplume;
import model.scripting.rocket.Script_00213_DarkWeezing;
import model.scripting.rocket.Script_00214_HereComesTeamRocket;
import model.scripting.rocket.Script_00215_RocketsSneakAttack;
import model.scripting.rocket.Script_00216_RainbowEnergy;
import model.scripting.rocket.Script_00217_DarkCharmeleon;
import model.scripting.rocket.Script_00218_DarkDragonair;
import model.scripting.rocket.Script_00219_DarkElectrode;
import model.scripting.rocket.Script_00220_DarkFlareon;
import model.scripting.rocket.Script_00221_DarkGloom;
import model.scripting.rocket.Script_00222_DarkGolduck;
import model.scripting.rocket.Script_00223_DarkJolteon;
import model.scripting.rocket.Script_00224_DarkKadabra;
import model.scripting.rocket.Script_00225_DarkMachoke;
import model.scripting.rocket.Script_00226_DarkMuk;
import model.scripting.rocket.Script_00227_DarkPersian;
import model.scripting.rocket.Script_00228_DarkPrimeape;
import model.scripting.rocket.Script_00229_DarkRapidash;
import model.scripting.rocket.Script_00230_DarkVaporeon;
import model.scripting.rocket.Script_00231_DarkWartortle;
import model.scripting.rocket.Script_00232_Magikarp;
import model.scripting.rocket.Script_00233_Porygon;
import model.scripting.rocket.Script_00234_Abra;
import model.scripting.rocket.Script_00235_Charmander;
import model.scripting.rocket.Script_00236_DarkRaticate;
import model.scripting.rocket.Script_00237_Diglett;
import model.scripting.rocket.Script_00238_Dratini;
import model.scripting.rocket.Script_00239_Drowzee;
import model.scripting.rocket.Script_00240_Eevee;
import model.scripting.rocket.Script_00241_Ekans;
import model.scripting.rocket.Script_00242_Grimer;
import model.scripting.rocket.Script_00243_Koffing;
import model.scripting.rocket.Script_00244_Machop;
import model.scripting.rocket.Script_00245_Magnemite;
import model.scripting.rocket.Script_00246_Mankey;
import model.scripting.rocket.Script_00247_Meowth;
import model.scripting.rocket.Script_00248_Oddish;
import model.scripting.rocket.Script_00249_Ponyta;
import model.scripting.rocket.Script_00250_Psyduck;
import model.scripting.rocket.Script_00251_Rattata;
import model.scripting.rocket.Script_00252_Slowpoke;
import model.scripting.rocket.Script_00253_Squirtle;
import model.scripting.rocket.Script_00254_Voltorb;
import model.scripting.rocket.Script_00255_Zubat;
import model.scripting.rocket.Script_00256_TheBosssWay;
import model.scripting.rocket.Script_00257_Challenge;
import model.scripting.rocket.Script_00258_Digger;
import model.scripting.rocket.Script_00259_ImposterOaksRevenge;
import model.scripting.rocket.Script_00260_NightlyGarbageRun;
import model.scripting.rocket.Script_00261_GoopGasAttack;
import model.scripting.rocket.Script_00262_Sleep;
import model.scripting.rocket.Script_00263_FullHealEnergy;
import model.scripting.rocket.Script_00264_PotionEnergy;
import model.scripting.rocket.Script_00265_DarkRaichu;

/**
 * Factory, which generates the {@link CardScript} for a specific card.
 * 
 * @author Michael
 *
 */
public class CardScriptFactory {

	/**
	 * Creates a new instance of a CardScriptFactory.
	 * 
	 * @return
	 */
	public static CardScriptFactory getInstance() {
		return new CardScriptFactory();
	}

	/**
	 * Creates the {@link CardScript} for the given cardID, using the given Card and {@link PokemonGame}.
	 * 
	 * @param cardID
	 * @param card
	 * @param gameModel
	 * @return
	 */
	public CardScript createScript(String cardID, Card card, PokemonGame gameModel) {
		switch (cardID) {
		case "00000":
			return new CardScript(card, gameModel) {
				@Override
				public PlayerAction canBePlayedFromHand() {
					return null;
				}

				@Override
				public void playFromHand() {

				}
			};
		case "00001":
			return new Script_00001_Simsala((PokemonCard) card, gameModel);
		case "00002":
			return new Script_00002_Turtok((PokemonCard) card, gameModel);
		case "00003":
			return new Script_00003_Chaneira((PokemonCard) card, gameModel);
		case "00004":
			return new Script_00004_Glurak((PokemonCard) card, gameModel);
		case "00005":
			return new Script_00005_Piepi((PokemonCard) card, gameModel);
		case "00006":
			return new Script_00006_Garados((PokemonCard) card, gameModel);
		case "00007":
			return new Script_00007_Nockchan((PokemonCard) card, gameModel);
		case "00008":
			return new Script_00008_Machomei((PokemonCard) card, gameModel);
		case "00009":
			return new Script_00009_Magneton((PokemonCard) card, gameModel);
		case "00010":
			return new Script_00010_Mewto((PokemonCard) card, gameModel);
		case "00011":
			return new Script_00011_Nidoking((PokemonCard) card, gameModel);
		case "00012":
			return new Script_00012_Vulnona((PokemonCard) card, gameModel);
		case "00013":
			return new Script_00013_Quappo((PokemonCard) card, gameModel);
		case "00014":
			return new Script_00014_Raichu((PokemonCard) card, gameModel);
		case "00015":
			return new Script_00015_Bisaflor((PokemonCard) card, gameModel);
		case "00016":
			return new Script_00016_Zapdos((PokemonCard) card, gameModel);
		case "00017":
			return new Script_00017_Bibor((PokemonCard) card, gameModel);
		case "00018":
			return new Script_00018_Dragonir((PokemonCard) card, gameModel);
		case "00019":
			return new Script_00019_Digdri((PokemonCard) card, gameModel);
		case "00020":
			return new Script_00020_Elektek((PokemonCard) card, gameModel);
		case "00021":
			return new Script_00021_Lektrobal((PokemonCard) card, gameModel);
		case "00022":
			return new Script_00022_Tauboga((PokemonCard) card, gameModel);
		case "00023":
			return new Script_00023_Arkani((PokemonCard) card, gameModel);
		case "00024":
			return new Script_00024_Glutexo((PokemonCard) card, gameModel);
		case "00025":
			return new Script_00025_Jugong((PokemonCard) card, gameModel);
		case "00026":
			return new Script_00026_Dratini((PokemonCard) card, gameModel);
		case "00027":
			return new Script_00027_Porenta((PokemonCard) card, gameModel);
		case "00028":
			return new Script_00028_Fukano((PokemonCard) card, gameModel);
		case "00029":
			return new Script_00029_Alpollo((PokemonCard) card, gameModel);
		case "00030":
			return new Script_00030_Bisaknosp((PokemonCard) card, gameModel);
		case "00031":
			return new Script_00031_Rossana((PokemonCard) card, gameModel);
		case "00032":
			return new Script_00032_Kadabra((PokemonCard) card, gameModel);
		case "00033":
			return new Script_00033_Kokuna((PokemonCard) card, gameModel);
		case "00034":
			return new Script_00034_Maschok((PokemonCard) card, gameModel);
		case "00035":
			return new Script_00035_Karpador((PokemonCard) card, gameModel);
		case "00036":
			return new Script_00036_Magmar((PokemonCard) card, gameModel);
		case "00037":
			return new Script_00037_Nidorino((PokemonCard) card, gameModel);
		case "00038":
			return new Script_00038_Quaputzi((PokemonCard) card, gameModel);
		case "00039":
			return new Script_00039_Porygon((PokemonCard) card, gameModel);
		case "00040":
			return new Script_00040_Rattikarl((PokemonCard) card, gameModel);
		case "00041":
			return new Script_00041_Jurob((PokemonCard) card, gameModel);
		case "00042":
			return new Script_00042_Schillok((PokemonCard) card, gameModel);
		case "00043":
			return new Script_00043_Abra((PokemonCard) card, gameModel);
		case "00044":
			return new Script_00044_Bisasam((PokemonCard) card, gameModel);
		case "00045":
			return new Script_00045_Raupy((PokemonCard) card, gameModel);
		case "00046":
			return new Script_00046_Glumanda((PokemonCard) card, gameModel);
		case "00047":
			return new Script_00047_Digda((PokemonCard) card, gameModel);
		case "00048":
			return new Script_00048_Dodu((PokemonCard) card, gameModel);
		case "00049":
			return new Script_00049_Traumato((PokemonCard) card, gameModel);
		case "00050":
			return new Script_00050_Nebulak((PokemonCard) card, gameModel);
		case "00051":
			return new Script_00051_Smogon((PokemonCard) card, gameModel);
		case "00052":
			return new Script_00052_Machollo((PokemonCard) card, gameModel);
		case "00053":
			return new Script_00053_Magnetilo((PokemonCard) card, gameModel);
		case "00054":
			return new Script_00054_Safcon((PokemonCard) card, gameModel);
		case "00055":
			return new Script_00055_NidoranM((PokemonCard) card, gameModel);
		case "00056":
			return new Script_00056_Onix((PokemonCard) card, gameModel);
		case "00057":
			return new Script_00057_Taubsi((PokemonCard) card, gameModel);
		case "00058":
			return new Script_00058_Pikachu((PokemonCard) card, gameModel);
		case "00059":
			return new Script_00059_Quapsel((PokemonCard) card, gameModel);
		case "00060":
			return new Script_00060_Ponita((PokemonCard) card, gameModel);
		case "00061":
			return new Script_00061_Rattfratz((PokemonCard) card, gameModel);
		case "00062":
			return new Script_00062_Sandan((PokemonCard) card, gameModel);
		case "00063":
			return new Script_00063_Schiggy((PokemonCard) card, gameModel);
		case "00064":
			return new Script_00064_Starmie((PokemonCard) card, gameModel);
		case "00065":
			return new Script_00065_Sterndu((PokemonCard) card, gameModel);
		case "00066":
			return new Script_00066_Tangela((PokemonCard) card, gameModel);
		case "00067":
			return new Script_00067_Voltobal((PokemonCard) card, gameModel);
		case "00068":
			return new Script_00068_Vulpix((PokemonCard) card, gameModel);
		case "00069":
			return new Script_00069_Hornliu((PokemonCard) card, gameModel);
		case "00070":
			return new Script_00070_PiepiPuppe((TrainerCard) card, gameModel);
		case "00071":
			return new Script_00071_Computersuche((TrainerCard) card, gameModel);
		case "00072":
			return new Script_00072_DevolutionSpray((TrainerCard) card, gameModel);
		case "00073":
			return new Script_00073_FalscherProfEich((TrainerCard) card, gameModel);
		case "00074":
			return new Script_00074_Detektor((TrainerCard) card, gameModel);
		case "00075":
			return new Script_00075_Goere((TrainerCard) card, gameModel);
		case "00076":
			return new Script_00076_PokZuechter((TrainerCard) card, gameModel);
		case "00077":
			return new Script_00077_PokHaendler((TrainerCard) card, gameModel);
		case "00078":
			return new Script_00078_Aufwisch((TrainerCard) card, gameModel);
		case "00079":
			return new Script_00079_SupEnergieAbsauger((TrainerCard) card, gameModel);
		case "00080":
			return new Script_00080_Defender((TrainerCard) card, gameModel);
		case "00081":
			return new Script_00081_Energiezugewinnung((TrainerCard) card, gameModel);
		case "00082":
			return new Script_00082_Hyperheiler((TrainerCard) card, gameModel);
		case "00083":
			return new Script_00083_Wartung((TrainerCard) card, gameModel);
		case "00084":
			return new Script_00084_Pluspower((TrainerCard) card, gameModel);
		case "00085":
			return new Script_00085_PokCenter((TrainerCard) card, gameModel);
		case "00086":
			return new Script_00086_PokFloete((TrainerCard) card, gameModel);
		case "00087":
			return new Script_00087_Pokedex((TrainerCard) card, gameModel);
		case "00088":
			return new Script_00088_ProfEich((TrainerCard) card, gameModel);
		case "00089":
			return new Script_00089_Beleber((TrainerCard) card, gameModel);
		case "00090":
			return new Script_00090_Supertrank((TrainerCard) card, gameModel);
		case "00091":
			return new Script_00091_Bill((TrainerCard) card, gameModel);
		case "00092":
			return new Script_00092_EnergieAbsauger((TrainerCard) card, gameModel);
		case "00093":
			return new Script_00093_Windhauch((TrainerCard) card, gameModel);
		case "00094":
			return new Script_00094_Trank((TrainerCard) card, gameModel);
		case "00095":
			return new Script_00095_Tausch((TrainerCard) card, gameModel);
		case "00096":
			return new Script_00096_ColorlessEnergy((EnergyCard) card, gameModel);
		case "00097":
			return new Script_00097_RockEnergy((EnergyCard) card, gameModel);
		case "00098":
			return new Script_00098_FireEnergy((EnergyCard) card, gameModel);
		case "00099":
			return new Script_00099_GrassEnergy((EnergyCard) card, gameModel);
		case "00100":
			return new Script_00100_LightningEnergy((EnergyCard) card, gameModel);
		case "00101":
			return new Script_00101_PsychicEnergy((EnergyCard) card, gameModel);
		case "00102":
			return new Script_00102_WaterEnergy((EnergyCard) card, gameModel);
		case "00103":
			return new Script_00103_Doll((PokemonCard) card, gameModel);
		case "00104":
			return new Script_00104_LektrobalToken((EnergyCard) card, gameModel);
		case "00105":
			return new Script_00105_Clefable((PokemonCard) card, gameModel);
		case "00106":
			return new Script_00106_Electrode((PokemonCard) card, gameModel);
		case "00107":
			return new Script_00107_Flareon((PokemonCard) card, gameModel);
		case "00108":
			return new Script_00108_Jolteon((PokemonCard) card, gameModel);
		case "00109":
			return new Script_00109_Kangaskhan((PokemonCard) card, gameModel);
		case "00110":
			return new Script_00110_MrMime((PokemonCard) card, gameModel);
		case "00111":
			return new Script_00111_Nidoqueen((PokemonCard) card, gameModel);
		case "00112":
			return new Script_00112_Pidgeot((PokemonCard) card, gameModel);
		case "00113":
			return new Script_00113_Pinsir((PokemonCard) card, gameModel);
		case "00114":
			return new Script_00114_Scyther((PokemonCard) card, gameModel);
		case "00115":
			return new Script_00115_Snorlax((PokemonCard) card, gameModel);
		case "00116":
			return new Script_00116_Vaporeon((PokemonCard) card, gameModel);
		case "00117":
			return new Script_00117_Venomoth((PokemonCard) card, gameModel);
		case "00118":
			return new Script_00118_Victreebel((PokemonCard) card, gameModel);
		case "00119":
			return new Script_00119_Vileplume((PokemonCard) card, gameModel);
		case "00120":
			return new Script_00120_Wigglytuff((PokemonCard) card, gameModel);
		case "00121":
			return new Script_00121_Butterfree((PokemonCard) card, gameModel);
		case "00122":
			return new Script_00122_Dodrio((PokemonCard) card, gameModel);
		case "00123":
			return new Script_00123_Exeggutor((PokemonCard) card, gameModel);
		case "00124":
			return new Script_00124_Fearow((PokemonCard) card, gameModel);
		case "00125":
			return new Script_00125_Gloom((PokemonCard) card, gameModel);
		case "00126":
			return new Script_00126_Lickitung((PokemonCard) card, gameModel);
		case "00127":
			return new Script_00127_Marowack((PokemonCard) card, gameModel);
		case "00128":
			return new Script_00128_Nidorina((PokemonCard) card, gameModel);
		case "00129":
			return new Script_00129_Parasect((PokemonCard) card, gameModel);
		case "00130":
			return new Script_00130_Persian((PokemonCard) card, gameModel);
		case "00131":
			return new Script_00131_Primeape((PokemonCard) card, gameModel);
		case "00132":
			return new Script_00132_Rapidash((PokemonCard) card, gameModel);
		case "00133":
			return new Script_00133_Rhydon((PokemonCard) card, gameModel);
		case "00134":
			return new Script_00134_Seaking((PokemonCard) card, gameModel);
		case "00135":
			return new Script_00135_Tauros((PokemonCard) card, gameModel);
		case "00136":
			return new Script_00136_Weepinbell((PokemonCard) card, gameModel);
		case "00137":
			return new Script_00137_Bellsprout((PokemonCard) card, gameModel);
		case "00138":
			return new Script_00138_Cubone((PokemonCard) card, gameModel);
		case "00139":
			return new Script_00139_Eevee((PokemonCard) card, gameModel);
		case "00140":
			return new Script_00140_Exeggcute((PokemonCard) card, gameModel);
		case "00141":
			return new Script_00141_Goldeen((PokemonCard) card, gameModel);
		case "00142":
			return new Script_00142_Jigglypuff((PokemonCard) card, gameModel);
		case "00143":
			return new Script_00143_Mankey((PokemonCard) card, gameModel);
		case "00144":
			return new Script_00144_Meowth((PokemonCard) card, gameModel);
		case "00145":
			return new Script_00145_NidoranF((PokemonCard) card, gameModel);
		case "00146":
			return new Script_00146_Oddish((PokemonCard) card, gameModel);
		case "00147":
			return new Script_00147_Paras((PokemonCard) card, gameModel);
		case "00148":
			return new Script_00148_Pikachu((PokemonCard) card, gameModel);
		case "00149":
			return new Script_00149_Rhyhorn((PokemonCard) card, gameModel);
		case "00150":
			return new Script_00150_Spearow((PokemonCard) card, gameModel);
		case "00151":
			return new Script_00151_Venonat((PokemonCard) card, gameModel);
		case "00152":
			return new Script_00152_Pokeball((TrainerCard) card, gameModel);
		case "00153":
			return new Script_00153_Aerodactyl((PokemonCard) card, gameModel);
		case "00154":
			return new Script_00154_Articuno((PokemonCard) card, gameModel);
		case "00155":
			return new Script_00155_Dragonite((PokemonCard) card, gameModel);
		case "00156":
			return new Script_00156_Gengar((PokemonCard) card, gameModel);
		case "00157":
			return new Script_00157_Haunter((PokemonCard) card, gameModel);
		case "00158":
			return new Script_00158_Hitmonlee((PokemonCard) card, gameModel);
		case "00159":
			return new Script_00159_Hypno((PokemonCard) card, gameModel);
		case "00160":
			return new Script_00160_Kabutops((PokemonCard) card, gameModel);
		case "00161":
			return new Script_00161_Lapras((PokemonCard) card, gameModel);
		case "00162":
			return new Script_00162_Magneton((PokemonCard) card, gameModel);
		case "00163":
			return new Script_00163_Moltres((PokemonCard) card, gameModel);
		case "00164":
			return new Script_00164_Muk((PokemonCard) card, gameModel);
		case "00165":
			return new Script_00165_Raichu((PokemonCard) card, gameModel);
		case "00166":
			return new Script_00166_Zapdos((PokemonCard) card, gameModel);
		case "00167":
			return new Script_00167_Arbok((PokemonCard) card, gameModel);
		case "00168":
			return new Script_00168_Cloyster((PokemonCard) card, gameModel);
		case "00169":
			return new Script_00169_Gastly((PokemonCard) card, gameModel);
		case "00170":
			return new Script_00170_Golbat((PokemonCard) card, gameModel);
		case "00171":
			return new Script_00171_Golduck((PokemonCard) card, gameModel);
		case "00172":
			return new Script_00172_Golem((PokemonCard) card, gameModel);
		case "00173":
			return new Script_00173_Graveler((PokemonCard) card, gameModel);
		case "00174":
			return new Script_00174_Kingler((PokemonCard) card, gameModel);
		case "00175":
			return new Script_00175_Magmar((PokemonCard) card, gameModel);
		case "00176":
			return new Script_00176_Omastar((PokemonCard) card, gameModel);
		case "00177":
			return new Script_00177_Sandslash((PokemonCard) card, gameModel);
		case "00178":
			return new Script_00178_Seadra((PokemonCard) card, gameModel);
		case "00179":
			return new Script_00179_Slowbro((PokemonCard) card, gameModel);
		case "00180":
			return new Script_00180_Tentacruel((PokemonCard) card, gameModel);
		case "00181":
			return new Script_00181_Weezing((PokemonCard) card, gameModel);
		case "00182":
			return new Script_00182_Ekans((PokemonCard) card, gameModel);
		case "00183":
			return new Script_00183_Geodude((PokemonCard) card, gameModel);
		case "00184":
			return new Script_00184_Grimer((PokemonCard) card, gameModel);
		case "00185":
			return new Script_00185_Horsea((PokemonCard) card, gameModel);
		case "00186":
			return new Script_00186_Kabuto((PokemonCard) card, gameModel);
		case "00187":
			return new Script_00187_Krabby((PokemonCard) card, gameModel);
		case "00188":
			return new Script_00188_Omanyte((PokemonCard) card, gameModel);
		case "00189":
			return new Script_00189_Psyduck((PokemonCard) card, gameModel);
		case "00190":
			return new Script_00190_Shellder((PokemonCard) card, gameModel);
		case "00191":
			return new Script_00191_Slowpoke((PokemonCard) card, gameModel);
		case "00192":
			return new Script_00192_Tentacool((PokemonCard) card, gameModel);
		case "00193":
			return new Script_00193_Zubat((PokemonCard) card, gameModel);
		case "00194":
			return new Script_00194_MrFuji((TrainerCard) card, gameModel);
		case "00195":
			return new Script_00195_EnergySearch((TrainerCard) card, gameModel);
		case "00196":
			return new Script_00196_Gambler((TrainerCard) card, gameModel);
		case "00197":
			return new Script_00197_Recycle((TrainerCard) card, gameModel);
		case "00198":
			return new Script_00198_MysteriousFossil((TrainerCard) card, gameModel);
		case "00199":
			return new Script_00199_FossilToken((PokemonCard) card, gameModel);
		case "00200":
			return new Script_00200_DarkAlakazam((PokemonCard) card, gameModel);
		case "00201":
			return new Script_00201_DarkArbok((PokemonCard) card, gameModel);
		case "00202":
			return new Script_00202_DarkBlastoise((PokemonCard) card, gameModel);
		case "00203":
			return new Script_00203_DarkCharizard((PokemonCard) card, gameModel);
		case "00204":
			return new Script_00204_DarkDragonite((PokemonCard) card, gameModel);
		case "00205":
			return new Script_00205_DarkDugtrio((PokemonCard) card, gameModel);
		case "00206":
			return new Script_00206_DarkGolbat((PokemonCard) card, gameModel);
		case "00207":
			return new Script_00207_DarkGyarados((PokemonCard) card, gameModel);
		case "00208":
			return new Script_00208_DarkHypno((PokemonCard) card, gameModel);
		case "00209":
			return new Script_00209_DarkMachamp((PokemonCard) card, gameModel);
		case "00210":
			return new Script_00210_DarkMagneton((PokemonCard) card, gameModel);
		case "00211":
			return new Script_00211_DarkSlowbro((PokemonCard) card, gameModel);
		case "00212":
			return new Script_00212_DarkVileplume((PokemonCard) card, gameModel);
		case "00213":
			return new Script_00213_DarkWeezing((PokemonCard) card, gameModel);
		case "00214":
			return new Script_00214_HereComesTeamRocket((TrainerCard) card, gameModel);
		case "00215":
			return new Script_00215_RocketsSneakAttack((TrainerCard) card, gameModel);
		case "00216":
			return new Script_00216_RainbowEnergy((EnergyCard) card, gameModel);
		case "00217":
			return new Script_00217_DarkCharmeleon((PokemonCard) card, gameModel);
		case "00218":
			return new Script_00218_DarkDragonair((PokemonCard) card, gameModel);
		case "00219":
			return new Script_00219_DarkElectrode((PokemonCard) card, gameModel);
		case "00220":
			return new Script_00220_DarkFlareon((PokemonCard) card, gameModel);
		case "00221":
			return new Script_00221_DarkGloom((PokemonCard) card, gameModel);
		case "00222":
			return new Script_00222_DarkGolduck((PokemonCard) card, gameModel);
		case "00223":
			return new Script_00223_DarkJolteon((PokemonCard) card, gameModel);
		case "00224":
			return new Script_00224_DarkKadabra((PokemonCard) card, gameModel);
		case "00225":
			return new Script_00225_DarkMachoke((PokemonCard) card, gameModel);
		case "00226":
			return new Script_00226_DarkMuk((PokemonCard) card, gameModel);
		case "00227":
			return new Script_00227_DarkPersian((PokemonCard) card, gameModel);
		case "00228":
			return new Script_00228_DarkPrimeape((PokemonCard) card, gameModel);
		case "00229":
			return new Script_00229_DarkRapidash((PokemonCard) card, gameModel);
		case "00230":
			return new Script_00230_DarkVaporeon((PokemonCard) card, gameModel);
		case "00231":
			return new Script_00231_DarkWartortle((PokemonCard) card, gameModel);
		case "00232":
			return new Script_00232_Magikarp((PokemonCard) card, gameModel);
		case "00233":
			return new Script_00233_Porygon((PokemonCard) card, gameModel);
		case "00234":
			return new Script_00234_Abra((PokemonCard) card, gameModel);
		case "00235":
			return new Script_00235_Charmander((PokemonCard) card, gameModel);
		case "00236":
			return new Script_00236_DarkRaticate((PokemonCard) card, gameModel);
		case "00237":
			return new Script_00237_Diglett((PokemonCard) card, gameModel);
		case "00238":
			return new Script_00238_Dratini((PokemonCard) card, gameModel);
		case "00239":
			return new Script_00239_Drowzee((PokemonCard) card, gameModel);
		case "00240":
			return new Script_00240_Eevee((PokemonCard) card, gameModel);
		case "00241":
			return new Script_00241_Ekans((PokemonCard) card, gameModel);
		case "00242":
			return new Script_00242_Grimer((PokemonCard) card, gameModel);
		case "00243":
			return new Script_00243_Koffing((PokemonCard) card, gameModel);
		case "00244":
			return new Script_00244_Machop((PokemonCard) card, gameModel);
		case "00245":
			return new Script_00245_Magnemite((PokemonCard) card, gameModel);
		case "00246":
			return new Script_00246_Mankey((PokemonCard) card, gameModel);
		case "00247":
			return new Script_00247_Meowth((PokemonCard) card, gameModel);
		case "00248":
			return new Script_00248_Oddish((PokemonCard) card, gameModel);
		case "00249":
			return new Script_00249_Ponyta((PokemonCard) card, gameModel);
		case "00250":
			return new Script_00250_Psyduck((PokemonCard) card, gameModel);
		case "00251":
			return new Script_00251_Rattata((PokemonCard) card, gameModel);
		case "00252":
			return new Script_00252_Slowpoke((PokemonCard) card, gameModel);
		case "00253":
			return new Script_00253_Squirtle((PokemonCard) card, gameModel);
		case "00254":
			return new Script_00254_Voltorb((PokemonCard) card, gameModel);
		case "00255":
			return new Script_00255_Zubat((PokemonCard) card, gameModel);
		case "00256":
			return new Script_00256_TheBosssWay((TrainerCard) card, gameModel);
		case "00257":
			return new Script_00257_Challenge((TrainerCard) card, gameModel);
		case "00258":
			return new Script_00258_Digger((TrainerCard) card, gameModel);
		case "00259":
			return new Script_00259_ImposterOaksRevenge((TrainerCard) card, gameModel);
		case "00260":
			return new Script_00260_NightlyGarbageRun((TrainerCard) card, gameModel);
		case "00261":
			return new Script_00261_GoopGasAttack((TrainerCard) card, gameModel);
		case "00262":
			return new Script_00262_Sleep((TrainerCard) card, gameModel);
		case "00263":
			return new Script_00263_FullHealEnergy((EnergyCard) card, gameModel);
		case "00264":
			return new Script_00264_PotionEnergy((EnergyCard) card, gameModel);
		case "00265":
			return new Script_00265_DarkRaichu((PokemonCard) card, gameModel);
		case "00266":
			return new Script_00266_BrocksRhydon((PokemonCard) card, gameModel);
		case "00267":
			return new Script_00267_Brock((TrainerCard) card, gameModel);
		case "00268":
			return new Script_00268_BrocksGolem((PokemonCard) card, gameModel);
		case "00269":
			return new Script_00269_BrocksOnix((PokemonCard) card, gameModel);
		case "00270":
			return new Script_00270_BrocksRhyhorn((PokemonCard) card, gameModel);
		case "00271":
			return new Script_00271_BrocksSandslash((PokemonCard) card, gameModel);
		case "00272":
			return new Script_00272_BrocksZubat((PokemonCard) card, gameModel);
		case "00273":
			return new Script_00273_BrocksGeodude((PokemonCard) card, gameModel);
		case "00274":
			return new Script_00274_BrocksGolbat((PokemonCard) card, gameModel);
		case "00275":
			return new Script_00275_BrocksGraveler((PokemonCard) card, gameModel);
		case "00276":
			return new Script_00276_BrocksLickitung((PokemonCard) card, gameModel);
		case "00277":
			return new Script_00277_BrocksGeodude((PokemonCard) card, gameModel);
		case "00278":
			return new Script_00278_BrocksMankey((PokemonCard) card, gameModel);
		case "00279":
			return new Script_00279_BrocksMankey((PokemonCard) card, gameModel);
		case "00280":
			return new Script_00280_BrocksOnix((PokemonCard) card, gameModel);
		case "00281":
			return new Script_00281_BrocksRhyhorn((PokemonCard) card, gameModel);
		case "00282":
			return new Script_00282_BrocksSandshrew((PokemonCard) card, gameModel);
		case "00283":
			return new Script_00283_BrocksSandshrew((PokemonCard) card, gameModel);
		case "00284":
			return new Script_00284_BrocksZubat((PokemonCard) card, gameModel);
		case "00285":
			return new Script_00285_BrocksTrainingMethod((TrainerCard) card, gameModel);
		case "00286":
			return new Script_00286_PewterCityGym((TrainerCard) card, gameModel);
		case "00287":
			return new Script_00287_BrocksDugtrio((PokemonCard) card, gameModel);
		case "00288":
			return new Script_00288_BrocksGraveler((PokemonCard) card, gameModel);
		case "00289":
			return new Script_00289_BrocksPrimeape((PokemonCard) card, gameModel);
		case "00290":
			return new Script_00290_BrocksSandslash((PokemonCard) card, gameModel);
		case "00291":
			return new Script_00291_BrocksDiglett((PokemonCard) card, gameModel);
		case "00292":
			return new Script_00292_BrocksGeodude((PokemonCard) card, gameModel);
		case "00293":
			return new Script_00293_BrocksProtection((TrainerCard) card, gameModel);
		case "00294":
			return new Script_00294_MistysSeadra((PokemonCard) card, gameModel);
		case "00295":
			return new Script_00295_MistysTentacruel((PokemonCard) card, gameModel);
		case "00296":
			return new Script_00296_Misty((TrainerCard) card, gameModel);
		case "00297":
			return new Script_00297_MistysCloyster((PokemonCard) card, gameModel);
		case "00298":
			return new Script_00298_MistysGoldeen((PokemonCard) card, gameModel);
		case "00299":
			return new Script_00299_MistysPoliwrath((PokemonCard) card, gameModel);
		case "00300":
			return new Script_00300_MistysTentacool((PokemonCard) card, gameModel);
		case "00301":
			return new Script_00301_MistysPoliwhirl((PokemonCard) card, gameModel);
		case "00302":
			return new Script_00302_MistysPsyduck((PokemonCard) card, gameModel);
		case "00303":
			return new Script_00303_MistysSeaking((PokemonCard) card, gameModel);
		case "00304":
			return new Script_00304_MistysStarmie((PokemonCard) card, gameModel);
		case "00305":
			return new Script_00305_MistysTentacool((PokemonCard) card, gameModel);
		case "00306":
			return new Script_00306_MistysGoldeen((PokemonCard) card, gameModel);
		case "00307":
			return new Script_00307_MistysHorsea((PokemonCard) card, gameModel);
		case "00308":
			return new Script_00308_MistysPoliwag((PokemonCard) card, gameModel);
		case "00309":
			return new Script_00309_MistysSeel((PokemonCard) card, gameModel);
		case "00310":
			return new Script_00310_MistysShellder((PokemonCard) card, gameModel);
		case "00311":
			return new Script_00311_MistysStaryu((PokemonCard) card, gameModel);
		case "00312":
			return new Script_00312_CelureanCityGym((TrainerCard) card, gameModel);
		case "00313":
			return new Script_00313_MistysWrath((TrainerCard) card, gameModel);
		case "00314":
			return new Script_00314_MistysDuel((TrainerCard) card, gameModel);
		case "00315":
			return new Script_00315_MistysGolduck((PokemonCard) card, gameModel);
		case "00316":
			return new Script_00316_MistysGyarados((PokemonCard) card, gameModel);
		case "00317":
			return new Script_00317_MistysDewgong((PokemonCard) card, gameModel);
		case "00318":
			return new Script_00318_MistysHorsea((PokemonCard) card, gameModel);
		case "00319":
			return new Script_00319_MistysMagikarp((PokemonCard) card, gameModel);
		case "00320":
			return new Script_00320_MistysPoliwag((PokemonCard) card, gameModel);
		case "00321":
			return new Script_00321_MistysPsyduck((PokemonCard) card, gameModel);
		case "00322":
			return new Script_00322_MistysSeel((PokemonCard) card, gameModel);
		case "00323":
			return new Script_00323_MistysStaryu((PokemonCard) card, gameModel);
		case "00324":
			return new Script_00324_MistysWish((TrainerCard) card, gameModel);
		case "00325":
			return new Script_00325_MistysTears((TrainerCard) card, gameModel);
		case "00326":
			return new Script_00326_LtSurgesElectabuzz((PokemonCard) card, gameModel);
		case "00327":
			return new Script_00327_LtSurgesFearow((PokemonCard) card, gameModel);
		case "00328":
			return new Script_00328_LtSurgesMagneton((PokemonCard) card, gameModel);
		case "00329":
			return new Script_00329_LtSurge((TrainerCard) card, gameModel);
		case "00330":
			return new Script_00330_LtSurgesElectabuzz((PokemonCard) card, gameModel);
		case "00331":
			return new Script_00331_LtSurgesRaichu((PokemonCard) card, gameModel);
		case "00332":
			return new Script_00332_LtSurgesMagnemite((PokemonCard) card, gameModel);
		case "00333":
			return new Script_00333_LtSurgesRaticate((PokemonCard) card, gameModel);
		case "00334":
			return new Script_00334_LtSurgesSpearow((PokemonCard) card, gameModel);
		case "00335":
			return new Script_00335_LtSurgesMagnemite((PokemonCard) card, gameModel);
		case "00336":
			return new Script_00336_LtSurgesPikachu((PokemonCard) card, gameModel);
		case "00337":
			return new Script_00337_LtSurgesRattata((PokemonCard) card, gameModel);
		case "00338":
			return new Script_00338_LtSurgesSpearow((PokemonCard) card, gameModel);
		case "00339":
			return new Script_00339_LtSurgesVoltorb((PokemonCard) card, gameModel);
		case "00340":
			return new Script_00340_LtSurgesTreaty((TrainerCard) card, gameModel);
		case "00341":
			return new Script_00341_SecretMission((TrainerCard) card, gameModel);
		case "00342":
			return new Script_00342_VermillionCityGym((TrainerCard) card, gameModel);
		case "00343":
			return new Script_00343_LtSurgesRaichu((PokemonCard) card, gameModel);
		case "00344":
			return new Script_00344_LtSurgesJolteon((PokemonCard) card, gameModel);
		case "00345":
			return new Script_00345_LtSurgesEevee((PokemonCard) card, gameModel);
		case "00346":
			return new Script_00346_LtSurgesElectrode((PokemonCard) card, gameModel);
		case "00347":
			return new Script_00347_LtSurgesRaticate((PokemonCard) card, gameModel);
		case "00348":
			return new Script_00348_LtSurgesPikachu((PokemonCard) card, gameModel);
		case "00349":
			return new Script_00349_LtSurgesRattata((PokemonCard) card, gameModel);
		case "00350":
			return new Script_00350_LtSurgesVoltorb((PokemonCard) card, gameModel);
		case "00351":
			return new Script_00351_ErikasClefable((PokemonCard) card, gameModel);
		case "00352":
			return new Script_00352_ErikasDragonair((PokemonCard) card, gameModel);
		case "00353":
			return new Script_00353_ErikasVileplume((PokemonCard) card, gameModel);
		case "00354":
			return new Script_00354_Erika((TrainerCard) card, gameModel);
		case "00355":
			return new Script_00355_ErikasClefairy((PokemonCard) card, gameModel);
		case "00356":
			return new Script_00356_Erikas_Victreebel((PokemonCard) card, gameModel);
		case "00357":
			return new Script_00357_ErikasDratini((PokemonCard) card, gameModel);
		case "00358":
			return new Script_00358_ErikasExeggcute((PokemonCard) card, gameModel);
		case "00359":
			return new Script_00359_ErikasExeggutor((PokemonCard) card, gameModel);
		case "00360":
			return new Script_00360_ErikasGloom((PokemonCard) card, gameModel);
		case "00361":
			return new Script_00361_ErikasGloom((PokemonCard) card, gameModel);
		case "00362":
			return new Script_00362_ErikasOddish((PokemonCard) card, gameModel);
		case "00363":
			return new Script_00363_ErikasWeepinbell((PokemonCard) card, gameModel);
		case "00364":
			return new Script_00364_ErikasWeepinbell((PokemonCard) card, gameModel);
		case "00365":
			return new Script_00365_ErikasBellsprout((PokemonCard) card, gameModel);
		case "00366":
			return new Script_00366_ErikasBellsprout((PokemonCard) card, gameModel);
		case "00367":
			return new Script_00367_ErikasExeggcute((PokemonCard) card, gameModel);
		case "00368":
			return new Script_00368_ErikasOddish((PokemonCard) card, gameModel);
		case "00369":
			return new Script_00369_ErikasTangela((PokemonCard) card, gameModel);
		case "00370":
			return new Script_00370_CeladonCityGym((TrainerCard) card, gameModel);
		case "00371":
			return new Script_00371_ErikasMaids((TrainerCard) card, gameModel);
		case "00372":
			return new Script_00372_ErikasPerfume((TrainerCard) card, gameModel);
		case "00373":
			return new Script_00373_GoodManners((TrainerCard) card, gameModel);
		case "00374":
			return new Script_00374_ErikasVenusaur((PokemonCard) card, gameModel);
		case "00375":
			return new Script_00375_ErikasBellsprout((PokemonCard) card, gameModel);
		case "00376":
			return new Script_00376_ErikasBulbasaur((PokemonCard) card, gameModel);
		case "00377":
			return new Script_00377_ErikasClefairy((PokemonCard) card, gameModel);
		case "00378":
			return new Script_00378_ErikasIvysaur((PokemonCard) card, gameModel);
		case "00379":
			return new Script_00379_ErikasJigglypuff((PokemonCard) card, gameModel);
		case "00380":
			return new Script_00380_ErikasOddish((PokemonCard) card, gameModel);
		case "00381":
			return new Script_00381_ErikasParas((PokemonCard) card, gameModel);
		case "00382":
			return new Script_00382_ErikasKindness((TrainerCard) card, gameModel);
		case "00383":
			return new Script_00383_KogasBeedrill((PokemonCard) card, gameModel);
		case "00384":
			return new Script_00384_KogasDitto((PokemonCard) card, gameModel);
		case "00385":
			return new Script_00385_Koga((TrainerCard) card, gameModel);
		case "00386":
			return new Script_00386_KogasArbok((PokemonCard) card, gameModel);
		case "00387":
			return new Script_00387_KogasMuk((PokemonCard) card, gameModel);
		case "00388":
			return new Script_00388_KogasPidgeotto((PokemonCard) card, gameModel);
		case "00389":
			return new Script_00389_KogasGolbat((PokemonCard) card, gameModel);
		case "00390":
			return new Script_00390_KogasKakuna((PokemonCard) card, gameModel);
		case "00391":
			return new Script_00391_KogasKoffing((PokemonCard) card, gameModel);
		case "00392":
			return new Script_00392_KogasPidgey((PokemonCard) card, gameModel);
		case "00393":
			return new Script_00393_KogasWeezing((PokemonCard) card, gameModel);
		case "00394":
			return new Script_00394_KogasEkans((PokemonCard) card, gameModel);
		case "00395":
			return new Script_00395_KogasGrimer((PokemonCard) card, gameModel);
		case "00396":
			return new Script_00396_KogasKoffing((PokemonCard) card, gameModel);
		case "00397":
			return new Script_00397_KogasPidgey((PokemonCard) card, gameModel);
		case "00398":
			return new Script_00398_KogasTangela((PokemonCard) card, gameModel);
		case "00399":
			return new Script_00399_KogasWeedle((PokemonCard) card, gameModel);
		case "00400":
			return new Script_00400_KogasZubat((PokemonCard) card, gameModel);
		case "00401":
			return new Script_00401_FuchsiaCityGym((TrainerCard) card, gameModel);
		case "00402":
			return new Script_00402_KogasNinjaTrick((TrainerCard) card, gameModel);
		default:
			throw new IllegalArgumentException("Error: Wrong card id in createScript of CardScriptFactory: " + card.getCardId());
		}
	}
}