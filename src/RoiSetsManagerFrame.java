import ij.IJ;
import ij.gui.Roi;
import ij.plugin.frame.PlugInFrame;
import ij.plugin.frame.RoiManager;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;

public class RoiSetsManagerFrame extends PlugInFrame {

    /**
     * Select the node whose ROI's 'tree_uid' property matches the given uid,
     * expand its parent nodes, and trigger onTreeSelection().
     */
    public void selectRoiByTreeIndex(int uid) {
        TreePath foundPath = findPathByTreeUid(new TreePath(rootNode), String.valueOf(uid));
        if (foundPath != null) {
            roiTree.setSelectionPath(foundPath);
            roiTree.scrollPathToVisible(foundPath);
            onTreeSelection();
        }
    }

    // Helper to recursively find a TreePath to a node with matching tree_uid property
    private TreePath findPathByTreeUid(TreePath parent, String uid) {
        Object nodeObj = parent.getLastPathComponent();
        if (nodeObj instanceof RoiTreeNode) {
            RoiTreeNode roiNode = (RoiTreeNode) nodeObj;
            Roi roi = roiNode.roi;
            if (roi != null) {
                String roiUid = roi.getProperty("tree_uid");
                if (roiUid != null && roiUid.equals(uid)) {
                    return parent;
                }
            }
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) nodeObj;
        for (int i = 0; i < node.getChildCount(); i++) {
            TreePath childPath = parent.pathByAddingChild(node.getChildAt(i));
            TreePath result = findPathByTreeUid(childPath, uid);
            if (result != null) return result;
        }
        return null;
    }

    /**
     * Return the number of direct children for the node whose 'tree_uid' property matches the given index.
     */
    public int nChildren(int index) {
        if (index == 0) {
            // Return number of first-level tree elements (children of root)
            return getRootChildCount();
        }
        TreePath path = findPathByTreeUid(new TreePath(rootNode), String.valueOf(index));
        if (path != null) {
            Object nodeObj = path.getLastPathComponent();
            if (nodeObj instanceof DefaultMutableTreeNode) {
                return ((DefaultMutableTreeNode) nodeObj).getChildCount();
            }
        }
        return -1;
    }

    /**
     * Return the number of first-level tree elements (children of rootNode).
     */
    public int getRootChildCount() {
        return rootNode.getChildCount();
    }

    public JTree roiTree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;
    private JPanel buttonPanel;
    // Package-private for macro access
    JButton addFromRMButton;
    JButton saveSetsButton;
    JButton loadSetsButton;
    JButton addToRMButton;
    JButton clearRMButton;
    JButton clearSetsButton;

    // Store ROIs in tree nodes
    private static class RoiTreeNode extends DefaultMutableTreeNode {
        public Roi roi;
        public RoiTreeNode(Roi roi) {
            super(roi != null && roi.getName() != null ? roi.getName() : (roi != null ? roi.toString() : "null"));
            this.roi = roi;
        }
    }

    public RoiSetsManagerFrame() {
        super("ROI Sets Manager");
        setLayout(new BorderLayout());

        // Register this frame as the last opened instance for static access
        RoiSets.lastFrame = this;

        // Tree setup
        rootNode = new DefaultMutableTreeNode("ROIs");
        treeModel = new DefaultTreeModel(rootNode);
        roiTree = new JTree(treeModel);
        JScrollPane treeScroll = new JScrollPane(roiTree);
        treeScroll.setPreferredSize(new Dimension(200, 400));

        // Panel to hold tree and button panel side by side
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        mainPanel.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));
        mainPanel.setPreferredSize(new Dimension(300, 400));
        mainPanel.add(treeScroll);

        // Button panel setup (fixed width)
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        int buttonPanelWidth = 120; // Adjust as needed for label length
        buttonPanel.setPreferredSize(new Dimension(buttonPanelWidth, 200));
        buttonPanel.setMaximumSize(new Dimension(buttonPanelWidth, Integer.MAX_VALUE));
        buttonPanel.setMinimumSize(new Dimension(buttonPanelWidth, 100));

        addFromRMButton = new JButton("Add from RM");
        saveSetsButton = new JButton("Save Sets");
        loadSetsButton = new JButton("Load Sets");
        addToRMButton = new JButton("Add to RM");
        clearRMButton = new JButton("Clear RM");
        clearSetsButton = new JButton("Clear Sets");

        // Make all buttons fill parent width
        for (JButton btn : new JButton[]{addFromRMButton,  addToRMButton, clearRMButton, loadSetsButton, saveSetsButton, clearSetsButton}) {
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            btn.setMinimumSize(new Dimension(100, 30));
            btn.setPreferredSize(new Dimension(120, 30));
            buttonPanel.add(btn);
        }   
        mainPanel.add(buttonPanel);
        add(mainPanel, BorderLayout.CENTER);
        setPreferredSize(new Dimension(300, 400));
        setMinimumSize(new Dimension(220, 200));

        // Add selection listener to display ROI on image
        roiTree.addTreeSelectionListener(e -> onTreeSelection());

        // Button panel

        // Button actions
        addFromRMButton.addActionListener(e -> addFromRoiManager());
        saveSetsButton.addActionListener(e -> saveSets());
        loadSetsButton.addActionListener(e -> loadSets());
        addToRMButton.addActionListener(e -> addSelectedToRoiManager());
        clearRMButton.addActionListener(e -> clearRoiManager());
        clearSetsButton.addActionListener(e -> clearSets());

        setSize(300, 400);
        setVisible(true);
    }

    void addFromRoiManager() {
        RoiManager rm = RoiManager.getInstance();
        if (rm == null) {
            IJ.showMessage("ROI Manager not found");
            return;
        }
        int[] selectedIndexes = rm.getSelectedIndexes();
        Roi[] rois;
        if (selectedIndexes != null && selectedIndexes.length > 0) {
            rois = new Roi[selectedIndexes.length];
            for (int i = 0; i < selectedIndexes.length; i++) {
                rois[i] = rm.getRoi(selectedIndexes[i]);
            }
        } else {
            rois = rm.getRoisAsArray();
        }
        if (rois.length == 0) {
            IJ.showMessage("No ROIs in ROI Manager");
            return;
        }
        DefaultMutableTreeNode parent = getSelectedNodeOrRoot();
        if (parent instanceof RoiTreeNode) {
            RoiTreeNode parentRoiNode = (RoiTreeNode) parent;
            Roi parentRoi = parentRoiNode.roi;
            if (parentRoi != null) {
            }
        }
        int[] counter = {1};
        // Find max roi_N in tree to avoid duplicates
        java.util.Enumeration<?> e = rootNode.breadthFirstEnumeration();
        while (e.hasMoreElements()) {
            Object n = e.nextElement();
            if (n instanceof RoiTreeNode) {
                Roi r = ((RoiTreeNode) n).roi;
                if (r != null) {
                    String uid = r.getProperty("tree_uid");
                    if (uid != null) {
                        try {
                            int val = Integer.parseInt(uid);
                            if (val >= counter[0]) counter[0] = val + 1;
                        } catch (Exception ex) {}
                    }
                }
            }
        }
        for (Roi roi : rois) {
            if (roi != null) {
                Roi roiClone = (Roi) roi.clone();
                RoiTreeNode roiNode = new RoiTreeNode(roiClone);
                parent.add(roiNode);
            }
        }
        // After adding, assign or reassign all tree_uid and tree_parentid properties
        assignTreeIds();
        treeModel.reload();
    }

    // Helper to get selected node or root
    private DefaultMutableTreeNode getSelectedNodeOrRoot() {
        TreePath path = roiTree.getSelectionPath();
        if (path == null) return rootNode;
        Object node = path.getLastPathComponent();
        if (node instanceof DefaultMutableTreeNode) {
            return (DefaultMutableTreeNode) node;
        }
        return rootNode;
    }


    // Display selected ROI on the active image
    void onTreeSelection() {
        TreePath path = roiTree.getSelectionPath();
        if (path == null) {
            return;
        }
        Object node = path.getLastPathComponent();
        if (node instanceof RoiTreeNode) {
            RoiTreeNode roiNode = (RoiTreeNode) node;
            Roi roi = roiNode.roi;
            if (roi != null) {
                ij.ImagePlus imp = ij.WindowManager.getCurrentImage();
                if (imp != null) {
                    // Clone the ROI to avoid side effects
                    Roi roiCopy = (Roi) roi.clone();
                    imp.setRoi(roiCopy);
                }
                if (IJ.debugMode) {
                    IJ.log("onTreeSelection: about to log ROI info");
                    // Log all properties and info of the ROI
                    String props = roiNode.roi.getProperties();
                    String name = roiNode.roi.getName();
                    String info = "[" + (name != null ? name : "(no name)") + "] " + roiNode.roi.toString();
                    if (props != null && !props.isEmpty()) {
                        IJ.log("ROI selected: " + info + ", properties: " + props);
                    } else {
                        IJ.log("ROI selected: " + info + ", no properties found.");
                    }
                }
            }
        }
    }

    // --- Save/Load Sets ---
    private void saveSets() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save ROI Set");
        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;
        java.io.File file = chooser.getSelectedFile();
        saveSetsToFile(file.getAbsolutePath());
    }

    // Save sets to a specified file path (public for macro/static access)
    public void saveSetsToFile(String path) {
        // Collect all ROIs in the tree (do not reassign properties)
        java.util.List<Roi> rois = new java.util.ArrayList<>();
        collectRois(rootNode, rois);
        // Use RoiManager's save logic: add ROIs to a new RoiManager and call save
        try {
            ij.plugin.frame.RoiManager tempRM = new ij.plugin.frame.RoiManager(true); // hide window
            for (Roi roi : rois) tempRM.addRoi(roi);
            tempRM.runCommand("Save", path);
        } catch (Exception ex) {
            IJ.showMessage("Error saving ROI set: " + ex.getMessage());
        }
    }

    private void loadSets() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Load ROI Set");
        int result = chooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;
        java.io.File file = chooser.getSelectedFile();
        loadSetsFromFile(file.getAbsolutePath());
    }

    // Load sets from a specified file path (public for macro/static access)
    public void loadSetsFromFile(String path) {
        try {
            java.io.File file = new java.io.File(path);
            java.util.Map<String, String> roiToParent = new java.util.HashMap<>();
            java.util.List<Roi> loadedRois = loadRoisFromZip(file);
            // First, clear the tree
            rootNode.removeAllChildren();
            // Build nodes and maps (no tree_index)
            java.util.Map<String, RoiTreeNode> uidToNode = new java.util.HashMap<>();
            for (Roi roi : loadedRois) {
                String uid = roi.getProperty("tree_uid");
                String parentid = roi.getProperty("tree_parentid");
                RoiTreeNode node = new RoiTreeNode(roi);
                if (uid != null) {
                    uidToNode.put(uid, node);
                }
                if (uid != null && parentid != null) roiToParent.put(uid, parentid);
            }
            // Attach nodes to parents or root (order as in file)
            for (Roi roi : loadedRois) {
                String uid = roi.getProperty("tree_uid");
                RoiTreeNode node = uidToNode.get(uid);
                String parentid = roiToParent.get(uid);
                if (parentid != null && uidToNode.containsKey(parentid)) {
                    uidToNode.get(parentid).add(node);
                } else {
                    rootNode.add(node);
                }
            }
            treeModel.reload();
        } catch (Exception ex) {
            IJ.showMessage("Error loading ROI set: " + ex.getMessage());
        }
    }

    // Assign unique ids and parent ids to all ROIs in the tree, and a tree_index for order
    private void assignTreeIds() {
        java.util.Map<DefaultMutableTreeNode, String> nodeToId = new java.util.HashMap<>();
        int[] counter = {1};
        assignTreeIdsRecursive(rootNode, null, nodeToId, counter);
    }

    private void assignTreeIdsRecursive(DefaultMutableTreeNode node, String parentId, java.util.Map<DefaultMutableTreeNode, String> nodeToId, int[] counter) {
        if (node instanceof RoiTreeNode) {
            RoiTreeNode roiNode = (RoiTreeNode) node;
            String uid = "" + counter[0]++;
            roiNode.roi.setProperty("tree_uid", uid);
            if (parentId != null) {
                roiNode.roi.setProperty("tree_parentid", parentId);
            } else {
                roiNode.roi.setProperty("tree_parentid", null);
            }
            nodeToId.put(node, uid);
            parentId = uid;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            assignTreeIdsRecursive((DefaultMutableTreeNode) node.getChildAt(i), parentId, nodeToId, counter);
        }
    }

    // Collect all ROIs in the tree
    private void collectRois(DefaultMutableTreeNode node, java.util.List<Roi> rois) {
        if (node instanceof RoiTreeNode) {
            RoiTreeNode roiNode = (RoiTreeNode) node;
            if (roiNode.roi != null) rois.add(roiNode.roi);
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            collectRois((DefaultMutableTreeNode) node.getChildAt(i), rois);
        }
    }

    // Collect all ROIs from a node and its children
    private void collectRoisFromNode(DefaultMutableTreeNode node, java.util.List<Roi> rois) {
        if (node instanceof RoiTreeNode) {
            RoiTreeNode roiNode = (RoiTreeNode) node;
            if (roiNode.roi != null) rois.add(roiNode.roi);
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            collectRoisFromNode((DefaultMutableTreeNode) node.getChildAt(i), rois);
        }
    }

    // Add selected node and its children to ROI Manager
    void addSelectedToRoiManager() {
        TreePath path = roiTree.getSelectionPath();
        if (path == null) {
            IJ.showMessage("No node selected");
            return;
        }
        Object node = path.getLastPathComponent();
        if (!(node instanceof DefaultMutableTreeNode)) return;
        java.util.List<Roi> rois = new java.util.ArrayList<>();
        collectRoisFromNode((DefaultMutableTreeNode) node, rois);
        if (rois.isEmpty()) {
            IJ.showMessage("No ROIs to add");
            return;
        }
        RoiManager rm = RoiManager.getInstance();
        if (rm == null) rm = new RoiManager();
        for (Roi roi : rois) {
            rm.addRoi((Roi)roi.clone());
        }
    }

    // Clear all nodes from the tree (except root)
    void clearSets() {
        rootNode.removeAllChildren();
        treeModel.reload();
    }

    // Clear all ROIs from ROI Manager
    private void clearRoiManager() {
        RoiManager rm = RoiManager.getInstance();
        if (rm == null) {
            IJ.showMessage("ROI Manager not found");
            return;
        }
        rm.runCommand("Deselect");
        rm.runCommand("Delete");
    }

    // Load ROIs from a zip file (as in RoiManager)
    private java.util.List<Roi> loadRoisFromZip(java.io.File file) throws Exception {
        java.util.List<Roi> rois = new java.util.ArrayList<>();
        java.util.zip.ZipInputStream zis = new java.util.zip.ZipInputStream(new java.io.FileInputStream(file));
        java.util.zip.ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            if (!entry.getName().toLowerCase().endsWith(".roi")) continue;
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = zis.read(buffer)) > 0) baos.write(buffer, 0, len);
            byte[] bytes = baos.toByteArray();
            ij.io.RoiDecoder decoder = new ij.io.RoiDecoder(bytes, entry.getName());
            Roi roi = decoder.getRoi();
            if (roi != null) rois.add(roi);
        }
        zis.close();
        return rois;
    }


    // (Removed: redundant selectRoiByUid and findPathByUid methods)

    // (Removed: static lastFrame, now managed in RoiSetsManager_)
}
