/*******************************************************************************
 * Copyright (c) 2011 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Research Group for Industrial Software (INSO), Vienna University of Technology - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.reviews.tasks.dsl.internal;

// $ANTLR 3.0 ReviewDsl.g 2011-03-06 18:30:25

import org.antlr.runtime.CharStream;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;

/**
 * 
 * @author mattk
 *
 */
public class ReviewDslLexer extends Lexer {
    public static final int TASK_ID=6;
    public static final int UNICODE_ESC=8;
    public static final int OCTAL_ESC=9;
    public static final int HEX_DIGIT=10;
    public static final int T29=29;
    public static final int INT=5;
    public static final int T28=28;
    public static final int T27=27;
    public static final int T26=26;
    public static final int T25=25;
    public static final int Tokens=33;
    public static final int T24=24;
    public static final int EOF=-1;
    public static final int T23=23;
    public static final int T22=22;
    public static final int T21=21;
    public static final int T20=20;
    public static final int ESC_SEQ=7;
    public static final int WS=11;
    public static final int T12=12;
    public static final int T13=13;
    public static final int T14=14;
    public static final int T15=15;
    public static final int T16=16;
    public static final int T17=17;
    public static final int T18=18;
    public static final int T30=30;
    public static final int T19=19;
    public static final int T32=32;
    public static final int STRING=4;
    public static final int T31=31;
    public ReviewDslLexer() {;} 
    public ReviewDslLexer(CharStream input) {
        super(input);
    }
    public String getGrammarFileName() { return "ReviewDsl.g"; }
@Override

	public void reportError(RecognitionException arg0) {
		/* ignore */
	}

    // $ANTLR start T12
    public final void mT12() throws RecognitionException {
        try {
            int _type = T12;
            // ReviewDsl.g:3:7: ( 'Review' )
            // ReviewDsl.g:3:7: 'Review'
            {
            match("Review"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T12

    // $ANTLR start T13
    public final void mT13() throws RecognitionException {
        try {
            int _type = T13;
            // ReviewDsl.g:4:7: ( 'result:' )
            // ReviewDsl.g:4:7: 'result:'
            {
            match("result:"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T13

    // $ANTLR start T14
    public final void mT14() throws RecognitionException {
        try {
            int _type = T14;
            // ReviewDsl.g:5:7: ( 'Comment:' )
            // ReviewDsl.g:5:7: 'Comment:'
            {
            match("Comment:"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T14

    // $ANTLR start T15
    public final void mT15() throws RecognitionException {
        try {
            int _type = T15;
            // ReviewDsl.g:6:7: ( 'PASSED' )
            // ReviewDsl.g:6:7: 'PASSED'
            {
            match("PASSED"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T15

    // $ANTLR start T16
    public final void mT16() throws RecognitionException {
        try {
            int _type = T16;
            // ReviewDsl.g:7:7: ( 'WARNING' )
            // ReviewDsl.g:7:7: 'WARNING'
            {
            match("WARNING"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T16

    // $ANTLR start T17
    public final void mT17() throws RecognitionException {
        try {
            int _type = T17;
            // ReviewDsl.g:8:7: ( 'FAILED' )
            // ReviewDsl.g:8:7: 'FAILED'
            {
            match("FAILED"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T17

    // $ANTLR start T18
    public final void mT18() throws RecognitionException {
        try {
            int _type = T18;
            // ReviewDsl.g:9:7: ( 'TODO' )
            // ReviewDsl.g:9:7: 'TODO'
            {
            match("TODO"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T18

    // $ANTLR start T19
    public final void mT19() throws RecognitionException {
        try {
            int _type = T19;
            // ReviewDsl.g:10:7: ( 'File' )
            // ReviewDsl.g:10:7: 'File'
            {
            match("File"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T19

    // $ANTLR start T20
    public final void mT20() throws RecognitionException {
        try {
            int _type = T20;
            // ReviewDsl.g:11:7: ( ':' )
            // ReviewDsl.g:11:7: ':'
            {
            match(':'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T20

    // $ANTLR start T21
    public final void mT21() throws RecognitionException {
        try {
            int _type = T21;
            // ReviewDsl.g:12:7: ( 'Line' )
            // ReviewDsl.g:12:7: 'Line'
            {
            match("Line"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T21

    // $ANTLR start T22
    public final void mT22() throws RecognitionException {
        try {
            int _type = T22;
            // ReviewDsl.g:13:7: ( '-' )
            // ReviewDsl.g:13:7: '-'
            {
            match('-'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T22

    // $ANTLR start T23
    public final void mT23() throws RecognitionException {
        try {
            int _type = T23;
            // ReviewDsl.g:14:7: ( 'scope:' )
            // ReviewDsl.g:14:7: 'scope:'
            {
            match("scope:"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T23

    // $ANTLR start T24
    public final void mT24() throws RecognitionException {
        try {
            int _type = T24;
            // ReviewDsl.g:15:7: ( 'Resource' )
            // ReviewDsl.g:15:7: 'Resource'
            {
            match("Resource"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T24

    // $ANTLR start T25
    public final void mT25() throws RecognitionException {
        try {
            int _type = T25;
            // ReviewDsl.g:16:7: ( 'Changeset' )
            // ReviewDsl.g:16:7: 'Changeset'
            {
            match("Changeset"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T25

    // $ANTLR start T26
    public final void mT26() throws RecognitionException {
        try {
            int _type = T26;
            // ReviewDsl.g:17:7: ( 'from' )
            // ReviewDsl.g:17:7: 'from'
            {
            match("from"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T26

    // $ANTLR start T27
    public final void mT27() throws RecognitionException {
        try {
            int _type = T27;
            // ReviewDsl.g:18:7: ( 'Patch' )
            // ReviewDsl.g:18:7: 'Patch'
            {
            match("Patch"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T27

    // $ANTLR start T28
    public final void mT28() throws RecognitionException {
        try {
            int _type = T28;
            // ReviewDsl.g:19:7: ( 'Attachment' )
            // ReviewDsl.g:19:7: 'Attachment'
            {
            match("Attachment"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T28

    // $ANTLR start T29
    public final void mT29() throws RecognitionException {
        try {
            int _type = T29;
            // ReviewDsl.g:20:7: ( 'by' )
            // ReviewDsl.g:20:7: 'by'
            {
            match("by"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T29

    // $ANTLR start T30
    public final void mT30() throws RecognitionException {
        try {
            int _type = T30;
            // ReviewDsl.g:21:7: ( 'on' )
            // ReviewDsl.g:21:7: 'on'
            {
            match("on"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T30

    // $ANTLR start T31
    public final void mT31() throws RecognitionException {
        try {
            int _type = T31;
            // ReviewDsl.g:22:7: ( 'of' )
            // ReviewDsl.g:22:7: 'of'
            {
            match("of"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T31

    // $ANTLR start T32
    public final void mT32() throws RecognitionException {
        try {
            int _type = T32;
            // ReviewDsl.g:23:7: ( 'task' )
            // ReviewDsl.g:23:7: 'task'
            {
            match("task"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T32

    // $ANTLR start TASK_ID
    public final void mTASK_ID() throws RecognitionException {
        try {
            int _type = TASK_ID;
            // ReviewDsl.g:72:2: ( ( 'a' .. 'z' | 'A' .. 'Z' )+ ( '_' | '-' )? ( 'a' .. 'z' | 'A' .. 'Z' | INT )+ )
            // ReviewDsl.g:72:2: ( 'a' .. 'z' | 'A' .. 'Z' )+ ( '_' | '-' )? ( 'a' .. 'z' | 'A' .. 'Z' | INT )+
            {
            // ReviewDsl.g:72:2: ( 'a' .. 'z' | 'A' .. 'Z' )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>='a' && LA1_0<='z')) ) {
                    alt1=1;
                }
                else if ( ((LA1_0>='A' && LA1_0<='Z')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // ReviewDsl.g:
            	    {
            	    if ( (input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);

            // ReviewDsl.g:72:25: ( '_' | '-' )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='-'||LA2_0=='_') ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // ReviewDsl.g:
                    {
                    if ( input.LA(1)=='-'||input.LA(1)=='_' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recover(mse);    throw mse;
                    }


                    }
                    break;

            }

            // ReviewDsl.g:72:38: ( 'a' .. 'z' | 'A' .. 'Z' | INT )+
            int cnt3=0;
            loop3:
            do {
                int alt3=4;
                switch ( input.LA(1) ) {
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                    {
                    alt3=1;
                    }
                    break;
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z':
                    {
                    alt3=2;
                    }
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    {
                    alt3=3;
                    }
                    break;

                }

                switch (alt3) {
            	case 1 :
            	    // ReviewDsl.g:72:39: 'a' .. 'z'
            	    {
            	    matchRange('a','z'); 

            	    }
            	    break;
            	case 2 :
            	    // ReviewDsl.g:72:50: 'A' .. 'Z'
            	    {
            	    matchRange('A','Z'); 

            	    }
            	    break;
            	case 3 :
            	    // ReviewDsl.g:72:61: INT
            	    {
            	    mINT(); 

            	    }
            	    break;

            	default :
            	    if ( cnt3 >= 1 ) break loop3;
                        EarlyExitException eee =
                            new EarlyExitException(3, input);
                        throw eee;
                }
                cnt3++;
            } while (true);


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TASK_ID

    // $ANTLR start INT
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            // ReviewDsl.g:75:2: ( ( '0' .. '9' )+ )
            // ReviewDsl.g:75:2: ( '0' .. '9' )+
            {
            // ReviewDsl.g:75:2: ( '0' .. '9' )+
            int cnt4=0;
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( ((LA4_0>='0' && LA4_0<='9')) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // ReviewDsl.g:75:2: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt4 >= 1 ) break loop4;
                        EarlyExitException eee =
                            new EarlyExitException(4, input);
                        throw eee;
                }
                cnt4++;
            } while (true);


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end INT

    // $ANTLR start STRING
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            // ReviewDsl.g:79:8: ( '\"' ( ESC_SEQ | ~ ( '\\\\' | '\"' ) )* '\"' )
            // ReviewDsl.g:79:8: '\"' ( ESC_SEQ | ~ ( '\\\\' | '\"' ) )* '\"'
            {
            match('\"'); 
            // ReviewDsl.g:79:12: ( ESC_SEQ | ~ ( '\\\\' | '\"' ) )*
            loop5:
            do {
                int alt5=3;
                int LA5_0 = input.LA(1);

                if ( (LA5_0=='\\') ) {
                    alt5=1;
                }
                else if ( ((LA5_0>='\u0000' && LA5_0<='!')||(LA5_0>='#' && LA5_0<='[')||(LA5_0>=']' && LA5_0<='\uFFFE')) ) {
                    alt5=2;
                }


                switch (alt5) {
            	case 1 :
            	    // ReviewDsl.g:79:14: ESC_SEQ
            	    {
            	    mESC_SEQ(); 

            	    }
            	    break;
            	case 2 :
            	    // ReviewDsl.g:79:24: ~ ( '\\\\' | '\"' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFE') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);

            match('\"'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end STRING

    // $ANTLR start ESC_SEQ
    public final void mESC_SEQ() throws RecognitionException {
        try {
            // ReviewDsl.g:83:9: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) | UNICODE_ESC | OCTAL_ESC )
            int alt6=3;
            int LA6_0 = input.LA(1);

            if ( (LA6_0=='\\') ) {
                switch ( input.LA(2) ) {
                case 'u':
                    {
                    alt6=2;
                    }
                    break;
                case '\"':
                case '\'':
                case '\\':
                case 'b':
                case 'f':
                case 'n':
                case 'r':
                case 't':
                    {
                    alt6=1;
                    }
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                    {
                    alt6=3;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("81:1: fragment ESC_SEQ : ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) | UNICODE_ESC | OCTAL_ESC );", 6, 1, input);

                    throw nvae;
                }

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("81:1: fragment ESC_SEQ : ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) | UNICODE_ESC | OCTAL_ESC );", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // ReviewDsl.g:83:9: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' )
                    {
                    match('\\'); 
                    if ( input.LA(1)=='\"'||input.LA(1)=='\''||input.LA(1)=='\\'||input.LA(1)=='b'||input.LA(1)=='f'||input.LA(1)=='n'||input.LA(1)=='r'||input.LA(1)=='t' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recover(mse);    throw mse;
                    }


                    }
                    break;
                case 2 :
                    // ReviewDsl.g:84:9: UNICODE_ESC
                    {
                    mUNICODE_ESC(); 

                    }
                    break;
                case 3 :
                    // ReviewDsl.g:85:9: OCTAL_ESC
                    {
                    mOCTAL_ESC(); 

                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end ESC_SEQ

    // $ANTLR start OCTAL_ESC
    public final void mOCTAL_ESC() throws RecognitionException {
        try {
            // ReviewDsl.g:89:9: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
            int alt7=3;
            int LA7_0 = input.LA(1);

            if ( (LA7_0=='\\') ) {
                int LA7_1 = input.LA(2);

                if ( ((LA7_1>='0' && LA7_1<='3')) ) {
                    int LA7_2 = input.LA(3);

                    if ( ((LA7_2>='0' && LA7_2<='7')) ) {
                        int LA7_5 = input.LA(4);

                        if ( ((LA7_5>='0' && LA7_5<='7')) ) {
                            alt7=1;
                        }
                        else {
                            alt7=2;}
                    }
                    else {
                        alt7=3;}
                }
                else if ( ((LA7_1>='4' && LA7_1<='7')) ) {
                    int LA7_3 = input.LA(3);

                    if ( ((LA7_3>='0' && LA7_3<='7')) ) {
                        alt7=2;
                    }
                    else {
                        alt7=3;}
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("87:1: fragment OCTAL_ESC : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 7, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("87:1: fragment OCTAL_ESC : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // ReviewDsl.g:89:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); 
                    // ReviewDsl.g:89:14: ( '0' .. '3' )
                    // ReviewDsl.g:89:15: '0' .. '3'
                    {
                    matchRange('0','3'); 

                    }

                    // ReviewDsl.g:89:25: ( '0' .. '7' )
                    // ReviewDsl.g:89:26: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }

                    // ReviewDsl.g:89:36: ( '0' .. '7' )
                    // ReviewDsl.g:89:37: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;
                case 2 :
                    // ReviewDsl.g:90:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); 
                    // ReviewDsl.g:90:14: ( '0' .. '7' )
                    // ReviewDsl.g:90:15: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }

                    // ReviewDsl.g:90:25: ( '0' .. '7' )
                    // ReviewDsl.g:90:26: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;
                case 3 :
                    // ReviewDsl.g:91:9: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); 
                    // ReviewDsl.g:91:14: ( '0' .. '7' )
                    // ReviewDsl.g:91:15: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end OCTAL_ESC

    // $ANTLR start UNICODE_ESC
    public final void mUNICODE_ESC() throws RecognitionException {
        try {
            // ReviewDsl.g:95:9: ( '\\\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT )
            // ReviewDsl.g:95:9: '\\\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
            {
            match('\\'); 
            match('u'); 
            mHEX_DIGIT(); 
            mHEX_DIGIT(); 
            mHEX_DIGIT(); 
            mHEX_DIGIT(); 

            }

        }
        finally {
        }
    }
    // $ANTLR end UNICODE_ESC

    // $ANTLR start HEX_DIGIT
    public final void mHEX_DIGIT() throws RecognitionException {
        try {
            // ReviewDsl.g:99:13: ( ( INT | 'a' .. 'f' | 'A' .. 'F' ) )
            // ReviewDsl.g:99:13: ( INT | 'a' .. 'f' | 'A' .. 'F' )
            {
            // ReviewDsl.g:99:13: ( INT | 'a' .. 'f' | 'A' .. 'F' )
            int alt8=3;
            switch ( input.LA(1) ) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                {
                alt8=1;
                }
                break;
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
                {
                alt8=2;
                }
                break;
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
                {
                alt8=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("99:13: ( INT | 'a' .. 'f' | 'A' .. 'F' )", 8, 0, input);

                throw nvae;
            }

            switch (alt8) {
                case 1 :
                    // ReviewDsl.g:99:14: INT
                    {
                    mINT(); 

                    }
                    break;
                case 2 :
                    // ReviewDsl.g:99:18: 'a' .. 'f'
                    {
                    matchRange('a','f'); 

                    }
                    break;
                case 3 :
                    // ReviewDsl.g:99:27: 'A' .. 'F'
                    {
                    matchRange('A','F'); 

                    }
                    break;

            }


            }

        }
        finally {
        }
    }
    // $ANTLR end HEX_DIGIT

    // $ANTLR start WS
    public final void mWS() throws RecognitionException {
        try {
            // ReviewDsl.g:102:9: ( ( ' ' | '\\t' | '\\r' | '\\n' ) )
            // ReviewDsl.g:102:9: ( ' ' | '\\t' | '\\r' | '\\n' )
            {
            if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            channel=HIDDEN;

            }

        }
        finally {
        }
    }
    // $ANTLR end WS

    public void mTokens() throws RecognitionException {
        // ReviewDsl.g:1:10: ( T12 | T13 | T14 | T15 | T16 | T17 | T18 | T19 | T20 | T21 | T22 | T23 | T24 | T25 | T26 | T27 | T28 | T29 | T30 | T31 | T32 | TASK_ID | INT | STRING )
        int alt9=24;
        switch ( input.LA(1) ) {
        case 'R':
            {
            int LA9_1 = input.LA(2);

            if ( (LA9_1=='e') ) {
                switch ( input.LA(3) ) {
                case 'v':
                    {
                    int LA9_38 = input.LA(4);

                    if ( (LA9_38=='i') ) {
                        int LA9_57 = input.LA(5);

                        if ( (LA9_57=='e') ) {
                            int LA9_73 = input.LA(6);

                            if ( (LA9_73=='w') ) {
                                int LA9_89 = input.LA(7);

                                if ( (LA9_89=='-'||(LA9_89>='0' && LA9_89<='9')||(LA9_89>='A' && LA9_89<='Z')||LA9_89=='_'||(LA9_89>='a' && LA9_89<='z')) ) {
                                    alt9=22;
                                }
                                else {
                                    alt9=1;}
                            }
                            else {
                                alt9=22;}
                        }
                        else {
                            alt9=22;}
                    }
                    else {
                        alt9=22;}
                    }
                    break;
                case 's':
                    {
                    int LA9_39 = input.LA(4);

                    if ( (LA9_39=='o') ) {
                        int LA9_58 = input.LA(5);

                        if ( (LA9_58=='u') ) {
                            int LA9_74 = input.LA(6);

                            if ( (LA9_74=='r') ) {
                                int LA9_90 = input.LA(7);

                                if ( (LA9_90=='c') ) {
                                    int LA9_101 = input.LA(8);

                                    if ( (LA9_101=='e') ) {
                                        int LA9_109 = input.LA(9);

                                        if ( (LA9_109=='-'||(LA9_109>='0' && LA9_109<='9')||(LA9_109>='A' && LA9_109<='Z')||LA9_109=='_'||(LA9_109>='a' && LA9_109<='z')) ) {
                                            alt9=22;
                                        }
                                        else {
                                            alt9=13;}
                                    }
                                    else {
                                        alt9=22;}
                                }
                                else {
                                    alt9=22;}
                            }
                            else {
                                alt9=22;}
                        }
                        else {
                            alt9=22;}
                    }
                    else {
                        alt9=22;}
                    }
                    break;
                default:
                    alt9=22;}

            }
            else if ( (LA9_1=='-'||(LA9_1>='0' && LA9_1<='9')||(LA9_1>='A' && LA9_1<='Z')||LA9_1=='_'||(LA9_1>='a' && LA9_1<='d')||(LA9_1>='f' && LA9_1<='z')) ) {
                alt9=22;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("1:1: Tokens : ( T12 | T13 | T14 | T15 | T16 | T17 | T18 | T19 | T20 | T21 | T22 | T23 | T24 | T25 | T26 | T27 | T28 | T29 | T30 | T31 | T32 | TASK_ID | INT | STRING );", 9, 1, input);

                throw nvae;
            }
            }
            break;
        case 'r':
            {
            int LA9_2 = input.LA(2);

            if ( (LA9_2=='e') ) {
                int LA9_21 = input.LA(3);

                if ( (LA9_21=='s') ) {
                    int LA9_40 = input.LA(4);

                    if ( (LA9_40=='u') ) {
                        int LA9_59 = input.LA(5);

                        if ( (LA9_59=='l') ) {
                            int LA9_75 = input.LA(6);

                            if ( (LA9_75=='t') ) {
                                int LA9_91 = input.LA(7);

                                if ( (LA9_91==':') ) {
                                    alt9=2;
                                }
                                else {
                                    alt9=22;}
                            }
                            else {
                                alt9=22;}
                        }
                        else {
                            alt9=22;}
                    }
                    else {
                        alt9=22;}
                }
                else {
                    alt9=22;}
            }
            else if ( (LA9_2=='-'||(LA9_2>='0' && LA9_2<='9')||(LA9_2>='A' && LA9_2<='Z')||LA9_2=='_'||(LA9_2>='a' && LA9_2<='d')||(LA9_2>='f' && LA9_2<='z')) ) {
                alt9=22;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("1:1: Tokens : ( T12 | T13 | T14 | T15 | T16 | T17 | T18 | T19 | T20 | T21 | T22 | T23 | T24 | T25 | T26 | T27 | T28 | T29 | T30 | T31 | T32 | TASK_ID | INT | STRING );", 9, 2, input);

                throw nvae;
            }
            }
            break;
        case 'C':
            {
            switch ( input.LA(2) ) {
            case 'h':
                {
                int LA9_22 = input.LA(3);

                if ( (LA9_22=='a') ) {
                    int LA9_41 = input.LA(4);

                    if ( (LA9_41=='n') ) {
                        int LA9_60 = input.LA(5);

                        if ( (LA9_60=='g') ) {
                            int LA9_76 = input.LA(6);

                            if ( (LA9_76=='e') ) {
                                int LA9_92 = input.LA(7);

                                if ( (LA9_92=='s') ) {
                                    int LA9_103 = input.LA(8);

                                    if ( (LA9_103=='e') ) {
                                        int LA9_110 = input.LA(9);

                                        if ( (LA9_110=='t') ) {
                                            int LA9_115 = input.LA(10);

                                            if ( (LA9_115=='-'||(LA9_115>='0' && LA9_115<='9')||(LA9_115>='A' && LA9_115<='Z')||LA9_115=='_'||(LA9_115>='a' && LA9_115<='z')) ) {
                                                alt9=22;
                                            }
                                            else {
                                                alt9=14;}
                                        }
                                        else {
                                            alt9=22;}
                                    }
                                    else {
                                        alt9=22;}
                                }
                                else {
                                    alt9=22;}
                            }
                            else {
                                alt9=22;}
                        }
                        else {
                            alt9=22;}
                    }
                    else {
                        alt9=22;}
                }
                else {
                    alt9=22;}
                }
                break;
            case 'o':
                {
                int LA9_23 = input.LA(3);

                if ( (LA9_23=='m') ) {
                    int LA9_42 = input.LA(4);

                    if ( (LA9_42=='m') ) {
                        int LA9_61 = input.LA(5);

                        if ( (LA9_61=='e') ) {
                            int LA9_77 = input.LA(6);

                            if ( (LA9_77=='n') ) {
                                int LA9_93 = input.LA(7);

                                if ( (LA9_93=='t') ) {
                                    int LA9_104 = input.LA(8);

                                    if ( (LA9_104==':') ) {
                                        alt9=3;
                                    }
                                    else {
                                        alt9=22;}
                                }
                                else {
                                    alt9=22;}
                            }
                            else {
                                alt9=22;}
                        }
                        else {
                            alt9=22;}
                    }
                    else {
                        alt9=22;}
                }
                else {
                    alt9=22;}
                }
                break;
            case '-':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '_':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            case 'z':
                {
                alt9=22;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("1:1: Tokens : ( T12 | T13 | T14 | T15 | T16 | T17 | T18 | T19 | T20 | T21 | T22 | T23 | T24 | T25 | T26 | T27 | T28 | T29 | T30 | T31 | T32 | TASK_ID | INT | STRING );", 9, 3, input);

                throw nvae;
            }

            }
            break;
        case 'P':
            {
            switch ( input.LA(2) ) {
            case 'a':
                {
                int LA9_24 = input.LA(3);

                if ( (LA9_24=='t') ) {
                    int LA9_43 = input.LA(4);

                    if ( (LA9_43=='c') ) {
                        int LA9_62 = input.LA(5);

                        if ( (LA9_62=='h') ) {
                            int LA9_78 = input.LA(6);

                            if ( (LA9_78=='-'||(LA9_78>='0' && LA9_78<='9')||(LA9_78>='A' && LA9_78<='Z')||LA9_78=='_'||(LA9_78>='a' && LA9_78<='z')) ) {
                                alt9=22;
                            }
                            else {
                                alt9=16;}
                        }
                        else {
                            alt9=22;}
                    }
                    else {
                        alt9=22;}
                }
                else {
                    alt9=22;}
                }
                break;
            case 'A':
                {
                int LA9_25 = input.LA(3);

                if ( (LA9_25=='S') ) {
                    int LA9_44 = input.LA(4);

                    if ( (LA9_44=='S') ) {
                        int LA9_63 = input.LA(5);

                        if ( (LA9_63=='E') ) {
                            int LA9_79 = input.LA(6);

                            if ( (LA9_79=='D') ) {
                                int LA9_95 = input.LA(7);

                                if ( (LA9_95=='-'||(LA9_95>='0' && LA9_95<='9')||(LA9_95>='A' && LA9_95<='Z')||LA9_95=='_'||(LA9_95>='a' && LA9_95<='z')) ) {
                                    alt9=22;
                                }
                                else {
                                    alt9=4;}
                            }
                            else {
                                alt9=22;}
                        }
                        else {
                            alt9=22;}
                    }
                    else {
                        alt9=22;}
                }
                else {
                    alt9=22;}
                }
                break;
            case '-':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '_':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            case 'z':
                {
                alt9=22;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("1:1: Tokens : ( T12 | T13 | T14 | T15 | T16 | T17 | T18 | T19 | T20 | T21 | T22 | T23 | T24 | T25 | T26 | T27 | T28 | T29 | T30 | T31 | T32 | TASK_ID | INT | STRING );", 9, 4, input);

                throw nvae;
            }

            }
            break;
        case 'W':
            {
            int LA9_5 = input.LA(2);

            if ( (LA9_5=='A') ) {
                int LA9_26 = input.LA(3);

                if ( (LA9_26=='R') ) {
                    int LA9_45 = input.LA(4);

                    if ( (LA9_45=='N') ) {
                        int LA9_64 = input.LA(5);

                        if ( (LA9_64=='I') ) {
                            int LA9_80 = input.LA(6);

                            if ( (LA9_80=='N') ) {
                                int LA9_96 = input.LA(7);

                                if ( (LA9_96=='G') ) {
                                    int LA9_106 = input.LA(8);

                                    if ( (LA9_106=='-'||(LA9_106>='0' && LA9_106<='9')||(LA9_106>='A' && LA9_106<='Z')||LA9_106=='_'||(LA9_106>='a' && LA9_106<='z')) ) {
                                        alt9=22;
                                    }
                                    else {
                                        alt9=5;}
                                }
                                else {
                                    alt9=22;}
                            }
                            else {
                                alt9=22;}
                        }
                        else {
                            alt9=22;}
                    }
                    else {
                        alt9=22;}
                }
                else {
                    alt9=22;}
            }
            else if ( (LA9_5=='-'||(LA9_5>='0' && LA9_5<='9')||(LA9_5>='B' && LA9_5<='Z')||LA9_5=='_'||(LA9_5>='a' && LA9_5<='z')) ) {
                alt9=22;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("1:1: Tokens : ( T12 | T13 | T14 | T15 | T16 | T17 | T18 | T19 | T20 | T21 | T22 | T23 | T24 | T25 | T26 | T27 | T28 | T29 | T30 | T31 | T32 | TASK_ID | INT | STRING );", 9, 5, input);

                throw nvae;
            }
            }
            break;
        case 'F':
            {
            switch ( input.LA(2) ) {
            case 'A':
                {
                int LA9_27 = input.LA(3);

                if ( (LA9_27=='I') ) {
                    int LA9_46 = input.LA(4);

                    if ( (LA9_46=='L') ) {
                        int LA9_65 = input.LA(5);

                        if ( (LA9_65=='E') ) {
                            int LA9_81 = input.LA(6);

                            if ( (LA9_81=='D') ) {
                                int LA9_97 = input.LA(7);

                                if ( (LA9_97=='-'||(LA9_97>='0' && LA9_97<='9')||(LA9_97>='A' && LA9_97<='Z')||LA9_97=='_'||(LA9_97>='a' && LA9_97<='z')) ) {
                                    alt9=22;
                                }
                                else {
                                    alt9=6;}
                            }
                            else {
                                alt9=22;}
                        }
                        else {
                            alt9=22;}
                    }
                    else {
                        alt9=22;}
                }
                else {
                    alt9=22;}
                }
                break;
            case 'i':
                {
                int LA9_28 = input.LA(3);

                if ( (LA9_28=='l') ) {
                    int LA9_47 = input.LA(4);

                    if ( (LA9_47=='e') ) {
                        int LA9_66 = input.LA(5);

                        if ( (LA9_66=='-'||(LA9_66>='0' && LA9_66<='9')||(LA9_66>='A' && LA9_66<='Z')||LA9_66=='_'||(LA9_66>='a' && LA9_66<='z')) ) {
                            alt9=22;
                        }
                        else {
                            alt9=8;}
                    }
                    else {
                        alt9=22;}
                }
                else {
                    alt9=22;}
                }
                break;
            case '-':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '_':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            case 'z':
                {
                alt9=22;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("1:1: Tokens : ( T12 | T13 | T14 | T15 | T16 | T17 | T18 | T19 | T20 | T21 | T22 | T23 | T24 | T25 | T26 | T27 | T28 | T29 | T30 | T31 | T32 | TASK_ID | INT | STRING );", 9, 6, input);

                throw nvae;
            }

            }
            break;
        case 'T':
            {
            int LA9_7 = input.LA(2);

            if ( (LA9_7=='O') ) {
                int LA9_29 = input.LA(3);

                if ( (LA9_29=='D') ) {
                    int LA9_48 = input.LA(4);

                    if ( (LA9_48=='O') ) {
                        int LA9_67 = input.LA(5);

                        if ( (LA9_67=='-'||(LA9_67>='0' && LA9_67<='9')||(LA9_67>='A' && LA9_67<='Z')||LA9_67=='_'||(LA9_67>='a' && LA9_67<='z')) ) {
                            alt9=22;
                        }
                        else {
                            alt9=7;}
                    }
                    else {
                        alt9=22;}
                }
                else {
                    alt9=22;}
            }
            else if ( (LA9_7=='-'||(LA9_7>='0' && LA9_7<='9')||(LA9_7>='A' && LA9_7<='N')||(LA9_7>='P' && LA9_7<='Z')||LA9_7=='_'||(LA9_7>='a' && LA9_7<='z')) ) {
                alt9=22;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("1:1: Tokens : ( T12 | T13 | T14 | T15 | T16 | T17 | T18 | T19 | T20 | T21 | T22 | T23 | T24 | T25 | T26 | T27 | T28 | T29 | T30 | T31 | T32 | TASK_ID | INT | STRING );", 9, 7, input);

                throw nvae;
            }
            }
            break;
        case ':':
            {
            alt9=9;
            }
            break;
        case 'L':
            {
            int LA9_9 = input.LA(2);

            if ( (LA9_9=='i') ) {
                int LA9_30 = input.LA(3);

                if ( (LA9_30=='n') ) {
                    int LA9_49 = input.LA(4);

                    if ( (LA9_49=='e') ) {
                        int LA9_68 = input.LA(5);

                        if ( (LA9_68=='-'||(LA9_68>='0' && LA9_68<='9')||(LA9_68>='A' && LA9_68<='Z')||LA9_68=='_'||(LA9_68>='a' && LA9_68<='z')) ) {
                            alt9=22;
                        }
                        else {
                            alt9=10;}
                    }
                    else {
                        alt9=22;}
                }
                else {
                    alt9=22;}
            }
            else if ( (LA9_9=='-'||(LA9_9>='0' && LA9_9<='9')||(LA9_9>='A' && LA9_9<='Z')||LA9_9=='_'||(LA9_9>='a' && LA9_9<='h')||(LA9_9>='j' && LA9_9<='z')) ) {
                alt9=22;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("1:1: Tokens : ( T12 | T13 | T14 | T15 | T16 | T17 | T18 | T19 | T20 | T21 | T22 | T23 | T24 | T25 | T26 | T27 | T28 | T29 | T30 | T31 | T32 | TASK_ID | INT | STRING );", 9, 9, input);

                throw nvae;
            }
            }
            break;
        case '-':
            {
            alt9=11;
            }
            break;
        case 's':
            {
            int LA9_11 = input.LA(2);

            if ( (LA9_11=='c') ) {
                int LA9_31 = input.LA(3);

                if ( (LA9_31=='o') ) {
                    int LA9_50 = input.LA(4);

                    if ( (LA9_50=='p') ) {
                        int LA9_69 = input.LA(5);

                        if ( (LA9_69=='e') ) {
                            int LA9_85 = input.LA(6);

                            if ( (LA9_85==':') ) {
                                alt9=12;
                            }
                            else {
                                alt9=22;}
                        }
                        else {
                            alt9=22;}
                    }
                    else {
                        alt9=22;}
                }
                else {
                    alt9=22;}
            }
            else if ( (LA9_11=='-'||(LA9_11>='0' && LA9_11<='9')||(LA9_11>='A' && LA9_11<='Z')||LA9_11=='_'||(LA9_11>='a' && LA9_11<='b')||(LA9_11>='d' && LA9_11<='z')) ) {
                alt9=22;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("1:1: Tokens : ( T12 | T13 | T14 | T15 | T16 | T17 | T18 | T19 | T20 | T21 | T22 | T23 | T24 | T25 | T26 | T27 | T28 | T29 | T30 | T31 | T32 | TASK_ID | INT | STRING );", 9, 11, input);

                throw nvae;
            }
            }
            break;
        case 'f':
            {
            int LA9_12 = input.LA(2);

            if ( (LA9_12=='r') ) {
                int LA9_32 = input.LA(3);

                if ( (LA9_32=='o') ) {
                    int LA9_51 = input.LA(4);

                    if ( (LA9_51=='m') ) {
                        int LA9_70 = input.LA(5);

                        if ( (LA9_70=='-'||(LA9_70>='0' && LA9_70<='9')||(LA9_70>='A' && LA9_70<='Z')||LA9_70=='_'||(LA9_70>='a' && LA9_70<='z')) ) {
                            alt9=22;
                        }
                        else {
                            alt9=15;}
                    }
                    else {
                        alt9=22;}
                }
                else {
                    alt9=22;}
            }
            else if ( (LA9_12=='-'||(LA9_12>='0' && LA9_12<='9')||(LA9_12>='A' && LA9_12<='Z')||LA9_12=='_'||(LA9_12>='a' && LA9_12<='q')||(LA9_12>='s' && LA9_12<='z')) ) {
                alt9=22;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("1:1: Tokens : ( T12 | T13 | T14 | T15 | T16 | T17 | T18 | T19 | T20 | T21 | T22 | T23 | T24 | T25 | T26 | T27 | T28 | T29 | T30 | T31 | T32 | TASK_ID | INT | STRING );", 9, 12, input);

                throw nvae;
            }
            }
            break;
        case 'A':
            {
            int LA9_13 = input.LA(2);

            if ( (LA9_13=='t') ) {
                int LA9_33 = input.LA(3);

                if ( (LA9_33=='t') ) {
                    int LA9_52 = input.LA(4);

                    if ( (LA9_52=='a') ) {
                        int LA9_71 = input.LA(5);

                        if ( (LA9_71=='c') ) {
                            int LA9_87 = input.LA(6);

                            if ( (LA9_87=='h') ) {
                                int LA9_99 = input.LA(7);

                                if ( (LA9_99=='m') ) {
                                    int LA9_108 = input.LA(8);

                                    if ( (LA9_108=='e') ) {
                                        int LA9_113 = input.LA(9);

                                        if ( (LA9_113=='n') ) {
                                            int LA9_116 = input.LA(10);

                                            if ( (LA9_116=='t') ) {
                                                int LA9_118 = input.LA(11);

                                                if ( (LA9_118=='-'||(LA9_118>='0' && LA9_118<='9')||(LA9_118>='A' && LA9_118<='Z')||LA9_118=='_'||(LA9_118>='a' && LA9_118<='z')) ) {
                                                    alt9=22;
                                                }
                                                else {
                                                    alt9=17;}
                                            }
                                            else {
                                                alt9=22;}
                                        }
                                        else {
                                            alt9=22;}
                                    }
                                    else {
                                        alt9=22;}
                                }
                                else {
                                    alt9=22;}
                            }
                            else {
                                alt9=22;}
                        }
                        else {
                            alt9=22;}
                    }
                    else {
                        alt9=22;}
                }
                else {
                    alt9=22;}
            }
            else if ( (LA9_13=='-'||(LA9_13>='0' && LA9_13<='9')||(LA9_13>='A' && LA9_13<='Z')||LA9_13=='_'||(LA9_13>='a' && LA9_13<='s')||(LA9_13>='u' && LA9_13<='z')) ) {
                alt9=22;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("1:1: Tokens : ( T12 | T13 | T14 | T15 | T16 | T17 | T18 | T19 | T20 | T21 | T22 | T23 | T24 | T25 | T26 | T27 | T28 | T29 | T30 | T31 | T32 | TASK_ID | INT | STRING );", 9, 13, input);

                throw nvae;
            }
            }
            break;
        case 'b':
            {
            int LA9_14 = input.LA(2);

            if ( (LA9_14=='y') ) {
                int LA9_34 = input.LA(3);

                if ( (LA9_34=='-'||(LA9_34>='0' && LA9_34<='9')||(LA9_34>='A' && LA9_34<='Z')||LA9_34=='_'||(LA9_34>='a' && LA9_34<='z')) ) {
                    alt9=22;
                }
                else {
                    alt9=18;}
            }
            else if ( (LA9_14=='-'||(LA9_14>='0' && LA9_14<='9')||(LA9_14>='A' && LA9_14<='Z')||LA9_14=='_'||(LA9_14>='a' && LA9_14<='x')||LA9_14=='z') ) {
                alt9=22;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("1:1: Tokens : ( T12 | T13 | T14 | T15 | T16 | T17 | T18 | T19 | T20 | T21 | T22 | T23 | T24 | T25 | T26 | T27 | T28 | T29 | T30 | T31 | T32 | TASK_ID | INT | STRING );", 9, 14, input);

                throw nvae;
            }
            }
            break;
        case 'o':
            {
            switch ( input.LA(2) ) {
            case 'n':
                {
                int LA9_35 = input.LA(3);

                if ( (LA9_35=='-'||(LA9_35>='0' && LA9_35<='9')||(LA9_35>='A' && LA9_35<='Z')||LA9_35=='_'||(LA9_35>='a' && LA9_35<='z')) ) {
                    alt9=22;
                }
                else {
                    alt9=19;}
                }
                break;
            case 'f':
                {
                int LA9_36 = input.LA(3);

                if ( (LA9_36=='-'||(LA9_36>='0' && LA9_36<='9')||(LA9_36>='A' && LA9_36<='Z')||LA9_36=='_'||(LA9_36>='a' && LA9_36<='z')) ) {
                    alt9=22;
                }
                else {
                    alt9=20;}
                }
                break;
            case '-':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '_':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            case 'z':
                {
                alt9=22;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("1:1: Tokens : ( T12 | T13 | T14 | T15 | T16 | T17 | T18 | T19 | T20 | T21 | T22 | T23 | T24 | T25 | T26 | T27 | T28 | T29 | T30 | T31 | T32 | TASK_ID | INT | STRING );", 9, 15, input);

                throw nvae;
            }

            }
            break;
        case 't':
            {
            int LA9_16 = input.LA(2);

            if ( (LA9_16=='a') ) {
                int LA9_37 = input.LA(3);

                if ( (LA9_37=='s') ) {
                    int LA9_56 = input.LA(4);

                    if ( (LA9_56=='k') ) {
                        int LA9_72 = input.LA(5);

                        if ( (LA9_72=='-'||(LA9_72>='0' && LA9_72<='9')||(LA9_72>='A' && LA9_72<='Z')||LA9_72=='_'||(LA9_72>='a' && LA9_72<='z')) ) {
                            alt9=22;
                        }
                        else {
                            alt9=21;}
                    }
                    else {
                        alt9=22;}
                }
                else {
                    alt9=22;}
            }
            else if ( (LA9_16=='-'||(LA9_16>='0' && LA9_16<='9')||(LA9_16>='A' && LA9_16<='Z')||LA9_16=='_'||(LA9_16>='b' && LA9_16<='z')) ) {
                alt9=22;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("1:1: Tokens : ( T12 | T13 | T14 | T15 | T16 | T17 | T18 | T19 | T20 | T21 | T22 | T23 | T24 | T25 | T26 | T27 | T28 | T29 | T30 | T31 | T32 | TASK_ID | INT | STRING );", 9, 16, input);

                throw nvae;
            }
            }
            break;
        case 'B':
        case 'D':
        case 'E':
        case 'G':
        case 'H':
        case 'I':
        case 'J':
        case 'K':
        case 'M':
        case 'N':
        case 'O':
        case 'Q':
        case 'S':
        case 'U':
        case 'V':
        case 'X':
        case 'Y':
        case 'Z':
        case 'a':
        case 'c':
        case 'd':
        case 'e':
        case 'g':
        case 'h':
        case 'i':
        case 'j':
        case 'k':
        case 'l':
        case 'm':
        case 'n':
        case 'p':
        case 'q':
        case 'u':
        case 'v':
        case 'w':
        case 'x':
        case 'y':
        case 'z':
            {
            alt9=22;
            }
            break;
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
            {
            alt9=23;
            }
            break;
        case '\"':
            {
            alt9=24;
            }
            break;
        default:
            NoViableAltException nvae =
                new NoViableAltException("1:1: Tokens : ( T12 | T13 | T14 | T15 | T16 | T17 | T18 | T19 | T20 | T21 | T22 | T23 | T24 | T25 | T26 | T27 | T28 | T29 | T30 | T31 | T32 | TASK_ID | INT | STRING );", 9, 0, input);

            throw nvae;
        }

        switch (alt9) {
            case 1 :
                // ReviewDsl.g:1:10: T12
                {
                mT12(); 

                }
                break;
            case 2 :
                // ReviewDsl.g:1:14: T13
                {
                mT13(); 

                }
                break;
            case 3 :
                // ReviewDsl.g:1:18: T14
                {
                mT14(); 

                }
                break;
            case 4 :
                // ReviewDsl.g:1:22: T15
                {
                mT15(); 

                }
                break;
            case 5 :
                // ReviewDsl.g:1:26: T16
                {
                mT16(); 

                }
                break;
            case 6 :
                // ReviewDsl.g:1:30: T17
                {
                mT17(); 

                }
                break;
            case 7 :
                // ReviewDsl.g:1:34: T18
                {
                mT18(); 

                }
                break;
            case 8 :
                // ReviewDsl.g:1:38: T19
                {
                mT19(); 

                }
                break;
            case 9 :
                // ReviewDsl.g:1:42: T20
                {
                mT20(); 

                }
                break;
            case 10 :
                // ReviewDsl.g:1:46: T21
                {
                mT21(); 

                }
                break;
            case 11 :
                // ReviewDsl.g:1:50: T22
                {
                mT22(); 

                }
                break;
            case 12 :
                // ReviewDsl.g:1:54: T23
                {
                mT23(); 

                }
                break;
            case 13 :
                // ReviewDsl.g:1:58: T24
                {
                mT24(); 

                }
                break;
            case 14 :
                // ReviewDsl.g:1:62: T25
                {
                mT25(); 

                }
                break;
            case 15 :
                // ReviewDsl.g:1:66: T26
                {
                mT26(); 

                }
                break;
            case 16 :
                // ReviewDsl.g:1:70: T27
                {
                mT27(); 

                }
                break;
            case 17 :
                // ReviewDsl.g:1:74: T28
                {
                mT28(); 

                }
                break;
            case 18 :
                // ReviewDsl.g:1:78: T29
                {
                mT29(); 

                }
                break;
            case 19 :
                // ReviewDsl.g:1:82: T30
                {
                mT30(); 

                }
                break;
            case 20 :
                // ReviewDsl.g:1:86: T31
                {
                mT31(); 

                }
                break;
            case 21 :
                // ReviewDsl.g:1:90: T32
                {
                mT32(); 

                }
                break;
            case 22 :
                // ReviewDsl.g:1:94: TASK_ID
                {
                mTASK_ID(); 

                }
                break;
            case 23 :
                // ReviewDsl.g:1:102: INT
                {
                mINT(); 

                }
                break;
            case 24 :
                // ReviewDsl.g:1:106: STRING
                {
                mSTRING(); 

                }
                break;

        }

    }


 

}