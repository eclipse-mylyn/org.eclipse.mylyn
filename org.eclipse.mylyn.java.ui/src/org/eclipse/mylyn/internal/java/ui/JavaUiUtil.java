/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.java.ui;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.text.java.CompletionProposalComputerRegistry;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author Mik Kersten
 */
public class JavaUiUtil {

	private static final String SEPARATOR_CODEASSIST = "\0"; //$NON-NLS-1$

	public static final String ASSIST_MYLYN_TYPE = "org.eclipse.mylyn.java.javaTypeProposalCategory"; //$NON-NLS-1$

	public static final String ASSIST_MYLYN_NOTYPE = "org.eclipse.mylyn.java.javaNoTypeProposalCategory"; //$NON-NLS-1$

	public static final String ASSIST_JDT_ALL = "eclipse.jdt.ui.javaAllProposalCategory"; //$NON-NLS-1$

	@Deprecated
	public static final String ASSIST_JDT_TYPE = "org.eclipse.jdt.ui.javaTypeProposalCategory"; //$NON-NLS-1$

	@Deprecated
	public static final String ASSIST_JDT_NOTYPE = "org.eclipse.jdt.ui.javaNoTypeProposalCategory"; //$NON-NLS-1$

	public static final String ASSIST_JDT_TEMPLATE = "org.eclipse.jdt.ui.templateProposalCategory"; //$NON-NLS-1$

	public static final String ASSIST_MYLYN_TEMPLATE = "org.eclipse.mylyn.java.templateProposalCategory"; //$NON-NLS-1$

	public static boolean isDefaultAssistActive(String computerId) {
		Set<String> disabledIds = getDisabledIds(JavaPlugin.getDefault().getPreferenceStore());
		return !disabledIds.contains(computerId);
	}

	public static void installContentAssist(IPreferenceStore javaPrefs, boolean mylynContentAssist) {
		Set<String> disabledIds = getDisabledIds(javaPrefs);
		if (!mylynContentAssist) {
			disabledIds.remove(ASSIST_JDT_ALL);
			disabledIds.remove(ASSIST_JDT_TYPE);
			disabledIds.remove(ASSIST_JDT_NOTYPE);
			disabledIds.remove(ASSIST_JDT_TEMPLATE);
			disabledIds.add(ASSIST_MYLYN_NOTYPE);
			disabledIds.add(ASSIST_MYLYN_TYPE);
			disabledIds.add(ASSIST_MYLYN_TEMPLATE);
		} else {
			disabledIds.remove(ASSIST_JDT_ALL);
			disabledIds.add(ASSIST_JDT_TYPE);
			disabledIds.add(ASSIST_JDT_NOTYPE);
			disabledIds.add(ASSIST_JDT_TEMPLATE);
			disabledIds.remove(ASSIST_MYLYN_NOTYPE);
			disabledIds.remove(ASSIST_MYLYN_TYPE);
			disabledIds.remove(ASSIST_MYLYN_TEMPLATE);
		}
		String newValue = ""; //$NON-NLS-1$
		for (String id : disabledIds) {
			newValue += id + SEPARATOR_CODEASSIST;
		}
		javaPrefs.setValue(PreferenceConstants.CODEASSIST_EXCLUDED_CATEGORIES, newValue);

		CompletionProposalComputerRegistry.getDefault().reload();
	}

	private static Set<String> getDisabledIds(IPreferenceStore javaPrefs) {
		String oldValue = javaPrefs.getString(PreferenceConstants.CODEASSIST_EXCLUDED_CATEGORIES);
		StringTokenizer tokenizer = new StringTokenizer(oldValue, SEPARATOR_CODEASSIST);
		Set<String> disabledIds = new HashSet<String>();
		while (tokenizer.hasMoreTokens()) {
			disabledIds.add((String) tokenizer.nextElement());
		}
		return disabledIds;
	}

	// TODO: remove dead code for 3.1
//	public static ImageDescriptor decorate(ImageDescriptor base, int decorations) {
//		ImageDescriptor imageDescriptor = new JavaElementImageDescriptor(base, decorations, SMALL_SIZE);
//		return imageDescriptor;
//	}

	// TODO: remove dead code for 3.1
//	public static IJavaElement getJavaElement(ConcreteMarker marker) {
//		if (marker == null) {
//			return null;
//		}
//		try {
//			IResource res = marker.getResource();
//			ICompilationUnit cu = null;
//			if (res instanceof IFile) {
//				IFile file = (IFile) res;
//				if (file.getFileExtension().equals("java")) { // TODO: 
//					// instanceof
//					// instead?
//					cu = JavaCore.createCompilationUnitFrom(file);
//				} else {
//					return null;
//				}
//			}
//			if (cu != null) {
//				IJavaElement je = cu.getElementAt(marker.getMarker().getAttribute(IMarker.CHAR_START, 0));
//				return je;
//			} else {
//				return null;
//			}
//		} catch (JavaModelException ex) {
//			if (!ex.isDoesNotExist()) {
//				ExceptionHandler.handle(ex, "error", "could not find java element");
//			}
//			return null;
//		} catch (Throwable t) {
//			StatusHandler.log(new Status(IStatus.ERROR, JavaUiBridgePlugin.ID_PLUGIN, "Could not find element for: " //$NON-NLS-1$
//					+ marker, t));
//			return null;
//		}
//	}

	// TODO: delete dead code for 3.1
//	/**
//	 * Get the fully qualified name of a IMember
//	 * 
//	 * @param m
//	 *            The IMember to get the fully qualified name for
//	 * @return String representing the fully qualified name
//	 */
//	public static String getFullyQualifiedName(IJavaElement je) {
//		if (!(je instanceof IMember)) {
//			return null;
//		}
//
//		IMember m = (IMember) je;
//		if (m.getDeclaringType() == null) {
//			return ((IType) m).getFullyQualifiedName();
//		} else {
//			return m.getDeclaringType().getFullyQualifiedName() + "." + m.getElementName();
//		}
//	}

	// TODO: delete dead code for 3.1
//	@Deprecated
//	public static void closeActiveEditors(boolean javaOnly) {
//		for (IWorkbenchWindow workbenchWindow : PlatformUI.getWorkbench().getWorkbenchWindows()) {
//			IWorkbenchPage page = workbenchWindow.getActivePage();
//			if (page != null) {
//				IEditorReference[] references = page.getEditorReferences();
//				for (IEditorReference reference : references) {
//					IEditorPart part = reference.getEditor(false);
//					if (part != null) {
//						if (javaOnly && part.getEditorInput() instanceof IFileEditorInput && part instanceof JavaEditor) {
//							JavaEditor editor = (JavaEditor) part;
//							editor.close(true);
//						} else if (part instanceof JavaEditor) {
//							((AbstractTextEditor) part).close(true);
//						}
//					}
//				}
//			}
//		}
//	}
}
