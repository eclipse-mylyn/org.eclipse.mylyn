/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

	public static String ConnectorMigrationWizard_Connector_Migration;

	public static String ConnectorMigrationWizard_End_of_Connector_Support;

	public static String ConnectorMigrationWizard_Message;

	public static String ConnectorMigrationWizard_Body;

	public static String ConnectorMigrationWizard_Select_Connectors;

	public static String ConnectorMigrationWizard_Select_the_connectors_to_migrate;

	public static String ConnectorMigrationWizard_validation_failed;

	public static String ConnectorMigrator_complete_migration_prompt_message;

	public static String ConnectorMigrator_complete_migration_prompt_title;

	public static String ConnectorMigrator_Migrating_Queries;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
