/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
/*
 * Created on Oct 25, 2004
  */
package org.eclipse.mylar.java.search;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.java.JavaStructureBridge;


/**
 * @author Mik Kersten
 */
public class JUnitReferencesProvider extends AbstractJavaRelationProvider {

	public static final String ID = ID_GENERIC + ".junitreferences";
    public static final String NAME = "tested by";
        
    public JUnitReferencesProvider() {
        super(JavaStructureBridge.CONTENT_TYPE, ID);
    }  
    
    @Override
    protected boolean acceptResultElement(IJavaElement element) {
        if (element instanceof IMethod) {
            IMethod method = (IMethod)element;
            boolean isTestMethod = false;
            boolean isTestCase = false;
            if (method.getElementName().startsWith("test")) isTestMethod = true;
            
            IJavaElement parent = method.getParent();
            if (parent instanceof IType) {
                IType type = (IType)parent;
                ITypeHierarchy hierarchy;
                try {
                    hierarchy = type.newSupertypeHierarchy(null);
                    IType[] supertypes = hierarchy.getAllSuperclasses(type);
                    for (int i = 0; i < supertypes.length; i++) {
                        if (supertypes[i].getFullyQualifiedName().equals("junit.framework.TestCase")) {
                            isTestCase = true;
                        }
                    }
                } catch (JavaModelException e) {
                	MylarPlugin.log(e, "could not accept results");
                }
            }
            return isTestMethod && isTestCase;
        }
        return false;
    }
    
    @Override
    protected String getSourceId() {
        return ID;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
