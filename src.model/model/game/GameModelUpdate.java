package model.game;

import java.util.ArrayList;
import java.util.List;

import model.enums.PositionID;
import model.interfaces.Position;

public class GameModelUpdate {
	private List<Position> updateList;
	private GameModelParameters gameModelParameters;

	public GameModelUpdate() {
		updateList = new ArrayList<Position>();
		gameModelParameters = new GameModelParameters();
	}

	public void add(Position position) {
		updateList.add(position);
	}

	public List<Position> getPositionList() {
		return updateList;
	}

	public Position getPosition(PositionID id) {
		for (Position p : updateList)
			if (p.getPositionID() == id)
				return p;
		return null;
	}

	public void setPositionList(List<Position> posList) {
		this.updateList = posList;
	}

	public GameModelParameters getGameModelParameters() {
		return gameModelParameters;
	}

	public void setGameModelParameters(GameModelParameters gameModelParameters) {
		this.gameModelParameters = gameModelParameters;
	}
}
