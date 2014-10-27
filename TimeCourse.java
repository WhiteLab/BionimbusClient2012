package org.lac.bionimbus.client;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TimeCourse extends VerticalPanel
{
    private static final String        samples[] = { "E-0-4h", "E-4-8h",
            "E-8-12h", "E-12-16h", "E-16-20h", "E-20-24h", "L1", "L2", "L3",
            "Pupae", "AdultFemale", "AdultMale" };
    private static final String        agents[]  = { "H3K4Me3", "H3K4Me1",
            "H3K9AC", "H3K9Me3", "H3K27AC", "H3K27Me3", "PolII", "CBP" };
    private final ClientInterfaceAsync rpc       = GWT.create(ClientInterface.class);
    private Grid                       agilentGrid;
    private Grid                       solexaGrid;
    private VerticalPanel              agilentPanel;
    private VerticalPanel              solexaPanel;

    Grid makeGrid()
    {
        return new BnGrid(agents.length + 1, samples.length + 1);
    }

    TimeCourse()
    {
        super();

        // Window.setTitle("Drosophilia Chromatin Time Course");

        setSpacing(5);
        TabPanel tp = new TabPanel();
        agilentPanel = new VerticalPanel();
        solexaPanel = new VerticalPanel();
        tp.add(agilentPanel, "Agilent");
        tp.add(solexaPanel, "Solexa");
        tp.selectTab(0);

        add(tp);

        RootPanel.get().add(this);

        getData();

        agilentGrid = makeGrid();
        agilentPanel.add(agilentGrid);

        solexaGrid = makeGrid();
        solexaPanel.add(solexaGrid);

        for (int i = 0; i < samples.length; i++)
        {
            agilentGrid.setText(0, i + 1, samples[i]);
            solexaGrid.setText(0, i + 1, samples[i]);
            if (i < agents.length)
            {
                agilentGrid.setText(i + 1, 0, agents[i]);
                solexaGrid.setText(i + 1, 0, agents[i]);
            }
        }

    }

    private void getData()
    {
        AsyncCallback<ArrayList<HashMap<String, HashMap<String, Integer>>>> callback = new AsyncCallback<ArrayList<HashMap<String, HashMap<String, Integer>>>>()
        {
            public void onFailure(Throwable caught)
            {
                // TODO : do something when rpc fails
                new ErrorBox("Rpc to fetch data failed " + caught);
            }

            public void onSuccess(
                    ArrayList<HashMap<String, HashMap<String, Integer>>> result)
            {
                update(result);
            }
        };

        try
        {
            rpc.getTimeCourseMaps(samples, agents, callback);
        }
        catch (Exception e)
        {
            new ErrorBox("Error! Details : " + e);
        }
    }

    private void update(
            ArrayList<HashMap<String, HashMap<String, Integer>>> rpcResult)
    {
        HashMap<String, HashMap<String, Integer>> agilentAgentMap;
        HashMap<String, HashMap<String, Integer>> solexaAgentMap;

        agilentAgentMap = rpcResult.get(0);
        solexaAgentMap = rpcResult.get(1);

        for (int i = 1; i < agents.length + 1; i++)
        {
            HashMap<String, Integer> map = solexaAgentMap.get(agents[i - 1]);

            for (int j = 1; j < samples.length + 1; j++)
            {

                final Integer expId = map.get(samples[j - 1]);
                if (expId == null)
                {
                    solexaGrid.setText(i, j, "N/A");
                    continue;
                }

                solexaGrid.setWidget(i, j, new Button("CD_" + expId,
                        new ClickHandler()
                        {
                            public void onClick(ClickEvent e)
                            {
                                new ExperimentDetail(expId);
                            }
                        }));
            }
        }

        for (int i = 1; i < agents.length + 1; i++)
        {
            HashMap<String, Integer> map = agilentAgentMap.get(agents[i - 1]);

            for (int j = 1; j < samples.length + 1; j++)
            {

                final Integer expId = map.get(samples[j - 1]);
                if (expId == null)
                {
                    agilentGrid.setText(i, j, "N/A");
                    continue;
                }

                agilentGrid.setWidget(i, j, new Button("CD_" + expId,
                        new ClickHandler()
                        {
                            public void onClick(ClickEvent e)
                            {
                                new ExperimentDetail(expId);
                            }
                        }));
            }
        }
    }

}
