package edu.haw.fap.lanterna;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.ScreenWriter;
import com.googlecode.lanterna.screen.TabBehaviour;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;

public class ScreenDrawer extends Thread {
	
	
	
	private static final int MAX_MESSAGE_LENGTH = 300;
	private Terminal terminal;
	private Screen screen;
	private ScreenWriter writer;
	private boolean keepRunning;
	
	private List<String> chatArea;
	private String inputArea;
	private int chatAreaSize;
	
	public ScreenDrawer() {
		this.terminal = TerminalFacade.createTerminal(System.in, System.out, Charset.forName("UTF8"));
		this.screen = new Screen(terminal);
		this.screen.setTabBehaviour(TabBehaviour.CONVERT_TO_FOUR_SPACES);
		this.writer = new ScreenWriter(screen);
		this.writer.setForegroundColor(Terminal.Color.BLACK);
		this.writer.setBackgroundColor(Terminal.Color.WHITE);
	}

	@Override
	public void run() {
		super.run();
		this.startGui();
		this.keepRunning = true;
		this.inputArea = "";
		this.chatArea = new ArrayList<>();
		
		while(keepRunning) {
			screen.clear();
			TerminalSize screenSize = terminal.getTerminalSize();
	        drawChatArea(screenSize);
	        drawSeparatingLine(screenSize);
	        drawInputArea(screenSize);
	        this.screen.refresh();
	    }
	}

	private void drawChatArea(TerminalSize screenSize) {
		chatAreaSize = screenSize.getRows() - 2;
		
		List<String> chatLinesSplit = new ArrayList<>();
		for (String string : this.chatArea) {
			int visibleColumns = screenSize.getColumns() - 1;
			if (string.length() > visibleColumns) {
				string = splitLines(chatLinesSplit, string, visibleColumns);
			} else {
				chatLinesSplit.add(string);
			}
		}
		int visibleChatAreaStart = chatLinesSplit.size() - screenSize.getRows() + 2;
		if (visibleChatAreaStart < 0) {
			visibleChatAreaStart = 0;
		}
        for (int i = 0; (i < chatAreaSize) && (i < chatLinesSplit.size()); i++) {
            this.writer.drawString(0, i, chatLinesSplit.get(visibleChatAreaStart));
            visibleChatAreaStart++;
        }
	}

	private String splitLines(List<String> chatLinesSplit, String string, int visibleColumns) {
		while (string.length() > visibleColumns) {
			String current = string.substring(0, visibleColumns);
			chatLinesSplit.add(current);
			string = string.substring(visibleColumns);
		}
		chatLinesSplit.add(string);
		return string;
	}

	private void drawInputArea(TerminalSize screenSize) {
		int visibleInputAreaStart = inputArea.length() - screenSize.getColumns();
		if (visibleInputAreaStart < 0) {
			visibleInputAreaStart = 0;
		}
		this.writer.drawString(0, screenSize.getRows() - 1, inputArea.substring(visibleInputAreaStart));
	}

	private void drawSeparatingLine(TerminalSize screenSize) {
		String line = "";
		for (int i = 0; i < screenSize.getColumns(); i++) {
			line += "=";
		}
		this.writer.drawString(0, screenSize.getRows() - 2, line);
	}

	private void startGui() {
		this.screen.startScreen();
	}
	
	public synchronized void writeToInputArea(char character) {
		if (this.inputArea.length() < MAX_MESSAGE_LENGTH) {
			this.inputArea += character;
		}
	}
	
	public synchronized void deleteOneFromInputArea() {
		if (this.inputArea.length() > 0) {
			this.inputArea = this.inputArea.substring(0, inputArea.length() - 1);
		}
	}
	
	public Terminal getTerminal() {
		return terminal;
	}

	public Screen getScreen() {
		return screen;
	}

	public synchronized void printMessageToSelf() {
		this.chatArea.add(this.inputArea);
		this.inputArea = "";
	}

	public boolean inputAreaIsEmpty() {
		return this.inputArea.isEmpty();
	}

}
