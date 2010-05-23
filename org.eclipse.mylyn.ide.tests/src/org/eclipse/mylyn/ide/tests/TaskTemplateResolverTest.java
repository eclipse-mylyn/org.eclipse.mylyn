/*******************************************************************************
 * Copyright (c) 2010 Andreas Hoehmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andreas Hoehmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.ide.tests;

import java.util.Iterator;

import junit.framework.TestCase;

import org.eclipse.jdt.internal.corext.template.java.JavaContextType;
import org.eclipse.jdt.internal.corext.template.java.JavaDocContextType;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateException;
import org.eclipse.jface.text.templates.TemplateTranslator;
import org.eclipse.jface.text.templates.TemplateVariable;
import org.eclipse.jface.text.templates.TemplateVariableResolver;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.tests.connector.MockTask;
import org.eclipse.osgi.util.NLS;

/**
 * @author Andreas Hoehmann
 */
public class TaskTemplateResolverTest extends TestCase {

	/**
	 * Test with no active task. The resolver should not be able to resolve the mylyn template-variable.
	 */
	public void testNoTaskActive() throws Exception {
		canHandleTemplateResolver("${activeTaskKey}", "activeTaskKey");
		canHandleTemplateResolver("${activeTaskPrefix}", "activeTaskPrefix");
	}

	/**
	 * Test with active task. The resolver must resolve the mylyn template-variable to the expected values.
	 */
	public void testActiveLocalTask() throws Exception {
		ITask task = new LocalTask("12345", "Test Task");
		TasksUiPlugin.getTaskActivityManager().activateTask(task);
		canHandleTemplateResolver("${activeTaskKey}", "12345");
		canHandleTemplateResolver("${activeTaskPrefix}", "task");
		canHandleTemplateResolver("${activeTaskPrefix} ${activeTaskKey}", "task 12345");
		canHandleTemplateResolver("${activeTaskPrefix} #${activeTaskKey}", "task #12345");
	}

	/**
	 * Test with active task. Special check if task-key contains a "-".
	 */
	public void testActiveTaskJira() throws Exception {
		ITask task = new MockTask("http://foo.bar", "12345");
		task.setTaskKey("DEMO-2");
		TasksUiPlugin.getTaskActivityManager().activateTask(task);
		canHandleTemplateResolver("${activeTaskKey}", "DEMO-2");
		canHandleTemplateResolver("${activeTaskPrefix} ${activeTaskKey}", "task DEMO-2");
	}

	/**
	 * Test with active task. Check if fallback from task-key to task-id is working.
	 */
	public void testActiveTaskIdOrKey() throws Exception {
		ITask task = new MockTask("http://foo.bar", "12345");
		TasksUiPlugin.getTaskActivityManager().activateTask(task);
		canHandleTemplateResolver("${activeTaskKey}", "12345");
		canHandleTemplateResolver("${activeTaskPrefix}", "task");
		// from now the task have a key ... the resolve will prefer this key
		task.setTaskKey("foobar");
		canHandleTemplateResolver("${activeTaskKey}", "foobar");
	}

	private void canHandleTemplateResolver(String templateContent, String expectedResolvedTemplate)
			throws TemplateException, BadLocationException {
		// check java template context
		canHandleTemplateResolver(JavaContextType.ID_ALL, templateContent, expectedResolvedTemplate);
		// check javadoc template context
		canHandleTemplateResolver(JavaDocContextType.ID, templateContent, expectedResolvedTemplate);
	}

	private void canHandleTemplateResolver(final String contextType, final String templateContent,
			final String expectedResolvedTemplate) throws TemplateException, BadLocationException {
		final ContextTypeRegistry registry = JavaPlugin.getDefault().getTemplateContextRegistry();
		final TemplateContextType context = registry.getContextType(contextType);
		final Template template = new Template("name", "description", contextType, templateContent, false);
		final TemplateTranslator translator = new TemplateTranslator();
		TemplateBuffer buffer = null;
		buffer = translator.translate(template);
		final TemplateVariable[] variables = buffer.getVariables();
		for (final TemplateVariable variable : variables) {
			assertTrue(NLS.bind("No resolver found for variable ''{0}'' in template ''{1}''", variable, template),
					canHandleVariable(context, variable));
		}
		assertEquals(expectedResolvedTemplate, getResolveTemplate(context, template));
	}

	private boolean canHandleVariable(final TemplateContextType context, final TemplateVariable variable) {
		for (final Iterator<?> iterator = context.resolvers(); iterator.hasNext();) {
			final TemplateVariableResolver resolver = (TemplateVariableResolver) iterator.next();
			if (variable.getType().equals(resolver.getType())) {
				return true;
			}
		}
		return false;
	}

	private String getResolveTemplate(final TemplateContextType context, final Template template)
			throws BadLocationException, TemplateException {
		final DocumentTemplateContext templateContext = new DocumentTemplateContext(context, new Document(),
				new Position(0));
		TemplateBuffer templateBuffer = null;
		templateBuffer = templateContext.evaluate(template);
		return templateBuffer.getString();
	}
}