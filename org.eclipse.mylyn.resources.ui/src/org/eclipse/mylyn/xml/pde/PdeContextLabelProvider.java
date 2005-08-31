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

package org.eclipse.mylar.xml.pde;

import org.eclipse.mylar.core.IMylarContextEdge;
import org.eclipse.mylar.core.IMylarContextNode;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ui.AbstractContextLabelProvider;
import org.eclipse.mylar.ui.MylarImages;
import org.eclipse.mylar.xml.XmlReferencesProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 */
public class PdeContextLabelProvider extends AbstractContextLabelProvider {

	@Override
	protected Image getImage(IMylarContextNode node) {
		return MylarImages.getImage(MylarImages.FILE_XML); 
	}

	@Override
	protected Image getImage(IMylarContextEdge edge) {
		return MylarImages.getImage(MylarImages.EDGE_REF_XML);
	}

	@Override
	protected String getText(IMylarContextNode node) {
        IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(PdeStructureBridge.EXTENSION);
		String name = bridge.getName(bridge.getObjectForHandle(node.getElementHandle()));
        return name;
	}

	@Override
	protected String getText(IMylarContextEdge edge) {
		return XmlReferencesProvider.NAME;
	}
} 
