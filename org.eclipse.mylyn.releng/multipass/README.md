# Multipass

Here we describe how to set up a VM with local Mylyn test instances.

This setup uses Ansible, and within the VM we use Docker or Docker Swarm.

The Docker Swarm mode with 3 VMs is no longer used (but not removed); instead, we now work in container mode.

In this mode, we have 2 VMs
1. Management VM for admin, createCertificates
2. Work VM Docker host with all services
After setup, you can stop the management VM.


This setup has only been tested on a MacBook Pro M1 and a MacMini M4.

Here are the following steps.
1. Download Multipass
   please install https://multipass.run/install for your platform
2. Add certificate so you can run a runtime Workspace
   
   Steps from my Mac Environment use $JAVA_HOME on other Environments.
   $JAVA_HOME was not set on my environment so I have to use th Path '/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home' instead

   1. cp /Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home/lib/security/cacerts to the root folder of your installation ../certs/cacerts
3. Create the Multipass VM's
  - switch to this folder
     ```
     cd ./org.eclipse.mylyn.all/org.eclipse.mylyn/org.eclipse.mylyn.releng/multipass/
     ```
  - create the VM's
     ```
     ansible-playbook -i inventory.yml create_vm.yml --ask-become-pass --extra-vars="local_prefix=mylyn"
     ```
     this is the same as
     ```
     ansible-playbook -i inventory.yml create_vm.yml --ask-become-pass --extra-vars="local_prefix=mylyn" --extra-vars="docker_run_mode=container      
	 ```  
	 This does the following steps
	   1. creates the VM's
	   2. sure that /etc/hosts contains the correct entries
	   3. create the rootca and certificate 
	   4. During the first time run we create a sshkey for all instances in folder first_setup
	 This creates the VM's and make sure that /etc/hosts contains the correct entries and also update the inventory.yml with the new IP addresses of the VM's.
	 During the first time run we create a sshkey for all instances in folder first_setup
	 

4. First time Setup the VM's
   ```
   ansible-playbook -i inventory.yml mylyn_setup.yml --extra-vars="local_prefix=mylyn"
   ```
5. restart the services the VM's 
   ```
   ansible-playbook mylyn_setup.yml -i inventory.yml --skip-tags base_software --extra-vars="local_prefix=mylyn"
   ```

6. delete VM's and recreate VM's 
   1. delete the running VM's
   ```
   multipass delete mylynadm01 
   multipass delete mylynmstr01
   multipass delete mylynwrk01
   multipass purge
   ```
   2. on macOS we need to delete the on no longer needed IP Address 
    ```
   sudo vi /var/db/dhcpd_leases (delete the 7 lines per instance)
   vi ~/.ssh/known_hosts (remove the old ip adress entries)
   ```
  
Now you can use the following URLs
  - https://mylyn.local/traefikdashboard
  - https://mylyn.local/mylyn_idx/
  - https://mylyn.local/grafana/

 for verification on the installation host computer.
You will get certification warnings and must trust the self signed certificate.

If you want to access the URLs from an other computer in your local environment you must add a line to your /etc/hosts file.

You can add the following additional variables for the Ansible playbook. Use the extra-vars when executing create_vm.yml. For mylyn_setup.yml, use only local_prefix; all others will be loaded from create_vm.yml.

| varname | Decription |
| --- | --- |
| local_prefix | There are groups in the inventory, and you can select which groups to use.<br>This is used to support an inventory that can be used for different VM setups.<br>The default setting is “mylyn,” assuming we have the following groups: mylyn_admin, mylyn_swarm_master, mylyn_swarm_worker.  |
| local_domain | The name of the domain. The default setting is “mylyn.local.” |
| docker_run_mode | Setting mode. Allowed values: swarm, container, single.<br>The default setting is “container”, and the other modes may result in configuration errors. |