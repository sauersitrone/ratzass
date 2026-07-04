/*
 *   Copyright (c) 2025 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone.backend.llm;

public class OpenAiTtsRequest {
    public String model = "gpt-4o-mini-tts";
    public String input;
    public String voice = "alloy";

    public OpenAiTtsRequest(String input) {
        this.input = input;
    }
}

