package de.simone.backend.llm;

import java.util.List;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.AugmentationRequest;
import dev.langchain4j.rag.AugmentationResult;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.filter.comparison.IsEqualTo;
import dev.langchain4j.store.embedding.filter.comparison.IsNotIn;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TRetrievalAugmentor implements RetrievalAugmentor {

    private EmbeddingStore<TextSegment> store;
    private EmbeddingModel model;

    TRetrievalAugmentor(EmbeddingStore<TextSegment> store, EmbeddingModel model) {
        this.store = store;
        this.model = model;
    }

    @Override
    public AugmentationResult augment(AugmentationRequest augmentationRequest) {
        String memoryId = augmentationRequest.metadata().chatMemoryId().toString();

        // the memoryid muss contain ":", if not it is probably a tool execution. there is nothing to augment.
        Long uId = -1L;
        if (memoryId.contains(":")) {
            String[] string = memoryId.split(":");
            uId = Long.parseLong(string[1]);
        }

        System.out.println("DocumentRetriever.augment() " + uId);

        Filter filter = new IsEqualTo("userId", uId);
        filter.or(new IsNotIn("userId", List.of("")));

        EmbeddingStoreContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingModel(model)
                .embeddingStore(store)
                .maxResults(3)
                .filter(filter)
                .build();
        RetrievalAugmentor augmentor = DefaultRetrievalAugmentor
                .builder()
                .contentRetriever(contentRetriever)
                .build();

        return augmentor.augment(augmentationRequest);

    }

}