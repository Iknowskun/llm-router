# LLM Router — 多模型智能路由网关

一个用于面试与实战的轻量级多模型路由网关。遵循「反过度设计」：代码扁平、直接、高可读，不建继承体系，不引第三方 LLM SDK。

## 技术栈
- Java 17
- Spring Boot 3（仅 `spring-boot-starter-web`）
- Spring `RestClient` 直接调用 OpenAI 兼容协议

## 快速开始

1. 配置 API Key（以 DeepSeek 为例）：
   - Windows：`set DEEPSEEK_API_KEY=sk-xxxx`
   - macOS / Linux：`export DEEPSEEK_API_KEY=sk-xxxx`

2. 启动：
   ```bash
   mvn spring-boot:run
   ```

3. 调用（默认模型）：
   ```bash
   curl -X POST http://localhost:8080/chat -H "Content-Type: application/json" -d "{\"prompt\":\"用一句话解释什么是递归\"}"
   ```

   指定模型：
   ```bash
   curl -X POST http://localhost:8080/chat -H "Content-Type: application/json" -d "{\"model\":\"qwen\",\"prompt\":\"你好\"}"
   ```

## 目录结构（单包，扁平）

```
src/main/java/com/llmrouter/
├── LlmRouterApplication.java    # 启动类
├── LlmProperties.java           # 模型配置（@ConfigurationProperties）
├── ChatRequest.java             # 请求 DTO
├── ChatResponse.java            # 响应 DTO
├── OpenAiCompatibleClient.java  # 一个客户端兼容所有 OpenAI 协议模型
├── Router.java                  # 规则路由（按模型名选目标）
├── ChatController.java          # POST /chat 入口
├── LlmException.java            # 业务异常
└── GlobalExceptionHandler.java  # 统一错误返回
```

## 设计说明
- 所有模型共用 OpenAI 兼容协议，因此**只有一个客户端类**，没有继承体系。
- 路由逻辑集中在 `Router` 一个类里，用 Map 查找 + 默认值，不用策略模式。
- 配置驱动：新增模型只需在 `application.yml` 加一段配置。

## 后续阶段
- 规则路由扩展：按任务类型 / 成本 / 能力路由 + 失败重试与 fallback
- 可观测性：日志、耗时、token 用量
- LLM 智能路由：用分类模型先决定路由目标
