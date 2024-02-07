/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     IBM Corporation - initial API and implementation
 *     Manuel Doninger - fixes for bug 376271
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.versions.core;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.XmlMemento;
import org.eclipse.mylyn.internal.versions.core.ScmInternal;
import org.eclipse.team.core.ProjectSetCapability;
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.team.core.RepositoryProviderType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Provides support for importing and exporting team project sets.
 * 
 * @author Manuel Doninger
 */
public class ProjectSetConverter {

	/**
	 * Returns a team project set for <code>projects</code>.
	 * 
	 * @param projects
	 *            the projects to include
	 * @return an XML document
	 * @throws CoreException
	 *             indicates that the team project set could not be created
	 */
	public static ByteArrayOutputStream exportProjectSet(List<IProject> projects) throws CoreException {
		Map<String, Set<IProject>> map = new HashMap<>();
		for (IProject project : projects) {
			RepositoryProvider provider = RepositoryProvider.getProvider(project);
			if (provider != null) {
				String id = provider.getID();
				Set<IProject> list = map.get(id);
				if (list == null) {
					list = new TreeSet<>((o1, o2) -> o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase()));
					map.put(id, list);
				}
				list.add(project);
			}
		}

		BufferedWriter writer = null;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			writer = new BufferedWriter(new OutputStreamWriter(output, "UTF-8")); //$NON-NLS-1$

			XmlMemento xmlMemento = getXMLMementoRoot();
			for (String id : map.keySet()) {
				XmlMemento memento = xmlMemento.createChild("provider"); //$NON-NLS-1$
				memento.putString("id", id); //$NON-NLS-1$
				Set<IProject> list = map.get(id);
				IProject[] projectArray = list.toArray(new IProject[list.size()]);
				RepositoryProviderType providerType = RepositoryProviderType.getProviderType(id);
				ProjectSetCapability serializer = providerType.getProjectSetCapability();
				ProjectSetCapability.ensureBackwardsCompatible(providerType, serializer);
				if (serializer != null) {
					String[] references = serializer.asReference(projectArray, null, null);
					for (String reference : references) {
						XmlMemento proj = memento.createChild("project"); //$NON-NLS-1$
						proj.putString("reference", reference); //$NON-NLS-1$
					}
				}
			}

			xmlMemento.save(writer);
			return output;
		} catch (Exception e) {
			throw new CoreException(
					new Status(IStatus.ERROR, ScmInternal.ID_PLUGIN, "Unexpected error exporting project sets.", e)); //$NON-NLS-1$
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}

	/**
	 * Returns a list of project names referenced in the team project set <code>input</code> for the provider <code>prooviderType</code>.
	 * 
	 * @throws CoreException
	 */
	public static List<String> readProjectReferences(InputStream input, RepositoryProviderType providerType)
			throws CoreException {
		try {
			List<String> referenceStrings = new ArrayList<>();
			XmlMemento[] providers = importProjectSet(input);
			for (XmlMemento provider : providers) {
				if (provider.getString("id").equals(providerType.getID())) { //$NON-NLS-1$
					XmlMemento[] projects = provider.getChildren("project"); //$NON-NLS-1$
					for (XmlMemento project : projects) {
						referenceStrings.add(project.getString("reference")); //$NON-NLS-1$
					}
				}
			}
			return referenceStrings;
		} catch (Exception e) {
			throw new CoreException(
					new Status(IStatus.ERROR, ScmInternal.ID_PLUGIN, "Unexpected error reading project sets.", e)); //$NON-NLS-1$
		}
	}

	private static XmlMemento getXMLMementoRoot() throws ParserConfigurationException {
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element element = document.createElement("psf"); //$NON-NLS-1$
		element.setAttribute("version", "2.0"); //$NON-NLS-1$ //$NON-NLS-2$
		document.appendChild(element);
		return new XmlMemento(document, element);
	}

	private static XmlMemento[] importProjectSet(InputStream input)
			throws UnsupportedEncodingException, InvocationTargetException {
		XmlMemento xmlMemento = parseStream(input);
		return xmlMemento.getChildren("provider"); //$NON-NLS-1$
	}

	private static XmlMemento parseStream(InputStream input)
			throws InvocationTargetException, UnsupportedEncodingException {
		try (InputStreamReader reader = new InputStreamReader(input, "UTF-8")) { //$NON-NLS-1$
			return XmlMemento.createReadRoot(reader);
		} catch (IOException e) {
			throw new InvocationTargetException(e);
		}
	}

}
