
package com.olhcim.asciiterminal;

import asciiPanel.AsciiPanel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import javax.swing.JFrame;

public class AsciiTerminal implements KeyListener, MouseWheelListener{
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
//-----ASCIITERMINAL----------------------------------------------------------//
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
    
    public static final String FORMAT_VAR_PREFIX = "%prefix";
    public static final String FORMAT_CENTER = "%center";  
    
    private int horrGap;
    private int vertGap;
    private String prefix;
    
    private String typed = "";
    private ArrayList<String> log = new ArrayList<>();
    private int scrollPos;
    
    private boolean inputAfterLog = true;
    
    public AsciiPanel asciiPanel;
    private JFrame frame;
    
    private KeyEvent lastKeyEvent;
    private AsciiTerminalEvents event;
    
//----------Initialisation----------------------------------------------------//
    
    public AsciiTerminal(AsciiPanel asciiPanel, JFrame frame) 
    {
    	this(asciiPanel, frame, 2, 1, "> ");
    }
    
    public AsciiTerminal(AsciiPanel asciiPanel, JFrame frame, int horrGap, int vertGap, String prefix)
    {
        this.asciiPanel = asciiPanel;
        this.frame = frame;
        
    	this.horrGap = horrGap;
        this.vertGap = vertGap;
        this.prefix = prefix;

        scrollToStart();
    }
    
//----------Options-----------------------------------------------------------//
    
    public void setPrefix(String a)
    { prefix = a; }
    public void scrollToStart()
    { scrollPos = 0 - vertGap; }
    public void scrollToEnd()
    { scrollPos = (log.size() > calEndPoint() - vertGap) ? calEndScrollPos() : scrollPos; }
    public void setInputAfterLog(boolean a)
    { inputAfterLog = a; }
    
//----------Calculations------------------------------------------------------//
    
    private int calTotalHorrGap()
    { return horrGap + prefix.length() + horrGap; }
    private int calPregap()
    { return horrGap + prefix.length(); }
    private int calVertGap()
    { return vertGap; }
    private int calTextAreaWidth()
    { return asciiPanel.getWidthInCharacters() - calTotalHorrGap(); }
    private int calEndPoint()
    { return asciiPanel.getHeightInCharacters() - vertGap - 1; }
    private int calStartScrollPos()
    { return 0 - vertGap; }
    private int calEndScrollPos()
    { return log.size() - calEndPoint(); }
    private int calInputBarPos()
    { 
        return (inputAfterLog) ? (log.size()-scrollPos > calEndPoint() ? asciiPanel.getHeightInCharacters() : log.size()-scrollPos) : calEndPoint(); }
    
    private int stringLengthNoFormatting(String a) {
        return a.replace(FORMAT_VAR_PREFIX, "").replace(FORMAT_CENTER, "").length();
    }
    
    private String wrap(String in)
    { return wrap(in, calTextAreaWidth()); }
    
    private String wrap(String in, int len) {
        in=in.trim();
        if(stringLengthNoFormatting(in)<len) {
            return in;
        }
        if(in.substring(0, len).contains("\n")) {
            return in.substring(0, in.indexOf("\n")).trim() + "\n\n" + wrap(in.substring(in.indexOf("\n") + 1), len);
        }
    
        int place=Math.max(Math.max(in.lastIndexOf(" ",len),in.lastIndexOf("\t",len)),in.lastIndexOf("-",len));
        return in.substring(0,place).trim()+"\n"+wrap(in.substring(place),len);
    }
    
//----------General-Usage-----------------------------------------------------//
    
    public void logln(String a)
    { log(a); log(""); }
    
    public void log(String a) {
        if (stringLengthNoFormatting(a) > calTextAreaWidth()) {
            a = wrap(a);
        }
        for (String current : a.split("\n")) {
            log.add( current );
        }
        
        scrollToEnd();
    }
    public void write(String a, int y)
    { write(a, horrGap, y); }
    
    public void write(String a, int x, int y) {
    	a = a.replace(FORMAT_VAR_PREFIX, prefix);
    	
    	if (a.contains(FORMAT_CENTER)) {
            a = a.replace(FORMAT_CENTER, "");
            asciiPanel.writeCenter(a, y);
    	} else {
            asciiPanel.write(a, x, y);
    	}
    }
    public void paintAll() {
        paintLog();
        paintInput();
        frame.repaint();
    }
    private void paintLog() { 
    	asciiPanel.clear();
    	
        for(int i = calVertGap(); i <= calEndPoint(); i++) {
            try {
                write(log.get(i + scrollPos), i);
            } catch (Exception e) {}
        }
    }
    private void paintInput() {
        try { 
            write(FORMAT_VAR_PREFIX, calInputBarPos());
            write(typed, calPregap(), calInputBarPos());
        } catch (Exception e) {}
    }
    public void clearLog() {
    	log.clear();
    	scrollToStart();
    	asciiPanel.clear();
    }
    
//----------Input-KeyBoard----------------------------------------------------//

    public void inputBackspacePressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            if(e.isShiftDown()) { typed = ""; }
            else { typed = (typed.length() > 0) ? typed.substring(0, typed.length()-1) : typed; }
        }
    }
    
    public void inputTypableCharacterPressed(KeyEvent e) {
        
        char a = e.getKeyChar();
        
        if (Character.isLetter(a) || Character.isSpaceChar(a) || Character.isDigit(a)) {
            typed = (typed.length() < calTextAreaWidth()) ? (typed + e.getKeyChar()) : (typed);
        }
    }
    
    public String[] inputEnterPressed(KeyEvent e) {
        String typedOld = null;
        
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            typedOld = typed;

            log(FORMAT_VAR_PREFIX + typed.trim());
            typed = "";
            scrollToEnd();
        }
        return (typedOld == null) ? null : typedOld.split(" ");
    }

//----------Input-ScrollWheel-------------------------------------------------//
    
    public void inputIncrementScrollPos(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            if(e.isShiftDown()) { scrollToStart(); }
            else { inputIncrementScrollPos(-1); }
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            if(e.isShiftDown()) { scrollToEnd(); }
            else { inputIncrementScrollPos(1); }
        }
    }
    
    public void inputIncrementScrollPos(MouseWheelEvent me, KeyEvent e)
    {
        if (me.getWheelRotation() < 0) {
            if(e.isShiftDown()) { scrollToStart(); }
            else { inputIncrementScrollPos(me.getWheelRotation()); }
        } else if (me.getWheelRotation() > 0) {
            if(e.isShiftDown()) { scrollToEnd(); }
            else { inputIncrementScrollPos(me.getWheelRotation()); }
        }
    }
    
    public void inputIncrementScrollPos(MouseWheelEvent me)
    { inputIncrementScrollPos( me.getWheelRotation() ); }
    
    public void inputIncrementScrollPos(int a) {
        int futurePos = scrollPos + a;
        scrollPos = (futurePos >= calStartScrollPos() && futurePos <= calEndScrollPos()) ? futurePos : scrollPos;
    }
    
//----------Key-&-Mouse-Listeners---------------------------------------------//

    
    @Override
    public void keyTyped(KeyEvent e) {
        lastKeyEvent = e;
    }
    @Override
    public void keyPressed(KeyEvent e) {
        lastKeyEvent = e;
        
        inputBackspacePressed(e);
        inputTypableCharacterPressed(e);
        
        String[] entered = inputEnterPressed(e); if (entered != null) { event.commandEntered(entered, e); }
        inputIncrementScrollPos(e);
        
        paintAll();
    }
    @Override
    public void keyReleased(KeyEvent e) {
        lastKeyEvent = e;
    }
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        inputIncrementScrollPos(e, lastKeyEvent);
        paintAll();
    }

}

