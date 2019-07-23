/*
 * Copyright 2019 Stephane Nicolas
 * Copyright 2019 Daniel Molinero Reguera
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package toothpick.compiler.common.generators;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;
import toothpick.compiler.common.generators.targets.ParamInjectionTarget;

/** Common base interface for all code generators. */
public abstract class CodeGenerator {

  protected static final String LINE_SEPARATOR = System.getProperty("line.separator");
  protected Types typeUtil;

  public CodeGenerator(Types typeUtil) {
    this.typeUtil = typeUtil;
  }

  /**
   * Creates all java code.
   *
   * @return a string containing the java code generated by this {@link CodeGenerator}.
   */
  public abstract String brewJava();

  protected CodeBlock getInvokeScopeGetMethodWithNameCodeBlock(
      ParamInjectionTarget paramInjectionTarget) {
    final String scopeGetMethodName;
    final String injectionName;
    if (paramInjectionTarget.name == null) {
      injectionName = "";
    } else {
      injectionName = ", \"" + paramInjectionTarget.name.toString() + "\"";
    }
    final ClassName className;
    switch (paramInjectionTarget.kind) {
      case INSTANCE:
        scopeGetMethodName = "getInstance";
        className = ClassName.get(paramInjectionTarget.memberClass);
        break;
      case PROVIDER:
        scopeGetMethodName = "getProvider";
        className = ClassName.get(paramInjectionTarget.kindParamClass);
        break;
      case LAZY:
        scopeGetMethodName = "getLazy";
        className = ClassName.get(paramInjectionTarget.kindParamClass);
        break;
      default:
        throw new IllegalStateException("The kind can't be null.");
    }
    return CodeBlock.builder()
        .add("$L($T.class$L)", scopeGetMethodName, className, injectionName)
        .build();
  }

  protected TypeName getParamType(ParamInjectionTarget paramInjectionTarget) {
    if (paramInjectionTarget.kind == ParamInjectionTarget.Kind.INSTANCE) {
      return TypeName.get(typeUtil.erasure(paramInjectionTarget.memberClass.asType()));
    } else {
      return ParameterizedTypeName.get(
          ClassName.get(paramInjectionTarget.memberClass),
          ClassName.get(typeUtil.erasure(paramInjectionTarget.kindParamClass.asType())));
    }
  }

  /** @return the FQN of the code generated by this {@link CodeGenerator}. */
  public abstract String getFqcn();

  protected static String getGeneratedFQNClassName(TypeElement typeElement) {
    return getGeneratedPackageName(typeElement) + "." + getGeneratedSimpleClassName(typeElement);
  }

  protected static String getGeneratedSimpleClassName(TypeElement typeElement) {
    String result = typeElement.getSimpleName().toString();
    // deals with inner classes
    while (typeElement.getEnclosingElement().getKind() != ElementKind.PACKAGE) {
      result = typeElement.getEnclosingElement().getSimpleName().toString() + "$" + result;
      typeElement = (TypeElement) typeElement.getEnclosingElement();
    }
    return result;
  }

  protected static String getSimpleClassName(ClassName className) {
    String result = "";
    java.util.List<String> simpleNames = className.simpleNames();
    for (int i = 0; i < simpleNames.size(); i++) {
      String name = simpleNames.get(i);
      result += name;
      if (i != simpleNames.size() - 1) {
        result += ".";
      }
    }
    return result;
  }

  protected static String getGeneratedPackageName(TypeElement typeElement) {
    // deals with inner classes
    while (typeElement.getEnclosingElement().getKind() != ElementKind.PACKAGE) {
      typeElement = (TypeElement) typeElement.getEnclosingElement();
    }
    return typeElement.getEnclosingElement().toString();
  }
}
