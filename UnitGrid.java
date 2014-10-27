package org.lac.bionimbus.client;

import java.util.Vector;

import org.lac.bionimbus.shared.Unit;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

class UnitGrid extends RowGrid<Unit>
{
    Vector<String> titles()
    {
        Vector<String> unitHeader = new Vector<String>();

        unitHeader.add("Unit");
        unitHeader.add("Bionimbus ID");
        unitHeader.add("Name");
        unitHeader.add("Project");
        unitHeader.add("Species");
        unitHeader.add("Antibody");
        unitHeader.add("Sample");
        unitHeader.add("Description");
        unitHeader.add("Protein");
        unitHeader.add("Type");
        unitHeader.add("Catalog");

        unitHeader.add("Status");
        unitHeader.add("Last Update");

        return unitHeader;
    }

    void renderRow(int vizRow, int contentRow)
    {
        Unit res = content.elementAt(contentRow);

        final int id = res.getId();
        final String cid = res.getCistrackId();

        Button downloadButton = new Button("Unit Details", new ClickHandler()
        {
            public void onClick(ClickEvent event)
            {
                new UnitDetail(id, cid);
            }
        });

        int col = 0;

        setWidget(vizRow, col++, downloadButton);
        setText(vizRow, col++, res.getCistrackId());
        setText(vizRow, col++, res.getName());
        setText(vizRow, col++, res.getProject());
        setText(vizRow, col++, res.getSpecies());
        setText(vizRow, col++, res.getAntiBody());
        setText(vizRow, col++, res.getSample());
        setText(vizRow, col++, res.getDescription());
        setText(vizRow, col++, res.getProtein());
        setText(vizRow, col++, res.getType());
        setText(vizRow, col++, res.getCatalog());

        setText(vizRow, col++, res.lc.status);
        setText(vizRow, col++, res.lc.time);

    }

    boolean filterRow(Unit row)
    {
        return match(row.getCistrackId()) || match(row.getName())
                || match(row.getProject()) || match(row.getSpecies())
                || match(row.getAntiBody()) || match(row.getSample())
                || match(row.getDescription()) || match(row.getProtein())
                || match(row.getType()) || match(row.getCatalog());
    }

}