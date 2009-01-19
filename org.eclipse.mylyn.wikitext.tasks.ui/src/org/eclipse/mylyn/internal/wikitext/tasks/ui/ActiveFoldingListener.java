/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.tasks.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.wikitext.ui.editor.IFoldingStructure;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineItem;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineItem.Visitor;
import org.eclipse.ui.IEditorPart;

/**
 * 
 * based on implementation of ActiveFoldingListener in org.eclipse.mylyn.java.ui
 * 
 * @author David Green
 */
class ActiveFoldingListener extends AbstractContextListener {

	private final IEditorPart part;

	private final IFoldingStructure foldingStructure;

	private IPropertyChangeListener preferenceListener = new IPropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent event) {
			if (WikiTextTasksUiPlugin.PREF_ACTIVE_FOLDING_ENABLED.equals(event.getProperty())) {
				Object newValue = event.getNewValue();
				updateFolding(Boolean.TRUE.toString().equals(newValue.toString()));
			}
		}
	};

	private boolean foldingEnabled;

	private final AbstractContextStructureBridge bridge;

	public ActiveFoldingListener(IEditorPart part, IFoldingStructure foldingStructure) {
		this.part = part;
		this.foldingStructure = foldingStructure;
		bridge = ContextCore.getStructureBridge(WikiTextContextStructureBridge.CONTENT_TYPE);
		ContextCore.getContextManager().addListener(this);
		Preferences pluginPreferences = WikiTextTasksUiPlugin.getDefault().getPluginPreferences();
		pluginPreferences.addPropertyChangeListener(preferenceListener);
		updateFolding(pluginPreferences.getBoolean(WikiTextTasksUiPlugin.PREF_ACTIVE_FOLDING_ENABLED));
	}

	private void updateFolding(boolean foldingEnabled) {
		this.foldingEnabled = foldingEnabled;
		updateFolding();
	}

	private void updateFolding() {
		if (!foldingStructure.isFoldingEnabled()) {
			return;
		}
		if (!foldingEnabled || !ContextCore.getContextManager().isContextActive()) {
			foldingStructure.expandAll();
		} else {
			OutlineItem outline = (OutlineItem) part.getAdapter(OutlineItem.class);
			if (outline != null) {
				final List<OutlineItem> toExpand = new ArrayList<OutlineItem>();
				outline.accept(new Visitor() {
					public boolean visit(OutlineItem item) {
						String identifier = bridge.getHandleIdentifier(item);
						IInteractionElement element = ContextCore.getContextManager().getElement(identifier);
						if (element != null && element.getInterest().isInteresting()) {
							toExpand.add(item);
						}
						return true;
					}
				});
				if (toExpand.isEmpty()) {
					foldingStructure.collapseAll();
				} else {
					foldingStructure.expandElementsExclusive(toExpand);
				}
			}
		}
	}

	@Override
	public void contextActivated(IInteractionContext context) {
		if (foldingStructure.isFoldingEnabled()) {
			updateFolding();
		}
	}

	@Override
	public void contextCleared(IInteractionContext context) {
		if (foldingStructure.isFoldingEnabled()) {
			if (!foldingEnabled || !ContextCore.getContextManager().isContextActive()) {
				foldingStructure.expandAll();
			} else {
				foldingStructure.collapseAll();
			}
		}
	}

	@Override
	public void contextDeactivated(IInteractionContext context) {
		if (foldingStructure.isFoldingEnabled()) {
			foldingStructure.expandAll();
		}
	}

	@Override
	public void interestChanged(List<IInteractionElement> elements) {
		if (foldingStructure.isFoldingEnabled()) {
			updateFolding();
		}
	}

	@Override
	public void elementsDeleted(List<IInteractionElement> elements) {
		if (foldingStructure.isFoldingEnabled()) {
			updateFolding();
		}
	}

	public void dispose() {
		if (preferenceListener != null) {
			WikiTextTasksUiPlugin.getDefault().getPluginPreferences().removePropertyChangeListener(preferenceListener);
			preferenceListener = null;
		}
		ContextCore.getContextManager().removeListener(this);
	}

}
