package org.lac.bionimbus.client;

import com.google.gwt.user.client.ui.ListBox;

public class UserListBox extends ListBox
{

    private static String def = "--Default--";

    boolean               hasDefault;

    UserListBox()
    {
    }

    UserListBox(String[] values, boolean _hasDefault)
    {
        hasDefault = _hasDefault;

        if (hasDefault)
        {
            addItem(def);
        }

        for (String items : values)
        {
            addItem(items);
        }
    }

    String getSelectedValue()
    {
        return this.getValue(getSelectedIndex());
    }

    void setSelectedString(String s)
    {
        try
        {
            for (int a = 0;; ++a)
            {
                if (getItemText(a).equals(s))
                {
                    setSelectedIndex(a);
                    return;
                }
            }
        }
        catch (Exception e)
        {

        }
    }

    String getSelectedNonDefaultValue(String error) throws ValidationException
    {
        if (getSelectedIndex() < 1)
            throw new ValidationException(error);
        return getSelectedValue();
    }

}
