package invite.hfad.com.inviter;

import java.util.List;

/**
 * Created by Jimmy on 11/11/2017.
 */

public class Promotion {
    private String promotionId;
    private String promotionName;
    private String promotionStartDate;
    private String promotionEndDate;
    private String promotionCode;
    private String promotionWebsite;
    private String promotionLogo;
    private List promotionImages;
    private String promotionDetails;
    private String promotionRequirement;


    public Promotion(){};

    public Promotion(String promotionId,
                     String promotionName,
                     String promotionStartDate,
                     String promotionEndDate,
                     String promotionCode,
                     String promotionWebsite,
                     String promotionLogo,
                     List promotionImages,
                     String promotionDetails,
                     String promotionRequirement){
        this.promotionId = promotionId;
        this.promotionName = promotionName;
        this.promotionStartDate = promotionStartDate;
        this.promotionEndDate = promotionEndDate;
        this.promotionCode = promotionCode;
        this.promotionWebsite = promotionWebsite;
        this.promotionLogo = promotionLogo;
        this.promotionImages = promotionImages;
        this.promotionDetails = promotionDetails;
        this.promotionRequirement = promotionRequirement;
    }


    public String getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(String promotionId) {
        this.promotionId = promotionId;
    }

    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }

    public String getpromotionStartDate() {
        return promotionStartDate;
    }

    public void setpromotionStartDate(String promotionStartDate) {
        this.promotionStartDate = promotionStartDate;
    }

    public String getpromotionEndDate() {
        return promotionEndDate;
    }

    public void setpromotionEndDate(String promotionEndDate) {
        this.promotionEndDate = promotionEndDate;
    }

    public String getPromotionCode() {
        return promotionCode;
    }

    public void setPromotionCode(String promotionCode) {
        this.promotionCode = promotionCode;
    }

    public String getPromotionWebsite() {
        return promotionWebsite;
    }

    public void setPromotionWebsite(String promotionWebsite) {
        this.promotionWebsite = promotionWebsite;
    }

    public String getPromotionLogo() {
        return promotionLogo;
    }

    public void setPromotionLogo(String promotionLogo) {
        this.promotionLogo = promotionLogo;
    }

    public List getPromotionImages() {
        return promotionImages;
    }

    public void setPromotionImages(List promotionImages) {
        this.promotionImages = promotionImages;
    }

    public String getPromotionDetails() {
        return promotionDetails;
    }

    public void setPromotionDetails(String promotionDetails) {
        this.promotionDetails = promotionDetails;
    }

    public String getPromotionRequirement() {
        return promotionRequirement;
    }

    public void setPromotionRequirement(String promotionRequirement) {
        this.promotionRequirement = promotionRequirement;
    }
}
