package org.lac.bionimbus.client;

import java.util.Vector;

import org.lac.bionimbus.shared.Unit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ExperimentUnitTable extends VerticalPanel
{
    private UnitExperimentBar          bar        = new UnitExperimentBar();
    private final ClientInterfaceAsync rpc        = GWT.create(ClientInterface.class);

    private boolean                    guest_only = true;

    // private VerticalPanel dbPanel = new VerticalPanel();

    public static void selfUpdate()
    {
        if (self != null)
        {
            self.updateUnits();
            //self.updateExperiments();
        }
    }

    private static ExperimentUnitTable self;

    public ExperimentUnitTable(boolean _guest_only)
    {
        guest_only = _guest_only;

        setSpacing(5);
        Window.setTitle("Experiments and Units");

        add(bar);
        setSpacing(5);
        bar.selectTab(0);

        // updateUnits();
        //updateExperiments();

        RootPanel.get().add(this);
        self = this;
    }

    /*
    private void updateExperiments()
    {
        AsyncCallback<Vector<Experiment>> callback = new AsyncCallback<Vector<Experiment>>()
        {
            public void onFailure(Throwable caught)
            {
                // TODO : do something when rpc fails
                new ErrorBox(caught);
            }

            public void onSuccess(Vector<Experiment> result)
            {
                updateExperiments(result);
            }
        };

        rpc.getExperimentsForUser(false, callback);
    }
    */

    private void updateUnits()
    {
        AsyncCallback<Vector<Unit>> callback = new AsyncCallback<Vector<Unit>>()
        {
            public void onFailure(Throwable caught)
            {
                // TODO : do something when rpc fails
                new ErrorBox(caught);
            }

            public void onSuccess(Vector<Unit> result)
            {
                updateUnits(result);
            }
        };

        rpc.getUnitsForUser(guest_only, callback);
    }

    protected void updateUnits(Vector<Unit> result)
    {
        bar.unitGrid.setContent(result);
        bar.unitGrid.scroll();
    }

    /*
    protected void updateExperiments(Vector<Experiment> tfResult)
    {
        bar.experimentGrid.setContent(tfResult);
        bar.experimentGrid.scroll();
    }
    */
}
