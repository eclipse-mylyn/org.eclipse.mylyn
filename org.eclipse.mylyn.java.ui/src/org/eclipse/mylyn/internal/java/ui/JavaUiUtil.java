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

package org.eclipse.mylyn.internal.java.ui;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;
import org.eclipse.jdt.ui.JavaElementImageDescriptor;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.views.markers.internal.ConcreteMarker;

/**
 * @author Mik Kersten
 */
public class JavaUiUtil {

	private static final Point SMALL_SIZE = new Point(16, 16);

	private static final String SEPARATOR_CODEASSIST = "\0"; //$NON-NLS-1$

	public static final String ASSIST_MYLAR_TYPE = "org.eclipse.mylyn.java.javaTypeProposalCategory";

	public static final String ASSIST_MYLAR_NOTYPE = "org.eclipse.mylyn.java.javaNoTypeProposalCategory";

	public static final String ASSIST_JDT_TYPE = "org.eclipse.jdt.ui.javaTypeProposalCategory";
	
	public static final String ASSIST_JDT_NOTYPE = "org.eclipse.jdt.ui.javaNoTypeProposalCategory";

	public static final String ASSIST_JDT_TEMPLATE= "org.eclipse.jdt.ui.templateProposalCategory";

	public static final String ASSIST_MYLAR_TEMPLATE = "org.eclipse.mylyn.java.templateProposalCategory";

	public static void installContentAssist(IPreferenceStore javaPrefs, boolean mylarContentAssist) {
		String oldValue = javaPrefs.getString(PreferenceConstants.CODEASSIST_EXCLUDED_CATEGORIES);
		StringTokenizer tokenizer = new StringTokenizer(oldValue, SEPARATOR_CODEASSIST);
		Set<String> disabledIds = new HashSet<String>();
		while (tokenizer.hasMoreTokens()) {
			disabledIds.add((String) tokenizer.nextElement());
		}
		if (!mylarContentAssist) {
			disabledIds.remove(ASSIST_JDT_TYPE);
			disabledIds.remove(ASSIST_JDT_NOTYPE);
			disabledIds.remove(ASSIST_JDT_TEMPLATE);
			disabledIds.add(ASSIST_MYLAR_NOTYPE);
			disabledIds.add(ASSIST_MYLAR_TYPE);
			disabledIds.add(ASSIST_MYLAR_TEMPLATE);
		} else {
			disabledIds.add(ASSIST_JDT_TYPE);
			disabledIds.add(ASSIST_JDT_NOTYPE);
			disabledIds.add(ASSIST_JDT_TEMPLATE);
			disabledIds.remove(ASSIST_MYLAR_NOTYPE);
			disabledIds.remove(ASSIST_MYLAR_TYPE);
			disabledIds.remove(ASSIST_MYLAR_TEMPLATE);
		}
		String newValue = "";
		for (String id : disabledIds) {
			newValue += id + SEPARATOR_CODEASSIST;
		}
		javaPrefs.setValue(PreferenceConstants.CODEASSIST_EXCLUDED_CATEGORIES, newValue);
	}

	public static ImageDescriptor decorate(ImageDescriptor base, int decorations) {
		ImageDescriptor imageDescriptor = new JavaElementImageDescriptor(base, decorations, SMALL_SIZE);
		return imageDescriptor;
	}

	public static IJavaElement getJavaElement(ConcreteMarker marker) {
		if (marker == null)
			return null;
		try {
			IResource res = marker.getResource();
			ICompilationUnit cu = null;
			if (res instanceof IFile) {
				IFile file = (IFile) res;
				if (file.getFileExtension().equals("java")) { // TODO:
					// instanceof
					// instead?
					cu = JavaCore.createCompilationUnitFrom(file);
				} else {
					return null;
				}
			}
			if (cu != null) {
				IJavaElement je = cu.getElementAt(marker.getMarker().getAttribute(IMarker.CHAR_START, 0));
				return je;
			} else {
				return null;
			}
		} catch (JavaModelException ex) {
			if (!ex.isDoesNotExist())
				ExceptionHandler.handle(ex, "error", "could not find java element"); //$NON-NLS-2$ //$NON-NLS-1$
			return null;
		} catch (Throwable t) {
			StatusHandler.fail(t, "Could not find element for: " + marker, false);
			return null;
		}
	}

	/**
	 * Get the fully qualified name of a IMember
	 * 
	 * @param m
	 *            The IMember to get the fully qualified name for
	 * @return String representing the fully qualified name
	 */
	public static String getFullyQualifiedName(IJavaElement je) {
		if (!(je instanceof IMember))
			return null;

		IMember m = (IMember) je;
		if (m.getDeclaringType() == null)
			return ((IType) m).getFullyQualifiedName();
		else
			return m.getDeclaringType().getFullyQualifiedName() + "." + m.getElementName();
	}

	public static void closeActiveEditors(boolean javaOnly) {
		for (IWorkbenchWindow workbenchWindow : PlatformUI.getWorkbench().getWorkbenchWindows()) {
			IWorkbenchPage page = workbenchWindow.getActivePage();
			if (page != null) {
				IEditorReference[] references = page.getEditorReferences();
				for (int i = 0; i < references.length; i++) {
					IEditorPart part = references[i].getEditor(false);
					if (part != null) {
						if (javaOnly && part.getEditorInput() instanceof IFileEditorInput && part instanceof JavaEditor) {
							JavaEditor editor = (JavaEditor) part;
							editor.close(true);
						} else if (part instanceof JavaEditor) {
							((AbstractTextEditor) part).close(true);
						}
					}
				}
			}
		}
	}
}
