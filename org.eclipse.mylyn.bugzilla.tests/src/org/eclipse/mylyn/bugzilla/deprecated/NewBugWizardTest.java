/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Ian Bull - improvements
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.deprecated;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Mik Kersten
 * @author Ian Bull
 */
public class NewBugWizardTest extends TestCase {

	public void testPlatformOptions() throws Exception {

		TaskRepository repository = new TaskRepository(BugzillaCorePlugin.CONNECTOR_KIND,
				IBugzillaConstants.TEST_BUGZILLA_220_URL);
		AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				repository.getConnectorKind());

		final TaskAttributeMapper attributeMapper = connector.getTaskDataHandler().getAttributeMapper(repository);

		final TaskData taskData = new TaskData(attributeMapper, BugzillaCorePlugin.CONNECTOR_KIND,
				repository.getRepositoryUrl(), "1");

		assertNotNull(connector);
		AbstractTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
		assertNotNull(taskDataHandler);

		BugzillaCorePlugin.getDefault().setPlatformOptions(taskData);

		String os = Platform.getOS();
		if (os.equals("win32")) {
			assertEquals("Windows", taskData.getRoot().getAttribute(BugzillaAttribute.OP_SYS.getKey()).getValue());
		} else if (os.equals("solaris")) {
			assertEquals("Solaris", taskData.getRoot().getAttribute(BugzillaAttribute.OP_SYS.getKey()).getValue());
		} else if (os.equals("qnx")) {
			assertEquals("QNX-Photon", taskData.getRoot().getAttribute(BugzillaAttribute.OP_SYS.getKey()).getValue());
		} else if (os.equals("macosx")) {
			assertEquals("Mac OS", taskData.getRoot().getAttribute(BugzillaAttribute.OP_SYS.getKey()).getValue());
		} else if (os.equals("linux")) {
			assertEquals("Linux", taskData.getRoot().getAttribute(BugzillaAttribute.OP_SYS.getKey()).getValue());
		} else if (os.equals("hpux")) {
			assertEquals("HP-UX", taskData.getRoot().getAttribute(BugzillaAttribute.OP_SYS.getKey()).getValue());
		} else if (os.equals("aix")) {
			assertEquals("AIX", taskData.getRoot().getAttribute(BugzillaAttribute.OP_SYS.getKey()).getValue());
		}

		String platform = Platform.getOSArch();
		if (platform.equals("x86")) {
			if (os.equals("macosx")) {
				assertEquals("Macintosh", taskData.getRoot()
						.getAttribute(BugzillaAttribute.REP_PLATFORM.getKey())
						.getValue());
			} else {
				assertEquals("PC", taskData.getRoot().getAttribute(BugzillaAttribute.REP_PLATFORM.getKey()).getValue());
			}
		} else if (platform.equals("x86_64")) {
			assertEquals("PC", taskData.getRoot().getAttribute(BugzillaAttribute.REP_PLATFORM.getKey()).getValue());
		} else if (platform.equals("ia64")) {
			assertEquals("PC", taskData.getRoot().getAttribute(BugzillaAttribute.REP_PLATFORM.getKey()).getValue());
		} else if (platform.equals("ia64_32")) {
			assertEquals("PC", taskData.getRoot().getAttribute(BugzillaAttribute.REP_PLATFORM.getKey()).getValue());
		} else if (platform.equals("sparc")) {
			assertEquals("Sun", taskData.getRoot().getAttribute(BugzillaAttribute.REP_PLATFORM.getKey()).getValue());
		} else if (platform.equals("ppc")) {
			if (os.equals("macosx")) {
				assertEquals("Macintosh", taskData.getRoot()
						.getAttribute(BugzillaAttribute.REP_PLATFORM.getKey())
						.getValue());
			} else {
				assertEquals("Power", taskData.getRoot()
						.getAttribute(BugzillaAttribute.REP_PLATFORM.getKey())
						.getValue());
			}
		}
	}
}
