package com.cryptography.utils;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.DataFlavor;
import javax.swing.JOptionPane;
import javax.swing.JComponent;

public class ClipboardUtils {
    public static void copyToClipboard(String text, String successMessage, JComponent parent) {
        if (!text.isEmpty()) {
            StringSelection selection = new StringSelection(text);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
            
            JOptionPane.showMessageDialog(
                parent,
                successMessage,
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    public static String getFromClipboard() throws Exception {
        return (String) Toolkit.getDefaultToolkit()
            .getSystemClipboard().getData(DataFlavor.stringFlavor);
    }
} 