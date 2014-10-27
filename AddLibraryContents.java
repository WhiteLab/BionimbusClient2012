package org.lac.bionimbus.client;

import org.lac.bionimbus.shared.LibraryContents;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

class AddLibraryContents extends DialogBox
{
    private final ClientInterfaceAsync   rpc             = GWT.create(ClientInterface.class);

    org.lac.bionimbus.client.Refreshable parent;

    final String                         bionimbus_id;
    LibraryContents                      contents;

    TextBox                              index;
    UserListBox                          type;
    UserListBox                          manufacturer;
    UserTextBox                          bases_requested = new UserTextBox();
    UserTextBox                          bases_covered   = new UserTextBox();
    UserTextBox                          run_1           = new UserTextBox();
    UserTextBox                          run_2           = new UserTextBox();
    UserTextBox                          concentration   = new UserTextBox();
    UserListBox                          statusBox;
    TextArea                             notes           = new TextArea();

    String                               statuses[]      = { "Run", "RunQC",
            "Failed", "Completed", "N/A"                };

    String                               typeStrings[]   = { "RNAseq",
            "exome-seq", "ChIP-seq", "DNA-seq", "small-RNAseq",
            "mate-pair DNAseq", "other"                 };

    private String[]                     manufacturers   = { "Illumina",
            "Epicentre", "Agilent", "Nimblegen", "Custom" };

    Grid makeGrid()
    {
        Grid g = new Grid();
        String[] titles = LibraryContents.titles;

        g.resize(2, titles.length);
        for (int i = 0; i < titles.length; ++i)
        {
            g.setText(0, i, titles[i]);

        }

        int col = 0;

        g.setWidget(1, col++, index);
        g.setWidget(1, col++, manufacturer);
        g.setWidget(1, col++, type);
        g.setWidget(1, col++, bases_requested);
        g.setWidget(1, col++, bases_covered);
        g.setWidget(1, col++, run_1);
        g.setWidget(1, col++, run_2);
        g.setWidget(1, col++, concentration);
        g.setWidget(1, col++, statusBox);
        g.setWidget(1, col++, notes);

        if (contents != null)
        {
            index.setText(contents.index);
            manufacturer.setSelectedString(contents.manufacturer);
            type.setSelectedString(contents.type);
            bases_requested.setText(Utils.ntoS(contents.bases_requested));
            bases_covered.setText(Utils.ntoS(contents.bases_covered));
            run_1.setText(Utils.ntoS(contents.run_1));
            run_2.setText(Utils.ntoS(contents.run_2));
            concentration.setText(Utils.ntoS(contents.concentration));
            statusBox.setSelectedString(contents.status);
            notes.setText(contents.notes);
        }

        return g;
    }

    AddLibraryContents(org.lac.bionimbus.client.Refreshable _parent,
            String bionimbus_id, LibraryContents contents, String message)
    {
        // "Adding an entry to the library"
        setText(message);

        parent = _parent;
        this.bionimbus_id = bionimbus_id;
        this.contents = contents;

        index = new TextBox();
        //Utils.fillBarcode(index);

        type = new UserListBox(typeStrings, true);
        statusBox = new UserListBox(statuses, true);
        manufacturer = new UserListBox(manufacturers, true);

        VerticalPanel vp = new VerticalPanel();

        Grid grid = makeGrid();
        vp.add(grid);

        Button addButton = new Button("Add", new ClickHandler()
        {
            public void onClick(ClickEvent event)
            {
                add();
            }
        });
        vp.add(addButton);

        Button okButton = new Button("Cancel", new ClickHandler()
        {
            public void onClick(ClickEvent event)
            {
                hide();
            }
        });
        vp.add(okButton);

        add(vp);

        center();
        show();

    }

    void add()
    {
        AsyncCallback<String> callback = new AsyncCallback<String>()
        {
            public void onFailure(Throwable caught)
            {
                new ErrorBox(caught);
            }

            public void onSuccess(String contents)
            {
                if (contents != null)
                {
                    new ErrorBox("Created library " + contents);
                }
                hide();
                parent.update();
                ExperimentUnitTable.selfUpdate();
            }
        };

        try
        {
            LibraryContents lc = new LibraryContents();

            lc.index = index.getText();

            lc.manufacturer = manufacturer
                    .getSelectedNonDefaultValue("Please select a manufacturer.");

            lc.type = type.getSelectedNonDefaultValue("Please select a type.");

            lc.bases_requested = bases_requested
                    .getLongFromField("Please enter valid number for bases requested");
            lc.bases_covered = bases_covered
                    .getLongFromField("Please enter valid number for bases covered");
            lc.run_1 = run_1
                    .getIntegerFromField("Please enter valid number for run 1");
            lc.run_2 = run_2
                    .getIntegerFromField("Please enter valid number for run 2");
            lc.concentration = concentration
                    .getDoubleFromField("Please enter valid number for concentration");
            lc.status = statusBox
                    .getSelectedNonDefaultValue("Please select a status.");
            lc.notes = notes.getText();
            lc.cistrack_id = bionimbus_id;

            //new ErrorBox("Calling with " + bionimbus_id + " , " + library_name);
            rpc.addToLibrary(contents != null ? contents.name : null, lc,
                    callback);
        }
        catch (ValidationException le)
        {
            new ErrorBox(le.message);
            return;
        }

    }
}
