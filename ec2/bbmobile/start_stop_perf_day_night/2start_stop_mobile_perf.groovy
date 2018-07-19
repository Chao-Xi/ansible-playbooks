node("bbmobile-ci-jenkins") {
    checkout scm
    sh 'pwd'
    sh 'date'
    ansiblePlaybook credentialsId: 'common_pri_key', extras: "-e ONE_INSTNACE_NAME=${ONE_INSTNACE_NAME}", installation: 'local_ansible', inventory: 'agents_playbook/localhost', playbook: 'agents_playbook/start_stop_all_mobile_perf_vir.yaml', tags: "${ACTION_TYPE}", vaultCredentialsId: 'ansible-vault'

}