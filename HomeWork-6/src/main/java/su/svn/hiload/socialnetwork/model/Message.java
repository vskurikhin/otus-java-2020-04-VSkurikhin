package su.svn.hiload.socialnetwork.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder(builderClassName = "Builder")
public class Message implements Serializable, DBEntry, Labeled {

    static final long serialVersionUID = -101L;

    @Id
    private Long id;

    private UUID label;

    @Column("from_id")
    private Long fromId;

    @Column("from_label")
    private UUID fromLabel;

    @Column("to_id")
    private Long toId;

    @Column("to_label")
    private UUID toLabel;

    @Column("text_message")
    private String textMessage;
}
