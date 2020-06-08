package su.svn.hiload.socialnetwork.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder(builderClassName = "Builder")
public class Post implements Serializable, DBEntry, Labeled {

    static final long serialVersionUID = -102L;

    @Id
    private Long id;

    private UUID label;

    @Column("owner_id")
    private Long ownerId;

    @Column("owner_label")
    private UUID ownerLabel;

    private String title;

    private String content;

    private PostType postType;

    private LocalDateTime createDateTime;

    private LocalDateTime editDateTime;
}
