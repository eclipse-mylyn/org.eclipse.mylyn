/*******************************************************************************
 * Copyright (c) 2004, 2008 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * Created on May 16, 2005
 */
package org.eclipse.cdt.mylyn.internal.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.IMethod;
import org.eclipse.cdt.core.model.IParent;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.internal.ui.editor.CEditor;
import org.eclipse.cdt.internal.ui.editor.CSourceViewer;
import org.eclipse.cdt.mylyn.internal.ui.CDTStructureBridge;
import org.eclipse.cdt.mylyn.internal.ui.CDTUIBridgePlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;

/**
 * @author Mik Kersten
 * @author Jeff Johnston
 */
public class ActiveFoldingListener extends AbstractContextListener {
	private final CEditor editor;

	private ProjectionAnnotationModel updater;

	private static CDTStructureBridge bridge = (CDTStructureBridge) ContextCorePlugin.getDefault()
			.getStructureBridge(CDTStructureBridge.CONTENT_TYPE);

	private boolean enabled = false;

	private IPropertyChangeListener PREFERENCE_LISTENER = new IPropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent event) {
			if (event.getProperty().equals(CDTUIBridgePlugin.AUTO_FOLDING_ENABLED)) {
				if (event.getNewValue().equals(Boolean.TRUE.toString())) {
					enabled = true;
				} else {
					enabled = false;
				}
				updateFolding();
			}
		}
	};

	public ActiveFoldingListener(CEditor editor) {
		this.editor = editor;
		if (ContextUiPlugin.getDefault() == null) {
			StatusHandler.fail(new Status(IStatus.ERROR, CDTUIBridgePlugin.PLUGIN_ID,
					CDTUIBridgePlugin.getResourceString("MylynCDT.initFoldingFailure"))); // $NON-NLS-1$
		} else { 
			ContextCorePlugin.getContextManager().addListener(this);
			ContextUiPlugin.getDefault().getPluginPreferences().addPropertyChangeListener(PREFERENCE_LISTENER);

			enabled = ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(
					CDTUIBridgePlugin.AUTO_FOLDING_ENABLED);
			updateFolding();
		}
	}
	
	protected void collapseElements(ICElement[] elements) {
		for (int i = 0; i < elements.length; ++i) {
			collapse(elements[i]);
		}
	}
	
	private void collapse(ICElement element) {
		CSourceViewer viewer = (CSourceViewer)editor.getViewer();
		viewer.doOperation(ProjectionViewer.COLLAPSE);
	}
	
	protected void expandElements(ICElement[] elements) {
		for (int i = 0; i < elements.length; ++i) {
			expand(elements[i]);
		}
	}
	
	private void expand(ICElement element) {
		CSourceViewer viewer = (CSourceViewer)editor.getViewer();
		viewer.doOperation(ProjectionViewer.EXPAND);
	}

	public void dispose() {
		ContextCorePlugin.getContextManager().removeListener(this);
		ContextUiPlugin.getDefault().getPluginPreferences().removePropertyChangeListener(PREFERENCE_LISTENER);
	}

	public static void resetProjection(CEditor CEditor) {
		// XXX: ignore for 3.2, leave for 3.1?
	}

	public void updateFolding() {
		if (!enabled || !ContextCorePlugin.getContextManager().isContextActive()) {
			editor.resetProjection();
		} else if (editor.getInputCElement() == null) {
			return;
		} else {
			try {
				List<ICElement> toExpand = new ArrayList<ICElement>();
				List<ICElement> toCollapse = new ArrayList<ICElement>();

				ICElement element = editor.getInputCElement();
				if (element instanceof ITranslationUnit) {
					ITranslationUnit compilationUnit = (ITranslationUnit) element;
					List<ICElement> allChildren = getAllChildren(compilationUnit);
					for (ICElement child : allChildren) {
						IInteractionElement interactionElement = ContextCorePlugin.getContextManager().getElement(
								bridge.getHandleIdentifier(child));
						if (interactionElement != null && interactionElement.getInterest().isInteresting()) {
							toExpand.add(child);
						} else {
							toCollapse.add(child);
						}
					}
				}

				collapseElements(toCollapse.toArray(new ICElement[toCollapse.size()]));
				expandElements(toExpand.toArray(new ICElement[toExpand.size()]));
					
			} catch (Exception e) {
				StatusHandler.fail(new Status(IStatus.ERROR, CDTUIBridgePlugin.PLUGIN_ID,
						CDTUIBridgePlugin.getResourceString("MylynCDT.updateFoldingFailure"), e)); // $NON-NLS-1$
			}
		}
	}

	private static List<ICElement> getAllChildren(IParent parentElement) {
		List<ICElement> allChildren = new ArrayList<ICElement>();
		try {
			for (ICElement child : parentElement.getChildren()) {
				allChildren.add(child);
				if (child instanceof IParent) {
					allChildren.addAll(getAllChildren((IParent) child));
				}
			}
		} catch (CModelException e) {
			// ignore failures
		}
		return allChildren;
	}

	public void interestChanged(List<IInteractionElement> elements) {
		for (IInteractionElement element : elements) {
			if (updater == null || !enabled) {
				return;
			} else {
				Object object = bridge.getObjectForHandle(element.getHandleIdentifier());
				if (object instanceof IMethod) {
					IMethod member = (IMethod) object;
					if (element.getInterest().isInteresting()) {
						expandElements(new ICElement[] { member });
						// expand the next 2 children down (e.g. anonymous types)
						try {
							ICElement[] children = ((IParent) member).getChildren();
							if (children.length == 1) {
								expandElements(new ICElement[] { children[0] });
								if (children[0] instanceof IParent) {
									ICElement[] childsChildren = ((IParent) children[0]).getChildren();
									if (childsChildren.length == 1) {
										expandElements(new ICElement[] { childsChildren[0] });
									}
								}
							}
						} catch (CModelException e) {
							// ignore
						}
					} else {
						collapseElements(new ICElement[] { member });
					}
				}
			}
		}
	}

	public void contextActivated(IInteractionContext context) {
		if (ContextUiPlugin.getDefault()
				.getPreferenceStore()
				.getBoolean(CDTUIBridgePlugin.AUTO_FOLDING_ENABLED)) {
			updateFolding();
		}
	}

	public void contextDeactivated(IInteractionContext context) {
		if (ContextUiPlugin.getDefault()
				.getPreferenceStore()
				.getBoolean(CDTUIBridgePlugin.AUTO_FOLDING_ENABLED)) {
			updateFolding();
		}
	}

	public void contextCleared(IInteractionContext context) {
		if (ContextUiPlugin.getDefault()
				.getPreferenceStore()
				.getBoolean(CDTUIBridgePlugin.AUTO_FOLDING_ENABLED)) {
			updateFolding();
		}
	}

	public void landmarkAdded(IInteractionElement element) {
		// ignore
	}

	public void landmarkRemoved(IInteractionElement element) {
		// ignore
	}

	public void relationsChanged(IInteractionElement node) {
		// ignore
	}

	public void elementDeleted(IInteractionElement node) {
		// ignore
	}
}
