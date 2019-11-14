package quiz;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class CategoryChooser implements Serializable {

    private String ChooserMessage = "Vilken kategori väljer du?";
    private List<CategoryName> enumList = Arrays.asList(CategoryName.values());
    private CategoryName choosedCategory;

    public String getChooserMessage() {
        return ChooserMessage;
    }

    public List<CategoryName> getEnumList() {
        return enumList;
    }

    public CategoryName getChoosedCategory() {
        return choosedCategory;
    }

    public void setChoosedCategory(CategoryName choosedCategory) {
        this.choosedCategory = choosedCategory;
    }
}
