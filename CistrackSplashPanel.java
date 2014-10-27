package org.lac.bionimbus.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CistrackSplashPanel extends VerticalPanel
{

    public CistrackSplashPanel(final Command publicDataCommand,
            final Command loginCommand)
    {

        HTML blurb = new HTML(
                "<b>Bionimbus is an open source cloud-based system for managing, analyzing and sharing genomic data "
                        + "that has been developed by the Institute for Genomics and Systems Biology (IGSB) at the University of Chicago.<br>"
                        + "<P>Bionimbus is designed to support next-generation sequencing instruments and integrates technology for the analyzing and transporting large datasets.<br>"
                        + "Bionimbus is supported through grants from the National Institute of Health award P50 081892, "
                        + "Chicago Biomedical Consortium Lever award, and the National Science Foundation awards 1129076 and 1127316.</b>");

        this.add(blurb);
        this.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);

        HorizontalPanel options = new HorizontalPanel();

        options.add(new Button("View public data", new ClickHandler()
        {
            public void onClick(ClickEvent e)
            {
                publicDataCommand.execute();
            }
        }));

        options.add(new Button(CistrackUI.loginText, new ClickHandler()
        {
            public void onClick(ClickEvent e)
            {
                loginCommand.execute();
            }
        }));

        this.add(options);

        //RootPanel.get().add(this);  // when used as basePanel in CistrackUI

    }
}
