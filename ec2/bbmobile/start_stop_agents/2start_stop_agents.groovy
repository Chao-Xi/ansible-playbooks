node("bbmobile-ci-jenkins") {
    checkout scm
    sh 'pwd'
    sh 'date'
    ansiblePlaybook credentialsId: 'common_pri_key', extras: "-e ONE_INSTNACE_NAME=${ONE_INSTNACE_NAME}", installation: 'local_ansible', inventory: 'agents_playbook/localhost', playbook: 'agents_playbook/start_stop_all_agents.yaml', tags: "${ACTION_TYPE}", vaultCredentialsId: 'ansible-vault'

}
//Two parameters:
//Passing Variables On The Command Line
//it is possible to set variables at the command line using the --extra-vars (or -e) argument. 
//Variables can be defined using a single quoted string (containing one or more variables) using one of the formats below

//Tags
//If you have a large playbook it may become useful to be able to run a specific part of the configuration without running the whole playbook.

//Both plays and tasks support a “tags:” attribute for this reason. You can ONLY filter tasks based on tags from the command line with --tags or --skip-tags. 
//Adding “tags:” in any part of a play (including roles) adds those tags to the contained tasks.
//tags: "${ACTION_TYPE}"
//So different ACTION_TYPE invokes different different tasks

// Ansible works against multiple systems in your infrastructure at the same time.
// It does this by selecting portions of systems listed in Ansible’s inventory, 
// which defaults to being saved in the location /etc/ansible/hosts. 
// You can specify a different inventory file using the -i <path> option on the command line.

// https://docs.ansible.com/ansible/2.4/ansible-playbook.html


// /home/ubuntu/pyenv/versions/ansible/bin/ansible-playbook agents_playbook/start_stop_all_agents.yaml 
// -i agents_playbook/localhost 
// -i, --inventory, --inventory-file.  specify inventory host path (default=[[u’/etc/ansible/hosts’]]) or comma separated host list.
// -t describe    -t = --tags => only run plays and tasks tagged with these values
// -e ONE_INSTNACE_NAME=

///home/ubuntu/pyenv/versions/ansible/bin/ansible-playbook agents_playbook/start_stop_all_agents.yaml -i agents_playbook/localhost -t describe 
// -e ONE_INSTNACE_NAME=bbmobile-mlcs-builder