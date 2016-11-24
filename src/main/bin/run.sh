#!/bin/bash
# --------------------------------------------------------------
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#
# --------------------------------------------------------------

echo "Starting Migration Client..."
echo "Check the run.log for the log..."

script_path="$( cd -P "$( dirname "$SOURCE" )" && pwd )/`dirname $0`"
lib_path=${script_path}/../lib/
class_path=`echo ${lib_path}/*.jar | tr ' ' ':'`
properties="-Dlog4j.properties.file.path=${script_path}/../conf/log4j.properties"

# Uncomment below line to enable remote debugging
#debug="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005"

## Uncomment to run in nohup mode. Comment default mode when running nohup mode.
#nohup java -cp "${class_path}" ${properties} ${debug} org.carbon.migration.App $* > /dev/null 2>&1 & echo $! > run.pid &

## Comment this when running in nohup mode.
java -cp "${class_path}" ${properties} ${debug} org.carbon.migration.App $*
