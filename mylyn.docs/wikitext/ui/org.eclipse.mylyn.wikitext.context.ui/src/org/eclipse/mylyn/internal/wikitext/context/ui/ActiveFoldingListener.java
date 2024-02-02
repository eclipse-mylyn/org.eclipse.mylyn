/*******************************************************************************
 * Copyright (c) 2009, 2013 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.context.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextChangeEvent;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.wikitext.ui.editor.IFoldingStructure;
import org.eclipse.mylyn.wikitext.parser.outline.OutlineItem;
import org.eclipse.ui.IEditorPart;

/**
 * based on implementation of ActiveFoldingListener in org.eclipse.mylyn.java.ui
 * 
 * @author David Green
 * @author Shawn Minto bug 274706 updated to use new 3.2 APIs.
 */
class ActiveFoldingListener extends AbstractContextListener {

	private final IEditorPart part;

	private final IFoldingStructure foldingStructure;

	private org.eclipse.jface.util.IPropertyChangeListener preferenceListener = event -> {
		if (WikiTextContextUiPlugin.PREF_ACTIVE_FOLDING_ENABLED.equals(event.getProperty())) {
			Object newValue = event.getNewValue();
			foldingEnabled = Boolean.TRUE.toString().equals(newValue.toString());
			updateFolding(false);
		}
	};

	private boolean foldingEnabled;

	private final AbstractContextStructureBridge bridge;

	private final IPreferenceStore preferences;

	public ActiveFoldingListener(IEditorPart part, IFoldingStructure foldingStructure) {
		this.part = part;
		this.foldingStructure = foldingStructure;
		bridge = ContextCore.getStructureBridge(WikiTextContextStructureBridge.CONTENT_TYPE);
		ContextCore.getContextManager().addListener(this);
		preferences = WikiTextContextUiPlugin.getDefault().getPreferenceStore();
		preferences.addPropertyChangeListener(preferenceListener);
		foldingEnabled = preferences.getBoolean(WikiTextContextUiPlugin.PREF_ACTIVE_FOLDING_ENABLED);
		updateFolding(false);
	}

	private void updateFolding(boolean elementsDeleted) {
		if (!foldingStructure.isFoldingEnabled()) {
			return;
		}
		if (!foldingEnabled || !ContextCore.getContextManager().isContextActive()) {
			foldingStructure.expandAll();
		} else {
			OutlineItem outline = part.getAdapter(OutlineItem.class);
			if (outline != null) {
				final List<OutlineItem> toExpand = new ArrayList<>();
				outline.accept(item -> {
					String identifier = bridge.getHandleIdentifier(item);
					IInteractionElement element = ContextCore.getContextManager().getElement(identifier);
					if (element != null && element.getInterest().isInteresting()) {
						toExpand.add(item);
					}
					return true;
				});
				if (toExpand.isEmpty()) {
					foldingStructure.collapseAll(elementsDeleted);
				} else {
					foldingStructure.expandElementsExclusive(toExpand, elementsDeleted);
				}
			}
		}
	}

	@Override
	public void contextChanged(ContextChangeEvent event) {
		switch (event.getEventKind()) {
			case ACTIVATED:
				if (foldingStructure.isFoldingEnabled()) {
					updateFolding(false);
				}
				break;
			case DEACTIVATED:
				if (foldingStructure.isFoldingEnabled()) {
					foldingStructure.expandAll();
				}
				break;
			case CLEARED:
				if (event.isActiveContext()) {
					if (foldingStructure.isFoldingEnabled()) {
						if (!foldingEnabled || !ContextCore.getContextManager().isContextActive()) {
							foldingStructure.expandAll();
						} else {
							foldingStructure.collapseAll(true);
						}
					}
				}
				break;

			case INTEREST_CHANGED:
				if (foldingStructure.isFoldingEnabled()) {
					updateFolding(false);
				}
				break;
			case ELEMENTS_DELETED:
				if (foldingStructure.isFoldingEnabled()) {
					updateFolding(true);
				}
				break;
			default:
				break;
		}
	}

	public void dispose() {
		if (preferenceListener != null) {
			preferences.removePropertyChangeListener(preferenceListener);
			preferenceListener = null;
		}
		ContextCore.getContextManager().removeListener(this);
	}

}
