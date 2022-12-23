/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies.
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

package org.eclipse.mylyn.internal.tasks.ui.migrator;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.tasks.ui.migrator.messages"; //$NON-NLS-1$

	public static String CompleteConnectorMigrationWizard_Complete_Connector_Migration;

	public static String CompleteConnectorMigrationWizard_Complete_Migration;

	public static String CompleteConnectorMigrationWizard_Completing_connector_migration;

	public static String CompleteConnectorMigrationWizard_ensure_created_queries;

	public static String CompleteConnectorMigrationWizard_first_page_message;

	public static String CompleteConnectorMigrationWizard_Have_You_Recreated_Your_Queries;

	public static String CompleteConnectorMigrationWizard_Migrate_Queries;

	public static String CompleteConnectorMigrationWizard_Migrating_Tasks_and_Private_Data;

	public static String CompleteConnectorMigrationWizard_Queries_not_migrated;

	public static String CompleteConnectorMigrationWizard_Queries_Using_New_Connectors;

	public static String CompleteConnectorMigrationWizard_Queries_Using_Old_Connectors;

	public static String CompleteConnectorMigrationWizard_second_page_message;

	public static String CompleteConnectorMigrationWizard_second_page_text;

	public static String CompleteConnectorMigrationWizard_Waiting_for_queries_to_synchronize;

	public static String ConnectorMigrationUi_Backing_up_task_list;

	public static String ConnectorMigrationUi_Complete_Connector_Migration_Prompt;

	public static String ConnectorMigrationUi_Connector_Migration;

	public static String ConnectorMigrationUi_Connector_Migration_Complete;

	public static String ConnectorMigrationUi_Connector_migration_completed_successfully_You_may_resume_using_the_task_list;

	public static String ConnectorMigrationUi_Deleting_old_repository_tasks_and_queries;

	public static String ConnectorMigrationUi_End_of_Connector_Support;

	public static String ConnectorMigrationUi_Error_deleting_task;

	public static String ConnectorMigrationUi_Validation_Failed;

	public static String ConnectorMigrationWizard_Connector_Migration;

	public static String ConnectorMigrationWizard_End_of_Connector_Support;

	public static String ConnectorMigrationWizard_Message;

	public static String ConnectorMigrationWizard_Body;

	public static String ConnectorMigrationWizard_Select_Connectors;

	public static String ConnectorMigrationWizard_Select_the_connectors_to_migrate;

	public static String ConnectorMigrationWizard_used_by_X_repositories;

	public static String ConnectorMigrationWizard_validation_failed;

	public static String ConnectorMigrator_complete_migration_prompt_message;

	public static String ConnectorMigrator_complete_migration_prompt_title;

	public static String ConnectorMigrator_Migrating_Queries;

	public static String ConnectorMigrator_Migrating_repositories;

	public static String ConnectorMigrator_Migrating_tasks_for_X;

	public static String ConnectorMigrator_Migrating_X;

	public static String ConnectorMigrator_Validating_connection_to_X;

	public static String ConnectorMigrator_Validating_repository_connections;

	public static String ConnectorMigrator_Waiting_for_tasks_to_synchronize;

	public static String ConnectorMigrator_X_Unsupported_do_not_delete;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
