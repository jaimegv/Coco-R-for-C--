
package compilationunit;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.BitSet;

class Token {
        public int kind;    // token kind
        public int pos;     // token position in the source text (starting at 0)
        public int col;     // token column (starting at 0)
        public int line;    // token line (starting at 1)
        public String val;  // token value
        public Token next;  // AW 2003-03-07 Tokens are kept in linked list
}

class Buffer {
        public static final char EOF = (char)256;
        static byte[] buf;
        static int bufLen;
        static int pos;

        public static void Fill (FileInputStream s) {
                try {
                        bufLen = s.available();
                        buf = new byte[bufLen];
                        s.read(buf, 0, bufLen);
                        pos = 0;
                } catch (IOException e){
                        System.out.println("--- error on filling the buffer ");
                        System.exit(1);
                }
        }

        public static int Read () {
                if (pos < bufLen) return buf[pos++] & 0xff;  // mask out sign bits
                else return EOF;                             /* pdt */
        }

        public static int Peek () {
                if (pos < bufLen) return buf[pos] & 0xff;    // mask out sign bits
                else return EOF;                             /* pdt */
        }

        /* AW 2003-03-10 moved this from ParserGen.cs */
        public static String GetString (int beg, int end) {
                StringBuffer s = new StringBuffer(64);
                int oldPos = Buffer.getPos();
                Buffer.setPos(beg);
                while (beg < end) { s.append((char)Buffer.Read()); beg++; }
                Buffer.setPos(oldPos);
                return s.toString();
        }

        public static int getPos() {
                return pos;
        }

        public static void setPos (int value) {
                if (value < 0) pos = 0;
                else if (value >= bufLen) pos = bufLen;
                else pos = value;
        }

} // end Buffer

public class Scanner {
        static final char EOL = '\n';
        static final int  eofSym = 0;
        static final int charSetSize = 256;
        static final int maxT = 41;
        static final int noSym = 41;
        static short[] start = {
          0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
          0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
          0, 12, 25,  0,  1,  0,  0,  0, 11, 16, 17, 13,  7, 24,  8, 18,
          3,  2,  2,  2,  2,  2,  2,  2,  2,  2,  6, 22, 19, 21, 20, 23,
          0,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,
          1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1, 10,  0, 15,  0,  1,
          0,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,
          1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  9,  0, 14,  0,  0,
          0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
          0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
          0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
          0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
          0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
          0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
          0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
          0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
          -1};


        static Token t;          // current token
        static char ch;          // current input character
        static int pos;          // column number of current character
        static int line;         // line number of current character
        static int lineStart;    // start position of current line
        static int oldEols;      // EOLs that appeared in a comment;
        static BitSet ignore;    // set of characters to be ignored by the scanner

        static Token tokens;     // the complete input token stream
        static Token pt;         // current peek token

        public static void Init (String fileName) {
                FileInputStream s = null;
                try {
                        s = new FileInputStream(fileName);
                        Init(s);
                } catch (IOException e) {
                        System.out.println("--- Cannot open file " + fileName);
                        System.exit(1);
                } finally {
                        if (s != null) {
                                try {
                                        s.close();
                                } catch (IOException e) {
                                        System.out.println("--- Cannot close file " + fileName);
                                        System.exit(1);
                                }
                        }
                }
        }

        public static void Init (FileInputStream s) {
                Buffer.Fill(s);
                pos = -1; line = 1; lineStart = 0;
                oldEols = 0;
                NextCh();
                ignore = new BitSet(charSetSize+1);
                ignore.set(' '); // blanks are always white space
                ignore.set(9); ignore.set(10); ignore.set(13); 
                //--- AW: fill token list
                tokens = new Token();  // first token is a dummy
                Token node = tokens;
                do {
                        node.next = NextToken();
                        node = node.next;
                } while (node.kind != eofSym);
                node.next = node;
                node.val = "EOF";
                t = pt = tokens;
        }

        static void NextCh() {
                if (oldEols > 0) { ch = EOL; oldEols--; }
                else {
                        ch = (char)Buffer.Read(); pos++;
                        // replace isolated '\r' by '\n' in order to make
                        // eol handling uniform across Windows, Unix and Mac
                        if (ch == '\r' && Buffer.Peek() != '\n') ch = EOL;
                        if (ch == EOL) { line++; lineStart = pos + 1; }
                }

        }


        static boolean Comment0() {
                int level = 1, line0 = line, lineStart0 = lineStart;
                NextCh();
                if (ch == '/') {
                        NextCh();
                        for(;;) {
                                if (ch == 10) {
                                        level--;
                                        if (level == 0) { oldEols = line - line0; NextCh(); return true; }
                                        NextCh();
                                } else if (ch == Buffer.EOF) return false;
                                else NextCh();
                        }
                } else {
                        if (ch == EOL) { line--; lineStart = lineStart0; }
                        pos = pos - 2; Buffer.setPos(pos+1); NextCh();
                }
                return false;
        }

        static boolean Comment1() {
                int level = 1, line0 = line, lineStart0 = lineStart;
                NextCh();
                if (ch == '*') {
                        NextCh();
                        for(;;) {
                                if (ch == '*') {
                                        NextCh();
                                        if (ch == '/') {
                                                level--;
                                                if (level == 0) { oldEols = line - line0; NextCh(); return true; }
                                                NextCh();
                                        }
                                } else if (ch == Buffer.EOF) return false;
                                else NextCh();
                        }
                } else {
                        if (ch == EOL) { line--; lineStart = lineStart0; }
                        pos = pos - 2; Buffer.setPos(pos+1); NextCh();
                }
                return false;
        }


        static void CheckLiteral() {
                String lit = t.val;
                if (lit.compareTo("boolean") == 0) t.kind = 4;
                else if (lit.compareTo("char") == 0) t.kind = 5;
                else if (lit.compareTo("class") == 0) t.kind = 6;
                else if (lit.compareTo("false") == 0) t.kind = 7;
                else if (lit.compareTo("int") == 0) t.kind = 8;
                else if (lit.compareTo("new") == 0) t.kind = 9;
                else if (lit.compareTo("short") == 0) t.kind = 10;
                else if (lit.compareTo("static") == 0) t.kind = 11;
                else if (lit.compareTo("true") == 0) t.kind = 12;
                else if (lit.compareTo("void") == 0) t.kind = 13;
                else if (lit.compareTo("public") == 0) t.kind = 34;
                else if (lit.compareTo("private") == 0) t.kind = 35;
                else if (lit.compareTo("main") == 0) t.kind = 36;
                else if (lit.compareTo("print") == 0) t.kind = 37;
                else if (lit.compareTo("read") == 0) t.kind = 38;
                else if (lit.compareTo("return") == 0) t.kind = 39;
                else if (lit.compareTo("for") == 0) t.kind = 40;
        }

        /* AW Scan() renamed to NextToken() */
        static Token NextToken() {
                while (ignore.get(ch)) NextCh();
                if (ch == '/' && Comment0() ||ch == '/' && Comment1()) return NextToken();
                t = new Token();
                t.pos = pos; t.col = pos - lineStart + 1; t.line = line;
                int state = start[ch];
                StringBuffer buf = new StringBuffer(16);
                buf.append(ch); NextCh();
                boolean done = false;
                while (!done) {
                        switch (state) {
                                case -1: { t.kind = eofSym; done = true; break; }  // NextCh already done /* pdt */
                                case 0: { t.kind = noSym; done = true; break; }    // NextCh already done
                                case 1:
                                        if ((ch >= '0' && ch <= '9'
                                          || ch >= 'A' && ch <= 'Z'
                                          || ch == '_'
                                          || ch >= 'a' && ch <= 'z')) { buf.append(ch); NextCh(); state = 1; break;}
                                        else { t.kind = 1; t.val = buf.toString(); CheckLiteral(); return t; }
                                case 2:
                                        if ((ch >= '0' && ch <= '9')) { buf.append(ch); NextCh(); state = 2; break;}
                                        else { t.kind = 2; done = true; break; }
                                case 3:
                                        { t.kind = 2; done = true; break; }
                                case 4:
                                        if (!(ch == '"') && ch != Buffer.EOF) { buf.append(ch); NextCh(); state = 4; break;}
                                        else if (ch == '"') { buf.append(ch); NextCh(); state = 5; break;}
                                        else { t.kind = noSym; done = true; break; }
                                case 5:
                                        { t.kind = 3; done = true; break; }
                                case 6:
                                        { t.kind = 14; done = true; break; }
                                case 7:
                                        { t.kind = 15; done = true; break; }
                                case 8:
                                        { t.kind = 16; done = true; break; }
                                case 9:
                                        { t.kind = 17; done = true; break; }
                                case 10:
                                        { t.kind = 18; done = true; break; }
                                case 11:
                                        { t.kind = 19; done = true; break; }
                                case 12:
                                        { t.kind = 21; done = true; break; }
                                case 13:
                                        { t.kind = 22; done = true; break; }
                                case 14:
                                        { t.kind = 23; done = true; break; }
                                case 15:
                                        { t.kind = 24; done = true; break; }
                                case 16:
                                        { t.kind = 25; done = true; break; }
                                case 17:
                                        { t.kind = 26; done = true; break; }
                                case 18:
                                        { t.kind = 27; done = true; break; }
                                case 19:
                                        { t.kind = 28; done = true; break; }
                                case 20:
                                        { t.kind = 29; done = true; break; }
                                case 21:
                                        { t.kind = 30; done = true; break; }
                                case 22:
                                        { t.kind = 31; done = true; break; }
                                case 23:
                                        { t.kind = 33; done = true; break; }
                                case 24:
                                        if ((ch == '0')) { buf.append(ch); NextCh(); state = 3; break;}
                                        else if ((ch >= '1' && ch <= '9')) { buf.append(ch); NextCh(); state = 2; break;}
                                        else { t.kind = 20; done = true; break; }
                                case 25:
                                        if (!(ch == '"') && ch != Buffer.EOF) { buf.append(ch); NextCh(); state = 4; break;}
                                        else if (ch == '"') { buf.append(ch); NextCh(); state = 5; break;}
                                        else { t.kind = 32; done = true; break; }

                        }
                }
                t.val = buf.toString();
                return t;
        }

        /* AW 2003-03-07 get the next token, move on and synch peek token with current */
        public static Token Scan () {
                t = pt = t.next;
                return t;
        }

        /* AW 2003-03-07 get the next token, ignore pragmas */
        public static Token Peek () {
                do {                      // skip pragmas while peeking
                        pt = pt.next;
                } while (pt.kind > maxT);
                return pt;
        }

        /* AW 2003-03-11 to make sure peek start at current scan position */
        public static void ResetPeek () { pt = t; }

} // end Scanner