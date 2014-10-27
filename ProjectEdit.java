package org.lac.bionimbus.client;

//import org.lac.bionimbus.server.Projects;
import java.util.Vector;

import org.lac.bionimbus.shared.BNPermissions.PermissionsEntity;
import org.lac.bionimbus.shared.BNPermissions.RW;
import org.lac.bionimbus.shared.Group;
import org.lac.bionimbus.shared.Project;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;

public class ProjectEdit extends AbsolutePanel
{

    private final ClientInterfaceAsync rpc = GWT.create(ClientInterface.class);

    ListBox                            subProjectListBox;
    ListBox                            projectListBox;

    Vector<Project>                    projects;
    Vector<Project>                    subProjects;
    ListBox                            userListbox;
    ListBox                            adminListbox;
    Button                             btnAddUser;
    Button                             btnAddAdmin;

    int                                subProjectId;

    void clearSubProject()
    {
        userListbox.clear();
        adminListbox.clear();
        subProjectListBox.clear();
        btnAddUser.setEnabled(false);
        btnAddAdmin.setEnabled(false);
    }

    ProjectEdit()
    {
        setSize("600px", "480px");

        projectListBox = new ListBox();
        add(projectListBox, 10, 80);
        projectListBox.setSize("153px", "134px");
        projectListBox.setVisibleItemCount(5);
        projectListBox.addClickHandler(new ClickHandler()
        {

            public void onClick(ClickEvent e)
            {
                clearSubProject();
                int index = projectListBox.getSelectedIndex();
                Project p = projects.elementAt(index);
                subProjects = p.subprojects;
                for (Project sp : subProjects)
                {
                    subProjectListBox.addItem(sp.name);
                }
            }
        });

        Label lblNewLabel = new Label("Projects");
        add(lblNewLabel, 10, 48);
        lblNewLabel.setSize("153px", "26px");

        subProjectListBox = new ListBox();
        add(subProjectListBox, 193, 80);
        subProjectListBox.setSize("149px", "134px");
        subProjectListBox.setVisibleItemCount(5);

        Label lblSubprojects = new Label("Subprojects");
        add(lblSubprojects, 193, 50);
        lblSubprojects.setSize("149px", "24px");

        Button btnNewButton = new Button("New Subproject");
        add(btnNewButton, 193, 10);
        btnNewButton.setSize("149px", "24px");

        userListbox = new ListBox();
        add(userListbox, 381, 80);
        userListbox.setSize("193px", "94px");
        userListbox.setVisibleItemCount(5);

        Label lblNewLabel_1 = new Label("Admins");
        add(lblNewLabel_1, 381, 48);
        lblNewLabel_1.setSize("193px", "26px");

        adminListbox = new ListBox();
        add(adminListbox, 381, 239);
        adminListbox.setSize("189px", "130px");
        adminListbox.setVisibleItemCount(5);

        Label lblUsers = new Label("Users");
        add(lblUsers, 379, 209);
        lblUsers.setSize("195px", "24px");

        ListBox listBox_4 = new ListBox();
        add(listBox_4, 10, 239);
        listBox_4.setSize("332px", "130px");
        listBox_4.setVisibleItemCount(5);

        RootPanel rootPanel = RootPanel.get();
        rootPanel.add(this);

        btnAddAdmin = new Button("Add Admin");
        add(btnAddAdmin, 478, 44);

        btnAddUser = new Button("Add User");
        add(btnAddUser, 475, 207);
        btnAddUser.addClickHandler(new ClickHandler()
        {
            public void onClick(ClickEvent event)
            {
                new EditPermissions(subProjectId, PermissionsEntity.Project,
                        RW.Read, "Setting Users for sub project ");
            }
        });

        Button newProjectButton = new Button("New Project");
        newProjectButton.addClickHandler(new ClickHandler()
        {
            public void onClick(ClickEvent event)
            {
                makeNewProject();
            }
        });
        add(newProjectButton, 10, 10);
        newProjectButton.setSize("131px", "24px");

        subProjectListBox.addClickHandler(new ClickHandler()
        {

            public void onClick(ClickEvent e)
            {
                int index = subProjectListBox.getSelectedIndex();
                //new ErrorBox("Index : " + index);
                Project p = subProjects.elementAt(index);

                subProjectId = p.id;
                //new ErrorBox("r,w:" + p.readers + "," + p.writers);

                fillUsers(userListbox, p.readers);
                fillUsers(adminListbox, p.writers);

                btnAddUser.setEnabled(true);
                btnAddAdmin.setEnabled(true);
            }
        });

        fetchProjects();
    }

    void fillUsers(ListBox lb, Vector<Group> groups)
    {
        if (groups == null)
        {
            new ErrorBox("Groups is null");
            return;
        }
        lb.clear();
        for (Group g : groups)
        {
            lb.addItem(g.name);
        }
    }

    void fetchProjects()
    {
        AsyncCallback<Vector<Project>> callback = new AsyncCallback<Vector<Project>>()
        {
            public void onFailure(Throwable e)
            {
                new ErrorBox("peachey " + e);
            }

            public void onSuccess(Vector<Project> _projects)
            {
                projects = _projects;
                subProjectListBox.clear();

                for (Project p : projects)
                {
                    projectListBox.addItem(p.name);
                }
            }
        };

        try
        {
            rpc.getProjects(callback);
        }
        catch (Exception e)
        {
            new ErrorBox(e);
        }
    }

    void makeNewProject()
    {

    }
}
