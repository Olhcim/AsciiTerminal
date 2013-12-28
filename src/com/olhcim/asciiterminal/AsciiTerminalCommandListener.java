
package com.olhcim.asciiterminal;

import java.awt.event.KeyEvent;

public interface AsciiTerminalCommandListener {
    
    public void commandEntered(String[] text, KeyEvent ke);

}
