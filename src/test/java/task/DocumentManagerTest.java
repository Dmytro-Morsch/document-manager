package task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static task.DocumentManager.*;

class DocumentManagerTest {

    public static final Document DOCUMENT1 = Document.builder()
            .title("Effective Java")
            .content("Lorem ipsum 1")
            .author(new Author(null, "Dima"))
            .created(instant("2024-10-09"))
            .build();
    public static final Document DOCUMENT2 = Document.builder()
            .title("Clean code")
            .content("Lorem ipsum 2")
            .author(new Author(null, "Denis"))
            .created(instant("2024-11-10"))
            .build();
    public static final Document DOCUMENT3 = Document.builder()
            .title("Clean coder")
            .content("Lorem ipsum 3")
            .author(new Author(null, "Sasha"))
            .created(instant("2024-11-26"))
            .build();

    private final DocumentManager documentManager = new DocumentManager();

    @BeforeEach
    void setUp() {
        documentManager.save(DOCUMENT1);
        documentManager.save(DOCUMENT2);
        documentManager.save(DOCUMENT3);
    }

    @Test
    void testSaveNewDocument() {
        Document document = Document.builder()
                .title("Test")
                .content("Lorem ipsum")
                .author(new Author(null, "Dima"))
                .created(Instant.now())
                .build();

        documentManager.save(document);

        assertNotNull(document.getId());
        assertNotNull(document.getAuthor().getId());
    }

    @Test
    void testSaveExistingDocument() {
        String id = DOCUMENT1.getId();
        Document document = Document.builder()
                .id(id)
                .title("Test")
                .content("Lorem ipsum")
                .author(DOCUMENT1.getAuthor())
                .created(Instant.EPOCH)
                .build();

        documentManager.save(document);
        Document foundDocument = documentManager.findById(id).orElseThrow();

        assertEquals("Test", foundDocument.getTitle());
        assertEquals("Lorem ipsum", foundDocument.getContent());
    }

    @Test
    void testSearchAll() {
        List<Document> result1 = documentManager.search(SearchRequest.builder().build());
        assertEquals(List.of(DOCUMENT1, DOCUMENT2, DOCUMENT3), result1);
    }

    @Test
    void testSearchByCreated() {
        List<Document> result1 = documentManager.search(SearchRequest.builder()
                .createdTo(instant("2023-05-03"))
                .build());
        assertEquals(Collections.emptyList(), result1);

        List<Document> result2 = documentManager.search(SearchRequest.builder()
                .createdFrom(instant("2025-12-13"))
                .build());
        assertEquals(Collections.emptyList(), result2);

        List<Document> result3 = documentManager.search(SearchRequest.builder()
                .createdFrom(instant("2024-11-09"))
                .createdTo(instant("2024-11-28"))
                .build());
        assertEquals(List.of(DOCUMENT2, DOCUMENT3), result3);
    }

    @Test
    void testSearchByTitle() {
        List<Document> result1 = documentManager.search(SearchRequest.builder()
                .titlePrefixes(List.of("Clean"))
                .build());
        assertEquals(List.of(DOCUMENT2, DOCUMENT3), result1);

        List<Document> result2 = documentManager.search(SearchRequest.builder()
                .titlePrefixes(List.of("Eff"))
                .build());
        assertEquals(List.of(DOCUMENT1), result2);
    }

    @Test
    void testSearchByContent() {
        List<Document> result = documentManager.search(SearchRequest.builder()
                .containsContents(List.of("ipsum"))
                .build());
        assertEquals(List.of(DOCUMENT1, DOCUMENT2, DOCUMENT3), result);
    }

    @Test
    void testSearchByAuthorId() {
        String author1 = DOCUMENT1.getAuthor().getId();
        String author3 = DOCUMENT3.getAuthor().getId();

        List<Document> result = documentManager.search(SearchRequest.builder()
                .authorIds(List.of(author1, author3))
                .build());
        assertEquals(List.of(DOCUMENT1, DOCUMENT3), result);

    }

    private static Instant instant(String date) {
        return Instant.parse(date + "T00:00:00Z");
    }
}