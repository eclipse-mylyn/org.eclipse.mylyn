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

package org.eclipse.mylyn.internal.java.ui.junit;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.junit.launcher.JUnitLaunchConfigurationConstants;
import org.eclipse.jdt.internal.junit.launcher.TestKindRegistry;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.core.IInteractionRelation;
import org.eclipse.mylyn.internal.java.ui.JavaStructureBridge;
import org.eclipse.mylyn.internal.java.ui.search.JUnitReferencesProvider;
import org.eclipse.mylyn.internal.monitor.core.util.StatusManager;

/**
 * @author Mik Kersten
 */
public class InteractionContextTestUtil {

	public static void setupTestConfiguration(Set<IType> contextTestCases, ILaunchConfiguration configuration, IProgressMonitor pm)
			throws CoreException {
		String testKindId = TestKindRegistry.JUNIT3_TEST_KIND_ID;

		IJavaProject javaProject = null;
		for (IType type : contextTestCases) {
			IProjectNature nature = type.getJavaProject().getProject().getNature("org.eclipse.pde.PluginNature");
			if (nature != null) {
				// HACK: might want another project
				javaProject = type.getJavaProject();
			}
		}

		ILaunchConfigurationWorkingCopy workingCopy = configuration.getWorkingCopy();
		if (javaProject != null) {
			workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, javaProject.getElementName());
		}

		// HACK: only checks first type
		if (contextTestCases.size() > 0) {
			testKindId = TestKindRegistry.getContainerTestKindId(contextTestCases.iterator().next());				
			workingCopy.setAttribute(JUnitLaunchConfigurationConstants.ATTR_TEST_RUNNER_KIND, testKindId);
			//			testKind = TestKindRegistry.getDefault().getKind(configuration);// contextTestCases.iterator().next());
		}
		workingCopy.doSave();
	}

	public static Set<IType> getTestCasesInContext() {
		Set<IType> testTypes = new HashSet<IType>();
		List<IInteractionElement> interesting = ContextCorePlugin.getContextManager().getActiveContext().getInteresting();
		AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault().getStructureBridge(
				JavaStructureBridge.CONTENT_TYPE);
		try {
			for (IInteractionElement element : interesting) {
				if (element.getContentType().equals(JavaStructureBridge.CONTENT_TYPE)) {
					Object javaElement = bridge.getObjectForHandle(element.getHandleIdentifier());
					if (javaElement instanceof IType) {
						IType type = (IType) javaElement;
						if (isTestType(type)) {
							testTypes.add(type);
						}
					}
					for (IInteractionRelation relation : element.getRelations()) {
						if (relation.getRelationshipHandle().equals(JUnitReferencesProvider.ID)) {
							IInteractionElement target = relation.getTarget();
							Object targetObject = bridge.getObjectForHandle(target.getHandleIdentifier());
							if (targetObject instanceof IMethod) {
								IMethod testMethod = (IMethod) targetObject;
								if (isTestType((IType) testMethod.getParent())) {
									testTypes.add((IType) testMethod.getParent());
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			StatusManager.fail(e, "could not add all test types", false);
		}
		return testTypes;
	}

	public static boolean isTestType(IType type) {
		ITypeHierarchy hierarchy;
		try {
			if (Flags.isAbstract(type.getFlags()))
				return false;
		} catch (JavaModelException e) {
			return false;
		}
		try {
			hierarchy = type.newSupertypeHierarchy(null);
			IType[] supertypes = hierarchy.getAllSuperclasses(type);
			for (int i = 0; i < supertypes.length; i++) {
				if (supertypes[i].getFullyQualifiedName().equals("junit.framework.TestCase")) {
					return true;
				}
			}
		} catch (JavaModelException e) {
			// ContextCorePlugin.log(e, "could not determine test type");
			// ignore, hierarchy is probably inconsistent
		}
		return false;
	}
}
