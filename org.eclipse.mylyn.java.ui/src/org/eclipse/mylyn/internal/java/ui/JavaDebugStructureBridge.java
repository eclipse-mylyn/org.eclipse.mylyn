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

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.RuntimeProcess;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.debug.core.JavaDebugUtils;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugElement;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionElement;

/**
 * @author Mik Kersten
 */
public class JavaDebugStructureBridge extends AbstractContextStructureBridge {

	public final static String CONTENT_TYPE = "java/debug";

	private JavaStructureBridge javaStructureBridge = new JavaStructureBridge();

	/**
	 * Needed due to slowness in resolving type names. We expect the stack frame
	 * elements to disappear, they are never explicitly removed.
	 * 
	 * TODO: consider clearing on each re-launch
	 */
	private Map<JDIStackFrame, IType> stackFrameMap = new WeakHashMap<JDIStackFrame, IType>();

	@Override
	public boolean acceptsObject(Object object) {
		return object instanceof ILaunch || object instanceof JDIDebugElement || object instanceof RuntimeProcess;
	}

	@Override
	public boolean canBeLandmark(String handle) {
		return false;
	}

	@Override
	public boolean canFilter(Object element) {
//		return element instanceof JDIStackFrame;
		if (element instanceof JDIStackFrame) {
			JDIStackFrame stackFrame = (JDIStackFrame) element;
			try {
				IStackFrame[] frames = stackFrame.getThread().getStackFrames();
				
				int indexOfInterestingFrame = 0;
				int indexOfCurrentFrame = 0;
				for (int i = 0; i < frames.length; i++) {
					IStackFrame frame = frames[i];
					if (stackFrame.getName().equals(frame.getName())) {
						indexOfCurrentFrame = i;
					}
					
					IInteractionElement correspondingElement = ContextCorePlugin.getContextManager().getElement(getHandleIdentifier(frame));
					if (correspondingElement != null && correspondingElement.getInterest().isInteresting()) {
						indexOfInterestingFrame = i;
					}
				} 
				return indexOfCurrentFrame > indexOfInterestingFrame;
			} catch (DebugException e) {
				return false;
			}
		} 
		return element instanceof JDIStackFrame;
	}

	@Override
	public List<String> getChildHandles(String handle) {
		return null;
	}

	@Override
	public String getContentType() {
		return CONTENT_TYPE;
	}

	@Override
	public String getContentType(String elementHandle) {
		return getContentType();
	}

	@Override
	public String getHandleForOffsetInObject(Object resource, int offset) {
		return null;
	}

	@Override
	public String getHandleIdentifier(Object object) {
		if (object instanceof JDIStackFrame) {
			JDIStackFrame stackFrame = (JDIStackFrame) object;
			IType type = null;
			if (stackFrameMap.containsKey(stackFrame)) {
				type = stackFrameMap.get(stackFrame);
			} else {
				try {
					type = JavaDebugUtils.resolveDeclaringType(stackFrame);
					stackFrameMap.put(stackFrame, type);
				} catch (CoreException e) {
					// ignore
				}
			}
			if (type != null && type.exists()) {
				return javaStructureBridge.getHandleIdentifier(type);
			}
		}
		return null;
	}

	@Override
	public String getLabel(Object object) {
		return "" + object;
	}

	@Override
	public Object getObjectForHandle(String handle) {
		return javaStructureBridge.getObjectForHandle(handle);
	}

	@Override
	public String getParentHandle(String handle) {
		return null;
	}

	@Override
	public boolean isDocument(String handle) {
		return false;
	}

}
