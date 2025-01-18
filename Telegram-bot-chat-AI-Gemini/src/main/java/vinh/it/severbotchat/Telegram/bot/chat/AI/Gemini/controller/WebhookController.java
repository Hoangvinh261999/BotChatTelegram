package vinh.it.severbotchat.Telegram.bot.chat.AI.Gemini.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import vinh.it.severbotchat.Telegram.bot.chat.AI.Gemini.entity.dto.ModelData;
import vinh.it.severbotchat.Telegram.bot.chat.AI.Gemini.entity.dto.UserData;
import vinh.it.severbotchat.Telegram.bot.chat.AI.Gemini.repo.ModelDataRepo;
import vinh.it.severbotchat.Telegram.bot.chat.AI.Gemini.repo.UserDataRepo;
import vinh.it.severbotchat.Telegram.bot.chat.AI.Gemini.service.DataTemp;
import vinh.it.severbotchat.Telegram.bot.chat.AI.Gemini.service.GeminiAIService;
import vinh.it.severbotchat.Telegram.bot.chat.AI.Gemini.ulti.ConversationCreator;

import java.util.*;

@RestController
public class WebhookController {
    @Autowired
    ModelDataRepo modelDataRepo;
    @Autowired
    UserDataRepo  userDataRepo;
    private final GeminiAIService geminiAIService;
    @Autowired
    ConversationCreator conversationCreator;
    public WebhookController(GeminiAIService geminiAIService) {
        this.geminiAIService = geminiAIService;
    }
    @RequestMapping(value = "/webhook", method = RequestMethod.POST)
    public Mono<Void> handleTelegramUpdates(@RequestBody String update) {
        return Mono.just(update)
                .flatMap(this::extractUserMessageMono)
                .flatMap(userMessage -> extractChatIdMono(update)
                        .flatMap(chatId -> {
                            sendTypingNotification(chatId);
//                            if (userMessage != null && userMessage.toLowerCase().contains("bot")) {
                                return geminiAIService.queryGeminiWithMessage(userMessage)
                                        .flatMap(geminiResponse -> {
                                            String geminiText = extractGeminiTexts(geminiResponse);
                                            return sendTelegramMessageMono(geminiText, chatId);
                                        });
//                            } else {
//                                return sendTelegramMessageMono("Không có câu hỏi liên quan đến bot.", chatId);
//                            }
                        })
                )
                .onErrorResume(e -> {
                    e.printStackTrace();
                    return Mono.empty(); // Xử lý lỗi, trả về void
                });

    }

    public String extractGeminiTexts(String geminiResponse){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseNode = objectMapper.readTree(geminiResponse);
            // StringBuilder để nối các đoạn văn bản lại với nhau
            StringBuilder texts = new StringBuilder();
            // Duyệt qua tất cả các phần tử trong "parts"
            JsonNode partsNode = responseNode.at("/candidates/0/content/parts");
            if (partsNode.isArray()) {
                for (JsonNode part : partsNode) {
                    JsonNode textNode = part.at("/text");
                    if (!textNode.isMissingNode()) {
                        if (texts.length() > 0) {
                            texts.append("\n");
                        }
                        texts.append(textNode.asText());
                    }
                }
            }

            if (texts.length() == 0) {
                DataTemp.userMessages.remove(DataTemp.userMessages.size()-1);
                return "Đéo biết !";
            }else{
                DataTemp.modelMessage.add(texts.toString());
                modelDataRepo.save(ModelData.builder()
                        .text(texts.toString())
                        .build());
                userDataRepo.save(UserData.builder()
                        .text(DataTemp.userMessages.get(DataTemp.userMessages.size()-1))
                        .build());
                return texts.toString();
            }

        }catch (Exception e){
            return e.getMessage();
        }

    }


    private Mono<Void> sendTelegramMessageMono(String message, String chatId) {
        message = escapeMarkdown(message);
        String url = "https://api.telegram.org/bot7192961876:AAHgjM6ZFeE6cKLb16zhrTFXn4YRwggLoNk/sendMessage";
        int maxMessageLength = 4096;
        List<String> messageParts = splitMessage(message, maxMessageLength);
        return Mono.when(
                messageParts.stream()
                        .map(part -> sendMessageMono(url, part, chatId))
                        .toArray(Mono[]::new)
        );
    }
    private Mono<Void> sendMessageMono(String url, String part, String chatId) {
        WebClient webClient = WebClient.create();
        Map<String, String> params = new HashMap<>();
        params.put("chat_id", chatId);
        params.put("text", part);
        return webClient.post()
                .uri(url)
                .bodyValue(params)
                .retrieve()
                .bodyToMono(Void.class);
    }

    private List<String> splitMessage(String message, int maxMessageLength) {
        List<String> messageParts = new ArrayList<>();
        int length = message.length();

        for (int i = 0; i < length; i += maxMessageLength) {
            int endIndex = Math.min(i + maxMessageLength, length);
            messageParts.add(message.substring(i, endIndex));
        }

        return messageParts;
    }
    private void sendMessage(String url, String message, String chatId) {
        WebClient webClient = WebClient.create();

        Map<String, String> requestPayload = new HashMap<>();
        requestPayload.put("chat_id", chatId);
        requestPayload.put("text", message);
        requestPayload.put("parse_mode", "MarkdownV2");

        webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestPayload)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(Throwable::printStackTrace)
                .subscribe();
    }
    private String escapeMarkdown(String message) {
             return    message.replace("*", "");
    }

    private Mono<String> extractUserMessageMono(String update) {
        return Mono.fromCallable(() -> {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode updateNode = objectMapper.readTree(update);
            JsonNode messageNode = updateNode.at("/message/text");
            if (messageNode.isMissingNode()) {
                return null; // Trả về null nếu không có tin nhắn văn bản
            }
            return messageNode.asText(); // Lấy tin nhắn văn bản
        }).onErrorResume(e -> {
            e.printStackTrace(); // Xử lý ngoại lệ
            return Mono.empty(); // Trả về Mono rỗng nếu lỗi
        });
    }

    private Mono<String> extractChatIdMono(String update) {
        return Mono.fromCallable(() -> {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode updateNode = objectMapper.readTree(update);
            JsonNode chatNode = updateNode.at("/message/chat/id");
            if (chatNode.isMissingNode() || chatNode.isNull()) {
                throw new IllegalArgumentException("Không tìm thấy chat_id trong payload");
            }
            return chatNode.asText();
        }).onErrorResume(e -> {
            e.printStackTrace(); // Xử lý ngoại lệ
            return Mono.error(new RuntimeException("Lỗi khi trích xuất chat_id", e));
        });
    }


    private void sendTypingNotification(String chatId) {
        String url = "https://api.telegram.org/bot7192961876:AAHgjM6ZFeE6cKLb16zhrTFXn4YRwggLoNk/sendChatAction";
        Map<String, String> typingParams = new HashMap<>();
        typingParams.put("chat_id", chatId);
        typingParams.put("action", "typing");

        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForObject(url, typingParams, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
