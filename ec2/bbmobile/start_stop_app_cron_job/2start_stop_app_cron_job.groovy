node("bbmobile-cd-jenkins") {
    sh 'pwd'
    sh 'date'
    checkout scm
    echo "action: ${ACTION_TYPE}"
    def region_list=["us-east-1","eu-central-1","ap-southeast-1","ap-southeast-2","ap-northeast-1"]
    def region_map=['us-east-1':'vir','eu-central-1':'fra','ap-southeast-1':'sin','ap-southeast-2':'syd','ap-northeast-1':'tyo']
    
    stage('parallel job'){
      def branches_mbaas = [:]
      def branches_mlcs = [:]
      def i=1
      if ("${ACTION_TYPE}" == "stop") { 
        //stop rds
        sh 'sudo /bin/bash aws-cd-jenkins/jenkins_admin/mlcs_stop_stage_rds_shell.sh'
        //stop mbaas
        region_map.each{
        //This is start of the loop
          def region_name=it.key
        // region_name =  us-east-1
        // mbaas_asg_name_prefix = mbaas - dev
        // mbaas_elb_name = mbaas - dev -vir
        // group_name = mbaas - dev - vir - asgapp
          def mbaas_asg_name_prefix="mbaas-${ENV_NAME}"
          def mbaas_elb_name="mbaas-${ENV_NAME}-${it.value}"
          def group_name="mbaas-${ENV_NAME}-${it.value}-asgapp"
          ansiblePlaybook credentialsId: 'common_pri_key', extras: "-e Global_AWS_Region=${region_name} -e APP_ASG_IDENTIFIER=${mbaas_asg_name_prefix} -e MBAAS_ASG_NEW_ELB_NAME=${mbaas_elb_name}", installation: 'local_ansible', inventory: 'agents_playbook/localhost', playbook: 'agents_playbook/stop_app_asg.yaml', vaultCredentialsId: 'ansible-vault'
          
          branches_mbaas["branch${i}"] = {
            build job: 'start_stop_instance_by_Group_Name', parameters: [
            string(name: 'ACTION_TYPE', value: "${ACTION_TYPE}"),
            string(name: 'REGION_NAME', value: "${region_name}"),
            string(name: 'GroupName', value: "${group_name}")]
          }
          i=i+1
        }
        echo "current branches_mbaas size: ${branches_mbaas.size()}"

        //stop mlcs
        def mlcs_app_list=["mlcs-stage-vir-app","mlcs-stage-vir-sphinx"]
        for (def y=0;y<2;y++){
        //THis is start of the loop    
          def mlcs_app_name=mlcs_app_list[y]
          echo "mlcs app name: ${mlcs_app_name}"
          //mlcs app name: mlcs-stage-vir-app
          //mlcs app name: mlcs-stage-vir-sphinx
          branches_mlcs["branch${y}"] = {
            build job: 'start_stop_instance_by_Group_Name', parameters: [
              string(name: 'ACTION_TYPE', value: "${ACTION_TYPE}"),
              string(name: 'REGION_NAME', value: "us-east-1"),
              string(name: 'GroupName', value: "${mlcs_app_name}")]
          }
        }
      }
      
      if ("${ACTION_TYPE}" == "start") {
        sh 'sudo /bin/bash aws-cd-jenkins/jenkins_admin/mlcs_start_stage_rds_shell.sh'
        region_map.each{
          def region_name=it.key
          def mbaas_asg_name_prefix="mbaas-${ENV_NAME}"
          def mbaas_elb_name="mbaas-${ENV_NAME}-${it.value}"
          def group_name="mbaas-${ENV_NAME}-${it.value}-asgapp"
          
          branches_mbaas["branch${i}"] = {
            build job: 'start_stop_instance_by_Group_Name', parameters: [
              string(name: 'ACTION_TYPE', value: "${ACTION_TYPE}"),
              string(name: 'REGION_NAME', value: "${region_name}"),
              string(name: 'GroupName', value: "${group_name}")]
          }
          i=i+1
        }
        

        def mlcs_app_list=["mlcs-stage-vir-app","mlcs-stage-vir-sphinx"]
          for (def y=0;y<2;y++){
            def mlcs_app_name=mlcs_app_list[y]
            echo "mlcs app name: ${mlcs_app_name}"
            branches_mlcs["branch${y}"] = {
              build job: 'start_stop_instance_by_Group_Name', parameters: [
                string(name: 'ACTION_TYPE', value: "${ACTION_TYPE}"),
                string(name: 'REGION_NAME', value: "us-east-1"),
                string(name: 'GroupName', value: "${mlcs_app_name}")]
            }
          }
    }

    echo "mbaas job: ${branches_mbaas.values()}"
    echo "mlcs job: ${branches_mlcs.values()}"
    parallel branches_mbaas
    parallel branches_mlcs

// Parallel Stages
// https://www.cloudbees.com/blog/parallelism-and-distributed-builds-jenkins
// Optimizing your pipeline with parallelism
// triggering jobs and allowing them to run concurrently



//enable asg if start action
    if ("${ACTION_TYPE}" == "start") {
      region_map.each{
        def region_name=it.key
        def mbaas_asg_name_prefix="mbaas-${ENV_NAME}"
        def mbaas_elb_name="mbaas-${ENV_NAME}-${it.value}"
        ansiblePlaybook credentialsId: 'common_pri_key', extras: "-e Global_AWS_Region=${region_name} -e APP_ASG_IDENTIFIER=${mbaas_asg_name_prefix} -e MBAAS_ASG_NEW_ELB_NAME=${mbaas_elb_name}", installation: 'local_ansible', inventory: 'agents_playbook/localhost', playbook: 'agents_playbook/start_app_asg.yaml', vaultCredentialsId: 'ansible-vault'
      }
    }
	}
}