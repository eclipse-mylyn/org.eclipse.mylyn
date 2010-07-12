/*******************************************************************************
 * Copyright (c) 2010 Markus Knittig and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Knittig - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiPlugin;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.SharedHeaderFormEditor;

/**
 * @author Markus Knittig
 */
public class BuildEditor extends SharedHeaderFormEditor {

	public static final String ID = "org.eclipse.mylyn.builds.ui.editor.buildeditor";

	@Override
	protected void addPages() {
		BuildEditorPage buildEditorPage = new BuildEditorPage(this, "Build Details");
		try {
			addPage(buildEditorPage);
		} catch (PartInitException e) {
			StatusHandler.log(new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN, "Could not create Build editor.", e));
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// ignore
	}

	@Override
	public void doSaveAs() {
		// ignore
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

}
