/*******************************************************************************
 * Copyright (c) 2010 Markus Knittig and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Knittig - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.core.operations;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.util.ProgressUtil;
import org.eclipse.mylyn.internal.builds.core.BuildsCorePlugin;
import org.eclipse.osgi.util.NLS;

/**
 * @author Markus Knittig
 */
public class RunBuildOperation extends AbstractBuildOperation {

	private final IBuildPlan plan;

	public RunBuildOperation(IBuildPlan plan) {
		super("Running build");
		Assert.isNotNull(plan);
		this.plan = plan;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		MultiStatus result = new MultiStatus(BuildsCorePlugin.ID_PLUGIN, 0, "Running of build failed", null);
		try {
			return plan.run(ProgressUtil.convert(monitor));
		} catch (CoreException e) {
			result.add((new Status(IStatus.ERROR, BuildsCorePlugin.ID_PLUGIN, NLS.bind("Run of build ''{0}'' failed",
					plan.getName(), e))));
		} catch (OperationCanceledException e) {
			return Status.CANCEL_STATUS;
		}
		setStatus(result);
		return Status.OK_STATUS;
	}
}
