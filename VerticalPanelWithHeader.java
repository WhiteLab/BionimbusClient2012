package org.lac.bionimbus.client;

import com.google.gwt.user.client.ui.*;

// Can use this to display all pages with headers consistently
// should get suggestions for a  better name

public class VerticalPanelWithHeader extends VerticalPanel
{
    public VerticalPanelWithHeader(String title)
    {
        add(new HTML("<br><h4>" + title + "</h4><br>"));
    }
}