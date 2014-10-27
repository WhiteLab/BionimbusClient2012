package org.lac.bionimbus.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Grid;

public class BnGrid extends Grid
{

    public BnGrid()
    {
        super();
        addStyleName("Grid");
        addStyleName("GridItem");
    }

    public BnGrid(int rows, int cols)
    {
        this();
        resize(rows, cols);
    }

    public void resize(int rows, int cols)
    {
        super.resize(rows, cols);

        for (int c = 0; c < cols; ++c)
        {
            for (int r = 1; r < rows; ++r)
            {
                DOM.setStyleAttribute(getCellFormatter().getElement(r, c),
                        "border", "1px solid #000");
            }
        }
    }
}
