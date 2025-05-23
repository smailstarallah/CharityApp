package ma.emsi.charityapp.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ma.emsi.charityapp.Enum.MediaType;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@lombok.Data
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
public class Media {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    @NotNull(message = "Le type de média est obligatoire")
    private MediaType type;

    @Column(nullable = false, columnDefinition = "LONGBLOB")
    @Lob
    private byte[] content;

    @Column
    private String fileName;

    @Column
    private String contentType;

    @Column
    private Long fileSize;

    @Column(nullable = false)
    @CreationTimestamp
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "charity_action_id")
    @JsonIgnore
    private CharityAction charityAction;

    /**
     * Constructor for creating a media entity with essential fields
     *
     * @param type The type of media (Image or Video)
     * @param content The binary content of the media file
     * @param fileName The original filename
     * @param contentType The MIME type of the content
     */
    public Media(MediaType type, byte[] content, String fileName, String contentType) {
        this.type = type;
        this.content = content;
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileSize = content != null ? (long) content.length : 0L;
    }

    @Override
    public String toString() {
        return "Media{" +
                "id=" + id +
                ", type=" + type +
                ", fileName='" + fileName + '\'' +
                ", contentType='" + contentType + '\'' +
                ", fileSize=" + fileSize +
                ", createdAt=" + createdAt +
                ", charityAction=" + (charityAction != null ? charityAction.getId() : "null") +
                '}';
    }
}
