package su.svn.hiload.socialnetwork.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder(builderClassName = "Builder")
public class Message implements Serializable, DBEntry, Labeled {

    static final long serialVersionUID = -101L;

    @Id
    private Long id;

    private Long fromId;

    private UUID fromLabel;

    private Long toId;

    private UUID toLabel;

    private String textMessage;

    private LocalDateTime createDateTime;

    private UUID label;
}
