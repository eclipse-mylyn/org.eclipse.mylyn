/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.internal.bugzilla.ui;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;

/**
 * @author Rob Elves (multiple bug hyperlink support)
 * @author Mik Kersten
 */
public class BugzillaHyperlinkUtil {

	private static final String BUG_HASH = "#";

	private static final String BUG_PATTERN_6 = "bug\\d+";// "^.*bug\\s#\\d+.*";

	private static final String BUG_PATTERN_5 = "bug\\s#\\s\\d+";// "^.*bug\\s#\\d+.*";

	private static final String BUG_PATTERN_4 = "bug#\\d+";// "^.*bug#\\d+.*";

	private static final String BUG_PATTERN_3 = "bug\\s#\\d+";// "^.*bug\\s#\\d+.*";

	private static final String BUG_PATTERN_2 = "bug#\\s+\\d+";// "^.*bug#\\s+\\d+.*";

	private static final String BUG_PATTERN_1 = "bug\\s+\\d+";// "^.*bug\\s+\\d+.*";

	private static final String[] BUG_PATTERNS = { BUG_PATTERN_1, BUG_PATTERN_2, BUG_PATTERN_3, BUG_PATTERN_4,
			BUG_PATTERN_5, BUG_PATTERN_6 };

	
	// TODO: legacy?: endOffset
	public static IHyperlink[] findBugHyperlinks(String repositoryUrl, int offset, int endOffset, String comment,
			int lineOffset) {
		ArrayList<IHyperlink> hyperlinksFound = new ArrayList<IHyperlink>();
		for (String regExp : BUG_PATTERNS) {
			// TODO: Store these compiled patterns rather than always
			// re-compiling
			Pattern p = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(comment);//comment.toLowerCase().trim()
			while (m.find()) {
				if (offset >= m.start() && offset <= m.end()) {
					IHyperlink link = extractHyperlink(repositoryUrl, lineOffset, m);
					if (link != null)
						hyperlinksFound.add(link);
				}
			}
		}

		if (hyperlinksFound.size() > 0) {
			return hyperlinksFound.toArray(new IHyperlink[1]);
		}
		return null;
	}

	private static IHyperlink extractHyperlink(String repositoryUrl, int lineOffset, Matcher m) {

		int start = m.start();
		int end = m.end();
		String bugText = m.group();
		int ahead = 3;
		if (bugText.contains(BUG_HASH)) {
			int pound = bugText.indexOf(BUG_HASH);
			ahead = pound + 1;
		}
		String endComment = bugText.substring(ahead, bugText.length());
		endComment = endComment.trim();

		if (end == -1)
			end = bugText.length();

		try {

			String bugId = endComment.trim();
			start += lineOffset;
			end += lineOffset;

			IRegion sregion = new Region(start, end - start);
			return new BugzillaHyperLink(sregion, bugId, repositoryUrl);

		} catch (NumberFormatException e) {
			return null;
		}
	}
	
//	/** The editor to use when a bug is opened */
//	private static IEditorPart fEditor;

//	/**
//	 * Convenience method for opening a bug in an editor.
//	 * 
//	 * @param id
//	 *            The bug id of the bug to open in the editor
//	 */
//	public static boolean show(String repositoryUrl, int id) {
//		// determine if the editor is to be reused or not and call the
//		// appropriate
//		// function to show the bug
//		if (NewSearchUI.reuseEditor())
//			return showWithReuse(repositoryUrl, id);
//		else
//			return showWithoutReuse(repositoryUrl, id);
//	}

//	/**
//	 * Show the bug in the same editor window
//	 * 
//	 * @param id
//	 *            The id of the bug to show
//	 */
//	private static boolean showWithReuse(String repositoryUrl, int id) {
//		// get the active page so that we can reuse it
//		IWorkbenchPage page = SearchPlugin.getActivePage();
//		try {
//			// if we couldn't get a page, get out
//			if (page == null)
//				return true;
//
//			IEditorInput input = null;
//
//			// try to get an editor input on the bug
//			input = new ExistingBugEditorInput(repositoryUrl, id);
//
//			// check if we found a valid bug
//			if (((ExistingBugEditorInput) input).getBug() == null) {
//				MessageDialog.openError(null, "No such bug", "No bug exists with this id");
//				return false;
//			}
//
//			// get the editor for the page
//			IEditorPart editor = page.findEditor(input);
//
//			if (editor == null) {
//				// close the current editor if it is clean and open
//				if (fEditor != null && !fEditor.isDirty())
//					page.closeEditor(fEditor, false);
//
//				try {
//					// try to open a new editor with the input bug, but don't
//					// activate it
//					editor = page.openEditor(input, IBugzillaConstants.EXISTING_BUG_EDITOR_ID, false);
//				} catch (PartInitException ex) {
//					// if there was a problem, handle it and log it, then get
//					// out of here
//					ExceptionHandler.handle(ex, SearchMessages.Search_Error_search_title,
//							SearchMessages.Search_Error_search_message); //$NON-NLS-2$ //$NON-NLS-1$
//					BugzillaPlugin.log(ex.getStatus());
//					return false;
//				}
//
//			} else {
//				// if a editor is openon that bug, just bring it to the top
//				// of the editors
//				page.bringToTop(editor);
//			}
//
//			if (editor != null) {
//				// if we have an editor, save it for later use
//				fEditor = editor;
//			}
//		} catch (LoginException e) {
//			MessageDialog
//					.openError(
//							null,
//							"Login Error",
//							"Bugzilla could not log you in to get the information you requested since login name or password is incorrect.\nPlease check your settings in the bugzilla preferences. ");
//			BugzillaPlugin.log(e);
//		} catch (IOException e) {
//			IStatus status = new MultiStatus(IBugzillaConstants.PLUGIN_ID, IStatus.ERROR, e.getClass().toString()
//					+ " occurred while opening the bug report.  \n\nClick Details or see log for more information.", e);
//			IStatus s = new Status(IStatus.ERROR, IBugzillaConstants.PLUGIN_ID, IStatus.ERROR, e.getClass().toString()
//					+ ":  ", e);
//			((MultiStatus) status).add(s);
//			s = new Status(IStatus.ERROR, IBugzillaConstants.PLUGIN_ID, IStatus.ERROR, e.getMessage(), e);
//			((MultiStatus) status).add(s);
//
//			// write error to log
//			BugzillaPlugin.log(status);
//
//			ErrorDialog.openError(null, "Bugzilla Error", null, status);
//			return false;
//		}
//
//		return true;
//	}

//	/**
//	 * Show the bug in a new editor window
//	 * 
//	 * @param id
//	 *            The id of the bug to show
//	 */
//	private static boolean showWithoutReuse(String repositoryUrl, int id) {
//		// get the active workbench page
//		IWorkbenchPage page = SearchPlugin.getActivePage();
//		try {
//			// if we couldn't get the page, get out of here
//			if (page == null)
//				return true;
//
//			IEditorInput input = null;
//			String editorId = IBugzillaConstants.EXISTING_BUG_EDITOR_ID;
//
//			// get a new editor input on the bug that we want to open
//			input = new ExistingBugEditorInput(repositoryUrl, id);
//
//			// check if we found a valid bug
//			if (((ExistingBugEditorInput) input).getBug() == null) {
//				MessageDialog.openError(null, "No such bug", "No bug exists with this id");
//				return false;
//			}
//
//			try {
//				// try to open an editor on the input bug
//				page.openEditor(input, editorId);
//			} catch (PartInitException ex) {
//				// if we have a problem, handle it, log it, and get out of here
//				ExceptionHandler.handle(ex, SearchMessages.Search_Error_search_title,
//						SearchMessages.Search_Error_search_message); //$NON-NLS-2$ //$NON-NLS-1$
//				BugzillaPlugin.log(ex.getStatus());
//				return false;
//			}
//		} catch (LoginException e) {
//			MessageDialog
//					.openError(
//							null,
//							"Login Error",
//							"Bugzilla could not log you in to get the information you requested since login name or password is incorrect.\nPlease check your settings in the bugzilla preferences. ");
//			BugzillaPlugin.log(e);
//		} catch (IOException e) {
//			IStatus status = new MultiStatus(IBugzillaConstants.PLUGIN_ID, IStatus.ERROR, e.getClass().toString()
//					+ " occurred while opening the bug report.  \n\nClick Details or see log for more information.", e);
//			IStatus s = new Status(IStatus.ERROR, IBugzillaConstants.PLUGIN_ID, IStatus.ERROR, e.getClass().toString()
//					+ ":  ", e);
//			((MultiStatus) status).add(s);
//			s = new Status(IStatus.ERROR, IBugzillaConstants.PLUGIN_ID, IStatus.ERROR, e.getMessage(), e);
//			((MultiStatus) status).add(s);
//
//			// write error to log
//			BugzillaPlugin.log(status);
//
//			ErrorDialog.openError(null, "Bugzilla Error", null, status);
//			return false;
//		}
//		return true;
//	}

//	public static void closeEditor(IWorkbenchPage page, IBugzillaBug bug) {
//		if (bug instanceof NewBugzillaReport) {
//			IEditorInput input = new NewBugEditorInput((NewBugzillaReport) bug);
//			IEditorPart bugEditor = page.findEditor(input);
//			if (bugEditor != null) {
//				page.closeEditor(bugEditor, false);
//			}
//		} else if (bug instanceof BugzillaReport) {
//			IEditorInput input = new ExistingBugEditorInput((BugzillaReport) bug);
//			IEditorPart bugEditor = page.findEditor(input);
//			if (bugEditor != null) {
//				page.closeEditor(bugEditor, false);
//				IEditorPart compareEditor = page.findEditor(((ExistingBugEditor) bugEditor).getCompareInput());
//				if (compareEditor != null) {
//					page.closeEditor(compareEditor, false);
//				}
//			}
//		}
//	}

	// public static IHyperlink[] findBugHyperlinks(String repositoryUrl, int
	// startOffset, int endOffset, String comment, int commentStart) {
	//
	//		
	// Pattern p = Pattern.compile("^.*bug\\s+\\d+.*");
	// Matcher m = p.matcher(comment.toLowerCase().trim());
	// boolean b = m.matches();
	//		
	// p = Pattern.compile("^.*bug#\\s+\\d+.*");
	// m = p.matcher(comment.toLowerCase().trim());
	// boolean b2 = m.matches();
	//
	// p = Pattern.compile("^.*bug\\s#\\d+.*");
	// m = p.matcher(comment.toLowerCase().trim());
	// boolean b3 = m.matches();
	//
	// p = Pattern.compile("^.*bug#\\d+.*");
	// m = p.matcher(comment.toLowerCase().trim());
	// boolean b4 = m.matches();
	//	
	// // XXX walk forward from where we are
	// if (b || b2 || b3 || b4) {
	//
	// int start = comment.toLowerCase().indexOf("bug");
	// int ahead = 4;
	// if (b2 || b3 || b4) {
	// int pound = comment.toLowerCase().indexOf("#", start);
	// ahead = pound - start + 1;
	// }
	// String endComment = comment.substring(start + ahead, comment.length());
	// endComment = endComment.trim();
	// int endCommentStart = comment.indexOf(endComment);
	//
	// int end = comment.indexOf(" ", endCommentStart);
	// int end2 = comment.indexOf(":", endCommentStart);
	//
	// if ((end2 < end && end2 != -1) || (end == -1 && end2 != -1)) {
	// end = end2;
	// }
	//
	// if (end == -1)
	// end = comment.length();
	//
	// try {
	// //int bugId = Integer.parseInt(comment.substring(endCommentStart,
	// end).trim());
	// String bugId = comment.substring(endCommentStart, end).trim();
	// start += commentStart;
	// end += commentStart;
	// if (startOffset >= start && startOffset <= end) {
	// // if (startOffset >= start && endOffset <= end) {
	// IRegion sregion = new Region(start, end - start);
	// return new IHyperlink[] { new BugzillaHyperLink(sregion, bugId,
	// repositoryUrl) };
	// }
	// } catch (NumberFormatException e) {
	// return null;
	// }
	// }
	// return null;
	// }

}
