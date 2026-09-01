# Behavior Tree Syntax Highlighter — Installation

## Option 1: Copy to VS Code Extensions Folder (simplest)

Copy the `vscode-tree-language/` folder into your VS Code user extensions directory and rename it to match the publisher format:

**Windows**
```
xcopy /E /I vscode-tree-language "C:\Users\terry\.vscode\extensions\gooddev.tree-language-0.0.1"
```

**macOS / Linux**
```
cp -r vscode-tree-language ~/.vscode/extensions/gooddev.tree-language-0.0.1
```

Reload VS Code (`Ctrl+Shift+P` → **Developer: Reload Window**).

---

## Option 2: Package and Install as VSIX

Requires [vsce](https://github.com/microsoft/vscode-vsce):

```bash
npm install -g @vscode/vsce
cd vscode-tree-language
vsce package
```

This produces `tree-language-0.0.1.vsix`. Install it in VS Code:

```bash
code --install-extension tree-language-0.0.1.vsix
```

Or via the UI: **Extensions** panel (`Ctrl+Shift+X`) → `···` menu → **Install from VSIX…**

---

## Update the Grammar After Changes

If you edit `syntaxes/tree.tmLanguage.json` (e.g. to add new keywords), the installed copy is **not updated automatically**. You must sync it manually.

**Windows — overwrite the installed copy**
```
xcopy /E /Y vscode-tree-language "%USERPROFILE%\.vscode\extensions\gooddev.tree-language-0.0.1"
```

**macOS / Linux**
```
cp -r vscode-tree-language/. ~/.vscode/extensions/gooddev.tree-language-0.0.1/
```

Then reload VS Code (`Ctrl+Shift+P` → **Developer: Reload Window**).

> **Why this is needed:** VS Code loads the grammar from the installed extension folder (`~/.vscode/extensions/`), not from the workspace source folder. Edits to the workspace copy have no effect until they are copied over.

---

## Verify

1. Open any `.tree` file.
2. Check the bottom-right status bar — language should read **Behavior Tree**.
3. Keywords like `root`, `sequence`, `selector`, `dynamicGuardSelector` should be highlighted in blue.

> If the language shows something else, click it and select **Behavior Tree** manually.
