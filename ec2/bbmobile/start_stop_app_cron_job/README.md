Project pipeline:
1. All start from start_stop_app_cron_job pipeline job, call the start_stop_app_cron_job jenkinsfile
2. start_stop_app_cron_job jenkinsfile include one stage. 'parallel job'
   one stage contains two part: start and stop
   [stop] parts contain four function: 
   1). stop rds
   2). get mbaas-elb, stop mbaas-asg, call stop_app_asg.yaml with [OPERATION_ASG: suspend-processes] call the ansible role [opearyion-asg]
   3). branches_mbaas: stop all instances inside mbaas-elb call start_stop_instance_by_Group_Name pipeline job
       with input variables and call start_stop_instance_by_group_name function and call start_stop_instance_by_group_name.yaml
   4). stop two mlcs-app

   [start] parts contain four function:
   1). start rds
   2). get mbaas-elb, start mbaas-asg, call start_app_asg.yaml with [OPERATION_ASG: resume-processes] call the ansible role [opearyion-asg]
       call first role: check-elb 
       after success: call second role [opearyion-asg]
   3). branches_mbaas: start all instances inside mbaas-elb call start_stop_instance_by_Group_Name pipeline job
       with input variables and call start_stop_instance_by_group_name function and call start_stop_instance_by_group_name.yaml
   4). start two mlcs-app
   
   
   parallel stop/start branches_mbaas
   parallel stop/start branches_mlcs

   [start] enable asg if it's start action


Project knowledges:
1. Parallel Stages
   start_stop_app_cron_job.groovy
   https://www.cloudbees.com/blog/parallelism-and-distributed-builds-jenkins
   Parallelism and Distributed Builds with Jenkins
   1. Making good use of one build server
   2. Distributed parallel with many build servers

2. Suspending and Resuming AWS asg Scaling Processes
   stop_app_asg.yaml
   OPERATION_ASG: suspend-processes

   To suspend all processes
   aws autoscaling suspend-processes --auto-scaling-group-name my-asg (omitting the --scaling-processes option)

   start_app_asg.yaml
   OPERATION_ASG: resume-processes

3. opeartion-asg/task/main.yaml
   1.  Get legacy cloudformation stack name
   aws cloudformation describe-stacks --region us-east-1 | grep StackName | grep Ansible-mbaas-dev-asg | head -1 | awk -F '\"' '{print $4}'
   aws cloudformation describe-stacks --region us-east-1 | grep StackName | grep Ansible-mbaas-dev-asg 
            "StackName": "Ansible-mbaas-dev-asg-########",
   "head -1"  show first 1 line  ( grep Ansible-mbaas-dev-asg | head -1)

   awk -F: '{print $4}'  

   awk this is the interpreter for the AWK Programming Language. The AWK language is useful for manipulation of data files, text retrieval and processing
   -F <value> - tells awk what field separator to use. In your case, -F: means that the separator is : (colon).
   '{print $4}' means print the fourth field (the fields being separated by :).
   '\"'  separate by ' " ‘
   "StackName": "Ansible-mbaas-dev-asg-########", separated by "
   1   
   2 StackName  
   3 : 
   4 Ansible-mbaas-dev-asg-########
   5 ,

   2. Get legacy instance list by ELB name  
   aws elb describe-load-balancers --load-balancer-name mbaas-dev-vir --region us-east-1 | egrep -o 'i-.{17}'
   output:
   i-############
   i-############
   
   Search for a pattern using extended regular expressions.   egrep 
   -o : Show only the part of a matching line that matches PATTERN.
   'i-.{}'
   aws elb describe-load-balancers --load-balancer-name mbaas-dev-vir --region us-east-1 
   
   https://www.computerhope.com/jargon/r/regex.htm
   i-.{17}  . is match any character   {17} is The exact 'x' amount of times to match   .{17}  17 times ant characters
   'i-[0-9a-f]+'   Matches any characters between 'a' and 'z' and '0' between '9'
   Matches character before + one or more times
  
   https://docs.aws.amazon.com/cli/latest/userguide/controlling-output.html
   aws elb describe-load-balancers --load-balancer-name mbaas-dev-vir --region us-east-1 --query 'LoadBalancerDescriptions[*].Instances[*].[InstanceId]' --output text
   output:
   i-############
   i-############

   aws elb describe-load-balancers --load-balancer-name mbaas-dev-vir --region us-east-1 --query 'LoadBalancerDescriptions[*].Instances[*].[InstanceId]' --output text
   ======
   aws elb describe-load-balancers --load-balancer-name mbaas-dev-vir --region us-east-1 | egrep -o 'i-.{17}'
    
   3. Get ASG name by instanceId from instanceTag
   aws ec2 describe-instances --instance-ids i-################# --region us-east-1 | sed -n '/groupName/{g;1!p;};h'
   aws ec2 describe-instances --instance-ids i-################# --query 'Reservations[].Instances[].Tags[?Key==`aws:autoscaling:groupName`].Value[]' --output text
   Ansible-mbaas-dev-asg-1516852767-MbaasAutoScalingGroup-#######
   
   4. Suspend or resume ASG before/after update war
   aws autoscaling suspend-processes --auto-scaling-group-name my-asg (omitting the --scaling-processes option)

 
 4. check-elb/task/main.yaml
    aws elb describe-instance-health --load-balancer-name mbaas-dev-vir --output text --region us-east-1 
    INSTANCESTATES  N/A i-################# N/A InService
    INSTANCESTATES  N/A i-################# N/A InService

    aws elb describe-instance-health --load-balancer-name mbaas-dev-vir --output text --region us-east-1 | grep InService | 
    INSTANCESTATES  N/A i-################# N/A InService
    INSTANCESTATES  N/A i-################# N/A InService

    aws elb describe-instance-health --load-balancer-name mbaas-dev-vir --output text --region us-east-1 | grep InService | wc -l
    We will use a pipe to count the number of lines, words and characters in a file with the UNIX wc command like this:
    wc to count the number of line  
    2 
    6 WC Command Examples to Count Number of Lines, Words, Characters in Linux
    The wc (word count) command in Unix/Linux operating systems is used to find out number of newline count, word count, byte and characters count in a files specified by the file arguments
    wc -l : Prints the number of lines in a file.
    wc -w : prints the number of words in a file.
    wc -c : Displays the count of bytes in a file.
    wc -m : prints the count of characters from a file.
    wc -L : prints only the length of the longest line in a file. 
    https://www.tecmint.com/wc-command-examples/

    Do-Until Loops
    The above example run the shell module recursively till the REG_OUTOFSERVICE_INSTANCE_COUNT.stdout == "0" 
    or the task has been retried for 60 times with a delay of 30 seconds. 
    The default value for “retries” is 60 and “delay” is 30.
    https://docs.ansible.com/ansible/2.6/user_guide/playbooks_error_handling.html
    Ignoring Failed Commands

5. rds 
   check status:
   aws rds describe-db-instances --db-instance-identifier {{rds-name}} --query DBInstances[*].DBInstanceStatus --output text --region us-east-1

   start /stop rds:
   aws rds start-db-instance --db-instance-identifier {{rds-name}} --region us-east-1 
   aws rds stop-db-instance --db-instance-identifier  {{rds-name}} --region us-east-1