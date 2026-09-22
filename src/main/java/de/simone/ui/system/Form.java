package de.simone.ui.system;

import javax.swing.*;

public class Form extends JPanel {

    private LookAndFeel oldTheme = UIManager.getLookAndFeel();

    public Form() {
        init();
    }

    private void init() {
    }

    public void formInit() {
    }

    public void formOpen() {
    }

    public void formRefresh() {
    }

    /**
     * Return the title component of this form
     * @return the title
     * @author Terry
     */
    public JComponent getTitle() {
        return null;
    }

    /**
     * Return the controls component of this form
     * @return the controls
     * @author Terry
     */
    public JComponent getControls() {
        return null;
    }

    protected boolean formCheck() {
        if (oldTheme != UIManager.getLookAndFeel()) {
            oldTheme = UIManager.getLookAndFeel();
            SwingUtilities.updateComponentTreeUI(this);
            return true;
        }
        return false;
    }
}
