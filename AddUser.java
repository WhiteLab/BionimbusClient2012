package org.lac.bionimbus.client;

import java.util.Vector;

import org.lac.bionimbus.shared.Personnel;
import org.lac.bionimbus.shared.SQLListBoxQuery;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class AddUser extends DialogBox
{
    //private UnitExperimentBar          bar            = new UnitExperimentBar();
    private final ClientInterfaceAsync rpc                = GWT.create(ClientInterface.class);
    private VerticalPanel              contentPanel;
    private int                        userType;
    private Button                     registerButton;
    private Widget                     parentUpdateWidget = null;

    public AddUser(int userType, boolean createLogin)
    {
        super(false);
        this.userType = userType;
        VerticalPanel basePanel = new VerticalPanel();
        VerticalPanel buttonPanel;
        HorizontalPanel horizontalButtonPanel;

        add(basePanel);
        basePanel.add(contentPanel = new VerticalPanel());
        basePanel.add(buttonPanel = new VerticalPanel());
        buttonPanel.add(horizontalButtonPanel = new HorizontalPanel());
        horizontalButtonPanel.add(registerButton = new Button("Register"));

        horizontalButtonPanel.add(new Button("Cancel", new ClickHandler()
        {
            public void onClick(ClickEvent e)
            {
                hide();
            }
        }));

        setText("Register new user");
        setPopupPositionAndShow(new PositionCallback()
        {
            public void setPosition(int offsetWidth, int offsetHeight)
            {
                setPopupPosition(offsetWidth, 2 * offsetHeight);
                show();
            }
        });

        draw(createLogin);
    }

    private void draw(final boolean createLogin)
    {
        String[] noLoginLabels = { "First Name : ", "Last Name : ",
                "Affiliation : ", "Address : ", "Email : ", "Phone : ",
                "Fax : ", "Website : ", "Role : " };

        String[] labels = { "First Name : ", "Last Name : ", "User Name : ",
                "Password : ", "Re-enter password", "Password must : ",
                "Affiliation : ", "Address : ", "Email : ", "Phone : ",
                "Fax : ", "Website : ", "Role : " };

        String passwordRules = "*Contain a minimum of 8 characters.* Not be based on personal information (e.g. birthdate, favorite team)"
                + "Not contain dictionary words.* Contain at least one upper case character, one lower case character, and one number";
        String[] roles = { "Tech", "Staff", "Student", "Investigator",
                "Primary Investigator" };

        Vector<Widget> widgets = new Vector<Widget>();
        final UserTextBox firstName = new UserTextBox(), lastName = new UserTextBox(), userName = new UserTextBox(), address = new UserTextBox(), email = new UserTextBox(), phone = new UserTextBox(), fax = new UserTextBox(), website = new UserTextBox();
        final PasswordTextBox password = new PasswordTextBox();
        final PasswordTextBox confirmPassword = new PasswordTextBox();
        Label passwordRestrictions = new Label(passwordRules);
        HorizontalPanel hp = new HorizontalPanel();
        hp.add(passwordRestrictions);
        hp.setWidth("250px");
        final SQLListBox affiliation = new SQLListBox(
                SQLListBoxQuery.items.UNIVERSITY_AFFILIATION);
        final ListBox role = new ListBox();

        for (int i = 0; i < roles.length; i++)
        {
            role.addItem(roles[i]);
        }

        widgets.add(firstName);
        widgets.add(lastName);
        if (createLogin)
        {
            widgets.add(userName);
            widgets.add(password);
            widgets.add(confirmPassword);
            widgets.add(hp);
        }
        else
        {
            userName.setText(Personnel.defaultUser);
            password.setText(Personnel.defaultPassword);
        }
        widgets.add(affiliation);
        widgets.add(address);
        widgets.add(email);
        widgets.add(phone);
        widgets.add(fax);
        widgets.add(website);
        widgets.add(role);

        Grid addUserGrid = new Grid(widgets.size(), 2);

        for (int i = 0; i < widgets.size(); i++)
        {
            if (createLogin)
                addUserGrid.setText(i, 0, labels[i]);
            else
                addUserGrid.setText(i, 0, noLoginLabels[i]);
            addUserGrid.setWidget(i, 1, widgets.get(i));
        }

        contentPanel.add(addUserGrid);

        final Vector<InputValidation> ip = new Vector<InputValidation>();
        firstName.setFieldLabel("Firstname");
        lastName.setFieldLabel("Lastname");
        email.setFieldLabel("Email");
        affiliation.setFieldLabel("Affiliation");
        if (createLogin)
        {
            userName.setFieldLabel("Username");
            ip.add(userName);
        }
        ip.add(firstName);
        ip.add(lastName);
        ip.add(email);
        ip.add(affiliation);

        registerButton.addClickHandler(new ClickHandler()
        {
            public void onClick(ClickEvent e)
            {
                if (!(verifyFormData(createLogin, password, confirmPassword,
                        userName.getText(), email.getText(), ip) > -1))
                    return;

                createUser(firstName.getText(), lastName.getText(),
                        userName.getText(), address.getText(), email.getText(),
                        phone.getText(), fax.getText(), website.getText(),
                        password.getText(), affiliation.getSelectedValue(),
                        role.getItemText(role.getSelectedIndex()));
            }
        });
    }

    private int verifyFormData(boolean createLogin, PasswordTextBox pwd,
            PasswordTextBox confirmPassword, String uName, String email,
            final Vector<InputValidation> inputValidations)
    {
        if (createLogin)
        {
            // Verify that password satisfies rules
            String password = pwd.getText();
            if (password.length() < 8)
            {
                new ErrorBox(
                        "Password should be at least 8 characters in length!");
                return -1;
            }

            if (!password.matches(".*[A-Z]+.*"))
            {
                new ErrorBox("Password must contain an upper case character!");
                return -1;
            }

            if (!password.matches(".*[a-z]+.*"))
            {
                new ErrorBox("Password must contain a lower case character!");
                return -1;
            }

            if (!password.matches(".*[0-9]+.*"))
            {
                new ErrorBox("Password must contain a number!");
                return -1;
            }
            if (!password.equals(confirmPassword.getText()))
            {
                new ErrorBox("Passwords do not match!");
                return -1;
            }
        }

        if (!email.matches(".*(@).*(\\.).*"))
        {
            new ErrorBox("Email is not valid");
            return -1;
        }

        boolean v = true;
        String validationMessage = "";

        for (InputValidation i : inputValidations)
        {
            if (!i.isValid())
            {
                v = false;
                if (!validationMessage.equals(""))
                    validationMessage += ", ";
                validationMessage += i.getFieldLabel();
            }
        }
        if (!v)
        {
            new ErrorBox("Please fill in the following required fields : "
                    + validationMessage);
            return -1;
        }

        return 0;
    }

    private void createUser(final String fName, final String lName,
            final String uName, final String address, final String email,
            final String phone, final String fax, final String website,
            final String password, final int affiliation, final String role)
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
                if (result > 0)
                {
                    new ErrorBox("Register new user",
                            "New user registered successfully ", new Button(
                                    "Ok"));
                    hide();
                    updateParentWidget(result.intValue());
                }
                else if (result == -1)
                {
                    new ErrorBox(
                            "Username already exists. Please choose a different username and try again.");
                }
            }
        };

        try
        {
            rpc.addUser(fName, lName, uName, address, email, phone, fax,
                    website, password, affiliation, role, userType, callback);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void addUpdateTarget(Widget parentUpdateWidget)
    {
        this.parentUpdateWidget = parentUpdateWidget;
    }

    private void updateParentWidget(int updateValue)
    {
        if (parentUpdateWidget != null)
        {
            if (parentUpdateWidget instanceof SQLListBox)
            {
                SQLListBox box = (SQLListBox) parentUpdateWidget;
                box.setSelectedValue(updateValue);
                box.refresh();
            }
        }
    }
}
