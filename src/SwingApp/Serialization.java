package SwingApp;

import javax.swing.tree.DefaultTreeModel;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

public class Serialization {

    public static void xmlTreeSerialize (DefaultTreeModel treeModel) throws IOException {
        {
            FileOutputStream fos = new FileOutputStream("temp.xml");
            XMLEncoder encoder = new XMLEncoder(fos);
            encoder.setExceptionListener(e -> System.out.println("Exception! :"+e.toString()));
            encoder.writeObject(treeModel);
            encoder.close();
            fos.close();
        }
    }

    public static DefaultTreeModel xmlTreeDeserialize () throws IOException {
        FileInputStream fis = new FileInputStream("temp.xml");
        XMLDecoder decoder = new XMLDecoder(fis);
        DefaultTreeModel decodedTree = (DefaultTreeModel) decoder.readObject();
        decoder.close();
        fis.close();

        return decodedTree;
    }

    public static void xmlPassSerialize (String pass) throws IOException {
        FileOutputStream fos = new FileOutputStream("settings.xml");
        XMLEncoder encoder = new XMLEncoder(fos);
        encoder.setExceptionListener(e -> System.out.println("Exception! :"+e.toString()));
        encoder.writeObject(pass);
        encoder.close();
        fos.close();
    }

    public static String xmlPassDeserialize () throws IOException {
        FileInputStream fis = new FileInputStream("settings.xml");
        XMLDecoder decoder = new XMLDecoder(fis);
        String pass = (String) decoder.readObject();
        decoder.close();
        fis.close();

        return pass;
    }

    public static void xmlTreeSplit() {
        double linesCount = 0;
        File delTemp = new File("temp.xml");
        if (FirstLaunchControl.fileIsExist(delTemp)) {
            try {
                linesCount = countLines("temp.xml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (linesCount > 0) {
            BufferedReader reader;
            double linesFP = Math.floor(linesCount / 3);
            double linesTP = linesCount - linesFP - linesFP;
            PrintWriter part1, part2, part3;
            try {
                part1 = new PrintWriter(FirstLaunchControl.getUserDirectory() +
                        "/AppData/Roaming/SimplePW/part1.txt");
                part2 = new PrintWriter(FirstLaunchControl.getUserDirectory() +
                        "/Documents/SimplePW/part2.txt");
                part3 = new PrintWriter("C://SimplePW/part3.txt");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            try {
                reader = new BufferedReader(new FileReader("temp.xml"));
                String line = reader.readLine();

                for (int i = 0; i < linesFP; i++) {
                    part1.println(line);
                    line = reader.readLine();
                }
                for (int i = 0; i < linesFP; i++) {
                    part2.println(line);
                    line = reader.readLine();
                }
                for (int i = 0; i < linesTP; i++) {
                    part3.println(line);
                    line = reader.readLine();
                }

                reader.close();
                part1.close();
                part2.close();
                part3.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            delTemp.delete();
        }
    }

    public static void constructTempXml() {
        File temp = new File("temp.xml");
        File firstPart = new File(FirstLaunchControl.getUserDirectory() + "/AppData/Roaming/SimplePW/part1.txt");
        File secondPart = new File(FirstLaunchControl.getUserDirectory() + "/Documents/SimplePW/part2.txt");
        File thirdPart = new File("C://SimplePW/part3.txt");
        if (FirstLaunchControl.fileIsExist(firstPart) && FirstLaunchControl.fileIsExist(secondPart) &&
                FirstLaunchControl.fileIsExist(thirdPart)) {
            try {
                copyContent(firstPart, secondPart, thirdPart, temp);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void copyContent(File a, File b, File c, File d)
            throws Exception
    {
        try (FileChannel in1 = new FileInputStream(a).getChannel(); FileChannel in2 = new FileInputStream(b).getChannel();
             FileChannel in3 = new FileInputStream(c).getChannel(); WritableByteChannel out = new FileOutputStream(d).getChannel()) {
            in1.transferTo(0, in1.size(), out);
            in2.transferTo(0, in2.size(), out);
            in3.transferTo(0, in3.size(), out);
        }
    }

    public static int countLines(String filename) throws IOException {
        try (InputStream is = new BufferedInputStream(new FileInputStream(filename))) {
            byte[] c = new byte[1024];

            int readChars = is.read(c);
            if (readChars == -1) {
                // bail out if nothing to read
                return 0;
            }

            // make it easy for the optimizer to tune this loop
            int count = 0;
            while (readChars == 1024) {
                for (int i = 0; i < 1024; ) {
                    if (c[i++] == '\n') {
                        ++count;
                    }
                }
                readChars = is.read(c);
            }

            // count remaining characters
            while (readChars != -1) {
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
                readChars = is.read(c);
            }

            return count == 0 ? 1 : count;
        }
    }
}
