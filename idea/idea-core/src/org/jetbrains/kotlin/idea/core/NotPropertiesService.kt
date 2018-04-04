/*
 * Copyright 2010-2017 JetBrains s.r.o.
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

package org.jetbrains.kotlin.idea.core

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.name.FqNameUnsafe

interface NotPropertiesService {

    fun getNotProperties(element: PsiElement): Set<FqNameUnsafe>

    companion object {
        fun getInstance(project: Project): NotPropertiesService {
            return ServiceManager.getService(project, NotPropertiesService::class.java)
        }

        fun getNotProperties(element: PsiElement): Set<FqNameUnsafe> =
                getInstance(element.project).getNotProperties(element)
    }
}