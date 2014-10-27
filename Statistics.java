package org.lac.bionimbus.client;

import org.lac.bionimbus.shared.StatResult;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

public class Statistics extends DialogBox
{
    private Grid                       statGrid;
    private VerticalPanel              statPanel;
    private final ClientInterfaceAsync rpc = GWT.create(ClientInterface.class);

    public Statistics()
    {
        super(false);
        setText("Bionimbus statistics");
        setHeight("50px");

        statPanel = new VerticalPanel();
        statPanel.setSpacing(5);
        statGrid = new Grid(5, 2);
        statGrid.addStyleName("Grid");
        statGrid.setCellSpacing(5);
        makeRpc();
        statPanel.add(statGrid);
        statPanel.add(new Button("Ok", new ClickHandler()
        {
            public void onClick(ClickEvent e)
            {
                hide();
            }
        }));
        add(statPanel);

        statGrid.setText(0, 0, "Number of Experiments");
        statGrid.setText(1, 0, "Number of Units");
        statGrid.setText(2, 0, "Number of Users");
        statGrid.setText(3, 0, "Size of the Bionimbus Database");
        statGrid.setText(4, 0, "Aggregated Size of all Bionimbus Data Files");
        statGrid.getRowFormatter().setStyleName(0, "GridItem");
        statGrid.getRowFormatter().setStyleName(2, "GridItem");
        statGrid.getRowFormatter().setStyleName(4, "GridItem");

        center();
    }

    private void makeRpc()
    {
        AsyncCallback<StatResult> callback = new AsyncCallback<StatResult>()
        {
            public void onFailure(Throwable caught)
            {
                // TODO : do something when rpc fails
                new ErrorBox("Failure in retreiving Bionimbus Statistics"
                        + caught);
            }

            public void onSuccess(StatResult result)
            {
                update(result);
            }
        };

        rpc.getStatistics(callback);
    }

    private void update(StatResult result)
    {
        statGrid.setText(0, 1, result.getExperimentCount() + "");
        statGrid.setText(1, 1, result.getUnitCount() + "");
        statGrid.setText(2, 1, result.getUserCount() + "");
        statGrid.setText(3, 1, result.getDbSize() / (1024 * 1024) + " MB");
        statGrid.setText(4, 1, result.getFileSize() / (1024 * 1024 * 1024)
                + " GB");
    }
}
