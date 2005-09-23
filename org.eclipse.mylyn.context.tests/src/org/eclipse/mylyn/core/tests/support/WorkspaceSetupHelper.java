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
package org.eclipse.mylar.core.tests.support;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.internal.ScalingFactors;
import org.eclipse.mylar.core.internal.MylarContext;


public class WorkspaceSetupHelper {

	private static boolean isSetup = false;
	
	private static MylarContext taskscape;
	
	private static IJavaProject project1;
	private static IJavaProject project2;
	private static TestProject jdtCoreDomProject;
    private static IWorkspaceRoot workspaceRoot;
	
    public static void clearWorkspace() throws CoreException, IOException{
    	isSetup = false;
    	ResourcesPlugin.getWorkspace().getRoot().delete(true, true, new NullProgressMonitor());
    	clearDoiModel();
    }
    
	public static IWorkspaceRoot setupWorkspace() throws CoreException, IOException{
		if(isSetup){
			clearDoiModel();
			return workspaceRoot;
		}
		taskscape = new MylarContext("1", new ScalingFactors());
		
		workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		
		project1 = ResourceHelper.createJavaPluginProjectFromZip("project1", "project1.zip");
    	project2 = ResourceHelper.createJavaPluginProjectFromZip("project2", "project2.zip");
   	
    	jdtCoreDomProject = new TestProject("workspace-helper-project");
    	IPackageFragment jdtCoreDomPkg = jdtCoreDomProject.createPackage("org.eclipse.jdt.core.dom");
        IType astNodeType = jdtCoreDomProject.createType(jdtCoreDomPkg, "ASTNode.java", "public class ASTNode { }" );
        astNodeType.createMethod("public final void setSourceRange(int startPosition, int length) { }",null, false, null);
        isSetup = true;
        
        project1.open(new NullProgressMonitor());
        project2.open(new NullProgressMonitor());
        jdtCoreDomProject.getJavaProject().open(new NullProgressMonitor());
        
		return workspaceRoot;
	}
	
	public static void clearDoiModel() throws CoreException{
		MylarPlugin.getContextManager().contextDeleted("1", "1");
		taskscape = new MylarContext("1", new ScalingFactors());
	}
	
	public static MylarContext getTaskscape()throws CoreException, IOException{
		if(!isSetup)
			setupWorkspace();
		return taskscape;
	}

	public static IJavaProject getJdtCoreDomProject() throws CoreException, IOException{
		if(!isSetup)
			setupWorkspace();
		return jdtCoreDomProject.getJavaProject();
	}

	public static IJavaProject getProject1() throws CoreException, IOException{
		if(!isSetup)
			setupWorkspace();
		return project1;
	}

	public static IJavaProject getProject2() throws CoreException, IOException{
		if(!isSetup)
			setupWorkspace();
		return project2;
	}

	public static IWorkspaceRoot getWorkspaceRoot() throws CoreException, IOException{
		if(!isSetup)
			setupWorkspace();
		return workspaceRoot;
	}
	
	public static IFile getFile(IJavaProject jp, String name) throws JavaModelException{
		if(jp == null || name == null)
			return null;
		Object[] files = jp.getNonJavaResources();
		for(Object o : files){
			if(o instanceof IFile && ((IFile)o).getName().equals(name))
				return (IFile)o;
		}
		return null;
	}
	
	public static IType getType(IJavaProject jp, String fullyQualifiedName) throws JavaModelException{
		if(jp == null || fullyQualifiedName == null)
			return null;
		IType t = jp.findType(fullyQualifiedName);
		return t;
	}
	
	public static IMethod getMethod(IType t, String methodName, String[] params){
		if(t == null || methodName == null || params == null)
			return null;
		return t.getMethod(methodName, params);
	}
}
