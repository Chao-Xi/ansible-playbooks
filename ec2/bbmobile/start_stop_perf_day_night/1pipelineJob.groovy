pipelineJob('start_stop_mobile_perf') {
    authorization {
        blocksInheritance(true)
        permission('hudson.model.Item.Read', '')
        permission('hudson.model.Item.Discover', '')
        permission('hudson.model.Item.Build', '')
        permission('hudson.model.Item.Cancel', '')
        permission('com.cloudbees.plugins.credentials.CredentialsProvider.View', '')
        permission('hudson.model.Item.Workspace', '')
        permission('hudson.model.Run.Delete', '')
        permission('hudson.model.Run.Update', '')

    }
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
                        credentials('mobileci')
                        url('ssh://git@stash.bbpd.io/mops/jenkins2.git')
                    }
                }
            }
            scriptPath('aws-ci-jenkins/jenkins_admin/start_stop_mobile_perf.groovy')
        }
    }
}