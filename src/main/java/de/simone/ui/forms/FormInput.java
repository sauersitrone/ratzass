package de.simone.ui.forms;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;

import de.simone.ui.system.Form;
import de.simone.ui.utils.SystemForm;

@SystemForm(name = "Form Input", description = "input form not yet update")
public class FormInput extends Form {

    public FormInput() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("al center center"));
        JLabel text = new JLabel("Input");
        add(text);
    }
}
