/*
 *   Copyright (c) 2025 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone.backend.llm;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
// public class TRetrievalAugmentor2 implements Supplier<RetrievalAugmentor> {
public class TRetrievalAugmentor2  {
    private final RetrievalAugmentor augmentor;

    @SuppressWarnings("rawtypes")
    TRetrievalAugmentor2(EmbeddingStore store, EmbeddingModel model) {
        // Configure the content retriever, responsible for fetching relevant content
        // based on the user query
        EmbeddingStoreContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingModel(model)
                .embeddingStore(store)
                .maxResults(3)
                .build();

        // Create the RetrievalAugmentor that combines the retriever and a default
        // content injector
        augmentor = DefaultRetrievalAugmentor.builder().contentRetriever(contentRetriever).build();
    }

    // @Override
    public RetrievalAugmentor get() {
        return augmentor;
    }

}