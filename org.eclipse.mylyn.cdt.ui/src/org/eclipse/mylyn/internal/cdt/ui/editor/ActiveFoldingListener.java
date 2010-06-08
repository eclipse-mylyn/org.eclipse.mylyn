/*******************************************************************************
 * Copyright (c) 2004, 2010 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.cdt.ui.editor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.IParent;
import org.eclipse.cdt.core.model.ISourceReference;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.internal.ui.editor.CEditor;
import org.eclipse.cdt.internal.ui.editor.CSourceViewer;
import org.eclipse.cdt.ui.text.folding.ICFoldingStructureProvider;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.ContextChangeEvent;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.cdt.ui.CDTStructureBridge;
import org.eclipse.mylyn.internal.cdt.ui.CDTUIBridgePlugin;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Mik Kersten
 * @author Jeff Johnston
 * @author Shawn Minto
 */
public class ActiveFoldingListener extends AbstractContextListener {

	private final CEditor editor;

	private ICFoldingStructureProvider updater;

	private static CDTStructureBridge bridge = (CDTStructureBridge) ContextCore.getStructureBridge(CDTStructureBridge.CONTENT_TYPE);

	private boolean enabled = false;

	private boolean isDisposed = false;

	private final IPropertyChangeListener PREFERENCE_LISTENER = new IPropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent event) {
			if (event.getProperty().equals(CDTUIBridgePlugin.AUTO_FOLDING_ENABLED)) {
				if (Boolean.parseBoolean(event.getNewValue().toString())) {
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
		ContextCore.getContextManager().addListener(this);
		CDTUIBridgePlugin.getDefault().getPreferenceStore().addPropertyChangeListener(PREFERENCE_LISTENER);

		enabled = CDTUIBridgePlugin.getDefault()
				.getPreferenceStore()
				.getBoolean(CDTUIBridgePlugin.AUTO_FOLDING_ENABLED);
		try {
			Class<CEditor> clazz = CEditor.class;
			Field f = clazz.getDeclaredField("fProjectionModelUpdater"); //$NON-NLS-1$
			f.setAccessible(true);
			ICFoldingStructureProvider updater = (ICFoldingStructureProvider) f.get(editor);
			if (updater instanceof ICFoldingStructureProvider) {
				this.updater = updater;
			} else {
				StatusHandler.log(new Status(IStatus.ERROR, CDTUIBridgePlugin.ID_PLUGIN,
						"Could not install active folding on provider: " + clazz + ", must extend " //$NON-NLS-1$ //$NON-NLS-2$
								+ ICFoldingStructureProvider.class.getName()));
			}
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, CDTUIBridgePlugin.ID_PLUGIN,
					"Could not install auto folding, reflection denied", e)); //$NON-NLS-1$
		}

		// XXX Look into this, there must be something else that we can do to handle this case
		Job j = new UIJob(Messages.ActiveFoldingListener_Updating_Folding) {

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				// need the isDisposed since we do the folding asynchronously and the editor could have been closed
				// between the time the job was scheduled and the time it runs
				if (!isDisposed) {
					updateFolding();
				}
				return Status.OK_STATUS;
			}

		};
		j.schedule(1000);
	}

	public void dispose() {
		isDisposed = true;
		ContextCore.getContextManager().removeListener(this);
		CDTUIBridgePlugin.getDefault().getPreferenceStore().removePropertyChangeListener(PREFERENCE_LISTENER);
	}

	public void updateFolding() {
		if (!enabled || !ContextCore.getContextManager().isContextActive()) {
			editor.resetProjection();
		} else if (editor.getEditorInput() == null) {
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
				if (updater != null) {
					collapseAllElements();
					Point selectedRange = editor.getViewer().getSelectedRange();
					expandElements(toExpand.toArray(new ICElement[toExpand.size()]));
					editor.getViewer().setSelectedRange(selectedRange.x, selectedRange.y);
					editor.getViewer().revealRange(selectedRange.x, selectedRange.y);
				}
			} catch (Exception e) {
				StatusHandler.log(new Status(IStatus.ERROR, CDTUIBridgePlugin.ID_PLUGIN, "Could not update folding", e)); //$NON-NLS-1$
			}
		}
	}

	protected void collapseElements(ICElement[] elements) {
		for (int i = 0; i < elements.length; ++i) {
			collapse(elements[i]);
		}
	}

	private void collapseAllElements() {
		CSourceViewer viewer = (CSourceViewer) editor.getViewer();
		if (viewer != null) {
			viewer.doOperation(ProjectionViewer.COLLAPSE_ALL);
		}
	}

	private void collapse(ICElement element) {
		// FIXME we do not support collapse right now
//		CSourceViewer viewer = (CSourceViewer) editor.getViewer();
//		editor.setSelection(element);
//		viewer.doOperation(ProjectionViewer.COLLAPSE);
	}

	protected void expandElements(ICElement[] elements) {
		for (int i = 0; i < elements.length; ++i) {
			expand(elements[i]);
		}
	}

	private void expand(ICElement element) {
		CSourceViewer viewer = (CSourceViewer) editor.getViewer();
		if (element instanceof ISourceReference && !(element instanceof ITranslationUnit)) {
			ISourceReference reference = (ISourceReference) element;

			try {
				viewer.exposeModelRange(new Region(reference.getSourceRange().getIdStartPos(), 0));
			} catch (CModelException e) {
				// ignore failures
			}

			// makes things jump around
//			editor.setSelection(element);
//			viewer.doOperation(ProjectionViewer.EXPAND);
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

	public void updateFolding(List<IInteractionElement> elements) {
		try {
			for (IInteractionElement element : elements) {
				if (updater == null || !enabled) {
					return;
				} else {
					Object object = bridge.getObjectForHandle(element.getHandleIdentifier());
					if (object instanceof ICElement) {
						ICElement member = (ICElement) object;
						if (element.getInterest().isInteresting()) {
							expandElements(new ICElement[] { member });
							// expand the next 2 children down (e.g. anonymous types)
							if (!(member instanceof IParent)) {
								return;
							}
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
						} else {
							collapseElements(new ICElement[] { member });
						}
					}
				}
			}
		} catch (Exception e) {
			// ignore elements that we can't resolve
		}
	}

	@Override
	public void contextChanged(ContextChangeEvent event) {
		switch (event.getEventKind()) {
		case ACTIVATED:
		case DEACTIVATED:
			if (CDTUIBridgePlugin.getDefault().getPreferenceStore().getBoolean(CDTUIBridgePlugin.AUTO_FOLDING_ENABLED)) {
				updateFolding();
			}
			break;
		case CLEARED:
			if (event.isActiveContext()) {
				if (CDTUIBridgePlugin.getDefault().getPreferenceStore().getBoolean(
						CDTUIBridgePlugin.AUTO_FOLDING_ENABLED)) {
					updateFolding();
				}
			}
			break;
		case INTEREST_CHANGED:
			updateFolding(event.getElements());
			break;
		}
	}

	public static void resetProjection(CEditor editor2) {
		// ignore

	}
}
