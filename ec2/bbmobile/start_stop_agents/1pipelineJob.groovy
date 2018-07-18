pipelineJob('start_stop_agents') {
    description('')
    compressBuildLog()
    parameters {
        choiceParam('ACTION_TYPE', ['describe', 'start','stop','start-one', 'stop-one'], '')
        stringParam {
            name('ONE_INSTNACE_NAME')
            defaultValue('')
            description('The instance you want to stop or start')
        }
    }
    definition {
        cpsScm {
            lightweight(true)
            scm {
                git {
                    branch('develop')
                    remote {
                        credentials('####')
                        url('ssh://git@#######')
                    }
                }
            }
            scriptPath('aws-ci-jenkins/jenkins_admin/start_stop_agents.groovy')
        }
    }
}