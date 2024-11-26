package task;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
public class DocumentManager {

    private final Map<String, Document> documents = new LinkedHashMap<>();

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        Objects.requireNonNull(document.author, "'author' is required");
        Objects.requireNonNull(document.title, "'title' is required");
        Objects.requireNonNull(document.content, "'content' is required");
        Objects.requireNonNull(document.created, "'created' is required");
        if (document.id == null) {
            document.id = UUID.randomUUID().toString();
        }
        if (document.author.id == null) {
            document.author.id = UUID.randomUUID().toString();
        }
        documents.put(document.id, document);
        return document;
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {
        return documents.values().stream()
                .filter(document -> matches(document, request))
                .toList();
    }

    private static boolean matches(Document document, SearchRequest request) {
        if (request.titlePrefixes != null) {
            if (request.titlePrefixes.stream().noneMatch(document.title::startsWith)) {
                return false;
            }
        }
        if (request.containsContents != null) {
            if (request.containsContents.stream().noneMatch(document.content::contains)) {
                return false;
            }
        }
        if (request.authorIds != null) {
            if (!request.authorIds.contains(document.author.id)) {
                return false;
            }
        }
        if (request.createdFrom != null) {
            if (document.created.isBefore(request.createdFrom)) {
                return false;
            }
        }
        if (request.createdTo != null) {
            if (!document.created.isBefore(request.createdTo)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {
        return Optional.ofNullable(documents.get(id));
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}