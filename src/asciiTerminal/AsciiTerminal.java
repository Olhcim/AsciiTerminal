package asciiTerminal;

import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;

import asciiPanel.AsciiPanel;


public class AsciiTerminal
{
    public final String FORMAT_VAR_PREFIX = "%prefix";
    public final String FORMAT_VAR_ENTER = "skipaline";
    public final String FORMAT_CENTER = "%center";  
    
    private int horrGap;
    private int vertGap;
    private String prefix;
    
    private String typed = "";
    private ArrayList<String> log = new ArrayList<String>();
    private int scrollPos;
    
    private boolean inputAfterLog = true;
    private int inputPos;

    
    private AsciiPanel asciiPanel;
    
    
    public AsciiTerminal(AsciiPanel ap) 
    {
    	this(ap, 2, 1, "> ");
    }
    
    public AsciiTerminal(AsciiPanel ap, int horrGap, int vertGap, String prefix)
    {
        asciiPanel = ap;
    	this.horrGap = horrGap;
        this.vertGap = vertGap;
        this.prefix = prefix;
        
        inputPos = ap.getHeightInCharacters() - 2;

        scrollToStart();
    	
    	paintAll();
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
    { return asciiPanel.getHeightInCharacters()- vertGap - 1; }
    
    
    
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
    { return (inputAfterLog) ? log.size() - scrollPos : inputPos; }
    
    
    
    public final void log(String a)
    { log.add( a.trim() ); }
    
    public void write(String a, int y)
    { write(a, horrGap, y); }
    
    public void write(String a, int x, int y) {
    	a = a.replace(FORMAT_VAR_PREFIX, prefix);
        a = a.replace(FORMAT_VAR_ENTER, "\n");
    	
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
    }
    
    private void paintLog() { 
    	asciiPanel.clear();
    	
        for(int i = vertGap(); i < endPoint(); i++) {
            try {
                write(log.get(i + scrollPos), i);
            } catch (Exception e) {}
        }
    }
    
    private void paintInput() {
        try { 
            write(FORMAT_VAR_PREFIX, inputBarPos());
            write(typed, pregap(), inputBarPos());
        } catch (Exception e) {}
    }
    
    public void clearLog() {
    	log.clear();
    	scrollToStart();
    	paintLog();
    }
    
    
    
    
    public void backspacePressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            if(e.isShiftDown()) { typed = ""; }
            else { typed = (typed.length() > 0) ? typed.substring(0, typed.length()-1) : typed; }
        }
    }
    
    public void typableCharacterPressed(KeyEvent e) {
        if (Character.isLetter(e.getKeyChar()) || Character.isSpaceChar(e.getKeyChar())) {
            typed = (typed.length() < asciiPanel.getWidthInCharacters() - totalHorrGap()) ? (typed + e.getKeyChar()) : (typed);
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
