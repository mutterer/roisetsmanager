import ij.plugin.PlugIn;

public class RoiSets implements PlugIn {

    /**
     * Select the ROI tree node whose 'tree_index' property matches the given index,
     * expand its parent nodes, and trigger onTreeSelection().
     */
    public static void select(int index) {
        if (lastFrame != null) {
            javax.swing.SwingUtilities.invokeLater(() -> {
                lastFrame.selectRoiByTreeIndex(index);
            });
        }
    }

        // --- Macro-accessible button press methods ---
        public static void toManager() {
            if (lastFrame != null) {
                javax.swing.SwingUtilities.invokeLater(() -> lastFrame.addSelectedToRoiManager());
            }
        }

        public static void clear() {
            if (lastFrame != null) {
                javax.swing.SwingUtilities.invokeLater(() -> lastFrame.clearSets());
            }
        }

        public static void fromManager() {
            if (lastFrame != null) {
                javax.swing.SwingUtilities.invokeLater(() -> lastFrame.addFromRoiManager());
            }
        }

        /**
         * Save the current ROI set to the given file path (zip or .roi).
         */
        public static void save(String path) {
            if (lastFrame != null) {
                javax.swing.SwingUtilities.invokeLater(() -> lastFrame.saveSetsToFile(path));
            }
        }

        /**
         * Load an ROI set from the given file path (zip or .roi).
         */
        public static void load(String path) {
            if (lastFrame != null) {
                javax.swing.SwingUtilities.invokeLater(() -> lastFrame.loadSetsFromFile(path));
            }
        }

        /**
         * Return the number of direct children for the ROI tree node whose 'tree_uid' property matches the given index.
         */
        public static int nChildren(int index) {
            if (lastFrame != null) {
                return lastFrame.nChildren(index);
            }
            return -1;
        }
        
    @Override
    public void run(String arg) {
        new RoiSetsManagerFrame();
    }

    // Reference to the last opened frame (singleton pattern for static access)
    public static RoiSetsManagerFrame lastFrame = null;

}
