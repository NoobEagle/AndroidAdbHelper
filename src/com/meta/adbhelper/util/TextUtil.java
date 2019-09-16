package com.meta.adbhelper.util;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;

public class TextUtil {
    public static boolean isEmpty(String param) {
        return param == null || param.equals("");
    }

    /**
     * 将字符串复制到剪切板。
     */
    public static void setSysClipboardText(String writeMe) {
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tText = new StringSelection(writeMe);
        clip.setContents(tText, null);
    }

    public static String getSystemClipboard() {
        //获取系统剪切板的文本内容[如果系统剪切板复制的内容是文本]
        Clipboard sysClb = null;
        sysClb = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable t = sysClb.getContents(null); //获取剪切板的内容，不存在则返回null
        //Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null); //跟上面三行代码一样
        try {

            //如果剪切板的内容存在并且该内容的类型为文本类型
            if (null != t && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String) t.getTransferData(DataFlavor.stringFlavor);
            }
        } catch (UnsupportedFlavorException | IOException e) {
            //System.out.println("Error tip: "+e.getMessage());
        }
        return null;
    }
}
