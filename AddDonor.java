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
import com.google.gwt.user.client.ui.VerticalPanel;

public class AddDonor extends DialogBox
{

    private final ClientInterfaceAsync rpc       = GWT.create(ClientInterface.class);
    private TextBox                    donorName = new TextBox();
    private SQLListBox                 oid       = new SQLListBox(
                                                         SQLListBoxQuery.items.SQLLIST_ORGANISM);
    private TextBox                    sex       = new TextBox();
    private TextBox                    sAge      = new TextBox();
    private TextBox                    aUnit     = new TextBox();
    private TextBox                    eAge      = new TextBox();
    private TextBox                    dStage    = new TextBox();
    private TextBox                    strain    = new TextBox();
    private TextBox                    desc      = new TextBox();
    private Button                     ok        = new Button("Ok");
    private Button                     cancel    = new Button("Cancel");
    //private DialogBox                  db        = null;
    private Grid                       g         = null;
    private SQLListBox                 donor;

    //public AddDonor()
    //{
    //}

    public AddDonor(SQLListBox donor)
    {
        this.donor = donor;
        //db = new DialogBox();
        center();
        setText("Add new donor");
        g = new Grid(10, 2);
        VerticalPanel vp = new VerticalPanel();
        vp.add(ok);
        ok.addClickHandler(new ClickHandler()
        {
            public void onClick(ClickEvent event)
            {
                insertDonor();
            }
        });

        vp.add(cancel);

        cancel.addClickHandler(new ClickHandler()
        {
            public void onClick(ClickEvent event)
            {
                hide();
            }
        });

        int gridRowCounter = 0;

        g.setWidget(gridRowCounter, 0, new Label("Donor name"));
        g.setWidget(gridRowCounter++, 1, donorName);
        g.setWidget(gridRowCounter, 0, new Label("Organism Id"));
        g.setWidget(gridRowCounter++, 1, oid);
        g.setWidget(gridRowCounter, 0, new Label("Sex"));
        g.setWidget(gridRowCounter++, 1, sex);
        g.setWidget(gridRowCounter, 0, new Label("Start age"));
        g.setWidget(gridRowCounter++, 1, sAge);
        g.setWidget(gridRowCounter, 0, new Label("End age"));
        g.setWidget(gridRowCounter++, 1, eAge);
        g.setWidget(gridRowCounter, 0, new Label("Age unit"));
        g.setWidget(gridRowCounter++, 1, aUnit);
        g.setWidget(gridRowCounter, 0, new Label("Development stage"));
        g.setWidget(gridRowCounter++, 1, dStage);
        g.setWidget(gridRowCounter, 0, new Label("Strain"));
        g.setWidget(gridRowCounter++, 1, strain);
        g.setWidget(gridRowCounter, 0, new Label("Description"));
        g.setWidget(gridRowCounter++, 1, desc);

        g.setWidget(gridRowCounter, 0, vp);

        donorName.setFocus(true);

        add(g);
        //hide();
    }

    private void insertDonor()
    {

        AsyncCallback<Integer> callback = new AsyncCallback<Integer>()
        {

            public void onFailure(Throwable caught)
            {
                // TODO : do something when rpc fails
                new ErrorBox("RPC FAILED : " + caught);
            }

            public void onSuccess(final Integer result)
            {
                hide();
                donor.refresh();
                donor.setSelectedIndex(donor.getItemCount() - 1);
            }
        };

        if (donorName.getText().equals(""))
        {
            new ErrorBox("Please enter a donor name");
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

        if (!oid.isValid())
        {
            new ErrorBox("Please select an organism type");
            oid.removeStyleName("Valid");
            oid.addStyleName("Invalid");
            oid.setFocus(true);
            return;
        }
        else
        {
            oid.removeStyleName("Invalid");
            oid.addStyleName("Valid");
        }

        Float ageStart = null, ageEnd = null;

        try
        {
            //TODO: the better parsing 
            ageStart = new Float(sAge.getText());
            ageEnd = new Float(eAge.getText());
            // TODO: return real errors here
        }
        catch (Exception e)
        {

        }

        try
        {
            rpc.addDonor(donorName.getText().trim(), oid.getSelectedValue(),
                    sex.getText().trim(), ageStart, ageEnd, aUnit.getText()
                            .trim(), dStage.getText().trim(), strain.getText()
                            .trim(), desc.getText().trim(), callback);
        }
        catch (Exception e)
        {
            new ErrorBox("RPC to add donor failed due to " + e);
            donor.setEnabled(false);
        }
    }

}
