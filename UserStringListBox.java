package org.lac.bionimbus.client;

import com.google.gwt.user.client.ui.ListBox;

public class UserStringListBox extends ListBox implements InputValidation
{

    protected static final String DEFAULT_VALUE = "-1";
    public static final String    DEFAULT_TEXT  = "-Select-";
    private String                label         = "";

    public UserStringListBox()
    {
        super();
        // addStyleName("gwt-ListBox");
        addStyleName("Valid");
        addDefaultItem();
    }

    public UserStringListBox(String[] items)
    {
        this();
        for (String s : items)
        {
            addItem(s, s);
        }
    }

    protected void addDefaultItem()
    {
        addItem(DEFAULT_TEXT, DEFAULT_VALUE);
        setSelectedIndex(0);
    }

    public boolean isValid()
    {
        boolean valid = !getSelectedText().equals("");
        if (valid)
        {
            removeStyleName("Invalid");
            addStyleName("Valid");
        }
        else
        {
            removeStyleName("Valid");
            addStyleName("Invalid");
        }
        return valid;
    }

    public void setFieldLabel(String label)
    {
        this.label = label;
    }

    public String getFieldLabel()
    {
        return label;
    }

    public void setSelectedText(String text)
    {
        for (int i = 0; i < getItemCount(); i++)
        {
            if (getItemText(i).equals(text))
            {
                setSelectedIndex(i);
                return;
            }
        }
    }

    public String getSelectedText()
    {
        int index = getSelectedIndex();
        if (index == -1 || getItemText(index).equals(DEFAULT_TEXT))
            return "";
        else
            return getItemText(index);
    }
}
