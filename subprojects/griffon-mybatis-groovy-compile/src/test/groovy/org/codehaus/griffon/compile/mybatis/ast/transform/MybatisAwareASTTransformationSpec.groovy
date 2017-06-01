/*
 * Copyright 2014-2017 the original author or authors.
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
package org.codehaus.griffon.compile.mybatis.ast.transform

import griffon.plugins.mybatis.MybatisHandler
import spock.lang.Specification

import java.lang.reflect.Method

/**
 * @author Andres Almiray
 */
class MybatisAwareASTTransformationSpec extends Specification {
    def 'MybatisAwareASTTransformation is applied to a bean via @MybatisAware'() {
        given:
        GroovyShell shell = new GroovyShell()

        when:
        def bean = shell.evaluate('''
        @griffon.transform.MybatisAware
        class Bean { }
        new Bean()
        ''')

        then:
        bean instanceof MybatisHandler
        MybatisHandler.methods.every { Method target ->
            bean.class.declaredMethods.find { Method candidate ->
                candidate.name == target.name &&
                candidate.returnType == target.returnType &&
                candidate.parameterTypes == target.parameterTypes &&
                candidate.exceptionTypes == target.exceptionTypes
            }
        }
    }

    def 'MybatisAwareASTTransformation is not applied to a MybatisHandler subclass via @MybatisAware'() {
        given:
        GroovyShell shell = new GroovyShell()

        when:
        def bean = shell.evaluate('''
        import griffon.plugins.mybatis.MybatisCallback
        import griffon.plugins.mybatis.exceptions.RuntimeMybatisException
        import griffon.plugins.mybatis.MybatisHandler

        import javax.annotation.Nonnull
        @griffon.transform.MybatisAware
        class MybatisHandlerBean implements MybatisHandler {
            @Override
            public <R> R withSqlSession(@Nonnull MybatisCallback<R> callback) throws RuntimeMybatisException {
                return null
            }
            @Override
            public <R> R withSqlSession(@Nonnull String sessionFactoryName, @Nonnull MybatisCallback<R> callback) throws RuntimeMybatisException {
                return null
            }
            @Override
            void closeSqlSession(){}
            @Override
            void closeSqlSession(@Nonnull String sessionFactoryName){}
        }
        new MybatisHandlerBean()
        ''')

        then:
        bean instanceof MybatisHandler
        MybatisHandler.methods.every { Method target ->
            bean.class.declaredMethods.find { Method candidate ->
                candidate.name == target.name &&
                    candidate.returnType == target.returnType &&
                    candidate.parameterTypes == target.parameterTypes &&
                    candidate.exceptionTypes == target.exceptionTypes
            }
        }
    }
}
