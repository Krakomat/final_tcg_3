package model.game;

import java.util.ArrayList;
import java.util.List;

import model.enums.PositionID;
import model.interfaces.GameModelUpdate;
import model.interfaces.Position;

public class GameModelUpdateImpl implements GameModelUpdate {
	private List<Position> updateList;
	private short turnNumber;
	private boolean energyPlayAllowed;

	public GameModelUpdateImpl() {
		updateList = new ArrayList<Position>();
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

	public short getTurnNumber() {
		return turnNumber;
	}

	public void setTurnNumber(short turnNumber) {
		this.turnNumber = turnNumber;
	}

	public boolean isEnergyPlayAllowed() {
		return energyPlayAllowed;
	}

	public void setEnergyPlayAllowed(boolean energyPlayAllowed) {
		this.energyPlayAllowed = energyPlayAllowed;
	}
}
