package org.lac.bionimbus.client;


/* public class LibraryDetails extends DialogBox implements
        org.lac.bionimbus.client.Refreshable
{

    private final ClientInterfaceAsync rpc      = GWT.create(ClientInterface.class);

    String                             titles[] = { "Index squence", "Type",
            "Bases Requested", "Bases Covered", "Read 1 length",
            "Read 2 length", "Timestamp", "Concentration", "Status ", "Notes" };

    Grid                               grid     = makeGrid();

    Grid makeGrid()
    {
        Grid g = new Grid();
        g.resize(2, titles.length);
        for (int i = 0; i < titles.length; ++i)
        {
            g.setText(0, i, titles[i]);

        }
        return g;
    }

    final String                         bionimbus_id;
    final String                         library_name;

    org.lac.bionimbus.client.Refreshable parent;

    LibraryDetails(org.lac.bionimbus.client.Refreshable r,
            String _bionimbus_id, String _library_name, String message)
    {
        this.bionimbus_id = _bionimbus_id;
        this.library_name = _library_name;

        parent = r;

        setText(message);

        VerticalPanel vp = new VerticalPanel();

        Button addButton = new Button("Add", new ClickHandler()
        {
            public void onClick(ClickEvent event)
            {
                try
                {
                    new AddLibraryContents(LibraryDetails.this, bionimbus_id,
                            library_name, "Updating the library");
                }
                catch (Exception e)
                {
                    new ErrorBox(e);
                }
            }
        });

        Button okButton = new Button("Ok", new ClickHandler()
        {
            public void onClick(ClickEvent event)
            {
                hide();
            }
        });

        vp.add(grid);
        vp.add(addButton);
        vp.add(okButton);

        add(vp);

        center();
        show();
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

                grid.resize(contents.size() + 1, titles.length);
                for (int a = 0; a < contents.size(); ++a)
                {
                    int row = a + 1;
                    LibraryContents lc = contents.elementAt(a);
                    grid.setText(row, 0, "" + lc.index);
                    grid.setText(row, 1, "" + lc.type);
                    grid.setText(row, 2, "" + lc.bases_requested);
                    grid.setText(row, 3, "" + lc.bases_covered);
                    grid.setText(row, 4, "" + lc.run_1);
                    grid.setText(row, 5, "" + lc.run_2);
                    grid.setText(row, 6, "" + lc.time);
                    grid.setText(row, 7, "" + lc.concentration);
                    grid.setText(row, 8, "" + lc.status);
                    grid.setText(row, 9, "" + lc.notes);
                }
            }
        };

        try
        {
            rpc.getLibrary(library_name, callback);
        }
        catch (Exception e)
        {
            new ErrorBox(e);
        }
    }
}
*/