package org.lac.bionimbus.client;

import java.util.Vector;

import org.lac.bionimbus.shared.Experiment;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

public class ExperimentGrid extends RowGrid<Experiment>
{
    Vector<String> titles()
    {
        Vector<String> expHeader = new Vector<String>();

        expHeader.add("Unit");
        expHeader.add("Experiment Id");
        expHeader.add("Name");
        expHeader.add("Antibody");
        expHeader.add("Sample");
        expHeader.add("Project");
        expHeader.add("Description");

        return expHeader;
    }

    void renderRow(int vizRow, int contentRow)
    {
        Experiment res = content.elementAt(contentRow);
        final int id = res.getExperimentId();

        Button downloadButton = new Button("View Experiment",
                new ClickHandler()
                {
                    public void onClick(ClickEvent event)
                    {
                        /* ExperimentDetail ed = */
                        new ExperimentDetail(id);

                    }
                });

        int col = 0;
        setWidget(vizRow, col++, downloadButton);
        setText(vizRow, col++, Integer.toString(id));
        setText(vizRow, col++, res.getName());
        setText(vizRow, col++, res.getAntibody());
        setText(vizRow, col++, res.getSample());
        setText(vizRow, col++, res.getProject());
        setText(vizRow, col++, res.getDescription());
    }

    boolean filterRow(Experiment row)
    {
        String id = Integer.toString(row.getExperimentId());
        return match(id) || match(row.getAntibody()) || match(row.getSample())
                || match(row.getName()) || match(row.getProject())
                || match(row.getDescription());
    }
}
