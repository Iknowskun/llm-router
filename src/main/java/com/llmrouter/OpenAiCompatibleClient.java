package com.llmrouter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

/**
 * DeepSeek / 通义千问 / GLM / Kimi 等都兼容 OpenAI 的 /chat/completions 协议，
 * 因此只需要这一个客户端类，不建任何继承体系或接口抽象。
 */
@Component
public class OpenAiCompatibleClient {

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper;

    public OpenAiCompatibleClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ChatResponse chat(LlmProperties.ModelConfig config, ChatRequest request) {
        var body = new OpenAiChatRequest(
                config.model(),
                List.of(new OpenAiMessage("user", request.prompt())),
                request.temperature(),
                request.maxTokens());

        String raw;
        try {
            raw = restClient.post()
                    .uri(config.baseUrl() + "/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + config.apiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);
        } catch (RestClientResponseException e) {
            throw new LlmException("模型 " + config.name() + " 返回错误 (HTTP "
                    + e.getStatusCode().value() + "): " + e.getResponseBodyAsString());
        } catch (ResourceAccessException e) {
            throw new LlmException("访问模型 " + config.name() + " 网络失败: " + e.getMessage());
        }

        try {
            JsonNode root = objectMapper.readTree(raw);
            String content = root.path("choices").path(0).path("message").path("content").asText();
            JsonNode usage = root.path("usage");
            return new ChatResponse(
                    config.model(),
                    content,
                    usage.path("prompt_tokens").asInt(),
                    usage.path("completion_tokens").asInt(),
                    usage.path("total_tokens").asInt());
        } catch (Exception e) {
            throw new LlmException("解析模型响应失败: " + e.getMessage());
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private record OpenAiChatRequest(
            String model,
            List<OpenAiMessage> messages,
            Double temperature,
            @JsonProperty("max_tokens") Integer maxTokens) {
    }

    private record OpenAiMessage(String role, String content) {
    }
}
