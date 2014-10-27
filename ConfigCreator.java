package org.lac.bionimbus.client;

import java.util.HashMap;
import java.util.Vector;

import org.lac.bionimbus.shared.SQLListBoxQuery;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

class ConfigCreatorLaneGrid extends Grid implements InputValidation
{
    int                       laneNumber               = 0;
    int                       numberOfSamplesDisplayed = 1;
    Vector<ConfigCreatorLane> samples                  = new Vector<ConfigCreatorLane>();

    //static final int          sampleCount              = 24;
    //new requirement from Jigyasa 
    static final int          sampleCount              = 48;

    String[] getSampleLabels()
    {
        String[] labels = new String[sampleCount];
        for (int a = 0; a < sampleCount; ++a)
            labels[a] = "" + (a + 1);
        return labels;
    }

    UserStringListBox numberOfSamples = new UserStringListBox(getSampleLabels());

    //static int        numberOfColumns = 14;

    //number of columns in the seq. config. page in which widgets and text are filled

    static int        numberOfColumns = 15;

    public ConfigCreatorLaneGrid(int maxNumberOfSamples, int laneNumber,
            SQLListBox project, ConfigCreator configCreator)
    {
        super(2, numberOfColumns);
        this.laneNumber = laneNumber;
        for (int i = 0; i < maxNumberOfSamples; i++)
        {
            samples.add(new ConfigCreatorLane(laneNumber, i + 1, project,
                    configCreator));
        }
        numberOfSamples.setSelectedText("1");
        numberOfSamples.addChangeHandler(new ChangeHandler()
        {
            public void onChange(ChangeEvent c)
            {
                refresh();
            }
        });
        samples.elementAt(0).addToGrid(this, 1, false);
        setWidget(1, 1, numberOfSamples);

        Button b = new Button("Copy", new ClickHandler()
        {
            public void onClick(ClickEvent c)
            {
                copyAllValues();
            }
        });
        setWidget(1, 2, b);
        setHeader();

    }

    public void copyWidgetValue(int row, int col, boolean overwrite)
    {
        Widget src = getWidget(1, col);
        Widget dest = getWidget(row, col);
        if (src instanceof UserTextBox)
        {
            copyTextBox((UserTextBox) src, (UserTextBox) dest, overwrite);
        }
        else if (src instanceof SQLListBox)
        {
            copySQLListBox((SQLListBox) src, (SQLListBox) dest, overwrite);
        }
        else if (src instanceof UserStringListBox)
        {
            copyUserStringListBox((UserStringListBox) src,
                    (UserStringListBox) dest, overwrite);
        }

    }

    public void refreshTabOrder()
    {
        int t = numberOfSamples.getTabIndex();
        for (int c = 2; c < getColumnCount(); c++)
            for (int r = 1; r < getRowCount(); r++)
            {
                Widget w = getWidget(r, c);
                if (w != null)
                    ((FocusWidget) w).setTabIndex(++t);
            }
    }

    public void copyTextBox(UserTextBox src, UserTextBox dest, boolean overwrite)
    {
        if (dest.getText().equals("") || overwrite)
        {
            dest.setText(src.getText());
        }
    }

    public void copySQLListBox(SQLListBox src, SQLListBox dest,
            boolean overwrite)
    {
        if (dest.getSelectedText().equals("") || overwrite)
        {
            dest.setSelectedIndex(src.getSelectedIndex());
        }
    }

    public void copyUserStringListBox(UserStringListBox src,
            UserStringListBox dest, boolean overwrite)
    {
        if (dest.getSelectedText().equals("") || overwrite)
        {
            dest.setSelectedIndex(src.getSelectedIndex());
        }
    }

    public void copyAllWidgetValues(int col, boolean overwrite)
    {
        for (int i = 2; i <= getNumberOfSamples(); i++)
        {
            copyWidgetValue(i, col, overwrite);
        }

    }

    public void setSingleHeader(String label, final int col, boolean doButtons)
    {
        VerticalPanel p = new VerticalPanel();
        p.add(new HTML(label.replace("\n", "<br>")));
        /*
         * IF COL COPY BUTTONS ARE NEEDED if (doButtons) { HorizontalPanel h =
         * new HorizontalPanel(); h.add(new Button("C", new ClickHandler() {
         * public void onClick(ClickEvent ce) { copyAllWidgetValues(col, false);
         * } })); h.add(new Button("C/O", new ClickHandler() { public void
         * onClick(ClickEvent ce) { copyAllWidgetValues(col, true); } }));
         * p.add(h); }
         */
        setWidget(0, col, p);
    }

    public void setHeader()
    {
        String[] laneHeadings = new String[] { "SubLane", "# of Samples", "",
                "Sample Name", "Database Key", "Requestor",
                "Mapping Reference File", "Cluster\nStation\nConcentration",
                "", "Analysis Type", "Analysis Version", "Database\nSubmit",
                "Index/Barcode", "Index/Barcode 2", "QC/Production" };
        for (int c = 0; c < laneHeadings.length; c++)
        {
            setSingleHeader(
                    laneHeadings[c],
                    c,
                    (!(laneHeadings[c].equals("SubLane")
                            || laneHeadings[c].equals("# of Samples") || laneHeadings[c]
                            .equals(""))));
        }
    }

    public void copyAllValues()
    {
        for (int i = 1; i < samples.size(); i++)
        {
            samples.elementAt(i).copyValues(samples.elementAt(0));
        }
    }

    int getNumberOfSamples()
    {
        try
        {
            return Integer.valueOf(numberOfSamples.getSelectedText());
        }
        catch (Exception e)
        {
            numberOfSamples.setSelectedText("1");
            return 1;
        }
    }

    public void refresh()
    {
        int current_rows = numberOfSamplesDisplayed;
        int to_display = getNumberOfSamples();
        if (current_rows == to_display)
        {
            return;
        }

        if (current_rows < to_display)
        {// add rows

            resizeRows(to_display + 1);
            for (int i = current_rows - 1; i < to_display; i++)
            {
                samples.elementAt(i).addToGrid(this, i + 1, true);
            }
        }

        if (current_rows > to_display)
        { // remove rows
            resizeRows(to_display + 1);
        }

        numberOfSamplesDisplayed = getNumberOfSamples();
        refreshTabOrder();
    }

    public void setFieldLabel(String s)
    {
    }

    public String getFieldLabel()
    {
        String msg = "Lane# " + laneNumber + "\n";
        for (int i = 0; i < getNumberOfSamples(); i++)
        {
            if (!samples.elementAt(i).isValid())
                msg += samples.elementAt(i).getFieldLabel() + "\n";
        }
        return msg;
    }

    public boolean isValid()
    {
        boolean v = true;
        for (int i = 0; i < getNumberOfSamples(); i++)
        {
            if (!samples.elementAt(i).isValid())
                v = false;
        }
        return v;
    }

    public boolean isUniqueBarcode()
    {
        boolean v = true;
        try
        {

            //may have to be changed to HashTable for threaded applications
            HashMap<String, Integer> hm = new HashMap<String, Integer>();

            //create HashMap with Key=Barcode and Value=no.of occurrences
            for (int i = 0; i < getNumberOfSamples(); i++)
            {
                if (hm.containsKey(samples.elementAt(i).indexBarcode
                        .getSelectedText()
                        + samples.elementAt(i).indexBarcode2.getSelectedText()))
                {
                    //barcode exists, increment its count value
                    v = false;
                    hm.put(samples.elementAt(i).indexBarcode.getSelectedText()
                            + samples.elementAt(i).indexBarcode2
                                    .getSelectedText(), hm.get(samples
                            .elementAt(i).indexBarcode.getSelectedText()
                            + samples.elementAt(i).indexBarcode2
                                    .getSelectedText()) + 1);
                    samples.elementAt(i).indexBarcode.removeStyleName("Valid");
                    samples.elementAt(i).indexBarcode.addStyleName("Invalid");
                    samples.elementAt(i).indexBarcode2.removeStyleName("Valid");
                    samples.elementAt(i).indexBarcode2.addStyleName("Invalid");

                }
                else
                {
                    //barcode doesn't exist, its count = 1
                    hm.put(samples.elementAt(i).indexBarcode.getSelectedText()
                            + samples.elementAt(i).indexBarcode2
                                    .getSelectedText(), 1);
                }
            }
        }
        catch (Exception e)
        {
            new ErrorBox("Exception in isUniqueBarcode function: "
                    + e.toString());
        }
        return v;
    }

    public boolean isUniqueDBKey()
    {
        boolean v = true;
        try
        {

            //may have to be changed to HashTable for threaded applications
            HashMap<String, Integer> hm = new HashMap<String, Integer>();

            //create HashMap with Key=BID and Value=no.of occurrences
            for (int i = 0; i < getNumberOfSamples(); i++)
            {
                if (hm.containsKey(samples.elementAt(i).databaseKey.getText()
                        .trim()))
                {
                    //dbkey exists, increment its count value
                    v = false;
                    hm.put(samples.elementAt(i).databaseKey.getText().trim(),
                            hm.get(samples.elementAt(i).databaseKey.getText()
                                    .trim()) + 1);
                    samples.elementAt(i).databaseKey.removeStyleName("Valid");
                    samples.elementAt(i).databaseKey.addStyleName("Invalid");

                }
                else
                {
                    //dbkey doesn't exist, its count = 1
                    hm.put(samples.elementAt(i).databaseKey.getText().trim(), 1);
                }
            }
        }
        catch (Exception e)
        {
            new ErrorBox("Exception in isUniqueDBKey function: " + e.toString());
        }
        return v;
    }

    public String getAsText()
    {
        String s = "";
        for (int i = 0; i < getNumberOfSamples(); i++)
        {
            s += samples.elementAt(i).getAsText() + "\n";
        }
        return s;
    }
}

class ConfigCreatorLane implements InputValidation
{

    int               laneNumberMajor      = 0;
    int               laneNumberMinor      = 0;
    ConfigCreator     configCreator        = null;

    UserTextBox       sampleName           = new UserTextBox(),
            clusterStationConcentration = new UserTextBox(),
            databaseKey = new UserTextBox();

    SQLListBox        projectName;

    String[]          headingMapping       = new String[] {
            "none",
            "2010_03_05_Sarayu_test",
            "B728A_Pseudomonas_syringae",
            "Bacillus_anthracis_complete_genome_str_Sterne_NC_005945.1_AND_plasmid_pX01_NC_001496.1",
            "Christos", "Dmel_genome_April2006",
            "Feb_2009_human_genome_hg19_GRCh37_chromFa",
            "Feb_2009_human_genome_hg19_GRCh37_chromFa_NO_random_Un_hap",
            "Feb_2009_human_genome_hg19_GRCh37_chromFa_minus_MaleY",
            "HG18_NCBI-build_36.1", "LSR1",
            "NC_003143-NC_003131-NC_003132-NC_003134",
            "NC_006153-NC_006154-NC_006155", "PB_Solexa_library_adapter",
            "RepBase15.01.fasta", "SoCe377v5",
            "Yersinia_pseudotuberculosis_IP_32953_chrom_and_plasmid_seq",
            "aquilegia", "dmel", "dmel-all-chromosome-r5.23.fasta",
            "dmel-all-chromosome-r5.5", "dpse-all-chrom-r2.3", "dpse-r2.1",
            "dpse-r2.2", "droSim1_All_chromosomesApr.2005",
            "dyak-all-chromosome-r1.3.fasta", "human", "idk",
            "mm9_NCBI_Build_37_July2007_chromFa", "mouse", "mouse_mm9",
            "panTro2", "phi", "rat", "repeats_for_Sarayu_Ursula_2010_02_24",
            "rodrep_edit_test", "switchgrass" },

            headingAnalysisFile = new String[] { "default", "eland",
            "eland_extended", "eland_pair", "eland_rna", "eland_tag", "none",
            "sequence", "sequence_pair" },

            headingAnalysisVersion = new String[] { "current",
            "IGSB-Pipeline-1.7.sh", "IGSB-Pipeline-1.6.sh",
            "IGSB-Pipeline-1.5.sh", "IGSB-Pipeline-1.4.sh",
            "IGSB-Pipeline-1.3.2.sh" };

    UserStringListBox mappingReferenceFile = new UserStringListBox(
                                                   headingMapping);
    UserStringListBox analysisFile         = new UserStringListBox(
                                                   headingAnalysisFile);

    UserStringListBox analysisVersion      = new UserStringListBox(
                                                   headingAnalysisVersion);

    UserStringListBox databaseSubmit       = new UserStringListBox(
                                                   new String[] { "NA",
            "Bionimbus"                           });

    UserStringListBox indexBarcode         = new UserStringListBox();

    UserStringListBox qcProduction         = new UserStringListBox(
                                                   new String[] { "QC",
            "Production"                          });

    //added indexBarcode2 to handle combined Barcodes
    UserStringListBox indexBarcode2        = new UserStringListBox();

    public ConfigCreatorLane(int laneNumberMajor, int laneNumberMinor,
            SQLListBox project, ConfigCreator configCreator)// throws Exception
    {
        Utils.fillBarcode(indexBarcode);
        this.laneNumberMajor = laneNumberMajor;
        this.laneNumberMinor = laneNumberMinor;
        this.configCreator = configCreator;
        projectName = new SQLListBox(project);
        databaseSubmit.setSelectedText("Bionimbus");
        analysisFile.setSelectedText("default");
        analysisVersion.setSelectedText("current");
        addValidations();

    }

    public void copyValues(ConfigCreatorLane c)
    {
        sampleName.setText(c.sampleName.getText());
        projectName.setSelectedText(c.projectName.getSelectedText());
        mappingReferenceFile.setSelectedText(c.mappingReferenceFile
                .getSelectedText());
        clusterStationConcentration.setText(c.clusterStationConcentration
                .getText());
        analysisFile.setSelectedText(c.analysisFile.getSelectedText());
        analysisVersion.setSelectedText(c.analysisVersion.getSelectedText());
        databaseKey.setText(c.databaseKey.getText());
        databaseSubmit.setSelectedText(c.databaseSubmit.getSelectedText());
        qcProduction.setSelectedText(c.qcProduction.getSelectedText());
        indexBarcode.setSelectedText(c.indexBarcode.getSelectedText());

        //load values from indexBarcode2 of previous sample
        indexBarcode2.clear();

        try
        {

            for (int i = 0; i < c.indexBarcode2.getItemCount(); i++)
            {
                indexBarcode2.addItem(c.indexBarcode2.getItemText(i));

            }
            indexBarcode2.setSelectedText(c.indexBarcode2.getSelectedText());
        }
        catch (Exception e)
        {
            new ErrorBox("Exception:" + e.toString());

        }

    }

    public void addToGrid(Grid g, int atRow, boolean showMinor)// throws
                                                               // Exception
    {

        clusterStationConcentration.setVisibleLength(5);

        databaseKey.addValueChangeHandler(new ValueChangeHandler<String>()
        {
            public void onValueChange(ValueChangeEvent<String> ev)
            {
                //allow only numbers-numbers pattern in BIDs
                String s = databaseKey.getText().trim();
                boolean val = s.matches("\\d+-\\d+");
                if (val == false)
                {
                    new ErrorBox(
                            "Database Key(BID) should be in \"Numbers-Numbers\" format. Ex:2012-1000.");
                    databaseKey.setText("");
                }

                getIndexBarcode();
            }
        });

        //changing back the StyleName on user click after highlighted by error in isUniqueBarcode method
        indexBarcode.addClickHandler(new ClickHandler()
        {
            public void onClick(ClickEvent ev)
            {

                indexBarcode.removeStyleName("Invalid");
                indexBarcode.addStyleName("Valid");
                indexBarcode2.removeStyleName("Invalid");
                indexBarcode2.addStyleName("Valid");

            }
        });

        //changing back the StyleName on user click after highlighted by error in isUniqueBarcode method
        indexBarcode2.addClickHandler(new ClickHandler()
        {
            public void onClick(ClickEvent ev)
            {

                indexBarcode2.removeStyleName("Invalid");
                indexBarcode2.addStyleName("Valid");

            }
        });

        //to handle combined barcode of A and N seriese
        indexBarcode.addChangeHandler(new ChangeHandler()
        {

            public void onChange(ChangeEvent c)
            {
                try
                {
                    //if selected barcode belongs to either A or N series, fill another barcode field, else return
                    char in = indexBarcode.getSelectedText().trim().charAt(0);

                    if (in == 'A')
                    {
                        indexBarcode2.clear();
                        indexBarcode2.addItem("None");

                        //add items that start with N
                        for (int i = 0; i < indexBarcode.getItemCount(); i++)
                        {
                            String s = indexBarcode.getItemText(i);
                            if (s.charAt(0) == 'N')
                                indexBarcode2.addItem(s);

                        }
                        indexBarcode2.setSelectedText("None");

                    }
                    else if (in == 'N')
                    {

                        indexBarcode2.clear();
                        indexBarcode2.addItem("None");

                        //add items that start with A
                        for (int i = 0; i < indexBarcode.getItemCount(); i++)
                        {
                            String s = indexBarcode.getItemText(i);
                            if (s.charAt(0) == 'A')
                                indexBarcode2.addItem(s);

                        }
                        indexBarcode2.setSelectedText("None");
                    }
                    else
                    {
                        indexBarcode2.clear();
                        indexBarcode2.addItem("None");

                        //select None by default
                        indexBarcode2.setSelectedText("None");
                    }

                }

                catch (Exception e)
                {
                    new ErrorBox("Exception:", e.toString());
                }
            }
        });

        int i = 0;
        g.setText(atRow, i++, laneNumberMajor + "-" + laneNumberMinor);
        i += 2;

        g.setWidget(atRow, i++, sampleName);
        g.setWidget(atRow, i++, databaseKey);
        g.setWidget(atRow, i++, projectName);
        g.setWidget(atRow, i++, mappingReferenceFile);

        g.setWidget(atRow, i++, clusterStationConcentration);
        g.setText(atRow, i++, "pM");
        g.setWidget(atRow, i++, analysisFile);
        g.setWidget(atRow, i++, analysisVersion);

        g.setWidget(atRow, i++, databaseSubmit);
        g.setWidget(atRow, i++, indexBarcode);
        g.setWidget(atRow, i++, indexBarcode2);
        g.setWidget(atRow, i++, qcProduction);

    }

    public String getAsText()
    {
        String txtProject = projectName.getSelectedText(), txtSample = sampleName
                .getText().trim(), txtMapping = mappingReferenceFile
                .getSelectedText(), txtAnalysisFile = analysisFile
                .getSelectedText(), txtAnalysisVersion = analysisVersion
                .getSelectedText(), txtDatabaseSubmit = databaseSubmit
                .getSelectedText(), txtConcentration = clusterStationConcentration
                .getText().trim(), txtDatabaseKey = databaseKey.getText()
                .trim(), txtIndexBarcode = Utils.beforeComma(indexBarcode
                .getSelectedText()), txtIndexBarcode2 = indexBarcode2
                .getSelectedText().trim(), txtQCProduction = qcProduction
                .getSelectedText(); //to handle combined barcodes

        return laneNumberMajor + "-" + laneNumberMinor + "\t" + txtSample
                + "\t" + txtDatabaseKey + "\t" + txtProject + "\t" + txtMapping
                + "\t" + txtConcentration + "\t" + txtAnalysisFile + "\t"
                + txtAnalysisVersion + "\t" + txtDatabaseSubmit + "\t"
                + txtIndexBarcode + " + " + txtIndexBarcode2 + "\t"
                + txtQCProduction;

    }

    private Vector<InputValidation> validations = new Vector<InputValidation>();

    private void addValidations()
    {
        databaseKey.setFieldLabel("Database Key");
        projectName.setFieldLabel("Requestor");
        mappingReferenceFile.setFieldLabel("Mapping Reference File");
        clusterStationConcentration
                .setFieldLabel("Cluster Station Concentration");
        analysisFile.setFieldLabel("Analysis Type");
        analysisVersion.setFieldLabel("Analysis Version");
        databaseSubmit.setFieldLabel("Database Submit");
        indexBarcode.setFieldLabel("Index/Barcode");
        indexBarcode2.setFieldLabel("Index/Barcode:2");
        qcProduction.setFieldLabel("QC/Production");

        validations.add(databaseKey);
        validations.add(projectName);
        // validations.add(mappingReferenceFile);
        validations.add(clusterStationConcentration);
        validations.add(analysisFile);
        validations.add(analysisVersion);
        validations.add(databaseSubmit);
        validations.add(indexBarcode);
        validations.add(qcProduction);
    }

    public boolean isValid()
    {
        boolean b = true;
        //mapping reference file is not mandatory any more
        // new conditional validation for mapping reference file
        /*
        if (!(configCreator.readType.getSelectedText().equals(
                "single-read multiplex") && (analysisFile.getSelectedText()
                .equals("sequence") || analysisFile.getSelectedText().equals(
                "sequence_pair"))))
        {
            if (!validations.contains(mappingReferenceFile))
                validations.add(mappingReferenceFile);
        }
        else
        {
            validations.remove(mappingReferenceFile);
            mappingReferenceFile.removeStyleName("Invalid");
        }*/

        for (InputValidation v : validations)
        {
            if (!v.isValid())
            {
                b = false; // we don't use return just to mark all fields
            }
        }
        return b;

    }

    public String getFieldLabel()
    {
        String labels = "";
        for (InputValidation v : validations)
        {
            if (!v.isValid())
            {
                labels += "'" + v.getFieldLabel() + "' ";
            }
        }
        return "SubLane# " + laneNumberMinor + " " + labels;
    }

    public void setFieldLabel(String s)
    {
        // nothing specific
    }

    private void getIndexBarcode()
    {
        AsyncCallback<String> callback = new AsyncCallback<String>()
        {
            public void onFailure(Throwable caught)
            {
                new ErrorBox(
                        "Config Creator: Error during RPC (getIndexBarcode)"
                                + caught);
            }

            public void onSuccess(String callbackResult)
            {
                try
                {
                    if (callbackResult.equals(""))
                    {
                        /*
                                    indexBarcode
                                            .setSelectedText(UserStringListBox.DEFAULT_TEXT);
                                    new ErrorBox("No Index/Barcode found for database key: " + databaseKey.getText().trim());
                        */
                    }
                    else
                    {
                        indexBarcode.setSelectedText(callbackResult);
                    }

                }
                catch (Exception e)
                {
                    new ErrorBox("RPC Failed");
                }
            }
        };

        try
        {
            CistrackUI.rpc.getIndexBarcode(databaseKey.getText().trim(),
                    callback);
        }
        catch (Exception e)
        {
            new ErrorBox("RPC Failed");
        }
    }

}

public class ConfigCreator extends VerticalPanelWithHeader
{
    TabPanel                      tab         = new TabPanel();
    Vector<ConfigCreatorLaneGrid> lanes       = new Vector<ConfigCreatorLaneGrid>();
    int                           numCols     = 3;
    BnGrid                        summaryGrid = new BnGrid(1, numCols);

    public ConfigCreator()
    {
        super("Welcome to your IGSB Illumina Sequencer Config Creation form");
        try
        {
            SQLListBox project = new SQLListBox(
                    SQLListBoxQuery.items.SQLLIST_PERSONNEL_DISTINCT);
            for (int i = 0; i < 8; i++)
            {
                ConfigCreatorLaneGrid c = new ConfigCreatorLaneGrid(
                        ConfigCreatorLaneGrid.sampleCount, i + 1, project, this);
                lanes.add(c);
                tab.add(c, "Lane " + (i + 1) + " ");
            }
            VerticalPanel submit = new VerticalPanel();

            //add a grid to show summary of Lane numbers,BIDs(Database keys),Barcodes
            summaryGrid
                    .setTitle("List of BIDs/Database keys and associated Barcodes");

            summaryGrid.setText(0, 0, "Lane #");
            summaryGrid.setText(0, 1, "Database key");
            summaryGrid.setText(0, 2, "Index/Barcode");

            for (int i = 0; i < numCols; i++)
            {
                DOM.setStyleAttribute(summaryGrid.getCellFormatter()
                        .getElement(0, i), "border", "1px solid #000");
            }

            submit.add(summaryGrid);

            submit.add(new Button("Create README", new ClickHandler()
            {
                public void onClick(ClickEvent c)
                {
                    if (validate())
                    {
                        createReadme();
                    }
                }
            }));
            tab.add(submit, "Summary/Create README");

            tab.addSelectionHandler(new SelectionHandler<Integer>()
            {
                public void onSelection(SelectionEvent<Integer> event)
                {
                    //on selection of Summary/Create Readme Tab, fill in grid with details from previous 0-7 tabs
                    if (event.getSelectedItem() == 8)
                    {

                        int gridrows = 1;

                        for (int i = 0; i < 8; i++)
                        {
                            int rows = lanes.elementAt(i).numberOfSamplesDisplayed;
                            summaryGrid.resize(gridrows + rows, numCols);

                            for (int j = 0; j < rows; j++)
                            {
                                //fill in lane numbers
                                String num = Integer.toString(lanes
                                        .elementAt(i).samples.elementAt(j).laneNumberMajor)
                                        + "-"
                                        + Integer.toString(lanes.elementAt(i).samples
                                                .elementAt(j).laneNumberMinor);
                                //0 column has Lane numbers
                                summaryGrid.setText(gridrows + j, 0, num);

                                //fill in database keys/BIDs
                                summaryGrid.setText(
                                        gridrows + j,
                                        1,
                                        lanes.elementAt(i).samples.elementAt(j).databaseKey
                                                .getText());

                                //fill in index/barcodes 
                                String str = "";
                                if (lanes.elementAt(i).samples.elementAt(j).indexBarcode2
                                        .getSelectedText().trim() != "")
                                {
                                    str = " + "
                                            + lanes.elementAt(i).samples
                                                    .elementAt(j).indexBarcode2
                                                    .getSelectedText();
                                }

                                summaryGrid.setText(
                                        gridrows + j,
                                        2,
                                        lanes.elementAt(i).samples.elementAt(j).indexBarcode
                                                .getSelectedText() + str);

                            }
                            gridrows += rows;

                        }

                    } // end of if (event.getSelectedItem() == 8)

                }
            });

            addFields();
            assignRunName();
            addValidations();
            HorizontalPanel h = new HorizontalPanel();
            h.add(gridMain);
            h.add(gridDate);
            h.add(gridNote);
            add(h);
            add(new HTML("<br>"));
            add(tab);
            RootPanel.get().add(this);
            tab.selectTab(0);

        }

        catch (Exception e)
        {
            new ErrorBox("Problem!", e.toString());
        }
    }

    private final ClientInterfaceAsync rpc           = GWT.create(ClientInterface.class);

    Grid                               gridMain      = new Grid(9, 2),
            gridDate = new Grid(1, 2), gridNote = new Grid(1, 2);

    int                                numberOfLanes = 8;

    UserTextBox                        runName       = new UserTextBox(),
            readCycles1 = new UserTextBox(), readCycles2 = new UserTextBox(),
            indexReadCycles = new UserTextBox(),
            flowCellID = new UserTextBox();

    UserStringListBox                  readType      = new UserStringListBox(
                                                             new String[] {
            "single read", "paired end", "single-read multiplex",
            "paired-end multiplex"                          }),

            solexa = new UserStringListBox(new String[] { "GAII", "GAIIx",
            "HiSeq2000" }), solexaSNR = new UserStringListBox(new String[] {
            "300146", "300279", "300137", "300552", "HiSeq2000-1",
            "HiSeq2000-2", "HiSeq2000-3", "HiSeq2000-4" });

    TextArea                           note          = new TextArea();

    DatePicker                         runDate       = new DatePicker();

    Vector<InputValidation>            validations   = new Vector<InputValidation>();

    private void addFields() throws Exception
    {
        runDate.setValue(new java.util.Date(), true);
        note.setCharacterWidth(30);
        note.setVisibleLines(4);

        addToGrid(gridMain, 0, "Run Name", runName);
        addToGrid(gridMain, 1, "Flow Cell ID", flowCellID);
        addToGrid(gridMain, 2, "Read Type", readType);
        addToGrid(gridMain, 3, "Read 1 cycles", readCycles1);
        addToGrid(gridMain, 4, "Read 2 cycles", readCycles2);
        addToGrid(gridMain, 5, "Index Read Cycles", indexReadCycles);
        addToGrid(gridMain, 6, "Solexa", solexa);
        addToGrid(gridMain, 7, "Solexa SNR", solexaSNR);

        gridDate.getRowFormatter().setVerticalAlign(0,
                HasVerticalAlignment.ALIGN_TOP);
        gridNote.getRowFormatter().setVerticalAlign(0,
                HasVerticalAlignment.ALIGN_TOP);
        addToGrid(gridDate, 0, "Run Date", runDate);
        addToGrid(gridNote, 0, "Note", note);
    }

    private void addToGrid(Grid g, int row, String labelText, Widget w)
    {
        g.setText(row, 0, labelText);
        g.setWidget(row, 1, w);
    }

    public String getAsText()
    {
        String txtReadType = readType.getSelectedText(), txtSolexa = solexa
                .getSelectedText(), txtSolexaSNR = solexaSNR.getSelectedText();

        java.util.Date dt = runDate.getValue();
        String txtRunDate = (dt.getYear() + 1900) + // gwt jre getYear()
                                                    // implementation is
                                                    // incorrect
                "-" + (dt.getMonth() + 1) + "-" + dt.getDate();

        String returnTxt = "Run Name:\t"
                + runName.getText().trim()
                + "\n"
                + "Flow Cell ID:\t"
                + flowCellID.getText().trim()
                + "\n"
                + "Run Date:\t"
                + txtRunDate
                + "\n"
                + "Read Type:\t"
                + txtReadType
                + "\n"
                + "Read 1 Cycles\t=\t"
                + readCycles1.getText().trim()
                + "\n"
                + "Read 2 Cycles\t=\t"
                + readCycles2.getText().trim()
                + "\n"
                + "Index Read Cycles\t=\t"
                + indexReadCycles.getText().trim()
                + "\n"
                + "Solexa\t=\t"
                + txtSolexa
                + "\n"
                + "SNR\t=\t"
                + txtSolexaSNR
                + "\n\n"
                + "Note:\t"
                + note.getText().trim()
                + "\n"
                + "Lane;\tSample Name;Database Key;\tRequestor Name;\tMapping Reference File;\tCluster Station Concentration;\tAnalysis Type;\tAnalysis Version;\tDatabase Submit;\tIndex/Barcode\n";

        return returnTxt;

    }

    private void addValidations()
    {
        runName.setFieldLabel("Run Name");
        flowCellID.setFieldLabel("Flow Cell ID");
        readType.setFieldLabel("Read type");
        readCycles1.setFieldLabel("Read 1 cycles");
        readCycles2.setFieldLabel("Read 2 cycles");
        indexReadCycles.setFieldLabel("Index Read Cycles");
        solexa.setFieldLabel("Solexa");
        solexaSNR.setFieldLabel("Solexa SNR");

        validations.add(runName);
        validations.add(flowCellID);
        validations.add(readType);
        validations.add(readCycles1);
        validations.add(readCycles2);
        validations.add(indexReadCycles);
        validations.add(solexa);
        validations.add(solexaSNR);
    }

    public boolean validate()
    {
        boolean valid = true;
        String messages = "Please enter data for the following fields <b>highlighted in yellow</b>:\n";
        for (InputValidation v : validations)
        {
            if (!v.isValid())
            {
                valid = false;
                messages += "  " + v.getFieldLabel() + "\n";
            }
        }

        if (!valid)
        {
            new ErrorBox("Missing or Empty fields", messages, new Button("OK",
                    new ClickHandler()
                    {
                        public void onClick(ClickEvent e)
                        {
                        }
                    }));
            return false;
        }

        for (ConfigCreatorLaneGrid c : lanes)
        {
            if (!c.isValid())
            {
                tab.selectTab(c.laneNumber - 1);
                new ErrorBox("Missing or Empty fields",
                        "Please enter data for following lane <b>highlighted in yellow</b>\n"
                                + c.getFieldLabel(), new Button("OK",
                                new ClickHandler()
                                {
                                    public void onClick(ClickEvent e)
                                    {
                                    }
                                }));
                return false;
            }
        }

        //check if Index/Barcode is unique in each lane
        for (ConfigCreatorLaneGrid c : lanes)
        {
            if (!c.isUniqueBarcode())
            {
                tab.selectTab(c.laneNumber - 1);
                new ErrorBox(
                        "Error: Repeated Index/Barcodes",
                        "Please enter unique Index/Barcodes for samples <b>highlighted in yellow</b>\n",
                        new Button("OK", new ClickHandler()
                        {
                            public void onClick(ClickEvent e)
                            {
                            }
                        }));
                return false;
            }
        }

        //check if DBKey/BID is unique in each lane
        for (ConfigCreatorLaneGrid c : lanes)
        {
            if (!c.isUniqueDBKey())
            {
                tab.selectTab(c.laneNumber - 1);
                new ErrorBox(
                        "Error: Repeated DatabaseKeys/BIDs",
                        "Please enter unique DatabaseKeys/BIDs for samples <b>highlighted in yellow</b>\n",
                        new Button("OK", new ClickHandler()
                        {
                            public void onClick(ClickEvent e)
                            {
                            }
                        }));
                return false;
            }
        }

        //check if (DatabaseKey, Barcode) pair is consistent across all lanes
        if (!checkDBKeyBarcode(lanes))
        {
            new ErrorBox(
                    "Error: (DatabaseKey, Barcode) pair is not consistent across lanes",
                    "Please enter same Barcode for a Database Key across lanes for samples <b>highlighted in yellow</b>\n",
                    new Button("OK", new ClickHandler()
                    {
                        public void onClick(ClickEvent e)
                        {
                        }
                    }));
            return false;
        }

        return true;
    }

    private boolean checkDBKeyBarcode(Vector<ConfigCreatorLaneGrid> lanes)
    {
        boolean v = true;
        try
        {
            //may have to be changed to HashTable for threaded applications
            HashMap<String, String> hm = new HashMap<String, String>();

            int currentrow = 0;
            //fill in hm with dbkey,barcode pairs for all lanes
            for (ConfigCreatorLaneGrid c : lanes)
            {

                for (int i = 0; i < c.getNumberOfSamples(); i++)
                {
                    currentrow++;
                    //check if dbkey exists
                    if (hm.containsKey(c.samples.elementAt(i).databaseKey
                            .getText()))
                    {
                        //barcode exists for dbkey
                        if (hm.get(c.samples.elementAt(i).databaseKey.getText())
                                .equals(c.samples.elementAt(i).indexBarcode
                                        .getSelectedText()
                                        + c.samples.elementAt(i).indexBarcode2
                                                .getSelectedText()) == false)
                        {
                            //different barcodes for the same dbkey
                            v = false;
                            c.samples.elementAt(i).indexBarcode
                                    .removeStyleName("Valid");
                            c.samples.elementAt(i).indexBarcode
                                    .addStyleName("Invalid");

                            c.samples.elementAt(i).indexBarcode2
                                    .removeStyleName("Valid");
                            c.samples.elementAt(i).indexBarcode2
                                    .addStyleName("Invalid");

                            summaryGrid.getRowFormatter().addStyleName(
                                    currentrow, "Invalid");

                        }

                    }

                    else
                    {
                        //barcode doesn't exist for dbkey
                        hm.put(c.samples.elementAt(i).databaseKey.getText()
                                .trim(),
                                c.samples.elementAt(i).indexBarcode
                                        .getSelectedText()
                                        + c.samples.elementAt(i).indexBarcode2
                                                .getSelectedText());
                    }
                }
            }
        }
        catch (Exception e)
        {
            new ErrorBox("Exception in checkDBKeyBarcode: " + e.toString());
        }

        return v;
    }

    private void assignRunName()
    {
        AsyncCallback<String> callback = new AsyncCallback<String>()
        {
            public void onFailure(Throwable caught)
            {
                new ErrorBox(
                        "Config Creator: Error during RPC (to assign run name) "
                                + caught);
            }

            public void onSuccess(String callbackResult)
            {
                try
                {
                    runName.setText(callbackResult);
                }
                catch (Exception e)
                {
                    new ErrorBox("RPC Failed (to assign run name)");
                }
            }
        };

        try
        {
            rpc.configCreatorGetRunName(callback);
        }
        catch (Exception e)
        {
            new ErrorBox("RPC Failed (to assign run name)");
        }
    }

    private void createReadme()
    {
        AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>()
        {
            public void onFailure(Throwable caught)
            {
                new ErrorBox("Config Creator: Error during RPC " + caught);
            }

            public void onSuccess(Boolean callbackResult)
            {
                try
                {
                    if (callbackResult.booleanValue())
                    {
                        new ErrorBox("Success creating file.");
                        assignRunName();
                    }
                    else
                    {
                        new ErrorBox("Failure");
                    }
                }
                catch (Exception e)
                {
                    new ErrorBox("RPC Failed");
                }
            }
        };

        String s = getAsText();
        for (ConfigCreatorLaneGrid c : lanes)
        {
            s += c.getAsText();
        }
        // System.out.println(s);
        add(new HTML("<h4>README FILE DATA</h4><br><br>"
                + s.replace("\n", "<br>")));
        try
        {
            rpc.createConfigFile(runName.getText().trim(), s, callback);
        }
        catch (Exception e)
        {
            new ErrorBox("RPC Failed");
        }
    }

}
