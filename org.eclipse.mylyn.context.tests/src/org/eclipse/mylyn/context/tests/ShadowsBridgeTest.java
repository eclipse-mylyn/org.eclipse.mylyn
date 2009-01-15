/*******************************************************************************
 * Copyright (c) 2004, 2008 Andrew Eisenberg and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrew Eisenberg - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.context.tests;

import java.lang.reflect.Method;
import java.util.List;

import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;

/**
 * This tests that structure bridge shadowing occurs appropriately.
 * 
 * @author Andrew Eisenberg
 */
public class ShadowsBridgeTest extends AbstractContextTest {

	static final String BASE_CONTENT_TYPE = "BASE_CONTENT_TYPE";

	static final String SHADOWS_CONTENT_TYPE = "SHADOWS_CONTENT_TYPE";

	static final String BASE_IDENTIFIER = "BASE_IDENTIFIER";

	static final String SHADOWS_IDENTIFIER = "SHADOWS_IDENTIFIER";

	static final String BASE_LABEL = "BASE_LABEL";

	static final String SHADOWS_LABEL = "SHADOWS_LABEL";

	static final Object BASE_OBJECT = new Object();

	static final Object SHADOWS_OBJECT = new Object();

	class BaseContentStructureBridge extends AbstractContextStructureBridge {

		@Override
		public boolean acceptsObject(Object object) {
			return object == BASE_OBJECT || object == SHADOWS_OBJECT;
		}

		@Override
		public boolean canBeLandmark(String handle) {
			return false;
		}

		@Override
		public boolean canFilter(Object element) {
			return false;
		}

		@Override
		public List<String> getChildHandles(String handle) {
			return null;
		}

		@Override
		public String getContentType() {
			return BASE_CONTENT_TYPE;
		}

		@Override
		public String getContentType(String elementHandle) {
			if (elementHandle == BASE_IDENTIFIER) {
				return BASE_CONTENT_TYPE;
			} else if (elementHandle == SHADOWS_IDENTIFIER) {
				return SHADOWS_IDENTIFIER;
			} else {
				return null;
			}
		}

		@Override
		public String getHandleForOffsetInObject(Object resource, int offset) {
			if (resource == BASE_OBJECT) {
				return BASE_IDENTIFIER;
			} else if (resource == SHADOWS_OBJECT) {
				return SHADOWS_IDENTIFIER;
			} else {
				return null;
			}
		}

		@Override
		public String getHandleIdentifier(Object object) {
			if (object == BASE_OBJECT) {
				return BASE_IDENTIFIER;
			} else if (object == SHADOWS_OBJECT) {
				return SHADOWS_IDENTIFIER;
			} else {
				return null;
			}
		}

		@Override
		public String getLabel(Object object) {
			if (object == BASE_OBJECT) {
				return BASE_LABEL;
			} else if (object == SHADOWS_OBJECT) {
				return SHADOWS_LABEL;
			} else {
				return null;
			}
		}

		@Override
		public Object getObjectForHandle(String handle) {
			if (handle == BASE_IDENTIFIER) {
				return BASE_OBJECT;
			} else if (handle == SHADOWS_IDENTIFIER) {
				return SHADOWS_OBJECT;
			} else {
				return null;
			}
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

	class ShadowsContentStructureBridge extends AbstractContextStructureBridge {

		@Override
		public boolean acceptsObject(Object object) {
			return object == SHADOWS_OBJECT;
		}

		@Override
		public boolean canBeLandmark(String handle) {
			return false;
		}

		@Override
		public boolean canFilter(Object element) {
			return false;
		}

		@Override
		public List<String> getChildHandles(String handle) {
			return null;
		}

		@Override
		public String getContentType() {
			return SHADOWS_CONTENT_TYPE;
		}

		@Override
		public String getContentType(String elementHandle) {
			if (elementHandle == SHADOWS_IDENTIFIER) {
				return SHADOWS_IDENTIFIER;
			} else {
				return null;
			}
		}

		@Override
		public String getHandleForOffsetInObject(Object resource, int offset) {
			if (resource == SHADOWS_OBJECT) {
				return SHADOWS_IDENTIFIER;
			} else {
				return null;
			}
		}

		@Override
		public String getHandleIdentifier(Object object) {
			if (object == SHADOWS_OBJECT) {
				return SHADOWS_IDENTIFIER;
			} else {
				return null;
			}
		}

		@Override
		public String getLabel(Object object) {
			if (object == SHADOWS_OBJECT) {
				return SHADOWS_LABEL;
			} else {
				return null;
			}
		}

		@Override
		public Object getObjectForHandle(String handle) {
			if (handle == SHADOWS_IDENTIFIER) {
				return SHADOWS_OBJECT;
			} else {
				return null;
			}
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

	/**
	 * @throws Exception
	 */
	public void testShadowsStructureBridge() throws Exception {
		// 1) Create mock bridge 
		BaseContentStructureBridge baseBridge = new BaseContentStructureBridge();

		// 2) Add it to ContextCorePlugin
		ContextCorePlugin context = ContextCorePlugin.getDefault();
		context.addStructureBridge(baseBridge);

		// 3) Check that the bridge is found properly
		AbstractContextStructureBridge otherBridge;

		otherBridge = context.getStructureBridge(BASE_CONTENT_TYPE);
		assertEquals("Should be the same bridges: " + baseBridge + " " + otherBridge, baseBridge, otherBridge);

		otherBridge = context.getStructureBridge(BASE_OBJECT);
		assertEquals("Should be the same bridges: " + baseBridge + " " + otherBridge, baseBridge, otherBridge);

//		otherBridge = context.getStructureBridge(SHADOWS_CONTENT_TYPE);
//		assertEquals("Should be the same bridges: " + baseBridge + " " + otherBridge, baseBridge, otherBridge);

		otherBridge = context.getStructureBridge(SHADOWS_OBJECT);
		assertEquals("Should be the same bridges: " + baseBridge + " " + otherBridge, baseBridge, otherBridge);

		// 4) Create second mock bridge 
		ShadowsContentStructureBridge shadowsBridge = new ShadowsContentStructureBridge();

		// 5) Add it to ContextCorePlugin
		context.addStructureBridge(shadowsBridge);

		// 6) Add shadows relationship (must use reflection because it is not exposed)
		Method addShadowsContentMethod = ContextCorePlugin.class.getDeclaredMethod("addShadowsContent", String.class,
				String.class);
		addShadowsContentMethod.setAccessible(true);
		addShadowsContentMethod.invoke(context, BASE_CONTENT_TYPE, SHADOWS_CONTENT_TYPE);

		// 7) Ensure that the proper kinds of objects are accepted by the shadows bridge instead
		// now the base content is shadowed and should return the shadows bridge
		otherBridge = context.getStructureBridge(BASE_CONTENT_TYPE);
		assertEquals("Should be the same bridges: " + shadowsBridge + " " + otherBridge, shadowsBridge, otherBridge);

		otherBridge = context.getStructureBridge(SHADOWS_CONTENT_TYPE);
		assertEquals("Should be the same bridges: " + shadowsBridge + " " + otherBridge, shadowsBridge, otherBridge);

		otherBridge = context.getStructureBridge(SHADOWS_OBJECT);
		assertEquals("Should be the same bridges: " + shadowsBridge + " " + otherBridge, shadowsBridge, otherBridge);

		// since shadows bridge does not accept BASE_OBJECT, we should still be getting base bridge here.
		otherBridge = context.getStructureBridge(BASE_OBJECT);
		assertEquals("Should be the same bridges: " + baseBridge + " " + otherBridge, baseBridge, otherBridge);
	}
}
