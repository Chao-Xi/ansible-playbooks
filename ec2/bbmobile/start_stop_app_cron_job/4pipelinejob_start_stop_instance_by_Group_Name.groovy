pipelineJob('start_stop_instance_by_Group_Name') {
    description('')
    compressBuildLog()
    parameters {
        choiceParam('ACTION_TYPE', ['describe', 'start','stop'], '')
        choiceParam('REGION_NAME', ['us-east-1', 'eu-central-1','ap-southeast-1','ap-southeast-2','ap-northeast-1'], '')
        stringParam {
            name('GroupName')
            defaultValue('')
            description('The instance group name you want to stop or start')
        }
    }
    definition {
        cpsScm {
            lightweight(true)
            scm {
                git {
                    branch('develop')
                    remote {
                        credentials('mobileci')
                        url('ssh://git@stash.bbpd.io/mops/jenkins2.git')
                    }
                }
            }
            scriptPath('aws-cd-jenkins/jenkins_admin/start_stop_instance_by_group_name.groovy')
        }
    }
}