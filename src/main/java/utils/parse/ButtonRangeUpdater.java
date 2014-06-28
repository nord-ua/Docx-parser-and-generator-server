package utils.parse;

import org.apache.poi.xwpf.usermodel.XWPFRun;

public class ButtonRangeUpdater extends ParagraphRangeUpdater {
    
    private String button;

    public ButtonRangeUpdater(String button) {
        this.button = button;
    }
    
    public XWPFRun updateRange(XWPFRun range) {
        range.setText(button, 0);
        range.getCTR().addNewRPr().addNewHighlight().setVal(org.openxmlformats.schemas.wordprocessingml.x2006.main.STHighlightColor.WHITE);
        return range;
    }

    @Override
    public String toString() {
        return "ButtonRangeUpdater [button=" + button + "]";
    }
    
}