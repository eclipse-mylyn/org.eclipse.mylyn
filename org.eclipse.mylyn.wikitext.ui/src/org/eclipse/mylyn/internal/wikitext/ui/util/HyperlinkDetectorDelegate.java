/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.util;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetectorExtension;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetectorExtension2;
import org.eclipse.ui.texteditor.HyperlinkDetectorDescriptor;

/**
 * A means to delay activation of plug-ins by delaying creation of a hyperlink detector from a descriptor.
 * 
 * @author David Green
 */
public class HyperlinkDetectorDelegate implements IHyperlinkDetector, IHyperlinkDetectorExtension,
		IHyperlinkDetectorExtension2 {

	private HyperlinkDetectorDescriptor descriptor;

	private AbstractHyperlinkDetector delegate;

	private boolean createFailed;

	private IAdaptable context;

	private int stateMask;

	private boolean enabled;

	public HyperlinkDetectorDelegate(HyperlinkDetectorDescriptor descriptor, IPreferenceStore preferenceStore) {
		this.descriptor = descriptor;
		if (preferenceStore != null) {
			stateMask = preferenceStore.getInt(descriptor.getId() + HyperlinkDetectorDescriptor.STATE_MASK_POSTFIX);
			enabled = !preferenceStore.getBoolean(descriptor.getId());
		}
	}

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		if (!isEnabled()) {
			return null;
		}

		if (!createFailed && delegate == null) {
			try {
				delegate = descriptor.createHyperlinkDetector();
			} catch (CoreException ex) {
				createFailed = true;
			}
			if (delegate != null && context != null) {
				delegate.setContext(context);
			}
		}
		if (delegate != null) {
			return delegate.detectHyperlinks(textViewer, region, canShowMultipleHyperlinks);
		}

		return null;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setContext(IAdaptable context) {
		this.context = context;
	}

	public void dispose() {
		if (delegate != null) {
			delegate.dispose();
			delegate = null;
		}
		descriptor = null;
		context = null;
	}

	public int getStateMask() {
		return stateMask;
	}
}