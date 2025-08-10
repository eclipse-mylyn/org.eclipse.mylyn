/*******************************************************************************
 * Copyright (c) 2009, 2011 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.ui.dialogs;

/**
 * Listener interface for close events from an {@link AbstractInPlaceDialog}
 *
 * @author Shawn Minto
 * @since 3.7
 */
public interface IInPlaceDialogListener {

	void buttonPressed(InPlaceDialogEvent event);

}