package edu.haw.fap.lanterna;


public class ScreenController {

	private ScreenDrawer screenDrawer;
	private ScreenInpuReader inputReader;

	public ScreenController(ScreenDrawer screenDrawer, ScreenInpuReader inputReader) {
		this.screenDrawer = screenDrawer;
		this.inputReader = inputReader;
	}

	public void writeToInputArea(char character) {
		this.screenDrawer.writeToInputArea(character);
	}

	public void deleteOneFromInputArea() {
		this.screenDrawer.deleteOneFromInputArea();
	}

	public void sendMessage() {
		if (!this.screenDrawer.inputAreaIsEmpty()) {
			this.screenDrawer.printMessageToSelf();
			// send to server
		}
	}

}
