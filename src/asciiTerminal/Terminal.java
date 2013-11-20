package asciiTerminal;

import asciiPanel.AsciiPanel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JFrame;

public abstract class Terminal extends JFrame implements KeyListener, MouseWheelListener
{

    public AsciiPanel asciiPanel;
    public AsciiTerminal asciiTerminal;
    
    public void createComponents() {
        
        asciiPanel = new AsciiPanel();
        asciiTerminal = new AsciiTerminal(asciiPanel);
        
        add(asciiPanel);
        pack();
        
        addKeyListener(this);
        addMouseWheelListener(this);
    }
    
    public void setDefaultFrameProperties()
    {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    abstract public void respondToEnterPress(String a);

    public void repaint() {
        asciiTerminal.paintAll();
        super.repaint();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent me) {
        asciiTerminal.incrementScrollPos(me);
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
            respondToEnterPress( asciiTerminal.enterKeyPressed(ke) );
        } else {
            asciiTerminal.typableCharacterPressed(ke);
            asciiTerminal.incrementScrollPos(ke);
            asciiTerminal.backspacePressed(ke);
        }
        repaint();
    }

    @Override public void keyReleased(KeyEvent ke) { }
    @Override public void keyTyped(KeyEvent ke) { }
}
