package mk.ukim.finki.wpproject.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mk.ukim.finki.wpproject.model.enums.TextFormat;
import mk.ukim.finki.wpproject.model.enums.TextTone;
import mk.ukim.finki.wpproject.model.enums.TextType;

import java.time.LocalDateTime;

@Entity
@Table(name = "text_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TextType textType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TextTone textTone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TextFormat textFormat;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
