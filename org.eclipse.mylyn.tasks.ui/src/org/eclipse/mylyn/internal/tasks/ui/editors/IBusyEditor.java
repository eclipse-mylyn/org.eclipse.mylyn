/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.swt.graphics.Image;

/**
 * @author Shawn Minto
 * @deprecated use {@link org.eclipse.mylyn.commons.workbench.BusyAnimator.IBusyClient} instead
 */
@Deprecated
public interface IBusyEditor {

	public void setTitleImage(Image image);

	public Image getTitleImage();

}
