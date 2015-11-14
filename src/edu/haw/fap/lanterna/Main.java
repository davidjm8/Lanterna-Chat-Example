package edu.haw.fap.lanterna;

public class Main {
	
	public static void main(String[] args) {
		ScreenDrawer screenDrawer = new ScreenDrawer();
		ScreenInpuReader inputReader = new ScreenInpuReader(screenDrawer.getTerminal(), screenDrawer.getScreen());
		ScreenController controller = new ScreenController(screenDrawer, inputReader);
		inputReader.setController(controller);
		screenDrawer.start();
		inputReader.start();
	}
}
