/***********************************************************************************************************************
 * Copyright (c) 2005, 2007 Mylyn project committers and others. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 **********************************************************************************************************************/

//package org.eclipse.mylyn.internal.context.ui.views;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Iterator;
//import java.util.List;
//
//import org.eclipse.core.resources.IResource;
//import org.eclipse.core.runtime.IAdaptable;
//import org.eclipse.jface.util.Assert;
//import org.eclipse.jface.util.TransferDragSourceListener;
//import org.eclipse.jface.viewers.ISelection;
//import org.eclipse.jface.viewers.ISelectionProvider;
//import org.eclipse.jface.viewers.IStructuredSelection;
//import org.eclipse.swt.dnd.DND;
//import org.eclipse.swt.dnd.DragSourceAdapter;
//import org.eclipse.swt.dnd.DragSourceEvent;
//import org.eclipse.swt.dnd.Transfer;
//
///**
// * @author Mik Kersten
// */
//public class ActiveViewResourceDragAdapter extends DragSourceAdapter implements TransferDragSourceListener {
//
//	private ISelectionProvider fProvider;
//
//	/**
//	 * Creates a new ActiveViewResourceDragAdapter for the given selection
//	 * provider.
//	 * 
//	 * @param provider
//	 *            the selection provider to access the viewer's selection
//	 */
//	public ActiveViewResourceDragAdapter(ISelectionProvider provider) {
//		fProvider = provider;
//		Assert.isNotNull(fProvider);
//	}
//
//	public Transfer getTransfer() {
//		return ResourceTransfer.getInstance();
//	}
//
//	public void dragStart(DragSourceEvent event) {
//		event.doit = convertSelection().size() > 0;
//	}
//
//	public void dragSetData(DragSourceEvent event) {
//		List<IResource> resources = convertSelection();
//		event.data = (IResource[]) resources.toArray(new IResource[resources.size()]);
//	}
//
//	public void dragFinished(DragSourceEvent event) {
//		if (!event.doit)
//			return;
//
//		if (event.detail == DND.DROP_MOVE) {
//			handleFinishedDropMove(event);
//		}
//	}
//
//	private List<IResource> convertSelection() {
//		ISelection s = fProvider.getSelection();
//		if (!(s instanceof IStructuredSelection))
//			return Collections.emptyList();
//		IStructuredSelection selection = (IStructuredSelection) s;
//		List<IResource> result = new ArrayList<IResource>(selection.size());
//		for (Iterator iter = selection.iterator(); iter.hasNext();) {
//			Object element = iter.next();
//			IResource resource = null;
//			if (element instanceof IAdaptable) {
//				resource = (IResource) ((IAdaptable) element).getAdapter(IResource.class);
//			}
//			if (resource != null)
//				result.add(resource);
//		}
//		return result;
//	}
//
//	private void handleFinishedDropMove(DragSourceEvent event) {
//		// ignore
//	}
//}