/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.bugzilla.ui;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.bugzilla.core.BugReport;
import org.eclipse.mylar.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.bugzilla.core.IBugzillaBug;
import org.eclipse.mylar.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.bugzilla.core.NewBugModel;
import org.eclipse.mylar.bugzilla.ui.editor.ExistingBugEditor;
import org.eclipse.mylar.bugzilla.ui.editor.ExistingBugEditorInput;
import org.eclipse.mylar.bugzilla.ui.editor.NewBugEditorInput;
import org.eclipse.search.internal.ui.SearchMessages;
import org.eclipse.search.internal.ui.SearchPlugin;
import org.eclipse.search.internal.ui.util.ExceptionHandler;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.internal.browser.WorkbenchBrowserSupport;

public class BugzillaUITools {

	/** The editor to use when a bug is opened */
	private static IEditorPart fEditor;
	
	/**
	 * Convenience method for opening a bug in an editor.
	 * @param id The bug id of the bug to open in the editor
	 */
	public static boolean show(int id) 
	{
		// determine if the editor is to be reused or not and call the appropriate
		// function to show the bug
		if (NewSearchUI.reuseEditor())
			return showWithReuse(id);
		else
			return showWithoutReuse(id);
	}

	/**
	 * Show the bug in the same editor window
	 * @param id The id of the bug to show
	 */
	private static boolean showWithReuse(int id) 
	{
		// get the active page so that we can reuse it
		IWorkbenchPage page = SearchPlugin.getActivePage();
		try 
		{
			// if we couldn't get a page, get out
			if (page == null)
				return true;
			
			IEditorInput input = null;
	
			// try to get an editor input on the bug
			input = new ExistingBugEditorInput(id);
			
			// check if we found a valid bug
			if(((ExistingBugEditorInput)input).getBug() == null)
			{
				MessageDialog.openError(null, "No such bug", "No bug exists with this id");
				return false;
			}
			
			// get the editor for the page			
			IEditorPart editor = page.findEditor(input);
			
			if (editor == null) 
			{
				// close the current editor if it is clean and open
				if (fEditor != null && !fEditor.isDirty())
					page.closeEditor(fEditor, false);
					
				try 
				{
					// try to open a new editor with the input bug, but don't activate it
					editor= page.openEditor(input, IBugzillaConstants.EXISTING_BUG_EDITOR_ID, false);
				}
				catch (PartInitException ex) 
				{
					// if there was a problem, handle it and log it, then get out of here
					ExceptionHandler.handle(ex, SearchMessages.Search_Error_search_title, SearchMessages.Search_Error_search_message); //$NON-NLS-2$ //$NON-NLS-1$
					BugzillaPlugin.log(ex.getStatus());
					return false;
				}
	
			} 
			else 
			{
				// if a editor is openon that bug, just bring it to the top 
				// of the editors
				page.bringToTop(editor);
			}
			
			if (editor != null) 
			{
				// if we have an editor, save it for later use
				fEditor= editor;
			}
		} 
		catch(LoginException e)
		{
			MessageDialog.openError(null, "Login Error", "Bugzilla could not log you in to get the information you requested since login name or password is incorrect.\nPlease check your settings in the bugzilla preferences. ");
			BugzillaPlugin.log(e);
		}
		catch(IOException e){
			IStatus status = new MultiStatus( IBugzillaConstants.PLUGIN_ID, IStatus.ERROR, e.getClass().toString() + " occurred while opening the bug report.  \n\nClick Details or see log for more information.", e);
			IStatus s = new Status(IStatus.ERROR, IBugzillaConstants.PLUGIN_ID, IStatus.ERROR, e.getClass().toString() + ":  ", e);
			((MultiStatus)status).add(s);
			s = new Status (IStatus.ERROR, IBugzillaConstants.PLUGIN_ID, IStatus.ERROR, e.getMessage(), e);
			((MultiStatus)status).add(s);

			//write error to log
			BugzillaPlugin.log(status);
			
			ErrorDialog.openError(null, "Bugzilla Error", null, status);
			return false;
		}
        
        return true;
	}
	
	/**
	 * Show the bug in a new editor window
	 * @param id The id of the bug to show
	 */
	private static boolean showWithoutReuse(int id) 
	{
		// get the active workbench page
		IWorkbenchPage page = SearchPlugin.getActivePage();
		try 
		{
			// if we couldn't get the page, get out of here
			if (page == null)
				return true;
			
			IEditorInput input = null;
			String editorId = IBugzillaConstants.EXISTING_BUG_EDITOR_ID;

			// get a new editor input on the bug that we want to open
			input = new ExistingBugEditorInput(id);
			
			// check if we found a valid bug
			if(((ExistingBugEditorInput)input).getBug() == null)
			{
				MessageDialog.openError(null, "No such bug", "No bug exists with this id");
				return false;
			}

			try 
			{
				// try to open an editor on the input bug
				page.openEditor(input, editorId);
			} 
			catch (PartInitException ex) 
			{
				// if we have a problem, handle it, log it, and get out of here
				ExceptionHandler.handle(ex, SearchMessages.Search_Error_search_title, SearchMessages.Search_Error_search_message); //$NON-NLS-2$ //$NON-NLS-1$
				BugzillaPlugin.log(ex.getStatus());
				return false;
			}
		}
		catch(LoginException e)
		{
			MessageDialog.openError(null, "Login Error", "Bugzilla could not log you in to get the information you requested since login name or password is incorrect.\nPlease check your settings in the bugzilla preferences. ");
			BugzillaPlugin.log(e);
		}
		catch(IOException e){
			IStatus status = new MultiStatus( IBugzillaConstants.PLUGIN_ID, IStatus.ERROR, e.getClass().toString() + " occurred while opening the bug report.  \n\nClick Details or see log for more information.", e);
			IStatus s = new Status(IStatus.ERROR, IBugzillaConstants.PLUGIN_ID, IStatus.ERROR, e.getClass().toString() + ":  ", e);
			((MultiStatus)status).add(s);
			s = new Status (IStatus.ERROR, IBugzillaConstants.PLUGIN_ID, IStatus.ERROR, e.getMessage(), e);
			((MultiStatus)status).add(s);

			//write error to log
			BugzillaPlugin.log(status);
			
			ErrorDialog.openError(null, "Bugzilla Error", null, status);
			return false;
		}
        return true;
	}

	public static void closeEditor(IWorkbenchPage page, IBugzillaBug bug) {
		if(bug instanceof NewBugModel){
			IEditorInput input = new NewBugEditorInput((NewBugModel)bug);
			IEditorPart bugEditor = page.findEditor(input);
			if (bugEditor != null) {
				page.closeEditor(bugEditor, false);
			}
		} else if(bug instanceof BugReport){
			IEditorInput input = new ExistingBugEditorInput((BugReport)bug);
			IEditorPart bugEditor = page.findEditor(input);
			if (bugEditor != null) {
				page.closeEditor(bugEditor, false);
				IEditorPart compareEditor = page.findEditor(((ExistingBugEditor)bugEditor).getCompareInput());
				if (compareEditor != null) {
					page.closeEditor(compareEditor, false);
				}
			}
		}
	}
	
	public static void openUrl(String title, String tooltip, String url) {
		try {
			IWebBrowser b = null;
			int flags = 0;
			if (WorkbenchBrowserSupport.getInstance()
					.isInternalWebBrowserAvailable()) {
				flags = WorkbenchBrowserSupport.AS_EDITOR
						| WorkbenchBrowserSupport.LOCATION_BAR
						| WorkbenchBrowserSupport.NAVIGATION_BAR;

			} else {
				flags = WorkbenchBrowserSupport.AS_EXTERNAL
						| WorkbenchBrowserSupport.LOCATION_BAR
						| WorkbenchBrowserSupport.NAVIGATION_BAR;
			}
			b = WorkbenchBrowserSupport.getInstance().createBrowser(
					flags, "org.eclipse.mylar.tasklist." + title, title,
					tooltip);
			b.openURL(new URL(url));
		} catch (PartInitException e) {
			MessageDialog.openError( Display.getDefault().getActiveShell(), 
					"Browser init error",  "Browser could not be initiated");
		} catch (MalformedURLException e) {
			MessageDialog.openError( Display.getDefault().getActiveShell(), 
					"URL not found",  "URL Could not be opened");
		}  
	}
	
}
