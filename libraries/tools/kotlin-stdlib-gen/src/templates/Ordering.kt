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

package templates

import templates.Family.*
import templates.SequenceClass.*

object Ordering : TemplateGroupBase() {

    val f_reverse: FamilyPrimitiveMemberDefinition = fn("reverse()") {
        include(Lists, InvariantArraysOfObjects, ArraysOfPrimitives)
    } builder {
        doc { "Reverses ${f.element.pluralize()} in the ${f.collection} in-place." }
        returns("Unit")
        body {
            """
            val midPoint = (size / 2) - 1
            if (midPoint < 0) return
            var reverseIndex = lastIndex
            for (index in 0..midPoint) {
                val tmp = this[index]
                this[index] = this[reverseIndex]
                this[reverseIndex] = tmp
                reverseIndex--
            }
            """
        }
        specialFor(Lists) {
            receiver("MutableList<T>")
            on(Platform.JVM) {
                body { """java.util.Collections.reverse(this)""" }
            }
        }
    }

    val f_reversed: FamilyPrimitiveMemberDefinition = fn("reversed()") {
        include(Iterables, ArraysOfObjects, ArraysOfPrimitives, CharSequences, Strings)
    } builder {
        doc { "Returns a list with elements in reversed order." }
        returns("List<T>")
        body {
            """
            if (this is Collection && size <= 1) return toList()
            val list = toMutableList()
            list.reverse()
            return list
            """
        }

        body(ArraysOfObjects, ArraysOfPrimitives) {
            """
            if (isEmpty()) return emptyList()
            val list = toMutableList()
            list.reverse()
            return list
            """
        }

        specialFor(CharSequences, Strings) {
            returns("SELF")
            doc { "Returns a ${f.collection} with characters in reversed order." }
        }
        body(CharSequences) { "return StringBuilder(this).reverse()" }
        specialFor(Strings) { inlineOnly() }
        body(Strings) { "return (this as CharSequence).reversed().toString()" }

    }

    val f_reversedArray: FamilyPrimitiveMemberDefinition = fn("reversedArray()") {
        include(InvariantArraysOfObjects, ArraysOfPrimitives)
    } builder {
        doc { "Returns an array with elements of this array in reversed order." }
        returns("SELF")
        body(InvariantArraysOfObjects) {
            """
            if (isEmpty()) return this
            val result = arrayOfNulls(this, size)
            val lastIndex = lastIndex
            for (i in 0..lastIndex)
                result[lastIndex - i] = this[i]
            return result
            """
        }
        body(ArraysOfPrimitives) {
            """
            if (isEmpty()) return this
            val result = SELF(size)
            val lastIndex = lastIndex
            for (i in 0..lastIndex)
                result[lastIndex - i] = this[i]
            return result
            """
        }
    }

    val f_sorted: FamilyPrimitiveMemberDefinition = fn("sorted()") {
        includeDefault()
        exclude(PrimitiveType.Boolean)
    } builder {

        doc {
            """
            Returns a list of all elements sorted according to their natural sort order.
            """
        }
        returns("List<T>")
        typeParam("T : Comparable<T>")
        body {
            """
                if (this is Collection) {
                    if (size <= 1) return this.toList()
                    @Suppress("UNCHECKED_CAST")
                    return (toTypedArray<Comparable<T>>() as Array<T>).apply { sort() }.asList()
                }
                return toMutableList().apply { sort() }
            """
        }
        body(ArraysOfPrimitives) {
            """
            return toTypedArray().apply { sort() }.asList()
            """
        }
        body(ArraysOfObjects) {
            """
            return sortedArray().asList()
            """
        }

        specialFor(Sequences) {
            returns("SELF")
            doc {
                "Returns a sequence that yields elements of this sequence sorted according to their natural sort order."
            }
            sequenceClassification(intermediate, stateful)
        }
        body(Sequences) {
            """
            return object : Sequence<T> {
                override fun iterator(): Iterator<T> {
                    val sortedList = this@sorted.toMutableList()
                    sortedList.sort()
                    return sortedList.iterator()
                }
            }
            """
        }
    }

    val f_sortedArray: FamilyPrimitiveMemberDefinition = fn("sortedArray()") {
        include(InvariantArraysOfObjects, ArraysOfPrimitives)
        exclude(PrimitiveType.Boolean)
    } builder {
        doc {
            "Returns an array with all elements of this array sorted according to their natural sort order."
        }
        typeParam("T : Comparable<T>")
        returns("SELF")
        body {
            """
            if (isEmpty()) return this
            return this.copyOf().apply { sort() }
            """
        }
    }

    val f_sortDescending: FamilyPrimitiveMemberDefinition = fn("sortDescending()") {
        include(Lists, ArraysOfObjects, ArraysOfPrimitives)
        exclude(PrimitiveType.Boolean)
    } builder {
        doc { """Sorts elements in the ${f.collection} in-place descending according to their natural sort order.""" }
        returns("Unit")
        typeParam("T : Comparable<T>")
        specialFor(Lists) {
            receiver("MutableList<T>")
        }

        body { """sortWith(reverseOrder())""" }
        body(ArraysOfPrimitives) {
            """
                if (size > 1) {
                    sort()
                    reverse()
                }
            """
        }
    }

    val f_sortedDescending: FamilyPrimitiveMemberDefinition = fn("sortedDescending()") {
        includeDefault()
        exclude(PrimitiveType.Boolean)
    } builder {

        doc {
            """
            Returns a list of all elements sorted descending according to their natural sort order.
            """
        }
        returns("List<T>")
        typeParam("T : Comparable<T>")
        body {
            """
            return sortedWith(reverseOrder())
            """
        }
        body(ArraysOfPrimitives) {
            """
            return copyOf().apply { sort() }.reversed()
            """
        }

        specialFor(Sequences) {
            returns("SELF")
            doc {
                "Returns a sequence that yields elements of this sequence sorted descending according to their natural sort order."
            }
            sequenceClassification(intermediate, stateful)
        }
    }

    val f_sortedArrayDescending: FamilyPrimitiveMemberDefinition = fn("sortedArrayDescending()") {
        include(InvariantArraysOfObjects, ArraysOfPrimitives)
        exclude(PrimitiveType.Boolean)
    } builder {
        doc {
            "Returns an array with all elements of this array sorted descending according to their natural sort order."
        }
        typeParam("T : Comparable<T>")
        returns("SELF")
        body(InvariantArraysOfObjects) {
            """
            if (isEmpty()) return this
            return this.copyOf().apply { sortWith(reverseOrder()) }
            """
        }
        body(ArraysOfPrimitives) {
            """
            if (isEmpty()) return this
            return this.copyOf().apply { sortDescending() }
            """
        }
    }

    val f_sortedWith: FamilyPrimitiveMemberDefinition = fn("sortedWith(comparator: Comparator<in T>)") {
        includeDefault()
    } builder {
        returns("List<T>")
        doc {
            """
            Returns a list of all elements sorted according to the specified [comparator].
            """
        }
        body {
            """
             if (this is Collection) {
                if (size <= 1) return this.toList()
                @Suppress("UNCHECKED_CAST")
                return (toTypedArray<Any?>() as Array<T>).apply { sortWith(comparator) }.asList()
            }
            return toMutableList().apply { sortWith(comparator) }
            """
        }
        body(ArraysOfPrimitives) {
            """
            return toTypedArray().apply { sortWith(comparator) }.asList()
            """
        }
        body(ArraysOfObjects) {
            """
            return sortedArrayWith(comparator).asList()
            """
        }

        specialFor(Sequences) {
            returns("SELF")
            doc {
                "Returns a sequence that yields elements of this sequence sorted according to the specified [comparator]."
            }
            sequenceClassification(intermediate, stateful)
        }
        body(Sequences) {
            """
            return object : Sequence<T> {
                override fun iterator(): Iterator<T> {
                    val sortedList = this@sortedWith.toMutableList()
                    sortedList.sortWith(comparator)
                    return sortedList.iterator()
                }
            }
            """
        }
    }

    val f_sortedArrayWith: FamilyPrimitiveMemberDefinition = fn("sortedArrayWith(comparator: Comparator<in T>)") {
        include(ArraysOfObjects)
    } builder {
        doc {
            "Returns an array with all elements of this array sorted according the specified [comparator]."
        }
        returns("SELF")
        body {
            """
            if (isEmpty()) return this
            return this.copyOf().apply { sortWith(comparator) }
            """
        }
    }

    val f_sortBy: FamilyPrimitiveMemberDefinition = fn("sortBy(crossinline selector: (T) -> R?)") {
        include(Lists, ArraysOfObjects)
    } builder {
        inline()
        doc { """Sorts elements in the ${f.collection} in-place according to natural sort order of the value returned by specified [selector] function.""" }
        returns("Unit")
        typeParam("R : Comparable<R>")
        specialFor(Lists) { receiver("MutableList<T>") }

        body { """if (size > 1) sortWith(compareBy(selector))""" }
    }

    val f_sortedBy: FamilyPrimitiveMemberDefinition = fn("sortedBy(crossinline selector: (T) -> R?)") {
        includeDefault()
    } builder {
        inline()
        returns("List<T>")
        typeParam("R : Comparable<R>")

        doc {
            """
            Returns a list of all elements sorted according to natural sort order of the value returned by specified [selector] function.
            """
        }

        specialFor(Sequences) {
            returns("SELF")
            doc {
                "Returns a sequence that yields elements of this sequence sorted according to natural sort order of the value returned by specified [selector] function."
            }
            sequenceClassification(intermediate, stateful)
        }
        body {
            "return sortedWith(compareBy(selector))"
        }
    }

    val f_sortByDescending: FamilyPrimitiveMemberDefinition = fn("sortByDescending(crossinline selector: (T) -> R?)") {
        include(Lists, ArraysOfObjects)
    } builder {
        inline()
        doc { """Sorts elements in the ${f.collection} in-place descending according to natural sort order of the value returned by specified [selector] function.""" }
        returns("Unit")
        typeParam("R : Comparable<R>")
        specialFor(Lists) { receiver("MutableList<T>") }

        body {
            """if (size > 1) sortWith(compareByDescending(selector))""" }
    }

    val f_sortedByDescending: FamilyPrimitiveMemberDefinition = fn("sortedByDescending(crossinline selector: (T) -> R?)") {
        includeDefault()
    } builder {
        inline()
        returns("List<T>")
        typeParam("R : Comparable<R>")

        doc {
            """
            Returns a list of all elements sorted descending according to natural sort order of the value returned by specified [selector] function.
            """
        }

        specialFor(Sequences) {
            returns("SELF")
            doc {
                "Returns a sequence that yields elements of this sequence sorted descending according to natural sort order of the value returned by specified [selector] function."
            }
            sequenceClassification(intermediate, stateful)
        }

        body {
            "return sortedWith(compareByDescending(selector))"
        }
    }

}
