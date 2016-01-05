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
	private final Terminal terminal;
	private final Screen screen;
	private final ScreenWriter writer;
	private boolean keepRunning;
	
	private List<String> chatArea;
	private String inputArea;
	private int chatAreaSize;
	
	public ScreenDrawer() {
		this.terminal = TerminalFacade.createTerminal(System.in, System.out, Charset.forName("UTF8"));
		this.terminal.setCursorVisible(false);
		this.screen = new Screen(this.terminal);
		this.screen.setTabBehaviour(TabBehaviour.CONVERT_TO_FOUR_SPACES);
		this.writer = new ScreenWriter(this.screen);
		this.setTextColors();
	}

	@Override
	public void run() {
		super.run();
		this.startGui();
		this.keepRunning = true;
		this.inputArea = "";
		this.chatArea = new ArrayList<>();
		
		while(this.keepRunning) {
			this.screen.clear();
			final TerminalSize screenSize = this.terminal.getTerminalSize();
	        this.drawChatArea(screenSize);
	        this.drawSeparatingLine(screenSize);
	        this.drawInputArea(screenSize);
	        this.screen.refresh();
	    }
	}

	private void drawChatArea(final TerminalSize screenSize) {
		this.setTextColors();
		this.chatAreaSize = screenSize.getRows() - 2;
		
		final List<String> chatLinesSplit = new ArrayList<>();
		for (String string : this.chatArea) {
			final int visibleColumns = screenSize.getColumns() - 1;
			if (string.length() > visibleColumns) {
				string = this.splitLines(chatLinesSplit, string, visibleColumns);
			} else {
				chatLinesSplit.add(string);
			}
		}
		int visibleChatAreaStart = chatLinesSplit.size() - screenSize.getRows() + 2;
		if (visibleChatAreaStart < 0) {
			visibleChatAreaStart = 0;
		}
        for (int i = 0; (i < this.chatAreaSize) && (i < chatLinesSplit.size()); i++) {
            this.writer.drawString(0, i, chatLinesSplit.get(visibleChatAreaStart));
            visibleChatAreaStart++;
        }
	}

	private String splitLines(final List<String> chatLinesSplit, String string, final int visibleColumns) {
		while (string.length() > visibleColumns) {
			final String current = string.substring(0, visibleColumns);
			chatLinesSplit.add(current);
			string = string.substring(visibleColumns);
		}
		chatLinesSplit.add(string);
		return string;
	}

	private void drawInputArea(final TerminalSize screenSize) {
		this.setTextColors();
		int visibleInputAreaStart = this.inputArea.length() - screenSize.getColumns();
		if (visibleInputAreaStart < 0) {
			visibleInputAreaStart = 0;
		}
		this.writer.drawString(0, screenSize.getRows() - 1, this.inputArea.substring(visibleInputAreaStart));
	}

	private void drawSeparatingLine(final TerminalSize screenSize) {
		this.setSeparatorColors();
		String line = "";
		for (int i = 0; i < screenSize.getColumns(); i++) {
			line += "=";
		}
		this.writer.drawString(0, screenSize.getRows() - 2, line);
	}

	private void startGui() {
		this.screen.startScreen();
	}
	
	public synchronized void writeToInputArea(final char character) {
		if (this.inputArea.length() < MAX_MESSAGE_LENGTH) {
			this.inputArea += character;
		}
	}
	
	public synchronized void deleteOneFromInputArea() {
		if (this.inputArea.length() > 0) {
			this.inputArea = this.inputArea.substring(0, this.inputArea.length() - 1);
		}
	}
	
	private void setTextColors() {
		this.writer.setForegroundColor(Terminal.Color.BLACK);
		this.writer.setBackgroundColor(Terminal.Color.WHITE);
	}
	
	private void setSeparatorColors() {
		this.writer.setForegroundColor(Terminal.Color.WHITE);
		this.writer.setBackgroundColor(Terminal.Color.BLACK);
	}
	
	public Terminal getTerminal() {
		return this.terminal;
	}

	public Screen getScreen() {
		return this.screen;
	}

	public synchronized void printMessageToSelf() {
		this.chatArea.add(this.inputArea);
		this.inputArea = "";
	}

	public boolean inputAreaIsEmpty() {
		return this.inputArea.isEmpty();
	}

}
