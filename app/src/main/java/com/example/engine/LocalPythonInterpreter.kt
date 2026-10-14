package com.example.engine

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Local Python 3 Interpreter for Data Structures (List, Tuple, Set, Dict, Nested)
 * Specially designed for SMA Kelas XI Informatika curriculum.
 */
class LocalPythonInterpreter : PythonExecutionEngine {

    override suspend fun execute(code: String): ExecutionResult = withContext(Dispatchers.Default) {
        val startTime = System.currentTimeMillis()
        val stdoutBuilder = StringBuilder()
        val env = mutableMapOf<String, Any?>()

        try {
            executeLines(code.lines(), env, stdoutBuilder)

            val execTime = System.currentTimeMillis() - startTime
            ExecutionResult(
                stdout = stdoutBuilder.toString().trimEnd(),
                error = null,
                variables = env,
                executionTimeMs = execTime
            )
        } catch (e: Exception) {
            val execTime = System.currentTimeMillis() - startTime
            val errorMsg = e.message ?: "RuntimeError: Terjadi kesalahan saat mengeksekusi kode Python."
            ExecutionResult(
                stdout = stdoutBuilder.toString().trimEnd(),
                error = errorMsg,
                variables = env,
                executionTimeMs = execTime
            )
        }
    }

    private fun executeLines(lines: List<String>, env: MutableMap<String, Any?>, stdoutBuilder: StringBuilder) {
        var lineIdx = 0
        while (lineIdx < lines.size) {
            val rawLine = lines[lineIdx]
            val trimmed = rawLine.trim()

            // Skip comments and empty lines
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                lineIdx++
                continue
            }

            // Handle If-Else conditions
            if (trimmed.startsWith("if ") && trimmed.endsWith(":")) {
                val conditionExpr = trimmed.substring(3, trimmed.length - 1).trim()
                val condResult = isTruthy(evaluateExpression(conditionExpr, env))

                // Collect if-block
                val ifBlock = mutableListOf<String>()
                lineIdx++
                while (lineIdx < lines.size && (lines[lineIdx].startsWith("    ") || lines[lineIdx].startsWith("\t") || lines[lineIdx].trim().isEmpty())) {
                    if (lines[lineIdx].trim().isNotEmpty() && !lines[lineIdx].trim().startsWith("#")) {
                        ifBlock.add(lines[lineIdx].trim())
                    }
                    lineIdx++
                }

                // Check for optional else block
                val elseBlock = mutableListOf<String>()
                if (lineIdx < lines.size && lines[lineIdx].trim() == "else:") {
                    lineIdx++
                    while (lineIdx < lines.size && (lines[lineIdx].startsWith("    ") || lines[lineIdx].startsWith("\t") || lines[lineIdx].trim().isEmpty())) {
                        if (lines[lineIdx].trim().isNotEmpty() && !lines[lineIdx].trim().startsWith("#")) {
                            elseBlock.add(lines[lineIdx].trim())
                        }
                        lineIdx++
                    }
                }

                val blockToRun = if (condResult) ifBlock else elseBlock
                executeLines(blockToRun, env, stdoutBuilder)
                continue
            }

            // Handle for loop: for x in iterable: OR for k, v in dict.items():
            if (trimmed.startsWith("for ") && trimmed.endsWith(":")) {
                val loopMatch = Regex("""for\s+(.+)\s+in\s+(.+):""").find(trimmed)
                if (loopMatch != null) {
                    val loopVarsStr = loopMatch.groupValues[1].trim()
                    val iterableExpr = loopMatch.groupValues[2].trim()
                    val iterableVal = evaluateExpression(iterableExpr, env)

                    val blockLines = mutableListOf<String>()
                    lineIdx++
                    while (lineIdx < lines.size && (lines[lineIdx].startsWith("    ") || lines[lineIdx].startsWith("\t") || lines[lineIdx].trim().isEmpty())) {
                        if (lines[lineIdx].trim().isNotEmpty() && !lines[lineIdx].trim().startsWith("#")) {
                            blockLines.add(lines[lineIdx].trim())
                        }
                        lineIdx++
                    }

                    val items: List<Any?> = when (iterableVal) {
                        is List<*> -> iterableVal
                        is PythonTuple -> iterableVal.items
                        is Set<*> -> iterableVal.toList()
                        is Map<*, *> -> iterableVal.keys.toList()
                        is PythonDictKeys -> iterableVal.keys
                        is PythonDictValues -> iterableVal.values
                        is PythonDictItems -> iterableVal.items
                        is String -> iterableVal.map { it.toString() }
                        is PythonRange -> iterableVal.toList()
                        else -> throw RuntimeException("TypeError: '$iterableExpr' is not iterable")
                    }

                    val loopVars = loopVarsStr.split(",").map { it.trim() }

                    for (item in items) {
                        if (loopVars.size == 1) {
                            env[loopVars[0]] = item
                        } else if (loopVars.size > 1) {
                            val tupleItems = when (item) {
                                is PythonTuple -> item.items
                                is List<*> -> item
                                else -> listOf(item)
                            }
                            for (i in loopVars.indices) {
                                env[loopVars[i]] = tupleItems.getOrNull(i)
                            }
                        }
                        executeLines(blockLines, env, stdoutBuilder)
                    }
                    continue
                }
            }

            // Multi-line statement accumulation (parentheses, brackets, braces)
            var statement = trimmed
            var bracketDepth = countBracketDepth(statement)
            while (bracketDepth > 0 && lineIdx + 1 < lines.size) {
                lineIdx++
                val nextLine = lines[lineIdx].trim()
                if (nextLine.isNotEmpty() && !nextLine.startsWith("#")) {
                    statement += " " + removeInlineComments(nextLine)
                    bracketDepth = countBracketDepth(statement)
                }
            }

            executeStatement(statement, env, stdoutBuilder)
            lineIdx++
        }
    }

    private fun countBracketDepth(str: String): Int {
        var depth = 0
        var inQuote = false
        var quoteChar = ' '
        var i = 0
        while (i < str.length) {
            val c = str[i]
            if (inQuote) {
                if (c == quoteChar) inQuote = false
            } else {
                when (c) {
                    '"', '\'' -> {
                        inQuote = true
                        quoteChar = c
                    }
                    '(', '[', '{' -> depth++
                    ')', ']', '}' -> depth--
                    '#' -> break
                }
            }
            i++
        }
        return depth
    }

    private fun executeStatement(line: String, env: MutableMap<String, Any?>, stdout: StringBuilder) {
        val cleanLine = removeInlineComments(line)
        if (cleanLine.isEmpty()) return

        // 1. print statement
        if (cleanLine.startsWith("print(") && cleanLine.endsWith(")")) {
            val inner = cleanLine.substring(6, cleanLine.length - 1).trim()
            if (inner.isEmpty()) {
                stdout.append("\n")
                return
            }
            val args = splitTopLevel(inner, ',')
            val outputs = args.map { arg ->
                val evalVal = evaluateExpression(arg.trim(), env)
                formatPythonOutput(evalVal)
            }
            stdout.append(outputs.joinToString(" ")).append("\n")
            return
        }

        // 2. Augmented assignment: var += expr, var -= expr, etc.
        val augMatch = Regex("""^([a-zA-Z_]\w*)\s*(\+=|-=|\*=|/=|//=)\s*(.+)$""").find(cleanLine)
        if (augMatch != null) {
            val varName = augMatch.groupValues[1]
            val op = augMatch.groupValues[2]
            val expr = augMatch.groupValues[3].trim()
            val oldVal = env[varName] ?: throw RuntimeException("NameError: name '$varName' is not defined")
            val evalExpr = evaluateExpression(expr, env)
            val newVal = when (op) {
                "+=" -> performAddition(oldVal, evalExpr)
                "-=" -> performSubtraction(oldVal, evalExpr)
                "*=" -> performMultiplication(oldVal, evalExpr)
                "/=" -> performDivision(oldVal, evalExpr)
                "//=" -> performFloorDivision(oldVal, evalExpr)
                else -> evalExpr
            }
            env[varName] = newVal
            return
        }

        // 3. Item assignment: var[idx] = val or var[k1][k2] = val
        val itemAssignMatch = Regex("""^([a-zA-Z_]\w*)(\[.+\])\s*=\s*(.+)$""").find(cleanLine)
        if (itemAssignMatch != null) {
            val rootVar = itemAssignMatch.groupValues[1]
            val brackets = itemAssignMatch.groupValues[2]
            val valueExpr = itemAssignMatch.groupValues[3].trim()
            val newVal = evaluateExpression(valueExpr, env)

            assignToSubscript(rootVar, brackets, newVal, env)
            return
        }

        // 4. del statement: del dict[key] or del list[i]
        if (cleanLine.startsWith("del ")) {
            val target = cleanLine.substring(4).trim()
            val delMatch = Regex("""^([a-zA-Z_]\w*)\[([^\]]+)\]$""").find(target)
            if (delMatch != null) {
                val targetVar = delMatch.groupValues[1]
                val keyExpr = delMatch.groupValues[2].trim()
                val targetObj = env[targetVar] ?: throw RuntimeException("NameError: name '$targetVar' is not defined")
                val keyVal = evaluateExpression(keyExpr, env)

                when (targetObj) {
                    is MutableMap<*, *> -> {
                        val map = targetObj as MutableMap<Any?, Any?>
                        if (!map.containsKey(keyVal)) throw RuntimeException("KeyError: '$keyVal'")
                        map.remove(keyVal)
                    }
                    is MutableList<*> -> {
                        val list = targetObj as MutableList<Any?>
                        val idx = (keyVal as? Number)?.toInt() ?: throw RuntimeException("TypeError: list index must be integer")
                        val actualIdx = if (idx < 0) idx + list.size else idx
                        if (actualIdx < 0 || actualIdx >= list.size) throw RuntimeException("IndexError: list index out of range")
                        list.removeAt(actualIdx)
                    }
                    is PythonTuple -> throw RuntimeException("TypeError: 'tuple' object doesn't support item deletion (Tuples are Immutable)")
                    else -> throw RuntimeException("TypeError: '$targetVar' does not support deletion")
                }
                return
            }
        }

        // 5. Assignment: var = val or a, b = (1, 2)
        if (cleanLine.contains("=") && !cleanLine.contains("==") && !cleanLine.contains("!=") && !cleanLine.contains("<=") && !cleanLine.contains(">=")) {
            val parts = splitTopLevel(cleanLine, '=')
            if (parts.size == 2) {
                val left = parts[0].trim()
                val right = parts[1].trim()
                val evaluatedRight = evaluateExpression(right, env)

                if (left.contains(",")) {
                    val targets = left.split(",").map { it.trim() }
                    val values = when (evaluatedRight) {
                        is PythonTuple -> evaluatedRight.items
                        is List<*> -> evaluatedRight
                        is Set<*> -> evaluatedRight.toList()
                        else -> throw RuntimeException("TypeError: cannot unpack non-iterable object of type ${evaluatedRight?.javaClass?.simpleName}")
                    }
                    if (targets.size != values.size) {
                        throw RuntimeException("ValueError: too many/few values to unpack (expected ${targets.size}, got ${values.size})")
                    }
                    for (i in targets.indices) {
                        env[targets[i]] = values[i]
                    }
                    return
                } else if (Regex("""^[a-zA-Z_]\w*$""").matches(left)) {
                    env[left] = evaluatedRight
                    return
                }
            }
        }

        // 6. Standalone Method calls: list.append(), dict.update(), set.add(), target[k].append(), etc.
        val methodCallMatch = Regex("""^(.+)\.([a-zA-Z_]\w*)\((.*)\)$""").find(cleanLine)
        if (methodCallMatch != null) {
            val targetExpr = methodCallMatch.groupValues[1].trim()
            val methodName = methodCallMatch.groupValues[2]
            val rawArgs = methodCallMatch.groupValues[3].trim()

            val targetObj = evaluateExpression(targetExpr, env)
            val args = if (rawArgs.isEmpty()) emptyList() else splitTopLevel(rawArgs, ',').map { evaluateExpression(it.trim(), env) }

            if (targetObj != null) {
                executeMethodOnObject(targetExpr, targetObj, methodName, args, env)
                return
            }
        }

        // 7. Standalone expression evaluation
        evaluateExpression(cleanLine, env)
    }

    private fun assignToSubscript(rootVar: String, bracketsString: String, newVal: Any?, env: MutableMap<String, Any?>) {
        val rootObj = env[rootVar] ?: throw RuntimeException("NameError: name '$rootVar' is not defined")

        val tokens = parseBracketTokens(bracketsString)
        if (tokens.isEmpty()) return

        var curr: Any? = rootObj
        for (i in 0 until tokens.size - 1) {
            val token = tokens[i]
            val keyOrIdx = evaluateExpression(token, env)
            curr = when (curr) {
                is MutableList<*> -> {
                    val intIdx = (keyOrIdx as? Number)?.toInt() ?: throw RuntimeException("TypeError: list indices must be integers")
                    val list = curr as MutableList<Any?>
                    val actual = if (intIdx < 0) intIdx + list.size else intIdx
                    if (actual < 0 || actual >= list.size) throw RuntimeException("IndexError: list index out of range")
                    list[actual]
                }
                is MutableMap<*, *> -> {
                    val map = curr as MutableMap<Any?, Any?>
                    map[keyOrIdx] ?: throw RuntimeException("KeyError: '$keyOrIdx'")
                }
                is PythonTuple -> throw RuntimeException("TypeError: 'tuple' object does not support item assignment (Tuples are Immutable)")
                else -> throw RuntimeException("TypeError: '$curr' is not subscriptable")
            }
        }

        val lastToken = tokens.last()
        val lastKeyOrIdx = evaluateExpression(lastToken, env)

        when (curr) {
            is PythonTuple -> {
                throw RuntimeException("TypeError: 'tuple' object does not support item assignment (Tuples are Immutable)")
            }
            is MutableList<*> -> {
                val list = curr as MutableList<Any?>
                val intIdx = (lastKeyOrIdx as? Number)?.toInt() ?: throw RuntimeException("TypeError: list indices must be integers, not ${lastKeyOrIdx?.javaClass?.simpleName}")
                val actualIdx = if (intIdx < 0) intIdx + list.size else intIdx
                if (actualIdx < 0 || actualIdx >= list.size) {
                    throw RuntimeException("IndexError: list assignment index out of range")
                }
                list[actualIdx] = newVal
            }
            is MutableMap<*, *> -> {
                val map = curr as MutableMap<Any?, Any?>
                map[lastKeyOrIdx] = newVal
            }
            else -> {
                throw RuntimeException("TypeError: object does not support item assignment")
            }
        }
    }

    private fun executeMethodOnObject(varName: String, obj: Any?, method: String, args: List<Any?>, env: MutableMap<String, Any?>) {
        when (obj) {
            is MutableList<*> -> {
                val list = obj as MutableList<Any?>
                when (method) {
                    "append" -> {
                        if (args.isEmpty()) throw RuntimeException("TypeError: append() takes exactly one argument (0 given)")
                        list.add(args[0])
                    }
                    "insert" -> {
                        if (args.size < 2) throw RuntimeException("TypeError: insert() takes exactly 2 arguments")
                        val idx = (args[0] as? Number)?.toInt() ?: 0
                        val pos = idx.coerceIn(0, list.size)
                        list.add(pos, args[1])
                    }
                    "extend" -> {
                        if (args.isEmpty()) throw RuntimeException("TypeError: extend() takes exactly one argument")
                        when (val ext = args[0]) {
                            is List<*> -> list.addAll(ext as Collection<Any?>)
                            is PythonTuple -> list.addAll(ext.items)
                            is Set<*> -> list.addAll(ext as Collection<Any?>)
                            else -> throw RuntimeException("TypeError: '$ext' object is not iterable")
                        }
                    }
                    "remove" -> {
                        if (args.isEmpty()) throw RuntimeException("TypeError: remove() takes exactly one argument")
                        if (!list.contains(args[0])) throw RuntimeException("ValueError: list.remove(x): x not in list")
                        list.remove(args[0])
                    }
                    "pop" -> {
                        if (list.isEmpty()) throw RuntimeException("IndexError: pop from empty list")
                        val idx = if (args.isNotEmpty()) (args[0] as? Number)?.toInt() ?: (list.size - 1) else list.size - 1
                        val actualIdx = if (idx < 0) idx + list.size else idx
                        if (actualIdx < 0 || actualIdx >= list.size) throw RuntimeException("IndexError: pop index out of range")
                        list.removeAt(actualIdx)
                    }
                    "clear" -> list.clear()
                    "sort" -> {
                        val reverse = args.any { it == true || it.toString().contains("reverse=True") }
                        try {
                            val sorted = list.sortedWith(Comparator { a, b ->
                                comparePythonObjects(a, b)
                            })
                            list.clear()
                            if (reverse) list.addAll(sorted.reversed()) else list.addAll(sorted)
                        } catch (e: Exception) {
                            throw RuntimeException("TypeError: '<' not supported between instances in list")
                        }
                    }
                    "reverse" -> list.reverse()
                    "count" -> { /* returns count */ }
                    "index" -> {
                        val idx = list.indexOf(args.getOrNull(0))
                        if (idx == -1) throw RuntimeException("ValueError: '${args.getOrNull(0)}' is not in list")
                    }
                    else -> throw RuntimeException("AttributeError: 'list' object has no attribute '$method'")
                }
            }
            is PythonTuple -> {
                when (method) {
                    "count", "index" -> { /* Valid read-only tuple methods */ }
                    "append", "insert", "remove", "pop", "sort", "reverse", "extend", "clear" -> {
                        throw RuntimeException("AttributeError: 'tuple' object has no attribute '$method' (Tuple bersifat IMMUTABLE / tidak dapat diubah)")
                    }
                    else -> throw RuntimeException("AttributeError: 'tuple' object has no attribute '$method'")
                }
            }
            is MutableSet<*> -> {
                val set = obj as MutableSet<Any?>
                when (method) {
                    "add" -> {
                        if (args.isEmpty()) throw RuntimeException("TypeError: add() takes exactly one argument")
                        set.add(args[0])
                    }
                    "remove" -> {
                        if (args.isEmpty()) throw RuntimeException("TypeError: remove() takes exactly one argument")
                        if (!set.contains(args[0])) throw RuntimeException("KeyError: ${args[0]}")
                        set.remove(args[0])
                    }
                    "discard" -> {
                        if (args.isNotEmpty()) set.remove(args[0])
                    }
                    "pop" -> {
                        if (set.isEmpty()) throw RuntimeException("KeyError: 'pop from an empty set'")
                        val elem = set.first()
                        set.remove(elem)
                    }
                    "clear" -> set.clear()
                    "update" -> {
                        for (arg in args) {
                            when (arg) {
                                is Set<*> -> set.addAll(arg as Collection<Any?>)
                                is List<*> -> set.addAll(arg as Collection<Any?>)
                                is PythonTuple -> set.addAll(arg.items)
                            }
                        }
                    }
                    "union", "intersection", "difference", "symmetric_difference", "issubset", "issuperset" -> {
                        // Handled in evaluateExpression if expression returns new set
                    }
                    else -> throw RuntimeException("AttributeError: 'set' object has no attribute '$method'")
                }
            }
            is MutableMap<*, *> -> {
                val map = obj as MutableMap<Any?, Any?>
                when (method) {
                    "update" -> {
                        if (args.isNotEmpty() && args[0] is Map<*, *>) {
                            map.putAll(args[0] as Map<Any?, Any?>)
                        }
                    }
                    "clear" -> map.clear()
                    "pop" -> {
                        val k = args.getOrNull(0)
                        if (!map.containsKey(k)) throw RuntimeException("KeyError: '$k'")
                        map.remove(k)
                    }
                    "get", "keys", "values", "items" -> { /* evaluated expression */ }
                    else -> throw RuntimeException("AttributeError: 'dict' object has no attribute '$method'")
                }
            }
            else -> throw RuntimeException("AttributeError: object has no attribute '$method'")
        }
    }

    private fun evaluateExpression(expr: String, env: Map<String, Any?>): Any? {
        val trimmed = expr.trim()
        if (trimmed.isEmpty()) return null

        // 1. Python F-Strings: f"..." or f'...'
        if ((trimmed.startsWith("f\"") && trimmed.endsWith("\"")) || (trimmed.startsWith("f'") && trimmed.endsWith("'"))) {
            val content = trimmed.substring(2, trimmed.length - 1)
            return evaluateFString(content, env)
        }

        // 2. String literal: "..." or '...'
        if ((trimmed.startsWith("\"") && trimmed.endsWith("\"")) || (trimmed.startsWith("'") && trimmed.endsWith("'"))) {
            return trimmed.substring(1, trimmed.length - 1)
        }

        // 3. Literals: Number, Boolean, None
        trimmed.toLongOrNull()?.let { return it }
        trimmed.toDoubleOrNull()?.let { return it }
        if (trimmed == "True") return true
        if (trimmed == "False") return false
        if (trimmed == "None") return null

        // 4. Parenthesized expression: (expr)
        if (trimmed.startsWith("(") && trimmed.endsWith(")")) {
            val inner = trimmed.substring(1, trimmed.length - 1).trim()
            val parts = splitTopLevel(inner, ',')
            if (parts.size == 1 && !inner.endsWith(",")) {
                return evaluateExpression(parts[0], env)
            }
            // Tuple literal
            return PythonTuple(parts.map { evaluateExpression(it.trim(), env) })
        }

        // 5. Logical operators: 'or', 'and'
        val orParts = splitTopLevelByKeywords(trimmed, "or")
        if (orParts.size > 1) {
            for (p in orParts) {
                val res = evaluateExpression(p.trim(), env)
                if (isTruthy(res)) return res
            }
            return evaluateExpression(orParts.last().trim(), env)
        }

        val andParts = splitTopLevelByKeywords(trimmed, "and")
        if (andParts.size > 1) {
            var lastRes: Any? = true
            for (p in andParts) {
                lastRes = evaluateExpression(p.trim(), env)
                if (!isTruthy(lastRes)) return lastRes
            }
            return lastRes
        }

        // 6. Logical 'not'
        if (trimmed.startsWith("not ")) {
            val inner = trimmed.substring(4).trim()
            return !isTruthy(evaluateExpression(inner, env))
        }

        // 7. Comparison operators: ==, !=, <=, >=, <, >, in, not in
        val compResult = evaluateComparisons(trimmed, env)
        if (compResult != null) return compResult

        // 8. Binary arithmetic / Set operations: +, -, *, /, //, %, **, |, &, ^
        val binResult = evaluateBinaryOps(trimmed, env)
        if (binResult != null) return binResult

        // 9. Built-in functions
        val builtinResult = evaluateBuiltinFunctions(trimmed, env)
        if (builtinResult != null) return builtinResult

        // 10. List literal: [...]
        if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
            val inner = trimmed.substring(1, trimmed.length - 1).trim()
            if (inner.isEmpty()) return mutableListOf<Any?>()
            val parts = splitTopLevel(inner, ',')
            return parts.map { evaluateExpression(it.trim(), env) }.toMutableList()
        }

        // 11. Set or Dict literal: {...}
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            val inner = trimmed.substring(1, trimmed.length - 1).trim()
            if (inner.isEmpty()) return mutableMapOf<Any?, Any?>() // {} is empty dict in Python

            val parts = splitTopLevel(inner, ',')
            val isDict = parts.any { splitTopLevel(it, ':').size == 2 }
            if (isDict) {
                val map = mutableMapOf<Any?, Any?>()
                for (pair in parts) {
                    val kv = splitTopLevel(pair, ':')
                    if (kv.size != 2) throw RuntimeException("SyntaxError: invalid syntax in dictionary literal")
                    val key = evaluateExpression(kv[0].trim(), env)
                    val value = evaluateExpression(kv[1].trim(), env)
                    map[key] = value
                }
                return map
            } else {
                val set = mutableSetOf<Any?>()
                for (elem in parts) {
                    set.add(evaluateExpression(elem.trim(), env))
                }
                return set
            }
        }

        // 12. Subscript / Nested lookup / Slicing: var[...][...]
        if (trimmed.contains("[") && trimmed.endsWith("]")) {
            return evaluateNestedAccessOrSlice(trimmed, env)
        }

        // 13. Method call: obj.method(args)
        val methodCallMatch = Regex("""^(.+)\.([a-zA-Z_]\w*)\((.*)\)$""").find(trimmed)
        if (methodCallMatch != null) {
            val targetExpr = methodCallMatch.groupValues[1].trim()
            val methodName = methodCallMatch.groupValues[2]
            val rawArgs = methodCallMatch.groupValues[3].trim()
            val targetObj = evaluateExpression(targetExpr, env)
            val args = if (rawArgs.isEmpty()) emptyList() else splitTopLevel(rawArgs, ',').map { evaluateExpression(it.trim(), env) }

            return evaluateMethodReturn(targetObj, methodName, args)
        }

        // 14. Variable lookup
        if (env.containsKey(trimmed)) {
            return env[trimmed]
        }

        throw RuntimeException("NameError: name '$trimmed' is not defined")
    }

    private fun evaluateFString(content: String, env: Map<String, Any?>): String {
        val sb = StringBuilder()
        var i = 0
        while (i < content.length) {
            if (content[i] == '{') {
                val end = content.indexOf('}', i)
                if (end == -1) {
                    sb.append(content[i])
                    i++
                } else {
                    val expr = content.substring(i + 1, end).trim()
                    val evalVal = evaluateExpression(expr, env)
                    sb.append(formatPythonOutput(evalVal))
                    i = end + 1
                }
            } else {
                sb.append(content[i])
                i++
            }
        }
        return sb.toString()
    }

    private fun evaluateBuiltinFunctions(trimmed: String, env: Map<String, Any?>): Any? {
        val callMatch = Regex("""^([a-zA-Z_]\w*)\((.*)\)$""").find(trimmed) ?: return null
        val fnName = callMatch.groupValues[1]
        val rawArgs = callMatch.groupValues[2].trim()
        val args = if (rawArgs.isEmpty()) emptyList() else splitTopLevel(rawArgs, ',').map { evaluateExpression(it.trim(), env) }

        return when (fnName) {
            "len" -> {
                val arg = args.getOrNull(0)
                when (arg) {
                    is List<*> -> arg.size.toLong()
                    is PythonTuple -> arg.items.size.toLong()
                    is Set<*> -> arg.size.toLong()
                    is Map<*, *> -> arg.size.toLong()
                    is String -> arg.length.toLong()
                    is PythonDictKeys -> arg.keys.size.toLong()
                    is PythonDictValues -> arg.values.size.toLong()
                    is PythonDictItems -> arg.items.size.toLong()
                    is PythonRange -> arg.toList().size.toLong()
                    else -> throw RuntimeException("TypeError: object of type '${arg?.javaClass?.simpleName}' has no len()")
                }
            }
            "sum" -> {
                val arg = args.getOrNull(0)
                val items = when (arg) {
                    is List<*> -> arg
                    is PythonTuple -> arg.items
                    is Set<*> -> arg.toList()
                    is PythonDictValues -> arg.values
                    is PythonRange -> arg.toList()
                    else -> throw RuntimeException("TypeError: sum() arg must be an iterable of numbers")
                }
                val sumVal = items.filterIsInstance<Number>().sumOf { it.toDouble() }
                if (sumVal % 1.0 == 0.0) sumVal.toLong() else sumVal
            }
            "max" -> {
                val items = if (args.size == 1 && args[0] is List<*>) (args[0] as List<*>)
                else if (args.size == 1 && args[0] is PythonTuple) (args[0] as PythonTuple).items
                else if (args.size == 1 && args[0] is Set<*>) (args[0] as Set<*>).toList()
                else if (args.size == 1 && args[0] is PythonDictValues) (args[0] as PythonDictValues).values
                else if (args.size == 1 && args[0] is PythonDictKeys) (args[0] as PythonDictKeys).keys
                else if (args.size == 1 && args[0] is PythonRange) (args[0] as PythonRange).toList()
                else args

                if (items.isEmpty()) throw RuntimeException("ValueError: max() arg is an empty sequence")
                items.maxWithOrNull(Comparator { a, b -> comparePythonObjects(a, b) })
            }
            "min" -> {
                val items = if (args.size == 1 && args[0] is List<*>) (args[0] as List<*>)
                else if (args.size == 1 && args[0] is PythonTuple) (args[0] as PythonTuple).items
                else if (args.size == 1 && args[0] is Set<*>) (args[0] as Set<*>).toList()
                else if (args.size == 1 && args[0] is PythonDictValues) (args[0] as PythonDictValues).values
                else if (args.size == 1 && args[0] is PythonDictKeys) (args[0] as PythonDictKeys).keys
                else if (args.size == 1 && args[0] is PythonRange) (args[0] as PythonRange).toList()
                else args

                if (items.isEmpty()) throw RuntimeException("ValueError: min() arg is an empty sequence")
                items.minWithOrNull(Comparator { a, b -> comparePythonObjects(a, b) })
            }
            "sorted" -> {
                val items = when (val arg = args.getOrNull(0)) {
                    is List<*> -> arg
                    is PythonTuple -> arg.items
                    is Set<*> -> arg.toList()
                    is Map<*, *> -> arg.keys.toList()
                    is PythonDictKeys -> arg.keys
                    is PythonDictValues -> arg.values
                    is PythonDictItems -> arg.items.map { it.items }
                    is PythonRange -> arg.toList()
                    is String -> arg.map { it.toString() }
                    else -> throw RuntimeException("TypeError: '${arg?.javaClass?.simpleName}' object is not iterable")
                }
                val reverse = rawArgs.contains("reverse=True")
                val sorted = items.sortedWith(Comparator { a, b -> comparePythonObjects(a, b) })
                if (reverse) sorted.reversed().toMutableList() else sorted.toMutableList()
            }
            "type" -> {
                val arg = args.getOrNull(0)
                when (arg) {
                    is List<*> -> "<class 'list'>"
                    is PythonTuple -> "<class 'tuple'>"
                    is Set<*> -> "<class 'set'>"
                    is Map<*, *> -> "<class 'dict'>"
                    is PythonDictKeys -> "<class 'dict_keys'>"
                    is PythonDictValues -> "<class 'dict_values'>"
                    is PythonDictItems -> "<class 'dict_items'>"
                    is PythonRange -> "<class 'range'>"
                    is String -> "<class 'str'>"
                    is Long, is Int -> "<class 'int'>"
                    is Double, is Float -> "<class 'float'>"
                    is Boolean -> "<class 'bool'>"
                    else -> "<class 'NoneType'>"
                }
            }
            "str" -> args.getOrNull(0)?.let { formatPythonOutput(it) } ?: ""
            "int" -> {
                val arg = args.getOrNull(0)
                when (arg) {
                    is Number -> arg.toLong()
                    is String -> arg.trim().toLongOrNull() ?: (arg.trim().toDoubleOrNull()?.toLong() ?: throw RuntimeException("ValueError: invalid literal for int()"))
                    is Boolean -> if (arg) 1L else 0L
                    else -> throw RuntimeException("TypeError: int() argument must be a string or a number")
                }
            }
            "float" -> {
                val arg = args.getOrNull(0)
                when (arg) {
                    is Number -> arg.toDouble()
                    is String -> arg.trim().toDoubleOrNull() ?: throw RuntimeException("ValueError: could not convert string to float")
                    is Boolean -> if (arg) 1.0 else 0.0
                    else -> throw RuntimeException("TypeError: float() argument must be a string or a number")
                }
            }
            "bool" -> isTruthy(args.getOrNull(0))
            "list" -> {
                when (val arg = args.getOrNull(0)) {
                    null -> mutableListOf<Any?>()
                    is List<*> -> arg.toMutableList()
                    is PythonTuple -> arg.items.toMutableList()
                    is Set<*> -> arg.toMutableList()
                    is Map<*, *> -> arg.keys.toMutableList()
                    is PythonDictKeys -> arg.keys.toMutableList()
                    is PythonDictValues -> arg.values.toMutableList()
                    is PythonDictItems -> arg.items.map { it.items }.toMutableList()
                    is PythonRange -> arg.toList().toMutableList()
                    is String -> arg.map { it.toString() }.toMutableList()
                    else -> throw RuntimeException("TypeError: '$arg' object is not iterable")
                }
            }
            "tuple" -> {
                when (val arg = args.getOrNull(0)) {
                    null -> PythonTuple(emptyList())
                    is List<*> -> PythonTuple(arg)
                    is PythonTuple -> arg
                    is Set<*> -> PythonTuple(arg.toList())
                    is Map<*, *> -> PythonTuple(arg.keys.toList())
                    is PythonDictKeys -> PythonTuple(arg.keys)
                    is PythonDictValues -> PythonTuple(arg.values)
                    is PythonDictItems -> PythonTuple(arg.items)
                    is PythonRange -> PythonTuple(arg.toList())
                    is String -> PythonTuple(arg.map { it.toString() })
                    else -> throw RuntimeException("TypeError: '$arg' object is not iterable")
                }
            }
            "set" -> {
                when (val arg = args.getOrNull(0)) {
                    null -> mutableSetOf<Any?>()
                    is List<*> -> arg.toMutableSet()
                    is PythonTuple -> arg.items.toMutableSet()
                    is Set<*> -> arg.toMutableSet()
                    is Map<*, *> -> arg.keys.toMutableSet()
                    is PythonDictKeys -> arg.keys.toMutableSet()
                    is PythonDictValues -> arg.values.toMutableSet()
                    is PythonDictItems -> arg.items.toMutableSet()
                    is PythonRange -> arg.toList().toMutableSet()
                    is String -> arg.map { it.toString() }.toMutableSet()
                    else -> throw RuntimeException("TypeError: '$arg' object is not iterable")
                }
            }
            "dict" -> {
                when (val arg = args.getOrNull(0)) {
                    null -> mutableMapOf<Any?, Any?>()
                    is Map<*, *> -> arg.toMutableMap()
                    is PythonDictItems -> arg.items.associate {
                        val k = it.items.getOrNull(0)
                        val v = it.items.getOrNull(1)
                        k to v
                    }.toMutableMap()
                    is List<*> -> {
                        val map = mutableMapOf<Any?, Any?>()
                        for (item in arg) {
                            if (item is PythonTuple && item.items.size == 2) {
                                map[item.items[0]] = item.items[1]
                            } else if (item is List<*> && item.size == 2) {
                                map[item[0]] = item[1]
                            }
                        }
                        map
                    }
                    else -> mutableMapOf<Any?, Any?>()
                }
            }
            "range" -> {
                when (args.size) {
                    1 -> {
                        val stop = (args[0] as? Number)?.toInt() ?: 0
                        PythonRange(0, stop, 1)
                    }
                    2 -> {
                        val start = (args[0] as? Number)?.toInt() ?: 0
                        val stop = (args[1] as? Number)?.toInt() ?: 0
                        PythonRange(start, stop, 1)
                    }
                    3 -> {
                        val start = (args[0] as? Number)?.toInt() ?: 0
                        val stop = (args[1] as? Number)?.toInt() ?: 0
                        val step = (args[2] as? Number)?.toInt() ?: 1
                        PythonRange(start, stop, step)
                    }
                    else -> PythonRange(0, 0, 1)
                }
            }
            "abs" -> {
                val arg = args.getOrNull(0)
                if (arg is Number) Math.abs(arg.toDouble()).let { if (it % 1.0 == 0.0) it.toLong() else it }
                else throw RuntimeException("TypeError: bad operand type for abs()")
            }
            else -> null
        }
    }

    private fun evaluateComparisons(trimmed: String, env: Map<String, Any?>): Boolean? {
        val operators = listOf("==", "!=", "<=", ">=", "<", ">", " not in ", " in ")
        for (op in operators) {
            val parts = splitTopLevelByOperator(trimmed, op.trim())
            if (parts.size == 2) {
                val left = evaluateExpression(parts[0].trim(), env)
                val right = evaluateExpression(parts[1].trim(), env)
                val opName = op.trim()
                return when (opName) {
                    "==" -> compareEquals(left, right)
                    "!=" -> !compareEquals(left, right)
                    "<" -> comparePythonObjects(left, right) < 0
                    "<=" -> comparePythonObjects(left, right) <= 0
                    ">" -> comparePythonObjects(left, right) > 0
                    ">=" -> comparePythonObjects(left, right) >= 0
                    "in" -> checkMembership(left, right)
                    "not in" -> !checkMembership(left, right)
                    else -> false
                }
            }
        }
        return null
    }

    private fun checkMembership(item: Any?, container: Any?): Boolean {
        return when (container) {
            is List<*> -> container.contains(item)
            is PythonTuple -> container.items.contains(item)
            is Set<*> -> container.contains(item)
            is Map<*, *> -> container.containsKey(item)
            is PythonDictKeys -> container.keys.contains(item)
            is PythonDictValues -> container.values.contains(item)
            is PythonDictItems -> container.items.any { it.items.getOrNull(0) == item || it == item }
            is String -> container.contains(item.toString())
            is PythonRange -> container.toList().contains(item)
            else -> false
        }
    }

    private fun evaluateBinaryOps(trimmed: String, env: Map<String, Any?>): Any? {
        // Evaluate additions / subtractions / set ops
        val ops = listOf("+", "-", "*", "//", "/", "%", "|", "&", "^")
        for (op in ops) {
            val parts = splitTopLevel(trimmed, op[0])
            if (parts.size == 2 && (op.length == 1 || trimmed.contains(op))) {
                val left = evaluateExpression(parts[0].trim(), env)
                val right = evaluateExpression(parts[1].trim(), env)
                return when (op) {
                    "+" -> performAddition(left, right)
                    "-" -> performSubtraction(left, right)
                    "*" -> performMultiplication(left, right)
                    "/" -> performDivision(left, right)
                    "//" -> performFloorDivision(left, right)
                    "%" -> performModulo(left, right)
                    "|" -> performSetOrBitwiseOr(left, right)
                    "&" -> performSetOrBitwiseAnd(left, right)
                    "^" -> performSetOrBitwiseXor(left, right)
                    else -> null
                }
            }
        }
        return null
    }

    private fun performAddition(a: Any?, b: Any?): Any? {
        if (a is Number && b is Number) {
            val res = a.toDouble() + b.toDouble()
            return if (res % 1.0 == 0.0 && a !is Double && b !is Double) res.toLong() else res
        }
        if (a is String && b is String) return a + b
        if (a is List<*> && b is List<*>) {
            val res = a.toMutableList()
            res.addAll(b)
            return res
        }
        if (a is PythonTuple && b is PythonTuple) {
            return PythonTuple(a.items + b.items)
        }
        throw RuntimeException("TypeError: unsupported operand type(s) for +: '${a?.javaClass?.simpleName}' and '${b?.javaClass?.simpleName}'")
    }

    private fun performSubtraction(a: Any?, b: Any?): Any? {
        if (a is Number && b is Number) {
            val res = a.toDouble() - b.toDouble()
            return if (res % 1.0 == 0.0 && a !is Double && b !is Double) res.toLong() else res
        }
        if (a is Set<*> && b is Set<*>) {
            return (a.toSet() - b.toSet()).toMutableSet()
        }
        throw RuntimeException("TypeError: unsupported operand type(s) for -")
    }

    private fun performMultiplication(a: Any?, b: Any?): Any? {
        if (a is Number && b is Number) {
            val res = a.toDouble() * b.toDouble()
            return if (res % 1.0 == 0.0 && a !is Double && b !is Double) res.toLong() else res
        }
        if (a is String && b is Number) return a.repeat(b.toInt().coerceAtLeast(0))
        if (a is List<*> && b is Number) {
            val count = b.toInt().coerceAtLeast(0)
            val res = mutableListOf<Any?>()
            repeat(count) { res.addAll(a) }
            return res
        }
        throw RuntimeException("TypeError: unsupported operand type(s) for *")
    }

    private fun performDivision(a: Any?, b: Any?): Any? {
        if (a is Number && b is Number) {
            if (b.toDouble() == 0.0) throw RuntimeException("ZeroDivisionError: division by zero")
            val res = a.toDouble() / b.toDouble()
            return if (res % 1.0 == 0.0) res else res
        }
        throw RuntimeException("TypeError: unsupported operand type(s) for /")
    }

    private fun performFloorDivision(a: Any?, b: Any?): Any? {
        if (a is Number && b is Number) {
            if (b.toLong() == 0L) throw RuntimeException("ZeroDivisionError: integer division or modulo by zero")
            return a.toLong() / b.toLong()
        }
        throw RuntimeException("TypeError: unsupported operand type(s) for //")
    }

    private fun performModulo(a: Any?, b: Any?): Any? {
        if (a is Number && b is Number) {
            if (b.toLong() == 0L) throw RuntimeException("ZeroDivisionError: integer division or modulo by zero")
            return a.toLong() % b.toLong()
        }
        throw RuntimeException("TypeError: unsupported operand type(s) for %")
    }

    private fun performSetOrBitwiseOr(a: Any?, b: Any?): Any? {
        if (a is Set<*> && b is Set<*>) return a.toSet().union(b.toSet()).toMutableSet()
        if (a is Number && b is Number) return a.toLong() or b.toLong()
        throw RuntimeException("TypeError: unsupported operand type(s) for |")
    }

    private fun performSetOrBitwiseAnd(a: Any?, b: Any?): Any? {
        if (a is Set<*> && b is Set<*>) return a.toSet().intersect(b.toSet()).toMutableSet()
        if (a is Number && b is Number) return a.toLong() and b.toLong()
        throw RuntimeException("TypeError: unsupported operand type(s) for &")
    }

    private fun performSetOrBitwiseXor(a: Any?, b: Any?): Any? {
        if (a is Set<*> && b is Set<*>) {
            val u = a.toSet().union(b.toSet())
            val i = a.toSet().intersect(b.toSet())
            return (u - i).toMutableSet()
        }
        if (a is Number && b is Number) return a.toLong() xor b.toLong()
        throw RuntimeException("TypeError: unsupported operand type(s) for ^")
    }

    private fun evaluateNestedAccessOrSlice(expr: String, env: Map<String, Any?>): Any? {
        val rootVarMatch = Regex("""^([a-zA-Z_]\w*)""").find(expr) ?: throw RuntimeException("SyntaxError: invalid syntax")
        val rootVar = rootVarMatch.groupValues[1]
        var current: Any? = env[rootVar] ?: throw RuntimeException("NameError: name '$rootVar' is not defined")

        val bracketTokens = parseBracketTokens(expr.substring(rootVar.length))

        for (token in bracketTokens) {
            if (token.contains(":")) {
                current = performSlice(current, token, env)
            } else {
                val indexOrKey = evaluateExpression(token, env)
                current = when (current) {
                    is List<*> -> {
                        val intIdx = (indexOrKey as? Number)?.toInt() ?: throw RuntimeException("TypeError: list indices must be integers")
                        val actual = if (intIdx < 0) intIdx + current.size else intIdx
                        if (actual < 0 || actual >= current.size) throw RuntimeException("IndexError: list index out of range")
                        current[actual]
                    }
                    is PythonTuple -> {
                        val intIdx = (indexOrKey as? Number)?.toInt() ?: throw RuntimeException("TypeError: tuple indices must be integers")
                        val actual = if (intIdx < 0) intIdx + current.items.size else intIdx
                        if (actual < 0 || actual >= current.items.size) throw RuntimeException("IndexError: tuple index out of range")
                        current.items[actual]
                    }
                    is Map<*, *> -> {
                        if (!current.containsKey(indexOrKey)) throw RuntimeException("KeyError: '$indexOrKey'")
                        current[indexOrKey]
                    }
                    is String -> {
                        val intIdx = (indexOrKey as? Number)?.toInt() ?: throw RuntimeException("TypeError: string indices must be integers")
                        val actual = if (intIdx < 0) intIdx + current.length else intIdx
                        if (actual < 0 || actual >= current.length) throw RuntimeException("IndexError: string index out of range")
                        current[actual].toString()
                    }
                    else -> throw RuntimeException("TypeError: '$current' is not subscriptable")
                }
            }
        }
        return current
    }

    private fun parseBracketTokens(bracketsString: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        while (i < bracketsString.length) {
            if (bracketsString[i] == '[') {
                var depth = 1
                val start = i + 1
                i++
                while (i < bracketsString.length && depth > 0) {
                    if (bracketsString[i] == '[') depth++
                    if (bracketsString[i] == ']') depth--
                    i++
                }
                tokens.add(bracketsString.substring(start, i - 1).trim())
            } else {
                i++
            }
        }
        return tokens
    }

    private fun performSlice(obj: Any?, sliceToken: String, env: Map<String, Any?>): Any? {
        val parts = sliceToken.split(":")
        val startStr = parts.getOrNull(0)?.trim().orEmpty()
        val stopStr = parts.getOrNull(1)?.trim().orEmpty()
        val stepStr = parts.getOrNull(2)?.trim().orEmpty()

        val step = if (stepStr.isNotEmpty()) (evaluateExpression(stepStr, env) as? Number)?.toInt() ?: 1 else 1
        if (step == 0) throw RuntimeException("ValueError: slice step cannot be zero")

        val items = when (obj) {
            is List<*> -> obj
            is PythonTuple -> obj.items
            is String -> obj.toList().map { it.toString() }
            else -> throw RuntimeException("TypeError: cannot slice object of type ${obj?.javaClass?.simpleName}")
        }

        val size = items.size
        var start = if (startStr.isNotEmpty()) (evaluateExpression(startStr, env) as? Number)?.toInt() ?: 0 else if (step > 0) 0 else size - 1
        var stop = if (stopStr.isNotEmpty()) (evaluateExpression(stopStr, env) as? Number)?.toInt() ?: size else if (step > 0) size else -size - 1

        if (start < 0) start += size
        if (stop < 0 && stopStr.isNotEmpty()) stop += size

        val resultList = mutableListOf<Any?>()
        if (step > 0) {
            start = start.coerceIn(0, size)
            stop = stop.coerceIn(0, size)
            var curr = start
            while (curr < stop && curr < size) {
                resultList.add(items[curr])
                curr += step
            }
        } else {
            start = start.coerceIn(0, size - 1)
            var curr = start
            while (curr > stop && curr >= 0) {
                resultList.add(items[curr])
                curr += step
            }
        }

        return when (obj) {
            is PythonTuple -> PythonTuple(resultList)
            is String -> resultList.joinToString("")
            else -> resultList
        }
    }

    private fun evaluateMethodReturn(obj: Any?, method: String, args: List<Any?>): Any? {
        return when (obj) {
            is Map<*, *> -> {
                when (method) {
                    "keys" -> PythonDictKeys(obj.keys.toList())
                    "values" -> PythonDictValues(obj.values.toList())
                    "items" -> PythonDictItems(obj.map { PythonTuple(listOf(it.key, it.value)) })
                    "get" -> {
                        val key = args.getOrNull(0)
                        val defaultVal = args.getOrNull(1)
                        if (obj.containsKey(key)) obj[key] else defaultVal
                    }
                    "pop" -> {
                        val key = args.getOrNull(0)
                        if (obj is MutableMap<*, *>) {
                            val map = obj as MutableMap<Any?, Any?>
                            if (map.containsKey(key)) {
                                map.remove(key)
                            } else if (args.size > 1) {
                                args[1]
                            } else {
                                throw RuntimeException("KeyError: '$key'")
                            }
                        } else {
                            throw RuntimeException("TypeError: cannot pop from read-only map")
                        }
                    }
                    "popitem" -> {
                        if (obj is MutableMap<*, *>) {
                            val map = obj as MutableMap<Any?, Any?>
                            if (map.isEmpty()) throw RuntimeException("KeyError: 'popitem(): dictionary is empty'")
                            val lastKey = map.keys.last()
                            val lastVal = map.remove(lastKey)
                            PythonTuple(listOf(lastKey, lastVal))
                        } else {
                            throw RuntimeException("TypeError: cannot pop from read-only map")
                        }
                    }
                    "setdefault" -> {
                        val key = args.getOrNull(0)
                        val defaultVal = args.getOrNull(1)
                        if (obj is MutableMap<*, *>) {
                            val map = obj as MutableMap<Any?, Any?>
                            if (map.containsKey(key)) {
                                map[key]
                            } else {
                                map[key] = defaultVal
                                defaultVal
                            }
                        } else {
                            if (obj.containsKey(key)) obj[key] else defaultVal
                        }
                    }
                    "copy" -> obj.toMutableMap()
                    else -> throw RuntimeException("AttributeError: 'dict' object has no attribute '$method'")
                }
            }
            is Set<*> -> {
                val s1 = obj.toSet()
                val s2 = (args.getOrNull(0) as? Set<*>)?.toSet() ?: emptySet<Any?>()
                when (method) {
                    "union" -> s1.union(s2).toMutableSet()
                    "intersection" -> s1.intersect(s2).toMutableSet()
                    "difference" -> s1.subtract(s2).toMutableSet()
                    "symmetric_difference" -> (s1.union(s2) - s1.intersect(s2)).toMutableSet()
                    "issubset" -> s2.containsAll(s1)
                    "issuperset" -> s1.containsAll(s2)
                    else -> throw RuntimeException("AttributeError: 'set' object has no attribute '$method'")
                }
            }
            is List<*> -> {
                when (method) {
                    "count" -> obj.count { it == args.getOrNull(0) }
                    "index" -> {
                        val idx = obj.indexOf(args.getOrNull(0))
                        if (idx == -1) throw RuntimeException("ValueError: '${args.getOrNull(0)}' is not in list")
                        idx
                    }
                    else -> throw RuntimeException("AttributeError: 'list' object has no attribute '$method'")
                }
            }
            is PythonTuple -> {
                when (method) {
                    "count" -> obj.items.count { it == args.getOrNull(0) }
                    "index" -> {
                        val idx = obj.items.indexOf(args.getOrNull(0))
                        if (idx == -1) throw RuntimeException("ValueError: tuple.index(x): x not in tuple")
                        idx
                    }
                    else -> throw RuntimeException("AttributeError: 'tuple' object has no attribute '$method'")
                }
            }
            is String -> {
                when (method) {
                    "upper" -> obj.uppercase()
                    "lower" -> obj.lowercase()
                    "split" -> {
                        val delim = args.getOrNull(0)?.toString() ?: " "
                        obj.split(delim).toMutableList()
                    }
                    "replace" -> {
                        val oldStr = args.getOrNull(0)?.toString() ?: ""
                        val newStr = args.getOrNull(1)?.toString() ?: ""
                        obj.replace(oldStr, newStr)
                    }
                    else -> throw RuntimeException("AttributeError: 'str' object has no attribute '$method'")
                }
            }
            else -> throw RuntimeException("AttributeError: object has no attribute '$method'")
        }
    }

    private fun isTruthy(obj: Any?): Boolean {
        return when (obj) {
            null -> false
            is Boolean -> obj
            is Number -> obj.toDouble() != 0.0
            is String -> obj.isNotEmpty()
            is List<*> -> obj.isNotEmpty()
            is PythonTuple -> obj.items.isNotEmpty()
            is Set<*> -> obj.isNotEmpty()
            is Map<*, *> -> obj.isNotEmpty()
            else -> true
        }
    }

    private fun compareEquals(a: Any?, b: Any?): Boolean {
        if (a == b) return true
        if (a is Number && b is Number) return a.toDouble() == b.toDouble()
        if (a is PythonTuple && b is PythonTuple) return a.items == b.items
        return false
    }

    private fun formatPythonOutput(obj: Any?): String {
        return when (obj) {
            null -> "None"
            is Boolean -> if (obj) "True" else "False"
            is String -> obj
            is PythonTuple -> "(${obj.items.joinToString(", ") { formatPythonRepr(it) }})"
            is List<*> -> "[${obj.joinToString(", ") { formatPythonRepr(it) }}]"
            is Set<*> -> if (obj.isEmpty()) "set()" else "{${obj.joinToString(", ") { formatPythonRepr(it) }}}"
            is Map<*, *> -> "{${obj.entries.joinToString(", ") { "${formatPythonRepr(it.key)}: ${formatPythonRepr(it.value)}" }}}"
            is PythonDictKeys -> "dict_keys([${obj.keys.joinToString(", ") { formatPythonRepr(it) }}])"
            is PythonDictValues -> "dict_values([${obj.values.joinToString(", ") { formatPythonRepr(it) }}])"
            is PythonDictItems -> "dict_items([${obj.items.joinToString(", ") { formatPythonRepr(it) }}])"
            else -> obj.toString()
        }
    }

    private fun formatPythonRepr(obj: Any?): String {
        return when (obj) {
            null -> "None"
            is Boolean -> if (obj) "True" else "False"
            is String -> "'$obj'"
            is PythonTuple -> "(${obj.items.joinToString(", ") { formatPythonRepr(it) }})"
            is List<*> -> "[${obj.joinToString(", ") { formatPythonRepr(it) }}]"
            is Set<*> -> if (obj.isEmpty()) "set()" else "{${obj.joinToString(", ") { formatPythonRepr(it) }}}"
            is Map<*, *> -> "{${obj.entries.joinToString(", ") { "${formatPythonRepr(it.key)}: ${formatPythonRepr(it.value)}" }}}"
            else -> obj.toString()
        }
    }

    private fun splitTopLevel(str: String, delimiter: Char): List<String> {
        val result = mutableListOf<String>()
        var depthBracket = 0
        var depthParen = 0
        var depthBrace = 0
        var inQuote = false
        var quoteChar = ' '
        val sb = StringBuilder()

        for (c in str) {
            if (inQuote) {
                sb.append(c)
                if (c == quoteChar) inQuote = false
            } else {
                when (c) {
                    '"', '\'' -> {
                        inQuote = true
                        quoteChar = c
                        sb.append(c)
                    }
                    '[' -> { depthBracket++; sb.append(c) }
                    ']' -> { depthBracket--; sb.append(c) }
                    '(' -> { depthParen++; sb.append(c) }
                    ')' -> { depthParen--; sb.append(c) }
                    '{' -> { depthBrace++; sb.append(c) }
                    '}' -> { depthBrace--; sb.append(c) }
                    delimiter -> {
                        if (depthBracket == 0 && depthParen == 0 && depthBrace == 0) {
                            result.add(sb.toString())
                            sb.clear()
                        } else {
                            sb.append(c)
                        }
                    }
                    else -> sb.append(c)
                }
            }
        }
        if (sb.isNotEmpty()) result.add(sb.toString())
        return result
    }

    private fun splitTopLevelByKeywords(str: String, keyword: String): List<String> {
        val pattern = Regex("""\b$keyword\b""")
        val matches = pattern.findAll(str).toList()
        if (matches.isEmpty()) return listOf(str)

        val parts = mutableListOf<String>()
        var lastIdx = 0
        for (m in matches) {
            val prefix = str.substring(0, m.range.first)
            // check depth
            val bracketCount = prefix.count { it == '[' } - prefix.count { it == ']' }
            val parenCount = prefix.count { it == '(' } - prefix.count { it == ')' }
            val quoteCount = prefix.count { it == '"' || it == '\'' }
            if (bracketCount == 0 && parenCount == 0 && quoteCount % 2 == 0) {
                parts.add(str.substring(lastIdx, m.range.first))
                lastIdx = m.range.last + 1
            }
        }
        parts.add(str.substring(lastIdx))
        return parts
    }

    private fun splitTopLevelByOperator(str: String, op: String): List<String> {
        val idx = str.indexOf(op)
        if (idx == -1) return listOf(str)
        val prefix = str.substring(0, idx)
        val bracketCount = prefix.count { it == '[' } - prefix.count { it == ']' }
        val parenCount = prefix.count { it == '(' } - prefix.count { it == ')' }
        val quoteCount = prefix.count { it == '"' || it == '\'' }
        if (bracketCount == 0 && parenCount == 0 && quoteCount % 2 == 0) {
            return listOf(prefix, str.substring(idx + op.length))
        }
        return listOf(str)
    }

    private fun removeInlineComments(line: String): String {
        var inQuote = false
        var quoteChar = ' '
        for (i in line.indices) {
            val c = line[i]
            if (inQuote) {
                if (c == quoteChar) inQuote = false
            } else {
                if (c == '"' || c == '\'') {
                    inQuote = true
                    quoteChar = c
                } else if (c == '#') {
                    return line.substring(0, i).trim()
                }
            }
        }
        return line.trim()
    }

    private fun comparePythonObjects(a: Any?, b: Any?): Int {
        if (a is Number && b is Number) {
            return a.toDouble().compareTo(b.toDouble())
        }
        return a.toString().compareTo(b.toString())
    }
}

data class PythonTuple(val items: List<Any?>)
data class PythonDictKeys(val keys: List<Any?>)
data class PythonDictValues(val values: List<Any?>)
data class PythonDictItems(val items: List<PythonTuple>)
data class PythonRange(val start: Int, val stop: Int, val step: Int) {
    fun toList(): List<Long> {
        val list = mutableListOf<Long>()
        if (step > 0) {
            var curr = start
            while (curr < stop) {
                list.add(curr.toLong())
                curr += step
            }
        } else if (step < 0) {
            var curr = start
            while (curr > stop) {
                list.add(curr.toLong())
                curr += step
            }
        }
        return list
    }
}
