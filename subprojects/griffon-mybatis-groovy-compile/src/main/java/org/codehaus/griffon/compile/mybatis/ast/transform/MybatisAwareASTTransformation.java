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
package org.codehaus.griffon.compile.mybatis.ast.transform;

import griffon.plugins.mybatis.MybatisHandler;
import griffon.transform.MybatisAware;
import org.codehaus.griffon.compile.core.AnnotationHandler;
import org.codehaus.griffon.compile.core.AnnotationHandlerFor;
import org.codehaus.griffon.compile.core.ast.transform.AbstractASTTransformation;
import org.codehaus.griffon.compile.mybatis.MybatisAwareConstants;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.injectInterface;

/**
 * Handles generation of code for the {@code @MybatisAware} annotation.
 *
 * @author Andres Almiray
 */
@AnnotationHandlerFor(MybatisAware.class)
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class MybatisAwareASTTransformation extends AbstractASTTransformation implements MybatisAwareConstants, AnnotationHandler {
    private static final Logger LOG = LoggerFactory.getLogger(MybatisAwareASTTransformation.class);
    private static final ClassNode MYBATIS_HANDLER_CNODE = makeClassSafe(MybatisHandler.class);
    private static final ClassNode MYBATIS_AWARE_CNODE = makeClassSafe(MybatisAware.class);

    /**
     * Convenience method to see if an annotated node is {@code @MybatisAware}.
     *
     * @param node the node to check
     * @return true if the node is an event publisher
     */
    public static boolean hasMybatisAwareAnnotation(AnnotatedNode node) {
        for (AnnotationNode annotation : node.getAnnotations()) {
            if (MYBATIS_AWARE_CNODE.equals(annotation.getClassNode())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Handles the bulk of the processing, mostly delegating to other methods.
     *
     * @param nodes  the ast nodes
     * @param source the source unit for the nodes
     */
    public void visit(ASTNode[] nodes, SourceUnit source) {
        checkNodesForAnnotationAndType(nodes[0], nodes[1]);
        addMybatisHandlerIfNeeded(source, (AnnotationNode) nodes[0], (ClassNode) nodes[1]);
    }

    public static void addMybatisHandlerIfNeeded(SourceUnit source, AnnotationNode annotationNode, ClassNode classNode) {
        if (needsDelegate(classNode, source, METHODS, MybatisAware.class.getSimpleName(), MYBATIS_HANDLER_TYPE)) {
            LOG.debug("Injecting {} into {}", MYBATIS_HANDLER_TYPE, classNode.getName());
            apply(classNode);
        }
    }

    /**
     * Adds the necessary field and methods to support mybatis handling.
     *
     * @param declaringClass the class to which we add the support field and methods
     */
    public static void apply(@Nonnull ClassNode declaringClass) {
        injectInterface(declaringClass, MYBATIS_HANDLER_CNODE);
        Expression mybatisHandler = injectedField(declaringClass, MYBATIS_HANDLER_CNODE, MYBATIS_HANDLER_FIELD_NAME);
        addDelegateMethods(declaringClass, MYBATIS_HANDLER_CNODE, mybatisHandler);
    }
}