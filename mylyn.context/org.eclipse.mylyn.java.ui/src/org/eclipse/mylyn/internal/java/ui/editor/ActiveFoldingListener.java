/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.java.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.text.folding.IJavaFoldingStructureProvider;
import org.eclipse.jdt.ui.text.folding.IJavaFoldingStructureProviderExtension;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.ContextChangeEvent;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.java.ui.JavaStructureBridge;
import org.eclipse.mylyn.internal.java.ui.JavaUiBridgePlugin;

/**
 * @author Mik Kersten
 */
public class ActiveFoldingListener extends AbstractContextListener {

	private final JavaEditor editor;

	private IJavaFoldingStructureProviderExtension updater;

	private static JavaStructureBridge bridge = (JavaStructureBridge) ContextCore
			.getStructureBridge(JavaStructureBridge.CONTENT_TYPE);

	private boolean enabled = false;

	private final IPropertyChangeListener preferenceListener = event -> {
		if (event.getProperty().equals(JavaUiBridgePlugin.AUTO_FOLDING_ENABLED)) {
			enabled = Boolean.parseBoolean(event.getNewValue().toString());
			updateFolding();
		}
	};

	public ActiveFoldingListener(JavaEditor editor) {
		this.editor = editor;
		ContextCore.getContextManager().addListener(this);
		JavaUiBridgePlugin.getDefault().getPreferenceStore().addPropertyChangeListener(preferenceListener);

		enabled = JavaUiBridgePlugin.getDefault()
				.getPreferenceStore()
				.getBoolean(JavaUiBridgePlugin.AUTO_FOLDING_ENABLED);
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
		JavaUiBridgePlugin.getDefault().getPreferenceStore().removePropertyChangeListener(preferenceListener);
	}

	@Deprecated
	public static void resetProjection(JavaEditor javaEditor) {
		// TODO 3.9 remove
	}

	public void updateFolding() {
		if (!enabled || !ContextCore.getContextManager().isContextActive()) {
			editor.resetProjection();
		} else if (editor.getEditorInput() == null) {
			return;
		} else {
			try {
				List<IJavaElement> toExpand = new ArrayList<>();
				List<IJavaElement> toCollapse = new ArrayList<>();

				IJavaElement element = JavaUI.getEditorInputJavaElement(editor.getEditorInput());
				if (element instanceof ICompilationUnit compilationUnit) {
					List<IJavaElement> allChildren = getAllChildren(compilationUnit);
					for (IJavaElement child : allChildren) {
						IInteractionElement interactionElement = ContextCore.getContextManager()
								.getElement(bridge.getHandleIdentifier(child));
						if (interactionElement != null && interactionElement.getInterest().isInteresting()) {
							toExpand.add(child);
						} else {
							toCollapse.add(child);
						}
					}
				}
				if (updater != null) {
					updater.collapseComments();
					updater.collapseMembers();
					updater.expandElements(toExpand.toArray(new IJavaElement[toExpand.size()]));
				}
			} catch (Exception e) {
				StatusHandler
						.log(new Status(IStatus.ERROR, JavaUiBridgePlugin.ID_PLUGIN, "Could not update folding", e)); //$NON-NLS-1$
			}
		}
	}

	private static List<IJavaElement> getAllChildren(IParent parentElement) {
		List<IJavaElement> allChildren = new ArrayList<>();
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

	public void updateFolding(List<IInteractionElement> elements) {
		for (IInteractionElement element : elements) {
			if (updater == null || !enabled) {
				return;
			} else {
				Object object = bridge.getObjectForHandle(element.getHandleIdentifier());
				if (object instanceof IMember member) {
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
	public void contextChanged(ContextChangeEvent event) {
		switch (event.getEventKind()) {
			case ACTIVATED:
			case DEACTIVATED:
				if (JavaUiBridgePlugin.getDefault()
						.getPreferenceStore()
						.getBoolean(JavaUiBridgePlugin.AUTO_FOLDING_ENABLED)) {
					updateFolding();
				}
				break;
			case CLEARED:
				if (event.isActiveContext()) {
					if (JavaUiBridgePlugin.getDefault()
							.getPreferenceStore()
							.getBoolean(JavaUiBridgePlugin.AUTO_FOLDING_ENABLED)) {
						updateFolding();
					}
				}
				break;
			case INTEREST_CHANGED:
				updateFolding(event.getElements());
				break;
		}
	}
}
