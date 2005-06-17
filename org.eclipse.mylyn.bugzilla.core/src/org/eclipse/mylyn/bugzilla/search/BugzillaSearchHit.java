/*******************************************************************************
 * Copyright (c) 2003 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.bugzilla.search;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.bugzilla.BugzillaPlugin;
import org.eclipse.mylar.bugzilla.IBugzillaConstants;
import org.eclipse.mylar.bugzilla.ui.editor.ExistingBugEditorInput;
import org.eclipse.search.internal.ui.SearchMessages;
import org.eclipse.search.internal.ui.SearchPlugin;
import org.eclipse.search.internal.ui.util.ExceptionHandler;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;


/**
 * An item in the Bugzilla database matching the search criteria.
 */
public class BugzillaSearchHit 
{
	/** The bug id */
	private int id;
	
	/** The description of the bug */
	private String description;
	
	/** The severity of the bug */
	private String severity;
	
	/** The priority of the bug */
	private String priority;
	
	/** The platform that the bug was found in */
	private String platform;
	
	/** The state of the bug */
	private String state;
	
	/** The resolution of the bug */
	private String result;
	
	/** The owner of the bug */
	private String owner;
	
	/** The query that the bug was a result of */
	private String query;
	
	/** The editor to use when a bug is opened */
	private static IEditorPart fEditor;

	/**
	 * Constructor
	 * @param id The id of the bug
	 * @param description The description of the bug
	 * @param severity The severity of the bug
	 * @param priority The priority of the bug
	 * @param platform The platform the bug was found in
	 * @param state The state of the bug
	 * @param result The resolution of the bug
	 * @param owner The owner of the bug
	 * @param query the query that the bug was a result of
	 */
	public BugzillaSearchHit(int id, String description, String severity, String priority, String platform, String state, String result, String owner, String query) 
	{
		this.id = id;
		this.description = description;
		this.severity = severity;
		this.priority = priority;
		this.platform = platform;
		this.state = state;
		this.result = result;
		this.owner = owner;
		this.query = query;
	}
	
	/**
	 * Get the bugs id
	 * @return The bugs id
	 */
	public int getId() 
	{
		return id;
	}

	/**
	 * Get the bugs description
	 * @return The description of the bug
	 */
	public String getDescription() 
	{
		return description;
	}

	/**
	 * Get the bugs priority
	 * @return The priority of the bug
	 */
	public String getPriority() 
	{
		return priority;
	}

	/**
	 * Get the bugs severity
	 * @return The severity of the bug
	 */
	public String getSeverity() 
	{
		return severity;
	}

	/**
	 * Get the platform the bug occurred under
	 * @return The platform that the bug occured under
	 */
	public String getPlatform() 
	{
		return platform;
	}

	/**
	 * Get the bugs state
	 * @return The state of the bug
	 */
	public String getState() 
	{
		return state;
	}

	/**
	 * Get the bugs resolution
	 * @return The resolution of the bug
	 */
	public String getResult() 
	{
		return result;
	}

	/**
	 * Get the bugs owner
	 * @return The owner of the bug
	 */
	public String getOwner() 
	{
		return owner;
	}
	
	/**
	 * Get the query that the bug was a result of
	 * @return The query that the bug was a result of
	 */
	public String getQuery() 
	{
		return query;
	}
	
	@Override
	public String toString()
	{
		return id + " " + description + "\n";
	}
	

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
}
