/**
 * 
 */
package jmri.jmrix.hsi88;

/**
 * @author gruening
 *
 */
public interface Hsi88ReplyListener {
    void notifyReply(int event, int payload);
}