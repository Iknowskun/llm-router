package com.llmrouter;

/**
 * 网关对外暴露的请求。model 为空时走默认模型；temperature / maxTokens 可选。
 */
public record ChatRequest(
        String model,
        String prompt,
        Double temperature,
        Integer maxTokens) {
}
