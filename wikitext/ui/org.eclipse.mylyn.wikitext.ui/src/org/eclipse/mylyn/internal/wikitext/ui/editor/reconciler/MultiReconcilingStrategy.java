/*******************************************************************************
 * Copyright (c) 2007, 2021 David Green and others.
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
package org.eclipse.mylyn.internal.wikitext.ui.editor.reconciler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;

/**
 * @author David Green
 */
public class MultiReconcilingStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension {

	private final List<IReconcilingStrategy> strategies = new CopyOnWriteArrayList<>();

	public void add(IReconcilingStrategy strategy) {
		if (strategy == null) {
			throw new IllegalArgumentException();
		}
		strategies.add(strategy);
	}

	public void remove(IReconcilingStrategy strategy) {
		if (strategy == null) {
			throw new IllegalArgumentException();
		}
		strategies.remove(strategy);
	}

	public boolean contains(IReconcilingStrategy reconcilingStrategy) {
		return strategies.contains(reconcilingStrategy);
	}

	@Override
	public void initialReconcile() {
		for (IReconcilingStrategy strategy : strategies) {
			if (strategy instanceof IReconcilingStrategyExtension) {
				((IReconcilingStrategyExtension) strategy).initialReconcile();
			}
		}
	}

	@Override
	public void setProgressMonitor(IProgressMonitor monitor) {
		for (IReconcilingStrategy strategy : strategies) {
			if (strategy instanceof IReconcilingStrategyExtension) {
				((IReconcilingStrategyExtension) strategy).setProgressMonitor(monitor);
			}
		}
	}

	@Override
	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		for (IReconcilingStrategy strategy : strategies) {
			strategy.reconcile(dirtyRegion, subRegion);
		}
	}

	@Override
	public void reconcile(IRegion partition) {
		for (IReconcilingStrategy strategy : strategies) {
			strategy.reconcile(partition);
		}
	}

	@Override
	public void setDocument(IDocument document) {
		for (IReconcilingStrategy strategy : strategies) {
			strategy.setDocument(document);
		}
	}

}
