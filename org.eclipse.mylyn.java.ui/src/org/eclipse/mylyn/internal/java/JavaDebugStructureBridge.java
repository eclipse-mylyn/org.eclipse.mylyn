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

package org.eclipse.mylar.internal.java;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.RuntimeProcess;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.debug.core.JavaDebugUtils;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugElement;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.mylar.context.core.AbstractRelationProvider;
import org.eclipse.mylar.context.core.IDegreeOfSeparation;
import org.eclipse.mylar.context.core.IMylarStructureBridge;

/**
 * @author Mik Kersten
 */
public class JavaDebugStructureBridge implements IMylarStructureBridge {

	public final static String CONTENT_TYPE = "java/debug";
	
	private JavaStructureBridge javaStructureBridge = new JavaStructureBridge();
	
	public boolean acceptsObject(Object object) {
		return object instanceof ILaunch || object instanceof JDIDebugElement || object instanceof RuntimeProcess;
	}

	public boolean canBeLandmark(String handle) {
		return false;
	}

	public boolean canFilter(Object element) {
		return element instanceof JDIStackFrame;
	}

	public List<String> getChildHandles(String handle) {
		return null;
	}

	public String getContentType() {
		return CONTENT_TYPE;
	}

	public String getContentType(String elementHandle) {
		return getContentType();
	}

	public List<IDegreeOfSeparation> getDegreesOfSeparation() {
		return null;
	}

	public String getHandleForOffsetInObject(Object resource, int offset) {
		return null;
	}

	public String getHandleIdentifier(Object object) {
		if (object instanceof JDIStackFrame) {
			JDIStackFrame stackFrame = (JDIStackFrame)object;
			try {
				IType type = JavaDebugUtils.resolveDeclaringType(stackFrame);
				if (type != null && type.exists()) {
					return javaStructureBridge.getHandleIdentifier(type);
				}
			} catch (CoreException e) {
				// ignore
			}
		}
		return null;
	}

	public String getName(Object object) {
		return "" + object;
	}

	public Object getObjectForHandle(String handle) {
		return javaStructureBridge.getObjectForHandle(handle);	
	}

	public String getParentHandle(String handle) {
		return null;
	}

	public List<AbstractRelationProvider> getRelationshipProviders() {
		return null;
	}

	public boolean isDocument(String handle) {
		return false;
	}

	public void setParentBridge(IMylarStructureBridge bridge) {
		// ignore
	}

}
