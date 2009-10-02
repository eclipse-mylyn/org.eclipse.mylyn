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
package org.eclipse.mylyn.internal.cdt.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.IMethod;
import org.eclipse.cdt.core.model.IParent;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.internal.ui.editor.CEditor;
import org.eclipse.cdt.internal.ui.editor.CSourceViewer;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.ContextChangeEvent;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.cdt.ui.CDTStructureBridge;
import org.eclipse.mylyn.internal.cdt.ui.CDTUIBridgePlugin;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;

/**
 * @author Mik Kersten
 * @author Jeff Johnston
 */
public class ActiveFoldingListener extends AbstractContextListener {
	private final CEditor editor;

	private ProjectionAnnotationModel updater;

	private static CDTStructureBridge bridge = (CDTStructureBridge) ContextCorePlugin.getDefault().getStructureBridge(
			CDTStructureBridge.CONTENT_TYPE);

	private boolean enabled = false;

	private final IPropertyChangeListener PREFERENCE_LISTENER = new IPropertyChangeListener() {
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

	@SuppressWarnings("restriction")
	public ActiveFoldingListener(CEditor editor) {
		this.editor = editor;
		if (ContextUiPlugin.getDefault() == null) {
			StatusHandler.fail(new Status(IStatus.ERROR, CDTUIBridgePlugin.ID_PLUGIN,
					"could not update folding, Mylyn is not correctly installed")); //$NON-NLS-1$
		} else {
			ContextCore.getContextManager().addListener(this);
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
		CSourceViewer viewer = (CSourceViewer) editor.getViewer();
		viewer.doOperation(ProjectionViewer.COLLAPSE);
	}

	protected void expandElements(ICElement[] elements) {
		for (int i = 0; i < elements.length; ++i) {
			expand(elements[i]);
		}
	}

	private void expand(ICElement element) {
		CSourceViewer viewer = (CSourceViewer) editor.getViewer();
		viewer.doOperation(ProjectionViewer.EXPAND);
	}

	@SuppressWarnings("restriction")
	public void dispose() {
		ContextCore.getContextManager().removeListener(this);
		ContextUiPlugin.getDefault().getPluginPreferences().removePropertyChangeListener(PREFERENCE_LISTENER);
	}

	public static void resetProjection(CEditor CEditor) {
		// XXX: ignore for 3.2, leave for 3.1?
	}

	public void updateFolding() {
		if (!enabled || !ContextCore.getContextManager().isContextActive()) {
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
						IInteractionElement interactionElement = ContextCore.getContextManager().getElement(
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
				StatusHandler.fail(new Status(IStatus.ERROR, CDTUIBridgePlugin.ID_PLUGIN,
						"could not update folding, Mylyn is not correctly installed", e)); //$NON-NLS-1$
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

	private void updateExpansion(List<IInteractionElement> elements) {
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

	@SuppressWarnings("restriction")
	@Override
	public void contextChanged(ContextChangeEvent event) {
		switch (event.getEventKind()) {
		case ACTIVATED:
		case DEACTIVATED:
		case CLEARED:
			if (ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(CDTUIBridgePlugin.AUTO_FOLDING_ENABLED)) {
				updateFolding();
			}
			break;
		case INTEREST_CHANGED:
			updateExpansion(event.getElements());
			break;
		}
	}
}
