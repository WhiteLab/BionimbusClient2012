package org.lac.bionimbus.client;

import org.lac.bionimbus.shared.SQLListBoxQuery;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public class AddSample
{

    private TextBox                    sampleName;
    private SQLListBox                 donorName = new SQLListBox(
                                                         SQLListBoxQuery.items.SQLLIST_DONOR);
    private Button                     addDonor  = new Button("Add Donor");
    private TextBox                    desc;
    private Button                     accept    = new Button("Ok");
    private Button                     cancel    = new Button("Cancel");
    private DialogBox                  db;
    // private VerticalPanel vp = new VerticalPanel();
    private Grid                       g         = null;
    private final ClientInterfaceAsync rpc       = GWT.create(ClientInterface.class);

    private UpdateSamples              us;

    private int                        project;

    //public AddSample()
    //{
    //}

    public AddSample(UpdateSamples us, int _project)
    {
        this.us = us;
        project = _project;

        g = new Grid(5, 3);

        int gridRowCounter = 0;

        g.setWidget(gridRowCounter, 0, new Label("Sample name "));
        sampleName = new TextBox();
        g.setWidget(gridRowCounter++, 1, sampleName);

        g.setWidget(gridRowCounter, 0, new Label("Donor name "));
        g.setWidget(gridRowCounter, 1, donorName);
        g.setWidget(gridRowCounter++, 2, addDonor);

        addDonor.addClickHandler(new ClickHandler()
        {
            public void onClick(ClickEvent e)
            {
                AddDonor ad = new AddDonor(donorName);
                ad.show();
            }
        });

        g.setWidget(gridRowCounter, 0, new Label("Description"));
        desc = new TextBox();
        g.setWidget(gridRowCounter++, 1, desc);
        g.setWidget(gridRowCounter++, 0, accept);

        accept.addClickHandler(new ClickHandler()
        {
            public void onClick(ClickEvent event)
            {
                insertSample();
            }
        });

        g.setWidget(gridRowCounter, 0, cancel);
        cancel.addClickHandler(new ClickHandler()
        {
            public void onClick(ClickEvent event)
            {
                db.hide();
            }
        });
        db = new DialogBox();
        db.add(g);
        db.setText("Add new sample");
        db.center();
        db.hide();
    }

    public void show()
    {
        db.show();
        sampleName.setFocus(true);
    }

    private void insertSample()
    {
        AsyncCallback<Integer> callback = new AsyncCallback<Integer>()
        {
            public void onFailure(Throwable caught)
            {
                // TODO : do something when rpc fails
                new ErrorBox("RPC FAILED");
            }

            public void onSuccess(final Integer result)
            {
                db.hide();
                us.refreshSampleRows(result.intValue());
            }
        };

        if (sampleName.getText().equals(""))
        {
            new ErrorBox("Please enter a name for the new sample");
            sampleName.removeStyleName("Valid");
            sampleName.addStyleName("Invalid");
            sampleName.setFocus(true);
            return;
        }
        else
        {
            sampleName.removeStyleName("Invalid");
            sampleName.addStyleName("Valid");
        }

        if (!donorName.isValid())
        {
            new ErrorBox(
                    "Please select a Donor from the list or Add a new Donor");
            donorName.removeStyleName("Valid");
            donorName.addStyleName("Invalid");
            donorName.setFocus(true);
            return;
        }
        else
        {
            donorName.removeStyleName("Invalid");
            donorName.addStyleName("Valid");
        }

        rpc.addSample(sampleName.getText().trim(),
                donorName.getSelectedValue(), desc.getText().trim(), project,
                callback);
    }
}
