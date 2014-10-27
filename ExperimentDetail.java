package org.lac.bionimbus.client;

import org.lac.bionimbus.shared.ExperimentDetailRes;
import org.lac.bionimbus.shared.UnitRole;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ExperimentDetail extends DialogBox
{
    private int                        eid      = 0;
    private final ClientInterfaceAsync rpc      = GWT.create(ClientInterface.class);
    private Grid                       grid     = null;
    private Grid                       unitGrid = null;
    Button                             downloadButton;

    public ExperimentDetail(int eid)
    {
        this.eid = eid;
        draw();
        getExperiments();
    }

    public void getExperiments()
    {
        AsyncCallback<ExperimentDetailRes> callback = new AsyncCallback<ExperimentDetailRes>()
        {
            public void onFailure(Throwable caught)
            {
                // TODO : do something when rpc fails
                new ErrorBox("Experiment Detail RPC failed" + caught);
            }

            public void onSuccess(ExperimentDetailRes result)
            {
                update(result);
            }
        };

        rpc.getExperimentDetail(eid, callback);
    }

    public void draw()
    {
        setText("Experiment Detail " + eid);
        Button closeButton = new Button("Close", new ClickHandler()
        {
            public void onClick(ClickEvent event)
            {
                hide();
            }
        });
        grid = new Grid(10, 2);

        unitGrid = new Grid(1, 3);

        VerticalPanel vp = new VerticalPanel();
        vp.add(grid);
        vp.add(unitGrid);
        vp.add(closeButton);

        add(vp);
        center();
        show();

        downloadButton = new Button("Download", new ClickHandler()
        {
            public void onClick(ClickEvent event)
            {
                CistrackUI.makeDownload("Experiment " + eid, eid, -1, -1);
            }
        });

        Button dropBoxButton = new Button("Add to Dropbox / Push to cloud ",
                new ClickHandler()
                {
                    public void onClick(ClickEvent event)
                    {
                        Dropbox.addExperiment(eid);
                    }
                });

        int gridRowCounter = 0;
        grid.setText(gridRowCounter++, 0, "Experiment Id");
        grid.setText(gridRowCounter++, 0, "PI");
        grid.setText(gridRowCounter++, 0, "Species");
        grid.setText(gridRowCounter++, 0, "Antibody");
        grid.setText(gridRowCounter++, 0, "Sample");
        grid.setText(gridRowCounter++, 0, "Catalog");
        grid.setText(gridRowCounter++, 0, "Type");
        grid.setText(gridRowCounter++, 0, "Protein");

        downloadButton.setEnabled(false);
        grid.setWidget(gridRowCounter++, 0, downloadButton);

        if (CistrackUI.isGuest() == false)
        {
            grid.setWidget(gridRowCounter++, 0, dropBoxButton);
        }

    }

    private void update(ExperimentDetailRes res)
    {
        int gridRowCounter = 0;
        grid.setText(gridRowCounter++, 1, eid + "");
        grid.setText(gridRowCounter++, 1, res.getPI());

        grid.setText(gridRowCounter++, 1, res.getSpecies());
        grid.setText(gridRowCounter++, 1, res.getAntiBody());

        grid.setText(gridRowCounter++, 1, res.getSample());
        grid.setText(gridRowCounter++, 1, res.getCatalog());

        grid.setText(gridRowCounter++, 1, res.getType());
        grid.setText(gridRowCounter++, 1, res.getProtein());

        if (res.unitRole.size() > 0)
        {
            unitGrid.resize(res.unitRole.size(), 3);
            unitGrid.setText((res.unitRole.size() / 2), 0, "Experiment Units");

            int unitGridRowCounter = 0;

            for (int i = 0; i < res.unitRole.size(); i++)
            {
                final UnitRole temp = res.unitRole.get(i);
                unitGrid.setWidget(unitGridRowCounter, 1,
                        new Button(temp.getCistrackID(), new ClickHandler()
                        {
                            public void onClick(ClickEvent event)
                            {
                                new UnitDetail(temp.getID(), temp
                                        .getCistrackID());
                            }
                        }));
                unitGrid.setWidget(unitGridRowCounter++, 2,
                        new Label(temp.getRole()));
            }
        }
        downloadButton.setEnabled(res.has_download);
    }
}
