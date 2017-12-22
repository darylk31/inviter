package invite.hfad.com.inviter;

import java.util.List;

/**
 * Created by Jimmy on 11/11/2017.
 */

public class Promotion {
    private String Id;
    private String Name;
    private String StartDate;
    private String EndDate;
    private String Code;
    private String WebsiteUrl;
    private String LogoUrl;
    private List ImageList;
    private String Description;
    private String RequirementNumber;
    private int promotionViews;
    private int promotionUsed;


    public Promotion(){};

    public Promotion(String Id,
                     String Name,
                     String StartDate,
                     String EndDate,
                     String Code,
                     String WebsiteUrl,
                     String LogoUrl,
                     List ImageList,
                     String Description,
                     String RequirementNumber,
                     int promotionViews,
                     int promotionUsed){
        this.Id = Id;
        this.Name = Name;
        this.StartDate = StartDate;
        this.EndDate = EndDate;
        this.Code = Code;
        this.WebsiteUrl = WebsiteUrl;
        this.LogoUrl = LogoUrl;
        this.ImageList = ImageList;
        this.Description = Description;
        this.RequirementNumber = RequirementNumber;
        this.promotionViews = promotionViews;
        this.promotionUsed = promotionUsed;
    }


    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String StartDate) {
        this.StartDate = StartDate;
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String EndDate) {
        this.EndDate = EndDate;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String Code) {
        this.Code = Code;
    }

    public String getWebsiteUrl() {
        return WebsiteUrl;
    }

    public void setWebsiteUrl(String WebsiteUrl) {
        this.WebsiteUrl = WebsiteUrl;
    }

    public String getLogoUrl() {
        return LogoUrl;
    }

    public void setLogoUrl(String LogoUrl) {
        this.LogoUrl = LogoUrl;
    }

    public List getImageList() {
        return ImageList;
    }

    public void setImageList(List ImageList) {
        this.ImageList = ImageList;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public String getRequirementNumber() {
        return RequirementNumber;
    }

    public void setRequirementNumber(String RequirementNumber) {
        this.RequirementNumber = RequirementNumber;
    }

    public int getPromotionViews() {
        return promotionViews;
    }

    public void setPromotionViews(int promotionViews) {
        this.promotionViews = promotionViews;
    }

    public int getPromotionUsed() {
        return promotionUsed;
    }

    public void setPromotionUsed(int promotionUsed) {
        this.promotionUsed = promotionUsed;
    }
}
