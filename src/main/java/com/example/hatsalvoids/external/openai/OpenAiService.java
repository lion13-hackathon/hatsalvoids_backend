package com.example.hatsalvoids.external.openai;

import com.example.hatsalvoids.global.utils.GlobalLogger;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
public class OpenAiService {

    private final ChatClient.Builder builder;

    public OpenAiService(ChatClient.Builder chatClientBuilder) {
        this.builder = chatClientBuilder;
    }


    public ChatClient createChatClient(){
        return this.builder.build();
    }

    @Async("openAiAsyncExecutor")
    public Future<String> getGeneratedTextAsync(String template) {
        ChatClient.CallResponseSpec response;

        try{
            response = req2gpt(template);
            String answer = response.chatResponse().getResult().getOutput().getText();
            return CompletableFuture.completedFuture(answer);
        }catch (NonTransientAiException e) {
            GlobalLogger.error("OpenAiService getGeneratedTextAsync error:", e);
            return CompletableFuture.failedFuture(
                    new NonTransientAiException("OpenAI API 호출 중 오류가 발생했습니다.", e)
            );
        }
    }

    private ChatClient.CallResponseSpec req2gpt(String template) {
        // chatClient를 아끼는 방법?
        return createChatClient().prompt(
                new Prompt(template,
                        OpenAiChatOptions.builder()
                                .model("gpt-4o-mini")
                                .temperature(0.0) // 추출은 보통 0.0~0.2
                                .build())
        ).call();
    }
}
