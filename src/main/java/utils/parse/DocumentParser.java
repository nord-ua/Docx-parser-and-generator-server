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
        List<ParsedPart> parsedParts = new Vector<ParsedPart>();

        parHolder.clear();
        updaters.clear();
        ignored.clear();

        XWPFParagraph prevParagraph;
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
            XWPFRun prevSegment;
            for (int characterRunNumber = 0; characterRunNumber<runs.size(); characterRunNumber++) {
                XWPFRun segment = runs.get(characterRunNumber);

                String runKey = paragraphNumber + ":" + characterRunNumber;

                String text = segment.toString();
                String highlight = "" + segment.getCTR().getRPr();
                System.out.println("Text: " + text);
                prevSegment = segment;

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
                        if (text == null)
                            text = "";

                        sbText.append(text);
                        break;
                }

                if (prev == Types.NONE)
                    prev = current;

                // An interesting part
                // One GREEN/RED block can be splitted by POI to several segments
                // So to collect parts correctly I use something like FSM
                if (current != prev || characterRunNumber == runs.size() - 1) {
                    // For every kind of segment we add corresponding widget to
                    // layout

                    if (prev == Types.TEXT && 0 != sbText.length()) {
                        parsedParts.add(new TextPart(prevKey, sbText.toString()));
                        sbText = new StringBuilder();
                    }

                    // RED block is represented with "LOAD DATA" (for date) and
                    // "BROWSE..." for image
                    if (prev == Types.BUTTON && 0 != sbButton.length()) {

                        String buttonsText = sbButton.toString();
                        if ("LOAD DATA".equals(buttonsText)) {
                            // TODO: add datepicker
//                            myParagraph.addTextSource(prevKey, new ButtonRangeUpdater("DATE"));
                            parsedParts.add(new DatePickerPart(prevKey));
                        } else if ("BROWSE".equals(buttonsText.substring(0, 6))) {
                            parsedParts.add(new ImagePart(prevKey));
//                            myParagraph.addTextSource(prevKey, new ImageRangeUpdater("image.png"));
                        }
                        sbButton = new StringBuilder();
                    }

                    // Edit texts for GREEN blocks
                    if (prev == Types.EDIT && 0 != sbEditText.length()) {
                        parsedParts.add(new EditPart(prevKey, sbEditText.toString()));
//                        myParagraph.addTextSource(prevKey, new EditRangeUpdater("EDIT"));
                        sbEditText = new StringBuilder();
                    }
                }

                prev = current;
                prevKey = runKey;
            }

            prevParagraph = pr;
        }

        return new ParsedData(parHolder, ignored, updaters, parsedParts);
    }

}


