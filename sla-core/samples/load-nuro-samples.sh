#
# Copyright 2014 Atos
# Contact: Atos <roman.sosa@atos.net>
#
#    Licensed under the Apache License, Version 2.0 (the "License");
#    you may not use this file except in compliance with the License.
#    You may obtain a copy of the License at
#
#        http://www.apache.org/licenses/LICENSE-2.0
#
#    Unless required by applicable law or agreed to in writing, software
#    distributed under the License is distributed on an "AS IS" BASIS,
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#    See the License for the specific language governing permissions and
#    limitations under the License.
#

curl -X POST http://localhost:8080/sla-service/providers -d@"provider-nuro.xml" -H"Content-type: application/xml" -u user:password

curl -X POST http://localhost:8080/sla-service/templates -d@"template-nuro.xml" -H"Content-type: application/xml" -u user:password

curl -X POST http://localhost:8080/sla-service/agreements -d@"agreement-nuro.xml" -H"Content-type: application/xml" -u user:password

curl -X PUT http://localhost:8080/sla-service/enforcements/$1/start -u user:password
