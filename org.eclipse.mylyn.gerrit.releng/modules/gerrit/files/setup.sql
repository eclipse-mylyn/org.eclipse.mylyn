INSERT INTO accounts 
  (account_id,preferred_email,registered_on)
VALUES
  (1000000,'admin@mylyn.eclipse.org',now());
INSERT INTO account_group_members
  (account_id, group_id)
VALUES (
  (SELECT account_id FROM accounts WHERE preferred_email='admin@mylyn.eclipse.org'),
  (SELECT group_id FROM account_groups WHERE name='Administrators')
);
INSERT INTO account_external_ids
  (account_id, external_id)
VALUES (
  (SELECT account_id FROM accounts WHERE preferred_email='admin@mylyn.eclipse.org'),
  'gerrit:admin@mylyn.eclipse.org'
);
INSERT INTO account_external_ids
  (account_id, external_id)
VALUES (
  (SELECT account_id FROM accounts WHERE preferred_email='admin@mylyn.eclipse.org'),
  'username:admin'
);
INSERT INTO account_ssh_keys
  (ssh_public_key,valid,account_id)
VALUES (
 'ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDZn6ilqAomaR67p9gSP9NtQYE2mBBGxZrU+dSs2RIEhFcmAyUQcXgfJf83OVniblSbytraWrbQHqfetkdf8NOYhxewy5W7iVw5z+trGtpSaPyNMn8SA5n1GZgyBHgytY+QdU2E3DFeHgLxKQkwV2xfpOkDOV/OT5fFEs+5Uecl9e6tQJMh4P0Xod3N+03yZ+mNrpBbFYJduLMIjMev9ywblCP8o840nD2Rcyg9Og2tk/zWLHeW+UyiLKQN7YFG8sfSEl/N8J57SHWpVwIn5yGS6w+1nP7eg0esnsAtX6iF0U9mXbIRAwBmtDJ5qnV0fUmku8cVoKdYU+1QZT59LiXb admin@mylyn.eclipse.org',
 'Y',
 (SELECT account_id FROM accounts WHERE preferred_email='admin@mylyn.eclipse.org'),
);
