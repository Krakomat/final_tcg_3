package gui2d.animations;

import network.tcp.messages.ByteString;

public class CardDrawAnimation extends Animation {

	// TODO which information for card draw?

	public CardDrawAnimation() {
		this.animationType = AnimationType.CARD_DRAW;
	}

	@Override
	public ByteString packAnimation() {
		// TODO Auto-generated method stub
		return null;
	}

}
