package vinh.it.severbotchat.Telegram.bot.chat.AI.Gemini.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import vinh.it.severbotchat.Telegram.bot.chat.AI.Gemini.entity.Conversation;
import vinh.it.severbotchat.Telegram.bot.chat.AI.Gemini.repo.UserDataRepo;
import vinh.it.severbotchat.Telegram.bot.chat.AI.Gemini.ulti.ConversationCreator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

@Service
public class GeminiAIService {
@Autowired
ConversationCreator conversationCreator;
@Autowired
    UserDataRepo userDataRepo;
    private static final String GEMINI_API_URL1 = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-8b-latest:generateContent?key=AIzaSyBOQp30Q2dJTWSZor_ZEV3NNVh35n3UYCE";

    // Truy vấn với hình ảnh (base64)gemini-1.5-flash-001-tuning gemini-2.0-flash-exp
    public String queryGeminiWithImage(String imgPath) throws IOException {
        // Mã hóa hình ảnh thành base64
        String base64Image = encodeImageToBase64(imgPath);
        // Tạo payload JSON cho yêu cầu API
        String jsonPayload = createJsonPayloadWithImage(base64Image);
        // Gửi yêu cầu POST đến Gemini API
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                GEMINI_API_URL1,
                HttpMethod.POST,
                entity,
                String.class
        );

        // Trả về kết quả từ API
        return response.getBody();
    }
    public Map<String,Object> configTemp() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("temperature", 2);
        payload.put("top_p", 0.7);
        payload.put("top_k", 10);
        payload.put("maxOutputTokens", 8192);
        payload.put("response_mime_type", "text/plain");
        return payload;
    }
    // Truy vấn với văn bản
    public Mono<String> queryGeminiWithMessage(String userMessage) {
        try {
            WebClient webClient = WebClient.create();
            Map<String, Object> jsonRequest = new HashMap<>();
            List<Map<String, Object>> contents = new ArrayList<>();
            // Tạo danh sách tin nhắn của user và model
            List<Conversation> userConversations = conversationCreator.generateUserAskData();
            List<Conversation> modelConversations = conversationCreator.generateModelReplyData();
            int userSize = userConversations.size();
            int modelSize = modelConversations.size();
            int maxSize = Math.max(userSize, modelSize);

            for (int i = 0; i < maxSize; i++) {
                if (i < userSize) {
                    Map<String, Object> userMessageContent = new HashMap<>();
                    List<Map<String, String>> userParts = new ArrayList<>();
                    userParts.add(Collections.singletonMap("text", userConversations.get(i).getParts().getText()));
                    userMessageContent.put("role", "user");
                    userMessageContent.put("parts", userParts);
                    contents.add(userMessageContent);
                }
                if (i < modelSize) {
                    Map<String, Object> modelMessageContent = new HashMap<>();
                    List<Map<String, String>> modelParts = new ArrayList<>();
                    modelParts.add(Collections.singletonMap("text", modelConversations.get(i).getParts().getText()));
                    modelMessageContent.put("role", "model");
                    modelMessageContent.put("parts", modelParts);
                    contents.add(modelMessageContent);
                }
            }

            Map<String, Object> finalUserMessageContent = new HashMap<>();
            List<Map<String, String>> finalUserParts = new ArrayList<>();
            finalUserParts.add(Collections.singletonMap("text", userMessage));
            finalUserMessageContent.put("role", "user");
            finalUserMessageContent.put("parts", finalUserParts);
            contents.add(finalUserMessageContent);

            // Đặt contents vào jsonRequest
            jsonRequest.put("contents", contents);
            jsonRequest.put("safetySettings", createSafetySettings());
            jsonRequest.put("generationConfig", configTemp());
//            jsonRequest.put("tools", new ArrayList<>());
            // Gửi yêu cầu POST qua WebClient
            return webClient.post()
                    .uri(GEMINI_API_URL1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(jsonRequest)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnSuccess(response -> {
                        System.out.println("Response: " + response);
                        DataTemp.userMessages.add(userMessage);
                    })
                    .doOnError(Throwable::printStackTrace);

        } catch (Exception e) {
            e.printStackTrace();
            return Mono.error(e);
        }
    }


    private List<Map<String, Object>> createSafetySettings() {
        List<Map<String, Object>> safetySettings = new ArrayList<>();

        // Tạo setting đầu tiên với LinkedHashMap
        Map<String, Object> safetySetting1 = new LinkedHashMap<>();
        safetySetting1.put("category", "HARM_CATEGORY_DANGEROUS_CONTENT");
        safetySetting1.put("threshold", "BLOCK_NONE");
        safetySettings.add(safetySetting1);

        // Tạo setting HARASSMENT với LinkedHashMap
        Map<String, Object> harassmentSetting = new LinkedHashMap<>();
        harassmentSetting.put("category", "HARM_CATEGORY_HARASSMENT");
        harassmentSetting.put("threshold", "BLOCK_NONE");
        safetySettings.add(harassmentSetting);

        // Tạo setting HATE_SPEECH với LinkedHashMap
        Map<String, Object> hateSpeechSetting = new LinkedHashMap<>();
        hateSpeechSetting.put("category", "HARM_CATEGORY_HATE_SPEECH");
        hateSpeechSetting.put("threshold", "BLOCK_NONE");
        safetySettings.add(hateSpeechSetting);

        // Tạo setting SEXUALLY_EXPLICIT với LinkedHashMap
        Map<String, Object> sexSetting = new LinkedHashMap<>();
        sexSetting.put("category", "HARM_CATEGORY_SEXUALLY_EXPLICIT"); // Sửa tên biến
        sexSetting.put("threshold", "BLOCK_NONE");
        safetySettings.add(sexSetting); // Thêm đúng setting
        return safetySettings;
    }



    // Mã hóa hình ảnh thành base64
    private String encodeImageToBase64(String imgPath) throws IOException {
        File imageFile = new File(imgPath);
        byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    private String createJsonPayloadWithImage(String base64Image) {
        return "{\n" +
                "  \"contents\": [{\n" +
                "    \"parts\": [\n" +
                "      {\n" +
                "        \"text\": \"Tell me about this instrument\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"inline_data\": {\n" +
                "          \"mime_type\": \"image/jpeg\",\n" +
                "          \"data\": \"" + base64Image + "\"\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  }]\n" +
                "}";
    }

}
