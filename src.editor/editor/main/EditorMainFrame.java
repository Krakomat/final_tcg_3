package editor.main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import common.utilities.BitmapComponent;
import editor.cardeditor.CardEditorModel;
import editor.cardeditor.CardInfoPanel;
import model.database.Card;
import model.database.EnergyCard;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.Edition;
import model.enums.Rarity;

public class EditorMainFrame extends JPanel {

	private static final String DB_IMAGEPATH = "data/";
	private static final long serialVersionUID = 3088083030918982778L;
	protected static Frame Frame;
	private JList<String> pokemonJList;
	private JScrollPane pokemonJScrollPane;
	private JButton newPokemonButton;
	private JButton newEnergyCardButton;
	private JButton newTrainerCardButton;
	private JButton upButton;
	private JButton downButton;
	private JButton deleteCardButton;
	private JButton saveButton;
	private JButton openButton;
	private JComboBox<Edition> editionChooser;

	private JTabbedPane tabs;
	private CardInfoPanel cardInfoPanel;

	private CardEditorModel cardEditorModel;

	public EditorMainFrame() {
		// construct preComponents
		cardEditorModel = new CardEditorModel();

		// construct components
		pokemonJList = new JList<String>(cardEditorModel.getAllCardsAsString());
		pokemonJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		pokemonJList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				updateListSelection();
			}
		});
		pokemonJScrollPane = new JScrollPane(pokemonJList);
		newPokemonButton = new JButton(new BitmapComponent("/tilesets/other/pokeball16x16.png").getImageIcon());
		newPokemonButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				addCard(new PokemonCard());
			}
		});
		newPokemonButton.setEnabled(false);
		newEnergyCardButton = new JButton(new BitmapComponent("/tilesets/other/energy.png").getImageIcon());
		newEnergyCardButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				addCard(new EnergyCard());
			}
		});
		newEnergyCardButton.setEnabled(false);
		newTrainerCardButton = new JButton(new BitmapComponent("/tilesets/other/trainer.png").getImageIcon());
		newTrainerCardButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				addCard(new TrainerCard());
			}
		});
		newTrainerCardButton.setEnabled(false);
		upButton = new JButton("U");
		upButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int index = pokemonJList.getSelectedIndex();
				Card movedCard = cardEditorModel.getAllCards().remove(index);
				String id = movedCard.getCardId();
				movedCard.setCardId(cardEditorModel.getAllCards().get(index - 1).getCardId());
				cardEditorModel.getAllCards().get(index - 1).setCardId(id);
				cardEditorModel.getAllCards().add(index - 1, movedCard);
				pokemonJList.setListData(cardEditorModel.getAllCardsAsString());
				pokemonJList.setSelectedIndex(index - 1);
			}
		});
		downButton = new JButton("D");
		downButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int index = pokemonJList.getSelectedIndex();
				Card movedCard = cardEditorModel.getAllCards().remove(index);
				String id = movedCard.getCardId();
				movedCard.setCardId(cardEditorModel.getAllCards().get(index).getCardId());
				cardEditorModel.getAllCards().get(index).setCardId(id);
				cardEditorModel.getAllCards().add(index + 1, movedCard);
				pokemonJList.setListData(cardEditorModel.getAllCardsAsString());
				pokemonJList.setSelectedIndex(index + 1);
			}
		});
		deleteCardButton = new JButton("Del");
		deleteCardButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (pokemonJList.getSelectedIndex() != -1) {
					int index = pokemonJList.getSelectedIndex();
					cardEditorModel.getAllCards().remove(index);
					for (int i = index; i < cardEditorModel.getAllCards().size(); i++)
						CardEditorModel.decreaseCardID(cardEditorModel.getAllCards().get(i));
					pokemonJList.setListData(cardEditorModel.getAllCardsAsString());
					pokemonJList.setSelectedIndex(index);
				}
			}
		});
		deleteCardButton.setVisible(false);

		this.editionChooser = new JComboBox<Edition>();
		this.editionChooser.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				Edition edition = (Edition) editionChooser.getSelectedItem();
				pokemonJList.setListData(cardEditorModel.getAllCardsAsString(edition));
				pokemonJList.setSelectedIndex(cardEditorModel.getAllCardsAsString(edition).length - 1);
				pokemonJList.ensureIndexIsVisible(cardEditorModel.getAllCardsAsString(edition).length - 1);
			}
		});
		for (Edition e : Edition.values())
			this.editionChooser.addItem(e);
		this.editionChooser.setEnabled(false);

		saveButton = new JButton("Speichern");
		saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				cardEditorModel.printInFile(cardEditorModel.getAllCards());
				JOptionPane.showMessageDialog(null, "Speichern erfolgreich!");
			}
		});
		saveButton.setEnabled(false);
		openButton = new JButton("Öffnen");
		openButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				FileFilter filter = new FileNameExtensionFilter("Final TCG2 Datenbanken", "xml");
				JFileChooser chooser = new JFileChooser(DB_IMAGEPATH);
				chooser.setFileFilter(filter);
				chooser.setAcceptAllFileFilterUsed(false);
				int rueckgabeWert = chooser.showOpenDialog(null);
				if (rueckgabeWert == JFileChooser.APPROVE_OPTION) {
					File f = chooser.getSelectedFile();
					try {
						cardEditorModel.readFromDatabaseFile(f);
						pokemonJList.setListData(cardEditorModel.getAllCardsAsString());
						saveButton.setEnabled(true);
						newPokemonButton.setEnabled(true);
						newTrainerCardButton.setEnabled(true);
						newEnergyCardButton.setEnabled(true);
						editionChooser.setEnabled(true);
						tabs.removeAll();
						cardInfoPanel = new CardInfoPanel(null, cardEditorModel.getPokemonNames());
						cardInfoPanel.setBounds(250, 10, 360, 455);
						tabs.addTab("Info", cardInfoPanel);
						tabs.setVisible(false);
					} catch (IOException e) {
						JOptionPane.showMessageDialog(null, "Datei fehlerhaft!");
					}
				}
			}
		});

		cardInfoPanel = new CardInfoPanel(null, null);
		cardInfoPanel.setBounds(250, 10, 560, 455);

		// adjust size and set layout
		setPreferredSize(new Dimension(1013, 566));
		setLayout(null);

		tabs = new JTabbedPane();
		tabs.setBounds(250, 10, 700, 540);
		tabs.addTab("Info", cardInfoPanel);
		tabs.setVisible(false);

		// add components
		add(pokemonJScrollPane);
		add(newPokemonButton);
		add(newEnergyCardButton);
		add(newTrainerCardButton);
		add(upButton);
		add(downButton);
		add(deleteCardButton);
		add(saveButton);
		add(openButton);
		add(tabs);
		add(editionChooser);

		// set component bounds (only needed by Absolute Positioning)
		pokemonJScrollPane.setBounds(10, 105, 205, 415);
		editionChooser.setBounds(10, 45, 205, 25);
		newPokemonButton.setBounds(10, 75, 65, 25);
		newEnergyCardButton.setBounds(150, 75, 65, 25);
		newTrainerCardButton.setBounds(80, 75, 65, 25);
		upButton.setBounds(220, 195, 25, 80);
		downButton.setBounds(220, 280, 25, 80);
		deleteCardButton.setBounds(10, 525, 60, 25);
		saveButton.setBounds(10, 15, 100, 25);
		openButton.setBounds(115, 15, 100, 25);
		upButton.setVisible(false);
		downButton.setVisible(false);
	}

	protected void addCard(Card c) {
		String s = "";
		boolean nameDone = false;
		while (!nameDone && s != null) {
			s = JOptionPane.showInputDialog("Geben sie einen Namen für die Karte an:");
			if (s != null && !s.equals("")) {
				nameDone = true;
				pokemonJList.setListData(cardEditorModel.getAllCardsAsString());
				pokemonJList.setSelectedIndex(cardEditorModel.getAllCardsAsString().length - 1);
				cardEditorModel.createCardID(c);
				cardEditorModel.getAllCards().add(c);
				c.setName(s);
				c.setImagePath("/cards/cardBack.png");
				c.setRarity(Rarity.COMMON);
				c.setEdition(Edition.BASE);
				pokemonJList.setListData(cardEditorModel.getAllCardsAsString());
				pokemonJList.setSelectedIndex(cardEditorModel.getAllCards().size() - 1);
				pokemonJList.ensureIndexIsVisible(cardEditorModel.getAllCards().size() - 1);
			}
		}
	}

	protected void updateListSelection() {
		if (pokemonJList.getSelectedIndex() != -1) {
			deleteCardButton.setVisible(true);

			tabs.removeAll();
			String cardID = pokemonJList.getSelectedValue().substring(0, 5);
			Card c = cardEditorModel.getCard(cardID);
			cardInfoPanel = new CardInfoPanel(c, cardEditorModel.getPokemonNames());
			cardInfoPanel.setBounds(250, 10, 360, 455);
			cardInfoPanel.updateEditorBoard(c);
			tabs.addTab("Info", cardInfoPanel);

			tabs.setVisible(false);

			if (pokemonJList.getSelectedIndex() > 0)
				upButton.setVisible(true);
			else
				upButton.setVisible(false);
			if (pokemonJList.getSelectedIndex() < pokemonJList.getModel().getSize() - 1)
				downButton.setVisible(true);
			else
				downButton.setVisible(false);
			tabs.setVisible(true);
		} else {
			cardInfoPanel.updateEditorBoard(null);
			upButton.setVisible(false);
			downButton.setVisible(false);
			deleteCardButton.setVisible(false);
			tabs.setVisible(false);
		}
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("EditorMainFrame");
		Frame = frame;
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(new EditorMainFrame());
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setBounds(300, 100, 1013, 600);
	}
}
