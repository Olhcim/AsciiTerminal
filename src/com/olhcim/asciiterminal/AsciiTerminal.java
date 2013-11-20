package com.olhcim.asciiterminal;

import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;

import asciiPanel.AsciiPanel;


public class AsciiTerminal
{
    public final String FORMAT_VAR_PREFIX = "%prefix";
    public final String FORMAT_VAR_ENTER = "skipaline";
    public final String FORMAT_CENTER = "%center";  
    
    int heightInCharacters;
    int widthInCharacters;
    
    private int horrGap;
    private int vertGap;
    private String prefix;
    
    private String typed = "";
    private ArrayList<String> log = new ArrayList<>();
    private int scrollPos;
    
    private boolean inputAfterLog = true;

    
    public AsciiTerminal(AsciiPanel ap) 
    {
    	this(ap, 2, 1, "> ");
    }
    
    public AsciiTerminal(AsciiPanel ap, int horrGap, int vertGap, String prefix)
    {
    	this.horrGap = horrGap;
        this.vertGap = vertGap;
        this.prefix = prefix;
        
        heightInCharacters = ap.getHeightInCharacters();
        widthInCharacters = ap.getWidthInCharacters();

        scrollToStart();
    }
    
    
    public void setPrefix(String a)
    { prefix = a; }
    
    
    
    public final int totalHorrGap()
    { return horrGap + prefix.length() + horrGap; }
    
    public final int pregap()
    { return horrGap + prefix.length(); }
    
    public final int vertGap()
    { return vertGap; }
    
    public final int endPoint()
    { return heightInCharacters - vertGap - 1; }
    
    
    
    public final int startScrollPos()
    { return 0 - vertGap; }
    public final void scrollToStart()
    { scrollPos = 0 - vertGap; }
    
    public final int endScrollPos()
    { return log.size() - endPoint(); }
    public final void scrollToEnd()
    { scrollPos = (log.size() > endPoint() - vertGap) ? endScrollPos() : scrollPos; }
    
    public void setInputAfterLog(boolean a)
    { inputAfterLog = a; }
    public final int inputBarPos()
    { return (inputAfterLog) ? log.size() - scrollPos : endPoint(); }
    
    
    
    public final void log(String a)
    { log.add( a.trim() ); }
    
    public void write(AsciiPanel asciiPanel, String a, int y)
    { write(asciiPanel, a, horrGap, y); }
    
    public void write(AsciiPanel asciiPanel, String a, int x, int y) {
    	a = a.replace(FORMAT_VAR_PREFIX, prefix);
        a = a.replace(FORMAT_VAR_ENTER, "\n");
    	
    	if (a.contains(FORMAT_CENTER)) {
            a = a.replace(FORMAT_CENTER, "");
            asciiPanel.writeCenter(a, y);
    	} else {
            asciiPanel.write(a, x, y);
    	}
    }
    
    
    
    public void paintAll(AsciiPanel asciiPanel) {
        paintLog(asciiPanel);
        paintInput(asciiPanel);
    }
    
    private void paintLog(AsciiPanel asciiPanel) { 
    	asciiPanel.clear();
    	
        for(int i = vertGap(); i < endPoint(); i++) {
            try {
                write(asciiPanel, log.get(i + scrollPos), i);
            } catch (Exception e) {}
        }
    }
    
    private void paintInput(AsciiPanel asciiPanel) {
        try { 
            write(asciiPanel, FORMAT_VAR_PREFIX, inputBarPos());
            write(asciiPanel, typed, pregap(), inputBarPos());
        } catch (Exception e) {}
    }
    
    public void clearLog(AsciiPanel asciiPanel) {
    	log.clear();
    	scrollToStart();
    	asciiPanel.clear();
    }
    
    
    
    
    public void backspacePressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            if(e.isShiftDown()) { typed = ""; }
            else { typed = (typed.length() > 0) ? typed.substring(0, typed.length()-1) : typed; }
        }
    }
    
    public void typableCharacterPressed(KeyEvent e) {
        if (Character.isLetter(e.getKeyChar()) || Character.isSpaceChar(e.getKeyChar())) {
            typed = (typed.length() < widthInCharacters - totalHorrGap()) ? (typed + e.getKeyChar()) : (typed);
        }
    }
    
    public String enterKeyPressed(KeyEvent e) {
        String typedOld = "";
        
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            typedOld = typed;

            log(FORMAT_VAR_PREFIX + typed.trim());
            typed = "";
            scrollToEnd();
        }
        
        return typedOld;
    }

    public void incrementScrollPos(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            if(e.isShiftDown()) { scrollToStart(); }
            else { incrementScrollPos(-1); }
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            if(e.isShiftDown()) { scrollToEnd(); }
            else { incrementScrollPos(1); }
        }
    }
    
    public void incrementScrollPos(MouseWheelEvent me)
    { incrementScrollPos( me.getWheelRotation() ); }
    
    public void incrementScrollPos(int a) {
        int futurePos = scrollPos + a;
        scrollPos = (futurePos >= startScrollPos() && futurePos <= endScrollPos()) ? futurePos : scrollPos;
    }
}
