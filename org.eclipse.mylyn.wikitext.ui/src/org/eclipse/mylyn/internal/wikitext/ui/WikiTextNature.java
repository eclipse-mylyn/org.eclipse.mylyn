/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.internal.wikitext.ui.validation.ValidationProjectBuilder;

/**
 * A WikiText nature which configures a project to have the WikiText validation builder.
 * 
 * @author David Green
 */
public class WikiTextNature implements IProjectNature {

	public static final String ID = "org.eclipse.mylyn.wikitext.ui.wikiTextNature"; //$NON-NLS-1$

	private IProject project;

	/**
	 * install the WikiText nature on a project
	 */
	public static void install(IProject project, IProgressMonitor monitor) throws CoreException {
		IProjectDescription description = project.getDescription();
		String[] natures = description.getNatureIds();
		String[] newNatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length] = ID;
		IStatus status = ResourcesPlugin.getWorkspace().validateNatureSet(natures);

		// check the status and decide what to do
		if (status.getCode() == IStatus.OK) {
			description.setNatureIds(newNatures);
			project.setDescription(description, monitor);
		} else {
			throw new CoreException(WikiTextUiPlugin.getDefault().createStatus(
					Messages.WikiTextNature_cannotValidateNatureSet, IStatus.ERROR, null));
		}
	}

	public static void uninstall(IProject project, IProgressMonitor monitor) throws CoreException {
		IProjectDescription description = project.getDescription();
		String[] natures = description.getNatureIds();
		List<String> newNatures = new ArrayList<String>(natures.length);
		for (String n : natures) {
			if (!n.equals(ID)) {
				newNatures.add(n);
			}
		}
		if (newNatures.size() != natures.length) {
			natures = newNatures.toArray(new String[newNatures.size()]);
			IStatus status = ResourcesPlugin.getWorkspace().validateNatureSet(natures);

			// check the status and decide what to do
			if (status.getCode() == IStatus.OK) {
				description.setNatureIds(natures);
				project.setDescription(description, monitor);
			} else {
				throw new CoreException(WikiTextUiPlugin.getDefault().createStatus(
						Messages.WikiTextNature_cannotValidateNatureSet, IStatus.ERROR, null));
			}

		}
	}

	public void configure() throws CoreException {
		// add the WikiText validation builder
		IProjectDescription desc = project.getDescription();
		ICommand[] commands = desc.getBuildSpec();
		boolean found = false;

		for (ICommand command : commands) {
			if (command.getBuilderName().equals(ValidationProjectBuilder.ID)) {
				found = true;
				break;
			}
		}
		if (!found) {
			//add builder to project
			ICommand command = desc.newCommand();
			command.setBuilderName(ValidationProjectBuilder.ID);
			ICommand[] newCommands = new ICommand[commands.length + 1];

			// Add it before other builders.
			System.arraycopy(commands, 0, newCommands, 1, commands.length);
			newCommands[0] = command;
			desc.setBuildSpec(newCommands);
			project.setDescription(desc, null);
		}
	}

	public void deconfigure() throws CoreException {
		// remove the WikiText validation builder
		IProjectDescription desc = project.getDescription();
		ICommand[] commands = desc.getBuildSpec();

		List<ICommand> newCommands = new ArrayList<ICommand>(commands.length);
		for (ICommand command : commands) {
			if (!command.getBuilderName().equals(ValidationProjectBuilder.ID)) {
				newCommands.add(command);
			}
		}
		if (newCommands.size() == commands.length - 1) {
			desc.setBuildSpec(newCommands.toArray(new ICommand[newCommands.size()]));
			project.setDescription(desc, null);
		}
	}

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

}
