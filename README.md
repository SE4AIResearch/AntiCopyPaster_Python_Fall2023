# AntiCopyPasterPython

AntiCopyPasterPython is a plugin for Pycharm that tracks the copying and pasting carried out by the developer and
suggests extracting duplicates into a new method as soon as they are introduced in the code.

> **Warning**: Please note that AntiCopyPasterPython is a prototype and a work in progress. We appreciate any feedback
> on both the concept itself and its implementation.

## How To Install

AntiCopyPasterPython requires Pycharm version 2023.3 or later to work. To install the plugin:

1. Download the pre-built version of the plugin from 
   [here](https://github.com/SE4AIResearch/AntiCopyPaster_Python_Fall2023/releases/tag/v1.0).
2. Open Pycharm and go to `File`/`Settings`/`Plugins`.
3. Select the gear icon, and choose `Install Plugin from Disk...`.
4. Choose the downloaded ZIP archive.
5. Click `Apply`.
6. Restart the IDE.

## Technical Information

### How It Works

The plugin monitors the copying and pasting that takes place inside the IDE. As soon as a code fragment is pasted,
the plugin checks if it introduces code duplication. If it does, the plugin calculates a set of metrics for its code
and compares its metrics with the weighted average metric values of each file; how this average is weighted is
controlled by the sensitivity settings for each metric. If the fragment's metrics exceed these thresholds, the plugin
will suggest for the fragment to be refactored. If this suggestion is accepted by the user, a refactoring prompt will
trigger, allowing the user to view and edit the refactored fragment before replacing each instance of the fragment
one at a time (or all at once).

### Metric Categories

AntiCopyPasterPython analyzes code fragments by considering four main categories of heuristics:

* Keywords: The number and/or frequency of Python language keywords (ex. `def`, `True`, `pass`, etc.) in a fragment.
* Coupling: The number and/or frequency of references made by the fragment to variables defined outside the fragment.
* Complexity: The total and/or average nesting (essentially, indentation) of a fragment.
* Size: The number of lines, characters, and/or average per-line characters in a fragment.

These categories can be individually configured further in the plugin's advanced settings menu.

## Contacts

If you have any questions or propositions, do not hesitate to contact Eman AlOmar at ealomar@stevens.edu.
