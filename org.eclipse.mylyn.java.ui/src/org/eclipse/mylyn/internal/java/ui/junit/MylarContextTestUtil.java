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

package org.eclipse.mylar.internal.java.ui.junit;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.junit.launcher.ITestKind;
import org.eclipse.jdt.internal.junit.launcher.TestKindRegistry;
import org.eclipse.jdt.internal.junit.launcher.TestSearchResult;
import org.eclipse.mylar.context.core.IMylarElement;
import org.eclipse.mylar.context.core.IMylarRelation;
import org.eclipse.mylar.context.core.IMylarStructureBridge;
import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.java.JavaStructureBridge;
import org.eclipse.mylar.internal.java.search.JUnitReferencesProvider;

/**
 * @author Mik Kersten
 */
public class MylarContextTestUtil {

	public static TestSearchResult findTestTypes(ILaunchConfiguration configuration, IProgressMonitor pm) throws CoreException {
		Set<IType> contextTestCases = MylarContextTestUtil.getTestCasesInContext();
		ITestKind testKind = TestKindRegistry.getDefault().getKind(configuration);
		// HACK: only checks first type
		if (contextTestCases.size() > 0) {
			testKind = TestKindRegistry.getDefault().getKind(contextTestCases.iterator().next());
		}
		return new TestSearchResult(contextTestCases.toArray(new IType[contextTestCases.size()]), testKind);
	}
	
	public static Set<IType> getTestCasesInContext() {
		Set<IType> testTypes = new HashSet<IType>();
		List<IMylarElement> interesting = ContextCorePlugin.getContextManager().getActiveContext().getInteresting();
		IMylarStructureBridge bridge = ContextCorePlugin.getDefault().getStructureBridge(JavaStructureBridge.CONTENT_TYPE);
		try {
			for (IMylarElement element : interesting) {
				if (element.getContentType().equals(JavaStructureBridge.CONTENT_TYPE)) {
					Object javaElement = bridge.getObjectForHandle(element.getHandleIdentifier());
					if (javaElement instanceof IType) {
						IType type = (IType) javaElement;
						if (isTestType(type)) {
							testTypes.add(type);
						}
					}
					for (IMylarRelation relation : element.getRelations()) {
						if (relation.getRelationshipHandle().equals(JUnitReferencesProvider.ID)) {
							IMylarElement target = relation.getTarget();
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
			MylarStatusHandler.fail(e, "could not add all test types", false);
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
