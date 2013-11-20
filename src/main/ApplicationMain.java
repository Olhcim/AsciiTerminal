
package main;

import asciiTerminal.Terminal;

public class ApplicationMain extends Terminal {

    public ApplicationMain()
    {
        
    }

    public static void main (String[] args) {
        ApplicationMain app = new ApplicationMain();
        
        app.createComponents();
        app.setDefaultFrameProperties();
    }

    @Override
    public void respondToEnterPress(String a) {
        
    }
}
