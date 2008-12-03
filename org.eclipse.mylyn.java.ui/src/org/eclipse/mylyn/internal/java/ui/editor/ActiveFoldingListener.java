/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.java.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.text.folding.IJavaFoldingStructureProvider;
import org.eclipse.jdt.ui.text.folding.IJavaFoldingStructureProviderExtension;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.java.ui.JavaStructureBridge;
import org.eclipse.mylyn.internal.java.ui.JavaUiBridgePlugin;

/**
 * @author Mik Kersten
 */
public class ActiveFoldingListener extends AbstractContextListener {

	private final JavaEditor editor;

	private IJavaFoldingStructureProviderExtension updater;

	private static JavaStructureBridge bridge = (JavaStructureBridge) ContextCore.getStructureBridge(JavaStructureBridge.CONTENT_TYPE);

	private boolean enabled = false;

	private final IPropertyChangeListener PREFERENCE_LISTENER = new IPropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent event) {
			if (event.getProperty().equals(JavaUiBridgePlugin.AUTO_FOLDING_ENABLED)) {
				if (event.getNewValue().equals(Boolean.TRUE.toString())) {
					enabled = true;
				} else {
					enabled = false;
				}
				updateFolding();
			}
		}
	};

	public ActiveFoldingListener(JavaEditor editor) {
		this.editor = editor;
		ContextCore.getContextManager().addListener(this);
		JavaUiBridgePlugin.getDefault().getPluginPreferences().addPropertyChangeListener(PREFERENCE_LISTENER);

		enabled = JavaUiBridgePlugin.getDefault().getPreferenceStore().getBoolean(
				JavaUiBridgePlugin.AUTO_FOLDING_ENABLED);
		try {
			Object adapter = editor.getAdapter(IJavaFoldingStructureProvider.class);
			if (adapter instanceof IJavaFoldingStructureProviderExtension) {
				updater = (IJavaFoldingStructureProviderExtension) adapter;
			} else {
				StatusHandler.log(new Status(IStatus.ERROR, JavaUiBridgePlugin.ID_PLUGIN,
						"Could not install active folding on provider: " + adapter + ", must extend " //$NON-NLS-1$ //$NON-NLS-2$
								+ IJavaFoldingStructureProviderExtension.class.getName()));
			}
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, JavaUiBridgePlugin.ID_PLUGIN,
					"Could not install auto folding, reflection denied", e)); //$NON-NLS-1$
		}
		updateFolding();
	}

	public void dispose() {
		ContextCore.getContextManager().removeListener(this);
		JavaUiBridgePlugin.getDefault().getPluginPreferences().removePropertyChangeListener(PREFERENCE_LISTENER);
	}

	public static void resetProjection(JavaEditor javaEditor) {
		// XXX: ignore for 3.2, leave for 3.1?
	}

	public void updateFolding() {
		if (!enabled || !ContextCore.getContextManager().isContextActive()) {
			editor.resetProjection();
		} else if (editor.getEditorInput() == null) {
			return;
		} else {
			try {
				List<IJavaElement> toExpand = new ArrayList<IJavaElement>();
				List<IJavaElement> toCollapse = new ArrayList<IJavaElement>();

				IJavaElement element = JavaUI.getEditorInputJavaElement(editor.getEditorInput());
				if (element instanceof ICompilationUnit) {
					ICompilationUnit compilationUnit = (ICompilationUnit) element;
					List<IJavaElement> allChildren = getAllChildren(compilationUnit);
					for (IJavaElement child : allChildren) {
						IInteractionElement interactionElement = ContextCore.getContextManager().getElement(
								bridge.getHandleIdentifier(child));
						if (interactionElement != null && interactionElement.getInterest().isInteresting()) {
							toExpand.add(child);
						} else {
							toCollapse.add(child);
						}
					}
				}
				if (updater != null) {
					updater.collapseMembers();
					updater.expandElements(toExpand.toArray(new IJavaElement[toExpand.size()]));
				}
			} catch (Exception e) {
				StatusHandler.log(new Status(IStatus.ERROR, JavaUiBridgePlugin.ID_PLUGIN, "Could not update folding", e)); //$NON-NLS-1$
			}
		}
	}

	private static List<IJavaElement> getAllChildren(IParent parentElement) {
		List<IJavaElement> allChildren = new ArrayList<IJavaElement>();
		try {
			for (IJavaElement child : parentElement.getChildren()) {
				allChildren.add(child);
				if (child instanceof IParent) {
					allChildren.addAll(getAllChildren((IParent) child));
				}
			}
		} catch (JavaModelException e) {
			// ignore failures
		}
		return allChildren;
	}

	@Override
	public void interestChanged(List<IInteractionElement> elements) {
		for (IInteractionElement element : elements) {
			if (updater == null || !enabled) {
				return;
			} else {
				Object object = bridge.getObjectForHandle(element.getHandleIdentifier());
				if (object instanceof IMember) {
					IMember member = (IMember) object;
					if (element.getInterest().isInteresting()) {
						updater.expandElements(new IJavaElement[] { member });
						// expand the next 2 children down (e.g. anonymous types)
						try {
							IJavaElement[] children = ((IParent) member).getChildren();
							if (children.length == 1) {
								updater.expandElements(new IJavaElement[] { children[0] });
								if (children[0] instanceof IParent) {
									IJavaElement[] childsChildren = ((IParent) children[0]).getChildren();
									if (childsChildren.length == 1) {
										updater.expandElements(new IJavaElement[] { childsChildren[0] });
									}
								}
							}
						} catch (JavaModelException e) {
							// ignore
						}
					} else {
						updater.collapseElements(new IJavaElement[] { member });
					}
				}
			}
		}
	}

	@Override
	public void contextActivated(IInteractionContext context) {
		if (JavaUiBridgePlugin.getDefault().getPreferenceStore().getBoolean(JavaUiBridgePlugin.AUTO_FOLDING_ENABLED)) {
			updateFolding();
		}
	}

	@Override
	public void contextDeactivated(IInteractionContext context) {
		if (JavaUiBridgePlugin.getDefault().getPreferenceStore().getBoolean(JavaUiBridgePlugin.AUTO_FOLDING_ENABLED)) {
			updateFolding();
		}
	}

	@Override
	public void contextCleared(IInteractionContext context) {
		if (JavaUiBridgePlugin.getDefault().getPreferenceStore().getBoolean(JavaUiBridgePlugin.AUTO_FOLDING_ENABLED)) {
			updateFolding();
		}
	}
}
