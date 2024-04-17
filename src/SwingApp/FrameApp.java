package SwingApp;
import java.util.Locale;

public class FrameApp {
    public static void main(String[] args){
        Locale.setDefault(Locale.ROOT);

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new FrameMain().setVisible(true));
    }
}
