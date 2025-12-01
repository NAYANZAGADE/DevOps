package constants;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Constants for Risk Assessment Questionnaire
 * Contains all 7 questions with their text
 * Questions are stored as constants to avoid storing question text in database
 */
public class RiskAssessmentConstants {

    // Question IDs
    public static final int WITHDRAWAL_AGE_QUESTION = 1;
    public static final int INCOME_SECURITY_QUESTION = 2;
    public static final int INVESTMENT_FAMILIARITY_QUESTION = 3;
    public static final int MARKET_DROP_REACTION_QUESTION = 4;
    public static final int PAST_RISK_BEHAVIOR_QUESTION = 5;
    public static final int EXPECTED_RETURN_QUESTION = 6;
    public static final int RETIREMENT_CONFIDENCE_QUESTION = 7;

    // Question Texts
    public static final String WITHDRAWAL_AGE_TEXT = "At what age do you expect to begin withdrawing from your 401(k)?";
    public static final String INCOME_SECURITY_TEXT = "How secure and predictable is your household income today?";
    public static final String INVESTMENT_FAMILIARITY_TEXT = "How familiar are you with investing concepts (e.g., stocks, bonds, diversification)?";
    public static final String MARKET_DROP_REACTION_TEXT = "If your 401(k) dropped 20% in one year, how would you most likely react?";
    public static final String PAST_RISK_BEHAVIOR_TEXT = "Thinking about past financial decisions, how much risk have you typically taken with your investments?";
    public static final String EXPECTED_RETURN_TEXT = "Over the long term, what annual return range do you expect from your investments, knowing higher returns come with higher risk?";
    public static final String RETIREMENT_CONFIDENCE_TEXT = "How confident are you that your current savings rate and assets will be enough for retirement?";

    // Maps for easy access
    public static final Map<Integer, String> QUESTION_TEXTS = new HashMap<>();

    static {
        // Initialize question texts map
        QUESTION_TEXTS.put(WITHDRAWAL_AGE_QUESTION, WITHDRAWAL_AGE_TEXT);
        QUESTION_TEXTS.put(INCOME_SECURITY_QUESTION, INCOME_SECURITY_TEXT);
        QUESTION_TEXTS.put(INVESTMENT_FAMILIARITY_QUESTION, INVESTMENT_FAMILIARITY_TEXT);
        QUESTION_TEXTS.put(MARKET_DROP_REACTION_QUESTION, MARKET_DROP_REACTION_TEXT);
        QUESTION_TEXTS.put(PAST_RISK_BEHAVIOR_QUESTION, PAST_RISK_BEHAVIOR_TEXT);
        QUESTION_TEXTS.put(EXPECTED_RETURN_QUESTION, EXPECTED_RETURN_TEXT);
        QUESTION_TEXTS.put(RETIREMENT_CONFIDENCE_QUESTION, RETIREMENT_CONFIDENCE_TEXT);
    }

    /**
     * Get question text by question ID
     */
    public static String getQuestionText(Integer questionId) {
        return QUESTION_TEXTS.get(questionId);
    }

    /**
     * Get all question IDs
     */
    public static List<Integer> getAllQuestionIds() {
        return Arrays.asList(
            WITHDRAWAL_AGE_QUESTION,
            INCOME_SECURITY_QUESTION,
            INVESTMENT_FAMILIARITY_QUESTION,
            MARKET_DROP_REACTION_QUESTION,
            PAST_RISK_BEHAVIOR_QUESTION,
            EXPECTED_RETURN_QUESTION,
            RETIREMENT_CONFIDENCE_QUESTION
        );
    }
}
