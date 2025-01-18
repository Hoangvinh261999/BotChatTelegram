//package vinh.it.severbotchat.Telegram.bot.chat.AI.Gemini.service;
//
//
//import org.springframework.stereotype.Service;
//
//@Service
//public class GeminiVer2 {
//    // Specify a Gemini model appropriate for your use case
//    GenerativeModel gm =
//            new GenerativeModel(
//                    /* modelName */ "gemini-1.5-flash",
//                    // Access your API key as a Build Configuration variable (see "Set up your API key"
//                    // above)
//                    /* apiKey */ BuildConfig.apiKey);
//    GenerativeModelFutures model = GenerativeModelFutures.from(gm);
//
//    Content content =
//            new Content.Builder().addText("Write a story about a magic backpack.").build();
//
//    // For illustrative purposes only. You should use an executor that fits your needs.
//    Executor executor = Executors.newSingleThreadExecutor();
//
//    ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
//Futures.addCallback(
//    response,
//            new FutureCallback<GenerateContentResponse>() {
//        @Override
//        public void onSuccess(GenerateContentResponse result) {
//            String resultText = result.getText();
//            System.out.println(resultText);
//        }
//
//        @Override
//        public void onFailure(Throwable t) {
//            t.printStackTrace();
//        }
//    },
//    executor);
//}
