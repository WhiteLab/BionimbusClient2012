package org.lac.bionimbus.client;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.lac.bionimbus.shared.LibraryContents;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.VerticalPanel;

/* library tables 
 *
   
   create table library_contents (
     bionimbus_id varxhar(20) , 
     name varchar(50)  ,
     manufacturer character varying(255)  ,
     bases_requested int,
     bases_covered int,
     status character varying(50),
     time timestamp default now() , 
     run_1 integer , 
     run_2 integer , 
     concentration double precision ,
     type character varying(50),
     index character varying(50),
     notes text
   );  
   
*/

public class LibraryTrackerClient extends DialogBox implements
        org.lac.bionimbus.client.Refreshable
{
    VerticalPanel                      vp      = new VerticalPanel();
    int                                columns = 0;

    Grid                               grid;
    final String                       bionimbusID;
    final String                       libraryID;

    private final ClientInterfaceAsync rpc     = GWT.create(ClientInterface.class);

    String[]                           titles  = LibraryContents.titles;

    Grid makeTitleGrid()
    {
        Grid g = new BnGrid(2, columns + 1);
        g.setText(0, 0, "Library name");
        for (int a = 0; a < columns; ++a)
        {
            g.setText(0, a + 1, titles[a]);
        }
        return g;
    }

    LibraryTrackerClient(String _bionimbusID, String _libraryID)
    {
        List<String> names = Arrays.asList(LibraryContents.titles);
        //names.add(0, "Library name");
        Vector<String> titles = new Vector<String>(names);

        columns = titles.size() + 1;
        bionimbusID = _bionimbusID;
        libraryID = _libraryID;

        setText("Libraries for " + bionimbusID);

        grid = makeTitleGrid();
        vp.add(grid);

        /* 
        Button addButton = new Button("Add", new ClickHandler()
        {
            public void onClick(ClickEvent event)
            {
                try
                {
                    new AddLibraryContents(LibraryTrackerClient.this,
                            bionimbusID, null, "Creating new library for "
                                    + bionimbusID);
                }
                catch (Exception e)
                {
                    new ErrorBox(e);
                }
            }
        });

        vp.add(addButton);
        */

        Button okButton = new Button("Ok", new ClickHandler()
        {
            public void onClick(ClickEvent event)
            {
                hide();
            }
        });

        vp.add(okButton);

        add(vp);
        show();
        center();
        update();
    }

    public void update()
    {
        AsyncCallback<Vector<LibraryContents>> callback = new AsyncCallback<Vector<LibraryContents>>()
        {

            public void onFailure(Throwable caught)
            {
                new ErrorBox(caught);
            }

            public void onSuccess(Vector<LibraryContents> contents)
            {
                grid.resize(contents.size() + 1, columns + 2);
                int row = 0;
                //String oldName = "";

                for (final LibraryContents entry : contents)
                {
                    //final LibraryContents entry = entry.name;
                    ++row;

                    int col = 0;

                    grid.setText(row, col++, entry.name);
                    grid.setText(row, col++, entry.index);
                    grid.setText(row, col++, entry.manufacturer);
                    grid.setText(row, col++, entry.type);
                    grid.setText(row, col++, Utils.ntoS(entry.bases_requested));
                    grid.setText(row, col++, Utils.ntoS(entry.bases_covered));
                    grid.setText(row, col++, Utils.ntoS(entry.run_1));
                    grid.setText(row, col++, Utils.ntoS(entry.run_2));
                    grid.setText(row, col++, Utils.ntoS(entry.concentration));
                    grid.setText(row, col++, entry.status);
                    grid.setText(row, col++, entry.notes);

                    Button alb = new Button("Update", new ClickHandler()
                    {
                        public void onClick(ClickEvent event)
                        {
                            try
                            {
                                new AddLibraryContents(
                                        LibraryTrackerClient.this, bionimbusID,
                                        entry, "Updating status of library "
                                                + entry.name);
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                                new ErrorBox(e);
                            }
                        }
                    });

                    grid.setWidget(row, col++, alb);
                }
                grid.resize(row + 1, columns + 1);

            }
        };

        try
        {
            rpc.getLibraryForSample(bionimbusID, true, callback);
        }
        catch (Exception e)
        {
            new ErrorBox(e);
        }
    }
}
