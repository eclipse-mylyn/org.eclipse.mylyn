/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client.compat;

import com.google.gerrit.common.data.ChangeDetail;
import com.google.gerrit.reviewdb.PatchSet;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwtjsonrpc.client.VoidResult;

/**
 * @author Steffen Pingel
 */
public interface ChangeManageService extends com.google.gerrit.common.data.ChangeManageService {

	/**
	 * Available since Gerrit 2.3.
	 */
	void publish(PatchSet.Id patchSetId, AsyncCallback<ChangeDetail> callback);

	/**
	 * Available since Gerrit 2.2.
	 */
	void revertChange(PatchSet.Id patchSetId, String message, AsyncCallback<ChangeDetail> callback);

	/**
	 * Available since Gerrit 2.4.
	 */
	void rebaseChange(PatchSet.Id patchSetId, AsyncCallback<ChangeDetail> callback);

	/**
	 * Available since Gerrit 2.3.
	 */
	void deleteDraftChange(PatchSet.Id patchSetId, AsyncCallback<VoidResult> callback);

}
