package edu.haw.fap.lanterna;

import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;

public class ScreenInpuReader extends Thread {

	private Terminal terminal;
	private Screen screen;
	private ScreenController controller;
	private boolean keepRunning;

	public ScreenInpuReader(Terminal terminal, Screen screen) {
		this.terminal = terminal;
		this.screen = screen;
	}
	
	@Override
	public void run() {
		keepRunning = true;
		
		while(keepRunning) {
			Key key = null;
			while (null == key) {
				key = this.screen.readInput();
			}
			this.handleInput(key);
		}
	}
	
	private void handleInput(Key key) {
		if (key.getKind().equals(Key.Kind.NormalKey)) {
			this.controller.writeToInputArea(key.getCharacter());		
		} else if (key.getKind().equals(Key.Kind.Backspace)) {
			this.controller.deleteOneFromInputArea();
		} else if (key.getKind().equals(Key.Kind.Enter)) {
			this.controller.sendMessage();
		}
	}

	public void setController(ScreenController controller) {
		this.controller = controller;
	}

}
