/*
 *   Copyright (c) 2025 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone.backend.llm;

import java.time.LocalDateTime;
import java.util.List;

import de.simone.FileUtils;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class TToolBox {

    @Inject
    EntityManager entityManager;

    @Inject
    AiService aiService;

    public static record MediaJson(
            Long id,
            String name,
            String description,
            String mimeType,
            String tags) {
    }

    public static record ReminderAddedResponse(Long id, String text, LocalDateTime when, boolean appended) {
    }



    @Tool("""
            Generate a native SQL SELECT that can be used to query this systems Db to obtain date to answer a user question.
            - if the user question is not related to the database, this tool returns an empty string.
            - the parameter 'userId' is the id of the user asking the question. This parameter is mandatory.
            - the parameter 'adultId' is the id of the adult for which the user is asking the question. This parameter is mandatory.
            """)
    public String generateSql(Long userId, Long adultId, String userMessage) {
        String sql = aiService.generateSql(FileUtils.getDbDdl(), LocalDateTime.now(), userId, adultId, userMessage);
        System.out.println("generateSql: " + sql);
        return sql;
    }

    @Tool("""
            Execute a SQL query and return the result as a List.
            - The parameter 'sql' is the SQL query to be executed. This query must be a SELECT query.
            """)
    @Transactional
    public List<?> executeSql(String sql) {
        if (!sql.trim().toUpperCase().startsWith("SELECT")) {
            throw new IllegalArgumentException("Only SELECT queries are allowed.");
        }
        System.out.println("executeSql: " + sql);
        Query query = entityManager.createNativeQuery(sql);
        List<?> result = query.getResultList();
        return result;
    }

    @Tool("""
            ---
                """)
    @Transactional
    public boolean respondAQuestion(Long id, String answer) {
        boolean ok = false;

        // TODO: this muss be a background job who analize the conversation and store the answer in the correct question.
        // maybe store a journal too (not rigidly attached to the questions).
        
        return ok;
    }

}
