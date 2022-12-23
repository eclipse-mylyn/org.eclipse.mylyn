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

// $ANTLR 3.0 ReviewDsl.g 2011-03-06 18:30:25

package org.eclipse.mylyn.reviews.tasks.dsl.internal;

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.BitSet;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RuleReturnScope;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.TreeAdaptor;

/**
 * 
 * @author mattk
 *
 */
public class ReviewDslParser extends Parser {
	 static final String[] tokenNames = new String[] { "<invalid>",
			"<EOR>", "<DOWN>", "<UP>", "STRING", "INT", "TASK_ID", "ESC_SEQ",
			"UNICODE_ESC", "OCTAL_ESC", "HEX_DIGIT", "WS", "'Review'",
			"'result:'", "'Comment:'", "'PASSED'", "'WARNING'", "'FAILED'",
			"'TODO'", "'File'", "':'", "'Line'", "'-'", "'scope:'",
			"'Resource'", "'Changeset'", "'from'", "'Patch'", "'Attachment'",
			"'by'", "'on'", "'of'", "'task'" };
	public static final int WS = 11;
	public static final int ESC_SEQ = 7;
	public static final int TASK_ID = 6;
	public static final int UNICODE_ESC = 8;
	public static final int OCTAL_ESC = 9;
	public static final int HEX_DIGIT = 10;
	public static final int INT = 5;
	public static final int EOF = -1;
	public static final int STRING = 4;

	public ReviewDslParser(TokenStream input) {
		super(input);
	}

	protected TreeAdaptor adaptor = new CommonTreeAdaptor();

	public void setTreeAdaptor(TreeAdaptor adaptor) {
		this.adaptor = adaptor;
	}

	public TreeAdaptor getTreeAdaptor() {
		return adaptor;
	}

	public String[] getTokenNames() {
		return tokenNames.clone();
	}

	public String getGrammarFileName() {
		return "ReviewDsl.g";
	}

	public static class reviewResult_return extends ParserRuleReturnScope {
		public String result;
		public String comment;
		public List fileComments;
		CommonTree tree;

		public Object getTree() {
			return tree;
		}
	};

	// $ANTLR start reviewResult
	// ReviewDsl.g:14:1: reviewResult returns [String result, String comment,
	// List fileComments] : 'Review' 'result:' res= resultEnum ( 'Comment:' c=
	// STRING )? ( (fc+= fileComment )+ )? ;
	public final reviewResult_return reviewResult() throws RecognitionException {
		reviewResult_return retval = new reviewResult_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token c = null;
		Token string_literal1 = null;
		Token string_literal2 = null;
		Token string_literal3 = null;
		List list_fc = null;
		resultEnum_return res = null;

		RuleReturnScope fc = null;
		CommonTree c_tree = null;
		CommonTree string_literal1_tree = null;
		CommonTree string_literal2_tree = null;
		CommonTree string_literal3_tree = null;

		try {
			// ReviewDsl.g:15:3: ( 'Review' 'result:' res= resultEnum (
			// 'Comment:' c= STRING )? ( (fc+= fileComment )+ )? )
			// ReviewDsl.g:15:3: 'Review' 'result:' res= resultEnum ( 'Comment:'
			// c= STRING )? ( (fc+= fileComment )+ )?
			{
				root_0 = (CommonTree) adaptor.nil();

				string_literal1 = (Token) input.LT(1);
				match(input, 12, FOLLOW_12_in_reviewResult39);
				string_literal1_tree = (CommonTree) adaptor
						.create(string_literal1);
				adaptor.addChild(root_0, string_literal1_tree);

				string_literal2 = (Token) input.LT(1);
				match(input, 13, FOLLOW_13_in_reviewResult41);
				string_literal2_tree = (CommonTree) adaptor
						.create(string_literal2);
				adaptor.addChild(root_0, string_literal2_tree);

				pushFollow(FOLLOW_resultEnum_in_reviewResult46);
				res = resultEnum();
				_fsp--;

				adaptor.addChild(root_0, res.getTree());
				// ReviewDsl.g:16:3: ( 'Comment:' c= STRING )?
				int alt1 = 2;
				int LA1_0 = input.LA(1);

				if ((LA1_0 == 14)) {
					alt1 = 1;
				}
				switch (alt1) {
				case 1:
					// ReviewDsl.g:16:4: 'Comment:' c= STRING
				{
					string_literal3 = (Token) input.LT(1);
					match(input, 14, FOLLOW_14_in_reviewResult52);
					string_literal3_tree = (CommonTree) adaptor
							.create(string_literal3);
					adaptor.addChild(root_0, string_literal3_tree);

					c = (Token) input.LT(1);
					match(input, STRING, FOLLOW_STRING_in_reviewResult56);
					c_tree = (CommonTree) adaptor.create(c);
					adaptor.addChild(root_0, c_tree);

				}
					break;

				}

				// ReviewDsl.g:17:2: ( (fc+= fileComment )+ )?
				int alt3 = 2;
				int LA3_0 = input.LA(1);

				if ((LA3_0 == 19)) {
					alt3 = 1;
				}
				switch (alt3) {
				case 1:
					// ReviewDsl.g:17:3: (fc+= fileComment )+
				{
					// ReviewDsl.g:17:5: (fc+= fileComment )+
					int cnt2 = 0;
					loop2: do {
						int alt2 = 2;
						int LA2_0 = input.LA(1);

						if ((LA2_0 == 19)) {
							alt2 = 1;
						}

						switch (alt2) {
						case 1:
							// ReviewDsl.g:17:5: fc+= fileComment
						{
							pushFollow(FOLLOW_fileComment_in_reviewResult64);
							fc = fileComment();
							_fsp--;

							adaptor.addChild(root_0, fc.getTree());
							if (list_fc == null)
								list_fc = new ArrayList();
							list_fc.add(fc);

						}
							break;

						default:
							if (cnt2 >= 1)
								break loop2;
							EarlyExitException eee = new EarlyExitException(2,
									input);
							throw eee;
						}
						cnt2++;
					} while (true);

				}
					break;

				}

				retval.result = input.toString(res.start, res.stop);
				retval.comment = c != null ? c.getText() : null;
				retval.fileComments = list_fc;

			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end reviewResult

	public static class resultEnum_return extends ParserRuleReturnScope {
		CommonTree tree;

		public Object getTree() {
			return tree;
		}
	};

	// $ANTLR start resultEnum
	// ReviewDsl.g:21:1: resultEnum : ( 'PASSED' | 'WARNING' | 'FAILED' | 'TODO'
	// );
	public final resultEnum_return resultEnum() throws RecognitionException {
		resultEnum_return retval = new resultEnum_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token set4 = null;

		try {
			// ReviewDsl.g:22:3: ( 'PASSED' | 'WARNING' | 'FAILED' | 'TODO' )
			// ReviewDsl.g:
			{
				root_0 = (CommonTree) adaptor.nil();

				set4 = (Token) input.LT(1);
				if ((input.LA(1) >= 15 && input.LA(1) <= 18)) {
					input.consume();
					adaptor.addChild(root_0, adaptor.create(set4));
					errorRecovery = false;
				} else {
					MismatchedSetException mse = new MismatchedSetException(
							null, input);
					recoverFromMismatchedSet(input, mse,
							FOLLOW_set_in_resultEnum0);
					throw mse;
				}

			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end resultEnum

	public static class fileComment_return extends ParserRuleReturnScope {
		public String path;
		public String comment;
		public List lineComments;
		CommonTree tree;

		public Object getTree() {
			return tree;
		}
	};

	// $ANTLR start fileComment
	// ReviewDsl.g:24:1: fileComment returns [String path, String comment, List
	// lineComments] : 'File' p= STRING ':' (c= STRING )? (lc+= lineComment )* ;
	public final fileComment_return fileComment() throws RecognitionException {
		fileComment_return retval = new fileComment_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token p = null;
		Token c = null;
		Token string_literal5 = null;
		Token char_literal6 = null;
		List list_lc = null;
		RuleReturnScope lc = null;
		CommonTree p_tree = null;
		CommonTree c_tree = null;
		CommonTree string_literal5_tree = null;
		CommonTree char_literal6_tree = null;

		try {
			// ReviewDsl.g:25:2: ( 'File' p= STRING ':' (c= STRING )? (lc+=
			// lineComment )* )
			// ReviewDsl.g:25:2: 'File' p= STRING ':' (c= STRING )? (lc+=
			// lineComment )*
			{
				root_0 = (CommonTree) adaptor.nil();

				string_literal5 = (Token) input.LT(1);
				match(input, 19, FOLLOW_19_in_fileComment98);
				string_literal5_tree = (CommonTree) adaptor
						.create(string_literal5);
				adaptor.addChild(root_0, string_literal5_tree);

				p = (Token) input.LT(1);
				match(input, STRING, FOLLOW_STRING_in_fileComment102);
				p_tree = (CommonTree) adaptor.create(p);
				adaptor.addChild(root_0, p_tree);

				char_literal6 = (Token) input.LT(1);
				match(input, 20, FOLLOW_20_in_fileComment104);
				char_literal6_tree = (CommonTree) adaptor.create(char_literal6);
				adaptor.addChild(root_0, char_literal6_tree);

				// ReviewDsl.g:25:22: (c= STRING )?
				int alt4 = 2;
				int LA4_0 = input.LA(1);

				if ((LA4_0 == STRING)) {
					alt4 = 1;
				}
				switch (alt4) {
				case 1:
					// ReviewDsl.g:25:23: c= STRING
				{
					c = (Token) input.LT(1);
					match(input, STRING, FOLLOW_STRING_in_fileComment109);
					c_tree = (CommonTree) adaptor.create(c);
					adaptor.addChild(root_0, c_tree);

				}
					break;

				}

				// ReviewDsl.g:26:3: (lc+= lineComment )*
				loop5: do {
					int alt5 = 2;
					int LA5_0 = input.LA(1);

					if ((LA5_0 == 21)) {
						alt5 = 1;
					}

					switch (alt5) {
					case 1:
						// ReviewDsl.g:26:3: lc+= lineComment
					{
						pushFollow(FOLLOW_lineComment_in_fileComment115);
						lc = lineComment();
						_fsp--;

						adaptor.addChild(root_0, lc.getTree());
						if (list_lc == null)
							list_lc = new ArrayList();
						list_lc.add(lc);

					}
						break;

					default:
						break loop5;
					}
				} while (true);

				retval.path = p.getText();
				retval.comment = c.getText();
				retval.lineComments = list_lc;

			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end fileComment

	public static class lineComment_return extends ParserRuleReturnScope {
		public int begin;
		public String end;
		public String comment;
		CommonTree tree;

		public Object getTree() {
			return tree;
		}
	};

	// $ANTLR start lineComment
	// ReviewDsl.g:29:1: lineComment returns [int begin, String end, String
	// comment] : 'Line' s= INT ( '-' e= INT )? ':' c= STRING ;
	public final lineComment_return lineComment() throws RecognitionException {
		lineComment_return retval = new lineComment_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token s = null;
		Token e = null;
		Token c = null;
		Token string_literal7 = null;
		Token char_literal8 = null;
		Token char_literal9 = null;

		CommonTree s_tree = null;
		CommonTree e_tree = null;
		CommonTree c_tree = null;
		CommonTree string_literal7_tree = null;
		CommonTree char_literal8_tree = null;
		CommonTree char_literal9_tree = null;

		try {
			// ReviewDsl.g:30:3: ( 'Line' s= INT ( '-' e= INT )? ':' c= STRING )
			// ReviewDsl.g:30:3: 'Line' s= INT ( '-' e= INT )? ':' c= STRING
			{
				root_0 = (CommonTree) adaptor.nil();

				string_literal7 = (Token) input.LT(1);
				match(input, 21, FOLLOW_21_in_lineComment130);
				string_literal7_tree = (CommonTree) adaptor
						.create(string_literal7);
				adaptor.addChild(root_0, string_literal7_tree);

				s = (Token) input.LT(1);
				match(input, INT, FOLLOW_INT_in_lineComment134);
				s_tree = (CommonTree) adaptor.create(s);
				adaptor.addChild(root_0, s_tree);

				// ReviewDsl.g:30:16: ( '-' e= INT )?
				int alt6 = 2;
				int LA6_0 = input.LA(1);

				if ((LA6_0 == 22)) {
					alt6 = 1;
				}
				switch (alt6) {
				case 1:
					// ReviewDsl.g:30:17: '-' e= INT
				{
					char_literal8 = (Token) input.LT(1);
					match(input, 22, FOLLOW_22_in_lineComment137);
					char_literal8_tree = (CommonTree) adaptor
							.create(char_literal8);
					adaptor.addChild(root_0, char_literal8_tree);

					e = (Token) input.LT(1);
					match(input, INT, FOLLOW_INT_in_lineComment141);
					e_tree = (CommonTree) adaptor.create(e);
					adaptor.addChild(root_0, e_tree);

				}
					break;

				}

				char_literal9 = (Token) input.LT(1);
				match(input, 20, FOLLOW_20_in_lineComment145);
				char_literal9_tree = (CommonTree) adaptor.create(char_literal9);
				adaptor.addChild(root_0, char_literal9_tree);

				c = (Token) input.LT(1);
				match(input, STRING, FOLLOW_STRING_in_lineComment150);
				c_tree = (CommonTree) adaptor.create(c);
				adaptor.addChild(root_0, c_tree);

				retval.begin = Integer.parseInt(s.getText());
				retval.end = e != null ? e.getText() : null;
				retval.comment = c.getText();

			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end lineComment

	public static class reviewScope_return extends ParserRuleReturnScope {
		public List scopeItems;
		CommonTree tree;

		public Object getTree() {
			return tree;
		}
	};

	// $ANTLR start reviewScope
	// ReviewDsl.g:35:1: reviewScope returns [List scopeItems] : 'Review'
	// 'scope:' (s+= ( resourceDef | patchDef | changesetDef ) )* ;
	public final reviewScope_return reviewScope() throws RecognitionException {
		reviewScope_return retval = new reviewScope_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token string_literal10 = null;
		Token string_literal11 = null;
		List list_s = new ArrayList();
		resourceDef_return resourceDef12 = null;

		patchDef_return patchDef13 = null;

		changesetDef_return changesetDef14 = null;

		CommonTree string_literal10_tree = null;
		CommonTree string_literal11_tree = null;

		try {
			// ReviewDsl.g:36:4: ( 'Review' 'scope:' (s+= ( resourceDef |
			// patchDef | changesetDef ) )* )
			// ReviewDsl.g:36:4: 'Review' 'scope:' (s+= ( resourceDef | patchDef
			// | changesetDef ) )*
			{
				root_0 = (CommonTree) adaptor.nil();

				string_literal10 = (Token) input.LT(1);
				match(input, 12, FOLLOW_12_in_reviewScope167);
				string_literal10_tree = (CommonTree) adaptor
						.create(string_literal10);
				adaptor.addChild(root_0, string_literal10_tree);

				string_literal11 = (Token) input.LT(1);
				match(input, 23, FOLLOW_23_in_reviewScope169);
				string_literal11_tree = (CommonTree) adaptor
						.create(string_literal11);
				adaptor.addChild(root_0, string_literal11_tree);

				// ReviewDsl.g:36:22: (s+= ( resourceDef | patchDef |
				// changesetDef ) )*
				loop8: do {
					int alt8 = 2;
					int LA8_0 = input.LA(1);

					if (((LA8_0 >= 24 && LA8_0 <= 25) || LA8_0 == 27)) {
						alt8 = 1;
					}

					switch (alt8) {
					case 1:
						// ReviewDsl.g:36:23: s+= ( resourceDef | patchDef |
						// changesetDef )
					{
						// ReviewDsl.g:36:26: ( resourceDef | patchDef |
						// changesetDef )
						int alt7 = 3;
						switch (input.LA(1)) {
						case 24: {
							alt7 = 1;
						}
							break;
						case 27: {
							alt7 = 2;
						}
							break;
						case 25: {
							alt7 = 3;
						}
							break;
						default:
							NoViableAltException nvae = new NoViableAltException(
									"36:26: ( resourceDef | patchDef | changesetDef )",
									7, 0, input);

							throw nvae;
						}

						switch (alt7) {
						case 1:
							// ReviewDsl.g:36:27: resourceDef
						{
							pushFollow(FOLLOW_resourceDef_in_reviewScope175);
							resourceDef12 = resourceDef();
							list_s.add(resourceDef12);
							_fsp--;

							adaptor.addChild(root_0, resourceDef12.getTree());

						}
							break;
						case 2:
							// ReviewDsl.g:36:40: patchDef
						{
							pushFollow(FOLLOW_patchDef_in_reviewScope178);
							patchDef13 = patchDef();
							list_s.add(patchDef13);
							_fsp--;

							adaptor.addChild(root_0, patchDef13.getTree());

						}
							break;
						case 3:
							// ReviewDsl.g:36:52: changesetDef
						{
							pushFollow(FOLLOW_changesetDef_in_reviewScope183);
							changesetDef14 = changesetDef();
							list_s.add(changesetDef14);
							_fsp--;

							adaptor.addChild(root_0, changesetDef14.getTree());

						}
							break;

						}

					}
						break;

					default:
						break loop8;
					}
				} while (true);

				retval.scopeItems = list_s;

			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end reviewScope

	public static class resourceDef_return extends ParserRuleReturnScope {
		public Object source;
		CommonTree tree;

		public Object getTree() {
			return tree;
		}
	};

	// $ANTLR start resourceDef
	// ReviewDsl.g:40:1: resourceDef returns [Object source] : 'Resource' s=
	// attachmentSource ;
	public final resourceDef_return resourceDef() throws RecognitionException {
		resourceDef_return retval = new resourceDef_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token string_literal15 = null;
		attachmentSource_return s = null;

		CommonTree string_literal15_tree = null;

		try {
			// ReviewDsl.g:41:4: ( 'Resource' s= attachmentSource )
			// ReviewDsl.g:41:4: 'Resource' s= attachmentSource
			{
				root_0 = (CommonTree) adaptor.nil();

				string_literal15 = (Token) input.LT(1);
				match(input, 24, FOLLOW_24_in_resourceDef205);
				string_literal15_tree = (CommonTree) adaptor
						.create(string_literal15);
				adaptor.addChild(root_0, string_literal15_tree);

				pushFollow(FOLLOW_attachmentSource_in_resourceDef210);
				s = attachmentSource();
				_fsp--;

				adaptor.addChild(root_0, s.getTree());
				retval.source = s;

			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end resourceDef

	public static class changesetDef_return extends ParserRuleReturnScope {
		public String revision;
		public String repoUrl;
		CommonTree tree;

		public Object getTree() {
			return tree;
		}
	};

	// $ANTLR start changesetDef
	// ReviewDsl.g:44:1: changesetDef returns [String revision, String repoUrl]
	// : 'Changeset' rev= STRING 'from' url= STRING ;
	public final changesetDef_return changesetDef() throws RecognitionException {
		changesetDef_return retval = new changesetDef_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token rev = null;
		Token url = null;
		Token string_literal16 = null;
		Token string_literal17 = null;

		CommonTree rev_tree = null;
		CommonTree url_tree = null;
		CommonTree string_literal16_tree = null;
		CommonTree string_literal17_tree = null;

		try {
			// ReviewDsl.g:45:4: ( 'Changeset' rev= STRING 'from' url= STRING )
			// ReviewDsl.g:45:4: 'Changeset' rev= STRING 'from' url= STRING
			{
				root_0 = (CommonTree) adaptor.nil();

				string_literal16 = (Token) input.LT(1);
				match(input, 25, FOLLOW_25_in_changesetDef226);
				string_literal16_tree = (CommonTree) adaptor
						.create(string_literal16);
				adaptor.addChild(root_0, string_literal16_tree);

				rev = (Token) input.LT(1);
				match(input, STRING, FOLLOW_STRING_in_changesetDef230);
				rev_tree = (CommonTree) adaptor.create(rev);
				adaptor.addChild(root_0, rev_tree);

				string_literal17 = (Token) input.LT(1);
				match(input, 26, FOLLOW_26_in_changesetDef233);
				string_literal17_tree = (CommonTree) adaptor
						.create(string_literal17);
				adaptor.addChild(root_0, string_literal17_tree);

				url = (Token) input.LT(1);
				match(input, STRING, FOLLOW_STRING_in_changesetDef237);
				url_tree = (CommonTree) adaptor.create(url);
				adaptor.addChild(root_0, url_tree);

				retval.revision = rev.getText();
				retval.repoUrl = url.getText();

			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end changesetDef

	public static class patchDef_return extends ParserRuleReturnScope {
		public Object source;
		CommonTree tree;

		public Object getTree() {
			return tree;
		}
	};

	// $ANTLR start patchDef
	// ReviewDsl.g:48:1: patchDef returns [Object source] : 'Patch' s=
	// attachmentSource ;
	public final patchDef_return patchDef() throws RecognitionException {
		patchDef_return retval = new patchDef_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token string_literal18 = null;
		attachmentSource_return s = null;

		CommonTree string_literal18_tree = null;

		try {
			// ReviewDsl.g:49:2: ( 'Patch' s= attachmentSource )
			// ReviewDsl.g:49:2: 'Patch' s= attachmentSource
			{
				root_0 = (CommonTree) adaptor.nil();

				string_literal18 = (Token) input.LT(1);
				match(input, 27, FOLLOW_27_in_patchDef252);
				string_literal18_tree = (CommonTree) adaptor
						.create(string_literal18);
				adaptor.addChild(root_0, string_literal18_tree);

				pushFollow(FOLLOW_attachmentSource_in_patchDef256);
				s = attachmentSource();
				_fsp--;

				adaptor.addChild(root_0, s.getTree());
				retval.source = s;

			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end patchDef

	public static class attachmentSource_return extends ParserRuleReturnScope {
		public String filename;
		public String author;
		String createdDate;
		public String taskId;
		CommonTree tree;

		public Object getTree() {
			return tree;
		}
	};

	// $ANTLR start attachmentSource
	// ReviewDsl.g:52:1: attachmentSource returns [String filename, String
	// author; String createdDate, String taskId] : 'from' 'Attachment' fn=
	// STRING 'by' a= STRING 'on' d= STRING 'of' 'task' t= taskIdDef ;
	public final attachmentSource_return attachmentSource()
			throws RecognitionException {
		attachmentSource_return retval = new attachmentSource_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token fn = null;
		Token a = null;
		Token d = null;
		Token string_literal19 = null;
		Token string_literal20 = null;
		Token string_literal21 = null;
		Token string_literal22 = null;
		Token string_literal23 = null;
		Token string_literal24 = null;
		taskIdDef_return t = null;

		CommonTree fn_tree = null;
		CommonTree a_tree = null;
		CommonTree d_tree = null;
		CommonTree string_literal19_tree = null;
		CommonTree string_literal20_tree = null;
		CommonTree string_literal21_tree = null;
		CommonTree string_literal22_tree = null;
		CommonTree string_literal23_tree = null;
		CommonTree string_literal24_tree = null;

		try {
			// ReviewDsl.g:53:2: ( 'from' 'Attachment' fn= STRING 'by' a= STRING
			// 'on' d= STRING 'of' 'task' t= taskIdDef )
			// ReviewDsl.g:53:2: 'from' 'Attachment' fn= STRING 'by' a= STRING
			// 'on' d= STRING 'of' 'task' t= taskIdDef
			{
				root_0 = (CommonTree) adaptor.nil();

				string_literal19 = (Token) input.LT(1);
				match(input, 26, FOLLOW_26_in_attachmentSource274);
				string_literal19_tree = (CommonTree) adaptor
						.create(string_literal19);
				adaptor.addChild(root_0, string_literal19_tree);

				string_literal20 = (Token) input.LT(1);
				match(input, 28, FOLLOW_28_in_attachmentSource276);
				string_literal20_tree = (CommonTree) adaptor
						.create(string_literal20);
				adaptor.addChild(root_0, string_literal20_tree);

				fn = (Token) input.LT(1);
				match(input, STRING, FOLLOW_STRING_in_attachmentSource280);
				fn_tree = (CommonTree) adaptor.create(fn);
				adaptor.addChild(root_0, fn_tree);

				string_literal21 = (Token) input.LT(1);
				match(input, 29, FOLLOW_29_in_attachmentSource284);
				string_literal21_tree = (CommonTree) adaptor
						.create(string_literal21);
				adaptor.addChild(root_0, string_literal21_tree);

				a = (Token) input.LT(1);
				match(input, STRING, FOLLOW_STRING_in_attachmentSource288);
				a_tree = (CommonTree) adaptor.create(a);
				adaptor.addChild(root_0, a_tree);

				string_literal22 = (Token) input.LT(1);
				match(input, 30, FOLLOW_30_in_attachmentSource292);
				string_literal22_tree = (CommonTree) adaptor
						.create(string_literal22);
				adaptor.addChild(root_0, string_literal22_tree);

				d = (Token) input.LT(1);
				match(input, STRING, FOLLOW_STRING_in_attachmentSource296);
				d_tree = (CommonTree) adaptor.create(d);
				adaptor.addChild(root_0, d_tree);

				string_literal23 = (Token) input.LT(1);
				match(input, 31, FOLLOW_31_in_attachmentSource300);
				string_literal23_tree = (CommonTree) adaptor
						.create(string_literal23);
				adaptor.addChild(root_0, string_literal23_tree);

				string_literal24 = (Token) input.LT(1);
				match(input, 32, FOLLOW_32_in_attachmentSource302);
				string_literal24_tree = (CommonTree) adaptor
						.create(string_literal24);
				adaptor.addChild(root_0, string_literal24_tree);

				pushFollow(FOLLOW_taskIdDef_in_attachmentSource306);
				t = taskIdDef();
				_fsp--;

				adaptor.addChild(root_0, t.getTree());
				retval.filename = fn.getText();
				retval.author = a.getText();
				retval.createdDate = d.getText();
				retval.taskId = input.toString(t.start, t.stop);

			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end attachmentSource

	public static class taskIdDef_return extends ParserRuleReturnScope {
		CommonTree tree;

		public Object getTree() {
			return tree;
		}
	};

	// $ANTLR start taskIdDef
	// ReviewDsl.g:60:1: taskIdDef : ( TASK_ID | INT );
	public final taskIdDef_return taskIdDef() throws RecognitionException {
		taskIdDef_return retval = new taskIdDef_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token set25 = null;

		try {
			// ReviewDsl.g:61:4: ( TASK_ID | INT )
			// ReviewDsl.g:
			{
				root_0 = (CommonTree) adaptor.nil();

				set25 = (Token) input.LT(1);
				if ((input.LA(1) >= INT && input.LA(1) <= TASK_ID)) {
					input.consume();
					adaptor.addChild(root_0, adaptor.create(set25));
					errorRecovery = false;
				} else {
					MismatchedSetException mse = new MismatchedSetException(
							null, input);
					recoverFromMismatchedSet(input, mse,
							FOLLOW_set_in_taskIdDef0);
					throw mse;
				}

			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}

	// $ANTLR end taskIdDef

	public static final BitSet FOLLOW_12_in_reviewResult39 = new BitSet(
			new long[] { 0x0000000000002000L });
	public static final BitSet FOLLOW_13_in_reviewResult41 = new BitSet(
			new long[] { 0x0000000000078000L });
	public static final BitSet FOLLOW_resultEnum_in_reviewResult46 = new BitSet(
			new long[] { 0x0000000000084002L });
	public static final BitSet FOLLOW_14_in_reviewResult52 = new BitSet(
			new long[] { 0x0000000000000010L });
	public static final BitSet FOLLOW_STRING_in_reviewResult56 = new BitSet(
			new long[] { 0x0000000000080002L });
	public static final BitSet FOLLOW_fileComment_in_reviewResult64 = new BitSet(
			new long[] { 0x0000000000080002L });
	public static final BitSet FOLLOW_set_in_resultEnum0 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_19_in_fileComment98 = new BitSet(
			new long[] { 0x0000000000000010L });
	public static final BitSet FOLLOW_STRING_in_fileComment102 = new BitSet(
			new long[] { 0x0000000000100000L });
	public static final BitSet FOLLOW_20_in_fileComment104 = new BitSet(
			new long[] { 0x0000000000200012L });
	public static final BitSet FOLLOW_STRING_in_fileComment109 = new BitSet(
			new long[] { 0x0000000000200002L });
	public static final BitSet FOLLOW_lineComment_in_fileComment115 = new BitSet(
			new long[] { 0x0000000000200002L });
	public static final BitSet FOLLOW_21_in_lineComment130 = new BitSet(
			new long[] { 0x0000000000000020L });
	public static final BitSet FOLLOW_INT_in_lineComment134 = new BitSet(
			new long[] { 0x0000000000500000L });
	public static final BitSet FOLLOW_22_in_lineComment137 = new BitSet(
			new long[] { 0x0000000000000020L });
	public static final BitSet FOLLOW_INT_in_lineComment141 = new BitSet(
			new long[] { 0x0000000000100000L });
	public static final BitSet FOLLOW_20_in_lineComment145 = new BitSet(
			new long[] { 0x0000000000000010L });
	public static final BitSet FOLLOW_STRING_in_lineComment150 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_12_in_reviewScope167 = new BitSet(
			new long[] { 0x0000000000800000L });
	public static final BitSet FOLLOW_23_in_reviewScope169 = new BitSet(
			new long[] { 0x000000000B000002L });
	public static final BitSet FOLLOW_resourceDef_in_reviewScope175 = new BitSet(
			new long[] { 0x000000000B000002L });
	public static final BitSet FOLLOW_patchDef_in_reviewScope178 = new BitSet(
			new long[] { 0x000000000B000002L });
	public static final BitSet FOLLOW_changesetDef_in_reviewScope183 = new BitSet(
			new long[] { 0x000000000B000002L });
	public static final BitSet FOLLOW_24_in_resourceDef205 = new BitSet(
			new long[] { 0x0000000004000000L });
	public static final BitSet FOLLOW_attachmentSource_in_resourceDef210 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_25_in_changesetDef226 = new BitSet(
			new long[] { 0x0000000000000010L });
	public static final BitSet FOLLOW_STRING_in_changesetDef230 = new BitSet(
			new long[] { 0x0000000004000000L });
	public static final BitSet FOLLOW_26_in_changesetDef233 = new BitSet(
			new long[] { 0x0000000000000010L });
	public static final BitSet FOLLOW_STRING_in_changesetDef237 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_27_in_patchDef252 = new BitSet(
			new long[] { 0x0000000004000000L });
	public static final BitSet FOLLOW_attachmentSource_in_patchDef256 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_26_in_attachmentSource274 = new BitSet(
			new long[] { 0x0000000010000000L });
	public static final BitSet FOLLOW_28_in_attachmentSource276 = new BitSet(
			new long[] { 0x0000000000000010L });
	public static final BitSet FOLLOW_STRING_in_attachmentSource280 = new BitSet(
			new long[] { 0x0000000020000000L });
	public static final BitSet FOLLOW_29_in_attachmentSource284 = new BitSet(
			new long[] { 0x0000000000000010L });
	public static final BitSet FOLLOW_STRING_in_attachmentSource288 = new BitSet(
			new long[] { 0x0000000040000000L });
	public static final BitSet FOLLOW_30_in_attachmentSource292 = new BitSet(
			new long[] { 0x0000000000000010L });
	public static final BitSet FOLLOW_STRING_in_attachmentSource296 = new BitSet(
			new long[] { 0x0000000080000000L });
	public static final BitSet FOLLOW_31_in_attachmentSource300 = new BitSet(
			new long[] { 0x0000000100000000L });
	public static final BitSet FOLLOW_32_in_attachmentSource302 = new BitSet(
			new long[] { 0x0000000000000060L });
	public static final BitSet FOLLOW_taskIdDef_in_attachmentSource306 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_set_in_taskIdDef0 = new BitSet(
			new long[] { 0x0000000000000002L });

	@Override
	public void reportError(RecognitionException arg0) {
		/* */
	}
}