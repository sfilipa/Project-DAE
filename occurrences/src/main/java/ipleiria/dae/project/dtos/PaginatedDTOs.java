package ipleiria.dae.project.dtos;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class PaginatedDTOs<DTO> implements Serializable {

    public final class Metadata implements Serializable {
        private final Long count;
        private final Long totalCount;

        private final Long pageCount;

        public Metadata(Long count, Long totalCount, Long pageCount) {
            this.count = count;
            this.totalCount = totalCount;
            this.pageCount = pageCount;
        }

        public Metadata(Long totalCount) {
            this(0L, totalCount, 1L);
        }

        public Long getCount() {
            return count;
        }

        public Long getTotalCount() {
            return totalCount;
        }

        public Long getPageCount() {
            return pageCount;
        }
    }

    private final List<DTO> data;

    private final Metadata metadata;

    public PaginatedDTOs(long totalCount) {
        this.data = Collections.emptyList();
        this.metadata = new Metadata(0L, totalCount, 1L);
    }

    public PaginatedDTOs(List<DTO> data, long totalCount, int offset, int limit) {
        this.data = data;
        this.metadata = data.isEmpty() ? new Metadata(0L, totalCount, 1L) : new Metadata((long) (offset + data.size()), totalCount, (long) Math.ceil((double) totalCount / limit));
    }

    public List<DTO> getData() {
        return data;
    }

    public Metadata getMetadata() {
        return metadata;
    }
}