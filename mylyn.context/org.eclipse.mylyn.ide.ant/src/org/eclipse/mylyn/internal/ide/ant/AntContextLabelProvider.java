/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.ide.ant;

import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.core.IInteractionRelation;
import org.eclipse.mylyn.internal.context.ui.AbstractContextLabelProvider;
import org.eclipse.mylyn.internal.context.ui.ContextUiImages;
import org.eclipse.mylyn.internal.ide.ui.IdeUiBridgePlugin;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 */
@SuppressWarnings("restriction")
public class AntContextLabelProvider extends AbstractContextLabelProvider {

	public static final String LABEL_RELATION = Messages.AntContextLabelProvider_referenced_by;

	@Override
	protected Image getImage(IInteractionElement node) {
		return CommonImages.getImage(ContextUiImages.FILE_XML);
	}

	@Override
	protected Image getImage(IInteractionRelation edge) {
		return CommonImages.getImage(IdeUiBridgePlugin.EDGE_REF_XML);
	}

	@Override
	protected String getText(IInteractionElement node) {
		AbstractContextStructureBridge bridge = ContextCore.getStructureBridge(AntStructureBridge.CONTENT_TYPE);
		return bridge.getLabel(bridge.getObjectForHandle(node.getHandleIdentifier()));
	}

	@Override
	protected String getText(IInteractionRelation edge) {
		return LABEL_RELATION;
	}

	@Override
	protected Image getImageForObject(Object object) {
		return CommonImages.getImage(ContextUiImages.FILE_XML);
	}

	@Override
	protected String getTextForObject(Object object) {
		AbstractContextStructureBridge bridge = ContextCore.getStructureBridge(AntStructureBridge.CONTENT_TYPE);
		return bridge.getLabel(object);
	}

}
