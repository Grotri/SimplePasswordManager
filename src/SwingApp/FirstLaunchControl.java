package SwingApp;

import java.io.File;

public class FirstLaunchControl {
    public static boolean fileIsExist(File f) {
        return f.exists() && !f.isDirectory();
    }

    public static String getUserDirectory() {
        return System.getProperty("user.home");
    }

}
