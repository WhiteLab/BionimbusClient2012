package org.lac.bionimbus.client;

import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class UnitExperimentBar extends TabPanel
{
    //public VerticalPanel experimentsBarPanel = new VerticalPanel();
    public VerticalPanel unitsBarPanel = new VerticalPanel();

    //ExperimentGrid       experimentGrid      = new ExperimentGrid();
    UnitGrid             unitGrid      = new UnitGrid();

    UnitExperimentBar()
    {
        add(unitsBarPanel, "Units");
        //add(experimentsBarPanel, "Experiments");

        //experimentGrid.addStyleName("Grid");
        //experimentGrid.addStyleName("GridItem");

        //experimentsBarPanel.add(experimentGrid);

        unitGrid.addStyleName("Grid");
        unitGrid.addStyleName("GridItem");
        unitsBarPanel.add(unitGrid);
    }
}
