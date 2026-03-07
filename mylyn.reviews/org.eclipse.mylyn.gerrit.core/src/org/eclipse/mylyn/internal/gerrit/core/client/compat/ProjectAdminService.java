/*******************************************************************************
 * Copyright (c) 2012 SAP and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *      Sascha Scholz (SAP) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client.compat;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Sascha Scholz
 */
public interface ProjectAdminService extends com.google.gerrit.common.data.ProjectAdminService {

	void visibleProjectDetails(AsyncCallback<List<ProjectDetailX>> callback);

}
