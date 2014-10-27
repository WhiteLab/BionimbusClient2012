package org.lac.bionimbus.client;

import java.util.Vector;

import com.google.gwt.user.client.ui.*;

public class RadioButtonGroup extends Grid implements InputValidation
{
    Vector<RadioButton> buttons         = new Vector<RadioButton>();
    String              validationLabel = "";

    public RadioButtonGroup(String groupName, String... buttonTexts)
    {
        super(1, buttonTexts.length); // should we check for null buttonTexts??
        int i = 0;
        for (String text : buttonTexts)
        {
            RadioButton r = new RadioButton(groupName, text);
            buttons.add(r);
            setWidget(0, i++, r);
        }

    }

    public Vector<RadioButton> getButtons()
    {
        return buttons;
    }

    public void setCheckedText(String text)
    {
        for (RadioButton b : buttons)
        {
            if (b.getText().equals(text))
            {
                b.setChecked(true);
                return;
            }
        }
    }

    public void resetGroup()
    {
        for (RadioButton b : buttons)
        {
            b.setChecked(false);
        }
    }

    public String getCheckedText()
    {
        for (RadioButton b : buttons)
        {
            if (b.isChecked())
            {
                return b.getText();
            }
        }
        return "";
    }

    public String getFieldLabel()
    {
        return validationLabel;
    }

    public void setFieldLabel(String label)
    {
        validationLabel = label;
    }

    public boolean isValid()
    {
        return !getCheckedText().equals("");
    }

    public static RadioButtonGroup newYesNo(String groupName, boolean setYes)
    {
        RadioButtonGroup r = new RadioButtonGroup(groupName, "Yes", "No");
        r.setCheckedText(setYes ? "Yes" : "No");
        return r;
    }

}
