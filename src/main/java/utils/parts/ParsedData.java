package utils.parts;

import utils.parse.MyParagraph;
import utils.parse.ParagraphRangeUpdater;

import java.util.List;
import java.util.Map;

public class ParsedData {
    final public List<MyParagraph> parHolder;
    final public List<Object> ignored;
    final public Map<Object, ParagraphRangeUpdater> updaters;
    final public Map<String, ParsedPart> parsedParts;

    public ParsedData(List<MyParagraph> parHolder, List<Object> ignored, Map<Object, ParagraphRangeUpdater> updaters, Map<String, ParsedPart> parsedParts) {
        this.parHolder = parHolder;
        this.ignored = ignored;
        this.updaters = updaters;
        this.parsedParts = parsedParts;
    }
}
