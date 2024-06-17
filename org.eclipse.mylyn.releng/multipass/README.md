# Multipass

Here we describe how to setup an VM with local Mylyn Test Instances

This setup use Ansible and inside the VM we use Docker Swarm

Actual we only support the setup in Docker Swarm with 3 VM's but in the
future I will continue to support a setup with only 1 VM

This setup is only tested on am MacBook Pro M1.

1. Download Multipass
   please install https://multipass.run/install for your platform
2. Add certificate so you can run a runtime Workspace
   
   Steps from my Mac Environment use $JAVA_HOME on other Environments.
   $JAVA_HOME was not set on my environment so I have to use th Path '/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home' instead

   1. cp $JAVA_HOME/lib/security/cacerts certs/cacerts
3. Create the Multipass VM's (3 VM's)
  - switch to this folder
     ```
     cd ./org.eclipse.mylyn.all/org.eclipse.mylyn/org.eclipse.mylyn.releng/multipass/
     ```
  - create the 3 VM's
     ```
     ansible-playbook create_vm.yml --ask-become-pass --extra-vars="docker_run_mode=container"
     or
     ansible-playbook create_vm.yml --ask-become-pass --extra-vars="docker_run_mode=swarm"
     or
     ansible-playbook create_vm.yml --ask-become-pass --extra-vars="docker_run_mode=single"
     ```
     This creates the 3 VM's and make sure that /etc/hosts contains the correct entries
     and also update the inventory.yml with the new IP addresses of the VM's.
     During the first time run we create a sshkey for all instances in folder first_setup
4. First time Setup the VM's
   ```
   ansible-playbook mylyn_setup.yml --extra-vars="docker_run_mode=container"
   or
   ansible-playbook mylyn_setup.yml --extra-vars="docker_run_mode=swarm"
   or
   ansible-playbook mylyn_setup.yml --extra-vars="docker_run_mode=single"
   ```
5. restart the services the VM's 
   ```
   ansible-playbook mylyn_setup.yml --skip-tags base_software --extra-vars="docker_run_mode=container"
   or
   ansible-playbook mylyn_setup.yml --skip-tags base_software --extra-vars="docker_run_mode=swarm"
   or
   ansible-playbook mylyn_setup.yml --skip-tags base_software --extra-vars="docker_run_mode=single"
   ```
   or with recreate the Docker swarm 
   ```
   ansible-playbook mylyn_setup.yml --skip-tags base_software --extra-vars="swarm_recreate=true" --extra-vars="docker_run_mode=swarm"
   ```

6. delete VM's and recreate VM's 
   1. delete the running VM's
   ```
   multipass delete --all
   multipass purge
   ```
   2. on macOS we need to delete the on no longer needed IP Address 
    ```
   sudo vi /var/db/dhcpd_leases (delete the 7 lines per instance)
   vi ~/.ssh/known_hosts (remove the old ip adress entries)
   ```
   3. recreate the VM's and start the Services with
   ```
   ansible-playbook create_vm.yml --ask-become-pass --extra-vars="docker_run_mode=container"
   ansible-playbook mylyn_setup.yml --extra-vars="docker_run_mode=container"
   because container is Default you can also use 
   ansible-playbook create_vm.yml --ask-become-pass
   ansible-playbook mylyn_setup.yml
   or
   ansible-playbook create_vm.yml --ask-become-pass --extra-vars="docker_run_mode=swarm"
   ansible-playbook mylyn_setup.yml --extra-vars="docker_run_mode=swarm"
   or
   ansible-playbook create_vm.yml --ask-become-pass --extra-vars="docker_run_mode=single"
   ansible-playbook mylyn_setup.yml --extra-vars="docker_run_mode=single"
   ```
   
   
   ansible-playbook create_vm.yml --ask-become-pass --extra-vars="docker_run_mode=all"
   ansible-playbook mylyn_setup.yml --extra-vars="docker_run_mode=container" --extra-vars="remote_domain=${FQDN}" --skip-tags artifactory,bugzilla,jenkins,gitlab