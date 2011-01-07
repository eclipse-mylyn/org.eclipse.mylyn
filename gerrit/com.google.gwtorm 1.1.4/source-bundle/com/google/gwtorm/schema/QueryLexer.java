// $ANTLR 3.1.1 /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g 2010-02-22 08:26:35

package com.google.gwtorm.schema;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class QueryLexer extends Lexer {
    public static final int CONSTANT_INTEGER=20;
    public static final int GE=11;
    public static final int WHERE=4;
    public static final int BY=6;
    public static final int LT=8;
    public static final int ASC=17;
    public static final int ORDER=5;
    public static final int LIMIT=19;
    public static final int AND=7;
    public static final int ID=14;
    public static final int EOF=-1;
    public static final int TRUE=22;
    public static final int WS=25;
    public static final int COMMA=16;
    public static final int CONSTANT_STRING=21;
    public static final int GT=10;
    public static final int DESC=18;
    public static final int EQ=12;
    public static final int DOT=24;
    public static final int FALSE=23;
    public static final int LE=9;
    public static final int PLACEHOLDER=15;
    public static final int NE=13;

        public void displayRecognitionError(String[] tokenNames,
                                            RecognitionException e) {
            String hdr = getErrorHeader(e);
            String msg = getErrorMessage(e, tokenNames);
            throw new QueryParseInternalException(hdr + " " + msg);
        }


    // delegates
    // delegators

    public QueryLexer() {;} 
    public QueryLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public QueryLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "/usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g"; }

    // $ANTLR start "WHERE"
    public final void mWHERE() throws RecognitionException {
        try {
            int _type = WHERE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:213:6: ( 'WHERE' )
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:213:8: 'WHERE'
            {
            match("WHERE"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WHERE"

    // $ANTLR start "ORDER"
    public final void mORDER() throws RecognitionException {
        try {
            int _type = ORDER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:214:6: ( 'ORDER' )
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:214:8: 'ORDER'
            {
            match("ORDER"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ORDER"

    // $ANTLR start "BY"
    public final void mBY() throws RecognitionException {
        try {
            int _type = BY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:215:3: ( 'BY' )
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:215:8: 'BY'
            {
            match("BY"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BY"

    // $ANTLR start "AND"
    public final void mAND() throws RecognitionException {
        try {
            int _type = AND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:216:4: ( 'AND' )
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:216:8: 'AND'
            {
            match("AND"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "AND"

    // $ANTLR start "ASC"
    public final void mASC() throws RecognitionException {
        try {
            int _type = ASC;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:217:4: ( 'ASC' )
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:217:8: 'ASC'
            {
            match("ASC"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ASC"

    // $ANTLR start "DESC"
    public final void mDESC() throws RecognitionException {
        try {
            int _type = DESC;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:218:5: ( 'DESC' )
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:218:8: 'DESC'
            {
            match("DESC"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DESC"

    // $ANTLR start "LIMIT"
    public final void mLIMIT() throws RecognitionException {
        try {
            int _type = LIMIT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:219:6: ( 'LIMIT' )
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:219:8: 'LIMIT'
            {
            match("LIMIT"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LIMIT"

    // $ANTLR start "TRUE"
    public final void mTRUE() throws RecognitionException {
        try {
            int _type = TRUE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:220:5: ( 'true' )
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:220:8: 'true'
            {
            match("true"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TRUE"

    // $ANTLR start "FALSE"
    public final void mFALSE() throws RecognitionException {
        try {
            int _type = FALSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:221:6: ( 'false' )
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:221:8: 'false'
            {
            match("false"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FALSE"

    // $ANTLR start "LT"
    public final void mLT() throws RecognitionException {
        try {
            int _type = LT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:223:4: ( '<' )
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:223:6: '<'
            {
            match('<'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LT"

    // $ANTLR start "LE"
    public final void mLE() throws RecognitionException {
        try {
            int _type = LE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:224:4: ( '<=' )
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:224:6: '<='
            {
            match("<="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LE"

    // $ANTLR start "GT"
    public final void mGT() throws RecognitionException {
        try {
            int _type = GT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:225:4: ( '>' )
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:225:6: '>'
            {
            match('>'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "GT"

    // $ANTLR start "GE"
    public final void mGE() throws RecognitionException {
        try {
            int _type = GE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:226:4: ( '>=' )
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:226:6: '>='
            {
            match(">="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "GE"

    // $ANTLR start "EQ"
    public final void mEQ() throws RecognitionException {
        try {
            int _type = EQ;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:227:4: ( '=' )
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:227:6: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "EQ"

    // $ANTLR start "NE"
    public final void mNE() throws RecognitionException {
        try {
            int _type = NE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:228:4: ( '!=' )
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:228:6: '!='
            {
            match("!="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NE"

    // $ANTLR start "PLACEHOLDER"
    public final void mPLACEHOLDER() throws RecognitionException {
        try {
            int _type = PLACEHOLDER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:230:12: ( '?' )
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:230:14: '?'
            {
            match('?'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PLACEHOLDER"

    // $ANTLR start "COMMA"
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:231:6: ( ',' )
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:231:8: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COMMA"

    // $ANTLR start "DOT"
    public final void mDOT() throws RecognitionException {
        try {
            int _type = DOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:232:4: ( '.' )
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:232:6: '.'
            {
            match('.'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOT"

    // $ANTLR start "CONSTANT_INTEGER"
    public final void mCONSTANT_INTEGER() throws RecognitionException {
        try {
            int _type = CONSTANT_INTEGER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:235:3: ( '0' | '1' .. '9' ( '0' .. '9' )* )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='0') ) {
                alt2=1;
            }
            else if ( ((LA2_0>='1' && LA2_0<='9')) ) {
                alt2=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:235:5: '0'
                    {
                    match('0'); 

                    }
                    break;
                case 2 :
                    // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:236:5: '1' .. '9' ( '0' .. '9' )*
                    {
                    matchRange('1','9'); 
                    // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:236:14: ( '0' .. '9' )*
                    loop1:
                    do {
                        int alt1=2;
                        int LA1_0 = input.LA(1);

                        if ( ((LA1_0>='0' && LA1_0<='9')) ) {
                            alt1=1;
                        }


                        switch (alt1) {
                    	case 1 :
                    	    // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:236:15: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    break loop1;
                        }
                    } while (true);


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CONSTANT_INTEGER"

    // $ANTLR start "CONSTANT_STRING"
    public final void mCONSTANT_STRING() throws RecognitionException {
        try {
            int _type = CONSTANT_STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:240:3: ( '\\'' (~ ( '\\'' ) )* '\\'' )
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:240:5: '\\'' (~ ( '\\'' ) )* '\\''
            {
            match('\''); 
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:240:10: (~ ( '\\'' ) )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( ((LA3_0>='\u0000' && LA3_0<='&')||(LA3_0>='(' && LA3_0<='\uFFFF')) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:240:12: ~ ( '\\'' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);

            match('\''); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CONSTANT_STRING"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:244:3: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )* )
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:244:5: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:244:29: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( ((LA4_0>='0' && LA4_0<='9')||(LA4_0>='A' && LA4_0<='Z')||LA4_0=='_'||(LA4_0>='a' && LA4_0<='z')) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ID"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:248:3: ( ( ' ' | '\\r' | '\\t' | '\\n' ) )
            // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:248:6: ( ' ' | '\\r' | '\\t' | '\\n' )
            {
            if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

             _channel=HIDDEN; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WS"

    public void mTokens() throws RecognitionException {
        // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:1:8: ( WHERE | ORDER | BY | AND | ASC | DESC | LIMIT | TRUE | FALSE | LT | LE | GT | GE | EQ | NE | PLACEHOLDER | COMMA | DOT | CONSTANT_INTEGER | CONSTANT_STRING | ID | WS )
        int alt5=22;
        alt5 = dfa5.predict(input);
        switch (alt5) {
            case 1 :
                // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:1:10: WHERE
                {
                mWHERE(); 

                }
                break;
            case 2 :
                // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:1:16: ORDER
                {
                mORDER(); 

                }
                break;
            case 3 :
                // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:1:22: BY
                {
                mBY(); 

                }
                break;
            case 4 :
                // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:1:25: AND
                {
                mAND(); 

                }
                break;
            case 5 :
                // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:1:29: ASC
                {
                mASC(); 

                }
                break;
            case 6 :
                // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:1:33: DESC
                {
                mDESC(); 

                }
                break;
            case 7 :
                // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:1:38: LIMIT
                {
                mLIMIT(); 

                }
                break;
            case 8 :
                // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:1:44: TRUE
                {
                mTRUE(); 

                }
                break;
            case 9 :
                // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:1:49: FALSE
                {
                mFALSE(); 

                }
                break;
            case 10 :
                // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:1:55: LT
                {
                mLT(); 

                }
                break;
            case 11 :
                // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:1:58: LE
                {
                mLE(); 

                }
                break;
            case 12 :
                // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:1:61: GT
                {
                mGT(); 

                }
                break;
            case 13 :
                // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:1:64: GE
                {
                mGE(); 

                }
                break;
            case 14 :
                // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:1:67: EQ
                {
                mEQ(); 

                }
                break;
            case 15 :
                // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:1:70: NE
                {
                mNE(); 

                }
                break;
            case 16 :
                // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:1:73: PLACEHOLDER
                {
                mPLACEHOLDER(); 

                }
                break;
            case 17 :
                // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:1:85: COMMA
                {
                mCOMMA(); 

                }
                break;
            case 18 :
                // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:1:91: DOT
                {
                mDOT(); 

                }
                break;
            case 19 :
                // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:1:95: CONSTANT_INTEGER
                {
                mCONSTANT_INTEGER(); 

                }
                break;
            case 20 :
                // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:1:112: CONSTANT_STRING
                {
                mCONSTANT_STRING(); 

                }
                break;
            case 21 :
                // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:1:128: ID
                {
                mID(); 

                }
                break;
            case 22 :
                // /usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g:1:131: WS
                {
                mWS(); 

                }
                break;

        }

    }


    protected DFA5 dfa5 = new DFA5(this);
    static final String DFA5_eotS =
        "\1\uffff\10\22\1\36\1\40\11\uffff\2\22\1\43\6\22\4\uffff\2\22\1"+
        "\uffff\1\54\1\55\6\22\2\uffff\1\64\1\22\1\66\1\22\1\70\1\71\1\uffff"+
        "\1\72\1\uffff\1\73\4\uffff";
    static final String DFA5_eofS =
        "\74\uffff";
    static final String DFA5_minS =
        "\1\11\1\110\1\122\1\131\1\116\1\105\1\111\1\162\1\141\2\75\11\uffff"+
        "\1\105\1\104\1\60\1\104\1\103\1\123\1\115\1\165\1\154\4\uffff\1"+
        "\122\1\105\1\uffff\2\60\1\103\1\111\1\145\1\163\1\105\1\122\2\uffff"+
        "\1\60\1\124\1\60\1\145\2\60\1\uffff\1\60\1\uffff\1\60\4\uffff";
    static final String DFA5_maxS =
        "\1\172\1\110\1\122\1\131\1\123\1\105\1\111\1\162\1\141\2\75\11\uffff"+
        "\1\105\1\104\1\172\1\104\1\103\1\123\1\115\1\165\1\154\4\uffff\1"+
        "\122\1\105\1\uffff\2\172\1\103\1\111\1\145\1\163\1\105\1\122\2\uffff"+
        "\1\172\1\124\1\172\1\145\2\172\1\uffff\1\172\1\uffff\1\172\4\uffff";
    static final String DFA5_acceptS =
        "\13\uffff\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26\11\uffff"+
        "\1\13\1\12\1\15\1\14\2\uffff\1\3\10\uffff\1\4\1\5\6\uffff\1\6\1"+
        "\uffff\1\10\1\uffff\1\1\1\2\1\7\1\11";
    static final String DFA5_specialS =
        "\74\uffff}>";
    static final String[] DFA5_transitionS = {
            "\2\23\2\uffff\1\23\22\uffff\1\23\1\14\5\uffff\1\21\4\uffff\1"+
            "\16\1\uffff\1\17\1\uffff\12\20\2\uffff\1\11\1\13\1\12\1\15\1"+
            "\uffff\1\4\1\3\1\22\1\5\7\22\1\6\2\22\1\2\7\22\1\1\3\22\4\uffff"+
            "\1\22\1\uffff\5\22\1\10\15\22\1\7\6\22",
            "\1\24",
            "\1\25",
            "\1\26",
            "\1\27\4\uffff\1\30",
            "\1\31",
            "\1\32",
            "\1\33",
            "\1\34",
            "\1\35",
            "\1\37",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\41",
            "\1\42",
            "\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\32\22",
            "\1\44",
            "\1\45",
            "\1\46",
            "\1\47",
            "\1\50",
            "\1\51",
            "",
            "",
            "",
            "",
            "\1\52",
            "\1\53",
            "",
            "\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\32\22",
            "\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\32\22",
            "\1\56",
            "\1\57",
            "\1\60",
            "\1\61",
            "\1\62",
            "\1\63",
            "",
            "",
            "\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\32\22",
            "\1\65",
            "\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\32\22",
            "\1\67",
            "\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\32\22",
            "\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\32\22",
            "",
            "\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\32\22",
            "",
            "\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\32\22",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA5_eot = DFA.unpackEncodedString(DFA5_eotS);
    static final short[] DFA5_eof = DFA.unpackEncodedString(DFA5_eofS);
    static final char[] DFA5_min = DFA.unpackEncodedStringToUnsignedChars(DFA5_minS);
    static final char[] DFA5_max = DFA.unpackEncodedStringToUnsignedChars(DFA5_maxS);
    static final short[] DFA5_accept = DFA.unpackEncodedString(DFA5_acceptS);
    static final short[] DFA5_special = DFA.unpackEncodedString(DFA5_specialS);
    static final short[][] DFA5_transition;

    static {
        int numStates = DFA5_transitionS.length;
        DFA5_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA5_transition[i] = DFA.unpackEncodedString(DFA5_transitionS[i]);
        }
    }

    class DFA5 extends DFA {

        public DFA5(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 5;
            this.eot = DFA5_eot;
            this.eof = DFA5_eof;
            this.min = DFA5_min;
            this.max = DFA5_max;
            this.accept = DFA5_accept;
            this.special = DFA5_special;
            this.transition = DFA5_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( WHERE | ORDER | BY | AND | ASC | DESC | LIMIT | TRUE | FALSE | LT | LE | GT | GE | EQ | NE | PLACEHOLDER | COMMA | DOT | CONSTANT_INTEGER | CONSTANT_STRING | ID | WS );";
        }
    }
 

}