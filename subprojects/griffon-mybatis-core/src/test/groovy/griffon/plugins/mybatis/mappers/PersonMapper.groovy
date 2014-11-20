/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package griffon.plugins.mybatis.mappers

import griffon.metadata.TypeProviderFor
import griffon.plugins.mybatis.MybatisMapper
import griffon.plugins.mybatis.Person

@TypeProviderFor(MybatisMapper)
interface PersonMapper extends MybatisMapper {
    Person findPersonById(int id)

    int insert(Person person)

    int update(Person person)

    int delete(Person person)

    List<Person> list()
}