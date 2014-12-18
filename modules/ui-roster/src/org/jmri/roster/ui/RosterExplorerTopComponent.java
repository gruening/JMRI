/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jmri.roster.ui;

import java.util.Locale;
import javax.swing.GroupLayout;
import jmri.jmrit.roster.Roster;
import org.jmri.roster.AllRosterEntries;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//org.jmri.roster.ui//Roster//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "RosterTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "explorer", openAtStartup = true)
@ActionID(category = "Roster", id = "org.jmri.roster.ui.RosterTopComponent")
@ActionReference(path = "Menu/Roster" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_RosterAction",
        preferredID = "RosterTopComponent"
)
@Messages({
    "CTL_RosterAction=Roster",
    "CTL_RosterTopComponent=Roster",
    "HINT_RosterTopComponent=This is a Roster window"
})
public final class RosterExplorerTopComponent extends TopComponent implements ExplorerManager.Provider {

    private final ExplorerManager explorerManager = new ExplorerManager();

    public RosterExplorerTopComponent() {
        initComponents();
        setName(Bundle.CTL_RosterTopComponent());
        setToolTipText(Bundle.HINT_RosterTopComponent());
        Children groups = Children.create(new RosterObjectFactory(new AllRosterEntries()), true);
        Node rootNode = new AbstractNode(groups);
        rootNode.setDisplayName(Roster.AllEntries(Locale.getDefault()));
        explorerManager.setRootContext(rootNode);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        beanTreeView1 = new BeanTreeView();

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(beanTreeView1, GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(beanTreeView1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private BeanTreeView beanTreeView1;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return this.explorerManager;
    }
}
