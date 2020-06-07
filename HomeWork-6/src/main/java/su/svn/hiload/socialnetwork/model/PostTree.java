package su.svn.hiload.socialnetwork.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder(builderClassName = "Builder")
public class PostTree implements Serializable, DBEntry, Labeled {

    static final long serialVersionUID = -103L;

    @Id
    private Long id;

    private Long parentId;

    private UUID label;
}
