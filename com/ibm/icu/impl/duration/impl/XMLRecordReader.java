package com.ibm.icu.impl.duration.impl;

import com.ibm.icu.impl.duration.impl.RecordReader;
import com.ibm.icu.lang.UCharacter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class XMLRecordReader implements RecordReader {
   private Reader r;
   private List nameStack;
   private boolean atTag;
   private String tag;

   public XMLRecordReader(Reader r) {
      this.r = r;
      this.nameStack = new ArrayList();
      if(this.getTag().startsWith("?xml")) {
         this.advance();
      }

      if(this.getTag().startsWith("!--")) {
         this.advance();
      }

   }

   public boolean open(String title) {
      if(this.getTag().equals(title)) {
         this.nameStack.add(title);
         this.advance();
         return true;
      } else {
         return false;
      }
   }

   public boolean close() {
      int ix = this.nameStack.size() - 1;
      String name = (String)this.nameStack.get(ix);
      if(this.getTag().equals("/" + name)) {
         this.nameStack.remove(ix);
         this.advance();
         return true;
      } else {
         return false;
      }
   }

   public boolean bool(String name) {
      String s = this.string(name);
      return s != null?"true".equals(s):false;
   }

   public boolean[] boolArray(String name) {
      String[] sa = this.stringArray(name);
      if(sa == null) {
         return null;
      } else {
         boolean[] result = new boolean[sa.length];

         for(int i = 0; i < sa.length; ++i) {
            result[i] = "true".equals(sa[i]);
         }

         return result;
      }
   }

   public char character(String name) {
      String s = this.string(name);
      return s != null?s.charAt(0):'\uffff';
   }

   public char[] characterArray(String name) {
      String[] sa = this.stringArray(name);
      if(sa == null) {
         return null;
      } else {
         char[] result = new char[sa.length];

         for(int i = 0; i < sa.length; ++i) {
            result[i] = sa[i].charAt(0);
         }

         return result;
      }
   }

   public byte namedIndex(String name, String[] names) {
      String sa = this.string(name);
      if(sa != null) {
         for(int i = 0; i < names.length; ++i) {
            if(sa.equals(names[i])) {
               return (byte)i;
            }
         }
      }

      return (byte)-1;
   }

   public byte[] namedIndexArray(String name, String[] names) {
      String[] sa = this.stringArray(name);
      if(sa == null) {
         return null;
      } else {
         byte[] result = new byte[sa.length];

         for(int i = 0; i < sa.length; ++i) {
            String s = sa[i];
            int j = 0;

            while(true) {
               if(j >= names.length) {
                  result[i] = -1;
                  break;
               }

               if(names[j].equals(s)) {
                  result[i] = (byte)j;
                  break;
               }

               ++j;
            }
         }

         return result;
      }
   }

   public String string(String name) {
      if(this.match(name)) {
         String result = this.readData();
         if(this.match("/" + name)) {
            return result;
         }
      }

      return null;
   }

   public String[] stringArray(String name) {
      if(this.match(name + "List")) {
         List<String> list;
         String s;
         for(list = new ArrayList(); null != (s = this.string(name)); list.add(s)) {
            if("Null".equals(s)) {
               s = null;
            }
         }

         if(this.match("/" + name + "List")) {
            return (String[])list.toArray(new String[list.size()]);
         }
      }

      return null;
   }

   public String[][] stringTable(String name) {
      if(this.match(name + "Table")) {
         List<String[]> list = new ArrayList();

         String[] sa;
         while(null != (sa = this.stringArray(name))) {
            list.add(sa);
         }

         if(this.match("/" + name + "Table")) {
            return (String[][])list.toArray(new String[list.size()][]);
         }
      }

      return (String[][])null;
   }

   private boolean match(String target) {
      if(this.getTag().equals(target)) {
         this.advance();
         return true;
      } else {
         return false;
      }
   }

   private String getTag() {
      if(this.tag == null) {
         this.tag = this.readNextTag();
      }

      return this.tag;
   }

   private void advance() {
      this.tag = null;
   }

   private String readData() {
      StringBuilder sb = new StringBuilder();
      boolean inWhitespace = false;

      while(true) {
         int c = this.readChar();
         if(c == -1 || c == 60) {
            this.atTag = c == 60;
            return sb.toString();
         }

         if(c == 38) {
            c = this.readChar();
            if(c == 35) {
               StringBuilder numBuf = new StringBuilder();
               int radix = 10;
               c = this.readChar();
               if(c == 120) {
                  radix = 16;
                  c = this.readChar();
               }

               while(c != 59 && c != -1) {
                  numBuf.append((char)c);
                  c = this.readChar();
               }

               try {
                  int num = Integer.parseInt(numBuf.toString(), radix);
                  c = (char)num;
               } catch (NumberFormatException var7) {
                  System.err.println("numbuf: " + numBuf.toString() + " radix: " + radix);
                  throw var7;
               }
            } else {
               StringBuilder charBuf;
               for(charBuf = new StringBuilder(); c != 59 && c != -1; c = this.readChar()) {
                  charBuf.append((char)c);
               }

               String charName = charBuf.toString();
               if(charName.equals("lt")) {
                  c = 60;
               } else if(charName.equals("gt")) {
                  c = 62;
               } else if(charName.equals("quot")) {
                  c = 34;
               } else if(charName.equals("apos")) {
                  c = 39;
               } else {
                  if(!charName.equals("amp")) {
                     System.err.println("unrecognized character entity: \'" + charName + "\'");
                     continue;
                  }

                  c = 38;
               }
            }
         }

         if(UCharacter.isWhitespace(c)) {
            if(inWhitespace) {
               continue;
            }

            c = 32;
            inWhitespace = true;
         } else {
            inWhitespace = false;
         }

         sb.append((char)c);
      }
   }

   private String readNextTag() {
      int c = 0;

      while(!this.atTag) {
         c = this.readChar();
         if(c != 60 && c != -1) {
            if(UCharacter.isWhitespace(c)) {
               continue;
            }

            System.err.println("Unexpected non-whitespace character " + Integer.toHexString(c));
            break;
         }

         if(c == 60) {
            this.atTag = true;
         }
         break;
      }

      if(!this.atTag) {
         return null;
      } else {
         this.atTag = false;
         StringBuilder sb = new StringBuilder();

         while(true) {
            c = this.readChar();
            if(c == 62 || c == -1) {
               return sb.toString();
            }

            sb.append((char)c);
         }
      }
   }

   int readChar() {
      try {
         return this.r.read();
      } catch (IOException var2) {
         return -1;
      }
   }
}
