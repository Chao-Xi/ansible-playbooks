pipelineJob('stop_mobile_perf_at_seven_pm') {
    description('')
    compressBuildLog()
    definition {
        cps {
            sandbox(true)
            script('''node('bbmobile-ci-jenkins') {
   build job: 'start_stop_mobile_perf', parameters: [string(name: 'ACTION_TYPE', value: 'stop'), string(name: 'ONE_INSTNACE_NAME', value: '')], propagate: false, quietPeriod: 30, wait: false
}''')
        }
    }
    triggers {
        cron {
            spec('''TZ=Asia/Shanghai
H 19 * * *''')
        }
    }
}