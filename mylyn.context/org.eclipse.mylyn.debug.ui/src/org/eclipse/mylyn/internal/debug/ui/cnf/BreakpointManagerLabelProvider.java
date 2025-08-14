/*******************************************************************************
 * Copyright (c) 2012, 2014 Sebastian Schmidt and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Sebastian Schmidt - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.debug.ui.cnf;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.internal.ui.views.breakpoints.BreakpointsLabelProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.internal.debug.ui.DebugUiPlugin;
import org.eclipse.swt.graphics.Image;

/**
 * @author Sebastian Schmidt
 */
@SuppressWarnings("restriction")
public class BreakpointManagerLabelProvider extends BreakpointsLabelProvider {

	private Image bpmImage;

	public BreakpointManagerLabelProvider() {
		try {
			URL bpmImageUrl = new URL(DebugUiPlugin.getDefault().getBundle().getEntry("/"), //$NON-NLS-1$
					"icons/elcl16/breakpointmanager.gif"); //$NON-NLS-1$
			bpmImage = ImageDescriptor.createFromURL(bpmImageUrl).createImage();
		} catch (MalformedURLException e) {
		}
	}

	@Override
	public String getText(Object element) {
		if (element instanceof IBreakpointManager) {
			return "Breakpoints"; //$NON-NLS-1$
		}
		return super.getText(element);
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof IBreakpointManager) {
			return bpmImage;
		}
		return super.getImage(element);
	}

	@Override
	public void dispose() {
		if (bpmImage != null) {
			bpmImage.dispose();
			bpmImage = null;
		}
		super.dispose();
	}
}
