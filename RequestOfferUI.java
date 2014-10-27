package org.lac.bionimbus.client;

import java.util.Vector;

import org.lac.bionimbus.shared.Personnel;
import org.lac.bionimbus.shared.RequestOffer;
import org.lac.bionimbus.shared.SQLListBoxQuery;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class RequestOfferUI extends VerticalPanelWithHeader
{

    int                            requestOfferId       = -1;

    Grid                           gridPersonnel        = new Grid(7, 3),
            gridSequencing = new Grid(10, 3), gridArray = new Grid(10, 3);

    TabPanel                       tabPanel             = new TabPanel();

    SQLListBox                     contactPI, contactAdmin, contactRequestor,
            project;

    // for sequencing
    UserStringListBox              statusBilling        = new UserStringListBox(
                                                                new String[] {
            "New", "Processing", "Invoice Sent", "Payment Received",
            "Cancelled"                                        }),
            statusSequencing = new UserStringListBox(new String[] { "New",
            "Package Received", "Processing", "Library Complete", "Complete",
            "Cancelled" }), seqPlatform = new UserStringListBox(new String[] {
            "454", "Solexa" }), arrPlatform = new UserStringListBox(
                    new String[] { "Agilent", "Agilent Seq Cap", "Nimblegen",
            "other" });

    RadioButtonGroup               seqYesNo             = RadioButtonGroup
                                                                .newYesNo(
                                                                        "seqyesno",
                                                                        true),
            arrYesNo = RadioButtonGroup.newYesNo("arryesno", true);

    UserTextBox                    seqNumSamples        = new UserTextBox(),
            seqPreparationType = new UserTextBox(),
            seqLanesRequired = new UserTextBox(),
            seqCyclesRequired = new UserTextBox(),
            seqReference = new UserTextBox(),

            arrNumSamples = new UserTextBox(),
            arrLabelingType = new UserTextBox(),
            arrOutputRequired = new UserTextBox();

    TextArea                       seqComments          = new TextArea(),
            arrComments = new TextArea();

    Button                         submitSequencing     = new Button(
                                                                "Finish",
                                                                new ClickHandler()
                                                                {
                                                                    public void onClick(
                                                                            ClickEvent e)
                                                                    {
                                                                        createUpdateRequestOffer(RequestOffer.SEQUENCING);
                                                                    }
                                                                }),
            submitArray = new Button("Finish", new ClickHandler()
            {
                public void onClick(ClickEvent e)
                {
                    createUpdateRequestOffer(RequestOffer.ARRAY);
                }
            }),
            closeSequencing = new Button("Close"),
            closeArray = new Button("Close"),
            addNewPI = null,
            addNewAdmin = null,
            addNewRequestor = null;

    public Vector<InputValidation> validationMain       = new Vector<InputValidation>();
    public Vector<InputValidation> validationSequencing = new Vector<InputValidation>();
    public Vector<InputValidation> validationArray      = new Vector<InputValidation>();

    public RequestOfferUI()
    {
        super("Request offer from IGSB-HGAC");
        init(null);
    }

    public RequestOfferUI(RequestOffer r)
    {
        super("");
        init(r);
    }

    public void displayThisSomewhere()
    {
        closeSequencing.setVisible(false); // the close buttons are invisble until a child class wants them
        closeArray.setVisible(false);
        add(new HTML(
                "The High-throughput Genome Analysis Core (HGAC) is a state-of-the-art facility providing resources and services for ultra-high-throughput sequencing and large-scale microarray processing. Operated by the Institute for Genomics and Systems Biology, the HGAC facility is available to University of Chicago and Argonne National Laboratory investigators and their collaborators. Please fill out the form below to request an offer.<br><br>"));
        RootPanel.get().add(this);
    }

    protected void init(RequestOffer r)
    {
        displayThisSomewhere(); // any display customizations before init

        try
        {

            contactPI = new SQLListBox(
                    SQLListBoxQuery.items.SQLLIST_PERSONNEL_PI);
            contactAdmin = new SQLListBox(
                    SQLListBoxQuery.items.SQLLIST_PERSONNEL_ADMIN);
            contactRequestor = new SQLListBox(
                    SQLListBoxQuery.items.SQLLIST_PERSONNEL_DISTINCT);
            project = new SQLListBox(SQLListBoxQuery.items.SQLLIST_PROJECT);

            // buttons need the above listboxes
            addNewPI = new Button("Add New", new ClickHandler()
            {
                public void onClick(ClickEvent e)
                {
                    AddUser u = new AddUser(Personnel.USER_PI, false);
                    u.addUpdateTarget(contactPI);
                }
            });
            addNewAdmin = new Button("Add New", new ClickHandler()
            {
                public void onClick(ClickEvent e)
                {
                    AddUser u = new AddUser(Personnel.USER_ADMIN, false);
                    u.addUpdateTarget(contactAdmin);
                }
            });
            addNewRequestor = new Button("Add New", new ClickHandler()
            {
                public void onClick(ClickEvent e)
                {
                    AddUser u = new AddUser(Personnel.USER_WITHLOGIN, true);
                    u.addUpdateTarget(contactRequestor);
                }
            });

            seqNumSamples.setText("0");
            arrNumSamples.setText("0");

            seqNumSamples.setNumeric();
            arrNumSamples.setNumeric();

            if (r == null)
            {
                statusSequencing.setSelectedText("New");
                statusBilling.setSelectedText("New");
                addPersonnel(gridPersonnel, 0);
            }
            else
            {
                requestOfferToUI(r);
                addStatus(gridPersonnel);
                addPersonnel(gridPersonnel, 3);
            }

            add(gridPersonnel);

            addSequencing(gridSequencing);
            addArray(gridArray);
            tabPanel.add(gridSequencing, "Sequencing");
            tabPanel.add(gridArray, "Array");
            add(tabPanel);
            tabPanel.selectTab(r == null || r.type == RequestOffer.SEQUENCING ? 0
                    : 1);

            addValidations();

        }
        catch (Exception e)
        {
            new ErrorBox(new Exception("Exception in RequestOfferUI.init()", e));
        }

    }

    private void addStatus(Grid g) throws Exception
    {
        int row = 0;
        addToGrid(g, row++, "Request ID", new Label(requestOfferId + ""));
        addToGrid(g, row++, "Billing Status", statusBilling);
        addToGrid(g, row++, "Sequencing Status", statusSequencing);
    }

    private void addPersonnel(Grid g, int startAtRow) throws Exception
    {
        int row = startAtRow;
        addToGrid(g, row++, "Principal Investigator", contactPI, addNewPI);
        addToGrid(g, row++, "Administrative/Accounts Payable", contactAdmin,
                addNewAdmin);
        addToGrid(g, row++, "Requestor", contactRequestor, addNewRequestor);
        addToGrid(
                g,
                row++,
                "Project",
                project,
                new Label(
                        "Note: If project is not on list please select 'other'. If you would like a new project added please email sequencing@igsb.org"));
    }

    private void addSequencing(Grid g)
    {
        int row = 0;
        addToGrid(g, row++, "Platform", seqPlatform);
        addToGrid(g, row++, "Number of Samples", seqNumSamples);
        addToGrid(g, row++, "Library Preparation Required", seqYesNo);

        addToGrid(g, row++, "Library Preparation Type", seqPreparationType,
                new Label(
                        "e.g. DNAseq, ChIP-seq, Paired-end, RNAseq, 16s, etc."));
        addToGrid(g, row++, "Lanes required per Sample", seqLanesRequired);
        addToGrid(g, row++, "Cycles required per Lane", seqCyclesRequired,
                new Label("e.g. 1x36 cycles, 2x100 cycles, 2x150 cycles"));

        addToGrid(g, row++, "Reference library to map output", seqReference,
                new Label("e.g. HG18_NCBI-build_36.1"));
        addToGrid(g, row++, "Comments", seqComments);
        addToGrid(g, row++, "", submitSequencing, closeSequencing);
    }

    private void addArray(Grid g)
    {
        int row = 0;
        addToGrid(g, row++, "Platform", arrPlatform);
        addToGrid(g, row++, "Number of Samples", arrNumSamples);
        addToGrid(g, row++, "Labeling Required", arrYesNo);
        addToGrid(g, row++, "Labeling Type", arrLabelingType, new Label(
                "e.g. 2-color GE, 1-color GE, etc"));
        addToGrid(g, row++, "Output Required per Sample", arrOutputRequired,
                new Label("e.g. Microarray 1x44K array"));
        addToGrid(g, row++, "Comments", arrComments);
        addToGrid(g, row++, "", submitArray, closeArray);

    }

    private void addToGrid(Grid g, int row, String labelText, Widget... widgets)
    {
        int c = 0;
        g.setWidget(row, c++, new Label(labelText));
        for (Widget w : widgets)
        {
            g.setWidget(row, c++, w);
        }
    }

    private void addValidations()
    {
        statusBilling.setFieldLabel("Billing Status");
        statusSequencing.setFieldLabel("Sequencing Status");
        contactPI.setFieldLabel("Principal Investigator");
        contactAdmin.setFieldLabel("Administrative/Accounts Payable Contact");
        contactRequestor.setFieldLabel("Requestor");
        project.setFieldLabel("Project");
        validationMain.add(statusBilling);
        validationMain.add(statusSequencing);
        validationMain.add(contactPI);
        validationMain.add(contactAdmin);
        validationMain.add(contactRequestor);
        validationMain.add(project);

        seqPlatform.setFieldLabel("Platform");
        seqNumSamples.setFieldLabel("Number of Samples");
        seqLanesRequired.setFieldLabel("Lanes required per Sample");
        seqCyclesRequired.setFieldLabel("Cycles required per Lane");
        seqPreparationType.setFieldLabel("Library Preparation Type");
        seqReference.setFieldLabel("Reference library to map output");
        validationSequencing.add(seqPlatform);
        validationSequencing.add(seqNumSamples);
        validationSequencing.add(seqPreparationType);
        validationSequencing.add(seqLanesRequired);
        validationSequencing.add(seqCyclesRequired);
        validationSequencing.add(seqReference);

        arrPlatform.setFieldLabel("Platform");
        arrNumSamples.setFieldLabel("Number of Samples");
        arrLabelingType.setFieldLabel("Labeling Type");
        arrOutputRequired.setFieldLabel("Output Required per Sample");
        validationArray.add(arrPlatform);
        validationArray.add(arrNumSamples);
        validationArray.add(arrLabelingType);
        validationArray.add(arrOutputRequired);
    }

    public boolean isValid(int requestType)
    {
        Vector<InputValidation> others = requestType == RequestOffer.SEQUENCING ? validationSequencing
                : validationArray;
        boolean valid = true;
        String messages = "Please enter data in the following fields <b>highlighted in yellow</b>:<br>";
        for (InputValidation v : validationMain)
        {
            if (!v.isValid())
            {
                valid = false;
                messages += "  " + v.getFieldLabel() + "<br>";
            }
        }
        for (InputValidation v : others)
        {
            if (!v.isValid())
            {
                valid = false;
                messages += "  " + v.getFieldLabel() + "<br>";
            }
        }
        if (!valid)
        {
            new ErrorBox("Incomplete data", messages, new Button("OK"));
        }
        return valid;
    }

    private void createUpdateRequestOffer(int type)
    {
        if (!isValid(type))
            return;

        AsyncCallback<RequestOffer> callback = new AsyncCallback<RequestOffer>()
        {
            public void onFailure(Throwable caught)
            {
                new ErrorBox(
                        new Exception(
                                "RequestOfferUI.createUpdateRequestOffer(), rpc onFailure()",
                                caught));
            }

            public void onSuccess(RequestOffer callbackResult)
            {
                try
                {
                    doSomethingOnSuccess(callbackResult);
                }
                catch (Exception e)
                {
                    new ErrorBox(
                            new Exception(
                                    "RequestOfferUI.createUpdateRequestOffer(), rpc onSuccess(), failure in doSomethingOnSuccess()",
                                    e));
                }
            }
        };

        try
        {
            CistrackUI.rpc.createUpdateRequestOffer(uiToRequestOffer(type),
                    callback);

        }
        catch (Exception e)
        {
            new ErrorBox(
                    new Exception(
                            "RequestOfferUI.createUpdateRequestOffer(), failure while initiating rpc OR in uiToRequestOffer()",
                            e));
        }
    }

    public void doSomethingOnSuccess(RequestOffer submittedRequest)
    {
        new ErrorBox("Success", submittedRequest.getDisplayString(),
                new Button("OK"));
    }

    private void requestOfferToUI(RequestOffer r)
    {
        requestOfferId = r.id;
        contactPI.setSelectedValue(r.idContactPI);
        contactAdmin.setSelectedValue(r.idContactAdmin);
        contactRequestor.setSelectedValue(r.idContactRequestor);
        project.setSelectedValue(r.projectId);

        statusBilling.setSelectedText(r.statusBilling);
        statusSequencing.setSelectedText(r.statusSequencing);

        if (r.type == RequestOffer.SEQUENCING)
        {
            seqPlatform.setSelectedText(r.platform);
            seqYesNo.setCheckedText(r.requiredPreparation);
            seqNumSamples.setText(r.numberOfSamples + "");
            seqPreparationType.setText(r.subType);
            seqLanesRequired.setText(r.seqLanesRequired);
            seqCyclesRequired.setText(r.seqCyclesRequired);
            seqReference.setText(r.reference);
            seqComments.setText(r.comments);
        }
        else
        {
            arrPlatform.setSelectedText(r.platform);
            arrYesNo.setCheckedText(r.requiredPreparation);
            arrNumSamples.setText(r.numberOfSamples + "");
            arrLabelingType.setText(r.subType);
            arrOutputRequired.setText(r.arrOutputPerSample);
            arrComments.setText(r.comments);
        }
    }

    private RequestOffer uiToRequestOffer(int type)
    {
        RequestOffer r = new RequestOffer();

        r.statusBilling = statusBilling.getSelectedText();
        r.statusSequencing = statusSequencing.getSelectedText();

        r.id = requestOfferId;
        r.idContactPI = contactPI.getSelectedValue();
        r.idContactAdmin = contactAdmin.getSelectedValue();
        r.idContactRequestor = contactRequestor.getSelectedValue();
        r.projectId = project.getSelectedValue();
        r.type = type;

        if (type == RequestOffer.SEQUENCING)
        {
            r.platform = seqPlatform.getSelectedText();
            r.requiredPreparation = seqYesNo.getCheckedText();
            r.numberOfSamples = seqNumSamples.getInt();
            r.subType = seqPreparationType.getText();
            r.seqLanesRequired = seqLanesRequired.getText().trim();
            r.seqCyclesRequired = seqCyclesRequired.getText().trim();
            r.reference = seqReference.getText().trim();
            r.comments = seqComments.getText().trim();
        }
        else
        {
            r.platform = arrPlatform.getSelectedText();
            r.requiredPreparation = arrYesNo.getCheckedText();
            r.numberOfSamples = arrNumSamples.getInt();
            r.subType = arrLabelingType.getText().trim();
            r.arrOutputPerSample = arrOutputRequired.getText().trim();
            r.comments = arrComments.getText().trim();
        }

        return r;
    }
}

class RequestOfferDialogBox extends RequestOfferUI // miss multiple inheritance
{
    DialogBox        dialog = null;
    ViewRequestOffer parent = null;

    public RequestOfferDialogBox(RequestOffer r, ViewRequestOffer parent)
    {
        super(r);
        this.parent = parent;
    }

    public void displayThisSomewhere()
    {
        dialog = new DialogBox();

        submitSequencing.setText("Update");
        submitArray.setText("Update");

        closeSequencing.setVisible(true);
        closeSequencing.addClickHandler(new ClickHandler()
        {
            public void onClick(ClickEvent ce)
            {
                dialog.hide();
            }
        });
        closeArray.setVisible(true);
        closeArray.addClickHandler(new ClickHandler()
        {
            public void onClick(ClickEvent ce)
            {
                dialog.hide();
            }
        });
        dialog.setText("Edit Request");

        dialog.setSize("650px", "500px");
        dialog.add(this);
        dialog.center();
        dialog.show();
    }

    public void doSomethingOnSuccess(RequestOffer submittedRequest)
    {
        dialog.hide();
        parent.refresh();
        dialog = null;
    }
}
