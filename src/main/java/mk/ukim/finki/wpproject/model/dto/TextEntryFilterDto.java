package mk.ukim.finki.wpproject.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mk.ukim.finki.wpproject.model.enums.TextTone;
import mk.ukim.finki.wpproject.model.enums.TextType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TextEntryFilterDto {

    private int pageNum = 1;

    private int pageSize = 10;

    private String sortBy = "id";

    private Long id;

    private TextType textType;

    private TextTone textTone;

    public void normalize() {
        if (pageNum < 1) {
            pageNum = 1;
        }

        if (pageSize < 1) {
            pageSize = 10;
        }

        if (!"id".equals(sortBy)
                && !"createdAt".equals(sortBy)
                && !"textType".equals(sortBy)
                && !"textTone".equals(sortBy)) {
            sortBy = "id";
        }
    }
}
