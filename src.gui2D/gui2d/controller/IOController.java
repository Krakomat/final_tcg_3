package gui2d.controller;

import java.util.ArrayList;
import java.util.List;

import gui2d.abstracts.SelectableNode;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector2f;

import common.utilities.Lock;

/**
 * Handles all the inputs that come from the mouse or the keyboard.
 * 
 * @author Michael
 *
 */
public class IOController {

	private InputManager inputManager;
	/** Contains nodes that can be selected by the mouse */
	private List<SelectableNode> shootables, rightClickShootables, storedShootables;
	/** Node on which the mouse is positioned currently */
	private SelectableNode currentMouseOverNode;
	private boolean rightClickAllowed;
	
	private Lock lock;

	public IOController(InputManager inputManager) {
		this.inputManager = inputManager;
		this.shootables = new ArrayList<>();
		this.storedShootables = new ArrayList<>();
		this.rightClickShootables = new ArrayList<>();
		this.currentMouseOverNode = null;
		this.setRightClickAllowed(true);
		this.lock = new Lock();
	}

	public void initKeys() {
		inputManager.setCursorVisible(true);

		inputManager.addMapping("MouseClick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT)); // Trigger: leftclick
		inputManager.addMapping("MouseClickRight", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT)); // Trigger: rightclick
		inputManager.addMapping("ActivateFlyCam", new KeyTrigger(KeyInput.KEY_RETURN)); // Trigger: Return
		inputManager.addMapping("RotateRight", new MouseAxisTrigger(MouseInput.AXIS_X, true));
		inputManager.addMapping("RotateLeft", new MouseAxisTrigger(MouseInput.AXIS_X, false));
		inputManager.addMapping("RotateUp", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
		inputManager.addMapping("RotateDown", new MouseAxisTrigger(MouseInput.AXIS_Y, false));

		inputManager.addListener(actionListener, "MouseClick");
		inputManager.addListener(actionListener, "MouseClickRight");
		inputManager.addListener(actionListener, "ActivateFlyCam");

		inputManager.addListener(analogListener, "RotateRight");
		inputManager.addListener(analogListener, "RotateLeft");
		inputManager.addListener(analogListener, "RotateUp");
		inputManager.addListener(analogListener, "RotateDown");
	}

	/** Defining the "MouseClick" action: Determine what was hit and how to respond. */
	protected ActionListener actionListener = new ActionListener() {

		public void onAction(String name, boolean keyPressed, float tpf) {
			try {
				lock.lock();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (name.equals("MouseClick")) {
				// 1. Reset results list.
				Vector2f click2d = inputManager.getCursorPosition();

				// Compute result:
				SelectableNode shotNode = null;
				List<SelectableNode> shotNodes = new ArrayList<>();
				for (SelectableNode node : shootables) {
					if (node.mouseOver(click2d.x, click2d.y))
						shotNodes.add(node);
				}
				// Get the node with the highest level:
				for (SelectableNode node : shotNodes) {
					if (shotNode == null || shotNode.getLevel() < node.getLevel())
						shotNode = node;
				}

				// 3. Use the result:
				if (shotNode != null) {
					if (!keyPressed) {
						shotNode.audioSelect();
						shotNode.mouseSelect();
						shotNode.mouseReleased();
					} else
						shotNode.mousePressed();
				}
			} else if (name.equals("MouseClickRight") && rightClickAllowed) {
				// 1. Reset results list.
				Vector2f click2d = inputManager.getCursorPosition();

				// Compute result:
				SelectableNode shotNode = null;
				List<SelectableNode> shotNodes = new ArrayList<>();
				for (SelectableNode node : rightClickShootables) {
					if (node.mouseOver(click2d.x, click2d.y))
						shotNodes.add(node);
				}
				// Get the node with the highest level:
				for (SelectableNode node : shotNodes) {
					if (shotNode == null || shotNode.getLevel() < node.getLevel())
						shotNode = node;
				}

				// 3. Use the result:
				if (shotNode != null && keyPressed) {
					shotNode.mouseSelectRightClick();
				}
			}
			lock.unlock();
		}
	};

	protected AnalogListener analogListener = new AnalogListener() {
		@Override
		public void onAnalog(String name, float value, float tpf) {
			if (name.equals("RotateLeft") || name.equals("RotateRight") || name.equals("RotateUp") || name.equals("RotateDown")) {
				try {
					lock.lock();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// 1. Reset results list.
				Vector2f click2d = inputManager.getCursorPosition();

				// Compute result:
				SelectableNode shotNode = null;
				List<SelectableNode> shotNodes = new ArrayList<>();
				for (SelectableNode node : shootables) {
					if (node.mouseOver(click2d.x, click2d.y))
						shotNodes.add(node);
				}
				// Get the node with the highest level:
				for (SelectableNode node : shotNodes) {
					if (shotNode == null || shotNode.getLevel() < node.getLevel())
						shotNode = node;
				}

				// 3. Use the result:
				if (shotNode != null && currentMouseOverNode != null) {
					if (!shotNode.equals(currentMouseOverNode)) {
						currentMouseOverNode.mouseExit();
						currentMouseOverNode = shotNode;
						currentMouseOverNode.mouseEnter();
					}
				} else if (shotNode == null && currentMouseOverNode != null) {
					currentMouseOverNode.mouseExit();
					currentMouseOverNode = null;
				} else if (shotNode != null && currentMouseOverNode == null) {
					currentMouseOverNode = shotNode;
					currentMouseOverNode.mouseEnter();
				}
				lock.unlock();
			}
		}
	};

	public boolean hasShootable(SelectableNode node) {
		try {
			lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		boolean b = this.shootables.contains(node);
		lock.unlock();
		return b;
	}

	/**
	 * Adds a shootable.
	 * 
	 * @param node
	 */
	public synchronized void addShootable(SelectableNode node) {
		try {
			lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (!this.shootables.contains(node))
			this.shootables.add(node);

		lock.unlock();
	}

	/**
	 * Removes a shootable.
	 * 
	 * @param node
	 */
	public synchronized void removeShootable(SelectableNode node) {
		try {
			lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (this.shootables.contains(node))
			this.shootables.remove(node);

		lock.unlock();
	}

	/**
	 * Clears the list of shootables and returns them in a list.
	 */
	public List<SelectableNode> clearShootables() {
		try {
			lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		List<SelectableNode> sList = new ArrayList<>();
		for (SelectableNode shootable : this.shootables)
			sList.add(shootable);
		this.shootables.clear();

		lock.unlock();
		return sList;
	}

	public boolean hasRightShootable(SelectableNode node) {
		try {
			lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		boolean b = this.rightClickShootables.contains(node);
		lock.unlock();
		return b;
	}

	/**
	 * Adds a shootable for right clicking.
	 * 
	 * @param node
	 */
	public synchronized void addRightShootable(SelectableNode node) {
		try {
			lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (!this.rightClickShootables.contains(node))
			this.rightClickShootables.add(node);

		lock.unlock();
	}

	/**
	 * Removes a shootable for right clicking.
	 * 
	 * @param node
	 */
	public synchronized void removeRightShootable(SelectableNode node) {
		try {
			lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (this.rightClickShootables.contains(node))
			this.rightClickShootables.remove(node);

		lock.unlock();
	}

	/**
	 * Stores all nodes that are currently shootable in a separate list.
	 */
	public synchronized void storeShootables() {
		try {
			lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		this.storedShootables.clear();
		for (SelectableNode node : this.shootables)
			this.storedShootables.add(node);
		this.shootables.clear();

		lock.unlock();
	}

	/**
	 * Restores all nodes that have been stored previously in {@link #storeShootables()}. Deletes all nodes that have been added between storing and restoring from
	 * the shootables list.
	 */
	public synchronized void restoreShootables() {
		try {
			lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		this.shootables.clear();
		for (SelectableNode node : this.storedShootables)
			this.shootables.add(node);
		lock.unlock();
	}

	public boolean isRightClickAllowed() {
		return rightClickAllowed;
	}

	public void setRightClickAllowed(boolean rightClickAllowed) {
		this.rightClickAllowed = rightClickAllowed;
	}
}
