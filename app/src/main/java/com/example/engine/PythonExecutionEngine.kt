package com.example.engine

data class ExecutionResult(
    val stdout: String,
    val error: String? = null,
    val variables: Map<String, Any?> = emptyMap(),
    val executionTimeMs: Long = 0L,
    val isSuccess: Boolean = error == null
) {
    val output: String get() = stdout
    val errorMessage: String? get() = error
}

interface PythonExecutionEngine {
    suspend fun execute(code: String): ExecutionResult
    suspend fun executeCode(code: String): ExecutionResult = execute(code)
}
