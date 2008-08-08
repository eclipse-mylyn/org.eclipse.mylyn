/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.editors;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.internal.browser.BrowserViewer;
import org.eclipse.ui.internal.browser.IBrowserViewerContainer;

/**
 * A form page that contains a browser control.
 * 
 * @since 3.0
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class BrowserFormPage extends FormPage {

	public static final String ID_EDITOR = "org.eclipse.mylyn.tasks.ui.editor.browser";

	private BrowserViewer browserViewer;

	public BrowserFormPage(FormEditor editor, String title) {
		super(editor, ID_EDITOR, title);
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		try {
			ScrolledForm form = managedForm.getForm();
			form.getBody().setLayout(new FillLayout());
			browserViewer = new BrowserViewer(form.getBody(), SWT.NONE);
			browserViewer.setLayoutData(null);
			browserViewer.setContainer(new IBrowserViewerContainer() {

				public boolean close() {
					return false;
				}

				public IActionBars getActionBars() {
					return BrowserFormPage.this.getEditorSite().getActionBars();
				}

				public void openInExternalBrowser(String url) {
					// ignore
				}

			});
			managedForm.getForm().setContent(browserViewer);
			String url = getUrl();
			if (url != null) {
				browserViewer.setURL(url);
			}
		} catch (SWTError e) {
			// TODO review error handling
			StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not create browser page: "
					+ e.getMessage(), e));
		} catch (RuntimeException e) {
			// TODO review error handling
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not create browser page", e));
		}
	}

	/**
	 * Returns a reference to the browser control.
	 */
	public Browser getBrowser() {
		return browserViewer.getBrowser();
	}

	/**
	 * Returns the initial URL that is displayed in the browser control. The default implementation tries to determine
	 * the URL from the editor input.
	 * <p>
	 * Subclasses should override this method to display a specific URL.
	 * 
	 * @return the URL to load when the page is created; null, if no URL should be loaded
	 */
	protected String getUrl() {
		IEditorInput input = getEditorInput();
		if (input instanceof TaskEditorInput) {
			return ((TaskEditorInput) input).getTask().getUrl();
		}
		return null;
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);
		if (input instanceof TaskEditorInput) {
			TasksUiPlugin.getTaskDataManager().setTaskRead(((TaskEditorInput) input).getTask(), true);
		}
	}

}
