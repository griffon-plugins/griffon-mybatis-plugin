/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2014-2021 The author and/or original authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
dataSource {
    driverClassName = 'org.h2.Driver'
    username = 'sa'
    password = ''
    pool {
        idleTimeout = 60000
        maximumPoolSize = 8
        minimumIdle = 5
    }
}

environments {
    development {
        dataSource {
            dbCreate = 'create' // one of ['create', 'skip']
            url = 'jdbc:h2:mem:${application_name}-dev'
        }
    }
    test {
        dataSource {
            dbCreate = 'create'
            url = 'jdbc:h2:mem:${application_name}-test'
        }
    }
    production {
        dataSource {
            dbCreate = 'skip'
            url = 'jdbc:h2:mem:${application_name}-prod'
        }
    }
}

dataSources {
    internal {
        driverClassName = 'org.h2.Driver'
        username = 'sa'
        password = ''
        schema = false
        url = 'jdbc:h2:mem:${application_name}-internal'
    }
    people {
        driverClassName = 'org.h2.Driver'
        username = 'sa'
        password = ''
        dbCreate = 'create'
        url = 'jdbc:h2:mem:${application_name}-people'
    }
}