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

package org.jetbrains.kotlin.idea.decompiler.js

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.kotlin.serialization.js.KotlinJavascriptSerializationUtil

object KotlinJavaScriptMetaFileType : FileType {

    override fun getName(): String = "KJSM"

    override fun getDescription(): String = "Kotlin JavaScript meta file"

    override fun getDefaultExtension(): String = KotlinJavascriptSerializationUtil.CLASS_METADATA_FILE_EXTENSION

    override fun getIcon(): Nothing? = null

    override fun isBinary(): Boolean = true

    override fun isReadOnly(): Boolean = true

    override fun getCharset(file: VirtualFile, content: ByteArray): Nothing? = null
}
