/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
/*
 * Created on May 16, 2005
 */
package org.eclipse.mylar.internal.java.ui.editor;

import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.java.JavaStructureBridge;
import org.eclipse.mylar.internal.java.MylarJavaPrefConstants;
import org.eclipse.mylar.provisional.core.IMylarContext;
import org.eclipse.mylar.provisional.core.IMylarContextListener;
import org.eclipse.mylar.provisional.core.IMylarElement;
import org.eclipse.mylar.provisional.core.MylarPlugin;

/**
 * @author Mik Kersten
 */
public class ActiveFoldingListener implements IMylarContextListener {
	private final JavaEditor editor;

	private IJavaFoldingStructureProviderExtension updater;

	private static JavaStructureBridge bridge = (JavaStructureBridge) MylarPlugin.getDefault().getStructureBridge(
			JavaStructureBridge.CONTENT_TYPE);

	private boolean enabled = false;

	private IPropertyChangeListener PREFERENCE_LISTENER = new IPropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent event) {
			if (event.getProperty().equals(MylarJavaPrefConstants.AUTO_FOLDING_ENABLED)) {
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
		MylarPlugin.getContextManager().addListener(this);
		MylarPlugin.getDefault().getPluginPreferences().addPropertyChangeListener(PREFERENCE_LISTENER);

		enabled = MylarPlugin.getDefault().getPreferenceStore().getBoolean(MylarJavaPrefConstants.AUTO_FOLDING_ENABLED);
		try {
			Object adapter = editor.getAdapter(IJavaFoldingStructureProvider.class);
			if (adapter instanceof IJavaFoldingStructureProviderExtension) {
				updater = (IJavaFoldingStructureProviderExtension) adapter;
			} else {
				MylarStatusHandler.log("Could not install active folding on provider: " + adapter + ", must extend "
						+ IJavaFoldingStructureProviderExtension.class.getName(), this);
			}
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "could not install auto folding, reflection denied", false);
		}
		updateFolding();
	}

	public void dispose() {
		MylarPlugin.getContextManager().removeListener(this);
		MylarPlugin.getDefault().getPluginPreferences().removePropertyChangeListener(PREFERENCE_LISTENER);
	}

	public static void resetProjection(JavaEditor javaEditor) {
		// XXX: ignore for 3.2, leave for 3.1?
	}

	public void updateFolding() {
		if (!enabled || !MylarPlugin.getContextManager().isContextActive()) {
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
						IMylarElement mylarElement = MylarPlugin.getContextManager().getElement(
								bridge.getHandleIdentifier(child));
						if (mylarElement != null && mylarElement.getInterest().isInteresting()) {
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
				MylarStatusHandler.fail(e, "couldn't update folding", false);
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

	public void interestChanged(IMylarElement element) {
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
						if (member instanceof IParent) {
							IJavaElement[] children = ((IParent)member).getChildren();
							if (children.length == 1) {
								updater.expandElements(new IJavaElement[] { children[0] });
								if (children[0] instanceof IParent) {
									IJavaElement[] childsChildren = ((IParent)children[0]).getChildren();
									if (childsChildren.length == 1) {
										updater.expandElements(new IJavaElement[] { childsChildren[0] });
									}
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

	public void interestChanged(List<IMylarElement> elements) {
		for (IMylarElement element : elements) {
			interestChanged(element);
		}
	}

	public void contextActivated(IMylarContext context) {
		if (MylarPlugin.getDefault().getPreferenceStore().getBoolean(MylarJavaPrefConstants.AUTO_FOLDING_ENABLED)) {
			updateFolding();
		}
	}

	public void contextDeactivated(IMylarContext context) {
		if (MylarPlugin.getDefault().getPreferenceStore().getBoolean(MylarJavaPrefConstants.AUTO_FOLDING_ENABLED)) {
			updateFolding();
		}
	}

	public void presentationSettingsChanging(IMylarContextListener.UpdateKind kind) {
		// ignore
	}

	public void presentationSettingsChanged(IMylarContextListener.UpdateKind kind) {
		updateFolding();
	}

	public void landmarkAdded(IMylarElement element) {
		// ignore
	}

	public void landmarkRemoved(IMylarElement element) {
		// ignore
	}

	public void edgesChanged(IMylarElement node) {
		// ignore
	}

	public void nodeDeleted(IMylarElement node) {
		// ignore
	}
}

// class ActiveFoldingController implements IPartListener2 {
//
// public ActiveFoldingController(JavaEditor editor) {
// IWorkbenchPartSite site = editor.getSite();
// if (site != null) {
// IWorkbenchPage page = site.getPage();
// if (!page.isPartVisible(editor))
// page.addPartListener(this);
// }
// }
//
// public void updateFolding(final boolean expand, boolean async) {
// if (async) {
// PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
// public void run() {
// internalUpdateFolding(expand);
// }
// });
// } else {
// internalUpdateFolding(expand);
// }
// }
//
// private void internalUpdateFolding(final boolean expand) {
// if (!editor.getSite().getPage().isPartVisible(editor))
// return;
// ISourceViewer sourceViewer = editor.getViewer();
// if (sourceViewer instanceof ProjectionViewer) {
// ProjectionViewer pv = (ProjectionViewer) sourceViewer;
// if (isAutoFoldingEnabled()) {
// if (expand) {
// if (pv.canDoOperation(ProjectionViewer.EXPAND))
// pv.doOperation(ProjectionViewer.EXPAND);
// } else {
// if (pv.canDoOperation(ProjectionViewer.COLLAPSE))
// pv.doOperation(ProjectionViewer.COLLAPSE);
// }
// }
// }
// }
//
// public void resetFolding() {
// PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
// public void run() {
// ActiveFoldingListener.resetProjection(editor);
// }
// });
// }
//
// private boolean isAutoFoldingEnabled() {
// return
// AutoFoldingStructureProvider.ID.equals(JavaPlugin.getDefault().getPreferenceStore().getString(
// PreferenceConstants.EDITOR_FOLDING_PROVIDER));
// }
//
// public void partVisible(IWorkbenchPartReference partRef) {
// // don't care when a part becomes visible
// }
//
// public void partActivated(IWorkbenchPartReference partRef) {
// if (editor.equals(partRef.getPart(false))) {
// updateFolding(true, true);
// }
// }
//
// public void partClosed(IWorkbenchPartReference partRef) {
// // monitor.unregisterEditor(editor);
// }
//
// public void partBroughtToTop(IWorkbenchPartReference partRef) {
// if (editor.equals(partRef.getPart(false))) {
// // cancel();
// updateFolding(true, true);
// }
// }
//
// public void partDeactivated(IWorkbenchPartReference partRef) {
// // don't care when a part is deactivated
// }
//
// public void partOpened(IWorkbenchPartReference partRef) {
// // don't care when a part is opened
// }
//
// public void partHidden(IWorkbenchPartReference partRef) {
// // don't care when a part is hidden
// }
//
// public void partInputChanged(IWorkbenchPartReference partRef) {
// // don't care when an input changes
// }
// }
