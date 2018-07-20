node("bbmobile-cd-jenkins") {
    checkout scm
    sh 'pwd'
    sh 'date'
    ansiblePlaybook credentialsId: 'common_pri_key', extras: "-e GroupName=${GroupName} -e REGION_NAME=${REGION_NAME}", installation: 'local_ansible', inventory: 'agents_playbook/localhost', playbook: 'agents_playbook/start_stop_instance_by_group_name.yaml', tags: "${ACTION_TYPE}", vaultCredentialsId: 'ansible-vault'

}