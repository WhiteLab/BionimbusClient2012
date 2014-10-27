package org.lac.bionimbus.client;

import java.util.Vector;

import org.lac.bionimbus.shared.RequestOffer;

import com.google.gwt.user.client.Window;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

class ViewRequestOfferGrid extends RowGrid<RequestOffer>
{
    ViewRequestOffer parent = null;

    public ViewRequestOfferGrid(ViewRequestOffer parent)
    {
        this.parent = parent;
    }

    Vector<String> titles()
    {
        Vector<String> h = new Vector<String>();
        h.add("ID");
        h.add("");
        h.add("Created");
        h.add("Requestor");
        h.add("Billing Status");
        h.add("Sequencing Status");
        return h;
    }

    void renderRow(int vizRow, int contentRow)
    {
        final RequestOffer r = content.elementAt(contentRow);

        Button button = new Button("View Request", new ClickHandler()
        {
            public void onClick(ClickEvent e)
            {
                new RequestOfferDialogBox(r, parent);
            }
        });

        int col = 0;
        setText(vizRow, col++, r.id + "");
        setWidget(vizRow, col++, button);
        setText(vizRow, col++, r.created.toString());
        setText(vizRow, col++, r.perRequestor.getNameString());
        setText(vizRow, col++, r.statusBilling);
        setText(vizRow, col++, r.statusSequencing);
    }

    boolean filterRow(RequestOffer r)
    {
        return match(r.id + "") || match(r.perRequestor.getNameString())
                || match(r.statusBilling) || match(r.statusSequencing);
    }
}

class ViewRequestOffer extends VerticalPanelWithHeader
{
    ViewRequestOfferGrid grid = null;

    public ViewRequestOffer()
    {
        super("View All Requests");
        RootPanel.get().add(this);
        refresh();
    }

    private void displayRequestOffers(Vector<RequestOffer> r)
    {
        if (grid != null)
            this.remove(grid);
        grid = new ViewRequestOfferGrid(this);
        grid.setContent(r);
        add(grid);
    }

    public void refresh()
    {
        AsyncCallback<Vector<RequestOffer>> callback = new AsyncCallback<Vector<RequestOffer>>()
        {
            public void onFailure(Throwable caught)
            {
                Window.alert( "foo" );
                new ErrorBox(new Exception(
                        "ViewRequestOffer.refresh(), rpc onFailure()", caught));
            }

            public void onSuccess(Vector<RequestOffer> callbackResult)
            {
                try
                {
                    displayRequestOffers(callbackResult);
                }
                catch (Exception e)
                {
                    new ErrorBox(
                            new Exception(
                                    "ViewRequestOffer.refresh(), rpc onSuccess(), displayRequestOffers()",
                                    e));
                }
            }
        };

        try
        {
            CistrackUI.rpc.getAllRequestOffers(callback);

        }
        catch (Exception e)
        {
            new ErrorBox(new Exception(
                    "ViewRequestOffer(), failure while initiating rpc", e));
        }

    }
}
