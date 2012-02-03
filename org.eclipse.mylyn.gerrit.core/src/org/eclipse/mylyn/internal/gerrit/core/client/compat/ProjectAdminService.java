/*******************************************************************************
 * Copyright (c) 2012 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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
