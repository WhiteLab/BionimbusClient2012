package org.lac.bionimbus.client;

import java.util.HashMap;
import java.util.Vector;

import org.lac.bionimbus.shared.SQLListBoxQuery;
import org.lac.bionimbus.shared.Unit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

class UnitDetailRow extends Unit
{
    CheckBox checkbox = null;

    public UnitDetailRow(Unit r) throws Exception
    {
        super(r);
        checkbox = new CheckBox(r.getCistrackId());
    }

    public CheckBox getCheckBox()
    {
        return checkbox;
    }

    public void reset()
    {
        checkbox.setChecked(false);
    }

    public boolean isSelected()
    {
        return checkbox.isChecked();
    }
}

class DisplayUnits
{
    Vector<UnitDetailRow> units = new Vector<UnitDetailRow>();
    String                name  = "";

    public DisplayUnits(String name)
    {
        this.name = name;
    }

    public void addUnit(UnitDetailRow u)
    {
        units.add(u);
    }

    public String getName()
    {
        return name;
    }

    public Vector<UnitDetailRow> getAllUnits()
    {
        return units;
    }

    public void resetAll()
    {
        for (UnitDetailRow u : units)
        {
            u.reset();
        }
    }

    public Vector<UnitDetailRow> getSelectedUnits()
    {
        Vector<UnitDetailRow> sel = new Vector<UnitDetailRow>();
        for (UnitDetailRow u : units)
        {
            if (u.isSelected())
                sel.add(u);
        }

        return sel;
    }
}

class UnitGridEx extends RowGrid<UnitDetailRow>
{
    Vector<String> titles()
    {
        Vector<String> unitHeader = new Vector<String>();

        unitHeader.add("Unit");
        // unitHeader.add(" ");
        unitHeader.add("Bionimbus ID");
        // unitHeader.add("Name");
        // unitHeader.add("Project");
        unitHeader.add("Species");
        unitHeader.add("Antibody");
        unitHeader.add("Sample");
        // unitHeader.add("Description");
        unitHeader.add("Protein");
        unitHeader.add("Type");
        unitHeader.add("Catalog");
        unitHeader.add("Associated Experiments");

        return unitHeader;
    }

    void renderRow(int vizRow, int contentRow)
    {
        UnitDetailRow res = content.elementAt(contentRow);

        final int id = res.getId();
        final String cistrackID = res.getCistrackId();
        Button downloadButton = new Button("View Unit ", new ClickHandler()
        {
            public void onClick(ClickEvent event)
            {
                new UnitDetail(id, cistrackID);
            }
        });

        int col = 0;

        setWidget(vizRow, col++, downloadButton);
        setWidget(vizRow, col++, res.getCheckBox());
        // setText(vizRow, col++, res.getCistrackId());
        // setText( vizRow , col++, res.getName());
        // setText( vizRow , col++, res.getProject());
        setText(vizRow, col++, res.getSpecies());
        setText(vizRow, col++, res.getAntiBody());
        setText(vizRow, col++, res.getSample());
        // setText( vizRow , col++, res.getDescription());
        setText(vizRow, col++, res.getProtein());
        setText(vizRow, col++, res.getType());
        setText(vizRow, col++, res.getCatalog());
        setText(vizRow, col++, res.getAssociatedExperiments());
    }

    boolean filterRow(UnitDetailRow row)
    {
        return match(row.getCistrackId())
                ||
                // match( row.getName() ) ||
                // match( row.getProject() ) ||
                match(row.getSpecies()) || match(row.getAntiBody())
                || match(row.getSample())
                ||
                // match( row.getDescription() ) ||
                match(row.getProtein()) || match(row.getType())
                || match(row.getCatalog());
    }

}

class ExperimentCreator extends VerticalPanelWithHeader
{

    private final ClientInterfaceAsync    rpc               = GWT.create(ClientInterface.class);
    RadioButtonGroup                      pubOrPriv         = null;
    UserStringListBox                     platformSelect    = null;
    SQLListBox                            projectSelect     = null;

    UserTextBox                           experimentName    = new UserTextBox();

    VerticalPanel                         sp;

    Grid                                  gridMain          = new Grid(8, 2);

    String                                debugMsg          = "";

    TabPanel                              tabPane           = new TabPanel();

    static final String[]                 platforms         = new String[] {
            "Affymetrix", "Agilent", "Sequencing"          };

    private HashMap<String, UnitGridEx>   panes             = new HashMap<String, UnitGridEx>();
    private HashMap<String, DisplayUnits> displayUnits      = new HashMap<String, DisplayUnits>();
    Vector<UnitDetailRow>                 units             = null;

    String                                validationMessage = "";
    Vector<InputValidation>               inputValidations  = new Vector<InputValidation>();

    public boolean validate()
    {
        String validationMessage = "";
        boolean v = true;
        for (InputValidation i : inputValidations)
        {
            if (!i.isValid())
            {
                // ((Widget)i).removeStyleName("Valid");
                // ((Widget)i).addStyleName("Invalid");
                v = false;
                validationMessage += i.getFieldLabel() + " \n";
            }
            else
            {
                // ((Widget)i).addStyleName("Valid");
                // ((Widget)i).removeStyleName("Invalid");
            }

        }
        if (!v)
        {
            new ErrorBox("Please fill the following fields: "
                    + validationMessage);
            return false;
        }

        String chosenPlatform = platformSelect.getSelectedText();

        if (displayUnits.get(chosenPlatform + "-Unit").getSelectedUnits()
                .size() == 0)
        {
            tabPane.selectTab(0);
            new ErrorBox("Please select one or more experiment units to add");
            return false;
        }

        if (chosenPlatform.equals("Affymetrix")
                && (displayUnits.get("Affymetrix-Control").getSelectedUnits()
                        .size() == 0))
        {
            tabPane.selectTab(1);
            new ErrorBox("Please select one or more control units to add");
            return false;
        }

        return true;
    }

    public ExperimentCreator()
    {
        super("Experiment Creator");
        try
        {
            add(gridMain);

            addMainFields();
            fetchUnits();

            RootPanel.get().add(this);

        }
        catch (Exception e)
        {
            new ErrorBox("exception while creating dialog box" + e);
        }
    }

    public DisplayUnits createDisplayUnits(String plat, String name,
            boolean isControl) throws Exception
    {
        HashMap<String, String> CONTROL_ANTIBODY = new HashMap<String, String>();

        CONTROL_ANTIBODY.put("", "");
        CONTROL_ANTIBODY.put("No-Antibody", "No-Antibody");
        CONTROL_ANTIBODY.put("MOCK", "MOCK");
        CONTROL_ANTIBODY.put("INPUT", "INPUT");
        CONTROL_ANTIBODY.put("Mock", "Mock");
        CONTROL_ANTIBODY.put("NONE", "NONE");
        String chosenPlatform = plat;
        DisplayUnits d = new DisplayUnits(name);

        for (UnitDetailRow r : units)
        {
            boolean select = chosenPlatform.equals(r.getType());
            if (select && chosenPlatform.equals("Affymetrix"))
                select = !isControl
                        ^ CONTROL_ANTIBODY.containsKey(r.getAntiBody());

            select = select
                    && !(r.getSample().equals("") || r.getAntiBody().equals(""));

            if (select)
            {
                d.addUnit(r);
            }
        }
        return d;
    }

    private void createGrid(String plat, String name, boolean isControl)
            throws Exception
    {
        DisplayUnits d = createDisplayUnits(plat, name, isControl);
        displayUnits.put(name, d);
        UnitGridEx u = new UnitGridEx();
        u.setContent(d.getAllUnits());
        panes.put(name, u);
    }

    public boolean createGrids() throws Exception
    {
        try
        {
            panes.clear();
            displayUnits.clear();

            for (int i = 0; i < platforms.length; i++)
            {
                createGrid(platforms[i], platforms[i] + "-Unit", false);

            }
            createGrid("Affymetrix", "Affymetrix-Control", true);
        }
        catch (Exception e)
        {
            throw new Exception("addpanes" + e.toString());
        }
        return true;
    }

    public void refreshTabPane()
    {
        remove(tabPane);
        tabPane.clear();
        String chosenPlatform = platformSelect.getSelectedText();
        if (chosenPlatform.equals(""))
            return;

        tabPane.add(panes.get(chosenPlatform + "-Unit"), "Experiment Units");
        if (chosenPlatform.equals("Affymetrix"))
        {
            tabPane.add(panes.get("Affymetrix-Control"), "Control Units");
        }
        tabPane.selectTab(0);
        add(tabPane);
    }

    private void addMainFields() throws Exception
    {
        try
        {
            projectSelect = new SQLListBox(
                    SQLListBoxQuery.items.SQLLIST_PROJECT);

            platformSelect = new UserStringListBox(platforms);
            pubOrPriv = new RadioButtonGroup("pubpriv", "Public", "Private");
            pubOrPriv.setCheckedText("Private");

            gridMain.setText(0, 0, "Project");
            gridMain.setWidget(0, 1, projectSelect);
            projectSelect.setFieldLabel("Project");

            gridMain.setText(1, 0, "Experiment Name");
            gridMain.setWidget(1, 1, experimentName);
            experimentName.setFieldLabel("Experiment Name");

            gridMain.setText(2, 0, "Is the Data public or Private?");
            gridMain.setWidget(2, 1, pubOrPriv);
            pubOrPriv.setFieldLabel("Public/Private");

            platformSelect.setSelectedText("Affymetrix");
            gridMain.setText(3, 0, "Select Platform");
            gridMain.setWidget(3, 1, platformSelect);
            platformSelect.setFieldLabel("Platform");

            platformSelect.addChangeHandler(new ChangeHandler()
            {
                public void onChange(ChangeEvent ce)
                {
                    refreshTabPane();
                }
            });

            gridMain.setWidget(6, 0, new Button("Create Experiment",
                    new ClickHandler()
                    {
                        public void onClick(ClickEvent ev)
                        {
                            if (validate())
                            {
                                insertNewExperiment();
                            }
                        }
                    }));

            inputValidations.add(projectSelect);
            inputValidations.add(experimentName);
            inputValidations.add(pubOrPriv);
            inputValidations.add(platformSelect);

        }
        catch (Exception e)
        {
            throw new Exception("add main fields: " + e);
        }

    }

    private void fetchUnits()
    {
        AsyncCallback<Vector<Unit>> callback = new AsyncCallback<Vector<Unit>>()
        {
            public void onFailure(Throwable caught)
            {
                new ErrorBox("RPC Failed fetchUnits " + caught);
            }

            public void onSuccess(Vector<Unit> callbackResult)
            {
                try
                {
                    units = new Vector<UnitDetailRow>();
                    for (Unit r : callbackResult)
                    {
                        units.add(new UnitDetailRow(r));
                    }
                    createGrids();
                    refreshTabPane();
                }
                catch (Exception e)
                {
                    new ErrorBox("RPC Failed (fetchUnits) " + units.size()
                            + e.toString());
                }
            }
        };

        try
        {
            rpc.fetchDesignAndUnits(callback);
        }
        catch (Exception e)
        {
            new ErrorBox("RPC Failed (fetchUnits) " + e);
        }
    }

    private void insertNewExperiment()
    {
        AsyncCallback<Integer> callback = new AsyncCallback<Integer>()
        {
            public void onFailure(Throwable caught)
            {
                new ErrorBox("RPC Failed insertNewExperiment " + caught);
            }

            public void onSuccess(Integer callbackResult)
            {
                try
                {
                    final int eid = callbackResult.intValue();
                    new ErrorBox("Success", "Experiment Created successfully",
                            new Button("OK", new ClickHandler()
                            {
                                public void onClick(ClickEvent c)
                                {
                                    ; // do nothing
                                }
                            }), new Button("View Experiment",
                                    new ClickHandler()
                                    {
                                        public void onClick(ClickEvent c)
                                        {
                                            new ExperimentDetail(eid);
                                        }
                                    }));

                    fetchUnits();
                }
                catch (Exception e)
                {
                    new ErrorBox("RPC Failed (insertNewExperiment) "
                            + units.size() + e.toString());
                }
            }
        };

        try
        {

            String chosenPlatform = platformSelect.getSelectedText();

            Vector<String> ve = new Vector<String>(), vc = new Vector<String>();
            for (UnitDetailRow u : displayUnits.get(chosenPlatform + "-Unit")
                    .getSelectedUnits())
            {
                ve.add(u.getId() + "");
            }

            if (chosenPlatform.equals("Affymetrix")
                    && (displayUnits.get("Affymetrix-Control")
                            .getSelectedUnits().size() > 0))
            {
                for (UnitDetailRow u : displayUnits.get("Affymetrix-Control")
                        .getSelectedUnits())
                {
                    vc.add(u.getId() + "");
                }
            }

            rpc.insertNewExperiment("-- SOME USER --", pubOrPriv
                    .getCheckedText().equals("Public"), experimentName
                    .getText(), projectSelect.getSelectedValue(), ve, vc,
                    callback);
        }
        catch (Exception e)
        {
            new ErrorBox("RPC Failed (insertNewExperiment) " + e);
        }

    }
}
