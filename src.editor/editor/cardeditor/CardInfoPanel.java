package editor.cardeditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import common.utilities.BitmapComponent;
import model.database.Card;
import model.database.EnergyCard;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.CardType;
import model.enums.Edition;
import model.enums.Element;
import model.enums.Rarity;

public class CardInfoPanel extends JPanel {

	private static final long serialVersionUID = -1116467881966333698L;

	private volatile boolean updateFlag;

	private BitmapComponent cardImageBitmap;
	private JButton chooseImageButton;
	private ButtonGroup radioButtonGroup;
	private JRadioButton basisRadioButton;
	private JRadioButton stage1RadioButton;
	private JRadioButton stage2RadioButton;
	private JLabel evolveFromLabel;
	private JLabel hpLabel;
	private JLabel typeLabel;
	private JLabel weaknessLabel;
	private JLabel resistanceLabel;
	private JLabel reatreatCostLabel;
	private JLabel rarityLabel;
	private JLabel editionLabel;
	private JLabel nameLabel;
	@SuppressWarnings("rawtypes")
	private JComboBox hpComboBox;
	@SuppressWarnings("rawtypes")
	private JComboBox typeComboBox;
	@SuppressWarnings("rawtypes")
	private JComboBox weaknessComboBox;
	@SuppressWarnings("rawtypes")
	private JComboBox resistanceComboBox;
	@SuppressWarnings("rawtypes")
	private JComboBox retreatCostComboBox;
	@SuppressWarnings("rawtypes")
	private JComboBox rarityComboBox;
	@SuppressWarnings("rawtypes")
	private JComboBox editionComboBox;
	@SuppressWarnings("rawtypes")
	private JComboBox evolvesFromComboBox;
	private JLabel nameTextField;
	private JCheckBox basicEnergyCheckBox;
	private JLabel provideLabel;
	private JLabel colorlessLabel;
	private JLabel grassLabel;
	private JLabel waterLabel;
	private JLabel psychicLabel;
	private JLabel lightningLabel;
	private JLabel fireLabel;
	private JLabel rockLabel;
	@SuppressWarnings("rawtypes")
	private JComboBox colorlessComboBox;
	@SuppressWarnings("rawtypes")
	private JComboBox fireComboBox;
	@SuppressWarnings("rawtypes")
	private JComboBox waterComboBox;
	@SuppressWarnings("rawtypes")
	private JComboBox grassComboBox;
	@SuppressWarnings("rawtypes")
	private JComboBox rockComboBox;
	@SuppressWarnings("rawtypes")
	private JComboBox lightningComboBox;
	@SuppressWarnings("rawtypes")
	private JComboBox psychicComboBox;

	private Card selectedCard;
	private String[] pokemonCardNames;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public CardInfoPanel(Card s, String[] pokemonNames) {
		pokemonCardNames = pokemonNames;
		String[] editions = { Edition.BASE.toString(), Edition.JUNGLE.toString(), Edition.FOSSIL.toString(), Edition.ROCKET.toString(), Edition.TOKEN.toString() };
		String[] hpComboBoxItems = { "10", "20", "30", "40", "50", "60", "70", "80", "90", "100", "110", "120" };
		ImageIcon[] typeImages = new ImageIcon[7];
		typeImages[0] = new BitmapComponent("/tilesets/elements/thumbnails/elektro.png").getImageIcon();
		typeImages[1] = new BitmapComponent("/tilesets/elements/thumbnails/feuer.png").getImageIcon();
		typeImages[2] = new BitmapComponent("/tilesets/elements/thumbnails/wasser.png").getImageIcon();
		typeImages[3] = new BitmapComponent("/tilesets/elements/thumbnails/pflanze.png").getImageIcon();
		typeImages[4] = new BitmapComponent("/tilesets/elements/thumbnails/kampf.png").getImageIcon();
		typeImages[5] = new BitmapComponent("/tilesets/elements/thumbnails/psycho.png").getImageIcon();
		typeImages[6] = new BitmapComponent("/tilesets/elements/thumbnails/normal.png").getImageIcon();
		ImageIcon[] elementImages = new ImageIcon[8];
		elementImages[0] = new BitmapComponent("/tilesets/elements/thumbnails/noElement.png").getImageIcon();
		elementImages[1] = new BitmapComponent("/tilesets/elements/thumbnails/elektro.png").getImageIcon();
		elementImages[2] = new BitmapComponent("/tilesets/elements/thumbnails/feuer.png").getImageIcon();
		elementImages[3] = new BitmapComponent("/tilesets/elements/thumbnails/wasser.png").getImageIcon();
		elementImages[4] = new BitmapComponent("/tilesets/elements/thumbnails/pflanze.png").getImageIcon();
		elementImages[5] = new BitmapComponent("/tilesets/elements/thumbnails/kampf.png").getImageIcon();
		elementImages[6] = new BitmapComponent("/tilesets/elements/thumbnails/psycho.png").getImageIcon();
		elementImages[7] = new BitmapComponent("/tilesets/elements/thumbnails/normal.png").getImageIcon();
		String[] retreatCostComboBoxItems = { "0", "1", "2", "3", "4" };
		String[] rarityComboBoxItems = { "COMMON", "UNCOMMON", "RARE", "HOLO", "LEGENDARY" };
		String[] colorlessComboBoxItems = { "0", "1", "2" };
		String[] fireComboBoxItems = { "0", "1", "2" };
		String[] waterComboBoxItems = { "0", "1", "2" };
		String[] grassComboBoxItems = { "0", "1", "2" };
		String[] rockComboBoxItems = { "0", "1", "2" };
		String[] lightningComboBoxItems = { "0", "1", "2" };
		String[] psychicComboBoxItems = { "0", "1", "2" };

		this.setLayout(null);

		this.selectedCard = s;

		updateFlag = true;

		ActionListener listener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (updateFlag) {
					updateCard(selectedCard);
				}
			}
		};

		cardImageBitmap = new BitmapComponent("/cards/cardBack.jpg");
		chooseImageButton = new JButton("Bild auswählen");
		chooseImageButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				FileFilter filter = new FileNameExtensionFilter("Final TCG2 Datenbanken", "jpg");
				JFileChooser chooser = new JFileChooser("images/cards/base04/");
				chooser.setFileFilter(filter);
				chooser.setAcceptAllFileFilterUsed(false);
				int rueckgabeWert = chooser.showOpenDialog(null);
				if (rueckgabeWert == JFileChooser.APPROVE_OPTION) {
					File f = chooser.getSelectedFile();
					String s = null;
					s = "/cards/base04/" + f.getName();
					selectedCard.setImagePath(s);
					cardImageBitmap.setImage(s);
				}
			}
		});
		basisRadioButton = new JRadioButton("Basis");
		basisRadioButton.addActionListener(listener);
		stage1RadioButton = new JRadioButton("Stage 1");
		stage1RadioButton.addActionListener(listener);
		stage2RadioButton = new JRadioButton("Stage 2");
		stage2RadioButton.addActionListener(listener);
		radioButtonGroup = new ButtonGroup();
		radioButtonGroup.add(basisRadioButton);
		radioButtonGroup.add(stage1RadioButton);
		radioButtonGroup.add(stage2RadioButton);
		basisRadioButton.setSelected(true);
		evolveFromLabel = new JLabel("Entwickelt aus:");
		hpLabel = new JLabel("HP:");
		typeLabel = new JLabel("Typ:");
		weaknessLabel = new JLabel("Schwäche:");
		resistanceLabel = new JLabel("Resistenz:");
		reatreatCostLabel = new JLabel("Rückzugskosten:");
		rarityLabel = new JLabel("Seltenheit:");
		editionLabel = new JLabel("Edition:");
		nameLabel = new JLabel("Name:");
		hpComboBox = new JComboBox(hpComboBoxItems);
		hpComboBox.addActionListener(listener);
		typeComboBox = new JComboBox(typeImages);
		typeComboBox.addActionListener(listener);
		weaknessComboBox = new JComboBox(elementImages);
		weaknessComboBox.addActionListener(listener);
		resistanceComboBox = new JComboBox(elementImages);
		resistanceComboBox.addActionListener(listener);
		retreatCostComboBox = new JComboBox(retreatCostComboBoxItems);
		retreatCostComboBox.addActionListener(listener);
		rarityComboBox = new JComboBox(rarityComboBoxItems);
		rarityComboBox.addActionListener(listener);
		editionComboBox = new JComboBox(editions);
		editionComboBox.addActionListener(listener);
		evolvesFromComboBox = new JComboBox(new String[] { "", "" });
		evolvesFromComboBox.addActionListener(listener);
		nameTextField = new JLabel();
		basicEnergyCheckBox = new JCheckBox("Basis-Energie");
		basicEnergyCheckBox.addActionListener(listener);
		provideLabel = new JLabel("Liefert:");
		colorlessLabel = new JLabel("Farblos:");
		grassLabel = new JLabel("Pflanze:");
		waterLabel = new JLabel("Wasser:");
		psychicLabel = new JLabel("Psycho:");
		lightningLabel = new JLabel("Elektro:");
		fireLabel = new JLabel("Feuer:");
		rockLabel = new JLabel("Boden:");
		colorlessComboBox = new JComboBox(colorlessComboBoxItems);
		colorlessComboBox.addActionListener(listener);
		fireComboBox = new JComboBox(fireComboBoxItems);
		fireComboBox.addActionListener(listener);
		waterComboBox = new JComboBox(waterComboBoxItems);
		waterComboBox.addActionListener(listener);
		grassComboBox = new JComboBox(grassComboBoxItems);
		grassComboBox.addActionListener(listener);
		rockComboBox = new JComboBox(rockComboBoxItems);
		rockComboBox.addActionListener(listener);
		lightningComboBox = new JComboBox(lightningComboBoxItems);
		lightningComboBox.addActionListener(listener);
		psychicComboBox = new JComboBox(psychicComboBoxItems);
		psychicComboBox.addActionListener(listener);

		add(cardImageBitmap);
		add(chooseImageButton);
		add(basisRadioButton);
		add(stage1RadioButton);
		add(stage2RadioButton);
		add(evolveFromLabel);
		add(hpLabel);
		add(typeLabel);
		add(weaknessLabel);
		add(resistanceLabel);
		add(reatreatCostLabel);
		add(rarityLabel);
		add(editionLabel);
		add(nameLabel);
		add(hpComboBox);
		add(typeComboBox);
		add(weaknessComboBox);
		add(resistanceComboBox);
		add(retreatCostComboBox);
		add(rarityComboBox);
		add(editionComboBox);
		add(evolvesFromComboBox);
		add(nameTextField);
		add(basicEnergyCheckBox);
		add(provideLabel);
		add(colorlessLabel);
		add(grassLabel);
		add(waterLabel);
		add(psychicLabel);
		add(lightningLabel);
		add(fireLabel);
		add(rockLabel);
		add(colorlessComboBox);
		add(fireComboBox);
		add(waterComboBox);
		add(grassComboBox);
		add(rockComboBox);
		add(lightningComboBox);
		add(psychicComboBox);

		cardImageBitmap.setBounds(5, 5, 120, 150);
		chooseImageButton.setBounds(5, 160, 120, 25);
		basisRadioButton.setBounds(5, 190, 120, 25);
		stage1RadioButton.setBounds(5, 215, 120, 25);
		stage2RadioButton.setBounds(5, 240, 120, 25);
		evolveFromLabel.setBounds(150, 240, 100, 25);
		hpLabel.setBounds(150, 30, 100, 25);
		typeLabel.setBounds(150, 60, 100, 25);
		weaknessLabel.setBounds(150, 90, 100, 25);
		resistanceLabel.setBounds(150, 120, 100, 25);
		reatreatCostLabel.setBounds(150, 150, 100, 25);
		rarityLabel.setBounds(150, 180, 100, 25);
		editionLabel.setBounds(150, 210, 100, 25);
		nameLabel.setBounds(150, 5, 100, 25);
		hpComboBox.setBounds(255, 35, 100, 25);
		typeComboBox.setBounds(255, 65, 100, 25);
		weaknessComboBox.setBounds(255, 95, 100, 25);
		resistanceComboBox.setBounds(255, 125, 100, 25);
		retreatCostComboBox.setBounds(255, 155, 100, 25);
		rarityComboBox.setBounds(255, 185, 100, 25);
		editionComboBox.setBounds(255, 215, 100, 25);
		evolvesFromComboBox.setBounds(255, 245, 100, 25);
		nameTextField.setBounds(255, 5, 100, 25);
		basicEnergyCheckBox.setBounds(10, 280, 105, 25);
		provideLabel.setBounds(10, 305, 100, 25);
		colorlessLabel.setBounds(10, 335, 100, 25);
		grassLabel.setBounds(10, 425, 100, 25);
		waterLabel.setBounds(10, 395, 100, 25);
		psychicLabel.setBounds(180, 395, 100, 25);
		lightningLabel.setBounds(180, 365, 100, 25);
		fireLabel.setBounds(10, 365, 100, 25);
		rockLabel.setBounds(180, 335, 100, 25);
		colorlessComboBox.setBounds(120, 335, 45, 25);
		fireComboBox.setBounds(120, 365, 45, 25);
		waterComboBox.setBounds(120, 395, 45, 25);
		grassComboBox.setBounds(120, 425, 45, 25);
		rockComboBox.setBounds(290, 335, 45, 25);
		lightningComboBox.setBounds(290, 365, 45, 25);
		psychicComboBox.setBounds(290, 395, 45, 25);
	}

	protected void updateCard(Card c) {
		if (c instanceof PokemonCard) {
			PokemonCard p = (PokemonCard) c;
			p.setImagePath(cardImageBitmap.getPfad());
			if (basisRadioButton.isSelected())
				p.setCardType(CardType.BASICPOKEMON);
			else if (stage1RadioButton.isSelected())
				p.setCardType(CardType.STAGE1POKEMON);
			else
				p.setCardType(CardType.STAGE2POKEMON);
			p.setHitpoints(Integer.parseInt((String) hpComboBox.getSelectedItem()));
			switch (typeComboBox.getSelectedIndex()) {
			case 0:
				p.setElement(Element.LIGHTNING);
				break;
			case 1:
				p.setElement(Element.FIRE);
				break;
			case 2:
				p.setElement(Element.WATER);
				break;
			case 3:
				p.setElement(Element.GRASS);
				break;
			case 4:
				p.setElement(Element.ROCK);
				break;
			case 5:
				p.setElement(Element.PSYCHIC);
				break;
			case 6:
				p.setElement(Element.COLORLESS);
				break;
			}

			switch (weaknessComboBox.getSelectedIndex()) {
			case 0:
				p.setWeakness(null);
				break;
			case 1:
				p.setWeakness(Element.LIGHTNING);
				break;
			case 2:
				p.setWeakness(Element.FIRE);
				break;
			case 3:
				p.setWeakness(Element.WATER);
				break;
			case 4:
				p.setWeakness(Element.GRASS);
				break;
			case 5:
				p.setWeakness(Element.ROCK);
				break;
			case 6:
				p.setWeakness(Element.PSYCHIC);
				break;
			case 7:
				p.setWeakness(Element.COLORLESS);
				break;
			}

			switch (resistanceComboBox.getSelectedIndex()) {
			case 0:
				p.setResistance(null);
				break;
			case 1:
				p.setResistance(Element.LIGHTNING);
				break;
			case 2:
				p.setResistance(Element.FIRE);
				break;
			case 3:
				p.setResistance(Element.WATER);
				break;
			case 4:
				p.setResistance(Element.GRASS);
				break;
			case 5:
				p.setResistance(Element.ROCK);
				break;
			case 6:
				p.setResistance(Element.PSYCHIC);
				break;
			case 7:
				p.setResistance(Element.COLORLESS);
				break;
			}
			ArrayList<Element> rC = new ArrayList<Element>();
			for (int i = 0; i < Integer.parseInt((String) retreatCostComboBox.getSelectedItem()); i++)
				rC.add(Element.COLORLESS);
			p.setRetreatCosts(rC);
			p.setRarity(Rarity.valueOf((String) rarityComboBox.getSelectedItem()));
			p.setEdition(Edition.valueOf((String) editionComboBox.getSelectedItem()));
			if (!basisRadioButton.isSelected())
				p.setEvolvesFrom((String) evolvesFromComboBox.getSelectedItem());
			else
				p.setEvolvesFrom("");
			p.setName(nameTextField.getText());
		} else if (c instanceof TrainerCard) {
			TrainerCard p = (TrainerCard) c;
			p.setImagePath(cardImageBitmap.getPfad());
			p.setRarity(Rarity.valueOf((String) rarityComboBox.getSelectedItem()));
			p.setEdition(Edition.valueOf((String) editionComboBox.getSelectedItem()));
			p.setName(nameTextField.getText());
		} else {
			EnergyCard p = (EnergyCard) c;
			p.setImagePath(cardImageBitmap.getPfad());
			p.setRarity(Rarity.valueOf((String) rarityComboBox.getSelectedItem()));
			p.setEdition(Edition.valueOf((String) editionComboBox.getSelectedItem()));
			p.setName(nameTextField.getText());
			p.setBasisEnergy(basicEnergyCheckBox.isSelected());
			int colorless = Integer.parseInt((String) colorlessComboBox.getSelectedItem());
			int fire = Integer.parseInt((String) fireComboBox.getSelectedItem());
			int water = Integer.parseInt((String) waterComboBox.getSelectedItem());
			int lightning = Integer.parseInt((String) lightningComboBox.getSelectedItem());
			int grass = Integer.parseInt((String) grassComboBox.getSelectedItem());
			int rock = Integer.parseInt((String) rockComboBox.getSelectedItem());
			int psychic = Integer.parseInt((String) psychicComboBox.getSelectedItem());
			ArrayList<Element> energy = new ArrayList<Element>();
			for (int i = 0; i < colorless; i++)
				energy.add(Element.COLORLESS);
			for (int i = 0; i < fire; i++)
				energy.add(Element.FIRE);
			for (int i = 0; i < water; i++)
				energy.add(Element.WATER);
			for (int i = 0; i < lightning; i++)
				energy.add(Element.LIGHTNING);
			for (int i = 0; i < grass; i++)
				energy.add(Element.GRASS);
			for (int i = 0; i < rock; i++)
				energy.add(Element.ROCK);
			for (int i = 0; i < psychic; i++)
				energy.add(Element.PSYCHIC);
			p.setProvidedEnergy(energy);
		}
		updateEditorBoard(c);
	}

	@SuppressWarnings("unchecked")
	public void updateEditorBoard(Card c) {
		selectedCard = c;
		updateFlag = false;
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
		String[] s = pokemonCardNames;
		model.addElement("");
		for (int i = 0; i < s.length; i++)
			model.addElement(s[i]);
		evolvesFromComboBox.setModel(model);
		if (c != null) {
			if (c instanceof PokemonCard) {
				PokemonCard p = (PokemonCard) c;
				cardImageBitmap.setImage(p.getImagePath());
				if (p.getCardType().equals(CardType.BASICPOKEMON)) {
					basisRadioButton.setSelected(true);
					evolvesFromComboBox.setVisible(false);
				} else if (p.getCardType().equals(CardType.STAGE1POKEMON)) {
					stage1RadioButton.setSelected(true);
					evolvesFromComboBox.setVisible(true);
				} else {
					stage2RadioButton.setSelected(true);
					evolvesFromComboBox.setVisible(true);
				}

				hpComboBox.setSelectedItem(String.valueOf(p.getHitpoints()));
				if (p.getElement().equals(Element.LIGHTNING))
					typeComboBox.setSelectedIndex(0);
				else if (p.getElement().equals(Element.FIRE))
					typeComboBox.setSelectedIndex(1);
				else if (p.getElement().equals(Element.WATER))
					typeComboBox.setSelectedIndex(2);
				else if (p.getElement().equals(Element.GRASS))
					typeComboBox.setSelectedIndex(3);
				else if (p.getElement().equals(Element.ROCK))
					typeComboBox.setSelectedIndex(4);
				else if (p.getElement().equals(Element.PSYCHIC))
					typeComboBox.setSelectedIndex(5);
				else if (p.getElement().equals(Element.COLORLESS))
					typeComboBox.setSelectedIndex(6);

				if (p.getWeakness() == null)
					weaknessComboBox.setSelectedIndex(0);
				else if (p.getWeakness().equals(Element.LIGHTNING))
					weaknessComboBox.setSelectedIndex(1);
				else if (p.getWeakness().equals(Element.FIRE))
					weaknessComboBox.setSelectedIndex(2);
				else if (p.getWeakness().equals(Element.WATER))
					weaknessComboBox.setSelectedIndex(3);
				else if (p.getWeakness().equals(Element.GRASS))
					weaknessComboBox.setSelectedIndex(4);
				else if (p.getWeakness().equals(Element.ROCK))
					weaknessComboBox.setSelectedIndex(5);
				else if (p.getWeakness().equals(Element.PSYCHIC))
					weaknessComboBox.setSelectedIndex(6);
				else if (p.getWeakness().equals(Element.COLORLESS))
					weaknessComboBox.setSelectedIndex(7);

				if (p.getResistance() == null)
					resistanceComboBox.setSelectedIndex(0);
				else if (p.getResistance().equals(Element.LIGHTNING))
					resistanceComboBox.setSelectedIndex(1);
				else if (p.getResistance().equals(Element.FIRE))
					resistanceComboBox.setSelectedIndex(2);
				else if (p.getResistance().equals(Element.WATER))
					resistanceComboBox.setSelectedIndex(3);
				else if (p.getResistance().equals(Element.GRASS))
					resistanceComboBox.setSelectedIndex(4);
				else if (p.getResistance().equals(Element.ROCK))
					resistanceComboBox.setSelectedIndex(5);
				else if (p.getResistance().equals(Element.PSYCHIC))
					resistanceComboBox.setSelectedIndex(6);
				else if (p.getResistance().equals(Element.COLORLESS))
					resistanceComboBox.setSelectedIndex(7);
				retreatCostComboBox.setSelectedItem(String.valueOf(p.getRetreatCosts().size()));
				rarityComboBox.setSelectedItem(p.getRarity().toString());
				editionComboBox.setSelectedItem(p.getEdition().toString());
				evolvesFromComboBox.setSelectedItem(p.getEvolvesFrom());
				nameTextField.setText(p.getName());
				showPokemonProperties();
				if (p.getCardType().equals(CardType.BASICPOKEMON))
					evolvesFromComboBox.setVisible(false);
				else if (p.getCardType().equals(CardType.STAGE1POKEMON))
					evolvesFromComboBox.setVisible(true);
				else
					evolvesFromComboBox.setVisible(true);
			} else if (c instanceof TrainerCard) {
				TrainerCard p = (TrainerCard) c;
				nameTextField.setText(p.getName());
				rarityComboBox.setSelectedItem(p.getRarity().toString());
				editionComboBox.setSelectedItem(p.getEdition().toString());
				cardImageBitmap.setImage(p.getImagePath());
				showTrainerProperties();
			} else {
				EnergyCard p = (EnergyCard) c;
				nameTextField.setText(p.getName());
				rarityComboBox.setSelectedItem(p.getRarity().toString());
				editionComboBox.setSelectedItem(p.getEdition().toString());
				cardImageBitmap.setImage(p.getImagePath());
				basicEnergyCheckBox.setSelected(p.isBasisEnergy());
				int colorless = 0;
				int fire = 0;
				int water = 0;
				int lightning = 0;
				int grass = 0;
				int rock = 0;
				int psychic = 0;
				for (int i = 0; i < p.getProvidedEnergy().size(); i++) {
					Element e = p.getProvidedEnergy().get(i);
					if (e.equals(Element.COLORLESS))
						colorless++;
					else if (e.equals(Element.FIRE))
						fire++;
					else if (e.equals(Element.WATER))
						water++;
					else if (e.equals(Element.LIGHTNING))
						lightning++;
					else if (e.equals(Element.GRASS))
						grass++;
					else if (e.equals(Element.ROCK))
						rock++;
					else if (e.equals(Element.PSYCHIC))
						psychic++;
				}
				colorlessComboBox.setSelectedItem(String.valueOf(colorless));
				fireComboBox.setSelectedItem(String.valueOf(fire));
				waterComboBox.setSelectedItem(String.valueOf(water));
				grassComboBox.setSelectedItem(String.valueOf(grass));
				rockComboBox.setSelectedItem(String.valueOf(rock));
				lightningComboBox.setSelectedItem(String.valueOf(lightning));
				psychicComboBox.setSelectedItem(String.valueOf(psychic));
				showEnergyProperties();
			}
		} else
			hideAll();
		updateFlag = true;
	}

	private void showEnergyProperties() {
		hideAll();
		cardImageBitmap.setVisible(true);
		chooseImageButton.setVisible(true);
		rarityLabel.setVisible(true);
		editionLabel.setVisible(true);
		nameLabel.setVisible(true);
		rarityComboBox.setVisible(true);
		editionComboBox.setVisible(true);
		nameTextField.setVisible(true);
		basicEnergyCheckBox.setVisible(true);
		provideLabel.setVisible(true);
		colorlessLabel.setVisible(true);
		grassLabel.setVisible(true);
		waterLabel.setVisible(true);
		psychicLabel.setVisible(true);
		lightningLabel.setVisible(true);
		fireLabel.setVisible(true);
		rockLabel.setVisible(true);
		colorlessComboBox.setVisible(true);
		fireComboBox.setVisible(true);
		waterComboBox.setVisible(true);
		grassComboBox.setVisible(true);
		rockComboBox.setVisible(true);
		lightningComboBox.setVisible(true);
		psychicComboBox.setVisible(true);
	}

	private void showTrainerProperties() {
		hideAll();
		cardImageBitmap.setVisible(true);
		chooseImageButton.setVisible(true);
		rarityLabel.setVisible(true);
		editionLabel.setVisible(true);
		nameLabel.setVisible(true);
		rarityComboBox.setVisible(true);
		editionComboBox.setVisible(true);
		nameTextField.setVisible(true);
	}

	private void showPokemonProperties() {
		hideAll();
		cardImageBitmap.setVisible(true);
		chooseImageButton.setVisible(true);
		basisRadioButton.setVisible(true);
		stage1RadioButton.setVisible(true);
		stage2RadioButton.setVisible(true);
		evolveFromLabel.setVisible(true);
		hpLabel.setVisible(true);
		typeLabel.setVisible(true);
		weaknessLabel.setVisible(true);
		resistanceLabel.setVisible(true);
		reatreatCostLabel.setVisible(true);
		rarityLabel.setVisible(true);
		editionLabel.setVisible(true);
		nameLabel.setVisible(true);
		hpComboBox.setVisible(true);
		typeComboBox.setVisible(true);
		weaknessComboBox.setVisible(true);
		resistanceComboBox.setVisible(true);
		retreatCostComboBox.setVisible(true);
		rarityComboBox.setVisible(true);
		editionComboBox.setVisible(true);
		nameTextField.setVisible(true);
		evolvesFromComboBox.setVisible(true);
	}

	private void hideAll() {
		cardImageBitmap.setVisible(false);
		chooseImageButton.setVisible(false);
		basisRadioButton.setVisible(false);
		stage1RadioButton.setVisible(false);
		stage2RadioButton.setVisible(false);
		evolveFromLabel.setVisible(false);
		hpLabel.setVisible(false);
		typeLabel.setVisible(false);
		weaknessLabel.setVisible(false);
		resistanceLabel.setVisible(false);
		reatreatCostLabel.setVisible(false);
		rarityLabel.setVisible(false);
		editionLabel.setVisible(false);
		nameLabel.setVisible(false);
		hpComboBox.setVisible(false);
		typeComboBox.setVisible(false);
		weaknessComboBox.setVisible(false);
		resistanceComboBox.setVisible(false);
		retreatCostComboBox.setVisible(false);
		rarityComboBox.setVisible(false);
		editionComboBox.setVisible(false);
		evolvesFromComboBox.setVisible(false);
		nameTextField.setVisible(false);
		basicEnergyCheckBox.setVisible(false);
		provideLabel.setVisible(false);
		colorlessLabel.setVisible(false);
		grassLabel.setVisible(false);
		waterLabel.setVisible(false);
		psychicLabel.setVisible(false);
		lightningLabel.setVisible(false);
		fireLabel.setVisible(false);
		rockLabel.setVisible(false);
		colorlessComboBox.setVisible(false);
		fireComboBox.setVisible(false);
		waterComboBox.setVisible(false);
		grassComboBox.setVisible(false);
		rockComboBox.setVisible(false);
		lightningComboBox.setVisible(false);
		psychicComboBox.setVisible(false);
	}
}
