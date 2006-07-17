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

package org.eclipse.mylar.internal.ide.xml.pde;

import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.context.core.IMylarElement;
import org.eclipse.mylar.context.core.IMylarRelation;
import org.eclipse.mylar.context.core.IMylarStructureBridge;
import org.eclipse.mylar.internal.context.ui.AbstractContextLabelProvider;
import org.eclipse.mylar.internal.context.ui.MylarImages;
import org.eclipse.mylar.internal.ide.MylarIdePlugin;
import org.eclipse.mylar.internal.ide.xml.XmlReferencesProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 */
public class PdeContextLabelProvider extends AbstractContextLabelProvider {

	@Override
	protected Image getImage(IMylarElement node) {
		return MylarImages.getImage(MylarImages.FILE_XML);
	}

	@Override
	protected Image getImage(IMylarRelation edge) {
		return MylarImages.getImage(MylarIdePlugin.EDGE_REF_XML);
	}

	@Override
	protected String getText(IMylarElement node) {
		IMylarStructureBridge bridge = ContextCorePlugin.getDefault().getStructureBridge(PdeStructureBridge.CONTENT_TYPE);
		return bridge.getName(bridge.getObjectForHandle(node.getHandleIdentifier()));
	}

	@Override
	protected String getText(IMylarRelation edge) {
		return XmlReferencesProvider.NAME;
	}

	@Override
	protected Image getImageForObject(Object object) {
		return MylarImages.getImage(MylarImages.FILE_XML);
	}

	@Override
	protected String getTextForObject(Object object) {
		IMylarStructureBridge bridge = ContextCorePlugin.getDefault().getStructureBridge(PdeStructureBridge.CONTENT_TYPE);
		return bridge.getName(object);
	}
}
