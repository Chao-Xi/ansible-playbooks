node("bbmobile-ci-jenkins") {
    checkout scm
    sh 'pwd'
    sh 'date'
    ansiblePlaybook credentialsId: 'common_pri_key', extras: "-e GroupName=${GroupName} -e REGION_NAME=${REGION_NAME}", installation: 'local_ansible', inventory: 'agents_playbook/localhost', playbook: 'agents_playbook/start_stop_instance_by_group_name.yaml', tags: "${ACTION_TYPE}", vaultCredentialsId: 'ansible-vault'
}
// -e e set additional variables as key=value or YAML/JSON,
// -e GroupName=${GroupName} -e REGION_NAME=${REGION_NAME}

// /home/ubuntu/pyenv/versions/ansible/bin/ansible-playbook agents_playbook/start_stop_instance_by_group_name.yaml 
// -i agents_playbook/localhost -t start -e GroupName=mlcs-dev-vir-app -e REGION_NAME=us-east-1


