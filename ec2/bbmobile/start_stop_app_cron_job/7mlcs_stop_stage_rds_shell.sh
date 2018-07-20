#!/bin/bash \n
mlcs_stage_rds_analytics_status=`aws rds describe-db-instances --db-instance-identifier mlcs-analytics-stage-rds --query DBInstances[*].DBInstanceStatus --output text --region us-east-1`
if [ $mlcs_stage_rds_analytics_status != "stopped" ]
  then
    echo "stop rds mlcs-analytics-stage-rds"
    aws rds stop-db-instance --db-instance-identifier mlcs-analytics-stage-rds --region us-east-1
fi

mlcs_stage_rds_status=`aws rds describe-db-instances --db-instance-identifier mlcs-stage-rds --query DBInstances[*].DBInstanceStatus --output text --region us-east-1`
if [ $mlcs_stage_rds_status != "stopped" ]
  then
    echo "stop rds mlcs-stage-rds "
    aws rds stop-db-instance --db-instance-identifier mlcs-stage-rds --region us-east-1
fi