package SwingApp;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.*;
import javax.swing.JButton;

import javax.swing.tree.*;
import javax.swing.JTree;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;


public class FrameMain extends JFrame {
    private final JTree folders;
    private DefaultMutableTreeNode rootNode;
    private final DefaultTreeModel treeModel;
    private JPanel panelMain;
    private JButton addFolder;
    private JButton addPass;
    private JScrollPane treeView;
    private JButton deleteObject;

    public FrameMain() {
        this.setTitle("Simple Password Manager");
        this.setContentPane(panelMain);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(450, 550);

        File settings = new File("settings.xml");
        boolean firstLaunch = FirstLaunchControl.fileIsExist(settings);
        if (!firstLaunch) {
            File firstPart = new File(FirstLaunchControl.getUserDirectory() + "/AppData/Roaming/SimplePW/part1.txt");
            File secondPart = new File(FirstLaunchControl.getUserDirectory() + "/Documents/SimplePW/part2.txt");
            File thirdPart = new File("C://SimplePW/part3.txt");
            if (FirstLaunchControl.fileIsExist(firstPart)) { firstPart.delete(); }
            if (FirstLaunchControl.fileIsExist(secondPart)) { secondPart.delete(); }
            if (FirstLaunchControl.fileIsExist(thirdPart)) { thirdPart.delete(); }

            final JFrame parent = new JFrame();
            String enterPass = null;
            while (enterPass == null) {
                enterPass = JOptionPane.showInputDialog(parent, "Set Access Password:", null);
            }
            try {
                Serialization.xmlPassSerialize(enterPass);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            rootNode = new DefaultMutableTreeNode("Root");
            treeModel = new DefaultTreeModel(rootNode);
        }

        else {
            Serialization.constructTempXml();
            String enterPassw;
            try {
                enterPassw = Serialization.xmlPassDeserialize();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                treeModel = Serialization.xmlTreeDeserialize();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            final JFrame sParent = new JFrame();
            String enteredPass = JOptionPane.showInputDialog(sParent, "Enter Access Password:", null);;
            while (!enteredPass.equals(enterPassw)) {
                enteredPass = JOptionPane.showInputDialog(sParent, "Enter Access Password:", null);
            }
            DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)treeModel.getRoot();
        }

        folders = new JTree(treeModel);
        folders.setEditable(true);
        folders.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
        folders.setShowsRootHandles(true);
        folders.setRootVisible(false);
        treeView.setViewportView(folders);

            addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                try {
                    Serialization.xmlTreeSerialize(treeModel);
                    Serialization.xmlTreeSplit();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                e.getWindow().dispose();
            }
        });

        deleteObject.addActionListener(e -> removeCurrentNode());

        addFolder.addActionListener(e -> {
            try {
                addNewNode();
            } catch (IllegalStateException ex) {
                System.out.println("Exception! : " + ex);
            }
            treeModel.reload();
        });

        addPass.addActionListener(e -> {
            addPassword();
            treeModel.reload();
        });
    }

    public void removeCurrentNode() {
        TreePath currentSelection = folders.getSelectionPath();
        if (currentSelection != null) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)
                    (currentSelection.getLastPathComponent());
            MutableTreeNode parent = (MutableTreeNode)(currentNode.getParent());
            if (parent != null) {
                treeModel.removeNodeFromParent(currentNode);
            }
        }
    }

    public void addNewNode() throws IllegalStateException {
        TreePath currentSelection = folders.getSelectionPath();
        if (currentSelection != null) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)
                    (currentSelection.getLastPathComponent());
            currentNode.add(new DefaultMutableTreeNode("New Folder"));
        }
        else {
            rootNode.add(new DefaultMutableTreeNode("New Folder"));
        }
    }

    public void addPassword() {
        DefaultMutableTreeNode pass = new DefaultMutableTreeNode("Enter Password here!");
        pass.setAllowsChildren(false);
        TreePath currentSelection = folders.getSelectionPath();
        if (currentSelection != null) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)
                    (currentSelection.getLastPathComponent());
            currentNode.add(pass);
        } else {
            rootNode.add(pass);
        }
    }
}
