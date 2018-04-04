/*
 * Copyright 2010-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.idea.intentions

import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.idea.refactoring.changeSignature.KotlinChangeSignatureConfiguration
import org.jetbrains.kotlin.idea.refactoring.changeSignature.KotlinMethodDescriptor
import org.jetbrains.kotlin.idea.refactoring.changeSignature.modify
import org.jetbrains.kotlin.idea.refactoring.changeSignature.runChangeSignature
import org.jetbrains.kotlin.idea.refactoring.resolveToExpectedDescriptorIfPossible
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

class ConvertParameterToReceiverIntention : SelfTargetingIntention<KtParameter>(KtParameter::class.java, "Convert parameter to receiver") {
    override fun isApplicableTo(element: KtParameter, caretOffset: Int): Boolean {
        val identifier = element.nameIdentifier ?: return false
        if (!identifier.textRange.containsOffset(caretOffset)) return false
        if (element.isVarArg) return false
        val function = element.getStrictParentOfType<KtNamedFunction>() ?: return false
        return function.valueParameterList == element.parent && function.receiverTypeReference == null
    }

    private fun configureChangeSignature(parameterIndex: Int): KotlinChangeSignatureConfiguration {
        return object : KotlinChangeSignatureConfiguration {
            override fun configure(originalDescriptor: KotlinMethodDescriptor): KotlinMethodDescriptor {
                return originalDescriptor.modify { it.receiver = originalDescriptor.parameters[parameterIndex] }
            }

            override fun performSilently(affectedFunctions: Collection<PsiElement>) = true
        }
    }

    override fun startInWriteAction(): Boolean = false

    override fun applyTo(element: KtParameter, editor: Editor?) {
        val function = element.getStrictParentOfType<KtNamedFunction>() ?: return
        val parameterIndex = function.valueParameters.indexOf(element)
        val descriptor = function.resolveToExpectedDescriptorIfPossible() as? FunctionDescriptor ?: return
        runChangeSignature(element.project, descriptor, configureChangeSignature(parameterIndex), element, text)
    }
}
