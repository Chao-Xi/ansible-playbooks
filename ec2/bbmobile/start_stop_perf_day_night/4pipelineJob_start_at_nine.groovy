pipelineJob('start_mobile_perf_at_nine_am') {
    description('')
    compressBuildLog()
    definition {
        cps {
            sandbox(true)
            script('''node('bbmobile-ci-jenkins') {
   build job: 'start_stop_mobile_perf', parameters: [string(name: 'ACTION_TYPE', value: 'start'), string(name: 'ONE_INSTNACE_NAME', value: '')], propagate: false, quietPeriod: 30, wait: false
}''')
        }
    }
// cps{}
// pipeline job A invoke another pipeline job B 
// build: Build a job
// build job: 'start_stop_mobile_perf', parameters: [string(name: 'ACTION_TYPE', value: 'start'), string(name: 'ONE_INSTNACE_NAME', value: '')], quietPeriod: 30, wait: false   
    triggers {
        cron {
            spec('''TZ=Asia/Shanghai
H 9 * * 1-5''')
        }
    }
}