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

package org.eclipse.mylyn.internal.wikitext.ui.util.css.editor;

import java.util.Map;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.graphics.RGB;

/**
 * A configuration for editing CSS
 * 
 * @author David Green
 */
public class CssConfiguration extends SourceViewerConfiguration {

	private final ColorRegistry colorRegistry;

	public CssConfiguration(ColorRegistry colorRegistry) {
		this.colorRegistry = colorRegistry;
		for (Map.Entry<String, RGB> colorEnt : Colors.keyToRgb.entrySet()) {
			colorRegistry.put(colorEnt.getKey(), colorEnt.getValue());
		}
	}

	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] { IDocument.DEFAULT_CONTENT_TYPE, CssPartitionScanner.CONTENT_TYPE_COMMENT,
				CssPartitionScanner.CONTENT_TYPE_BLOCK };
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getCssTokenScanner());
		reconciler.setDamager(dr, CssPartitionScanner.CONTENT_TYPE_BLOCK);
		reconciler.setRepairer(dr, CssPartitionScanner.CONTENT_TYPE_BLOCK);

		dr = new DefaultDamagerRepairer(getCssScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		CommentDamagerRepairer commentDamagerRepairer = new CommentDamagerRepairer(new TextAttribute(
				colorRegistry.get(Colors.KEY_COMMENT)));
		reconciler.setDamager(commentDamagerRepairer, CssPartitionScanner.CONTENT_TYPE_COMMENT);
		reconciler.setRepairer(commentDamagerRepairer, CssPartitionScanner.CONTENT_TYPE_COMMENT);

		return reconciler;
	}

	private ITokenScanner getCssScanner() {
		return new CssScanner(colorRegistry);
	}

	private ITokenScanner getCssTokenScanner() {
		return new CssBlockScanner(colorRegistry);
	}
}
