package model.scripting.abstracts;

import model.database.Card;
import model.database.EnergyCard;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.interfaces.PokemonGame;
import model.scripting.baseEdition.*;
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
	 * Creates the {@link CardScript} for the given cardID, using the given {@link PokemonGame}.
	 * 
	 * @param cardID
	 * @param pokemonGame_game
	 * @return
	 */
	public CardScript createScript(Card card, PokemonGame gameModel) {
		switch (card.getCardId()) {
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
			return new Script_00103_Doll((PokemonCard) card, gameModel); // Only used locally for Piepi doll
		case "00104":
			return new Script_00104_LektrobalToken((EnergyCard) card, gameModel); // Only used locally for Lektrobal token
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
			return new Script_00199_FossilToken((PokemonCard) card, gameModel); // Only used locally for Mysterious Fossil
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
		default:
			throw new IllegalArgumentException("Error: Wrong card id in createScript of CardScriptFactory: " + card.getCardId());
		}
	}
}