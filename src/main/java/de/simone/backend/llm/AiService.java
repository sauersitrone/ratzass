/*
 *   Copyright (c) 2025 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone.backend.llm;

import java.time.LocalDateTime;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.quarkiverse.langchain4j.ToolBox;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RegisterAiService
public interface AiService {

    String NO_MEMORY = "-1";

    public static record CheckReminderResponse(boolean isValid, String message) {
    }

    TokenStream freeChat(@MemoryId String id, @UserMessage String userMessage);

    @UserMessage("""
            ## Verify text
            Check whether the following reminder violates the restrictions of your system prompt.
            - Respond isValid = true if the reminder does NOT violate your guidelines.
            - If isValid = false, store the explanation of why the reminder is not valid in the "message" variable.

            ---
            {reminder}
            ---
            """)
    public CheckReminderResponse checkReminder(@V("reminder") String reminder);


    @SystemMessage("""
            ## Definition
            Given the DDL in the DDL section, write an SQL query to answer the user's question following the guidelines listed in the GUIDELINES section.

            ### Guidelines:
            - Only produce SELECT queries.
            - The response produced should only contain the raw SQL query starting with the word 'SELECT'. Do not wrap the SQL query in markdown code blocks (```sql or ```).
            - If the question cannot be answered based on the provided DDL, respond with empty string.""
            - If the query involves a JOIN operation, prefix all the column names in the query with the corresponding table names.

            ### Parameters:
            - the parameter 'ddl' is the database schema in DDL format.
            - the parameter 'userId' is the id of the user asking the question.
            - the parameter 'adultId' is the id of the adult for which the user is asking the question.

            ### Guidelines:
            - if the question refer to an adult, you must use the 'secondaryKey = userId' parameter to filter the results.
            - if the question refer to a Relative, you must use the 'secondaryKey = adultId' parameter to filter the results.
            - the followings words are synonyms: name = firstName

            ### Examples:
            question: how many adults are registered in the system?
            answer: SELECT COUNT(*) FROM Adult WHERE secondaryKey = {userId};

            ### DDL
            the database schema is defined as follows:
            {ddl}
             """)
    public String generateSql(@V("ddl") String ddl, LocalDateTime currentDate, @V("userId") Long userId,
            @V("adultId") Long adultId, @UserMessage String userMessage);

    @SystemMessage("""
            ## Definition
            Now's date is {currentDate} and your current location is Dresden, Germany
            This system is named **Sitrone**, a Tamagotchi-style companion app designed to provide support and companionship to elderly and disabled individuals. You
            are a chatbot named **Sia** that can answer questions about this system. your users are the caregivers and family members of the adults who use
            Sitrone. You must answer questions about the system, its features, and its purpose.

            ### Parameters:
            - the parameter 'userId' is the id of the user asking the question.
            - the parameter 'adultId' is the id of the adult for which the user is asking the question.

            ### Guidelines:
            - Respond ALWAYS in TXT format, without any markdown formatting. Emonjis are allowed.
             """)
    @ToolBox({ TToolBox.class })
    String chatWithSiaImpl(@MemoryId String id, @V("currentDate") LocalDateTime currentDate, @V("userId") Long userId,
            @V("adultId") Long adultId,
            @UserMessage String userMessage);

    public default String chatWithSia(Long userId, Long adultId, String userMessage) {
        // ignore the message if is too long
        if (userMessage.length() > 1024)
            return "Message is too long.";

        System.out.println("chatWithSia: userId=" + userId + ", adultId=" + adultId + ", message=" + userMessage);

        return chatWithSiaImpl("chatWithSia:" + userId, LocalDateTime.now(), userId, adultId, userMessage);
    }

}
