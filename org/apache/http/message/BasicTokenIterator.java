package org.apache.http.message;

import java.util.NoSuchElementException;
import org.apache.http.HeaderIterator;
import org.apache.http.ParseException;
import org.apache.http.TokenIterator;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.util.Args;

@NotThreadSafe
public class BasicTokenIterator implements TokenIterator {
   public static final String HTTP_SEPARATORS = " ,;=()<>@:\\\"/[]?{}\t";
   protected final HeaderIterator headerIt;
   protected String currentHeader;
   protected String currentToken;
   protected int searchPos;

   public BasicTokenIterator(HeaderIterator headerIterator) {
      this.headerIt = (HeaderIterator)Args.notNull(headerIterator, "Header iterator");
      this.searchPos = this.findNext(-1);
   }

   public boolean hasNext() {
      return this.currentToken != null;
   }

   public String nextToken() throws NoSuchElementException, ParseException {
      if(this.currentToken == null) {
         throw new NoSuchElementException("Iteration already finished.");
      } else {
         String result = this.currentToken;
         this.searchPos = this.findNext(this.searchPos);
         return result;
      }
   }

   public final Object next() throws NoSuchElementException, ParseException {
      return this.nextToken();
   }

   public final void remove() throws UnsupportedOperationException {
      throw new UnsupportedOperationException("Removing tokens is not supported.");
   }

   protected int findNext(int pos) throws ParseException {
      int from;
      if(pos < 0) {
         if(!this.headerIt.hasNext()) {
            return -1;
         }

         this.currentHeader = this.headerIt.nextHeader().getValue();
         from = 0;
      } else {
         from = this.findTokenSeparator(pos);
      }

      int start = this.findTokenStart(from);
      if(start < 0) {
         this.currentToken = null;
         return -1;
      } else {
         int end = this.findTokenEnd(start);
         this.currentToken = this.createToken(this.currentHeader, start, end);
         return end;
      }
   }

   protected String createToken(String value, int start, int end) {
      return value.substring(start, end);
   }

   protected int findTokenStart(int pos) {
      int from = Args.notNegative(pos, "Search position");
      boolean found = false;

      while(!found && this.currentHeader != null) {
         int to = this.currentHeader.length();

         while(!found && from < to) {
            char ch = this.currentHeader.charAt(from);
            if(!this.isTokenSeparator(ch) && !this.isWhitespace(ch)) {
               if(!this.isTokenChar(this.currentHeader.charAt(from))) {
                  throw new ParseException("Invalid character before token (pos " + from + "): " + this.currentHeader);
               }

               found = true;
            } else {
               ++from;
            }
         }

         if(!found) {
            if(this.headerIt.hasNext()) {
               this.currentHeader = this.headerIt.nextHeader().getValue();
               from = 0;
            } else {
               this.currentHeader = null;
            }
         }
      }

      return found?from:-1;
   }

   protected int findTokenSeparator(int pos) {
      int from = Args.notNegative(pos, "Search position");
      boolean found = false;
      int to = this.currentHeader.length();

      while(!found && from < to) {
         char ch = this.currentHeader.charAt(from);
         if(this.isTokenSeparator(ch)) {
            found = true;
         } else {
            if(!this.isWhitespace(ch)) {
               if(this.isTokenChar(ch)) {
                  throw new ParseException("Tokens without separator (pos " + from + "): " + this.currentHeader);
               }

               throw new ParseException("Invalid character after token (pos " + from + "): " + this.currentHeader);
            }

            ++from;
         }
      }

      return from;
   }

   protected int findTokenEnd(int from) {
      Args.notNegative(from, "Search position");
      int to = this.currentHeader.length();

      int end;
      for(end = from + 1; end < to && this.isTokenChar(this.currentHeader.charAt(end)); ++end) {
         ;
      }

      return end;
   }

   protected boolean isTokenSeparator(char ch) {
      return ch == 44;
   }

   protected boolean isWhitespace(char ch) {
      return ch == 9 || Character.isSpaceChar(ch);
   }

   protected boolean isTokenChar(char ch) {
      return Character.isLetterOrDigit(ch)?true:(Character.isISOControl(ch)?false:!this.isHttpSeparator(ch));
   }

   protected boolean isHttpSeparator(char ch) {
      return " ,;=()<>@:\\\"/[]?{}\t".indexOf(ch) >= 0;
   }
}
