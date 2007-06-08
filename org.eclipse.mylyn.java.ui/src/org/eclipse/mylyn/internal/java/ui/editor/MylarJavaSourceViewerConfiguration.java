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
 * Created on Mar 14, 2005
 */
package org.eclipse.mylyn.internal.java.ui.editor;

import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Installs Mylar content assist and hyperlink detection
 * 
 * @author Mik Kersten
 */
public class MylarJavaSourceViewerConfiguration extends JavaSourceViewerConfiguration {

	public MylarJavaSourceViewerConfiguration(IColorManager colorManager, IPreferenceStore preferenceStore,
			ITextEditor editor, String partitioning) {
		super(colorManager, preferenceStore, editor, partitioning);
	}

//	private HyperlinkDetectorExtensionReader detectorExtensionReader = new HyperlinkDetectorExtensionReader();
//	
//	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
//		if (!fPreferenceStore.getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_HYPERLINKS_ENABLED))
//			return null;
//
//		IHyperlinkDetector[] inheritedDetectors = super.getHyperlinkDetectors(sourceViewer);
//
//		if (super.getEditor() == null)
//			return inheritedDetectors;
//
//		List contributedDectectors = detectorExtensionReader.readHyperlinkDetectorsExtension(super.getEditor());
//		if (contributedDectectors.isEmpty()) {
//			return inheritedDetectors;
//		} else {
//			List allDetectors = new ArrayList();
//			allDetectors.addAll(Arrays.asList(inheritedDetectors));
//			allDetectors.addAll(contributedDectectors);
//			return (IHyperlinkDetector[]) allDetectors.toArray(new IHyperlinkDetector[allDetectors.size()]);
//		}
//	}
//
//	private final class HyperlinkDetectorExtensionReader {
//
//		private List contributedDetectors = new ArrayList();
//
//		private boolean extensionsRead = false;
//
//		public static final String JAVA_EDITOR_CONTRIBUTOR_EXTENSION_POINT_ID = "org.eclipse.mylyn.java.javaEditorContributor";
//
//		public static final String JAVA_HYPERLINK_DETECTOR_ELEMENT = "hyperlinkDetector";
//
//		public static final String HYPERLINK_DETECTOR_CLASS = "class";
//
//		private List/*AbstractHyperlinkDetector*/ readHyperlinkDetectorsExtension(ITextEditor textEditor) {
//			if (!extensionsRead) {
//				IExtensionRegistry registry = Platform.getExtensionRegistry();
//				IExtensionPoint extensionPoint = registry.getExtensionPoint(JAVA_EDITOR_CONTRIBUTOR_EXTENSION_POINT_ID);
//				IExtension[] extensions = extensionPoint.getExtensions();
//				for (int i = 0; i < extensions.length; i++) {
//					IConfigurationElement[] elements = extensions[i].getConfigurationElements();
//					for (int j = 0; j < elements.length; j++) {
//						if (elements[j].getName().compareTo(JAVA_HYPERLINK_DETECTOR_ELEMENT) == 0) {
//							try {
//								Object detector = elements[j].createExecutableExtension(HYPERLINK_DETECTOR_CLASS);
//								if (detector instanceof AbstractHyperlinkDetector) {
//									((AbstractHyperlinkDetector) detector).setEditor(textEditor);
//									contributedDetectors.add(detector);
//								}
//							} catch (CoreException e) {
//								MylarStatusHandler.log(e, "Could not load java editor contributor");
//							}
//						}
//					}
//				}
//				extensionsRead = true;
//			}
//			return contributedDetectors;
//		}
//	}
}
