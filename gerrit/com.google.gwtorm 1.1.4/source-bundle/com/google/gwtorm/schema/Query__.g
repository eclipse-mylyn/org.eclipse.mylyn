lexer grammar Query;
options {
  language=Java;

}

@members {
    public void displayRecognitionError(String[] tokenNames,
                                        RecognitionException e) {
        String hdr = getErrorHeader(e);
        String msg = getErrorMessage(e, tokenNames);
        throw new QueryParseInternalException(hdr + " " + msg);
    }
}
@header {
package com.google.gwtorm.schema;
}

// $ANTLR src "/usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g" 213
WHERE: 'WHERE' ;
// $ANTLR src "/usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g" 214
ORDER: 'ORDER' ;
// $ANTLR src "/usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g" 215
BY:    'BY'    ;
// $ANTLR src "/usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g" 216
AND:   'AND'   ;
// $ANTLR src "/usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g" 217
ASC:   'ASC'   ;
// $ANTLR src "/usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g" 218
DESC:  'DESC'  ;
// $ANTLR src "/usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g" 219
LIMIT: 'LIMIT' ;
// $ANTLR src "/usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g" 220
TRUE:  'true'  ;
// $ANTLR src "/usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g" 221
FALSE: 'false' ;

// $ANTLR src "/usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g" 223
LT : '<'  ;
// $ANTLR src "/usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g" 224
LE : '<=' ;
// $ANTLR src "/usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g" 225
GT : '>'  ;
// $ANTLR src "/usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g" 226
GE : '>=' ;
// $ANTLR src "/usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g" 227
EQ : '='  ;
// $ANTLR src "/usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g" 228
NE : '!=' ;

// $ANTLR src "/usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g" 230
PLACEHOLDER: '?' ;
// $ANTLR src "/usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g" 231
COMMA: ',' ;
// $ANTLR src "/usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g" 232
DOT: '.' ;

// $ANTLR src "/usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g" 234
CONSTANT_INTEGER
  : '0'
  | '1'..'9' ('0'..'9')*
  ;

// $ANTLR src "/usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g" 239
CONSTANT_STRING
  : '\'' ( ~('\'') )* '\''
  ;

// $ANTLR src "/usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g" 243
ID
  : ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*
  ;

// $ANTLR src "/usr/local/google/users/sop/gerrit2/gwtorm/src/main/antlr/com/google/gwtorm/schema/Query.g" 247
WS
  :  ( ' ' | '\r' | '\t' | '\n' ) { $channel=HIDDEN; }
  ;
