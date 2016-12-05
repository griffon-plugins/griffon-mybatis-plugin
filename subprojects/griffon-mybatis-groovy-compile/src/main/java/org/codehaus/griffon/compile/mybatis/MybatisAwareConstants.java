/*
 * Copyright 2014-2016 the original author or authors.
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
package org.codehaus.griffon.compile.mybatis;

import org.codehaus.griffon.compile.core.BaseConstants;
import org.codehaus.griffon.compile.core.MethodDescriptor;

import static org.codehaus.griffon.compile.core.MethodDescriptor.annotatedMethod;
import static org.codehaus.griffon.compile.core.MethodDescriptor.annotatedType;
import static org.codehaus.griffon.compile.core.MethodDescriptor.annotations;
import static org.codehaus.griffon.compile.core.MethodDescriptor.args;
import static org.codehaus.griffon.compile.core.MethodDescriptor.method;
import static org.codehaus.griffon.compile.core.MethodDescriptor.throwing;
import static org.codehaus.griffon.compile.core.MethodDescriptor.type;
import static org.codehaus.griffon.compile.core.MethodDescriptor.typeParams;
import static org.codehaus.griffon.compile.core.MethodDescriptor.types;

/**
 * @author Andres Almiray
 */
public interface MybatisAwareConstants extends BaseConstants {
    String SQL_SESSION_TYPE = "org.apache.ibatis.session.SqlSession";
    String MYBATIS_HANDLER_TYPE = "griffon.plugins.mybatis.MybatisHandler";
    String MYBATIS_CALLBACK_TYPE = "griffon.plugins.mybatis.MybatisCallback";
    String RUNTIME_MYBATIS_EXCEPTION_TYPE = "griffon.plugins.mybatis.exceptions.RuntimeMybatisException";
    String MYBATIS_HANDLER_PROPERTY = "mybatisHandler";
    String MYBATIS_HANDLER_FIELD_NAME = "this$" + MYBATIS_HANDLER_PROPERTY;

    String METHOD_WITH_SQL_SESSION = "withSqlSession";
    String METHOD_CLOSE_SQL_SESSION = "closeSqlSession";
    String SESSION_FACTORY_NAME = "sessionFactoryName";
    String CALLBACK = "callback";

    MethodDescriptor[] METHODS = new MethodDescriptor[]{
        method(
            type(VOID),
            METHOD_CLOSE_SQL_SESSION
        ),
        method(
            type(VOID),
            METHOD_CLOSE_SQL_SESSION,
            args(annotatedType(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING))
        ),

        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NONNULL),
            type(R),
            typeParams(R),
            METHOD_WITH_SQL_SESSION,
            args(annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), MYBATIS_CALLBACK_TYPE, R)),
            throwing(type(RUNTIME_MYBATIS_EXCEPTION_TYPE))
        ),
        annotatedMethod(
            types(type(JAVAX_ANNOTATION_NONNULL)),
            type(R),
            typeParams(R),
            METHOD_WITH_SQL_SESSION,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_LANG_STRING),
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), MYBATIS_CALLBACK_TYPE, R)),
            throwing(type(RUNTIME_MYBATIS_EXCEPTION_TYPE))
        )
    };
}
