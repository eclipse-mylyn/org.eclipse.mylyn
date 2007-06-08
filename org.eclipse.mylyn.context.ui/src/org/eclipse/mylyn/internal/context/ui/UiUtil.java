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

package org.eclipse.mylyn.internal.context.ui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * @author Mik Kersten
 */
public class UiUtil {

	public static void displayInterestManipulationFailure() {
		MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Mylar Interest Manipulation",
				"Not a valid landmark, select an element within this resource instead.");
	}

	public static Color getBackgroundForElement(IInteractionElement node) {
		return getBackgroundForElement(node, false);
	}

	public static Color getBackgroundForElement(IInteractionElement node, boolean resolveContextColor) {
		if (node == null) {
			return null;
		} else if (!resolveContextColor && (node.getInterest().isPropagated() || node.getInterest().isPredicted())) {
			return null;
		} else if (node.getInterest().getEncodedValue() <= InteractionContextManager.getScalingFactors().getInteresting()) {
			return null;
		}

		boolean isMultiple = false;
		String contextId = ContextCorePlugin.getContextManager().getDominantContextHandleForElement(node);

		if (contextId != null) {
			Highlighter highlighter = ContextUiPlugin.getDefault().getHighlighterForContextId(contextId);
			if (highlighter == null) {
				return null;
			} else if (ContextUiPlugin.getDefault().isIntersectionMode()) {
				if (isMultiple) {
					return ContextUiPlugin.getDefault().getIntersectionHighlighter().getHighlightColor();
				} else {
					return null;
				}
			} else {
				return highlighter.getHighlight(node, false);
			}
		} else {
			return ColorMap.BACKGROUND_COLOR;
		}
	}

	public static Color getForegroundForElement(IInteractionElement node) {
		if (node == null)
			return null;
		if (node.getInterest().isPredicted() || node.getInterest().isPropagated()) {
			if (node.getInterest().getValue() >= InteractionContextManager.getScalingFactors().getLandmark() / 3) {
				return ColorMap.GRAY_DARK;
			} else if (node.getInterest().getValue() >= 10) {
				return ColorMap.GRAY_MEDIUM;
			} else {
				return ColorMap.GRAY_LIGHT;
			}
		} else if (node.getInterest().isLandmark()) {
			return ColorMap.LANDMARK;
		} else if (node.getInterest().isInteresting()) {
			return null;
		}
		return ColorMap.GRAY_MEDIUM;
	}
}

// if (node instanceof CompositeContextElement) {
// CompositeContextElement compositeNode = (CompositeContextElement)node;
// if (compositeNode.getNodes().isEmpty()) return null;
// dominantNode = (IMylarElement)compositeNode.getNodes().toArray()[0];
// if (compositeNode.getNodes().size() > 1) isMultiple = true;
//            
// for(IMylarElement concreteNode : compositeNode.getNodes()) {
// if (dominantNode != null
// && dominantNode.getDegreeOfInterest().getValue() <
// concreteNode.getDegreeOfInterest().getValue()) {
// dominantNode = concreteNode;
// }
// }
// } else if (node instanceof MylarContextElement) {
// dominantNode = node;
// }
// List<Highlighter> highlighters = new ArrayList<Highlighter>();
// for (Iterator<IDegreeOfInterest> it =
// compositeDoiInfo.getComposite().getInfos().iterator(); it.hasNext();) {
// IDegreeOfInterest specificInfo = it.next();
// Taskscape taskscape = specificInfo.getCorrespondingTaskscape();
// Highlighter highlighter =
// MylarUiPlugin.getDefault().getHighlighterForTaskId(taskscape.getId());
// if (highlighter != null) highlighters.add(highlighter);
// }
// if (highlighters.size() == 0) {
// return MylarUiPlugin.getDefault().getColorMap().BACKGROUND_COLOR;
// } else if (highlighters.size() == 1) {
// return highlighters.get(0).getHighlight(info, false);
// } else {
// return Highlighter.blend(highlighters, info, false);
// }
