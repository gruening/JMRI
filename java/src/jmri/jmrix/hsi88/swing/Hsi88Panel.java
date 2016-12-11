// Hsi88Panel.java
package jmri.jmrix.hsi88.swing;

import jmri.jmrix.hsi88.Hsi88SystemConnectionMemo;

/**
 * JPanel extension to handle automatic creation of window title and help
 * reference for Hsi88 panels
 * <p>
 * For use with JmriAbstractAction, etc
 *
 * @author Bob Jacobsen Copyright 2010
 * @since 2.11.3
 */
abstract public class Hsi88Panel extends jmri.util.swing.JmriPanel implements Hsi88PanelInterface {

    /**
     *
     */
    private static final long serialVersionUID = -997775203894632617L;
    /**
     * make "memo" object available as convenience
     */
    protected Hsi88SystemConnectionMemo memo;

    public void initComponents(Hsi88SystemConnectionMemo memo) {
        this.memo = memo;
    }

    @Override
    public void initContext(Object context) {
        if (context instanceof Hsi88SystemConnectionMemo) {
            initComponents((Hsi88SystemConnectionMemo) context);
        }
    }

}
