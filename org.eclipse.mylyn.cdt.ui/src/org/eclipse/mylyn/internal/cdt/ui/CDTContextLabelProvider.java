/*******************************************************************************
 * Copyright (c) 2004, 2008 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * Created on Aug 6, 2004
 * Modified for CDT usage Feb 28, 2008
 */
package org.eclipse.mylyn.internal.cdt.ui;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.internal.ui.viewsupport.AppearanceAwareLabelProvider;
import org.eclipse.cdt.internal.ui.viewsupport.CElementImageProvider;
import org.eclipse.cdt.internal.ui.viewsupport.ProblemsLabelDecorator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.core.IInteractionRelation;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 * @author Jeff Johnston
 * @author Shawn Minto
 */
public class CDTContextLabelProvider extends AppearanceAwareLabelProvider {

	public CDTContextLabelProvider() {
		super(AppearanceAwareLabelProvider.DEFAULT_TEXTFLAGS, AppearanceAwareLabelProvider.DEFAULT_IMAGEFLAGS
				| CElementImageProvider.SMALL_ICONS);
	}

	@Override
	public String getText(Object object) {
		if (object instanceof IInteractionElement) {
			IInteractionElement node = (IInteractionElement) object;
			if (CDTStructureBridge.CONTENT_TYPE.equals(node.getContentType())) {
				ICElement element = CDTStructureBridge.getElementForHandle(node.getHandleIdentifier());
				if (element == null) {
					return Messages.CDTContextLabelProvider_Missing_Element;
				} else {
					return getTextForElement(element);
				}
			}
		} else if (object instanceof IInteractionRelation) {
			return getNameForRelationship(((IInteractionRelation) object).getRelationshipHandle());
		} else if (object instanceof ICElement) {
			return getTextForElement((ICElement) object);
		}
		return super.getText(object);
	}

	private String getTextForElement(ICElement element) {
		if (element.exists()) {
			return super.getText(element);
		} else {
			return Messages.CDTContextLabelProvider_Missing_Element;
		}
	}

	@Override
	public Image getImage(Object object) {
		if (object instanceof IInteractionElement) {
			IInteractionElement node = (IInteractionElement) object;
			if (node.getContentType().equals(CDTStructureBridge.CONTENT_TYPE)) {
				ICElement element = CDTStructureBridge.getElementForHandle(node.getHandleIdentifier());
				if (element != null) {
					return super.getImage(element);
				}
				return null;
			}
		} else if (object instanceof IInteractionRelation) {
			ImageDescriptor descriptor = getIconForRelationship(((IInteractionRelation) object).getRelationshipHandle());
			if (descriptor != null) {
				return CommonImages.getImage(descriptor);
			} else {
				return null;
			}
		}
		return super.getImage(object);
	}

	private ImageDescriptor getIconForRelationship(String relationshipHandle) {
		// We have no relation providers for the CDT
		return null;
	}

	@SuppressWarnings("restriction")
	private String getNameForRelationship(String relationshipHandle) {
		if (relationshipHandle.equals(InteractionContextManager.CONTAINMENT_PROPAGATION_ID)) {
			return Messages.CDTContextLabelProvider_Containment;
		} else {
			return null;
		}
	}

	public static AppearanceAwareLabelProvider createCDTUiLabelProvider() {
		AppearanceAwareLabelProvider cdtUiLabelProvider = new AppearanceAwareLabelProvider(
				AppearanceAwareLabelProvider.DEFAULT_TEXTFLAGS, AppearanceAwareLabelProvider.DEFAULT_IMAGEFLAGS
						| CElementImageProvider.SMALL_ICONS);
		cdtUiLabelProvider.addLabelDecorator(new ProblemsLabelDecorator(null));
		return cdtUiLabelProvider;
	}
}
