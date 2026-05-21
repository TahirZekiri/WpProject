package mk.ukim.finki.wpproject.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserFilterDto {

    private int pageNum = 1;

    private int pageSize = 10;

    private String sortBy = "username";

    private String text;

    public void normalize() {
        if (pageNum < 1) {
            pageNum = 1;
        }

        if (pageSize < 1) {
            pageSize = 10;
        }

        if (!"username".equals(sortBy)
                && !"name".equals(sortBy)
                && !"surname".equals(sortBy)) {
            sortBy = "username";
        }

        if (text != null) {
            text = text.trim().toLowerCase();
        }
    }
}