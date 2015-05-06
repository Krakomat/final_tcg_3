package model.game;

import java.util.ArrayList;
import java.util.List;

import model.enums.PositionID;
import model.interfaces.GameModelUpdate;
import model.interfaces.Position;

public class GameModelUpdateImpl implements GameModelUpdate {
	private List<Position> updateList;
	private GameModelParameters gameModelParameters;

	public GameModelUpdateImpl() {
		updateList = new ArrayList<Position>();
		gameModelParameters = new GameModelParameters();
	}

	@Override
	public void add(Position position) {
		updateList.add(position);
	}

	@Override
	public List<Position> getPositionList() {
		return updateList;
	}

	public Position getPosition(PositionID id) {
		for (Position p : updateList)
			if (p.getPositionID() == id)
				return p;
		return null;
	}

	@Override
	public void setPositionList(List<Position> posList) {
		this.updateList = posList;
	}

	@Override
	public GameModelParameters getGameModelParameters() {
		return gameModelParameters;
	}

	@Override
	public void setGameModelParameters(GameModelParameters gameModelParameters) {
		this.gameModelParameters = gameModelParameters;
	}
}
