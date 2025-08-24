package com.example.hatsalvoids.external.openai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
public class OpenAiService {

    private final ChatClient chatClient;

    public OpenAiService(ChatClient.Builder chatClientBuilder) {
        // Builder는 전역 설정(application.yml)을 따름
        this.chatClient = chatClientBuilder.build();
    }



    @Async("openAiAsyncExecutor")
    public Future<String> getGeneratedTextAsync(String template) {
        ChatClient.CallResponseSpec response = chatClient.prompt(
                new Prompt(template,
                        OpenAiChatOptions.builder()
                                .model("gpt-4o-mini")
                                .temperature(0.0) // 추출은 보통 0.0~0.2
                                .build())
        ).call();
        String answer = response.chatResponse().getResult().getOutput().getText();

        return CompletableFuture.completedFuture(answer);
    }

    public String getGeneratedText(String template) {
        PromptTemplate pt = new PromptTemplate(template);

        ChatClient.CallResponseSpec response = chatClient.prompt(
                new Prompt(pt.render(),
                        OpenAiChatOptions.builder()
                                .model("gpt-4o-mini")
                                .temperature(0.0) // 추출은 보통 0.0~0.2
                                .build())
        ).call();


        return response.chatResponse().getResult().getOutput().getText();
    }
}
