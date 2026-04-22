# ROI Sets Manager (ImageJ Plugin)

## Summary
The ROI Sets Manager is an ImageJ plugin for managing hierarchical sets of ROIs (Regions of Interest). It works as a companion to the standard ImageJ RoiManager, providing a tree-based interface for organizing, saving, loading, and manipulating ROI groups, supporting parent-child relationships and custom properties for each ROI. The plugin is designed for advanced ROI workflows, batch operations, and macro automation.

### Key Features
- Tree view for organizing ROIs in parent-child hierarchies
- Add, remove, and clear ROIs or sets
- Save and load ROI sets (as .zip or .roi files)
- Assigns unique `tree_uid` and `tree_parentid` properties to each ROI
- Macro-accessible static methods for automation
- Selection and manipulation of ROIs by unique ID
- Logging and debug output (when ImageJ debug mode is active)

## Static Methods (Macro/Script Access)
You can call static methods from `RoiSets.java` using the ImageJ macro `call()` syntax. Example usage:

```
call("RoiSets.select", index);
call("RoiSets.nChildren", index);
call("RoiSets.save", path);
call("RoiSets.load", path);
call("RoiSets.toManager");
call("RoiSets.fromManager");
call("RoiSets.clear");
```

### Method Reference
- `select(int index)`: Selects the ROI tree node with the given `tree_uid`.
- `nChildren(int index)`: Returns the number of direct children for the node with the given `tree_uid`. Use `nChildren(0)` to get the number of top-level elements.
- `save(String path)`: Saves the current ROI set to the specified file.
- `load(String path)`: Loads an ROI set from the specified file.
- `toManager()`: Adds the selected node and its children to the ROI Manager.
- `fromManager()`: Adds ROIs from the ROI Manager to the tree.
- `clear()`: Clears all nodes from the tree.

## Example Macro
```
// Select the node with tree_uid 5
call("RoiSets.select", 5);

// Get the number of top-level ROI sets
n = call("RoiSets.nChildren", 0);
print("Top-level sets: " + n);

// Save current sets
call("RoiSets.save", "/path/to/rois.zip");
```

## Installation
1. Build the plugin (see source for build instructions).
2. Place `RoiSetsManager_.jar` in your ImageJ `plugins` folder.
3. Restart ImageJ.

## Author
- Maintained by: Jerome Mutterer
- License: Public Domain
