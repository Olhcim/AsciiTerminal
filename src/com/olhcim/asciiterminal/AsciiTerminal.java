package com.olhcim.asciiterminal;

import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;

import asciiPanel.AsciiPanel;


public class AsciiTerminal
{
    public final String FORMAT_VAR_PREFIX = "%prefix";
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
    
    
    
    public int totalHorrGap()
    { return horrGap + prefix.length() + horrGap; }
    
    public int pregap()
    { return horrGap + prefix.length(); }
    
    public int vertGap()
    { return vertGap; }
    
    public int endPoint()
    { return heightInCharacters - vertGap - 1; }
    
    
    
    public int startScrollPos()
    { return 0 - vertGap; }
    public void scrollToStart()
    { scrollPos = 0 - vertGap; }
    
    public int endScrollPos()
    { return log.size() - endPoint(); }
    public void scrollToEnd()
    { scrollPos = (log.size() > endPoint() - vertGap) ? endScrollPos() : scrollPos; }
    
    public void setInputAfterLog(boolean a)
    { inputAfterLog = a; }
    public int inputBarPos()
    { return (inputAfterLog) ? log.size() - scrollPos : endPoint(); }
    
    
    public void logln(String a)
    { log(a); log(""); }
    
    public void log(String a)
    {
        if (stringLength(a) > widthInCharacters - totalHorrGap())
        {
            a = wrap(a, widthInCharacters - totalHorrGap());
        }
        
        for (String current : a.split("\n"))
        {
            log.add( current );
        }
    }
    
    public int stringLength(String a)
    {
        return a.replace(FORMAT_VAR_PREFIX, "").replace(FORMAT_CENTER, "").length();
    }
    
    public String wrap(String in,int len) {
        in=in.trim();
    
        if(stringLength(in)<len)
        {
            return in;
        }

        if(in.substring(0, len).contains("\n"))
        {
            return in.substring(0, in.indexOf("\n")).trim() + "\n\n" + wrap(in.substring(in.indexOf("\n") + 1), len);
        }
    
        int place=Math.max(Math.max(in.lastIndexOf(" ",len),in.lastIndexOf("\t",len)),in.lastIndexOf("-",len));
        
        return in.substring(0,place).trim()+"\n"+wrap(in.substring(place),len);
    }
    
    public void write(AsciiPanel asciiPanel, String a, int y)
    { write(asciiPanel, a, horrGap, y); }
    
    public void write(AsciiPanel asciiPanel, String a, int x, int y) {
    	a = a.replace(FORMAT_VAR_PREFIX, prefix);
    	
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
