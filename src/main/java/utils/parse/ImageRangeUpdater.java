package utils.parse;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileInputStream;

public class ImageRangeUpdater extends ParagraphRangeUpdater {

    private final File file;
    private final int w, h;
    private String filename;

    public ImageRangeUpdater(String filename, File file, int w, int h) {
        this.filename = filename;
        this.file = file;
        this.w = w;
        this.h = h;
    }
    
    public XWPFRun updateRange(XWPFRun range) {
        range.setText("", 0);
        range.getCTR().addNewRPr().addNewHighlight().setVal(org.openxmlformats.schemas.wordprocessingml.x2006.main.STHighlightColor.WHITE);

        try {
            range.addPicture(new FileInputStream(file), XWPFDocument.PICTURE_TYPE_PNG, filename, Units.toEMU(w), Units.toEMU(h));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return range;
    }

    @Override
    public String toString() {
        return "ButtonRangeUpdater [filename=" + filename + "]";
    }
    
}