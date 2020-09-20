/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2014-2020 The author and/or original authors.
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
package griffon.plugins.mybatis

import griffon.annotations.inject.BindTo
import griffon.core.GriffonApplication
import griffon.plugins.datasource.events.DataSourceConnectEndEvent
import griffon.plugins.datasource.events.DataSourceConnectStartEvent
import griffon.plugins.datasource.events.DataSourceDisconnectEndEvent
import griffon.plugins.datasource.events.DataSourceDisconnectStartEvent
import griffon.plugins.mybatis.events.MybatisConnectEndEvent
import griffon.plugins.mybatis.events.MybatisConnectStartEvent
import griffon.plugins.mybatis.events.MybatisDisconnectEndEvent
import griffon.plugins.mybatis.events.MybatisDisconnectStartEvent
import griffon.plugins.mybatis.exceptions.RuntimeMybatisException
import griffon.plugins.mybatis.mappers.PersonMapper
import griffon.test.core.GriffonUnitRule
import org.apache.ibatis.session.SqlSession
import org.junit.Rule
import spock.lang.Specification
import spock.lang.Unroll

import javax.application.event.EventHandler
import javax.inject.Inject

@Unroll
class MybatisSpec extends Specification {
    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace")
    }

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule()

    @Inject
    private MybatisHandler mybatisHandler

    @Inject
    private GriffonApplication application

    void 'Open and close default mybatis'() {
        given:
        List eventNames = [
            'MybatisConnectStartEvent', 'DataSourceConnectStartEvent',
            'DataSourceConnectEndEvent', 'MybatisConnectEndEvent',
            'MybatisDisconnectStartEvent', 'DataSourceDisconnectStartEvent',
            'DataSourceDisconnectEndEvent', 'MybatisDisconnectEndEvent'
        ]
        TestEventHandler testEventHandler = new TestEventHandler()
        application.eventRouter.subscribe(testEventHandler)

        when:
        mybatisHandler.withSqlSession { String sessionFactoryName, SqlSession session ->
            true
        }
        mybatisHandler.closeSqlSession()
        // second call should be a NOOP
        mybatisHandler.closeSqlSession()

        then:
        testEventHandler.events.size() == 8
        testEventHandler.events == eventNames
    }

    void 'Connect to default sqlSessionFactory'() {
        expect:
        mybatisHandler.withSqlSession { String sessionFactoryName, SqlSession session ->
            sessionFactoryName == 'default' && session
        }
    }

    void 'Bootstrap init is called'() {
        given:
        assert !bootstrap.initWitness

        when:
        mybatisHandler.withSqlSession { String sessionFactoryName, SqlSession session -> }

        then:
        bootstrap.initWitness
        !bootstrap.destroyWitness
    }

    void 'Bootstrap destroy is called'() {
        given:
        assert !bootstrap.initWitness
        assert !bootstrap.destroyWitness

        when:
        mybatisHandler.withSqlSession { String sessionFactoryName, SqlSession session -> }
        mybatisHandler.closeSqlSession()

        then:
        bootstrap.initWitness
        bootstrap.destroyWitness
    }

    void 'Can connect to #name sqlSessionFactory'() {
        expect:
        mybatisHandler.withSqlSession(name) { String sessionFactoryName, SqlSession session ->
            sessionFactoryName == name && session
        }

        where:
        name       | _
        'default'  | _
        'internal' | _
        'people'   | _
    }

    void 'Bogus sqlSessionFactory name (#name) results in error'() {
        when:
        mybatisHandler.withSqlSession(name) { String sessionFactoryName, SqlSession session ->
            true
        }

        then:
        thrown(IllegalArgumentException)

        where:
        name    | _
        null    | _
        ''      | _
        'bogus' | _
    }

    void 'Execute statements on people table'() {
        when:
        List peopleIn = mybatisHandler.withSqlSession('people') { String sessionFactoryName, SqlSession session ->
            PersonMapper personMapper = session.getMapper(PersonMapper)
            [[id: 1, name: 'Danno', lastname: 'Ferrin'],
             [id: 2, name: 'Andres', lastname: 'Almiray'],
             [id: 3, name: 'James', lastname: 'Williams'],
             [id: 4, name: 'Guillaume', lastname: 'Laforge'],
             [id: 5, name: 'Jim', lastname: 'Shingler'],
             [id: 6, name: 'Alexander', lastname: 'Klein'],
             [id: 7, name: 'Rene', lastname: 'Groeschke']].each { data ->
                Person person = new Person()
                data.each { propName, propValue ->
                    person[propName] = propValue
                }
                personMapper.insert(person)
            }
        }

        List peopleOut = mybatisHandler.withSqlSession('people') { String sessionFactoryName, SqlSession session ->
            PersonMapper personMapper = session.getMapper(PersonMapper)
            personMapper.list()*.asMap()
        }

        then:
        peopleIn == peopleOut
    }

    void 'A runtime SQLException is thrown within sqlSession handling'() {
        when:
        mybatisHandler.withSqlSession { String sessionFactoryName, SqlSession session ->
            PersonMapper personMapper = session.getMapper(PersonMapper)
            personMapper.insert(new Person(id: 0))
        }

        then:
        thrown(RuntimeMybatisException)
    }

    @BindTo(MybatisBootstrap)
    private TestMybatisBootstrap bootstrap = new TestMybatisBootstrap()

    private class TestEventHandler {
        List<String> events = []

        @EventHandler
        void handleDataSourceConnectStartEvent(DataSourceConnectStartEvent event) {
            events << event.class.simpleName
        }

        @EventHandler
        void handleDataSourceConnectEndEvent(DataSourceConnectEndEvent event) {
            events << event.class.simpleName
        }

        @EventHandler
        void handleDataSourceDisconnectStartEvent(DataSourceDisconnectStartEvent event) {
            events << event.class.simpleName
        }

        @EventHandler
        void handleDataSourceDisconnectEndEvent(DataSourceDisconnectEndEvent event) {
            events << event.class.simpleName
        }

        @EventHandler
        void handleMybatisConnectStartEvent(MybatisConnectStartEvent event) {
            events << event.class.simpleName
        }

        @EventHandler
        void handleMybatisConnectEndEvent(MybatisConnectEndEvent event) {
            events << event.class.simpleName
        }

        @EventHandler
        void handleMybatisDisconnectStartEvent(MybatisDisconnectStartEvent event) {
            events << event.class.simpleName
        }

        @EventHandler
        void handleMybatisDisconnectEndEvent(MybatisDisconnectEndEvent event) {
            events << event.class.simpleName
        }
    }
}
