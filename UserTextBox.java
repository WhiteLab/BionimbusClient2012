package org.lac.bionimbus.client;

import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

//TODO: what is a "user text box?"

class UserTextBox extends TextBox implements InputValidation
{

    UserTextBox()
    {
        super();
    }

    UserTextBox(String text)
    {
        super();
        setText(text);
    }

    public String getText()
    {
        return super.getText().trim();
    }

    void setValid(boolean valid)
    {
        if (valid)
        {
            addStyleName("Valid");
            removeStyleName("Invalid");
        }
        else
        {
            addStyleName("Invalid");
            removeStyleName("Valid");
        }
    }

    public boolean isValid()
    {
        boolean valid = !getText().equals("");
        setValid(valid);
        return valid;
    }

    public String label = "";

    public void setFieldLabel(String label)
    {
        this.label = label;
    }

    public String getFieldLabel()
    {
        return label;
    }

    public int getInt()
    {
        try
        {
            return Integer.parseInt(getText());
        }
        catch (NumberFormatException e)
        {
            return 0;
        }
    }

    public void setNumeric()
    {
        addKeyboardListener(new KeyboardListenerAdapter()
        {
            public void onKeyPress(Widget sender, char keyCode, int modifiers)
            {
                if ((!Character.isDigit(keyCode))
                        && (keyCode != (char) KEY_TAB)
                        && (keyCode != (char) KEY_BACKSPACE)
                        && (keyCode != (char) KEY_DELETE)
                        && (keyCode != (char) KEY_ENTER)
                        && (keyCode != (char) KEY_HOME)
                        && (keyCode != (char) KEY_END)
                        && (keyCode != (char) KEY_LEFT)
                        && (keyCode != (char) KEY_UP)
                        && (keyCode != (char) KEY_RIGHT)
                        && (keyCode != (char) KEY_DOWN))
                {
                    // TextBox.cancelKey() suppresses the current keyboard
                    // event.
                    ((TextBox) sender).cancelKey();
                }
            }
        });
    }

    Double getDoubleFromField(String errorMessage) throws ValidationException
    {
        String value = getText();

        if (value.equals(""))
        {
            return null;
        }
        try
        {
            return new Double(value);
        }
        catch (Exception e)
        {
            throw new ValidationException(errorMessage);
        }
    }

    Integer getIntegerFromField(String errorMessage) throws ValidationException
    {
        String value = getText();

        if (value.equals(""))
        {
            return null;
        }
        try
        {
            return new Integer(value);
        }
        catch (Exception e)
        {
            throw new ValidationException(errorMessage);
        }
    }

    Long getLongFromField(String errorMessage) throws ValidationException
    {
        String value = getText();

        if (value.equals(""))
        {
            return null;
        }
        try
        {
            return new Long(value);
        }
        catch (Exception e)
        {
            throw new ValidationException(errorMessage);
        }
    }

}
