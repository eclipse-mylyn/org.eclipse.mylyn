/*******************************************************************************
 * Copyright (c) 2004, 2008 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.cdt.mylyn.internal.ui;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.CoreModelUtil;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.internal.ui.util.ExceptionHandler;
import org.eclipse.cdt.ui.CElementImageDescriptor;
import org.eclipse.cdt.ui.PreferenceConstants;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.views.markers.internal.ConcreteMarker;

/**
 * @author Mik Kersten
 * @author Jeff Johnston
 */
public class CDTUiUtil {

	private static final Point SMALL_SIZE = new Point(16, 16);

	private static final String SEPARATOR_CODEASSIST = "\0"; 

	public static final String ASSIST_MYLYN_TYPE = "org.eclipse.cdt.mylyn.cdtTypeProposalCategory"; // $NON-NLS-1$

	public static final String ASSIST_MYLYN_NOTYPE = "org.eclipse.cdt.mylyn.cdtNoTypeProposalCategory"; // $NON-NLS-1$

	public static final String ASSIST_CDT_TYPE = "org.eclipse.cdt.ui.cdtTypeProposalCategory"; // $NON-NLS-1$

	public static final String ASSIST_CDT_NOTYPE = "org.eclipse.cdt.ui.cdtNoTypeProposalCategory";  // $NON-NLS-1$

	public static final String ASSIST_CDT_TEMPLATE = "org.eclipse.cdt.ui.templateProposalCategory"; // $NON-NLS-1$

	public static final String ASSIST_MYLYN_TEMPLATE = "org.eclipse.cdt.mylyn.templateProposalCategory"; // $NON-NLS-1$

	public static void installContentAssist(IPreferenceStore cdtPrefs, boolean mylynContentAssist) {
		String oldValue = cdtPrefs.getString(PreferenceConstants.CODEASSIST_EXCLUDED_CATEGORIES);
		StringTokenizer tokenizer = new StringTokenizer(oldValue, SEPARATOR_CODEASSIST);
		Set<String> disabledIds = new HashSet<String>();
		while (tokenizer.hasMoreTokens()) {
			disabledIds.add((String) tokenizer.nextElement());
		}
		if (!mylynContentAssist) {
			disabledIds.remove(ASSIST_CDT_TYPE);
			disabledIds.remove(ASSIST_CDT_NOTYPE);
			disabledIds.remove(ASSIST_CDT_TEMPLATE);
			disabledIds.add(ASSIST_MYLYN_NOTYPE);
			disabledIds.add(ASSIST_MYLYN_TYPE);
			disabledIds.add(ASSIST_MYLYN_TEMPLATE);
		} else {
			disabledIds.add(ASSIST_CDT_TYPE);
			disabledIds.add(ASSIST_CDT_NOTYPE);
			disabledIds.add(ASSIST_CDT_TEMPLATE);
			disabledIds.remove(ASSIST_MYLYN_NOTYPE);
			disabledIds.remove(ASSIST_MYLYN_TYPE);
			disabledIds.remove(ASSIST_MYLYN_TEMPLATE);
		}
		String newValue = ""; // $NON-NLS-1$
		for (String id : disabledIds) {
			newValue += id + SEPARATOR_CODEASSIST;
		}
		cdtPrefs.setValue(PreferenceConstants.CODEASSIST_EXCLUDED_CATEGORIES, newValue);
	}

	public static ImageDescriptor decorate(ImageDescriptor base, int decorations) {
		ImageDescriptor imageDescriptor = new CElementImageDescriptor(base, decorations, SMALL_SIZE);
		return imageDescriptor;
	}

	public static ICElement getCDTElement(ConcreteMarker marker) {
		if (marker == null)
			return null;
		try {
			IResource res = marker.getResource();
			ITranslationUnit cu = null;
			if (res instanceof IFile) {
				IFile file = (IFile) res;
				if (CoreModel.isValidTranslationUnitName(null, file.getName())) {
					cu = CoreModelUtil.findTranslationUnit(file);
				} else {
					return null;
				}
			}
			if (cu != null) {
				ICElement ce = cu.getElementAtOffset(marker.getMarker().getAttribute(IMarker.CHAR_START, 0));
				return ce;
			} else {
				return null;
			}
		} catch (CModelException ex) {
			if (!ex.doesNotExist())
				ExceptionHandler.handle(ex, CDTUIBridgePlugin.getResourceString("MylynCDT.error"), // $NON-NLS-1$
						CDTUIBridgePlugin.getResourceString("MylynCDT.findCElementFailure")); //$NON-NLS-1$
			return null;
		} catch (Throwable t) {
			StatusHandler.fail(t, CDTUIBridgePlugin.getFormattedString("MylynCDT.findElementFailure", new String[]{marker.toString()}), false); // $NON-NLS-1$
			return null;
		}
	}

	/**
	 * Get the fully qualified name of an IMember or IFunction
	 * 
	 * @param m
	 *            The IMember or IFunction to get the fully qualified name for
	 * @return String representing the fully qualified name
	 */
	public static String getFullyQualifiedName(ICElement ce) {
		if (ce.getParent() == null || ce.getParent() instanceof ICProject)
			return ce.getElementName();
		else
			return getFullyQualifiedName(ce.getParent()) + "." + ce.getElementName(); // $NON-NLS-1$
	}

}
