pipelineJob('start_stop_app_cron_job') {
    description('start stop instance with GroupName ops-stage-service')
    compressBuildLog()
    parameters {
        choiceParam('ACTION_TYPE', ['describe', 'start','stop'], '')
        choiceParam('ENV_NAME', ['dev', 'stage'], '')
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
            scriptPath('aws-cd-jenkins/jenkins_admin/start_stop_app_cron_job.groovy')
        }
    }
}