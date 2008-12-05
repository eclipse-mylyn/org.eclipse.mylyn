/*******************************************************************************
 * Copyright (c) 2004, 2008 Jingwen Ou and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jingwen Ou - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.commands;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.HandlerEvent;
import org.eclipse.jface.action.IAction;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

/**
 * @author Jingwen Ou
 * @author Steffen Pingel
 */
public class ViewSourceHandler extends AbstractHandler implements IElementUpdater {

	private static boolean checked;

	private static ViewSourceHandler instance;

	public ViewSourceHandler() {
		instance = this;
	}

	public boolean isChecked() {
		return checked;
	}

	public static void setChecked(boolean checked) {
		ViewSourceHandler.checked = checked;
		if (instance != null) {
			instance.fireHandlerChanged(new HandlerEvent(instance, true, false));
		}
	}

	private Control getFocusControl() {
		return PlatformUI.getWorkbench().getDisplay().getFocusControl();
	}

	@Override
	public boolean isEnabled() {
		Control focusControl = getFocusControl();
		if (focusControl instanceof StyledText && focusControl.getData(VIEW_SOURCE_ACTION) instanceof IAction) {
			return true;
		}

		return false;
	}

	public static final String VIEW_SOURCE_ACTION = "viewSourceAction"; //$NON-NLS-1$

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchSite site = HandlerUtil.getActiveSite(event);
		if (site instanceof IEditorSite) {
			IWorkbenchPart part = ((IEditorSite) site).getPart();
			if (part instanceof FormEditor) {
				IFormPage page = ((FormEditor) part).getActivePageInstance();
				Control focusedControl = EditorUtil.getFocusControl(page);
				if (focusedControl != null) {
					Object data = focusedControl.getData(VIEW_SOURCE_ACTION);
					if (data instanceof IAction) {
						IAction action = (IAction) data;
						action.setChecked(!action.isChecked());
						action.run();
						setChecked(action.isChecked());
						EditorUtil.reflow(focusedControl);
					}
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public void updateElement(UIElement element, Map parameters) {
		element.setChecked(checked);
	}

}
