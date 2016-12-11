package jmri.jmrix.hsi88.swing.monitor;

import jmri.jmrix.hsi88.Hsi88Constants;
import jmri.jmrix.hsi88.Hsi88Reply;

/**
 * class to convert Hsi88 Can bus messages to a human readable form
 */
public class Hsi88Mon {

    public static String displayReply(Hsi88Reply r) {

        StringBuffer sb = new StringBuffer();
        sb.append("Priority ");
        switch (r.getPriority()) {
            case Hsi88Constants.PRIO_1:
                sb.append("1, Stop/Go/Short");
                break;
            case Hsi88Constants.PRIO_2:
                sb.append("2, Feedback");
                break;
            case Hsi88Constants.PRIO_3:
                sb.append("3, Engine Stop");
                break;
            case Hsi88Constants.PRIO_4:
                sb.append("4, Engine/accessory command");
                break;
            default:
                sb.append("Unknown");
        }
        sb.append(" Command: ");
        int command = r.getCommand();
        if (command == Hsi88Constants.SYSCOMMANDSTART) {
            sb.append("System");
        } else if (command >= Hsi88Constants.MANCOMMANDSTART && command <= Hsi88Constants.MANCOMMANDEND) {
            switch (r.getCommand()) {
                case Hsi88Constants.LOCODIRECTION:
                    sb.append("Change of direction " + r.getElement(9));
                    break;
                case Hsi88Constants.LOCOSPEED:
                    sb.append("Change of speed " + ((r.getElement(9) & 0xff << 8) + (r.getElement(10) & 0xff)));
                    break;
                case Hsi88Constants.LOCOFUNCTION:
                    sb.append("Function: " + r.getElement(9) + " state: " + r.getElement(10));
                    break;
                default:
                    sb.append("Management");
            }
        } else if (command >= Hsi88Constants.ACCCOMMANDSTART && command <= Hsi88Constants.ACCCOMMANDEND) {
            sb.append("Accessory");
            switch (r.getElement(9)) {
                case 0x00:
                    sb.append("Set Thrown");
                    break;
                case 0x01:
                    sb.append("Set Closed");
                    break;
                default:
                    sb.append("Unknown state command " + r.getElement(9));
            }
        } else if (command >= Hsi88Constants.SOFCOMMANDSTART && command <= Hsi88Constants.SOFCOMMANDEND) {
            sb.append("Software");
        } else if (command >= Hsi88Constants.GUICOMMANDSTART && command <= Hsi88Constants.GUICOMMANDEND) {
            sb.append("GUI");
        } else if (command >= Hsi88Constants.AUTCOMMANDSTART && command <= Hsi88Constants.AUTCOMMANDEND) {
            sb.append("Automation");
        } else if (command >= Hsi88Constants.FEECOMMANDSTART && command <= Hsi88Constants.FEECOMMANDEND) {
            sb.append("Feedback");
        }
        if (r.isResponse()) {
            sb.append(" Reply");
        } else {
            sb.append(" Request Message");
        }
        long addr = r.getAddress();
        if (addr >= Hsi88Constants.MM1START && addr <= Hsi88Constants.MM1END) {
            if (addr == 0) {
                sb.append(" Broadcast");
            } else {
                sb.append(" to MM Loco Address " + addr);
            }
        } else if (addr >= Hsi88Constants.MM1FUNCTSTART && addr <= Hsi88Constants.MM1FUNCTEND) {
            addr = addr - Hsi88Constants.MM1FUNCTSTART;
            sb.append(" to MM Function decoder " + addr);
        } else if (addr >= Hsi88Constants.MM1LOCOSTART && addr <= Hsi88Constants.MM1LOCOEND) {
            addr = addr - Hsi88Constants.MM1LOCOSTART;
            sb.append(" to MM Loco Address " + addr);
        } else if (addr >= Hsi88Constants.SX1START && addr <= Hsi88Constants.SX1END) {
            addr = addr - Hsi88Constants.SX1START;
            sb.append(" to SX Address " + addr);
        } else if (addr >= Hsi88Constants.SX1ACCSTART && addr <= Hsi88Constants.SX1ACCEND) {
            addr = addr - Hsi88Constants.SX1ACCSTART;
            sb.append(" to SX Accessory Address " + addr);
        } else if (addr >= Hsi88Constants.MM1ACCSTART && addr <= Hsi88Constants.MM1ACCEND) {
            addr = addr - Hsi88Constants.MM1ACCSTART;
            sb.append(" to MM Accessory Address " + addr);
        } else if (addr >= Hsi88Constants.DCCACCSTART && addr <= Hsi88Constants.DCCACCEND) {
            addr = addr - Hsi88Constants.DCCACCSTART;
            sb.append(" to DCC Accessory Address " + addr);
        } else if (addr >= Hsi88Constants.MFXSTART && addr <= Hsi88Constants.MFXEND) {
            addr = addr - Hsi88Constants.MFXSTART;
            sb.append(" to MFX Address " + addr);
        } else if (addr >= Hsi88Constants.SX2START && addr <= Hsi88Constants.SX2END) {
            addr = addr - Hsi88Constants.SX2START;
            sb.append(" to SX2 Address " + addr);
        } else if (addr >= Hsi88Constants.DCCSTART && addr <= Hsi88Constants.DCCEND) {
            addr = addr - Hsi88Constants.DCCSTART;
            sb.append(" to DCC Address " + addr);
        }
        //StringBuffer buf = new StringBuffer();
        sb.append("0x" + Integer.toHexString(r.getCanData()[0]));
        for (int i = 1; i < r.getCanData().length; i++) {
            sb.append(", 0x" + Integer.toHexString(r.getCanData()[i]));
        }

        return sb.toString();
    }

}
