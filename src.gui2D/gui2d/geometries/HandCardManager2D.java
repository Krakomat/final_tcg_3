package gui2d.geometries;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.Database;

import com.jme3.asset.TextureKey;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;

import common.utilities.LinearFunction;
import common.utilities.Lock;
import gui2d.GUI2D;
import gui2d.abstracts.SelectableNode;
import gui2d.animations.AnimateableObject;
import gui2d.animations.AnimationParameters;
import gui2d.controller.IngameController;
import gui2d.geometries.chooser.CardViewer;

/**
 * Stores HandCard2d geometries and arranges/updates them on update(). Has to be attached to a {@link IngameController}!
 * 
 * @author Michael
 *
 */
public class HandCardManager2D extends Node implements SelectableNode, AnimateableObject {
	public static final int MAX_HAND_CARDS = 9;
	private HandCardManager2D self;
	private List<HandCard2D> handCards;
	private List<Card> cards;
	private boolean enemyHand;
	private int level;
	private float xPos = this.getLocalTranslation().x;
	private float yPos = this.getLocalTranslation().y;
	private List<LinearFunction> xFunction;
	private float animationTime;
	private TextButton2D scrollLeftButton, scrollRightButton;
	private Lock lock;
	private List<Integer> glowingIndices;
	private int currentScrollIndex;
	private boolean scrollingAllowed;

	public HandCardManager2D(String name, boolean enemyHand) {
		this.setName(name);
		self = this;
		scrollingAllowed = false;
		handCards = new ArrayList<>();
		xPos = 0;
		yPos = 0;
		level = 0;
		cards = new ArrayList<>();
		glowingIndices = new ArrayList<>();
		lock = new Lock();
		this.enemyHand = enemyHand;
		this.xFunction = new ArrayList<>();
		this.animationTime = 0;
		this.currentScrollIndex = 0;
		initHandCards();
	}

	public HandCardManager2D(String name, float xPos, float yPos, int zPos, boolean enemyHand) {
		this.setName(name);
		self = this;
		scrollingAllowed = false;
		handCards = new ArrayList<>();
		this.xPos = xPos;
		this.yPos = yPos;
		level = zPos;
		cards = new ArrayList<>();
		glowingIndices = new ArrayList<>();
		lock = new Lock();
		this.enemyHand = enemyHand;
		this.xFunction = new ArrayList<>();
		this.animationTime = 0;
		this.currentScrollIndex = 0;
		initHandCards();
	}

	private void initHandCards() {
		/*
		 * Creates 9 HandCard2D objects and attaches them to this.handCards.
		 */
		int screenWidth = GUI2D.getInstance().getResolution().getKey();
		float epsilon = screenWidth * 0.01f; // distance between two cards
		float handCardWidth = screenWidth * 0.06f; // Size of one single hand card

		float startPoint = 0;
		float newStartPoint = 0;
		startPoint = xPos - ((handCardWidth / 2) * (MAX_HAND_CARDS - 1) + epsilon * (MAX_HAND_CARDS / 2)) - handCardWidth / 2;
		newStartPoint = xPos - ((handCardWidth) * (MAX_HAND_CARDS / 2 - 1) + handCardWidth / 2 + (epsilon / 2) + epsilon * ((MAX_HAND_CARDS / 2) - 1)) - handCardWidth / 2;

		float height = handCardWidth * 1.141f;
		for (int i = 0; i < MAX_HAND_CARDS; i++) {
			TextureKey tex = Database.getTextureKey("00000");
			HandCard2D handCard = new HandCard2D("HandCard" + i, tex, handCardWidth, height, i) {
				@Override
				public void mouseSelect() {
					handCardSelected(this.getIndex());
				}

				@Override
				public void mouseSelectRightClick() {
					handCardSelectedRightClick(this.getIndex());
				}
			};
			handCard.setCurrentScrollIndex(this.currentScrollIndex);
			handCard.setLocalTranslation(startPoint + (handCardWidth + epsilon) * i, yPos, level);
			this.attachChild(handCard);
			this.handCards.add(handCard);
			handCard.setVisible(false);
			handCard.update();
		}

		if (this.enemyHand) {
			for (int i = 0; i < MAX_HAND_CARDS; i++) {
				LinearFunction xFunc = new LinearFunction(0, startPoint + (handCardWidth + epsilon) * i, AnimationParameters.CARD_DRAW_TIME,
						newStartPoint + (handCardWidth + epsilon) * (i + 1));
				this.xFunction.add(xFunc);
			}
		} else {
			for (int i = 0; i < MAX_HAND_CARDS; i++) {
				LinearFunction xFunc = new LinearFunction(0, startPoint + (handCardWidth + epsilon) * i, AnimationParameters.CARD_DRAW_TIME,
						newStartPoint + (handCardWidth + epsilon) * (i - 1));
				this.xFunction.add(xFunc);
			}
		}

		scrollLeftButton = new TextButton2D("scrollLeftButton", "<", screenWidth * 0.01f, height) {
			@Override
			public void mouseSelectRightClick() {
			}

			@Override
			public void mouseSelect() {
				scrollLeftClicked();
			}
		};
		scrollLeftButton.setLocalTranslation(startPoint - 2 * epsilon, yPos, level + 0.00001f);
		scrollLeftButton.setVisible(false);
		scrollLeftButton.update();
		this.attachChild(scrollLeftButton);

		scrollRightButton = new TextButton2D("scrollRightButton", ">", screenWidth * 0.01f, height) {
			@Override
			public void mouseSelectRightClick() {

			}

			@Override
			public void mouseSelect() {
				scrollRightClicked();
			}
		};
		scrollRightButton.setLocalTranslation(startPoint + (handCardWidth + epsilon) * 9, yPos, level + 0.00001f);
		scrollRightButton.setVisible(false);
		scrollRightButton.update();
		this.attachChild(scrollRightButton);
	}

	@Override
	public boolean mouseOver(float xPos, float yPos) {
		return false;
	}

	@Override
	public synchronized void update() {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Align hand cards:
		{
			int screenWidth = GUI2D.getInstance().getResolution().getKey();
			int cardSize = cards.size();
			int size = cardSize >= MAX_HAND_CARDS ? MAX_HAND_CARDS : cardSize;
			float epsilon = screenWidth * 0.01f; // distance between two cards
			float handCardWidth = screenWidth * 0.06f; // Size of one single hand card

			float startPoint = 0;
			if (size % 2 == 0)
				startPoint = xPos - ((handCardWidth) * (size / 2 - 1) + handCardWidth / 2 + (epsilon / 2) + epsilon * ((size / 2) - 1)) - handCardWidth / 2;
			else
				startPoint = xPos - ((handCardWidth / 2) * (size - 1) + epsilon * (size / 2)) - handCardWidth / 2;

			// Update Linear functions:
			this.xFunction.clear();
			if (this.enemyHand) {
				for (int i = size - 1; i >= 0; i--) {
					LinearFunction xFunc = new LinearFunction(0, startPoint + (handCardWidth + epsilon) * i, AnimationParameters.CARD_DRAW_TIME,
							startPoint + (handCardWidth + epsilon) * (i) + handCardWidth / 2 + epsilon / 2);
					this.xFunction.add(xFunc);
				}
			} else {
				for (int i = 0; i < size; i++) {
					LinearFunction xFunc = new LinearFunction(0, startPoint + (handCardWidth + epsilon) * i, AnimationParameters.CARD_DRAW_TIME,
							startPoint + (handCardWidth + epsilon) * (i) - handCardWidth / 2 + epsilon / 2);
					this.xFunction.add(xFunc);
				}
			}

			if (!this.enemyHand) {
				for (int i = 0; i < size; i++) {
					HandCard2D handCard = this.handCards.get(i);
					handCard.setLocalTranslation(startPoint + (handCardWidth + epsilon) * i, yPos, level);
				}
			} else {
				int j = 0;
				for (int i = size - 1; i >= 0; i--) {
					HandCard2D handCard = this.handCards.get(j);
					handCard.setLocalTranslation(startPoint + (handCardWidth + epsilon) * j, yPos, level);
					j++;
				}
			}
		}

		if (this.currentScrollIndex > 0)
			this.scrollLeftButton.setVisible(true);
		else
			this.scrollLeftButton.setVisible(false);

		if (this.currentScrollIndex + MAX_HAND_CARDS < this.cards.size() && scrollingAllowed)
			this.scrollRightButton.setVisible(true);
		else
			this.scrollRightButton.setVisible(false);

		// Update:
		for (int i = 0; i < MAX_HAND_CARDS; i++)
			this.handCards.get(i).update();
		this.scrollLeftButton.update();
		this.scrollRightButton.update();
		lock.unlock();
	}

	public void setScrollButtonsActivated(boolean flag) {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.scrollingAllowed = flag;
		this.currentScrollIndex = 0;
		if (this.cards.size() > MAX_HAND_CARDS && flag)
			this.scrollRightButton.setVisible(true);
		else
			this.scrollRightButton.setVisible(false);
		this.scrollLeftButton.setVisible(false);

		for (int i = 0; i < MAX_HAND_CARDS; i++) {
			HandCard2D handCard = this.handCards.get(i);
			handCard.setCurrentScrollIndex(currentScrollIndex);
		}
		lock.unlock();
	}

	public float getXCoordForNewCard() {
		int screenWidth = GUI2D.getInstance().getResolution().getKey();
		int size = (cards.size() >= MAX_HAND_CARDS ? MAX_HAND_CARDS : cards.size()) + 1;
		float epsilon = screenWidth * 0.01f; // distance between two cards
		float handCardWidth = screenWidth * 0.06f; // Size of one single hand card
		float startPoint = 0;
		if (size % 2 == 0)
			startPoint = xPos - ((handCardWidth) * (size / 2 - 1) + handCardWidth / 2 + (epsilon / 2) + epsilon * ((size / 2) - 1)) - handCardWidth / 2;
		else
			startPoint = xPos - ((handCardWidth / 2) * (size - 1) + epsilon * (size / 2)) - handCardWidth / 2;
		if (!this.enemyHand)
			return startPoint + (handCardWidth + epsilon) * (size - 1);
		return startPoint;
	}

	@Override
	public void startAnimation() {

	}

	@Override
	public void resetAnimation() {
		this.animationTime = 0;
	}

	@Override
	public boolean animationDone() {
		return this.animationTime == AnimationParameters.CARD_DRAW_TIME;
	}

	@Override
	public void simpleUpdate(float tpf) {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.animationTime = this.animationTime + tpf;
		if (this.animationTime > AnimationParameters.CARD_DRAW_TIME)
			this.animationTime = AnimationParameters.CARD_DRAW_TIME;

//		int cardSize = cards.size();
//		int size = cardSize >= MAX_HAND_CARDS ? MAX_HAND_CARDS : cardSize;
		int size = updateXFunction();
		if (!this.enemyHand) {
			for (int i = 0; i < size; i++) {
				HandCard2D handCard = this.handCards.get(i);
				LinearFunction func = this.xFunction.get(i);
				handCard.setLocalTranslation(func.function(animationTime), yPos, level);
			}
		} else {
			int j = 0;
			for (int i = size - 1; i >= 0; i--) {
				HandCard2D handCard = this.handCards.get(i);
				LinearFunction func = this.xFunction.get(j);
				handCard.setLocalTranslation(func.function(animationTime), yPos, level);
				j++;
			}
		}

		lock.unlock();
	}
	
	private int updateXFunction(){
		int screenWidth = GUI2D.getInstance().getResolution().getKey();
		int cardSize = cards.size();
		int size = cardSize >= MAX_HAND_CARDS ? MAX_HAND_CARDS : cardSize;
		float epsilon = screenWidth * 0.01f; // distance between two cards
		float handCardWidth = screenWidth * 0.06f; // Size of one single hand card

		float startPoint = 0;
		if (size % 2 == 0)
			startPoint = xPos - ((handCardWidth) * (size / 2 - 1) + handCardWidth / 2 + (epsilon / 2) + epsilon * ((size / 2) - 1)) - handCardWidth / 2;
		else
			startPoint = xPos - ((handCardWidth / 2) * (size - 1) + epsilon * (size / 2)) - handCardWidth / 2;

		// Update Linear functions:
		this.xFunction.clear();
		if (this.enemyHand) {
			for (int i = size - 1; i >= 0; i--) {
				LinearFunction xFunc = new LinearFunction(0, startPoint + (handCardWidth + epsilon) * i, AnimationParameters.CARD_DRAW_TIME,
						startPoint + (handCardWidth + epsilon) * (i) + handCardWidth / 2 + epsilon / 2);
				this.xFunction.add(xFunc);
			}
		} else {
			for (int i = 0; i < size; i++) {
				LinearFunction xFunc = new LinearFunction(0, startPoint + (handCardWidth + epsilon) * i, AnimationParameters.CARD_DRAW_TIME,
						startPoint + (handCardWidth + epsilon) * (i) - handCardWidth / 2 + epsilon / 2);
				this.xFunction.add(xFunc);
			}
		}
		return size;
	}
	
	public void handCardSelected(int index) {
		IngameController parent = (IngameController) this.getParent();
		parent.handCardGeometrySelected(handCards.get(index));
	}

	public void handCardSelectedRightClick(int index) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CardViewer viewer = GUI2D.getInstance().getIngameController().getCardViewer();
				viewer.setVisible(true);
				List<Card> cardList = new ArrayList<>();
				cardList.add(cards.get(index));
				viewer.setData("Cards", cardList);
				GUI2D.getInstance().addToUpdateQueue(viewer);
			}
		}).start();
	}

	public float getxPos() {
		return this.xPos;
	}

	public float getyPos() {
		return this.yPos;
	}

	@Override
	public void mouseEnter() {
		// Do nothing here
	}

	@Override
	public void mouseExit() {
		// Do nothing here
	}

	@Override
	public void mouseSelect() {
		// Do nothing here
	}

	public void mousePressed() {
		// Do nothing here!
	}

	public void mouseReleased() {
		// Do nothing here!
	}

	@Override
	public void audioSelect() {
		// Do nothing here!
	}

	@Override
	public boolean isGlowing() {
		return false;
	}

	@Override
	public void setGlowing(boolean value) {
		// Do nothing here
	}

	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public void setSelected(boolean value) {
		// Do nothing here
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	@Override
	public synchronized void setVisible(boolean value) {

	}

	@Override
	public int getLevel() {
		return level;
	}

	public List<HandCard2D> getHandCard2Ds() {
		return this.handCards;
	}

	public HandCard2D getHandCard(int index) {
		return handCards.get(index);
	}

	public void setCards(List<Card> cards) {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		this.cards = cards;
		for (int i = 0; i < MAX_HAND_CARDS; i++) {
			HandCard2D handCard = this.handCards.get(i);
			// Create a hand card for each card of position:
			TextureKey tex = null;
			if (cards.size() > i)
				tex = Database.getTextureKey(cards.get(i).getCardId());
			if (tex == null) // Card not visible
				tex = Database.getTextureKey("00000");

			handCard.setTexture(tex);
			if (cards.size() > i)
				handCard.setVisible(true);
			else
				handCard.setVisible(false);
		}
		this.lock.unlock();
	}

	@Override
	public void mouseSelectRightClick() {

	}

	public Vector2f getSize() {
		// HandCardManager has no size on the screen!
		return new Vector2f(0, 0);
	}

	protected void scrollRightClicked() {
		this.currentScrollIndex++;
		if (this.currentScrollIndex + MAX_HAND_CARDS == this.cards.size())
			this.scrollRightButton.setVisible(false);
		else
			this.scrollRightButton.setVisible(true);
		new Thread(new Runnable() {
			@Override
			public void run() {
				GUI2D.getInstance().addToUpdateQueue(self);
			}
		}).start();

		// Set textures for hand cards:
		for (int i = 0; i < MAX_HAND_CARDS; i++) {
			int modelIndex = i + currentScrollIndex;
			HandCard2D handCard = this.handCards.get(i);
			handCard.setCurrentScrollIndex(currentScrollIndex);
			// Create a hand card for each card of position:
			TextureKey tex = null;
			if (cards.size() > i)
				tex = Database.getTextureKey(cards.get(modelIndex).getCardId());
			if (tex == null) // Card not visible
				tex = Database.getTextureKey("00000");
			handCard.setTexture(tex);

			// Make it glow:
			if (this.glowingIndices.contains(modelIndex)) {
				handCard.setGlowing(true);
				new Thread(new Runnable() {
					@Override
					public void run() {
						GUI2D.getInstance().getIOController().addShootable(handCard);
						GUI2D.getInstance().addToUpdateQueue(handCard);
					}
				}).start();
			} else {
				handCard.setGlowing(false);
				new Thread(new Runnable() {
					@Override
					public void run() {
						GUI2D.getInstance().getIOController().removeShootable(handCard);
						GUI2D.getInstance().addToUpdateQueue(handCard);
					}
				}).start();
			}
		}
	}

	protected void scrollLeftClicked() {
		this.currentScrollIndex--;
		if (this.currentScrollIndex == 0)
			this.scrollLeftButton.setVisible(false);
		else
			this.scrollLeftButton.setVisible(true);
		new Thread(new Runnable() {
			@Override
			public void run() {
				GUI2D.getInstance().addToUpdateQueue(self);
			}
		}).start();

		// Set textures for hand cards:
		for (int i = 0; i < MAX_HAND_CARDS; i++) {
			int modelIndex = i + currentScrollIndex;
			HandCard2D handCard = this.handCards.get(i);
			handCard.setCurrentScrollIndex(currentScrollIndex);
			// Create a hand card for each card of position:
			TextureKey tex = null;
			if (cards.size() > i)
				tex = Database.getTextureKey(cards.get(modelIndex).getCardId());
			if (tex == null) // Card not visible
				tex = Database.getTextureKey("00000");
			handCard.setTexture(tex);

			// Make it glow:
			if (this.glowingIndices.contains(modelIndex)) {
				handCard.setGlowing(true);
				new Thread(new Runnable() {
					@Override
					public void run() {
						GUI2D.getInstance().getIOController().addShootable(handCard);
						GUI2D.getInstance().addToUpdateQueue(handCard);
					}
				}).start();
			} else {
				handCard.setGlowing(false);
				new Thread(new Runnable() {
					@Override
					public void run() {
						GUI2D.getInstance().getIOController().removeShootable(handCard);
						GUI2D.getInstance().addToUpdateQueue(handCard);
					}
				}).start();
			}
		}
	}

	public void clearGlowing() {
		glowingIndices.clear();
	}

	public void setIndexGlowing(int i) {
		glowingIndices.add(i);
		if (i < MAX_HAND_CARDS) {
			SelectableNode node = handCards.get(i);
			node.setGlowing(true);
			GUI2D.getInstance().getIOController().addShootable(node);
			GUI2D.getInstance().addToUpdateQueue(node);
		}
	}
}
