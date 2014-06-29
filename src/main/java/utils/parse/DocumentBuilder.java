package utils.parse;


import com.oreilly.servlet.MultipartRequest;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import utils.ClientData;
import utils.parts.ParsedData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

public class DocumentBuilder {

    private final MultipartRequest req;

    public DocumentBuilder(MultipartRequest req) {
        this.req = req;
    }

    public File buildDocument(File file, ParsedData parsedData, ClientData data) throws Exception {
        FileInputStream is = new FileInputStream(file);
        XWPFDocument doc = new XWPFDocument(is);

        List<XWPFParagraph> paragraphs = doc.getParagraphs();

        for (int paragraphNumber = 0; paragraphNumber < paragraphs.size(); paragraphNumber++) {
            XWPFParagraph pr = paragraphs.get(paragraphNumber);
//            MyParagraph myParagraph = parsedData.parHolder.get(paragraphNumber);

            List<XWPFRun> runs = pr.getRuns();
            for (int characterRunNumber = 0; characterRunNumber < runs.size(); characterRunNumber++) {
                XWPFRun segment = runs.get(characterRunNumber);

                String runKey = paragraphNumber + ":" + characterRunNumber;

                if (data.data.containsKey(runKey)) {
                    ClientData.Item item = data.item(runKey);
                    switch (item.type) {//TODO: merge
                        case EDIT:
                            new EditRangeUpdater(item.content).updateRange(segment);
                            break;
                        case DATE:
                            // Same as edit
                            new EditRangeUpdater(item.content).updateRange(segment);
                            break;
                        case IMAGE:
                            new ImageRangeUpdater(item.content, getImageFile(item.content), item.w, item.h).updateRange(segment);
                            break;
                    }
                } else if (parsedData.ignored.contains(runKey)) {
                    segment.setText("", 0);
                }
            }
        }

        File tempFile = File.createTempFile("random", "file");
        FileOutputStream stream = new FileOutputStream(tempFile);
        doc.write(stream);
        stream.close();

        return tempFile;
    }

    private File getImageFile(String content) {
        return req.getFile(content);
    }
}
