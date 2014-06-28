package utils.parse;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import utils.parts.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;


public class DocumentParser {

    enum Types {
        NONE, TEXT, EDIT, BUTTON
    }

    public static ParsedData parseDocument(File file) throws Exception {
        FileInputStream is = new FileInputStream(file);
        XWPFDocument doc = new XWPFDocument(is);

        List<XWPFParagraph> paragraphs = doc.getParagraphs();

        List<MyParagraph> parHolder = new Vector<MyParagraph>();
        List<Object> ignored = new Vector<Object>();
        Map<Object, ParagraphRangeUpdater> updaters = new HashMap<Object, ParagraphRangeUpdater>();
        Map<String, ParsedPart> parsedParts = new HashMap<String, ParsedPart>();

        parHolder.clear();
        updaters.clear();
        ignored.clear();

        MyParagraph myParagraph;
        for (int paragraphNumber = 0; paragraphNumber < paragraphs.size(); paragraphNumber++) {
            XWPFParagraph pr = paragraphs.get(paragraphNumber);

//            myParagraph = new MyParagraph();
//            parHolder.add(myParagraph);

            StringBuilder sbText = new StringBuilder();
            StringBuilder sbEditText = new StringBuilder();
            StringBuilder sbButton = new StringBuilder();

            Types prev = Types.NONE;
            Types current;

            String prevKey = null;
            List<XWPFRun> runs = pr.getRuns();
            for (int characterRunNumber = 0; characterRunNumber<runs.size(); characterRunNumber++) {
                XWPFRun segment = runs.get(characterRunNumber);

                String runKey = paragraphNumber + ":" + characterRunNumber;

                String text = segment.getText(segment.getTextPosition());
                String highlight = "" + segment.getCTR().getRPr();
                System.out.println("Text: " + text); // segment.getCTR().getRPr()
                int color = 0;
                if (highlight.contains("w:fill=\"FF0000")) {
                    color = 6;
                } else if (highlight.contains("w:fill=\"00FF00")) {
                    color = 4;
                }

                switch (color) {
                    case 6:
                        current = Types.BUTTON;
                        ignored.add(runKey);
                        sbButton.append(text);
                        break;
                    case 4:
                        current = Types.EDIT;
                        ignored.add(runKey);
                        sbEditText.append(text);
                        break;
                    default:
                        current = Types.TEXT;
                        if (text != null)
                            sbText.append(text);
                        break;
                }

                // An interesting part
                // One GREEN/RED block can be splitted by POI to several segments
                // So to collect parts correctly I use something like FSM
                if ((prev != Types.NONE && current != prev) || characterRunNumber == runs.size() - 1) {
                    // For every kind of segment we add corresponding widget to
                    // layout

                    if (prev == Types.TEXT && 0 != sbText.length()) {
                        parsedParts.put(prevKey, new TextPart(prevKey, sbText.toString()));
                        sbText = new StringBuilder();
                    }

                    // RED block is represented with "LOAD DATA" (for date) and
                    // "BROWSE..." for image
                    if (prev == Types.BUTTON && 0 != sbButton.length()) {

                        String buttonsText = sbButton.toString();
                        if ("LOAD DATA".equals(buttonsText)) {
                            // TODO: add datepicker
//                            myParagraph.addTextSource(prevKey, new ButtonRangeUpdater("DATE"));
                            parsedParts.put(prevKey, new DatePickerPart(prevKey));
                        } else if ("BROWSE".equals(buttonsText.substring(0, 6))) {
                            parsedParts.put(prevKey, new ImagePart(prevKey));
//                            myParagraph.addTextSource(prevKey, new ImageRangeUpdater("image.png"));
                        }
                        sbButton = new StringBuilder();
                    }

                    // Edit texts for GREEN blocks
                    if (prev == Types.EDIT && 0 != sbEditText.length()) {
                        parsedParts.put(prevKey, new EditPart(prevKey, sbEditText.toString()));
//                        myParagraph.addTextSource(prevKey, new EditRangeUpdater("EDIT"));
                        sbEditText = new StringBuilder();
                    }
                }

                prev = current;
                prevKey = runKey;
            }
        }

        return new ParsedData(parHolder, ignored, updaters, parsedParts);
    }

}


