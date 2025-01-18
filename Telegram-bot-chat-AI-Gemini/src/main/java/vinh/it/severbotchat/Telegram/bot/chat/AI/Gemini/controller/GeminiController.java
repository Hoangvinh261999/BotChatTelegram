package vinh.it.severbotchat.Telegram.bot.chat.AI.Gemini.controller;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import vinh.it.severbotchat.Telegram.bot.chat.AI.Gemini.service.GeminiAIService;

import java.io.IOException;

@RestController
@RequestMapping("/api/gemini")
public class GeminiController {

    private final GeminiAIService geminiAIService;

    public GeminiController(GeminiAIService geminiAIService) {
        this.geminiAIService = geminiAIService;
    }

    @PostMapping("/query-image")
    public String queryGeminiWithImage(@RequestParam String imgPath) {
        try {
            return geminiAIService.queryGeminiWithImage(imgPath);
        } catch (IOException e) {
            return "Error processing image: " + e.getMessage();
        }
    }

    @PostMapping("/query-message")
    public Mono<String> queryGeminiWithMessage(@RequestParam String message) {
        return geminiAIService.queryGeminiWithMessage(message);
    }
}
