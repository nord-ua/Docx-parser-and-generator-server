package utils.parse;

import org.apache.poi.xwpf.usermodel.XWPFRun;

abstract public class ParagraphRangeUpdater {

    public abstract XWPFRun updateRange(XWPFRun range);
    
}