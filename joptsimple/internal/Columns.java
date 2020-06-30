package joptsimple.internal;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import joptsimple.internal.Row;
import joptsimple.internal.Strings;

class Columns {
   private static final int INDENT_WIDTH = 2;
   private final int optionWidth;
   private final int descriptionWidth;

   Columns(int optionWidth, int descriptionWidth) {
      this.optionWidth = optionWidth;
      this.descriptionWidth = descriptionWidth;
   }

   List fit(Row row) {
      List<String> options = this.piecesOf(row.option, this.optionWidth);
      List<String> descriptions = this.piecesOf(row.description, this.descriptionWidth);
      List<Row> rows = new ArrayList();

      for(int i = 0; i < Math.max(options.size(), descriptions.size()); ++i) {
         rows.add(new Row(itemOrEmpty(options, i), itemOrEmpty(descriptions, i)));
      }

      return rows;
   }

   private static String itemOrEmpty(List items, int index) {
      return index >= items.size()?"":(String)items.get(index);
   }

   private List piecesOf(String raw, int width) {
      List<String> pieces = new ArrayList();

      for(String each : raw.trim().split(Strings.LINE_SEPARATOR)) {
         pieces.addAll(this.piecesOfEmbeddedLine(each, width));
      }

      return pieces;
   }

   private List piecesOfEmbeddedLine(String line, int width) {
      List<String> pieces = new ArrayList();
      BreakIterator words = BreakIterator.getLineInstance(Locale.US);
      words.setText(line);
      StringBuilder nextPiece = new StringBuilder();
      int start = words.first();

      for(int end = words.next(); end != -1; end = words.next()) {
         nextPiece = this.processNextWord(line, nextPiece, start, end, width, pieces);
         start = end;
      }

      if(nextPiece.length() > 0) {
         pieces.add(nextPiece.toString());
      }

      return pieces;
   }

   private StringBuilder processNextWord(String source, StringBuilder nextPiece, int start, int end, int width, List pieces) {
      StringBuilder augmented = nextPiece;
      String word = source.substring(start, end);
      if(nextPiece.length() + word.length() > width) {
         pieces.add(nextPiece.toString().replaceAll("\\s+$", ""));
         augmented = (new StringBuilder(Strings.repeat(' ', 2))).append(word);
      } else {
         nextPiece.append(word);
      }

      return augmented;
   }
}
