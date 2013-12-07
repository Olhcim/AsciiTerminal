
package com.olhcim.asciiterminal;

import java.awt.event.KeyEvent;

public interface AsciiTerminalEvents {
    
    public void commandEntered(String[] text, KeyEvent ke);

}
