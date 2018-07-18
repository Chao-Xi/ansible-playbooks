pipelineJob('start_stop_ci_agents') {
    description('')
    compressBuildLog()
    parameters {
        choiceParam('ACTION_TYPE', ['describe', 'start','stop','describe-one','start-one', 'stop-one'], '')
        stringParam {
            name('ONE_INSTNACE_NAME')
            defaultValue('')
            description('The instance you want to describe or stop, start')
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
            scriptPath('aws-ci-jenkins/jenkins_admin/start_stop_ci_agents.groovy')
        }
    }
}