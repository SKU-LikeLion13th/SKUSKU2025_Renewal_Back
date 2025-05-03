package com.sku_sku.backend.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sku_sku.backend.security.GeminiConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiService {
    private final GeminiConfig geminiConfig;
    private final RestTemplate restTemplate;
    private final Gson gson = new Gson();

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/{modelName}:generateContent";

    /**
     * 주관식 답변을 Gemini API를 사용하여 평가합니다.
     * @param question 퀴즈의 문제 내용
     * @param correctAnswer 퀴즈의 정답
     * @param userAnswer 사용자가 제출한 답변
     * @return 답변이 맞으면 true, 아니면 false
     */
    public boolean evaluateEssayAnswer(String question, String correctAnswer, String userAnswer) {
        try {
            System.out.println("============= 주관식 문제 평가 시작 =============");
            System.out.println("문제: " + question);
            System.out.println("모범 답안: " + correctAnswer);
            System.out.println("학생 답변: " + userAnswer);
            
            String response = callGeminiApi(buildPrompt(question, correctAnswer, userAnswer));
            
            boolean isCorrect = parseResponse(response);
            System.out.println("Gemini 평가 결과: " + (isCorrect ? "정답" : "오답"));
            System.out.println("평가 이유: " + parseReason(response));
            System.out.println("============= 주관식 문제 평가 완료 =============");
            
            return isCorrect;
        } catch (Exception e) {
            log.error("주관식 답변 평가 중 Gemini API 오류 발생", e);
            System.out.println("============= 주관식 문제 평가 오류 =============");
            System.out.println("오류 내용: " + e.getMessage());
            return false; // API 호출 실패 시 기본값으로 오답 처리
        }
    }

    private String buildPrompt(String question, String correctAnswer, String userAnswer) {
        return String.format("""
            당신은 학생들의 주관식 문제 답변을 평가하는 AI 조교입니다.
            
            주어진 정답과 학생의 답변을 비교하여 학생의 답변이 맞는지 평가해주세요.
            답변은 다른 방식으로 표현되었더라도 의미적으로 같다면 정답으로 인정합니다.
            사소한 표현 차이는 관대하게 평가해 주세요.
            학생이 핵심 개념을 이해하고 있는지가 중요합니다.
            
            문제: %s
            
            정답: %s
            
            학생 답변: %s
            
            다음 형식의 JSON으로 응답해주세요:
            {
              "iscorrect": true/false,
              "reason": "평가 이유를 자세히 설명해주세요. 왜 맞았는지 또는 왜 틀렸는지 구체적으로 작성해주세요."
            }
            """, question, correctAnswer, userAnswer);
    }
    
    private String parseReason(String response) {
        try {
            // JSON 형식인지 확인
            if (response.trim().startsWith("{")) {
                try {
                    JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
                    if (jsonResponse.has("reason")) {
                        return jsonResponse.get("reason").getAsString();
                    }
                } catch (Exception e) {
                    System.out.println("Reason JSON 파싱 실패: " + e.getMessage());
                }
            }
            
//            // 응답이 완전히 동일한 경우
//            if (response.equals("@SpringBootApplication 애노테이션은 @Configuration, @EnableAutoConfiguration, @ComponentScan 애노테이션을 조합한 복합 애노테이션이다.")) {
//                return "학생의 답변이 정확하게 모범 답안과 일치합니다.";
//            }
            
            // JSON이 아닌 경우 전체 텍스트를 이유로 반환 (길이 제한 없음)
            return response;
        } catch (Exception e) {
            log.error("Gemini API 응답 이유 파싱 중 오류 발생", e);
            return "응답 파싱 중 오류가 발생했습니다.";
        }
    }

    private String callGeminiApi(String prompt) {
        // URL에 모델 이름을 변수로 설정하고 API 키는 쿼리 파라미터로 추가
        String apiUrl = GEMINI_API_URL.replace("{modelName}", "gemini-1.5-flash") + "?key=" + geminiConfig.getKey();
        
        System.out.println("API URL 형식 확인: " + GEMINI_API_URL + "?key=YOUR_KEY_HIDDEN");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        JsonObject requestBody = new JsonObject();
        JsonArray contents = new JsonArray();
        JsonObject content = new JsonObject();
        JsonObject textPart = new JsonObject();
        
        textPart.addProperty("text", prompt);
        content.add("parts", new JsonArray());
        content.getAsJsonArray("parts").add(textPart);
        contents.add(content);
        requestBody.add("contents", contents);
        
        // 생성 파라미터 설정
        JsonObject generationConfig = new JsonObject();
        generationConfig.addProperty("maxOutputTokens", geminiConfig.getMaxTokens());
        generationConfig.addProperty("temperature", geminiConfig.getTemperature());
        generationConfig.addProperty("topK", geminiConfig.getTopK());
        generationConfig.addProperty("topP", geminiConfig.getTopP());
        requestBody.add("generationConfig", generationConfig);
        
        HttpEntity<String> request = new HttpEntity<>(gson.toJson(requestBody), headers);
        
        // API 요청 내용 로깅 (디버깅용)
        System.out.println("API 요청 본문: " + gson.toJson(requestBody));
        
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);
        System.out.println("API 응답 상태 코드: " + response.getStatusCode());
        System.out.println("API 응답 본문 일부: " + (response.getBody() != null ? 
                (response.getBody().length() > 100 ? response.getBody().substring(0, 100) + "..." : response.getBody()) 
                : "응답 없음"));
        
        return extractContentFromResponse(response.getBody());
    }
    
    private String extractContentFromResponse(String responseBody) {
        try {
            JsonObject responseJson = gson.fromJson(responseBody, JsonObject.class);
            JsonArray candidates = responseJson.getAsJsonArray("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
                JsonObject content = firstCandidate.getAsJsonObject("content");
                JsonArray parts = content.getAsJsonArray("parts");
                if (parts != null && !parts.isEmpty()) {
                    String text = parts.get(0).getAsJsonObject().get("text").getAsString();
                    System.out.println("추출된 응답 텍스트: " + text);
                    return text;
                }
            }
            log.warn("Gemini API 응답에서 텍스트 콘텐츠를 찾을 수 없습니다.");
            return "{}"; // 빈 JSON 객체 반환
        } catch (Exception e) {
            log.error("Gemini API 응답 파싱 오류", e);
            System.out.println("원본 응답: " + responseBody);
            return "{}"; // 빈 JSON 객체 반환
        }
    }

    private boolean parseResponse(String response) {
        try {
            System.out.println("파싱할 응답: " + response);
            
            // JSON 형식인지 확인
            if (response.trim().startsWith("{")) {
                try {
                    JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
                    if (jsonResponse.has("isCorrect")) {
                        boolean result = jsonResponse.get("isCorrect").getAsBoolean();
                        System.out.println("JSON 파싱 결과: isCorrect = " + result);
                        return result;
                    }
                } catch (Exception e) {
                    System.out.println("JSON 파싱 실패: " + e.getMessage());
                }
            }
            
            // contains("false") 체크 먼저 - 명시적인 오답 판정이 있는지 확인
            if (response.toLowerCase().contains("\"iscorrect\":false") || 
                response.toLowerCase().contains("\"iscorrect\": false") ||
                response.toLowerCase().contains("incorrect") || 
                response.toLowerCase().contains("틀렸") || 
                response.toLowerCase().contains("오답")) {
                System.out.println("텍스트 분석으로 오답 판정 (명시적인 오답 표현 감지)");
                return false;
            }
            
            // 직접 텍스트 내용 분석 (정답 판정)
            if (response.toLowerCase().contains("\"iscorrect\":true") || 
                response.toLowerCase().contains("\"iscorrect\": true") ||
                response.toLowerCase().contains("정답") || 
                response.toLowerCase().contains("맞습니다") || 
                response.toLowerCase().contains("correct") || 
                (response.toLowerCase().contains("일치") && !response.toLowerCase().contains("불일치"))) {
                System.out.println("텍스트 분석으로 정답 판정");
                return true;
            }
            
            System.out.println("정답 판정 실패, 오답으로 처리");
            return false;
        } catch (Exception e) {
            log.error("Gemini API 응답 파싱 중 오류 발생", e);
            return false; // 파싱 실패 시 기본값으로 오답 처리
        }
    }
}