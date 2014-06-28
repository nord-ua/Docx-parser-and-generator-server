package utils.parse;

import org.apache.poi.xwpf.usermodel.XWPFRun;

public class EditRangeUpdater extends ParagraphRangeUpdater {
    
    private String editText;

    public EditRangeUpdater(String editText) {
        this.editText = editText;
    }
    
    public XWPFRun updateRange(XWPFRun range) {
//        range.setHighlighted((byte) 0);
        range.setText(editText, 0);
        range.getCTR().addNewRPr().addNewHighlight().setVal(org.openxmlformats.schemas.wordprocessingml.x2006.main.STHighlightColor.WHITE);
        return range;
    }


    @Override
    public String toString() {
        return "EditRangeUpdater [editText=" + editText + "]";
    }
    
}