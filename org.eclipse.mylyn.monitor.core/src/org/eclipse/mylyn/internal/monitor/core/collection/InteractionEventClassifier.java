/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.monitor.core.collection;

import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * Test whether an InteractionEvent meets particular criteria
 * 
 * @author Gail Murphy and Mik Kersten
 */
public class InteractionEventClassifier {

	/**
	 * isEdit currently classifies selections in editor as edits. May need to split off a different version
	 */
	public static boolean isEdit(InteractionEvent event) {
		return event.getKind().equals(InteractionEvent.Kind.EDIT)
				|| (event.getKind().equals(InteractionEvent.Kind.SELECTION) && isSelectionInEditor(event));
	}

	public static boolean isSelection(InteractionEvent event) {
		return event.getKind().equals(InteractionEvent.Kind.SELECTION) && !isSelectionInEditor(event);
	}

	public static boolean isCommand(InteractionEvent event) {
		return event.getKind().equals(InteractionEvent.Kind.COMMAND);
	}

	public static boolean isJavaEdit(InteractionEvent event) {
		return event.getKind().equals(InteractionEvent.Kind.EDIT)
				&& (event.getOriginId().contains("java") || event.getOriginId().contains("jdt.ui"));
	}

	public static boolean isJDTEvent(InteractionEvent event) {
		return (isEdit(event) || isSelection(event) || isCommand(event)) && getCleanOriginId(event).contains("jdt");
	}

	public static boolean isSelectionInEditor(InteractionEvent event) {
		return event.getOriginId().contains("Editor") || event.getOriginId().contains("editor")
				|| event.getOriginId().contains("source");
	}

	public static String getCleanOriginId(InteractionEvent event) {
		String cleanOriginId = "";
		String originId = event.getOriginId();

		if (event.getKind().equals(InteractionEvent.Kind.COMMAND)) {
			for (int i = 0; i < originId.length(); i++) {
				char curChar = originId.charAt(i);
				if (!(curChar == '&')) {
					if (Character.getType(curChar) == Character.CONTROL) {
						cleanOriginId = cleanOriginId.concat(" ");
					} else {
						cleanOriginId = cleanOriginId.concat(String.valueOf(curChar));
					}
				}
			}
			return cleanOriginId;
		} else {
			return originId;
		}
	}

	public static String formatDuration(long timeToFormatInms) {
		long timeInSeconds = timeToFormatInms / 1000;
		long hours, minutes;
		hours = timeInSeconds / 3600;
		timeInSeconds = timeInSeconds - (hours * 3600);
		minutes = timeInSeconds / 60;
		timeInSeconds = timeInSeconds - (minutes * 60);
		return hours + "." + minutes;
	}

}
