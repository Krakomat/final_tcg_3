package gui2d.controller;

import gui2d.GUI2D;
import gui2d.abstracts.SelectableNode;
import gui2d.animations.AnimateableObject;
import gui2d.animations.Animation;
import gui2d.animations.AnimationParameters;
import gui2d.animations.CardDrawAnimation;
import gui2d.animations.CardDrawAnimationImage;
import gui2d.animations.CardMoveAnimation;
import gui2d.animations.CardMoveAnimationImage;
import gui2d.animations.CoinflipAnimation;
import gui2d.animations.CoinflipAnimationModel;
import gui2d.animations.DamageAnimation;
import gui2d.animations.DamageAnimationImage;
import gui2d.geometries.HandCardManager2D;

import java.util.ArrayList;
import java.util.List;

import com.jme3.scene.Node;

import model.database.Database;
import model.enums.Color;
import model.enums.PositionID;
import model.enums.Sounds;
import common.utilities.Lock;

/**
 * Maintains a list with all animation objects that are currently being
 * animated.
 * 
 * @author Michael
 *
 */
public class AnimationController {

	private List<AnimateableObject> animatedObjects;
	private Lock lock;
	private Color ownColor;

	public AnimationController() {
		this.animatedObjects = new ArrayList<>();
		this.lock = new Lock();
	}

	/**
	 * Simple update method called from the main simple update method in the
	 * gui.
	 * 
	 * @param tpf
	 *            time per frame in miliseconds
	 * @throws InterruptedException
	 */
	public void simpleUpdate(float tpf) throws InterruptedException {
		this.lock.lock();

		for (AnimateableObject animObj : animatedObjects)
			animObj.simpleUpdate(tpf);

		for (int i = 0; i < this.animatedObjects.size(); i++) {
			if (this.animatedObjects.get(i).animationDone()) {
				this.animatedObjects.remove(i);
				i--;
			}
		}
		this.lock.unlock();
	}

	/**
	 * Adds an animateable object to this controller.
	 * 
	 * @param animation
	 * @throws InterruptedException
	 */
	public AnimateableObject[] addAnimation(Animation animation) throws InterruptedException {
		GUI2D gui = GUI2D.getInstance();
		this.lock.lock();
		AnimateableObject[] animObjects = null;
		float screenWidth = GUI2D.getInstance().getResolution().getKey();
		float screenHeight = GUI2D.getInstance().getResolution().getValue();
		switch (animation.getAnimationType()) {
		case CARD_DRAW:
			CardDrawAnimation drawAnimation = (CardDrawAnimation) animation;
			if (drawAnimation.getPlayerColor() != null) {
				Color drawColor = drawAnimation.getPlayerColor();
				Node deck = (Node) gui.getIngameController().getPositionGeometry(drawColor == Color.BLUE ? PositionID.BLUE_DECK : PositionID.RED_DECK, ownColor);
				HandCardManager2D hand = (HandCardManager2D) gui.getIngameController().getPositionGeometry(drawColor == Color.BLUE ? PositionID.BLUE_HAND : PositionID.RED_HAND,
						ownColor);

				animObjects = new AnimateableObject[2];
				AnimateableObject animObj = new CardDrawAnimationImage("AnimatedHandCard", Database.getTextureKey("00000"), screenWidth * 0.06f, screenWidth * 0.06f * 1.141f,
						deck.getLocalTranslation().x, deck.getLocalTranslation().y, hand.getXCoordForNewCard(), hand.getyPos(), AnimationParameters.CARD_DRAW_TIME);

				this.animatedObjects.add(animObj);
				this.animatedObjects.add(hand);
				animObjects[0] = animObj;
				animObjects[1] = hand;
			} else {
				animObjects = new AnimateableObject[4];
				{
					Node deck = (Node) gui.getIngameController().getPositionGeometry(PositionID.BLUE_DECK, ownColor);
					HandCardManager2D hand = (HandCardManager2D) gui.getIngameController().getPositionGeometry(PositionID.BLUE_HAND, ownColor);

					AnimateableObject animObj = new CardDrawAnimationImage("AnimatedHandCard", Database.getTextureKey("00000"), screenWidth * 0.06f, screenWidth * 0.06f * 1.141f,
							deck.getLocalTranslation().x, deck.getLocalTranslation().y, hand.getXCoordForNewCard(), hand.getyPos(), AnimationParameters.CARD_DRAW_TIME);

					this.animatedObjects.add(animObj);
					this.animatedObjects.add(hand);
					animObjects[0] = animObj;
					animObjects[1] = hand;
				}
				{
					Node deck = (Node) gui.getIngameController().getPositionGeometry(PositionID.RED_DECK, ownColor);
					HandCardManager2D hand = (HandCardManager2D) gui.getIngameController().getPositionGeometry(PositionID.RED_HAND, ownColor);

					AnimateableObject animObj = new CardDrawAnimationImage("AnimatedHandCard", Database.getTextureKey("00000"), screenWidth * 0.06f, screenWidth * 0.06f * 1.141f,
							deck.getLocalTranslation().x, deck.getLocalTranslation().y, hand.getXCoordForNewCard(), hand.getyPos(), AnimationParameters.CARD_DRAW_TIME);

					this.animatedObjects.add(animObj);
					this.animatedObjects.add(hand);
					animObjects[2] = animObj;
					animObjects[3] = hand;
				}
			}
			EffectController.playSound(Sounds.DRAW);
			break;
		case DAMAGE_POSITION:
			DamageAnimation damageAnimation = (DamageAnimation) animation;
			animObjects = new AnimateableObject[1];
			PositionID posID = damageAnimation.getDamagedPosition();
			SelectableNode node = gui.getIngameController().getPositionGeometry(posID, ownColor);
			DamageAnimationImage animObj = new DamageAnimationImage(GUI2D.getInstance().getAssetManager().loadFont("assets/Fonts/DejaVuSans.fnt"), AnimationParameters.DAMAGE_COLOR,
					damageAnimation.getDamageAmount(), node.getLocalTranslation().x + node.getSize().x / 2, node.getLocalTranslation().y + node.getSize().y / 2,
					node.getLocalTranslation().y + node.getSize().y / 2 + screenHeight * 0.12f, AnimationParameters.DAMAGE_TIME);
			this.animatedObjects.add(animObj);
			animObjects[0] = animObj;
			EffectController.playSound(Sounds.DAMAGE);
			break;
		case HEAL_POSITION:
			damageAnimation = (DamageAnimation) animation;
			animObjects = new AnimateableObject[1];
			posID = damageAnimation.getDamagedPosition();
			node = gui.getIngameController().getPositionGeometry(posID, ownColor);
			animObj = new DamageAnimationImage(GUI2D.getInstance().getAssetManager().loadFont("assets/Fonts/DejaVuSans.fnt"), AnimationParameters.HEAL_COLOR,
					damageAnimation.getDamageAmount(), node.getLocalTranslation().x + node.getSize().x / 2, node.getLocalTranslation().y + node.getSize().y / 2,
					node.getLocalTranslation().y + node.getSize().y / 2 + screenHeight * 0.12f, AnimationParameters.DAMAGE_TIME);
			this.animatedObjects.add(animObj);
			animObjects[0] = animObj;
			EffectController.playSound(Sounds.HEAL);
			break;
		case COIN_FLIP:
			CoinflipAnimation coinflipAnimation = (CoinflipAnimation) animation;
			animObjects = new AnimateableObject[1];
			CoinflipAnimationModel coinflipModel = new CoinflipAnimationModel(coinflipAnimation.isHeads(), -1, 0.25f, 2.0f, AnimationParameters.COIN_FLIP_TIME);
			this.animatedObjects.add(coinflipModel);
			animObjects[0] = coinflipModel;
			break;
		case CARD__MOVE:
			CardMoveAnimation moveAnimation = (CardMoveAnimation) animation;
			Node from = (Node) gui.getIngameController().getPositionGeometry(moveAnimation.getFromPosition(), ownColor);
			Node to = (Node) gui.getIngameController().getPositionGeometry(moveAnimation.getToPosition(), ownColor);

			animObjects = new AnimateableObject[1];
			CardMoveAnimationImage moveImage = new CardMoveAnimationImage(from, to, "AnimatedMovingCard", Database.getTextureKey(moveAnimation.getCardID()), screenWidth * 0.06f,
					screenWidth * 0.06f * 1.141f, moveAnimation.getSoundEffect());

			this.animatedObjects.add(moveImage);
			animObjects[0] = moveImage;
			break;
		default:
			System.err.println("Could not parse AnimationType of animation object in addAnimation!");
			break;
		}

		// Call start animation for objects:
		for (AnimateableObject animObj : animObjects)
			animObj.startAnimation();
		this.lock.unlock();
		return animObjects;
	}

	public void setPlayerColor(Color color) {
		ownColor = color;
	}
}
