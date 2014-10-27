package org.lac.bionimbus.client;

import java.util.ArrayList;
import java.util.Vector;

import org.lac.bionimbus.shared.KeyGenData;
import org.lac.bionimbus.shared.RecycleKeyResult;
import org.lac.bionimbus.shared.SQLListBoxQuery;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class KeyGen extends VerticalPanel implements UpdateSamples
{
    class KeyRow
    {

        class KeyGenMultiplexingControls extends DialogBox
        {
            private VerticalPanel    illuminaPanel;
            private VerticalPanel    mainPanel;
            private boolean          pending                         = false;
            private Button           close;
            private String           state;
            private ListBox          illuminaCodes;
            private TextBox          barcode;
            private RadioButtonGroup rbg                             = null;
            private RadioButtonGroup rbg1                            = null;
            final static String      NO_SEQUENCING_LIB_USED          = "No index tag used";
            final static String      SEQUENCING_NOT_PREPARED_BY_USER = "Sequencing library not prepared by user";
            final static String      SEQUENCING_LIBRARY_USED         = "Sequencing library used";
            final static String      STANDARD_LIBRARY_MULTIPLEXED    = "Library multiplexed using standard code";
            final static String      CUSTOM_MULTIPLEXED_LIBRARY      = "Library multiplexed using custom code";

            KeyGenMultiplexingControls()
            {
                super(false);
                state = NO_SEQUENCING_LIB_USED;
                illuminaCodes = new ListBox();
                barcode = new TextBox();
                illuminaCodes.addItem(KeyGenData.defaultBarcode);
                illuminaCodes.addItem("Custom");

                //illuminaCodes.addItem(indexes[i].trim());
                Utils.fillBarcode(illuminaCodes);

                illuminaPanel = new VerticalPanel();
                mainPanel = new VerticalPanel();
                addContent();
                close = new Button("Close", new ClickHandler()
                {
                    public void onClick(ClickEvent e)
                    {
                        hide();
                        String temp = " ";
                        if (state.equals(NO_SEQUENCING_LIB_USED))
                            library.setCheckedText("No");
                        if (state.equals(STANDARD_LIBRARY_MULTIPLEXED))
                        {
                            temp += illuminaCodes.getItemText(illuminaCodes
                                    .getSelectedIndex());
                            if (pending)
                                temp += " pending";
                        }
                        else if (state.equals(CUSTOM_MULTIPLEXED_LIBRARY))
                        {
                            temp += barcode.getText();
                            if (pending)
                                temp += " pending";
                        }
                        new ErrorBox("Multiplexing information", state + temp,
                                new Button("Ok"));
                        g.getCellFormatter().setVerticalAlignment(row, 6,
                                HasVerticalAlignment.ALIGN_BOTTOM);
                        g.setText(row, 6, getBarcode());
                    }
                });
                addToPanel(illuminaPanel);
                addToPanel(close);
                add(mainPanel);
            }

            public int checkFields()
            {
                if (state.equals(CUSTOM_MULTIPLEXED_LIBRARY))
                {
                    if (barcode.getText().equals(""))
                    {
                        new ErrorBox("Please enter a custom barcode");
                        //TODO:Add styling here
                        return -1;
                    }
                }
                if (state.equals(STANDARD_LIBRARY_MULTIPLEXED))
                {
                    if (illuminaCodes.getItemText(
                            illuminaCodes.getSelectedIndex()).equals(
                            KeyGenData.defaultBarcode))
                    {
                        new ErrorBox("Please select the index sequence name");
                        //TODO:Add Styling here
                        return -1;
                    }
                }

                return 0;
            }

            public void setState(String text)
            {
                this.state = text;
            }

            public void addToPanel(Widget w)
            {
                mainPanel.add(w);
            }

            public String getBarcode()
            {
                String tempBarcodeString = "";
                if (state.equals(STANDARD_LIBRARY_MULTIPLEXED))
                {
                    tempBarcodeString = illuminaCodes.getItemText(illuminaCodes
                            .getSelectedIndex());
                    if (pending)
                        tempBarcodeString += " pending";
                }
                else if (state.equals(CUSTOM_MULTIPLEXED_LIBRARY))
                {
                    tempBarcodeString = barcode.getText();
                    if (pending)
                        tempBarcodeString += " pending";
                }
                //                else if (state.equals(STANDARD_LIBRARY_MULTIPLEXED)
                //                        || state.equals(CUSTOM_MULTIPLEXED_LIBRARY)
                //                        && library.getCheckedText().equals("No"))
                else
                    tempBarcodeString = state;

                return tempBarcodeString;
            }

            public void clearRadioButtons()
            {
                rbg.setCheckedText("No");
                rbg1.setCheckedText("No");
            }

            public void addContent()
            {
                rbg = new RadioButtonGroup("ControlsforSolexa0", "Yes", "No");
                rbg1 = new RadioButtonGroup("ControlsforSolexa1", "Yes", "No");

                final HorizontalPanel rbgPanel = new HorizontalPanel();
                Label rbgLabel = new Label(
                        "Sequencing Library preparation performed by User?");
                rbgPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
                rbgPanel.setSpacing(10);
                rbgPanel.add(rbgLabel);
                rbgPanel.add(rbg);

                final HorizontalPanel rbgPanel1 = new HorizontalPanel();
                Label rbgLabel1 = new Label(
                        "Will library be multiplexed (barcoded)?");
                rbgPanel1
                        .setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
                rbgPanel1.setSpacing(10);
                rbgPanel1.add(rbgLabel1);
                rbgPanel1.add(rbg1);

                final HorizontalPanel rbgPanel2 = new HorizontalPanel();
                Label rbgLabel2 = new Label("Please select the barcode name ");
                rbgPanel2
                        .setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
                rbgPanel2.setSpacing(10);
                rbgPanel2.add(rbgLabel2);
                rbgPanel2.add(illuminaCodes);

                final HorizontalPanel barcodePanel = new HorizontalPanel();
                Label rbgLabel3 = new Label(
                        "If custom, please enter the barcode name ");
                barcodePanel
                        .setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
                barcodePanel.setSpacing(10);
                barcodePanel.add(rbgLabel3);
                barcodePanel.add(barcode);

                illuminaPanel.add(rbgPanel);

                //TODO:CHECK HANDLERS FOR CONSISTENCY

                Vector<RadioButton> rbgVector = rbg.getButtons();
                for (final RadioButton b : rbgVector)
                {
                    b.addClickHandler(new ClickHandler()
                    {
                        public void onClick(ClickEvent e)
                        {
                            if (b.getText().equals("Yes"))
                            {
                                illuminaPanel.add(rbgPanel1);
                                state = SEQUENCING_LIBRARY_USED;
                            }
                            else if (b.getText().equals("No"))
                            {
                                illuminaPanel.add(rbgPanel1);
                                state = SEQUENCING_NOT_PREPARED_BY_USER;
                                pending = true;
                            }
                        }
                    });
                }

                rbgVector = rbg1.getButtons();
                for (final RadioButton b : rbgVector)
                {
                    b.addClickHandler(new ClickHandler()
                    {
                        public void onClick(ClickEvent e)
                        {
                            if (b.getText().equals("Yes"))
                            {
                                illuminaPanel.add(rbgPanel2);
                                verifyListBox = true;
                                state = STANDARD_LIBRARY_MULTIPLEXED;
                            }
                            else if (b.getText().equals("No"))
                            {
                                illuminaPanel.clear();
                                illuminaPanel.add(rbgPanel);
                                illuminaPanel.add(rbgPanel1);
                                illuminaCodes.setSelectedIndex(0);
                                verifyListBox = false;
                                if (pending
                                        && (state
                                                .equals(STANDARD_LIBRARY_MULTIPLEXED) || state
                                                .equals(CUSTOM_MULTIPLEXED_LIBRARY)))
                                    state = NO_SEQUENCING_LIB_USED;
                                if (!pending
                                        && (state
                                                .equals(STANDARD_LIBRARY_MULTIPLEXED) || state
                                                .equals(CUSTOM_MULTIPLEXED_LIBRARY)))
                                    state = SEQUENCING_LIBRARY_USED;
                            }
                        }
                    });
                }//End for

                illuminaCodes.addChangeHandler(new ChangeHandler()
                {
                    public void onChange(ChangeEvent event)
                    {
                        if (illuminaCodes.getItemText(
                                illuminaCodes.getSelectedIndex()).equals(
                                "Custom"))
                        {
                            illuminaPanel.add(barcodePanel);
                            customBarcode = true;
                            state = CUSTOM_MULTIPLEXED_LIBRARY;
                        }
                        else
                        {
                            customBarcode = false;
                            illuminaPanel.remove(barcodePanel);
                            state = STANDARD_LIBRARY_MULTIPLEXED;
                        }
                    }
                });

            }

        }

        private KeyGenMultiplexingControls kgmc;
        private TextBox                    sampleName;
        private SQLListBox                 bioMaterial;
        private TextBox                    description;
        private TextBox                    antiBody;
        private ListBox                    experimentType;
        private RadioButtonGroup           library;
        public boolean                     isValid = true;
        private int                        row     = -1;
        // private String keyGenerated = null;

        private String[]                   types   = { "ChIP", "rna-seq",
                                                           "dna-seq",
                                                           "gene expression",
                                                           "CGH", "Other" };

        KeyRow()
        {
            sampleName = new TextBox();
            bioMaterial = new SQLListBox(SQLListBoxQuery.items.SQLLIST_SAMPLE);
            antiBody = new TextBox();
            description = new TextBox();
            experimentType = new ListBox();
            kgmc = new KeyGenMultiplexingControls();

            library = new RadioButtonGroup("KeyRowGroup" + this.hashCode(),
                    "Yes", "No");

            Vector<RadioButton> rbgVector = library.getButtons();
            for (final RadioButton b : rbgVector)
            {
                b.addClickHandler(new ClickHandler()
                {
                    public void onClick(ClickEvent e)
                    {
                        if (b.getText().equals("Yes"))
                        {
                            kgmc.center();
                            kgmc.setText("Multiplexing controls for Row " + row
                                    + "");
                        }
                        else if (b.getText().equals("No"))
                        {
                            kgmc.hide();
                            kgmc.setState(KeyGenMultiplexingControls.NO_SEQUENCING_LIB_USED);
                            kgmc.clearRadioButtons();
                            g.getCellFormatter().setVerticalAlignment(row, 6,
                                    HasVerticalAlignment.ALIGN_BOTTOM);
                            g.setText(
                                    row,
                                    6,
                                    KeyGenMultiplexingControls.NO_SEQUENCING_LIB_USED);
                        }
                    }
                });
            }
            //	    library.addValueChangeHandler( new ValueChangeHandler<Boolean>()
            //	    {
            //		public void onValueChange( ValueChangeEvent<Boolean> event )
            //		{
            //		    if( event.getValue() ){
            //			kgmc.center();
            //		    }
            //		    else if( !event.getValue() )
            //			kgmc.hide();
            //		}
            //	    });

            for (int i = 0; i < types.length; i++)
            {
                experimentType.addItem(types[i]);
            }
        }

        public String getBarcode()
        {
            return kgmc.getBarcode();
        }

        public void setExperimentType(String param)
        {
            boolean other = true;
            int index = -1;

            for (int i = 0; i < experimentType.getItemCount(); i++)
            {
                if (param.contains(experimentType.getValue(i))
                        || experimentType.getValue(i).contains(param))
                {
                    experimentType.setSelectedIndex(i);
                    other = false;
                    break;
                }
                else if (experimentType.getValue(i).equals("Other"))
                    index = i;
            }

            if (other && index != -1)
                experimentType.setSelectedIndex(index);
        }

        public boolean isEmpty()
        {
            if (sampleName.getText().length() == 0
                    && description.getText().length() == 0)
            {
                if (bioMaterial.getSelectedIndex() == 0
                        && antiBody.getText().trim().equals(""))
                    return true;
            }
            return false;
        }

        public void setDescription(String n)
        {
            description.setText(n);
        }

        public void setSampleName(String n)
        {
            sampleName.setText(n);
        }

        public void setAntiBody(String param)
        {
            antiBody.setText(param);
        }

        public void setBioMaterial(String param)
        {
            bioMaterial.setSelectedText(param);
        }

        public void refreshSampleList(Integer index)
        {
            bioMaterial.refreshSaveSelected(index);
        }

        public String getDescription()
        {
            return description.getText();
        }

        public String getSampleName()
        {
            return sampleName.getText();
        }

        public String getSampleType()
        {
            return bioMaterial.getSelectedText();
        }

        public int getBioMaterial()
        {
            return bioMaterial.getSelectedValue();
        }

        public String getAgent()
        {
            return antiBody.getSelectedText();
        }

        public String getExperimentType()
        {
            return experimentType
                    .getItemText(experimentType.getSelectedIndex());
        }

        public void addKeyRow(int row)
        {
            this.row = row;
            g.setWidget(row, 0, sampleName);
            g.setWidget(row, 1, bioMaterial);
            g.setWidget(row, 4, description);
            g.setWidget(row, 2, antiBody);
            g.setWidget(row, 3, experimentType);
            //	    g.setWidget(row + 1, 0, illuminaPanel );
            if (platform.getItemText(platform.getSelectedIndex()).toLowerCase()
                    .contains("solexa"))
            {
                showLibraryCheckBox(row);
            }
        }

        public void showControls()
        {
            kgmc.center();
        }

        public void showLibraryCheckBox(int row)
        {
            g.setWidget(row, 5, library);
        }

        public void hideLibraryCheckBox(int row)
        {
            g.clearCell(row, 5);
            g.clearCell(row, 6);
        }

        //        private void updateGrid(Grid displayGrid, String[] myStrings,
        //                int gridRow)
        /*
         * private void updateGrid(Grid displayGrid, String[] myStrings, int
         * gridRow) {
         * 
         * for (int i = 0; i < myStrings.length; i++) {
         * displayGrid.setWidget(gridRow, i, new Label(myStrings[i])); } }
         */

        public int checkFields()
        {
            if (platform.getItemText(platform.getSelectedIndex()).toLowerCase()
                    .contains("solexa"))
            {
                if (kgmc.checkFields() < 0)
                    return -4;

                if (!library.isValid())
                {
                    library.removeStyleName("Valid");
                    library.addStyleName("Invalid");
                    return -5;
                }
                else
                {
                    library.addStyleName("Valid");
                    library.removeStyleName("Invalid");
                }
            }

            if (sampleName.getText().equals(""))
            {
                sampleName.removeStyleName("Valid");
                sampleName.addStyleName("Invalid");
                sampleName.setFocus(true);
                return -1;
            }
            else
            {
                sampleName.removeStyleName("Invalid");
                sampleName.addStyleName("Valid");
            }

            if (!bioMaterial.isValid())
            {
                bioMaterial.removeStyleName("Valid");
                bioMaterial.addStyleName("Invalid");
                bioMaterial.setFocus(true);
                return -2;
            }
            else
            {
                bioMaterial.removeStyleName("Invalid");
                bioMaterial.addStyleName("Valid");
            }

            return 1;

        }

    }// End KeyRow Def

    class RecycleGeneratedKey extends DialogBox
    {

        private VerticalPanel panel;
        private TextBox       tb;

        // The separator between year and number in the cistrack key generation
        // process
        //private final String  tokenizer = "-";

        RecycleGeneratedKey()
        {
            super(false);
            // dash.setStyleName( "bionimbus-Label" );
            setText("Re-use Bionumbus Keys");
            HorizontalPanel hp = new HorizontalPanel();
            panel = new VerticalPanel();
            panel.add(new Label(" Please enter the bionimbus key to re-use "));
            panel.add(hp);
            hp.add(tb = new TextBox());
            /*tb.addKeyUpHandler(new KeyUpHandler()
            {
                public void onKeyUp(KeyUpEvent event)
                {
                    if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
                    {
                        try
                        {
                            performReuse(tb.getText());
                        }
                        catch (NumberFormatException noe)
                        {
                            new ErrorBox(
                                    "The Bionimbus key entered is invalid. Please check the key entered and try again.");
                        }
                    }
                }
            }); 
            */
            // hp.add( dash );
            // hp.add( tb1 = new TextBox() );
            // hp.setSpacing( 5 );
            tb.setFocus(true);
            tb.setVisibleLength(10);
            // tb1.setVisibleLength( 4 );
            HorizontalPanel hp1 = new HorizontalPanel();
            hp1.setSpacing(5);
            panel.add(hp1);
            hp1.add(new Button("Ok", new ClickHandler()
            {
                public void onClick(ClickEvent e)
                {
                    //try
                    //{
                    // String key = Integer.parseInt( tb.getText() ) + "-" +
                    // Integer.parseInt( tb1.getText() );
                    performReuse(tb.getText());
                    //}
                    //catch (NumberFormatException noe)
                    //{
                    //new ErrorBox(
                    //"The Bionimbus key entered is invalid. Please check the key entered and try again.");
                    //}
                }
            }));

            hp1.add(new Button("Cancel", new ClickHandler()
            {
                public void onClick(ClickEvent e)
                {
                    hide();
                }
            }));
            add(panel);
            center();
        }

        private void performReuse(String key)
        {

            /*
            String[] pieces = key.split(tokenizer);
            key = "";

            try
            {
                for (int i = 0; i < pieces.length; i++)
                {
                    key += Integer.parseInt(pieces[i]);
                    if (i != pieces.length - 1)
                        key += tokenizer;
                }
            }
            catch (NumberFormatException noe)
            {
            }
            */

            AsyncCallback<RecycleKeyResult> callback = new AsyncCallback<RecycleKeyResult>()
            {
                public void onFailure(Throwable caught)
                {
                    // TODO : do something when rpc fails
                    new ErrorBox(caught.getMessage());
                }

                public void onSuccess(RecycleKeyResult result)
                {
                    hide();
                    update(result);
                }
            };

            rpc.recycleKey(key, callback);
        }

        private void update(RecycleKeyResult result)
        {
            setPlatform(result.getPlatform());
            setFacility(result.getFacility());
            setProject(result.getProject());
            for (int i = 0; i < krList.size(); i++)
            {
                KeyRow innerkr = krList.get(i);
                if (!innerkr.isEmpty() || i == (krList.size() - 1))
                {
                    innerkr.setSampleName(result.getSample());
                    innerkr.setBioMaterial(result.getBioMaterial());
                    innerkr.setAntiBody(result.getAntiBody());
                    innerkr.setExperimentType(result.getExperimentType());
                    innerkr.setDescription(result.getDescription());
                    break;
                }
            }
        }

    }// End class def

    // private String email = "";
    private final ClientInterfaceAsync rpc            = GWT.create(ClientInterface.class);
    boolean                            customBarcode  = false;
    boolean                            verifyListBox  = false;
    private TextBox                    requestId;
    private SQLListBox                 platform;
    private SQLListBox                 project;
    private SQLListBox                 facility;
    //    private TextBox                    barcode;
    private Button                     makeKeysButton = new Button(
                                                              "Make Bionimbus keys");
    private Grid                       g;
    private int                        gridRowCount   = 0;
    private int                        initialSize    = 2;

    private Button                     addBioMaterial = null;
    private Button                     addAntiBody    = null;
    private ArrayList<KeyRow>          krList         = new ArrayList<KeyRow>();
    private KeyRow                     kr1            = null;

    int getProjectID()
    {
        int id = project.getSelectedValue();
        if (id == 0)
        {
            new ErrorBox("You must select a project first");
            throw new RuntimeException();
        }
        return id;
    }

    public KeyGen()
    //    public KeyGen(String username)
    {
        //        this.username = username;
        requestId = new TextBox();
        platform = new SQLListBox(SQLListBoxQuery.items.PLATFORM);

        project = new SQLListBox(SQLListBoxQuery.items.SQLLIST_PROJECT);
        facility = new SQLListBox(SQLListBoxQuery.items.SQLLIST_FACILITY);

        /*
         * for (int i = 0; i < platforms.length; i++) {
         * platform.addItem(platforms[i]); }
         */

        //        barcode = new TextBox();

        draw();
    }

    public void setPlatform(String pl)
    {
        for (int i = 0; i < platform.getItemCount(); i++)
        {
            if (pl.contains(platform.getValue(i))
                    || platform.getValue(i).contains(pl))
                platform.setSelectedIndex(i);
        }
    }

    public void setRequestId(String n)
    {
        requestId.setText(n);
    }

    public void setFacility(String param)
    {
        try
        {
            if (param.length() > 0)
                facility.setSelectedIndex(Integer.parseInt(param));
        }
        catch (Exception noe)
        {
            //TODO: handle this
        }
    }

    public void setProject(String param)
    {
        project.setSelectedText(param);
    }

    public void refreshSampleRows(int val)
    {
        int count = krList.size();

        for (int i = 0; i < count; i++)
        {
            krList.get(i).refreshSampleList(i == (count - 1) ? val : null);
        }
    }

    boolean needsProject(String desc)
    {
        int pid = getProjectID();

        if (pid == -1)
        {
            new ErrorBox("Please choose a project for this " + desc
                    + " to be in");
            return true;
        }
        return false;
    }

    private void draw()
    {
        final String[] gridHeaders = { "Sample Name", "Biological Material",
                "Antibody / Treatment", "Experiment Type", "Description",
                "Library", "Barcode" };/*, "Sequencing Library preparation performed by User?",
                                       "Will library be multiplexed (barcoded)?" };*/

        g = new BnGrid(initialSize, gridHeaders.length);

        g.setText(gridRowCount, 0, "Sample Name");

        addBioMaterial = new Button("Add");

        class MyHandler implements ClickHandler
        {
            private KeyGen kg;

            public MyHandler(KeyGen kg)
            {
                this.kg = kg;
            }

            public void onClick(ClickEvent e)
            {
                if (needsProject("sample"))
                    return;

                AddSample as = new AddSample(kg, getProjectID());
                as.show();
            }
        }

        MyHandler mh = new MyHandler(this);

        addBioMaterial.addClickHandler(mh);

        Grid addButtonGrid = new Grid(1, 2);

        addButtonGrid.setWidget(0, 0, new Label("Biological Material"));
        addButtonGrid.setWidget(0, 1, addBioMaterial);

        HorizontalPanel hp = new HorizontalPanel();
        hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        hp.add(addButtonGrid);

        g.setWidget(gridRowCount, 1, hp);

        hp = new HorizontalPanel();
        hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

        addButtonGrid = new Grid(1, 2);
        addButtonGrid.setWidget(0, 0, new Label("Antibody/Treatment"));

        final KeyGen k = this;

        addButtonGrid.setWidget(0, 1, addAntiBody);
        hp.add(addButtonGrid);

        g.setWidget(gridRowCount, 2, hp);

        g.setText(gridRowCount, 3, "Experiment Type");
        g.setText(gridRowCount++, 4, "Description");

        Button addRowButton = new Button("Add Row", new ClickHandler()
        {
            public void onClick(ClickEvent event)
            {
                kr1 = new KeyRow(); //

                krList.add(kr1);
                if (gridRowCount + 1 > initialSize - 1)
                {
                    g.resize(gridRowCount + 1, gridHeaders.length);
                }
                kr1.addKeyRow(gridRowCount);
                gridRowCount++;
            }
        });

        if (gridRowCount + 1 <= initialSize - 1)
        {
            KeyRow kr = new KeyRow();
            krList.add(kr);
            kr.addKeyRow(gridRowCount);
            gridRowCount++;
        }
        else
        {
            g.resize(gridRowCount + 1, gridHeaders.length);
            KeyRow kr = new KeyRow();
            krList.add(kr);
            kr.addKeyRow(gridRowCount);
            gridRowCount++;
        }

        final VerticalPanel newPanel = new VerticalPanel();
        Grid g1 = new Grid(4, 3);

        g1.setWidget(0, 0, new Label("Sequencing core request ID  "));
        g1.setWidget(0, 1, requestId);
        g1.setWidget(0, 2, new Button("Re-use Bionimbus Key",
                new ClickHandler()
                {
                    public void onClick(ClickEvent e)
                    {
                        new RecycleGeneratedKey();
                    }
                }));

        g1.setWidget(1, 0, new Label("Facility  "));
        g1.setWidget(1, 1, facility);
        g1.setWidget(2, 0, new Label("Project  "));
        g1.setWidget(2, 1, project);
        g1.setWidget(3, 0, new Label("Platform  "));
        g1.setWidget(3, 1, platform);
        add(g1);
        add(newPanel);
        add(g);

        Grid g2 = new Grid(2, 1);
        g2.setWidget(0, 0, addRowButton);

        makeKeysButton.addClickHandler(new ClickHandler()
        {
            public void onClick(ClickEvent e)
            {
                generateBionimbusKey();
            }
        });

        g2.setWidget(1, 0, makeKeysButton);

        platform.addChangeHandler(new ChangeHandler()
        {
            int illuminaIndex = -1;

            public void onChange(ChangeEvent event)
            {
                if (platform.getItemText(platform.getSelectedIndex())
                        .toLowerCase().contains("solexa"))
                {
                    //		    newPanel.add( rbgVpanel  );
                    //Cycle through key rows list
                    illuminaIndex = platform.getSelectedIndex();
                    g.setText(0, 5, "Index tag used?");
                    g.setText(0, 6, "Barcode");
                    for (int i = 0; i < krList.size(); i++)
                    {
                        KeyRow temp = (KeyRow) krList.get(i);
                        temp.showLibraryCheckBox(i + 1);
                    }
                }
                else
                {
                    //		    newPanel.remove( rbgVpanel  );
                    if (illuminaIndex != -1)
                    {
                        new ErrorBox(
                                "Confirm action",
                                "Warning! Changing platforms might result in loss of barcode information. Click ok to confirm"
                                        + " action.", new Button("Ok",
                                        new ClickHandler()
                                        {
                                            public void onClick(ClickEvent e)
                                            {
                                                g.clearCell(0, 5);
                                                g.clearCell(0, 6);
                                                for (int i = 0; i < krList
                                                        .size(); i++)
                                                {
                                                    KeyRow temp = (KeyRow) krList
                                                            .get(i);
                                                    temp.hideLibraryCheckBox(i + 1);
                                                }
                                                illuminaIndex = -1;
                                            }
                                        }), new Button("Cancel",
                                        new ClickHandler()
                                        {
                                            public void onClick(ClickEvent e)
                                            {
                                                platform.setSelectedIndex(illuminaIndex);
                                            }
                                        }));
                    }

                }
            }
        });

        Window.setTitle("Make Bionimbus Keys");
        RootPanel.get().add(this);
        add(g2);
        requestId.setFocus(true);
    }

    private int verifyFields()
    {

        if (requestId.getText().equals(""))
        {
            new ErrorBox("Please enter a sequencing request Id");
            requestId.removeStyleName("Valid");
            requestId.addStyleName("Invalid");
            requestId.setFocus(true);
            return -1;
        }
        else
        {
            requestId.removeStyleName("Invalid");
            requestId.addStyleName("Valid");
        }

        if (!facility.isValid())
        {
            new ErrorBox("Please pick a facility");
            facility.removeStyleName("Valid");
            facility.addStyleName("Invalid");
            facility.setFocus(true);
            return -1;
        }
        else
        {
            facility.removeStyleName("Invalid");
            facility.addStyleName("Valid");
        }

        if (!project.isValid())
        {
            new ErrorBox("Please pick a project");
            project.removeStyleName("Valid");
            project.addStyleName("Invalid");
            project.setFocus(true);
            return -1;
        }
        else
        {
            project.removeStyleName("Invalid");
            project.addStyleName("Valid");
        }

        //        if (customBarcode && barcode.getText().equals(""))
        //        {
        //            new ErrorBox(
        //                    "Please enter the custom barcode or select one of the standard illumina codes");
        //            barcode.removeStyleName("Valid");
        //            barcode.addStyleName("Invalid");
        //            barcode.setFocus(true);
        //            return -1;
        //        }
        //        else if (customBarcode && !barcode.getText().equals(""))
        //        {
        //            barcode.removeStyleName("Invalid");
        //            barcode.addStyleName("Valid");
        //        }

        //        if (!customBarcode
        //                && verifyListBox
        //                && illuminaCodes.getItemText(illuminaCodes.getSelectedIndex())
        //                        .equals(KeyGenData.defaultBarcode))
        //        {
        //            new ErrorBox("Please enter a sequencing request Id");
        //            illuminaCodes.removeStyleName("Valid");
        //            illuminaCodes.addStyleName("Invalid");
        //            illuminaCodes.setFocus(true);
        //            return -1;
        //        }
        //        else
        //        {
        //            illuminaCodes.removeStyleName("Invalid");
        //            illuminaCodes.addStyleName("Valid");
        //        }

        return 0;
    }

    private void generateBionimbusKey()
    {

        final Vector<KeyGenData> kgData = new Vector<KeyGenData>();

        if (verifyFields() < 0)
            return;

        for (int i = 0; i < krList.size(); i++)
        {
            int result = krList.get(i).checkFields();
            if (result < 0)
            {
                switch (result)
                {
                    case -1:
                        new ErrorBox("Enter a sample name for row " + (i + 1));
                        return;
                    case -2:
                        new ErrorBox("Select a sample type for row " + (i + 1));
                        return;
                    case -3:
                        new ErrorBox("Select a antibody type for row "
                                + (i + 1));
                    case -4:
                    case -5:
                        new ErrorBox(
                                "Required multiplexing information missing for row "
                                        + (i + 1));
                        return;
                    default:
                        System.err.println("Error in Verifying fields!");
                        return;
                }
            }
            else
            {
                KeyRow keyData = krList.get(i);

                // add row object to list
                kgData.add(new KeyGenData(keyData.getSampleName(), keyData
                        .getBioMaterial(), project.getSelectedValue(), platform
                        .getSelectedValue(), facility.getSelectedValue(),
                        krList.get(i).getDescription(), krList.get(i)
                                .getExperimentType(), requestId.getText()
                                .trim(), keyData.getBarcode().trim(), keyData
                                .getSampleType(), keyData.getAgent(), project
                                .getItemText(project.getSelectedIndex()),
                        facility.getItemText(facility.getSelectedIndex())));
            }
        }

        AsyncCallback<Vector<KeyGenData>> callback = new AsyncCallback<Vector<KeyGenData>>()
        {
            public void onFailure(Throwable caught)
            {
                // TODO : do something when rpc fails
                new ErrorBox(caught);
            }

            public void onSuccess(Vector<KeyGenData> result)
            {
                showSummary(result, kgData);
            }
        };

        try
        {
            rpc.sendKeyData(kgData, callback);
        }
        catch (Exception e)
        {
            new ErrorBox(e);
        }

    }

    private void showSummary(Vector<KeyGenData> keys, Vector<KeyGenData> kgData)
    {

        if (kgData.size() > keys.size() || keys.size() == 0)
        {
            new ErrorBox("Key Generation failed. Errors exist in row "
                    + (keys.size() + 1));
            return;
        }

        class DialogBoxMinimizer implements ClickHandler
        {
            DialogBox toBeMinimized = null;

            DialogBoxMinimizer(DialogBox temp)
            {
                toBeMinimized = temp;
            }

            public void onClick(ClickEvent event)
            {
                toBeMinimized.hide();
                CistrackUI.clear();
                //                CistrackUI.basePanel = new KeyGen(username);
                CistrackUI.basePanel = new KeyGen();
            }
        }

        String[] headers = { "Bionimbus Key", "Name", "Biological Material",
                "Antibody / Treatment", "Project", // "Platform",
                "Experiment Type", "Description", "Facility", "Barcode" };
        Grid displayGrid = new Grid(keys.size() + 1, headers.length);
        // int gridRow = 0;

        for (int i = 0; i < headers.length; i++)
        {
            displayGrid.setText(0, i, headers[i]);
        }

        for (int i = 0; i < keys.size(); i++)
        {

            displayGrid.setText(i + 1, 0, keys.get(i).getBionimbusID());
            displayGrid.setText(i + 1, 1, keys.get(i).getUnitName());
            displayGrid.setText(i + 1, 2, keys.get(i).getSampleType());
            displayGrid.setText(i + 1, 3, keys.get(i).getAgentType());
            displayGrid.setText(i + 1, 4, keys.get(i).getProject());
            // displayGrid.setText(i + 1, 5, keys.get(i).getPlatformName());
            displayGrid.setText(i + 1, 5, keys.get(i).getSubtype());
            displayGrid.setText(i + 1, 6, keys.get(i).getDescription());
            displayGrid.setText(i + 1, 7, keys.get(i).getFacility());
            displayGrid.setText(i + 1, 8, keys.get(i).getBarcode());

        }

        VerticalPanel hp = new VerticalPanel();
        Button hideButton = new Button("Ok");

        hp.add(displayGrid);
        hp.add(hideButton);

        final DialogBox d = new DialogBox();

        DialogBoxMinimizer dbm = new DialogBoxMinimizer(d);

        hideButton.addClickHandler(dbm);

        d.add(hp);
        d.center();
        d.setText("Key Generation Summary");

    }

}
