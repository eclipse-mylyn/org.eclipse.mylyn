/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.ui.viewer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.mylyn.internal.wikitext.ui.util.HyperlinkDetectorDelegate;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.texteditor.HyperlinkDetectorDescriptor;
import org.eclipse.ui.texteditor.HyperlinkDetectorRegistry;

/**
 * Extends the default text source viewer configuration to provide greater configurability and extensibility. Makes it
 * easy to filter and customize hyperlink detectors.
 * 
 * @author David Green
 * @since 1.0
 */
public abstract class AbstractTextSourceViewerConfiguration extends TextSourceViewerConfiguration {
	/**
	 * a filter for filtering hyperlink descriptors
	 */
	public interface HyperlinkDetectorDescriptorFilter {
		public boolean filter(HyperlinkDetectorDescriptor descriptor);
	}

	/**
	 * indicate if markup hyperlinks should be detected before other types of hyperlinks. This can affect the order in
	 * which the hyperlinks are presented to the user in the case where multiple hyperlinks are detected in the same
	 * region of text. The default is true.
	 */
	protected boolean markupHyperlinksFirst = true;

	private List<HyperlinkDetectorDescriptorFilter> hyperlinkDetectorDescriptorFilters;

	private List<IHyperlinkDetector> hyperlinkDetectors;

	public AbstractTextSourceViewerConfiguration() {
		super();
	}

	public AbstractTextSourceViewerConfiguration(IPreferenceStore preferenceStore) {
		super(preferenceStore);
	}

	@SuppressWarnings("unchecked")
	@Override
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		List<IHyperlinkDetector> customDetectors = createCustomHyperlinkDetectors(sourceViewer);

		if (sourceViewer == null || fPreferenceStore == null) {
			return customDetectors.toArray(new IHyperlinkDetector[customDetectors.size()]);
		}
		HyperlinkDetectorRegistry registry = EditorsUI.getHyperlinkDetectorRegistry();
		HyperlinkDetectorDescriptor[] descriptors = registry.getHyperlinkDetectorDescriptors();
		Map<String, IAdaptable> targets = getHyperlinkDetectorTargets(sourceViewer);

		List<IHyperlinkDetector> detectors = new ArrayList<IHyperlinkDetector>(8);
		if (markupHyperlinksFirst) {
			detectors.addAll(customDetectors);
		}
		if (hyperlinkDetectors != null) {
			detectors.addAll(hyperlinkDetectors);
		}
		for (Map.Entry<String, IAdaptable> target : targets.entrySet()) {
			String targetId = target.getKey();
			IAdaptable context = target.getValue();

			for (HyperlinkDetectorDescriptor descriptor : descriptors) {
				if (targetId.equals(descriptor.getTargetId())) {
					if (filterHyperlinkDescriptor(descriptor)) {
						continue;
					}
					HyperlinkDetectorDelegate delegate = new HyperlinkDetectorDelegate(descriptor, fPreferenceStore);
					delegate.setContext(context);
					detectors.add(delegate);
				}
			}
		}
		if (!markupHyperlinksFirst) {
			detectors.addAll(customDetectors);
		}
		return detectors.toArray(new IHyperlinkDetector[detectors.size()]);
	}

	/**
	 * Provide custom hyperlink detectors. Subclasses may override, the default implementation provides an empty list.
	 */
	protected List<IHyperlinkDetector> createCustomHyperlinkDetectors(ISourceViewer sourceViewer) {
		return Collections.emptyList();
	}

	/**
	 * Indicate if the given hyperlink detector descriptor should be filtered. Filtered descriptors are not included.
	 * 
	 * @see #getHyperlinkDetectors(ISourceViewer)
	 */
	private boolean filterHyperlinkDescriptor(HyperlinkDetectorDescriptor descriptor) {
		if (hyperlinkDetectorDescriptorFilters == null || hyperlinkDetectorDescriptorFilters.isEmpty()) {
			return false;
		}
		for (HyperlinkDetectorDescriptorFilter filter : hyperlinkDetectorDescriptorFilters) {
			if (filter.filter(descriptor)) {
				return true;
			}
		}
		return false;
	}

	public void addHyperlinkDetectorDescriptorFilter(HyperlinkDetectorDescriptorFilter filter) {
		if (hyperlinkDetectorDescriptorFilters == null) {
			hyperlinkDetectorDescriptorFilters = new ArrayList<HyperlinkDetectorDescriptorFilter>();
		}
		hyperlinkDetectorDescriptorFilters.add(filter);
	}

	public void addHyperlinkDetector(IHyperlinkDetector detector) {
		if (hyperlinkDetectors == null) {
			hyperlinkDetectors = new ArrayList<IHyperlinkDetector>();
		}
		hyperlinkDetectors.add(detector);
	}
}
