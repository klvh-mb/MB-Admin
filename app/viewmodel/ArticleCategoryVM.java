package viewmodel;

import models.ArticleCategory;

import org.codehaus.jackson.annotate.JsonProperty;

public class ArticleCategoryVM {

	@JsonProperty("id") public long id;
	@JsonProperty("name") public String name;
	@JsonProperty("pn") public String pictureName;
	
	public static ArticleCategoryVM articleCategoryVM(ArticleCategory articleCategory) {
		ArticleCategoryVM articleCategoryVM = new ArticleCategoryVM();
		articleCategoryVM.id = articleCategory.id;
		articleCategoryVM.name = articleCategory.name;
		articleCategoryVM.pictureName = articleCategory.pictureName;
		return articleCategoryVM;
	}
}
