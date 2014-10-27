package org.lac.bionimbus.client;

import java.util.Vector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

abstract class RowGrid<T> extends BnGrid implements KeyboardListener
{
    int       offset      = 0;
    final int tableSize   = 15;
    int       resultsSize = 0;
    TextBox   searchBox;
    int       width       = 0;

    RowGrid()
    {
        Vector<String> ct = titles();
        width = ct.size();

        resize(tableSize + 2, width);

        setWidget(0, 0, new Button("<- Back", new ClickHandler()
        {
            public void onClick(ClickEvent event)
            {
                offset = java.lang.Math.max(offset - tableSize, 0);
                scroll();
            }
        }));

        setWidget(0, 1, new Button("Forward ->", new ClickHandler()
        {
            public void onClick(ClickEvent event)
            {
                offset = java.lang.Math.min(offset + tableSize, resultsSize
                        - tableSize);
                scroll();
            }
        }));
        searchBox = new TextBox();
        searchBox.setText("");
        searchBox.addKeyboardListener(this);

        setWidget(0, 2, searchBox);

        for (int i = 0; i < ct.size(); i++)
        {
            setText(1, i, ct.get(i));
        }
    }

    /**
     * Not used at all
     */
    public void onKeyDown(Widget arg0, char arg1, int arg2)
    {
    }

    /**
     * Not used at all
     */
    public void onKeyPress(Widget arg0, char arg1, int arg2)
    {
    }

    /**
     * A key was released, start autocompletion
     */
    public void onKeyUp(Widget arg0, char arg1, int arg2)
    {
        filterTable();
    }

    void scroll()
    {
        for (int row = 0; row < tableSize; ++row)
        {
            int r2 = row + 2;
            try
            {
                renderRow(r2, row + offset);
            }
            catch (Exception e)
            {
                for (int a = 0; a < width; ++a)
                {
                    setText(r2, a, "");
                }
            }
        }
    }

    abstract Vector<String> titles();

    abstract void renderRow(int viz_row, int data_row);

    Vector<T> baseContent;
    Vector<T> content;

    void setContent(Vector<T> content)
    {
        baseContent = content;
        this.content = new Vector<T>();
        // resultsSize = content.size();
        filterTable();
        scroll();
    }

    String filterText;

    void filterTable()
    {
        filterText = searchBox.getText().trim().toLowerCase();

        // Window.alert( ">" + filterText + "<" ) ;
        content.clear();

        try
        {
            for (T row : baseContent)
            {
                if (filterText.equals("") || filterRow(row))
                    content.add(row);
            }
        }
        catch (Exception e)
        {
            Window.alert("e " + e);
        }
        resultsSize = content.size();
        offset = 0;
        scroll();
    }

    boolean match(String s)
    {
        if (s == null)
            return false;
        s = s.trim().toLowerCase();

        return s.indexOf(filterText) >= 0;
    }

    abstract boolean filterRow(T row);
}
