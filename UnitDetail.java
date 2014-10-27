package org.lac.bionimbus.client;

import java.util.Date;
import java.util.Vector;

import org.lac.bionimbus.shared.BNPermissions.PermissionsEntity;
import org.lac.bionimbus.shared.BNPermissions.RW;
import org.lac.bionimbus.shared.Unit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Header;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.VerticalPanel;

public class UnitDetail extends DialogBox implements RequestCallback
{

    private int                        gridRowCounter = 0;
    private String                     cistrackID;
    private int                        unitID;
    private Grid                       g              = null;
    private Grid                       fileGrid;
    private final ClientInterfaceAsync rpc            = GWT.create(ClientInterface.class);
    Button                             downloadButton;
    Button                             dropBoxButton;
    Button                             editPermissions;
    Button                             galaxyButton;

    //

    public UnitDetail(int unitID, String cistrackID)
    {
        super(true);
        this.cistrackID = cistrackID;
        this.unitID = unitID;
        setText("Details for " + cistrackID + ".");
        draw();
        getUnitDetails();
    }

    private void getUnitDetails()
    {
        AsyncCallback<Unit> callback = new AsyncCallback<Unit>()
        {
            public void onFailure(Throwable caught)
            {
                new ErrorBox(caught);
            }

            public void onSuccess(Unit result)
            {
                update(result);
            }
        };
        rpc.getUnitDetail(unitID, callback);
    }

    private void draw()
    {
        Button closeButton = new Button("Close", new ClickHandler()
        {
            public void onClick(ClickEvent event)
            {
                hide();
            }
        });
        g = new Grid(14, 2);

        fileGrid = new Grid(10, 2);
        VerticalPanel vp = new VerticalPanel();
        vp.add(g);
        vp.add(closeButton);
        add(vp);
        center();

        downloadButton = new Button("Download", new ClickHandler()
        {
            public void onClick(ClickEvent event)
            {
                CistrackUI.makeDownload("Unit " + unitID, -1, unitID, -1);
            }
        });
        downloadButton.setEnabled(false);

        dropBoxButton = new Button("Add to Dropbox / Push to cloud",
                new ClickHandler()
                {
                    public void onClick(ClickEvent event)
                    {
                        Dropbox.addUnit(unitID);
                    }
                });
        dropBoxButton.setEnabled(false);

        Button libraryButton = new Button("Library", new ClickHandler()
        {
            public void onClick(ClickEvent event)
            {
                try
                {
                    new LibraryTrackerClient(cistrackID, null);
                }
                catch (Exception e)
                {
                    new ErrorBox(e);
                }
            }
        });

        editPermissions = new Button("Permissions", new ClickHandler()
        {
            public void onClick(ClickEvent event)
            {
                try
                {
                    new EditPermissions(unitID, PermissionsEntity.Unit,
                            RW.Read, "Experiment unit " + cistrackID);
                }
                catch (Exception e)
                {
                    new ErrorBox(e);
                }
            }
        });
        editPermissions.setEnabled(false);

        galaxyButton = new Button("Export to Galaxy", new ClickHandler()
        {
            public void onClick(ClickEvent event)
            {
                exportToGalaxy();
            }
        });

        galaxyButton.setEnabled(false);

        g.setText(gridRowCounter++, 0, "PI");
        g.setText(gridRowCounter++, 0, "Species");
        g.setText(gridRowCounter++, 0, "Antibody");
        g.setText(gridRowCounter++, 0, "Sample");
        g.setText(gridRowCounter++, 0, "Catalog");
        g.setText(gridRowCounter++, 0, "Type");
        g.setText(gridRowCounter++, 0, "Subtype");
        g.setText(gridRowCounter++, 0, "Protein");
        g.setWidget(gridRowCounter++, 0, downloadButton);

        if (CistrackUI.isGuest() == false)
        {
            g.setWidget(gridRowCounter++, 0, dropBoxButton);
            g.setWidget(gridRowCounter++, 0, libraryButton);
            g.setWidget(gridRowCounter++, 0, editPermissions);
            g.setWidget(gridRowCounter++, 0, galaxyButton);
        }

        g.setWidget(gridRowCounter++, 0, fileGrid);
    }

    private void update(Unit res)
    {
        gridRowCounter = 0;
        g.setText(gridRowCounter++, 1, res.getPI());
        g.setText(gridRowCounter++, 1, res.getSpecies());
        g.setText(gridRowCounter++, 1, res.getAntiBody());
        g.setText(gridRowCounter++, 1, res.getSample());
        g.setText(gridRowCounter++, 1, res.getCatalog());
        g.setText(gridRowCounter++, 1, res.getType());
        g.setText(gridRowCounter++, 1, res.getSubType());
        g.setText(gridRowCounter, 1, res.getProtein());
        if (res.hasFiles())
        {
            Vector<String> files = res.getFiles();
            fileGrid.resize(files.size(), 2);
            for (int i = 0; i < files.size(); ++i)
            {
                String ps = files.elementAt(i);
                String[] dat = ps.split(",");
                fileGrid.setText(i, 0, dat[0]);
                fileGrid.setText(i, 1, "         " + dat[1]);
            }

            dropBoxButton.setEnabled(true);
            downloadButton.setEnabled(true);
            editPermissions.setEnabled(res.writable);
            galaxyButton.setEnabled(true);
        }
    }

    void exportToGalaxy()
    {
        AsyncCallback<String> callback = new AsyncCallback<String>()
        {
            public void onFailure(Throwable caught)
            {
                new ErrorBox("Failed to export files : " + caught);
            }

            public void onSuccess(String result)
            {
                Window.alert(result);
                Date now = new Date();
                long nowLong = now.getTime();
                nowLong = nowLong + (1000 * 60 * 60 * 24 * 7);//seven days
                now.setTime(nowLong);
                Cookies.setCookie("galaxysession", result, now,
                        "bc.bionimbus.org", "/", false);
                Window.open("http://bc.bionimbus.org/galaxy/", "galaxy", "");
            }
        };

        try
        {
            rpc.exportToGalaxy(cistrackID, callback);
        }
        catch (Exception e)
        {
            Window.alert("Error contacting server");
        }
    }

    public static native void foofer(String url, String body)
    /*-{
                parameters = body; 
                javascript:var req = new XMLHttpRequest(); 
                req.open('POST', url , false); 
                //Send the proper header information along with the request 
                req.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
                req.setRequestHeader("Content-length", parameters.length); 
                req.send(parameters); 
                alert(req.responseText); 
                var headers = req.getAllResponseHeaders().cookies().toLowerCase(); 
                alert(headers); 
    }-*/;

    public void onResponseReceived(Request request, Response response)
    {
        Header[] hdrs = response.getHeaders();

        /* 
        for (Header h : hdrs)
        {
            new ErrorBox(h.toString());
        }
        */

    }

    public void onError(Request request, Throwable exception)
    {
        // TODO Auto-generated method stub

    }
}
