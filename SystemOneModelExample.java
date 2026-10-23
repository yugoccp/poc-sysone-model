import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Example Java code demonstrating how to send a multi-type evaluation request to the Nimble model
 * using the System One API. This example includes choice, noul, and score question types.
 */
public class SystemOneModelExample {

    public static void main(String[] args) throws Exception {
        
        try (var client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build()) {

            String url = "http://localhost:11434/v1/systemone";
            
            // Text block containing all three Nimble question types
            String jsonPayload = """
                {
                  "model": "nimble",
                  "state": "The customer stated: 'I tried to update my payment method for the annual plan but the button is greyed out. I am extremely frustrated as I have been trying for two days. If this isn't fixed I will just cancel.'",
                  "questions": {
                    
                    "ticket_category": {
                      "type": "choice",
                      "instructions": "Which department should handle this ticket?",
                      "criteria": {
                        "billing_error": "Issues making payments or updating cards", 
                        "cancellation": "User explicitly wants to cancel their account", 
                        "technical_support": "General app crashes or bugs"
                      }
                    },
                    
                    "escalation_required": {
                      "type": "noul",
                      "instructions": "Does this message require immediate management escalation?",
                      "criteria": {
                        "true": "The user is expressing extreme frustration, threatening to cancel, or mentioning legal action.",
                        "false": "The user is calm, polite, or just asking a standard question."
                      }
                    },
                    
                    "sentiment_rating": {
                      "type": "score",
                      "instructions": "Rate the customer's sentiment level.",
                      "criteria": [
                        "Highly negative or angry",
                        "Mildly frustrated",
                        "Neutral and factual",
                        "Positive or thankful"
                      ]
                    }
                    
                  }
                }
                """;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            System.out.println("Sending multi-type evaluation to Nimble...");
            
            HttpResponse<String> response = client.send(
                    request, 
                    HttpResponse.BodyHandlers.ofString()
            );

            System.out.println("Response Status Code: " + response.statusCode());
            System.out.println("Evaluation JSON:\n" + response.body());
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Request interrupted: " + e.getMessage());
        }
    }
}
